package org.example.api;

import org.example.bean.HotModule;
import org.example.bean.Live;
import org.example.bean.live.BiliBiliLive;
import org.example.bean.live.DouyuLive;
import org.example.bean.hotmodule.HotModuleList;

import org.example.constpool.ConstGroup;
import org.example.constpool.ConstPool;
import org.example.core.HotModuleDataCenter;
import org.example.core.creeper.loadconfig.BilibiliHotLiveConfig;
import org.example.core.creeper.loadconfig.DouyuHotLiveConfig;
import org.example.core.creeper.loadconfig.DouyuHotModuleConfig;
import org.example.core.creeper.loadtask.BiliBiliHotLiveLoadTask;
import org.example.core.creeper.loadtask.DouyuHotLiveLoadTask;
import org.example.core.creeper.loadtask.DouyuHotModuleLoadTask;
import org.example.core.manager.CreeperGroupCenter;
import org.example.core.taskcenter.TaskCenter;
import org.example.core.taskcenter.request.ReptileRequest;
import org.springframework.stereotype.Component;


import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 热门模块的一些爬虫方法api实现
 * @author Genius
 * @date 2023/07/21 17:53
 **/
//TODO 待重构
@Component
public class HotModuleApi {

    public HotModuleList getAllHotModule(String platform){
        return HotModuleDataCenter.DataCenter().getModuleList(platform);
    }

    public List<? extends Live> getHotLiveList(String platform){
       return HotModuleDataCenter.DataCenter().getLiveList(platform);
    }
    public static HotModuleList getDouyuAllHotModule(){
        return new DouyuHotModuleLoadTask(new DouyuHotModuleConfig()).start();
    }

    public static List<DouyuLive> getDouyuHotLive(){
        return new DouyuHotLiveLoadTask(new DouyuHotLiveConfig()).start();
    }

    public static List<DouyuLive> getDouyuHotLive(int moduleId){
        return new DouyuHotLiveLoadTask(new DouyuHotLiveConfig(moduleId)).start();
    }

    public static List<BiliBiliLive> getBiliBiliHotLive(String parentId,String areaId,int page){
        return new BiliBiliHotLiveLoadTask(new BilibiliHotLiveConfig(parentId,areaId,page)).start();
    }
}
