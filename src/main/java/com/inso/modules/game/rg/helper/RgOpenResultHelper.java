package com.inso.modules.game.rg.helper;


import com.alibaba.druid.util.LRUCache;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.rg.logical.RGPeriodStatus;
import com.inso.modules.game.rg.model.LotteryRGType;


public class RgOpenResultHelper {

    private static LRUCache<String, String> mLRUCache = new LRUCache<String, String>(100);


    public static String getOpenResult(LotteryRGType lotteryType, String issue)
    {
        String key = lotteryType.getKey() + issue;
        String value = mLRUCache.get(key);

        if(StringUtils.isEmpty(value))
        {
            RGPeriodStatus tmpPeriodStatus = RGPeriodStatus.tryLoadCache(true, lotteryType, issue);
            if(tmpPeriodStatus != null && tmpPeriodStatus.getOpenResult() >= 0)
            {
                value = tmpPeriodStatus.getOpenResult() + StringUtils.getEmpty();
                if(!StringUtils.isEmpty(value))
                {
                    mLRUCache.put(key, value);
                }
            }

        }
        return StringUtils.getNotEmpty(value);
    }

}
