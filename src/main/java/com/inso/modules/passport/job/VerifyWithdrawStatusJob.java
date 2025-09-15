//package com.inso.modules.passport.job;
//
//import com.inso.framework.log.Log;
//import com.inso.framework.log.LogFactory;
//import com.inso.framework.service.Callback;
//import com.inso.framework.spring.SpringContextUtils;
//import com.inso.framework.utils.DateUtils;
//import com.inso.framework.utils.StringUtils;
//import com.inso.modules.common.model.OrderTxStatus;
//import com.inso.modules.common.model.RemarkVO;
//import com.inso.modules.common.model.Status;
//import com.inso.modules.passport.business.model.WithdrawOrder;
//import com.inso.modules.passport.business.service.WithdrawOrderService;
//import com.inso.modules.paychannel.logical.payment.PaymentProcessorManager;
//import com.inso.modules.paychannel.logical.payment.model.PayoutResult;
//import com.inso.modules.paychannel.model.ChannelInfo;
//import com.inso.modules.paychannel.model.PayProductType;
//import com.inso.modules.paychannel.service.ChannelService;
//import org.joda.time.DateTime;
//import org.quartz.Job;
//import org.quartz.JobExecutionContext;
//import org.quartz.JobExecutionException;
//
//public class VerifyWithdrawStatusJob implements Job {
//
//    private static Log LOG = LogFactory.getLog(VerifyWithdrawStatusJob.class);
//
//    private WithdrawOrderService mWithdrawOrderService;
//
//    private ChannelService mChannelService;
//
//    private static boolean isRunning = false;
//
//    public VerifyWithdrawStatusJob()
//    {
//        this.mWithdrawOrderService = SpringContextUtils.getBean(WithdrawOrderService.class);
//        this.mChannelService = SpringContextUtils.getBean(ChannelService.class);
//    }
//
//
//    @Override
//    public void execute(JobExecutionContext context) throws JobExecutionException {
//
//        if(isRunning)
//        {
//            return;
//        }
//
//        isRunning = true;
//
//        try {
//            DateTime toTime = new DateTime(context.getFireTime());
//            DateTime fromTime = toTime.minusMinutes(1);
//
//            handleTask(fromTime, toTime);
//
//            toTime = toTime.minusMinutes(5);
//            fromTime = toTime.minusMinutes(100);
//            handleTask(fromTime, toTime);
//
//            toTime = toTime.minusMinutes(10);
//            fromTime = toTime.minusMinutes(11);
//            handleTask(fromTime, toTime);
//
//            toTime = toTime.minusMinutes(30);
//            fromTime = toTime.minusMinutes(31);
//            handleTask(fromTime, toTime);
//        } catch (Exception e) {
//            LOG.error("handle error:", e);
//        } finally {
//            isRunning = false;
//        }
//    }
//
//    private void handleTask(DateTime fromTime, DateTime toTime)
//    {
//
//        String fromTimeString = fromTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
//        String endTimeString = toTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
//
//        String checker = "System";
//        mWithdrawOrderService.queryAll(fromTimeString, endTimeString, new Callback<WithdrawOrder>() {
//            @Override
//            public void execute(WithdrawOrder orderInfo) {
//
//                try {
//                    PayProductType productType = PayProductType.getType(orderInfo.getPayProductType());
//                    if(productType != PayProductType.COIN)
//                    {
//                        return;
//                    }
//
//                    Status status = Status.getType(orderInfo.getStatus());
//                    if(status != Status.WAITING)
//                    {
//                        return;
//                    }
//
//                    if(StringUtils.isEmpty(orderInfo.getOutTradeNo()))
//                    {
//                        return;
//                    }
//
//                    RemarkVO remarkVO = orderInfo.getRemarkVO();
//                    long channelid = remarkVO.getIntValue(RemarkVO.KEY_CHANNEL_ID);
//                    ChannelInfo channelInfo = mChannelService.findById(false, channelid);
//
//                    PayoutResult payoutResult = PaymentProcessorManager.getIntance().getPayoutStatus(channelInfo, orderInfo);
//                    if(payoutResult == null)
//                    {
//                        return;
//                    }
//
//                    if(payoutResult.getmTxStatus() == OrderTxStatus.REALIZED || payoutResult.getmTxStatus() == OrderTxStatus.FAILED)
//                    {
//                        mWithdrawOrderService.updateTxStatus(orderInfo.getNo(), payoutResult.getmTxStatus(), null, null, null);
//                    }
//
//                } catch (Exception e) {
//                    LOG.error("handle error:", e);
//                }
//            }
//        });
//    }
//
//    public void test()
//    {
//        DateTime toTime = new DateTime();
//        DateTime fromTime = toTime.minusMinutes(1000);
//        handleTask(fromTime, toTime);
//    }
//}
