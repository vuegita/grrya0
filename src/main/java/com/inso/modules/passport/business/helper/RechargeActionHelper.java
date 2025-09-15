package com.inso.modules.passport.business.helper;

import java.math.BigDecimal;

import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.passport.business.model.RechargePresentStatus;
import com.inso.modules.passport.business.model.RechargePresentType;

/**
 * 充值活动赠送帮助类
 */
public class RechargeActionHelper {

    private static String ROOT_CACHE = RechargeActionHelper.class.getName();

    public static void saveAmount(RechargePresentType type, String username, String presentKey, RechargePresentStatus presentStatus)
    {
        if(type == null || StringUtils.isEmpty(presentKey))
        {
            return;
        }
        if(presentStatus == null || presentStatus.getPresentAmount() == null || presentStatus.getPresentAmount().compareTo(BigDecimal.ZERO) < 0)
        {
            return;
        }
        String cachekey = ROOT_CACHE + type.getKey() + username + presentKey;
        CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(presentStatus));
    }

    public static RechargePresentStatus getAmount(RechargePresentType type, String username, String presentKey)
    {
        if(type == null || StringUtils.isEmpty(presentKey))
        {
            return null;
        }
        String cachekey = ROOT_CACHE + type.getKey() + username + presentKey;
        RechargePresentStatus value = CacheManager.getInstance().getObject(cachekey, RechargePresentStatus.class);
        return value;
    }

    public static void deleteCache(RechargePresentType type, String username, String presentKey)
    {
        if(type == null || StringUtils.isEmpty(presentKey))
        {
            return;
        }
        String cachekey = ROOT_CACHE + type.getKey() + username + presentKey;
        CacheManager.getInstance().delete(cachekey);
    }


}
