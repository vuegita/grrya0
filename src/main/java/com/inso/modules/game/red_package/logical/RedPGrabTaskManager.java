package com.inso.modules.game.red_package.logical;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.modules.common.model.BusinessType;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.game.red_package.helper.RedPOrderIdHelper;
import com.inso.modules.game.red_package.model.RedPType;
import com.inso.modules.game.red_package.service.RedPReceivOrderService;
import com.inso.modules.passport.money.PayApiManager;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 抢红包任务管理器
 */
@Component
public class RedPGrabTaskManager {

    private static Log LOG = LogFactory.getLog(RedPGrabTaskManager.class);

    @Autowired
    private UserAttrService mAttrService;

    @Autowired
    private RedPReceivOrderService mReceivOrderService;

    @Autowired
    private PayApiManager mPayApiMgr;

    private static final int DEFAULT_CAPACITY = 100;
    private AtomicInteger count = new AtomicInteger();
//    private BlockingQueue<BetItemTask> queue = new ArrayBlockingQueue(DEFAULT_CAPACITY);

//    private ExecutorService mThreadPool = Executors.newFixedThreadPool(30);

    private boolean stop = false;

    public RedPGrabTaskManager()
    {
//        new Thread(new Runnable() {
//            public void run() {
//
//                while (!stop)
//                {
//                    try {
//                        BetItemTask itemInfo = queue.take();
//                        mThreadPool.execute(itemInfo);
//                    } catch (Exception e) {
//                    }
//                }
//
//            }
//        }).start();
    }

    public boolean isFull()
    {
        return count.get() >= DEFAULT_CAPACITY;
    }

    /**
     *
     * @param type
     * @param issue 红包id
     * @param userInfo
     * @return
     */
//    public boolean addItemToQueue(RedPType type, long issue, String redpKey, UserInfo userInfo)
//    {
//        if(isFull())
//        {
//            return false;
//        }
//        BetItemTask betItemTask = new BetItemTask();
//        betItemTask.setType(type);
//        betItemTask.setIssue(issue);
//        betItemTask.setRedpKey(redpKey);
//        betItemTask.setUserInfo(userInfo);
//
//        if(queue.add(betItemTask))
//        {
//            count.incrementAndGet();
//            return true;
//        }
//        return false;
//    }

    public ErrorResult doCreateOrder(ApiJsonTemplate apiJsonTemplate, RedPType type, long issue, String redpKey, UserInfo userInfo)
    {
        try {
            RedPGrabStatus runningStatus = RedPGrabStatus.loadCache(false, issue);

            ErrorResult verifyResult = runningStatus.verify(userInfo.getName());
            if(verifyResult != SystemErrorResult.SUCCESS)
            {
                return verifyResult;
            }

            String lockId = type.getKey() + issue;
            synchronized (lockId)
            {
                // 执行抢红包动作- 不为空表示抢到红包
                BigDecimal grabAmount = runningStatus.decreRPAmount(userInfo.getName());
                if(grabAmount == null)
                {
                    return SystemErrorResult.ERR_SYS_OPT_FAILURE;
                }

                UserAttr userAttr = mAttrService.find(false, userInfo.getId());

                String orderno = RedPOrderIdHelper.nextOrderId(type);

                // create order
                mReceivOrderService.addOrder(orderno, issue, type, userInfo, userAttr, grabAmount, runningStatus.getCurrentRPCount(), null);

                BusinessType businessType = BusinessType.GAME_RED_PACKAGE;
                if(type == RedPType.SOLIDCODE)
                {
                    businessType = BusinessType.GAME_RED_PACKAGE_NO_CODE;
                }

                ErrorResult result = SystemErrorResult.SUCCESS;
                if(type.isDirectPresentAmount())
                {
                    // 是否直接赠送金额-平台赠送-需要打码
                    FundAccountType accountType = FundAccountType.Spot;
                    ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
                    result = mPayApiMgr.doPlatformPresentation(accountType, currencyType, businessType, orderno, userInfo, grabAmount, null);
                }

                if(result == SystemErrorResult.SUCCESS)
                {
                    // 更新订单状态
                    mReceivOrderService.updateTxStatus(orderno, OrderTxStatus.REALIZED, null);

                    // 更新缓存
                    mReceivOrderService.clearUserCache(userInfo.getId(), type);

                    // 充值活动红包
                    if(type == RedPType.RECHARGE)
                    {
//                        RechargePresentStatus presentStatus = new RechargePresentStatus();
//                        presentStatus.setPresentAmount(grabAmount);
//                        presentStatus.setLimitMixAmount(runningStatus.getExternalLimitMinAmount());
//                        presentStatus.setType(RechargePresentType.RedP);
//                        presentStatus.setRemark("redp present, repid = " + issue + ", redp-order = " + orderno);
//                        RechargeActionHelper.saveAmount(RechargePresentType.RedP, userInfo.getName(), redpKey, presentStatus);
                    }

                    apiJsonTemplate.setData(grabAmount);
                }

                runningStatus.saveCache();
                return result;
            }


        } catch (Exception e) {
            LOG.error("doCreateReceivOrder error: ", e);
        }
        return SystemErrorResult.ERR_SYS_OPT_FAILURE;
    }

//    private class BetItemTask implements Runnable{
//        private RedPType type;
//        private long issue;
//        private String redpKey;
//        private UserInfo userInfo;
//
//        public RedPType getType() {
//            return type;
//        }
//
//        public void setType(RedPType type) {
//            this.type = type;
//        }
//
//
//        @Override
//        public void run() {
//            doCreateOrder(type, issue, redpKey, userInfo);
//            count.decrementAndGet();
//        }
//
//        public UserInfo getUserInfo() {
//            return userInfo;
//        }
//
//        public void setUserInfo(UserInfo userInfo) {
//            this.userInfo = userInfo;
//        }
//
//        public long getIssue() {
//            return issue;
//        }
//
//        public void setIssue(long issue) {
//            this.issue = issue;
//        }
//
//        public String getRedpKey() {
//            return redpKey;
//        }
//
//        public void setRedpKey(String redpKey) {
//            this.redpKey = redpKey;
//        }
//    }

}
