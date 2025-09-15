package com.inso.modules.coin.approve.job;

import com.inso.framework.context.MyEnvironment;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.CoinMesageManager;
import com.inso.modules.coin.contract.ApproveTokenManager;
import com.inso.modules.coin.contract.Token20Manager;
import com.inso.modules.coin.contract.model.TransactionResult;
import com.inso.modules.coin.approve.logical.TransferOrderManager;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.core.model.TransferOrderInfo;
import com.inso.modules.coin.approve.service.ApproveAuthService;
import com.inso.modules.coin.approve.service.TransferOrderService;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 同步状态
 * 余额 |　
 */
public class VerifyTransferOrderJob implements Job {

    private static Log LOG = LogFactory.getLog(VerifyTransferOrderJob.class);

    private UserService mUserService;
    private UserAttrService userAttrService;
    private ApproveAuthService mApproveAuthService;

    private TransferOrderService mTransferOrderService;

    private TransferOrderManager mTransferOrderMgr;
    private CoinMesageManager mCoinMesageManager;

    private static boolean isRunning = false;

    private static boolean isDEV = MyEnvironment.isDev();

//    private static ExecutorService mThreadPool = Executors.newFixedThreadPool(10);

    public VerifyTransferOrderJob()
    {
        this.mUserService = SpringContextUtils.getBean(UserService.class);
        this.userAttrService = SpringContextUtils.getBean(UserAttrService.class);
        this.mApproveAuthService = SpringContextUtils.getBean(ApproveAuthService.class);
        this.mTransferOrderService = SpringContextUtils.getBean(TransferOrderService.class);

        this.mTransferOrderMgr = SpringContextUtils.getBean(TransferOrderManager.class);
        this.mCoinMesageManager = SpringContextUtils.getBean(CoinMesageManager.class);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
//        if(!SystemRunningMode.isCryptoMode())
//        {
//            return;
//        }

        //LOG.info("Start VerifyTransferOrderJob ================");

        if(isRunning)
        {
            return;
        }
        isRunning = true;

        try {
            doVerifyTask();
        } catch (Exception e) {
        }

        isRunning = false;
    }

    private void doVerifyTask()
    {
        DateTime toTime = new DateTime().minusMinutes(1);

        //
        doVeiryByTime(toTime, getStepLength());
        doVeiryByTime(toTime.minusMinutes(10), 1);
//        doVeiryByTime(toTime.minusMinutes(60), 1);
    }

    private void doVeiryByTime(DateTime toTime, int stepLen)
    {
        if(!ApproveTokenManager.getInstance().isInit())
        {
            return;
        }

        DateTime fromTime = toTime.minusMinutes(stepLen);
        mTransferOrderService.queryAll(fromTime, toTime, OrderTxStatus.WAITING, new Callback<TransferOrderInfo>() {
            @Override
            public void execute(TransferOrderInfo model) {

                try {
                    if(StringUtils.isEmpty(model.getOutTradeNo()))
                    {
                        return;
                    }

//                CryptoCurrency currency = CryptoCurrency.getType(model.getCurrencyType());
                    CryptoNetworkType networkType = CryptoNetworkType.getType(model.getCtrNetworkType());

                    TransactionResult result = Token20Manager.getInstance().getTransactionStatus(networkType, model.getOutTradeNo());
                    if(result == null)
                    {
                        return;
                    }

                    if(result.getTxStatus() == OrderTxStatus.REALIZED )
                    {
                        mTransferOrderService.updateInfo(model.getNo(), OrderTxStatus.REALIZED, null, null);

                        handleExtraInfo(model);

                        mCoinMesageManager.sendTransferOrderResultMessage(model, true, null);
                    }

                    else if(result.getTxStatus() == OrderTxStatus.FAILED)
                    {
                        mTransferOrderService.updateInfo(model.getNo(), OrderTxStatus.FAILED, null, result.getMsg());
                        mCoinMesageManager.sendTransferOrderResultMessage(model, false, result.getMsg());
                    }
                } catch (Exception e) {
                    LOG.error("handle error:", e);
                }

            }
        });
    }

    private void handleExtraInfo(TransferOrderInfo model)
    {
        this.mTransferOrderMgr.handleExtraEvent(model);
    }

    private int getStepLength()
    {
//        if(isDEV)
//        {
//            return 1000;
//        }
        return 1;
    }


    public void test()
    {
        DateTime nowTime = new DateTime();
        DateTime fromTime = nowTime.minusDays(1);
        mTransferOrderService.queryAll(fromTime, nowTime, OrderTxStatus.WAITING, new Callback<TransferOrderInfo>() {
            @Override
            public void execute(TransferOrderInfo model) {


                try {
//                    CryptoCurrency currency = CryptoCurrency.getType(model.getCurrencyType());
                    CryptoNetworkType networkType = CryptoNetworkType.getType(model.getCtrNetworkType());

                    TransactionResult result = Token20Manager.getInstance().getTransactionStatus(networkType, model.getOutTradeNo());
                    if(result == null)
                    {
                        return;
                    }

                    if(result.getTxStatus() == OrderTxStatus.REALIZED )
                    {
                        mTransferOrderService.updateInfo(model.getNo(), OrderTxStatus.REALIZED, null, null);
                    }

                    else if(result.getTxStatus() == OrderTxStatus.FAILED)
                    {
                        mTransferOrderService.updateInfo(model.getNo(), OrderTxStatus.FAILED, null, result.getMsg());
                    }
                } catch (Exception e) {
                    LOG.error("update transfer status error:", e);
                }
            }
        });
    }


}
