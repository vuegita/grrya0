package com.inso.modules.passport.domain.cache;

public class AgentDomainCacheHelper {

    private static final String ROOT_CACHE_KEY = AgentDomainCacheHelper.class.getName();

    public static String findByUrl( String url)
    {
        return ROOT_CACHE_KEY + "findByUrl" + url ;
    }

}
