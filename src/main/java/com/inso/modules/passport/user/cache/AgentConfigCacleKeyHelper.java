package com.inso.modules.passport.user.cache;

import com.inso.modules.passport.user.model.AgentConfigInfo;

public class AgentConfigCacleKeyHelper {

    private static String ROOT_CACHE = AgentConfigCacleKeyHelper.class.getName();

    public static String findByAgentId(long agentid, AgentConfigInfo.AgentConfigType type)
    {
        return ROOT_CACHE + "findByAgentId" + agentid + type.getKey();
    }


}
