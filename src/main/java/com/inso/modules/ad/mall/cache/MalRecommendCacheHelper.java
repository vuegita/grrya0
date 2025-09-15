package com.inso.modules.ad.mall.cache;

import com.inso.framework.utils.StringUtils;
import com.inso.modules.ad.mall.model.MallRecommentType;

public class MalRecommendCacheHelper {

    private static final String ROOT_CACHE = MalRecommendCacheHelper.class.getName();


    public static String countByid(long merchantid, long materielid)
    {
        return ROOT_CACHE + "countByid" + merchantid + StringUtils.getEmpty() + materielid;
    }

    public static String queryListByType(MallRecommentType recommentType)
    {
        return ROOT_CACHE + "queryListByType" + recommentType.getKey();
    }




}
