package com.inso.modules.web.activity.cache;

import com.inso.modules.web.activity.model.ActivityBusinessType;
import com.inso.modules.web.team.model.TeamBusinessType;

public class ActivityCacleKeyHelper {

    private static String ROOT_CACHE = ActivityCacleKeyHelper.class.getName();


    public static String findById(long id)
    {
        return ROOT_CACHE + "findById" + id;
    }

    public static String findLatestActive(ActivityBusinessType businessType)
    {
        return ROOT_CACHE + "findLatestActive" + businessType.getKey();
    }


}
