package com.inso.modules.game.red_package.helper;

import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.MD5;
import com.inso.framework.utils.StringUtils;

public class RedPIDSignUtils {

    private static String CACHE_KEY = RedPIDSignUtils.class.getName();

//    private static final String KEY = "af3e666jb3e149ec";

    private static final String DEFAULT_SALT = "fsdafs90w354ludtgfd(I^";

    public static String encrypt(long id)
    {
        String key = MD5.encode(id + DEFAULT_SALT + System.currentTimeMillis());
        String cachekey = CACHE_KEY + key;
        CacheManager.getInstance().setString(cachekey, id + StringUtils.getEmpty(), CacheManager.EXPIRES_DAY * 2);
        return key;
    }

    public static long decrypt(String str)
    {
        String cachekey = CACHE_KEY + str;
        return CacheManager.getInstance().getLong(cachekey);
    }

    public static void main(String[] args) {

        String encryptid = encrypt(2);
        System.out.println("========" + encryptid);

        System.out.println("========" + decrypt(encryptid));

    }


}
