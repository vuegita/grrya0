package com.inso.modules.game.rg.logical;

import java.util.Collections;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.modules.game.rg.model.LotteryPeriodInfo;
import com.inso.modules.game.rg.model.LotteryRGType;
import com.inso.modules.passport.MyConstants;

/**
 * 历史开奖缓存，只缓存最新几期
 */
public class RGLatestPeriodCache {

    private static final String KEY_ISSUE = "issue";
    private static final String KEY_STATUS = "status";
    private static final String KEY_START_TIME = "startTime";
    private static final String KEY_END_TIME = "endTime";
    private static final String KEY_PRICE = "price";
    private static final String KEY_OPEN_RESULT = "openResult";

    private static final int EXPIRES = -1;
    private static final String CACHE_KEY = MyConstants.DEFAULT_GAME_MODULE_NAME + RGLatestPeriodCache.class.getName();

    public static void updateCache(LotteryRGType lotteryType, List<LotteryPeriodInfo> rsList)
    {
        if(CollectionUtils.isEmpty(rsList))
        {
            return;
        }

        List<JSONObject> list = Lists.newArrayList();
        for(LotteryPeriodInfo tmp : rsList)
        {
            JSONObject model = new JSONObject();
            model.put(KEY_ISSUE, tmp.getIssue());
//            model.put(KEY_START_TIME, tmp.getStarttime());
//            model.put(KEY_END_TIME, tmp.getEndtime());
            model.put(KEY_PRICE, tmp.getReferencePrice());
            model.put(KEY_OPEN_RESULT, tmp.getOpenResult());
            list.add(model);
        }

        String cachekey = CACHE_KEY + lotteryType.getKey();
        CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(list), EXPIRES);
    }

    public static List getCache(LotteryRGType lotteryType, int offset)
    {
        String cachekey = CACHE_KEY + lotteryType.getKey();
        List rsList = CacheManager.getInstance().getList(cachekey, JSONObject.class);
        if(CollectionUtils.isEmpty(rsList))
        {
            rsList = Collections.emptyList();
        }
        List list = Lists.newArrayList();

        int maxSize = rsList.size();
        int count = offset + 20;
        if(count > maxSize)
        {
            count = maxSize;
        }
        for(int i = offset; i < count; i ++)
        {
            list.add(rsList.get(i));
        }

        return list;

//        String cachekey = CACHE_KEY + lotteryType.getKey();
//        List rsList = CacheManager.getInstance().getList(cachekey, JSONObject.class);
//        if(CollectionUtils.isEmpty(rsList))
//        {
//            rsList = Collections.emptyList();
//        }
//        return rsList;
    }

}
