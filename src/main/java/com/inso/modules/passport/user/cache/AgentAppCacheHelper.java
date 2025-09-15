package com.inso.modules.passport.user.cache;

public class AgentAppCacheHelper {

    private static final String ROOT_CACHE_KEY = AgentAppCacheHelper.class.getName();

    public static String findByAgentId(long userid)
    {
        return ROOT_CACHE_KEY + "findByAgentId" + userid;
    }

}
