package com.inso.modules.ad.mall.cache;

import com.inso.framework.utils.StringUtils;

public class MallCommodityCacheHelper {

    private static final String ROOT_CACHE = MallCommodityCacheHelper.class.getName();


    public static String findByKey(long merchantid, long materielid)
    {
        return ROOT_CACHE + "findByKey" + merchantid + StringUtils.getEmpty() + materielid;
    }


    public static String findById(long id)
    {
        return ROOT_CACHE + "findById" + id;
    }


}
