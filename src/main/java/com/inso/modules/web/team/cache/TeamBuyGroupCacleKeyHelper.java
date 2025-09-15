package com.inso.modules.web.team.cache;

import com.inso.modules.web.team.model.TeamBusinessType;

public class TeamBuyGroupCacleKeyHelper {

    private static String ROOT_CACHE = TeamBuyGroupCacleKeyHelper.class.getName();


    public static String findById(long id)
    {
        return ROOT_CACHE + "findById" + id;
    }

    public static String findLatestByUseridAndBusinessType(long userid, TeamBusinessType businessType)
    {
        return ROOT_CACHE + "findLatestByUseridAndBusinessType" + userid + businessType.getKey();
    }

    public static String queryAllListByUserid(long userid, TeamBusinessType businessType)
    {
        return ROOT_CACHE + "queryAllListByUserid" + userid + businessType.getKey();
    }

    public static String doCreateGroup( long userid, TeamBusinessType businessType)
    {
        return ROOT_CACHE + "doCreateGroup" + userid + businessType.getKey();
    }


}
