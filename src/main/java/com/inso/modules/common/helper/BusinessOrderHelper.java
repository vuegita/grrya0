package com.inso.modules.common.helper;

import com.inso.modules.common.model.BusinessType;

public class BusinessOrderHelper {

    private static IdGenerator mIdGenerator = IdGenerator.newSingleWorder();


    public static String nextId(BusinessType businessType)
    {
        return mIdGenerator.nextId(businessType.getCode());
    }

}
