package com.inso.modules.coin.defi_mining.job;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.core.model.*;
import com.inso.modules.coin.core.service.ProfitConfigService;
import com.inso.modules.coin.defi_mining.logical.MiningProductManager;
import com.inso.modules.coin.defi_mining.service.MiningProductService;
import com.inso.modules.common.model.*;
import com.inso.modules.coin.approve.service.ApproveAuthService;
import com.inso.modules.coin.defi_mining.model.MiningOrderInfo;
import com.inso.modules.coin.defi_mining.model.MiningRecordInfo;
import com.inso.modules.coin.defi_mining.service.MiningOrderService;
import com.inso.modules.coin.defi_mining.service.MiningRecordService;
import com.inso.modules.passport.money.PayApiManager;
import com.inso.modules.passport.business.model.ReturnWaterType;
import com.inso.modules.passport.money.model.UserMoney;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.returnwater.ReturnWaterManager;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.SystemRunningMode;
import com.inso.modules.web.service.ConfigService;
import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

public class SettleDayMiningRecordJob implements Job {

    private static Log LOG = LogFactory.getLog(SettleDayMiningRecordJob.class);

    private static final long DEFAULT_MIN_SETTLE_PERIOD = 86400 * 1000 / 4; // 1天

    private static final String ROOT_CACHE = SettleDayMiningRecordJob.class.getName();

    private static final String SETTL_RUNNING_E_STATUS_CACHE_KEY = ROOT_CACHE + "_settle_running_status";

    public static final String SETTLE_USER_STATUS_CACHE_KEY = ROOT_CACHE + "_settle_user_status";

    private MiningProductService miningProductService;
    private MiningRecordService miningRecordService;
    private ApproveAuthService mApproveAuthService;

    private MiningOrderService miningOrderService;

    private MiningProductManager miningProductManager;

    private UserService mUserService;
    private UserAttrService mUserAttrService;
    private UserMoneyService moneyService;

    private ConfigService mConfigService;

    private PayApiManager mPayApiManager;

    private ReturnWaterManager mReturnWaterManager;

    private ProfitConfigService mProfitConfigService;

    public SettleDayMiningRecordJob()
    {
        this.mUserService = SpringContextUtils.getBean(UserService.class);
        this.mUserAttrService = SpringContextUtils.getBean(UserAttrService.class);
        this.moneyService = SpringContextUtils.getBean(UserMoneyService.class);


        this.miningProductService = SpringContextUtils.getBean(MiningProductService.class);
        this.miningRecordService = SpringContextUtils.getBean(MiningRecordService.class);
        this.mApproveAuthService = SpringContextUtils.getBean(ApproveAuthService.class);
        this.miningOrderService = SpringContextUtils.getBean(MiningOrderService.class);
        this.miningProductManager = SpringContextUtils.getBean(MiningProductManager.class);


        this.mConfigService = SpringContextUtils.getBean(ConfigService.class);
        this.mPayApiManager = SpringContextUtils.getBean(PayApiManager.class);

        this.mReturnWaterManager = SpringContextUtils.getBean(ReturnWaterManager.class);

        this.mProfitConfigService = SpringContextUtils.getBean(ProfitConfigService.class);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        if(!SystemRunningMode.isCryptoMode())
        {
            return;
        }

        bgTask(false);
    }

    public void start()
    {
        bgTask(false);
    }

//    public void resettle(MiningRecordInfo recordInfo)
//    {
//        DateTime todayTime = new DateTime();
//        int dayOfYear = todayTime.getDayOfYear();
//
//        Map<Long, List<ProfitConfigInfo>> profitConfigMaps = Maps.newHashMap();
//
//        UserInfo agentInfo = mUserService.findByUsername(false, UserInfo.DEFAULT_GAME_SYSTEM_AGENT);
//
//        handleSettle(false, recordInfo, dayOfYear, profitConfigMaps, agentInfo);
//    }

//    private String mAddress = "0x25379D5891dB2a3B246960360D8B14Fd6ED46594";
    private String username = "c_0xDEAc9aa0437823020581308C1E0be895deeB0013";
    private void bgTask(boolean isDebug)
    {
        updateSettleStatus(true);

        DateTime todayTime = new DateTime();
        int dayOfYear = todayTime.getDayOfYear();

        try {
            Map<String, List<ProfitConfigInfo>> profitConfigMaps = Maps.newHashMap();
            UserInfo agentInfo = mUserService.findByUsername(false, UserInfo.DEFAULT_GAME_SYSTEM_AGENT);


            miningRecordService.queryAll(new Callback<MiningRecordInfo>() {
                @Override
                public void execute(MiningRecordInfo model) {
//                    if(!username.equalsIgnoreCase(model.getUsername()))
//                    {
//                        return;
//                    }

                    handleSettle(isDebug, model, dayOfYear, profitConfigMaps, agentInfo);
                }
            });
        } catch (Exception e) {
            LOG.error("handle error:", e);
        }
        updateSettleStatus(false);
    }

    private void handleSettle(boolean isDebug, MiningRecordInfo model, int dayOfYear, Map<String, List<ProfitConfigInfo>> profitConfigMaps, UserInfo agentInfo)
    {
        boolean success = false;
        StringBuffer settleResult = new StringBuffer();
        settleResult.append("username = " + model.getUsername());
        settleResult.append("currency = " + model.getQuoteCurrency());
        try {
            Status status = Status.getType(model.getStatus());
            if(status != Status.ENABLE)
            {
                settleResult.append(", result = false");
                settleResult.append(", errmsg = 状态关闭");
                return;
            }

            //LOG.info("start settle record info : " + FastJsonHelper.jsonEncode(model));
            if(model.getExpectedRate().compareTo(BigDecimal.ZERO) <= 0)
            {
//                settleResult.append("result = 收益率为0");
                settleResult.append(", result = false");
                settleResult.append(", errmsg = 收益设置<=0");
                return;
            }

            if(model.getExpectedRate().compareTo(BigDecimalUtils.DEF_1) >= 0)
            {
                settleResult.append(", result = false");
                settleResult.append(", errmsg = 收益设置>0");
                return;
            }

            // 重新结算要过虑了
            String cachekey = SETTLE_USER_STATUS_CACHE_KEY + model.getId() + dayOfYear;
            if(CacheManager.getInstance().exists(cachekey))
            {
                settleResult.append("result = false");
                settleResult.append(", errmsg = 已结算过");
                return;
            }

            long userid = model.getUserid();
            long contractid = model.getContractid();
            ApproveAuthInfo authInfo = mApproveAuthService.findByUseridAndContractId(false, userid, contractid);
//            if(isDebug)
//            {
//                LOG.info("start settle approve info : " + FastJsonHelper.jsonEncode(authInfo));
//            }

            if(authInfo == null)
            {
                settleResult.append(", result = false");
                settleResult.append(", errmsg = 未授权");
                return;
            }

            if(!authInfo.getCurrencyType().equalsIgnoreCase(model.getQuoteCurrency()))
            {
                settleResult.append("result = false");
                settleResult.append(", errmsg = 币种异常, auth-currency = " + authInfo.getCurrencyType() + ", record-currency=" + model.getQuoteCurrency());
                miningRecordService.deleteByid(model);
                return;
            }

            // 授权取消
            if(authInfo.getAllowance().compareTo(ApproveAuthInfo.DEFAULT_MAX_ALLOWANCE) < 0)
            {
                settleResult.append(", result = false");
                settleResult.append(", errmsg = 取消授权");
                return;
            }

            // 小于最低余额没有收益,
            if(authInfo.getBalance().compareTo(model.getMinWalletBalance()) <= 0 && model.getVoucherNodeValue().compareTo(model.getMinWalletBalance()) <= 0)
            {
                settleResult.append(", result = false");
                settleResult.append(", errmsg = 系统配置最低余额=" + model.getMinWalletBalance() + ", 用户余额=" + authInfo.getBalance());
                settleResult.append(", voucherNodeValue=" + model.getVoucherNodeValue());
                return;
            }

            //
            ApproveFromType approveFromType = ApproveFromType.getType(authInfo.getFrom());
            if(approveFromType == ApproveFromType.DEFI_MINING_TIER)
            {
                success = settleTierMining(isDebug, profitConfigMaps, agentInfo, model, authInfo, cachekey);
                if(!success)
                {
                    settleResult.append(", result = false");
                    settleResult.append(", errmsg = 获取收益率异常!");
                }
                return;
            }
            else
            {
                success = settleDefaultMining(isDebug, model, authInfo, cachekey, null);
            }
        } catch (Exception e) {
            LOG.error("handleSettle error:", e);
        } finally {
            if(!success || isDebug)
            {
                LOG.error("log result: " + settleResult.toString());
            }
        }
    }

    private boolean settleDefaultMining(boolean isDebug, MiningRecordInfo model, ApproveAuthInfo authInfo, String cachekey, BigDecimal expectRate)
    {
        if(expectRate == null)
        {
            expectRate = model.getExpectedRate();
        }
        CryptoNetworkType networkType = CryptoNetworkType.getType(model.getNetworkType());
        ICurrencyType currency = CryptoCurrency.getType(model.getQuoteCurrency());

        if(currency != CryptoCurrency.getType(authInfo.getCurrencyType()))
        {
            return false;
        }

//        if(currency != CryptoCurrency.BUSD)
//        {
//            return true;
//        }

        UserInfo userInfo = mUserService.findByUsername(false, model.getUsername());
        UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());
        if(userType == UserInfo.UserType.TEST)
        {
            ICurrencyType currencyType = CryptoCurrency.getType(model.getQuoteCurrency());
            UserMoney userMoney = moneyService.findMoney(false, userInfo.getId(), FundAccountType.Spot, currencyType);
            if(userMoney.getBalance().compareTo(BigDecimal.ZERO) > 0)
            {
                BigDecimal newBalance = userMoney.getBalance().add(authInfo.getBalance());
                authInfo.setBalance(newBalance);

            }
        }

        BigDecimal rewardAmount = authInfo.getBalance().multiply(expectRate).setScale(6, RoundingMode.DOWN);

        StakingSettleMode vourcherNodeSettleMode = StakingSettleMode.getType(model.getVoucherNodeSettleMode());
        BigDecimal voucherRewardAmount = BigDecimal.ZERO;
        if(model.getVoucherNodeValue() != null && model.getVoucherNodeValue().compareTo(BigDecimal.ZERO) > 0)
        {
            //
            voucherRewardAmount = model.getVoucherNodeValue().multiply(expectRate).setScale(6, RoundingMode.DOWN);
            if(vourcherNodeSettleMode == StakingSettleMode.BALANCE)
            {
                rewardAmount = rewardAmount.add(voucherRewardAmount);
            }
        }

        rewardAmount = BigDecimalUtils.getNotNull(rewardAmount);
        if(rewardAmount.compareTo(model.getMinWalletBalance()) <= 0 && voucherRewardAmount.compareTo(model.getMinWalletBalance()) <= 0)
        {
            return false;
        }

        CacheManager.getInstance().setString(cachekey, "1", CacheManager.EXPIRES_DAY);

        UserAttr userAttr = mUserAttrService.find(false, authInfo.getUserid());


        FundAccountType accountType = FundAccountType.Spot;
        try {
            if(rewardAmount != null && rewardAmount.compareTo(BigDecimal.ZERO) > 0)
            {

                //LOG.info("record info : " + FastJsonHelper.jsonEncode(model));
                String orderno = miningOrderService.addOrder("DeFi-Balance", authInfo, userAttr, networkType, currency, MiningOrderInfo.OrderType.REWARD, rewardAmount, null);

                ErrorResult result = mPayApiManager.doBusinessRecharge(accountType, currency, BusinessType.COIN_DEFI_MINING_REWARD_ORDER, orderno, userInfo, rewardAmount, null);
//                ErrorResult result = SystemErrorResult.SUCCESS;
                if(result == SystemErrorResult.SUCCESS)
                {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("stakingRate", expectRate);

                    if(vourcherNodeSettleMode == StakingSettleMode.BALANCE)
                    {
                        jsonObject.put("voucherRewardAmount", voucherRewardAmount);
                    }
                    miningOrderService.updateInfo(orderno, OrderTxStatus.REALIZED, null, jsonObject);

                    // 统计历史收益
                    BigDecimal newTotalRewardAmount = model.getTotalRewardAmount().add(rewardAmount);
                    miningRecordService.updateTotalRewardAmount(model.getId(), newTotalRewardAmount);

                    //返佣
                    mReturnWaterManager.doReturnWater(currency, ReturnWaterType.COIN_DEFI, userInfo, orderno, rewardAmount);
                    return true;
                }
                else
                {
                    miningOrderService.updateInfo(orderno, OrderTxStatus.FAILED, null, null);
                    return false;
                }
            }

            return true;
        } finally {
            if(vourcherNodeSettleMode == StakingSettleMode.DEF && voucherRewardAmount.compareTo(BigDecimal.ZERO) > 0)
            {
                String orderno = miningOrderService.addOrder("DeFi-Voucher", authInfo, userAttr, networkType, currency, MiningOrderInfo.OrderType.VOUCHER_NODE_NOT_SETTLE, voucherRewardAmount, null);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("stakingRate", expectRate);
                jsonObject.put("voucherRewardAmount", voucherRewardAmount);
                miningOrderService.updateInfo(orderno, OrderTxStatus.REALIZED, null, jsonObject);

                // 统计历史收益
                BigDecimal newTotalRewardAmount = model.getTotalRewardAmount().add(voucherRewardAmount);
                miningRecordService.updateTotalRewardAmount(model.getId(), newTotalRewardAmount);
            }
        }
    }

    public boolean settleTierMining(boolean isDebug, Map<String, List<ProfitConfigInfo>> profitConfigMaps, UserInfo agentInfo, MiningRecordInfo model, ApproveAuthInfo authInfo, String cachekey)
    {
        UserAttr userAttr = mUserAttrService.find(false, model.getUserid());
        BigDecimal walletBalance = authInfo.getBalance().add(BigDecimalUtils.getNotNull(model.getVoucherNodeValue()));
        ProfitConfigInfo config = loadTierConfig(profitConfigMaps, walletBalance, userAttr.getAgentid(), model);
        if(config == null)
        {
            config = loadTierConfig(profitConfigMaps, walletBalance, agentInfo.getId(), model);
        }
        if(config == null || config.getDailyRate().compareTo(BigDecimal.ZERO) <= 0)
        {
            return false;
        }

        settleDefaultMining(isDebug, model, authInfo, cachekey, config.getDailyRate());
        return true;
    }

    private ProfitConfigInfo loadTierConfig(Map<String, List<ProfitConfigInfo>> maps, BigDecimal walletAmount, long agentid, MiningRecordInfo recordInfo)
    {
        String key = agentid + recordInfo.getQuoteCurrency();
        List<ProfitConfigInfo> rsList = maps.get(key);
        if(rsList == null)
        {
            CryptoCurrency currencyType = CryptoCurrency.getType(recordInfo.getQuoteCurrency());
            rsList = mProfitConfigService.queryAllList(false, agentid, ProfitConfigInfo.ProfitType.COIN_DEFI_TIER, currencyType);
            maps.put(key, rsList);
        }

        if(CollectionUtils.isEmpty(rsList))
        {
            return null;
        }

        int size = rsList.size();
        for(int i = size -1; i >= 0; i --)
        {
            ProfitConfigInfo model = rsList.get(i);
            if(walletAmount.compareTo(model.getMinAmount()) >= 0)
            {
                return model;
            }
        }
        return null;
    }

    private static void updateSettleStatus(boolean isProcessing)
    {
        DateTime todayTime = new DateTime();
        int dayOfYear = todayTime.getDayOfYear();
        String cachekey = SETTL_RUNNING_E_STATUS_CACHE_KEY + dayOfYear;
        CacheManager.getInstance().setString(cachekey, isProcessing + StringUtils.getEmpty());
    }

    public static boolean isProcessingSettle()
    {
        DateTime todayTime = new DateTime();
        int dayOfYear = todayTime.getDayOfYear();
        String cachekey = SETTL_RUNNING_E_STATUS_CACHE_KEY + dayOfYear;
        String value = CacheManager.getInstance().getString(cachekey);
        return StringUtils.asBoolean(value);
    }

    public void test()
    {
        bgTask(true);
        LOG.info("Finish ==========");
    }

    private void test2()
    {
        try {


            miningRecordService.queryAll(new Callback<MiningRecordInfo>() {
                @Override
                public void execute(MiningRecordInfo model) {
                    if(!username.equalsIgnoreCase(model.getUsername()))
                    {
                        return;
                    }

                    long userid = model.getUserid();
                    long contractid = model.getContractid();

                    ApproveAuthInfo authInfo = mApproveAuthService.findByUseridAndContractId(true, userid, contractid);
                    if(authInfo.getCurrencyType().equalsIgnoreCase(authInfo.getCurrencyType()))
                    {
                        return;
                    }

                    StringBuilder buffer  = new StringBuilder();
                    buffer.append("username = ").append(model.getUsername());
                    buffer.append(", record-currency = ").append(model.getQuoteCurrency()).append(", record-contractid").append(model.getContractid());
                    buffer.append(", auth-currency = ").append(authInfo.getCurrencyType()).append(", auth-contractid").append(authInfo.getId());

                    miningRecordService.deleteByid(model);
                }
            });
        } catch (Exception e) {
            LOG.error("handle error:", e);
        }

        LOG.info("Finish ==========");
    }

    public static void main(String[] args) {
        BigDecimal a = new BigDecimal("0.00000006");
        a = a.setScale(6, RoundingMode.HALF_UP);

        System.out.println(a);
    }

}
