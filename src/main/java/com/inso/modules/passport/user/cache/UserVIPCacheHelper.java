package com.inso.modules.passport.user.cache;

import com.inso.modules.web.model.VIPType;

public class UserVIPCacheHelper {

    private static final String ROOT_CACHE_KEY = UserVIPCacheHelper.class.getName();

    public static String findByUserId(long userid, VIPType vipType)
    {
        return ROOT_CACHE_KEY + "findByUserId" + userid + vipType.getKey();
    }

}
