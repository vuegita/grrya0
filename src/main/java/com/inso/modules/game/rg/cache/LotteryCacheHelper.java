package com.inso.modules.game.rg.cache;

import com.inso.modules.game.GameChildType;
import com.inso.modules.game.rg.model.LotteryRGType;
import com.inso.modules.passport.MyConstants;

public class LotteryCacheHelper {

    private static final String ROOT_CACHE_KEY = MyConstants.DEFAULT_GAME_MODULE_NAME + "lottery_cache";

    public static String findLotteryInfo(String issue)
    {
        return ROOT_CACHE_KEY + "_lottery_find_info_" + issue;
    }

    public static String queryLatestPage_100(long userid, GameChildType rgType)
    {
        return ROOT_CACHE_KEY + "queryScrollPageByUser_latest_100" + userid + rgType.getKey();
    }

    public static String findByIssueAndUser( GameChildType lotteryType, String issue, long userid)
    {
        return ROOT_CACHE_KEY + "findByIssueAndUser" + lotteryType.getKey()+ issue + userid;
    }
}
