package com.inso.modules.admin.core.helper;

public class LimitHelper {

    private static final String cacheTimesKey = "input.password.error.three.times:";
    private static final String cacheErrorAccount = "input.account.error.three.times:";


    public static String createIPCacheKey(String ip)
    {
        return cacheTimesKey + ip;
    }

    public static String createAccountCacheKey(String account)
    {
        return cacheTimesKey + cacheErrorAccount;
    }
}
