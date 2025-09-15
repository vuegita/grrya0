package com.inso.modules.game.task_checkin.logical;

import java.math.BigDecimal;

import com.inso.modules.common.model.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.cache.CacheManager;
import com.inso.modules.admin.config.PlatformConfig;
import com.inso.modules.game.GameErrorResult;
import com.inso.modules.game.task_checkin.cache.TaskCheckinOrderCacheHelper;
import com.inso.modules.game.task_checkin.helper.TaskCheckinOrderIdHelper;
import com.inso.modules.game.task_checkin.service.TaskCheckinOrderService;
import com.inso.modules.passport.money.PayApiManager;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.service.ConfigService;

@Component
public class TaskCheckinManger {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private TaskCheckinOrderService mTaskCheckinOrderService;

    @Autowired
    private PayApiManager mPayApiMgr;

    @Autowired
    private ConfigService mConfigService;

    public ErrorResult doCheckin(UserInfo userInfo)
    {
        // 1. 读取配置判断是否签到赠送
        boolean switchValue = mConfigService.getBoolean(false, PlatformConfig.ADMIN_PLATFORM_CONFIG_GAME_TASK_CHECKIN_SWITCH);
        if(!switchValue){
            return SystemErrorResult.ERR_SYS_OPT_FORBID;
        }

        //
        DateTime dateTime = DateTime.now();
        String cachekey = TaskCheckinOrderCacheHelper.createCheckinCachekey(userInfo.getName(), dateTime);
        if(CacheManager.getInstance().exists(cachekey))
        {
            return GameErrorResult.ERR_CHECKIN_EXIST;
        }
        // 写入缓存-表示已处理过，即使下面未已完成也不处理了
        CacheManager.getInstance().setString(cachekey, "1", CacheManager.EXPIRES_DAY);

        // 2. 读取赠送金额
        BigDecimal amount = mConfigService.getBigDecimal(false, PlatformConfig.ADMIN_PLATFORM_CONFIG_GAME_TASK_CHECKIN_AMOUNT);// new BigDecimal(4);
        if(amount == null || amount.compareTo(BigDecimal.ZERO) <= 0){
            return SystemErrorResult.ERR_SYS_OPT_FORBID;
        }

        String orderno = TaskCheckinOrderIdHelper.nextOrderId();
        UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());

        // create order
        mTaskCheckinOrderService.add(orderno, userInfo.getId(), userInfo.getName(), userAttr, amount);

        // pay
        FundAccountType accountType = FundAccountType.Spot;
        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
        ErrorResult result = mPayApiMgr.doPlatformPresentation(accountType, currencyType, BusinessType.GAME_TASKCHECKIN, orderno, userInfo, amount,null);
        if(result == SystemErrorResult.SUCCESS)
        {
            mTaskCheckinOrderService.updateStatus(orderno, OrderTxStatus.REALIZED);

            mTaskCheckinOrderService.clearCache(userInfo.getId());
        }

        return result;
    }

}
