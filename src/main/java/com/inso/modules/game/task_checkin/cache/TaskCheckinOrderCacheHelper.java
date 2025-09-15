package com.inso.modules.game.task_checkin.cache;

import org.joda.time.DateTime;

import com.inso.modules.passport.MyConstants;

public class TaskCheckinOrderCacheHelper {

    private static final String ROOT_CACHE_KEY = MyConstants.DEFAULT_GAME_MODULE_NAME + "_TaskCheckinOrderCacheHelper";


    public static String createCheckinCachekey(String username, DateTime time)
    {
        return ROOT_CACHE_KEY + "_createCheckinCachekey_" + username + time.getDayOfYear();
    }

}
