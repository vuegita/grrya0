package com.inso.modules.web.team.cache;

import com.inso.modules.web.team.model.TeamBusinessType;

public class TeamBuyConfigCacleKeyHelper {

    private static String ROOT_CACHE = TeamBuyConfigCacleKeyHelper.class.getName();


    public static String findById(long id)
    {
        return ROOT_CACHE + "findById" + id;
    }

    public static String queryAllListByAgentidAndBusinessType(long agentid, TeamBusinessType businessType)
    {
        return ROOT_CACHE + "queryAllListByUserid" + agentid + businessType.getKey();
    }



}
