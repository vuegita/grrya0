package com.inso.modules.game.lottery_game_impl.helper;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.lottery_game_impl.BaseLotterySupport;
import com.inso.modules.game.lottery_game_impl.MyLotteryManager;
import com.inso.modules.game.model.GamePeriodStatus;
import com.inso.modules.game.model.NewLotteryPeriodInfo;
import com.inso.modules.passport.MyConstants;
import com.inso.modules.web.eventlog.EventLogManager;
import com.inso.modules.web.eventlog.model.WebEventLogType;
import org.joda.time.DateTime;

import java.util.Collections;
import java.util.List;

/**
 * 历史开奖缓存，只缓存最新几期
 */
public class NewLotteryLatestPeriod {

    private static final String KEY_ISSUE = "issue";
    private static final String KEY_SHOW_ISSUE = "showIssue";
    private static final String KEY_STATUS = "status";
    private static final String KEY_START_TIME = "startTime";
    private static final String KEY_END_TIME = "endTime";
    private static final String KEY_PRICE = "price";
    private static final String KEY_OPEN_RESULT = "openResult";

    private static final int EXPIRES = -1;
    private static final String CACHE_KEY = MyConstants.DEFAULT_GAME_MODULE_NAME + NewLotteryLatestPeriod.class.getName();

    private static final String ROOT_CACHE_UPDATE_RESULT_COUNT = NewLotteryLatestPeriod.class.getName() + "_update_result_count";

    public static final int MAX_UPDATE_RESULT_COUNT = MyEnvironment.isDev() ? 10000 : 1000;

    public static void updateCache(GameChildType lotteryType, List<NewLotteryPeriodInfo> rsList)
    {
        if(CollectionUtils.isEmpty(rsList))
        {
            return;
        }

        List<JSONObject> list = Lists.newArrayList();
        for(NewLotteryPeriodInfo tmp : rsList)
        {
            JSONObject model = new JSONObject();
            model.put(KEY_ISSUE, tmp.getIssue());
            if(!StringUtils.isEmpty(tmp.getShowIssue()))
            {
                model.put(KEY_SHOW_ISSUE, tmp.getShowIssue());
            }
//            model.put(KEY_START_TIME, tmp.getStarttime());
//            model.put(KEY_END_TIME, tmp.getEndtime());

            GamePeriodStatus periodStatus = GamePeriodStatus.getType(tmp.getStatus());
            if( periodStatus == GamePeriodStatus.FINISH)
            {
                model.put(KEY_PRICE, tmp.getReferenceExternal());
            }
            else
            {
                model.put(KEY_PRICE, StringUtils.getEmpty());
            }
            model.put(KEY_OPEN_RESULT, tmp.getOpenResult());
            list.add(model);
        }

        String cachekey = CACHE_KEY + lotteryType.getKey();
        CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(list), EXPIRES);
    }

    public static List getCache(GameChildType lotteryType, int offset, int fetchCount)
    {
        String cachekey = CACHE_KEY + lotteryType.getKey();
        List rsList = CacheManager.getInstance().getList(cachekey, JSONObject.class);
        if(CollectionUtils.isEmpty(rsList))
        {
            rsList = Collections.emptyList();
        }
        List list = Lists.newArrayList();

        int maxSize = rsList.size();
        int count = offset + fetchCount;
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

    public static int getUpdateResultCount(GameChildType gameChildType)
    {
        DateTime dateTime = DateTime.now();
        String cachekey = ROOT_CACHE_UPDATE_RESULT_COUNT + gameChildType.getKey() + dateTime.getDayOfYear();
        int count = CacheManager.getInstance().getInt(cachekey);
        return count;
    }

    public static void updateResultCount(GameChildType gameChildType, int value, String logResult)
    {

        DateTime dateTime = DateTime.now();
        String cackey = ROOT_CACHE_UPDATE_RESULT_COUNT + gameChildType.getKey() + dateTime.getDayOfYear();
        CacheManager.getInstance().setString(cackey, value + StringUtils.getEmpty(), CacheManager.EXPIRES_DAY);

        logResult = logResult + ", count = " + value;
        EventLogManager eventLogManager = EventLogManager.getInstance();
        eventLogManager.addAdminLog(WebEventLogType.ADMIN_UPDATE_BTC_RESULT_COUNT, logResult);
    }

}
