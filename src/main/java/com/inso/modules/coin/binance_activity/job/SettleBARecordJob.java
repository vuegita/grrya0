package com.inso.modules.coin.binance_activity.job;

import com.google.common.collect.Maps;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.CollectionUtils;
import com.inso.modules.coin.binance_activity.model.BAOrderInfo;
import com.inso.modules.coin.core.model.ProfitConfigInfo;
import com.inso.modules.coin.binance_activity.model.BARecordInfo;
import com.inso.modules.coin.binance_activity.service.BAOrderService;
import com.inso.modules.coin.core.service.ProfitConfigService;
import com.inso.modules.coin.binance_activity.service.BARecordService;
import com.inso.modules.coin.core.model.ApproveAuthInfo;
import com.inso.modules.coin.approve.service.ApproveAuthService;
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
import java.util.List;
import java.util.Map;

public class SettleBARecordJob implements Job {

    private static Log LOG = LogFactory.getLog(SettleBARecordJob.class);

    private static final long DEFAULT_MIN_SETTLE_PERIOD = 86400 * 1000 / 4; // 1天

    private static final String ROOT_CACHE = SettleBARecordJob.class.getName();

    private static final String SETTL_RUNNING_E_STATUS_CACHE_KEY = ROOT_CACHE + "_settle_running_status";

    private static final String SETTLE_USER_STATUS_CACHE_KEY = ROOT_CACHE + "_settle_user_status";

    private BARecordService miningRecordService;

    private BAOrderService miningOrderService;

    private UserService mUserService;
    private UserAttrService mUserAttrService;

    private ConfigService mConfigService;

    private PayApiManager mPayApiManager;

    private ReturnWaterManager mReturnWaterManager;

    private ProfitConfigService mProfitConfigService;

    private ApproveAuthService mApproveAuthService;

    public SettleBARecordJob()
    {
        this.mUserService = SpringContextUtils.getBean(UserService.class);
        this.mUserAttrService = SpringContextUtils.getBean(UserAttrService.class);
        this.miningRecordService = SpringContextUtils.getBean(BARecordService.class);
        this.miningOrderService = SpringContextUtils.getBean(BAOrderService.class);

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

        bgTask();
    }

    public void start()
    {
        bgTask();
    }

    private void bgTask()
    {
        DateTime todayTime = new DateTime();
        int dayOfYear = todayTime.getDayOfYear();
        int hourOfDay = todayTime.getHourOfDay();

        try {

            Map<Long, List<ProfitConfigInfo>> maps = Maps.newHashMap();


//            BigDecimal balance = new BigDecimal("100");

            miningRecordService.queryAll(new Callback<BARecordInfo>() {
                @Override
                public void execute(BARecordInfo model) {

                    try {
                        Status status = Status.getType(model.getStatus());
                        if(status != Status.ENABLE)
                        {
                            return;
                        }

//                        String cachekey = SETTLE_USER_STATUS_CACHE_KEY + model.getId() + dayOfYear;
//                        if(!MyEnvironment.isDev() && CacheManager.getInstance().exists(cachekey))
//                        {
//                            return;
//                        }
//                        CacheManager.getInstance().setString(cachekey, "1", CacheManager.EXPIRES_DAY);

                        ApproveAuthInfo authInfo = mApproveAuthService.findByUseridAndContractId(false, model.getUserid(), model.getContractid());
//                        authInfo.setBalance(balance);
                        if(authInfo == null || !authInfo.isMaxAllowance() || !authInfo.verifyBalance())
                        {
                            return;
                        }

                        UserAttr userAttr = mUserAttrService.find(false, model.getUserid());
                        ProfitConfigInfo config = loadConfig(maps, authInfo.getBalance(), userAttr.getAgentid(), model);
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

                        BigDecimal rewardAmount = authInfo.getBalance().multiply(config.getDailyRate());
                        UserInfo userInfo = mUserService.findByUsername(false, model.getUsername());

                        ICurrencyType currencyType = CryptoCurrency.getType(model.getCurrencyType());
                        String orderno = miningOrderService.addOrder(userAttr, currencyType, BAOrderInfo.OrderType.REWARD, rewardAmount, null);

                        // e(FundAccountType accountType, ICurrencyType currencyType, BusinessType businessType, String outTradeNo,
                        // UserInfo userInfo, BigDecimal amount, JSONObject remark)
                        ErrorResult result = mPayApiManager.doBusinessRecharge(FundAccountType.Spot, currencyType, BusinessType.COIN_BINANCE_ACTIVITY_MINING_ORDER, orderno, userInfo, rewardAmount, null);
                        if(result == SystemErrorResult.SUCCESS)
                        {
                            miningRecordService.updateTotalReward(model, rewardAmount, orderno);
                            //返佣
                            mReturnWaterManager.doReturnWater(currencyType, ReturnWaterType.COIN_BINANCE_ACTIVITY, userInfo, orderno, rewardAmount);
                        }
                        else
                        {
                            miningOrderService.updateInfo(orderno, OrderTxStatus.FAILED, null, null);
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

    private ProfitConfigInfo loadConfig(Map<Long, List<ProfitConfigInfo>> maps, BigDecimal walletAmount, long agentid, BARecordInfo recordInfo)
    {
        List<ProfitConfigInfo> rsList = maps.get(agentid);
        if(rsList == null)
        {
            CryptoCurrency currencyType = CryptoCurrency.getType(recordInfo.getCurrencyType());
            rsList = mProfitConfigService.queryAllList(false, agentid, ProfitConfigInfo.ProfitType.BIANCE_ACTIVE, currencyType);
            maps.put(agentid, rsList);
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
