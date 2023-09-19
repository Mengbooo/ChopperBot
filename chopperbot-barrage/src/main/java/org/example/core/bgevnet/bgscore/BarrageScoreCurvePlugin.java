package org.example.core.bgevnet.bgscore;

import lombok.Data;
import org.example.bean.Barrage;
import org.example.bean.LiverKeyword;
import org.example.cache.FileCache;
import org.example.cache.FileCacheManagerInstance;
import org.example.config.BarrageModuleConfig;
import org.example.core.bgevnet.BarrageEvent;
import org.example.core.bgevnet.bgscore.score.AbstractScoreStrategy;
import org.example.core.bgevnet.bgscore.score.ScoreStrategyFactory;
import org.example.core.bgevnet.bgscore.split.AbstractSplitStrategy;
import org.example.core.bgevnet.bgscore.split.SplitStrategyFactory;
import org.example.plugin.SpringBootPlugin;
import org.example.service.LiverKeywordService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;


/**
 * @author Genius
 * @date 2023/09/13 18:59
 **/
@Data
@Component
public class BarrageScoreCurvePlugin extends SpringBootPlugin {

    @Resource
    private LiverKeywordService service;

    public static Map<String, LiverKeyword> globalKeywordMap;

    public static int basicBarrageScore = 5;
    private FileCache fileCache;

    private Map<String,List<BarragePoint>> barragePointMap = new ConcurrentHashMap<>();

    @Override
    public boolean init() {
        List<LiverKeyword> globalKeyWords = service.getGlobalKeyWords();
        globalKeywordMap = generateKeyMap(globalKeyWords);
        fileCache = FileCacheManagerInstance.getInstance().getFileCache(BarrageModuleConfig.getFullFilePath());
        basicBarrageScore = (Integer) fileCache.get("barrageScoreCurve","basicBarrageScore");
        return super.init();
    }

    public List<BarragePoint> generateCurve(BarrageEvent event){
        List<Barrage> barrages = event.getBarrages();
        if(barrages==null)return null;

        String liver = event.getLiver();
        String path = event.getBarrageFilePath();
        long duration = Long.parseLong(fileCache.get("barrageScoreCurve", "duration").toString());
        List<LiverKeyword> liverKeyWords = service.getLiverKeyWords(liver);
        Map<String, LiverKeyword> liverKeywordMap = generateKeyMap(liverKeyWords);

        String splitType = (String) fileCache.get("barrageScoreCurve", "splitStrategy");
        String scoreType = (String) fileCache.get("barrageScoreCurve", "scoreStrategy");
        AbstractScoreStrategy scoreStrategy = ScoreStrategyFactory.build(scoreType, liverKeywordMap);
        if(scoreType!=null){
            AbstractSplitStrategy splitStrategy = SplitStrategyFactory.build(splitType, scoreStrategy, barrages, duration,liverKeywordMap);
            if(splitStrategy!=null){
                List<BarragePoint> split = splitStrategy.split();
                barragePointMap.put(path,split==null?new ArrayList<>():split);
                return split;
            }
        }
        return null;
    }

    private Map<String,LiverKeyword> generateKeyMap(List<LiverKeyword> list){
        ConcurrentHashMap<String,LiverKeyword> map = new ConcurrentHashMap();
        if(list==null||list.isEmpty()){
            return map;
        }
        list.forEach(
                keyword -> {
                    map.put(keyword.getBarrage(),keyword);
                }
        );
        return map;
    }

    public boolean removeGlobalKeyword(String keyword){
        return globalKeywordMap.remove(keyword)!=null;
    }

    public boolean addGlobalKeyword(LiverKeyword liverKeyword){
        return globalKeywordMap.put(liverKeyword.getBarrage(),liverKeyword)!=null;
    }

    private static boolean isBan0(String barrage,Map<String, LiverKeyword> map){
        if(map==null||map.isEmpty()){
            return false;
        }
        if (map.containsKey(barrage)) {
            return map.get(barrage).getIsBan();
        }else{
            for (Map.Entry<String,LiverKeyword> entry : map.entrySet()) {
                if (Pattern.matches(entry.getKey(),barrage)) {
                    return entry.getValue().getIsBan();
                }
            }
        }
        return false;
    }

    public static boolean isBan(String barrage,Map<String, LiverKeyword> map){
        return isBan0(barrage,globalKeywordMap)||isBan0(barrage,map);
    }
}
