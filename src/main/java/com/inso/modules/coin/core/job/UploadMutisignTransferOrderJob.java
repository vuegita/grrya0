package com.inso.modules.coin.core.job;

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
import com.inso.modules.coin.contract.MutisignManager;
import com.inso.modules.coin.contract.model.TransactionResult;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.core.model.MutiSignTransferOrderInfo;
import com.inso.modules.coin.core.service.MutiSignService;
import com.inso.modules.coin.core.service.MutisignTransferOrderService;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.SystemRunningMode;
import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 同步状态
 * 余额 |　
 */
public class UploadMutisignTransferOrderJob implements Job {

    private static Log LOG = LogFactory.getLog(UploadMutisignTransferOrderJob.class);

    private static final String QUEUE_NAME = "inso_coin_crypto_mutisign_approve_transfer_token";

    private static final String KEY_COLUMN_ORDERNO = "orderno";
    private static final String KEY_COLUMN_TYPE = "type";

    private UserService mUserService;
    private UserAttrService userAttrService;
    private MutiSignService mApproveAuthService;

    private MutisignTransferOrderService mTransferOrderService;
    private CoinMesageManager mCoinMesageManager;


    private static boolean isDEV = MyEnvironment.isDev();

    private static boolean isRunningMQ = false;
    private static MQSupport mq = MQManager.getInstance().getMQ(MQManager.MQType.REDIS);

    public UploadMutisignTransferOrderJob()
    {
        this.mUserService = SpringContextUtils.getBean(UserService.class);
        this.userAttrService = SpringContextUtils.getBean(UserAttrService.class);
        this.mApproveAuthService = SpringContextUtils.getBean(MutiSignService.class);
        this.mTransferOrderService = SpringContextUtils.getBean(MutisignTransferOrderService.class);

        this.mCoinMesageManager = SpringContextUtils.getBean(CoinMesageManager.class);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        if(!SystemRunningMode.isCryptoMode())
        {
            return;
        }
        addMQTask();
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
//        mTransferOrderService.queryAll(fromTime, toTime, OrderTxStatus.NEW, new Callback<TransferOrderInfo>() {
//            @Override
//            public void execute(TransferOrderInfo model) {
//                mThreadPool.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            handleOrder(model);
//                        } catch (Exception e) {
//                            LOG.error("handleOrder error:", e);
//                        }
//                    }
//                });
//            }
//        });
    }

    private void handleOrder(MutiSignTransferOrderInfo model)
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

        CryptoNetworkType networkType = CryptoNetworkType.getType(model.getNetworkType());
        CryptoCurrency currency = CryptoCurrency.getType(model.getCurrencyType());

        TransactionResult result = MutisignManager.getInstance().transfer(networkType, currency, model.getFromAddress(), model.getToAddress(), model.getTotalAmount());
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
            mTransferOrderService.updateInfo(model.getNo(), OrderTxStatus.FAILED, result.getExternalTxnid(), null);
            //mCoinMesageManager.sendTransferOrderResultMessage(model, false, result.getMsg());
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
        synchronized (UploadMutisignTransferOrderJob.class)
        {
            if(isRunningMQ)
            {
                return;
            }
            isRunningMQ = true;

            mq.subscribe(QUEUE_NAME, null, new Callback<String>() {
                @Override
                public void execute(String jsonStr) {

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


                    MutiSignTransferOrderInfo orderInfo = mTransferOrderService.findById(orderno);
                    if(orderInfo == null)
                    {
                        return;
                    }
                    handleOrder(orderInfo);
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

}
