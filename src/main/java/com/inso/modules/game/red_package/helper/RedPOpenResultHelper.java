package com.inso.modules.game.red_package.helper;


import com.alibaba.druid.util.LRUCache;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.GameChildType;


public class RedPOpenResultHelper {

    private static LRUCache<String, String> mLRUCache = new LRUCache<String, String>(100);

//    private static RedPBetItemType[] mBetItemTypeArray = RedPBetItemType.values();
//    private static int mOpenResultSize = mBetItemTypeArray.length;
//
//    /**
//     * 随机开奖
//     * @return
//     */
//    public static RedPBetItemType randomOpenItem()
//    {
//        int index = RandomUtils.nextInt(mOpenResultSize);
//        return mBetItemTypeArray[index];
//    }

    public static String getOpenResult(GameChildType lotteryType, String issue)
    {
        String key = lotteryType.getKey() + issue;
        String value = mLRUCache.get(key);

        if(StringUtils.isEmpty(value))
        {
//            ABPeriodStatus tmpPeriodStatus = ABPeriodStatus.tryLoadCache(lotteryType, issue);
//            if(tmpPeriodStatus != null && tmpPeriodStatus.getOpenResult() != null)
//            {
//                value = tmpPeriodStatus.getOpenResult().getKey();
//                if(!StringUtils.isEmpty(value))
//                {
//                    mLRUCache.put(key, value);
//                }
//            }

        }
        return StringUtils.getNotEmpty(value);
    }

}
