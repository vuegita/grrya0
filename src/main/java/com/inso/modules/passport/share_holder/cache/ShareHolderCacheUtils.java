package com.inso.modules.passport.share_holder.cache;

public class ShareHolderCacheUtils {

    private static final String ROOT_CACHE_KEY = ShareHolderCacheUtils.class.getName();

    public static String findByUserId(long userid)
    {
        return ROOT_CACHE_KEY + "findByUserId" + userid;
    }
}
