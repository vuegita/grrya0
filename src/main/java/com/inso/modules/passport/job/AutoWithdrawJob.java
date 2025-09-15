package com.inso.modules.passport.job;

import com.inso.framework.service.Callback;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.DateUtils;
import com.inso.modules.common.config.SystemConfig;
import com.inso.modules.passport.money.UserPayManager;
import com.inso.modules.passport.business.model.WithdrawOrder;
import com.inso.modules.passport.business.service.WithdrawOrderService;
import com.inso.modules.web.service.ConfigService;
import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 自动提现
 */
public class AutoWithdrawJob implements Job {

    private ConfigService mConfigService;
    private UserPayManager mUserPayMgr;

    private WithdrawOrderService mWithdrawOrderService;

    public AutoWithdrawJob()
    {
        this.mConfigService = SpringContextUtils.getBean(ConfigService.class);
        this.mUserPayMgr = SpringContextUtils.getBean(UserPayManager.class);
        this.mWithdrawOrderService = SpringContextUtils.getBean(WithdrawOrderService.class);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        boolean enableAutoWithdraw = mConfigService.getBoolean(false, SystemConfig.USER_AUTO_WITHDRAW_MAX_MONEY.getKey());
        if(!enableAutoWithdraw)
        {
            // 非自动提现模式
            return;
        }

        DateTime dateTime = new DateTime(context.getFireTime());
        DateTime fromTime = dateTime.minusDays(1);

        String fromTimeString = fromTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String endTimeString = dateTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);

        String checker = "System";
        mWithdrawOrderService.queryAll(fromTimeString, endTimeString, new Callback<WithdrawOrder>() {
            @Override
            public void execute(WithdrawOrder orderInfo) {
                mUserPayMgr.passWithdrawOrderToWaiting(orderInfo.getNo(), checker);
            }
        });

    }
}
