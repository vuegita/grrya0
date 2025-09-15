package com.inso.modules.coin.approve.job;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.mq.MQManager;
import com.inso.framework.mq.MQSupport;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.CoinMesageManager;
import com.inso.modules.coin.contract.ApproveTokenManager;
import com.inso.modules.coin.contract.model.TransactionResult;
import com.inso.modules.coin.approve.logical.TransferOrderManager;
import com.inso.modules.coin.core.model.TransferOrderInfo;
import com.inso.modules.coin.core.model.TriggerOperatorType;
import com.inso.modules.coin.approve.service.ApproveAuthService;
import com.inso.modules.coin.approve.service.TransferOrderService;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 同步状态
 * 余额 |　
 */
public class UploadTransferOrderJob implements Job {

    private static Log LOG = LogFactory.getLog(UploadTransferOrderJob.class);

    private static final String QUEUE_NAME = "inso_coin_crypto_approve_transfer_token";

    private static final String KEY_COLUMN_ORDERNO = "orderno";
    private static final String KEY_COLUMN_TYPE = "type";

    private UserService mUserService;
    private UserAttrService userAttrService;
    private ApproveAuthService mApproveAuthService;

    private TransferOrderService mTransferOrderService;
    private TransferOrderManager mTransferOrderManager;
    private CoinMesageManager mCoinMesageManager;

    private static boolean isRunning = false;

    private static boolean isDEV = MyEnvironment.isDev();

    private static boolean isRunningMQ = false;
    private static MQSupport mq = MQManager.getInstance().getMQ(MQManager.MQType.REDIS);

    private static ExecutorService mThreadPool = Executors.newFixedThreadPool(10);


    public UploadTransferOrderJob()
    {

        this.mUserService = SpringContextUtils.getBean(UserService.class);
        this.userAttrService = SpringContextUtils.getBean(UserAttrService.class);
        this.mApproveAuthService = SpringContextUtils.getBean(ApproveAuthService.class);
        this.mTransferOrderService = SpringContextUtils.getBean(TransferOrderService.class);

        this.mTransferOrderManager = SpringContextUtils.getBean(TransferOrderManager.class);
        this.mCoinMesageManager = SpringContextUtils.getBean(CoinMesageManager.class);

    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

//        if(!(SystemRunningMode.isCryptoMode() || SystemRunningMode.isFundsMode()))
//        {
//            return;
//        }

        addMQTask();

        if(isRunning)
        {
            return;
        }
        isRunning = true;

        try {
            doUploadTask();
        } catch (Exception e) {
        }

        isRunning = false;
    }


    private void doUploadTask()
    {
        DateTime toTime = new DateTime();

        //
        doUploadByTime(toTime, getStepLength());
        doUploadByTime(toTime.minusMinutes(10), 1);
        doUploadByTime(toTime.minusMinutes(60), 1);
    }

    private void doUploadByTime(DateTime toTime, int stopLen)
    {
        if(!ApproveTokenManager.getInstance().isInit())
        {
            return;
        }
        DateTime fromTime = toTime.minusMinutes(stopLen);
        mTransferOrderService.queryAll(fromTime, toTime, OrderTxStatus.NEW, new Callback<TransferOrderInfo>() {
            @Override
            public void execute(TransferOrderInfo model) {
                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            handleOrder(model);
                        } catch (Exception e) {
                            LOG.error("handleOrder error:", e);
                        }
                    }
                });
            }
        });
    }

    private void handleOrder(TransferOrderInfo model)
    {
        OrderTxStatus txStatus = OrderTxStatus.getType(model.getStatus());
        if(!(txStatus == OrderTxStatus.NEW || txStatus == OrderTxStatus.PENDING))
        {
            return;
        }

        if(txStatus == OrderTxStatus.NEW )
        {
            mTransferOrderService.updateInfo(model.getNo(), OrderTxStatus.PENDING, null, null);
        }

        TransactionResult result = ApproveTokenManager.getInstance().transferFrom(model);
        if(result == null)
        {
            return;
        }

        if(result.getTxStatus() == OrderTxStatus.WAITING )
        {
            mTransferOrderService.updateInfo(model.getNo(), OrderTxStatus.WAITING, result.getExternalTxnid(), null);
        }

        else if(result.getTxStatus() == OrderTxStatus.FAILED)
        {
            mTransferOrderService.updateInfo(model.getNo(), OrderTxStatus.FAILED, result.getExternalTxnid(), result.getMsg());
            mCoinMesageManager.sendTransferOrderResultMessage(model, false, result.getMsg());
        }
    }

    private int getStepLength()
    {
        if(isDEV)
        {
            return 1000;
        }
        return 2;
    }

    private void addMQTask()
    {
        synchronized (UploadTransferOrderJob.class)
        {
            if(isRunningMQ)
            {
                return;
            }
            isRunningMQ = true;

            mq.subscribe(QUEUE_NAME, null, new Callback<String>() {
                @Override
                public void execute(String jsonStr) {

                    DateTime dateTime = new DateTime();
                    if(dateTime.getSecondOfMinute() >= 55)
                    {
                        // 55s过后走自动提交
                        return;
                    }

                    JSONObject jsonObject = FastJsonHelper.toJSONObject(jsonStr);
                    if(jsonObject == null || jsonObject.isEmpty())
                    {
                        return;
                    }

                    String orderno = jsonObject.getString(KEY_COLUMN_ORDERNO);
                    if(StringUtils.isEmpty(orderno))
                    {
                        return;
                    }

                    String type = jsonObject.getString(KEY_COLUMN_TYPE);
                    if(!StringUtils.isEmpty(type) && TriggerOperatorType.Monitor.getKey().equalsIgnoreCase(type))
                    {
                        mTransferOrderManager.doTransfer(orderno);
                        return;
                    }

                    TransferOrderInfo orderInfo = mTransferOrderService.findById(orderno);
                    if(orderInfo == null)
                    {
                        return;
                    }
                    synchronized (orderInfo.getCtrNetworkType())
                    {
                        handleOrder(orderInfo);
                    }
                }
            });

        }
    }

    public static void sendMessage(String orderno)
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(KEY_COLUMN_ORDERNO, orderno);
        mq.sendMessage(QUEUE_NAME, jsonObject.toJSONString());
    }


    public static void sendMessageByMonitor(String orderno)
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(KEY_COLUMN_ORDERNO, orderno);
        jsonObject.put(KEY_COLUMN_TYPE, TriggerOperatorType.Monitor.getKey());
        mq.sendMessage(QUEUE_NAME, jsonObject.toJSONString());
    }


}
