package com.inso.modules.passport.helper;

import com.inso.framework.cache.CacheManager;

public class SetDefPwdHelper {

    private static final String ROOT_CACHE = SetDefPwdHelper.class.getName();

    private static final String DEF_VALUE = "1";

    private static int EXPIRES = 600;

    public static void setUpdate(String username)
    {
        String cachekey = ROOT_CACHE + username;
        CacheManager.getInstance().setString(cachekey, DEF_VALUE, EXPIRES);
    }

    public static boolean exist(String username)
    {
        String cachekey = ROOT_CACHE + username;
        boolean exist = CacheManager.getInstance().exists(cachekey);
        if(exist)
        {
            CacheManager.getInstance().delete(cachekey);
        }
        return exist;
    }

    public static void main(String[] args) {
        String username = "u1";

        setUpdate(username);

        System.out.println(exist(username));

    }

}
