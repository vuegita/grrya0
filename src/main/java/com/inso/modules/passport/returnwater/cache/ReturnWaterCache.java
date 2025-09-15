package com.inso.modules.passport.returnwater.cache;

import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.passport.MyConstants;

public class ReturnWaterCache {

    private static final String ROOT_KEY = MyConstants.DEFAULT_GAME_MODULE_NAME + "_cache_return_water_";


    public static String findReturnWaterLogByUserid(long userid, FundAccountType accountType, ICurrencyType currencyType)
    {
        return ROOT_KEY + "findReturnWaterLogByUserid " + userid + accountType.getKey() + currencyType.getKey();
    }

    public static String queryReturnWaterLogByUserid(long userid, int level)
    {
        return ROOT_KEY + "queryReturnWaterLogByUserid " + userid + level;
    }

    public static String queryScrollPageByParentidAndGrantid(long userid,long parentid,long  grantid,int limit)
    {
        return ROOT_KEY + "queryScrollPageByParentidAndGrantid " + userid + parentid + grantid +limit;
    }

    public static String queryTotalNumByParentidAndGrantid(long userid,long parentid,long  grantid)
    {
        return ROOT_KEY + "queryTotalNumByParentidAndGrantid " + userid + parentid + grantid;
    }


}
