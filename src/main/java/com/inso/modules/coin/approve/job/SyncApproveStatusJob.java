package com.inso.modules.coin.approve.job;

import com.inso.framework.cache.CacheManager;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.modules.coin.contract.ApproveTokenManager;
import com.inso.modules.coin.contract.Token20Manager;
import com.inso.modules.coin.core.logical.SyncMutisignStatusManager;
import com.inso.modules.coin.core.model.ApproveAuthInfo;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.approve.service.ApproveAuthService;
import com.inso.modules.coin.defi_mining.logical.MiningProductManager;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.SystemRunningMode;
import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 同步状态
 * 余额 |　
 */
public class SyncApproveStatusJob implements Job {

    private static final String TMP_SYNCALL_CACHE_KEY = SyncApproveStatusJob.class.getName() + "sync_all_temp";

    private static Log LOG = LogFactory.getLog(SyncApproveStatusJob.class);

    private UserService mUserService;
    private UserAttrService userAttrService;
    private ApproveAuthService mApproveAuthService;

    private MiningProductManager mDeFiMiningProductMgr;
    private SyncMutisignStatusManager mSyncMutisignStatusManager;

    public static final String TYPE_SYNC_MINUTES_1 = "sync_m1";
    public static final String TYPE_HOUR = "sync_hour";
    public static final String TYPE_SYNC_ALL = "sync_all";

    private static boolean isRunning = false;


    private long mCheckExpires = 86400 * 1000 * 30;

    private static ExecutorService mThreadPool = Executors.newFixedThreadPool(5);

    public SyncApproveStatusJob()
    {
        this.mUserService = SpringContextUtils.getBean(UserService.class);
        this.userAttrService = SpringContextUtils.getBean(UserAttrService.class);
        this.mApproveAuthService = SpringContextUtils.getBean(ApproveAuthService.class);
        this.mDeFiMiningProductMgr = SpringContextUtils.getBean(MiningProductManager.class);
        this.mSyncMutisignStatusManager = SpringContextUtils.getBean(SyncMutisignStatusManager.class);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        DateTime toTime = new DateTime(context.getFireTime());

        String type = context.getJobDetail().getJobDataMap().getString("type");
        if(TYPE_SYNC_ALL.equalsIgnoreCase(type))
        {
            isRunning = true;
            DateTime fromTime = toTime.minusMonths(6);
            doVerifyTask(fromTime, toTime, true, false);
//            doVerifyDeFimining();
            mSyncMutisignStatusManager.start();
            isRunning = false;
        }

        if(isRunning)
        {
            return;
        }
        isRunning = true;

        try {

            if(TYPE_SYNC_MINUTES_1.equalsIgnoreCase(type))
            {

                DateTime fromTime = toTime.minusMinutes(1);
                //doVerifyTask(fromTime, toTime, false);

                toTime = toTime.minusMinutes(1);
                fromTime = toTime.minusMinutes(2);
                doVerifyTask(fromTime, toTime, false, true);

                toTime = toTime.minusMinutes(10);
                fromTime = toTime.minusMinutes(11);
                doVerifyTask(fromTime, toTime, false, true);

                toTime = toTime.minusMinutes(30);
                fromTime = toTime.minusMinutes(31);
                doVerifyTask(fromTime, toTime, false, true);

//                toTime = toTime.minusMinutes(65);
//                fromTime = toTime.minusMinutes(66);
//                doVerifyTask(fromTime, toTime, true, true);

//
//                toTime = toTime.minusMinutes(300);
//                fromTime = toTime.minusMinutes(301);
//                doVerifyTask(fromTime, toTime, false);
//
//                toTime = toTime.minusMinutes(600);
//                fromTime = toTime.minusMinutes(601);
//                doVerifyTask(fromTime, toTime, false);
            }
//            else if(TYPE_HOUR.equalsIgnoreCase(type))
//            {
//                DateTime toTime = new DateTime().minusDays(1);
//                DateTime fromTime = toTime.minusDays(3);
//                doVerifyTask(fromTime, toTime, false);
//
//                toTime = toTime.minusDays(10);
//                fromTime = toTime.minusDays(11);
//                doVerifyTask(fromTime, toTime, false);
//            }
        } catch (Exception e) {
        }

        isRunning = false;
        //testAdd();
    }

    public static boolean syncAll()
    {
        if(CacheManager.getInstance().exists(TMP_SYNCALL_CACHE_KEY))
        {
            return false;
        }

        CacheManager.getInstance().setString(TMP_SYNCALL_CACHE_KEY, "1", CacheManager.EXPIRES_HOUR);

        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                SyncApproveStatusJob job = new SyncApproveStatusJob();
                DateTime toTime = new DateTime();
                DateTime fromTime = toTime.minusDays(366);
                job.doVerifyTask(fromTime, toTime, true, false);

                // 处理
                //job.doVerifyDeFimining();
            }
        });

        return true;

    }

    private void doVerifyTask(DateTime fromTime, DateTime toTime, boolean isSyncAllowance, boolean verifyStaking)
    {
        if(!ApproveTokenManager.getInstance().isInit())
        {
            return;
        }

        try {
            mApproveAuthService.queryAll(new Callback<ApproveAuthInfo>() {
                @Override
                public void execute(ApproveAuthInfo model) {
                    try {
                        // 如果授权过了 直接return 不在验证
                        if(model.getAllowance() != null && model.getAllowance().compareTo(ApproveAuthInfo.DEFAULT_MAX_ALLOWANCE) >= 0)
                        {
                            if(!isSyncAllowance)
                            {
                                return;
                            }
                        }

                        handleOrder(model);
                    } finally {
                        if(verifyStaking && SystemRunningMode.isCryptoMode())
                        {
                            mDeFiMiningProductMgr.verifyDeFiMining(model);
                        }
                    }
                }
            }, fromTime, toTime);
        } catch (Exception e) {
            LOG.error("handle error:", e);
        }
    }

    private void handleOrder(ApproveAuthInfo model)
    {
        try {
            UserInfo userInfo = mUserService.findByUsername(false, model.getUsername());
            if(userInfo == null)
            {
                return;
            }
            UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());
            if(userType == UserInfo.UserType.TEST)
            {
                return;
            }
            CryptoCurrency currency = CryptoCurrency.getType(model.getCurrencyType());
            if(currency == null)
            {
                return;
            }
            String senderAddress = model.getSenderAddress();
            CryptoNetworkType networkType = CryptoNetworkType.getType(model.getCtrNetworkType());

            ApproveTokenManager mgr = ApproveTokenManager.getInstance();
            BigDecimal balance = mgr.balanceOf(model.getCtrAddress(), networkType, currency, senderAddress);
            BigDecimal allowance = mgr.allowance(model.getCtrAddress(), networkType, currency, senderAddress, model.getApproveAddress());

            int approveCount = -1;
            if( System.currentTimeMillis() - model.getCreatetime().getTime() <= mCheckExpires)
            {
                approveCount = Token20Manager.getInstance().getApproveCount(senderAddress, networkType, currency);
            }

            mApproveAuthService.updateInfo(model, balance, allowance, null, null, approveCount);
            if(allowance != null)
            {
                model.setAllowance(allowance);
            }

        } catch (Exception e) {
            LOG.error("handle error: ", e);
        }
    }

    public void test()
    {
        try {
            DateTime toTime = new DateTime();
            DateTime fromTime = toTime.minusDays(100000000);
            doVerifyTask(fromTime, toTime, true, true);

        } catch (Exception e) {
            LOG.error("handle error:", e);
        }

    }

}
