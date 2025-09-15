package com.inso.modules.coin.cloud_mining.job;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
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
import com.inso.modules.coin.cloud_mining.model.CloudProfitConfigInfo;
import com.inso.modules.coin.cloud_mining.model.CloudRecordInfo;
import com.inso.modules.coin.cloud_mining.service.CloudOrderService;
import com.inso.modules.coin.cloud_mining.service.CloudProfitConfigService;
import com.inso.modules.coin.cloud_mining.service.CloudRecordService;
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
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

public class SettleCloudSolidRecordJob implements Job {

    private static Log LOG = LogFactory.getLog(SettleCloudSolidRecordJob.class);

    private static final String ROOT_CACHE = SettleCloudSolidRecordJob.class.getName();

    public static final String TYPE_REWARD = ROOT_CACHE + "reward";
    public static final String TYPE_ALL = ROOT_CACHE + "all";


    private CloudRecordService recordService;
    private CloudOrderService orderService;

    private UserService mUserService;
    private UserAttrService mUserAttrService;

    private ConfigService mConfigService;

    private PayApiManager mPayApiManager;

    private ReturnWaterManager mReturnWaterManager;

    private CloudProfitConfigService mProfitConfigService;

    public SettleCloudSolidRecordJob()
    {
        this.mUserService = SpringContextUtils.getBean(UserService.class);
        this.mUserAttrService = SpringContextUtils.getBean(UserAttrService.class);
        this.recordService = SpringContextUtils.getBean(CloudRecordService.class);
        this.orderService = SpringContextUtils.getBean(CloudOrderService.class);

        this.mConfigService = SpringContextUtils.getBean(ConfigService.class);
        this.mPayApiManager = SpringContextUtils.getBean(PayApiManager.class);

        this.mReturnWaterManager = SpringContextUtils.getBean(ReturnWaterManager.class);

        this.mProfitConfigService = SpringContextUtils.getBean(CloudProfitConfigService.class);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        if(!(SystemRunningMode.isFundsMode() || MyEnvironment.isDev()))
        {
            return;
        }

        String type = context.getJobDetail().getJobDataMap().getString("type");
        if(TYPE_REWARD.equalsIgnoreCase(type))
        {
            doRewardTask(null, false);
        }
        else if(TYPE_ALL.equalsIgnoreCase(type))
        {
            doSettleAllTask(false);
        }
    }

    public void testStart()
    {
        doRewardTask("up98123456", false);
//        doSettleAllTask(true);
    }

    private void doRewardTask(String onlySettleUsername, boolean isTest)
    {
        try {
            Map<Long, List<CloudProfitConfigInfo>> profitConfigMaps = Maps.newHashMap();

            long currentTime = System.currentTimeMillis();
            recordService.queryAll(new Callback<CloudRecordInfo>() {
                @Override
                public void execute(CloudRecordInfo model) {
                    if(isTest)
                    {
                        if(!StringUtils.isEmpty(onlySettleUsername) && !onlySettleUsername.equalsIgnoreCase(model.getUsername()))
                        {
                            return;
                        }
                    }

                    Status status = Status.getType(model.getStatus());
                    if(status != Status.ENABLE)
                    {
                        return;
                    }

                    if(!isTest)
                    {
                        if(currentTime > model.getEndtime().getTime())
                        {
                            return;
                        }
                    }

                    CloudProductType productType = CloudProductType.getType(model.getProductType());
                    if(productType != CloudProductType.COIN_CLOUD_SOLID)
                    {
                        return;
                    }
                    handleSettle(model, profitConfigMaps);
                }
            });
        } catch (Exception e) {
            LOG.error("handle error:", e);
        }
    }

    private void doSettleAllTask(boolean isTest)
    {
        try {

            long nowTime = System.currentTimeMillis();

            recordService.queryAll(new Callback<CloudRecordInfo>() {
                @Override
                public void execute(CloudRecordInfo model) {

                    try {
                        Status status = Status.getType(model.getStatus());
                        if(status != Status.ENABLE)
                        {
                            return;
                        }

                        CloudProductType productType = CloudProductType.getType(model.getProductType());
                        if(productType != CloudProductType.COIN_CLOUD_SOLID)
                        {
                            return;
                        }

                        if(!
                                isTest)
                        {
                            if(nowTime < model.getEndtime().getTime())
                            {
                                return;
                            }
                        }


                        if(model.getInvesTotalAmount() == null || model.getInvesTotalAmount().compareTo(BigDecimal.ZERO) <= 0)
                        {
                            return;
                        }

                        FundAccountType accountType = FundAccountType.Spot;
                        ICurrencyType currency = ICurrencyType.getType(model.getCurrencyType());
                        UserInfo userInfo = mUserService.findByUsername(false, model.getUsername());
                        UserAttr userAttr = mUserAttrService.find(false, model.getUserid());

                        BigDecimal totalAmount = model.getInvesTotalAmount();

                        String orderno = orderService.addOrder(userAttr, productType, currency, CloudOrderInfo.OrderType.REWARD, totalAmount, null, null);
                        recordService.settleSolidMining(model, orderno);
                        ErrorResult result = mPayApiManager.doBusinessRecharge(accountType, currency, BusinessType.COIN_CLOUD_MINING_REWARD_ORDER, orderno, userInfo, totalAmount, null);
                        if(result == SystemErrorResult.SUCCESS)
                        {
                            orderService.updateInfo(orderno, OrderTxStatus.REALIZED, null, null);
                        }
                        else
                        {
//                            orderService.updateInfo(orderno, OrderTxStatus.FAILED, null, null);
                        }
                    } catch (Exception e) {
                        LOG.error("handle error:", e);
                    }
                }
            });
        } catch (Exception e) {
            LOG.error("handle error:", e);
        }
    }

    private void handleSettle(CloudRecordInfo model, Map<Long, List<CloudProfitConfigInfo>> profitConfigMaps)
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

            //LOG.info("start settle record info : " + FastJsonHelper.jsonEncode(model));
            UserAttr userAttr = mUserAttrService.find(false, model.getUserid());
            CloudProfitConfigInfo config = loadConfig(profitConfigMaps, model.getInvesTotalAmount(), model);
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

    private CloudProfitConfigInfo loadConfig(Map<Long, List<CloudProfitConfigInfo>> maps, BigDecimal walletAmount, CloudRecordInfo recordInfo)
    {
        long key = recordInfo.getDays();
        List<CloudProfitConfigInfo> rsList = maps.get(key);
        if(rsList == null)
        {
            rsList = mProfitConfigService.queryAllListByDays(key);
            maps.put(key, rsList);
        }

        if(CollectionUtils.isEmpty(rsList))
        {
            return null;
        }

        CloudProfitConfigInfo validModel = null;
        for(CloudProfitConfigInfo model : rsList)
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

    public static void main(String[] args) {
        BigDecimal a = new BigDecimal("0.00000006");
        a = a.setScale(6, RoundingMode.HALF_UP);

        System.out.println(a);
    }

}
