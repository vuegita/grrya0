package com.inso.modules.web.team.cache;

public class TeamBuyRecordCacleKeyHelper {

    private static String ROOT_CACHE = TeamBuyRecordCacleKeyHelper.class.getName();


    public static String findById(long id)
    {
        return ROOT_CACHE + "findById" + id;
    }

    public static String queryAllListByUserid(long userid)
    {
        return ROOT_CACHE + "queryAllListByUserid" + userid;
    }



}
