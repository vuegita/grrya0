package com.inso.modules.game.cache;

import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.model.GameCategory;
import com.inso.modules.passport.MyConstants;

public class GameCacheKeyHelper {

    private static final String ROOT_CACHE_KEY = MyConstants.DEFAULT_GAME_MODULE_NAME;

    public static String findPeriodInfoByNo(GameCategory category, String issue)
    {
        return ROOT_CACHE_KEY + category.getKey() + "_find_period_info_by_issue_" + issue;
    }

    public static String findOrderInfoByNo(GameCategory category, String orderno)
    {
        return ROOT_CACHE_KEY + category.getKey() + "_find_order_info_by_orderno_" + orderno;
    }

    public static String queryOrderLatestPage_100(GameCategory category, GameChildType childType, long userid)
    {
        if(childType == null)
        {
            return ROOT_CACHE_KEY + category.getKey() + "_all_queryOrderScrollPageByUser_latest_100" + userid;
        }
        return ROOT_CACHE_KEY + category.getKey() + childType.getKey() + "_queryOrderScrollPageByUser_latest_100" + userid;
    }

    public static String queryOrderLatestPage_status_100(GameCategory category, long userid, OrderTxStatus Status)
    {
        if(Status == null)
        {
            return ROOT_CACHE_KEY + category.getKey() + "_all_queryOrderScrollPageByUser_latest_100" + userid ;
        }
        return ROOT_CACHE_KEY + category.getKey() + Status.getKey() + "_queryOrderScrollPageByUser_latest_100" + userid;
    }

    public static String queryTotalAmountByUserAndIssue (GameCategory category, long userid, long  rpid )
    {

        return ROOT_CACHE_KEY + category.getKey() + "_financial_mgr_period_status_user_buy_money_" + userid +rpid;
    }

    public static String findGameInfo(long gameid)
    {
        return MyConstants.DEFAULT_GAME_MODULE_NAME + "_game_find_game_info_" + gameid;
    }

    public static String findGameInfoByKey(String key)
    {
        return MyConstants.DEFAULT_GAME_MODULE_NAME + "findGameInfoByKey" + key;
    }

    public static String queryAllByCategory(GameCategory category)
    {
        return MyConstants.DEFAULT_GAME_MODULE_NAME + "_game_queryAllByCategory_" + category.getKey();
    }
}
