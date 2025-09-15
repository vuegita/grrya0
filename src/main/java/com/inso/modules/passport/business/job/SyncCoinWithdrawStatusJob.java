package com.inso.modules.passport.business.job;

import com.inso.framework.context.MyEnvironment;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.contract.Token20Manager;
import com.inso.modules.coin.contract.model.TransactionResult;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.common.model.RemarkVO;
import com.inso.modules.passport.business.model.UserWithdrawVO;
import com.inso.modules.passport.business.model.WithdrawOrder;
import com.inso.modules.passport.business.service.WithdrawOrderService;
import com.inso.modules.passport.money.UserPayManager;
import com.inso.modules.paychannel.model.PayProductType;
import com.inso.modules.web.SystemRunningMode;
import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class SyncCoinWithdrawStatusJob implements Job {

    private static Log LOG = LogFactory.getLog(SyncCoinWithdrawStatusJob.class);


    private WithdrawOrderService mWithdrawOrderService;
    private UserPayManager mUserPayMgr;

    private static boolean isRunning = false;

    public SyncCoinWithdrawStatusJob()
    {
        this.mWithdrawOrderService = SpringContextUtils.getBean(WithdrawOrderService.class);
        this.mUserPayMgr = SpringContextUtils.getBean(UserPayManager.class);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        if( !(SystemRunningMode.isCryptoMode() || SystemRunningMode.isBCMode()) )
        {
            return;
        }

        if(isRunning)
        {
            return;
        }
        isRunning = true;
        try {
            DateTime dataTime = new DateTime(context.getFireTime());
            start(dataTime);
        } finally {
            isRunning = false;
        }
    }

    public void start(DateTime dateTime)
    {
        // 2-3

        DateTime fromTime = dateTime.minusMinutes(6);
        DateTime toTime = dateTime.minusMinutes(5);
        doTask(fromTime, toTime);


        // 10 - 11
        fromTime = dateTime.minusMinutes(61);
        toTime = dateTime.minusMinutes(60);
        doTask(fromTime, toTime);

        // 10 - 11
//        toTime = dateTime.minusMinutes(90);
//        fromTime = dateTime.minusMinutes(91);
//        doTask(fromTime, toTime);

        fromTime = dateTime.minusMinutes(301);
        toTime = dateTime.minusMinutes(300);
        doTask(fromTime, toTime);
//
        fromTime = dateTime.minusMinutes(1441);
        toTime = dateTime.minusMinutes(1440);
        doTask(fromTime, toTime);

        // 10 - 11
//        fromTime = dateTime.minusMinutes(1771);
//        toTime = dateTime.minusMinutes(1770);
//        doTask(fromTime, toTime);


        if(MyEnvironment.isDev())
        {
            // all
            toTime = dateTime;
            fromTime = dateTime.minusDays(1000);
            doTask(fromTime, toTime);
        }
    }


    private void doTask(DateTime fromTime, DateTime toTime)
    {
        try {
//            String fromTimeString = fromTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
//            String toTimeString = toTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);

//            LOG.info("fromTime = " + fromTimeString + ", endTime = " + toTimeString);

            if(mWithdrawOrderService == null)
            {
                return;
            }
            mWithdrawOrderService.queryAllByUpdateTime(fromTime, toTime, new Callback<WithdrawOrder>() {
                @Override
                public void execute(WithdrawOrder o) {
                    try {
                        handleOrder(o);
                    } catch (Exception e) {
                        LOG.error("handle get status error:");
                    }
                }
            });
        } catch (Exception e) {
            LOG.error("doTask error:", e);
        }

    }

    public void doTask2()
    {
        try {
            String toTimeString = "2022-04-05 01:12:33";
            String fromTimeString = "2022-04-05 01:11:10";

//            LOG.info("fromTime = " + fromTimeString + ", endTime = " + toTimeString);

            mWithdrawOrderService.queryAll(fromTimeString, toTimeString, new Callback<WithdrawOrder>() {
                @Override
                public void execute(WithdrawOrder o) {
                    try {
                        handleOrder(o);
                        //LOG.info("withdraw order info : orderno = " + o.getNo() + ", productType = " + o.getPayProductType() + ", outTradeNo = " + o.getOutTradeNo());
                    } catch (Exception e) {
                        LOG.error("handle get status error:");
                    }
                }
            });
        } catch (Exception e) {
            LOG.error("handle error:", e);
        }

    }

    public void handleOrder(WithdrawOrder o)
    {
        String outTradeno = StringUtils.getNotEmpty(o.getOutTradeNo()).trim();
//        LOG.info("withdraw order info : orderno = " + o.getNo() + ", productType = " + o.getPayProductType() + ", outTradeNo = " + outTradeno + ", txStatus = " + o.getStatus());
//        LOG.info(FastJsonHelper.jsonEncode(o));

        OrderTxStatus status = OrderTxStatus.getType(o.getStatus());
        if(status != OrderTxStatus.WAITING)
        {
            return;
        }

        PayProductType productType = PayProductType.getType(o.getPayProductType());
        if(!(productType == PayProductType.COIN || productType != PayProductType.FIAT_2_STABLE_COIN))
        {
            return;
        }


        //LOG.info("withdraw order info : " + FastJsonHelper.jsonEncode(o));
        RemarkVO remarkVO = o.getRemarkVO();

        CryptoNetworkType networkType = CryptoNetworkType.getType(remarkVO.getString(UserWithdrawVO.KEY_IFSC));
        if(networkType == null)
        {
            return;
        }

//        String txHash = o.getOutTradeNo();
//        if(StringUtils.isEmpty(txHash))
//        {
//            return;
//        }

        TransactionResult txResult = Token20Manager.getInstance().getTransactionStatus(networkType, outTradeno);
        //LOG.info("Get order status: " + FastJsonHelper.jsonEncode(txResult));
        if(txResult == null)
        {
            return;
        }

        if(txResult.getTxStatus() == OrderTxStatus.REALIZED)
        {
            mUserPayMgr.doWithdrawSuccess(o.getNo(), null, null);
            //LOG.info("Get order status success: " + txResult.getTxStatus());
        }
        else if(txResult.getTxStatus() == OrderTxStatus.FAILED)
        {
            mUserPayMgr.refuseWithdrawOrder(o.getNo(), txResult.getMsg(), null);
            //LOG.info("Get order status fair: " + txResult.getTxStatus());
        }
    }


}
