package com.inso.modules.game.red_package.cache;

import com.inso.modules.passport.MyConstants;

public class RedPStaffLimitCache {

    private static final String ROOT_CACHE_KEY = MyConstants.DEFAULT_GAME_MODULE_NAME + RedPStaffLimitCache.class.getName();

    public static String findByStaffid( long staffid)
    {
        return ROOT_CACHE_KEY  + "_findByStaffid_" + staffid;
    }

}
