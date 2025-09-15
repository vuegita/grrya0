package com.inso.modules.coin.cloud_mining.job;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.cloud_mining.model.CloudOrderInfo;
import com.inso.modules.coin.cloud_mining.model.CloudProductType;
import com.inso.modules.coin.cloud_mining.model.CloudRecordInfo;
import com.inso.modules.coin.cloud_mining.service.CloudOrderService;
import com.inso.modules.coin.cloud_mining.service.CloudRecordService;
import com.inso.modules.coin.core.model.ProfitConfigInfo;
import com.inso.modules.coin.core.service.ProfitConfigService;
import com.inso.modules.common.model.*;
import com.inso.modules.passport.business.model.ReturnWaterType;
import com.inso.modules.passport.money.PayApiManager;
import com.inso.modules.passport.returnwater.ReturnWaterManager;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
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

/**
 * 活期结算
 */
public class SettleCloudDayRecordJob implements Job {

    private static Log LOG = LogFactory.getLog(SettleCloudDayRecordJob.class);

    private static final long DEFAULT_MIN_SETTLE_PERIOD = 86400 * 1000 / 4; // 1天

    private static final String ROOT_CACHE = SettleCloudDayRecordJob.class.getName();

    private static final String SETTL_RUNNING_E_STATUS_CACHE_KEY = ROOT_CACHE + "_settle_running_status";

    private static final String SETTLE_USER_STATUS_CACHE_KEY = ROOT_CACHE + "_settle_user_status";

    private CloudRecordService recordService;
    private CloudOrderService orderService;

    private UserService mUserService;
    private UserAttrService mUserAttrService;

    private ConfigService mConfigService;

    private PayApiManager mPayApiManager;

    private ReturnWaterManager mReturnWaterManager;

    private ProfitConfigService mProfitConfigService;

    public SettleCloudDayRecordJob()
    {
        this.mUserService = SpringContextUtils.getBean(UserService.class);
        this.mUserAttrService = SpringContextUtils.getBean(UserAttrService.class);
        this.recordService = SpringContextUtils.getBean(CloudRecordService.class);
        this.orderService = SpringContextUtils.getBean(CloudOrderService.class);

        this.mConfigService = SpringContextUtils.getBean(ConfigService.class);
        this.mPayApiManager = SpringContextUtils.getBean(PayApiManager.class);

        this.mReturnWaterManager = SpringContextUtils.getBean(ReturnWaterManager.class);

        this.mProfitConfigService = SpringContextUtils.getBean(ProfitConfigService.class);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        if(!(SystemRunningMode.isFundsMode() || MyEnvironment.isDev()))
        {
            return;
        }
        bgTask(null);
    }

    public void start()
    {
        bgTask("up98123456");
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
    private void bgTask(String onlySettleUsername)
    {
        updateSettleStatus(true);

        DateTime todayTime = new DateTime();
        int dayOfYear = todayTime.getDayOfYear();

        try {
            Map<Long, List<ProfitConfigInfo>> profitConfigMaps = Maps.newHashMap();
            UserInfo agentInfo = mUserService.findByUsername(false, UserInfo.DEFAULT_GAME_SYSTEM_AGENT);

            recordService.queryAll(new Callback<CloudRecordInfo>() {
                @Override
                public void execute(CloudRecordInfo model) {
                    if(!StringUtils.isEmpty(onlySettleUsername) && !onlySettleUsername.equalsIgnoreCase(model.getUsername()))
                    {
                        return;
                    }

                    Status status = Status.getType(model.getStatus());
                    if(status != Status.ENABLE)
                    {
                        return;
                    }

                    CloudProductType productType = CloudProductType.getType(model.getProductType());
                    if(productType != CloudProductType.COIN_CLOUD_ACTIVE)
                    {
                        return;
                    }
                    handleSettle(model, dayOfYear, profitConfigMaps, agentInfo);
                }
            });
        } catch (Exception e) {
            LOG.error("handle error:", e);
        }
        updateSettleStatus(false);
    }

    private void handleSettle(CloudRecordInfo model, int dayOfYear, Map<Long, List<ProfitConfigInfo>> profitConfigMaps, UserInfo agentInfo)
    {
        boolean success = false;
        StringBuffer settleResult = new StringBuffer();
        settleResult.append("username = " + model.getUsername());
        try {
            Status status = Status.getType(model.getStatus());
            if(status != Status.ENABLE)
            {
                settleResult.append("result = false");
                settleResult.append(", errmsg = 状态关闭");
                return;
            }

            UserAttr userAttr = mUserAttrService.find(false, model.getUserid());
            ProfitConfigInfo config = loadConfig(profitConfigMaps, model.getInvesTotalAmount(), agentInfo.getId(), model);
            if(config == null)
            {
                return;
            }

            success = settleDefaultMining(model, config.getDailyRate(), userAttr);
        } catch (Exception e) {
            LOG.error("handleSettle error:", e);
        } finally {
            if(!success)
            {
                LOG.error(settleResult.toString());
            }
        }
    }

    private boolean settleDefaultMining(CloudRecordInfo model, BigDecimal expectRate, UserAttr userAttr)
    {
        CloudProductType productType = CloudProductType.getType(model.getProductType());
        BigDecimal rewardAmount = model.getInvesTotalAmount().multiply(expectRate);
        rewardAmount = rewardAmount.divide(BigDecimalUtils.DEF_4, 2, RoundingMode.HALF_UP);
        if(rewardAmount == null || rewardAmount.compareTo(BigDecimal.ZERO) <= 0)
        {
            return false;
        }

        ICurrencyType currency = CryptoCurrency.getType(model.getCurrencyType());

        UserInfo userInfo = mUserService.findByUsername(false, model.getUsername());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("settleRate", expectRate);
        jsonObject.put("settleSrcAmount", model.getInvesTotalAmount());
        String orderno = orderService.addOrder(userAttr, productType, currency, CloudOrderInfo.OrderType.REWARD, rewardAmount, null, jsonObject);

        //返佣
        FundAccountType accountType = FundAccountType.Spot;
        ErrorResult result = mPayApiManager.doBusinessRecharge(accountType, currency, BusinessType.COIN_CLOUD_MINING_REWARD_ORDER, orderno, userInfo, rewardAmount, null);
        if(result == SystemErrorResult.SUCCESS)
        {
            recordService.updateRewardAmount(model, rewardAmount, orderno);

            //返佣
            mReturnWaterManager.doReturnWater(currency, ReturnWaterType.COIN_CLOUD, userInfo, orderno, rewardAmount);
            return true;
        }
        else
        {
            orderService.updateInfo(orderno, OrderTxStatus.FAILED, null, null);
            return false;
        }
    }

    private ProfitConfigInfo loadConfig(Map<Long, List<ProfitConfigInfo>> maps, BigDecimal walletAmount, long agentid, CloudRecordInfo recordInfo)
    {
        List<ProfitConfigInfo> rsList = maps.get(agentid);
        if(rsList == null)
        {
            CryptoCurrency currencyType = CryptoCurrency.getType(recordInfo.getCurrencyType());
            rsList = mProfitConfigService.queryAllList(false, agentid, ProfitConfigInfo.ProfitType.COIN_CLOUD_ACTIVE, currencyType);
            maps.put(agentid, rsList);
        }

        if(CollectionUtils.isEmpty(rsList))
        {
            return null;
        }

        ProfitConfigInfo validModel = null;
        for(ProfitConfigInfo model : rsList)
        {
            if(walletAmount.compareTo(model.getMinAmount()) >= 0)
            {
                validModel = model;
                continue;
            }
            break;
        }
        return validModel;
    }

    private static void updateSettleStatus(boolean isProcessing)
    {
        DateTime todayTime = new DateTime();
        int dayOfYear = todayTime.getDayOfYear();
        String cachekey = SETTL_RUNNING_E_STATUS_CACHE_KEY + dayOfYear;
        CacheManager.getInstance().setString(cachekey, isProcessing + StringUtils.getEmpty());
    }

    public static void main(String[] args) {
        BigDecimal a = new BigDecimal("0.00000006");
        a = a.setScale(6, RoundingMode.HALF_UP);

        System.out.println(a);
    }

}
