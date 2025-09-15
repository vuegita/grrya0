package com.inso.modules.coin.defi_mining.job;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.CollectionUtils;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.core.model.ProfitConfigInfo;
import com.inso.modules.coin.core.model.StakingSettleMode;
import com.inso.modules.coin.approve.service.ApproveAuthService;
import com.inso.modules.coin.core.service.ProfitConfigService;
import com.inso.modules.coin.defi_mining.model.MiningOrderInfo;
import com.inso.modules.coin.defi_mining.model.MiningRecordInfo;
import com.inso.modules.coin.defi_mining.service.MiningOrderService;
import com.inso.modules.coin.defi_mining.service.MiningRecordService;
import com.inso.modules.common.model.*;
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

public class SettleStakingRecordJob implements Job {

    private static Log LOG = LogFactory.getLog(SettleStakingRecordJob.class);

    private static final long DEFAULT_MIN_SETTLE_PERIOD = 86400 * 1000 / 4; // 1天

    private static final String ROOT_CACHE = SettleStakingRecordJob.class.getName();

    private static final String SETTL_RUNNING_E_STATUS_CACHE_KEY = ROOT_CACHE + "_settle_running_status";

    private static final String SETTLE_USER_STATUS_CACHE_KEY = ROOT_CACHE + "_settle_user_status";

    private MiningRecordService miningRecordService;

    private MiningOrderService miningOrderService;

    private UserService mUserService;
    private UserAttrService mUserAttrService;

    private ConfigService mConfigService;

    private PayApiManager mPayApiManager;

    private ReturnWaterManager mReturnWaterManager;

    private ProfitConfigService mProfitConfigService;

    private ApproveAuthService mApproveAuthService;

    public SettleStakingRecordJob()
    {
        this.mUserService = SpringContextUtils.getBean(UserService.class);
        this.mUserAttrService = SpringContextUtils.getBean(UserAttrService.class);
        this.miningRecordService = SpringContextUtils.getBean(MiningRecordService.class);
        this.miningOrderService = SpringContextUtils.getBean(MiningOrderService.class);

        this.mConfigService = SpringContextUtils.getBean(ConfigService.class);
        this.mPayApiManager = SpringContextUtils.getBean(PayApiManager.class);

        this.mReturnWaterManager = SpringContextUtils.getBean(ReturnWaterManager.class);
        this.mProfitConfigService = SpringContextUtils.getBean(ProfitConfigService.class);
        this.mApproveAuthService = SpringContextUtils.getBean(ApproveAuthService.class);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        if(!SystemRunningMode.isCryptoMode())
        {
            return;
        }

        bgTask(false);
    }

    public void testStart()
    {
        bgTask(true);
    }

    private void bgTask(boolean isTest)
    {
        try {

            Map<String, List<ProfitConfigInfo>> maps = Maps.newHashMap();


//            BigDecimal balance = new BigDecimal("100");

            miningRecordService.queryAll(new Callback<MiningRecordInfo>() {
                @Override
                public void execute(MiningRecordInfo model) {

                    try {
                        Status status = Status.getType(model.getStatus());
                        if(status != Status.ENABLE)
                        {
                            return;
                        }

                        Status stakingStatus = Status.getType(model.getStakingStatus());
                        if(stakingStatus != Status.ENABLE)
                        {
                            return;
                        }

                        if(model.getStakingRewardHour() <= 0)
                        {
                            return;
                        }

                        BigDecimal stakingAmount = BigDecimalUtils.getNotNull(model.getStakingAmount());
                        BigDecimal voucherAmount = BigDecimalUtils.getNotNull(model.getVoucherStakingValue());
                        if(stakingAmount.compareTo(BigDecimal.ZERO) <= 0 && voucherAmount.compareTo(BigDecimal.ZERO) <= 0)
                        {
                            return;
                        }

                        UserAttr userAttr = mUserAttrService.find(false, model.getUserid());
                        ProfitConfigInfo config = loadConfig(maps, stakingAmount, userAttr.getAgentid(), model);
                        if(config == null)
                        {
                            return;
                        }

                        if(config.getDailyRate().compareTo(BigDecimal.ZERO) <= 0)
                        {
                            return;
                        }

                        if(config.getDailyRate().compareTo(BigDecimalUtils.DEF_1) >= 0)
                        {
                            return;
                        }

                        StakingSettleMode settleMode = StakingSettleMode.getType(model.getStakingSettleMode());

                        BigDecimal rewardAmount = stakingAmount.multiply(config.getDailyRate()).divide(BigDecimalUtils.DEF_4, 6, RoundingMode.DOWN);
                        BigDecimal voucherRewardAmount = BigDecimal.ZERO;
                        if(model.getVoucherStakingValue().compareTo(BigDecimal.ZERO) > 0)
                        {
                            voucherRewardAmount = model.getVoucherStakingValue().multiply(config.getDailyRate()).divide(BigDecimalUtils.DEF_4, 6, RoundingMode.DOWN);
                            rewardAmount = rewardAmount.add(voucherRewardAmount);
                        }
                        if(rewardAmount.compareTo(BigDecimal.ZERO) <= 0)
                        {
                            return;
                        }

                        BigDecimal totalReward = rewardAmount.add(model.getTotalRewardAmount());
                        BigDecimal stakingRewardAmount = rewardAmount.add(model.getStakingRewardValue());

                        if(isTest)
                        {
                            LOG.info("username = " + model.getUsername() + ", rewardAmount = " + rewardAmount + ", stakingRate = " + config.getDailyRate());
                            return;
                        }

                        long stackingHour = model.getStakingRewardHour() - 6;
                        if(stackingHour <= 0)
                        {
                            stackingHour = 0;
                        }


                        CryptoNetworkType networkType = CryptoNetworkType.getType(model.getNetworkType());
                        ICurrencyType currencyType = CryptoCurrency.getType(model.getQuoteCurrency());
                        String orderno = miningOrderService.addOrder(null, null, userAttr, networkType, currencyType, MiningOrderInfo.OrderType.Staking, rewardAmount, null);

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("stakingRate", config.getDailyRate());
                        jsonObject.put("voucherRewardAmount", voucherRewardAmount);
                        miningOrderService.updateInfo(orderno, OrderTxStatus.REALIZED, null, jsonObject);

                        if(settleMode == StakingSettleMode.BALANCE)
                        {
                            UserInfo userInfo = mUserService.findByUsername(false, model.getUsername());

                            ErrorResult result = mPayApiManager.doBusinessRecharge(FundAccountType.Spot, currencyType, BusinessType.COIN_DEFI_MINING_REWARD_ORDER, orderno, userInfo, rewardAmount, null);
                            if(result == SystemErrorResult.SUCCESS)
                            {
                                miningRecordService.updateInfo(model, null,
                                        totalReward, null, null, null, stakingRewardAmount, null, stackingHour,
                                        null, null, null);
                                miningOrderService.updateInfo(orderno, OrderTxStatus.REALIZED, null, null);
                            }
                            else
                            {
                                miningOrderService.updateInfo(orderno, OrderTxStatus.FAILED, null, null);
                            }
                        }
                        else
                        {

                            miningRecordService.updateInfo(model, null,
                                    totalReward, null, null, null, stakingRewardAmount, null, stackingHour,
                                    null, null, null);
                        }
                    } catch (Exception e) {
                        LOG.error("settle error:", e);
                        //miningOrderService.updateInfo(orderno, OrderTxStatus.FAILED, null, null);
                    }
                }
            });
        } catch (Exception e) {
            LOG.error("handle error:", e);
        }
    }

    private ProfitConfigInfo loadConfig(Map<String, List<ProfitConfigInfo>> maps, BigDecimal walletAmount, long agentid, MiningRecordInfo recordInfo)
    {
        CryptoCurrency currencyType = CryptoCurrency.getType(recordInfo.getQuoteCurrency());
        String key = agentid + currencyType.getKey();
        List<ProfitConfigInfo> rsList = maps.get(key);
        if(rsList == null)
        {
            rsList = mProfitConfigService.queryAllList(false, agentid, ProfitConfigInfo.ProfitType.DEFI_STAKING, currencyType);
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


}
