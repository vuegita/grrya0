package com.inso.modules.web.cache;

import com.inso.modules.web.logical.WebInfoManager;

public class WebInfoCacheHelper {

    private static final String ROOT_KEY = KeyCacheHelper.getModuleForWebKey() + "_WebInfoCacheHelper_";

    public static String getWebInfoCacheKey(WebInfoManager.TargetType targetType)
    {
        return ROOT_KEY + "getWebInfoCacheKey_" + targetType.getKey();
    }

    public static void main(String[] args) {
        System.out.println(getWebInfoCacheKey(WebInfoManager.TargetType.GAME_AB_BET_RULE));
    }

}
