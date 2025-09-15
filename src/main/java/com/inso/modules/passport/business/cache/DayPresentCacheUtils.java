package com.inso.modules.passport.business.cache;

import com.inso.modules.common.model.BusinessType;
import com.inso.modules.passport.MyConstants;
import com.inso.modules.passport.business.model.PresentBusinessType;

public class DayPresentCacheUtils {

    private static final String ROOT_CACHE_KEY = MyConstants.DEFAULT_PASSPORT_MODULE_NAME + "_DayPresent_";


    public static String createFindDayPresentKey(String pdateString, long userid, BusinessType businessType, String businessKey)
    {
        StringBuilder builder = new StringBuilder();
        builder.append(ROOT_CACHE_KEY);
        builder.append("_createFindDayPresentKey_");
        builder.append(pdateString);
        builder.append(userid);
        builder.append(businessType.getCode());
        builder.append(businessKey);

        return builder.toString();
    }

    public static String findByOuTradeNo(String outradeno)
    {
        return ROOT_CACHE_KEY + "findByOuTradeNo" + outradeno;
    }

    public static String queryByUser(long userid, PresentBusinessType businessType)
    {
        return ROOT_CACHE_KEY + "queryByUser" + userid + businessType.getKey();
    }



}
