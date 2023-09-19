package org.example.core.creeper.group;

import org.example.constpool.ConstGroup;
import org.example.constpool.ConstPool;
import org.example.core.manager.AbstractCreeperGroup;
import org.springframework.stereotype.Component;

/**
 * @author Genius
 * @date 2023/09/07 17:11
 **/
@Component
public class BilibiliHotLiveGroup extends AbstractCreeperGroup {

    @Override
    public String getPlatform() {
        return ConstPool.PLATFORM.BILIBILI.getName();
    }

    @Override
    public String getFunctionName() {
        return ConstGroup.HOT_LIVE;
    }

}
