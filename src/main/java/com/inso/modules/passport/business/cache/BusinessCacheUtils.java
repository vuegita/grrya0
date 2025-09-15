package com.inso.modules.passport.business.cache;

import com.inso.modules.common.model.BusinessType;
import com.inso.modules.passport.MyConstants;
import com.inso.modules.passport.business.model.BusinessOrder;
import com.inso.modules.passport.user.model.UserInfo;

import java.util.List;

public class BusinessCacheUtils {

    private static final String ROOT_CACHE_KEY = MyConstants.DEFAULT_PASSPORT_MODULE_NAME + "_business_";

    public static String queryLatestPage_100(long userid, BusinessType type)
    {
        return ROOT_CACHE_KEY + "queryScrollPageByUser_latest_100" + userid + type.getCode();
    }


    public static String queryByAgentOrUser(long agentid, long userid)
    {
        if(userid > 0)
        {
            return ROOT_CACHE_KEY + "queryByAgentOrUser" + userid;
        }
        return ROOT_CACHE_KEY + "queryByAgentOrUser" + agentid;
    }
}
