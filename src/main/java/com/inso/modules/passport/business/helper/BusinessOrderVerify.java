package com.inso.modules.passport.business.helper;

import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.helper.BusinessOrderHelper;
import com.inso.modules.common.model.BusinessType;

/**
 * 业务订单号检验
 */
public class BusinessOrderVerify {


    public static boolean verify(String orderno, BusinessType businessType)
    {
        String codeString = orderno.substring(17, 19);
        return StringUtils.asInt(codeString) == businessType.getCode();
    }


    public static void main(String[] args) {

        // 11
        String orderno = BusinessOrderHelper.nextId(BusinessType.PLATFORM_PRESENTATION);

        System.out.println(orderno);

        System.out.println(orderno.substring(17, 19));
    }

}
