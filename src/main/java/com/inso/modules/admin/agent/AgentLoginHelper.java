package com.inso.modules.admin.agent;

import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.UUIDUtils;

public class AgentLoginHelper {


    private static final String ROOT_CACHE = AgentLoginHelper.class.getName();

//    public static String getUsername() {
//        try {
//            Subject subject = SecurityUtils.getSubject();
//            UserInfo merchantInfo = (UserInfo)subject.getPrincipal();
//            return merchantInfo.getName();
//        } catch (Exception exception) {
//        }
//    }

    public static String createAgentAccessKey(String loginname)
    {
        String uuid = UUIDUtils.getUUID();
        String cachekey = ROOT_CACHE + uuid;
        CacheManager.getInstance().setString(cachekey, loginname);
        return uuid;
    }

    public static String getAgentLoginName(String key)
    {
        String cachekey = ROOT_CACHE + key;
        String username = CacheManager.getInstance().getString(cachekey);
        CacheManager.getInstance().delete(cachekey);
        return username;
    }

}
