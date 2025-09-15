package com.inso.modules.common.job;

import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.money.UserPayManager;
import com.inso.modules.passport.business.model.RechargeOrder;
import com.inso.modules.passport.business.model.WithdrawOrder;
import com.inso.modules.passport.business.service.RechargeOrderService;
import com.inso.modules.passport.business.service.WithdrawOrderService;

/**
 * 修正订单状态(暂时未启用)
 *
 */
public class FixOrderStatusJob implements Job {

    private static Log LOG = LogFactory.getLog(FixOrderStatusJob.class);

    private RechargeOrderService mRechargeOrderService;
    private WithdrawOrderService mWithdrawOrderService;

    private UserPayManager mUserPayManager;

    public FixOrderStatusJob()
    {
        this.mRechargeOrderService = SpringContextUtils.getBean(RechargeOrderService.class);
        this.mWithdrawOrderService = SpringContextUtils.getBean(WithdrawOrderService.class);
        this.mUserPayManager = SpringContextUtils.getBean(UserPayManager.class);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        DateTime nowTime = new DateTime();

        // 修正最近10分钟的数据
        String startTime = nowTime.minusMinutes(80).toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String endTime = nowTime.minusSeconds(1).toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);

        fixRecharge(startTime, endTime);

        fixWithdraw(startTime, endTime);
    }

    /**
     * 修正充值-如果异常，则不可能回复ok
     * @param fromTime
     * @param endTime
     */
    public void fixRecharge(String fromTime, String endTime)
    {
        mRechargeOrderService.queryAll(fromTime, endTime, new Callback<RechargeOrder>() {
            @Override
            public void execute(RechargeOrder o) {
                try {
                    OrderTxStatus txStatus = OrderTxStatus.getType(o.getStatus());
                    if(txStatus != OrderTxStatus.CAPTURED)
                    {
                        return;
                    }

                    mUserPayManager.doRechargeSuccessAction(o.getNo(), null,null);
                } catch (Exception exception) {
                    LOG.error("fixRecharge error:", exception);
                }
            }
        });
    }

    /**
     * 修正提现- 如果异常，则不可能回复ok
     * @param fromTime
     * @param endTime
     */
    public void fixWithdraw(String fromTime, String endTime)
    {
        mWithdrawOrderService.queryAll(fromTime, endTime, new Callback<WithdrawOrder>() {
            @Override
            public void execute(WithdrawOrder o) {
                OrderTxStatus txStatus = OrderTxStatus.getType(o.getStatus());
                if(txStatus == OrderTxStatus.REFUNDING)
                {
                    mUserPayManager.refuseWithdrawOrder(o.getNo(), StringUtils.getEmpty(), StringUtils.getEmpty());
                }

            }
        });
    }

}
