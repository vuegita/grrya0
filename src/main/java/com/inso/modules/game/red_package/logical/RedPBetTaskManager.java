package com.inso.modules.game.red_package.logical;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.inso.modules.common.model.*;
import com.inso.modules.game.MyLotteryBetRecordCache;
import com.inso.modules.passport.business.model.ReturnWaterType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.druid.util.LRUCache;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.red_package.helper.RedPOrderIdHelper;
import com.inso.modules.game.red_package.model.RedPType;
import com.inso.modules.game.red_package.service.RedPBetOrderService;
import com.inso.modules.game.rg.helper.LotteryHelper;
import com.inso.modules.game.rg.logical.RGPeriodStatus;
import com.inso.modules.game.rg.model.LotteryRGType;
import com.inso.modules.passport.money.PayApiManager;
import com.inso.modules.passport.business.RefundManager;
import com.inso.modules.passport.returnwater.ReturnWaterManager;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;

/**
 * 投注任务管理器
 */
@Component
public class RedPBetTaskManager {

    private static Log LOG = LogFactory.getLog(RedPBetTaskManager.class);

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private RedPBetOrderService mLotteryOrderService;

    @Autowired
    private PayApiManager mPayApiMgr;

    @Autowired
    private RefundManager mRefundMgr;

    @Autowired
    private ReturnWaterManager mReturnWaterManager;

    private LRUCache<String, RGPeriodStatus> mLruCache = new LRUCache<>(LotteryRGType.values().length * 2);

    private static final int DEFAULT_CAPACITY = 100;
    private AtomicInteger count = new AtomicInteger();
    private BlockingQueue<BetItemTask> queue = new ArrayBlockingQueue(DEFAULT_CAPACITY);

    private ExecutorService mThreadPool = Executors.newFixedThreadPool(30);

    private boolean stop = false;

    public RedPBetTaskManager()
    {
        new Thread(new Runnable() {
            public void run() {

                while (!stop)
                {
                    try {
                        BetItemTask itemInfo = queue.take();
                        mThreadPool.execute(itemInfo);
                    } catch (Exception e) {
                    }
                }

            }
        }).start();
    }

    public boolean isFull()
    {
        return count.get() >= DEFAULT_CAPACITY;
    }

    public boolean addItemToQueue(RedPType type, long issue, UserInfo userInfo, long amountValue, long betCount, String betItem)
    {
        if(isFull())
        {
            return false;
        }
        BetItemTask betItemTask = new BetItemTask();
        betItemTask.setType(type);
        betItemTask.setIssue(issue);
        betItemTask.setUserInfo(userInfo);
        betItemTask.setAmountValue(amountValue);
        betItemTask.setBetCount(betCount);
        betItemTask.setBetItem(betItem);

        if(queue.add(betItemTask))
        {
            count.incrementAndGet();
            return true;
        }
        return false;
    }

    public ErrorResult doCreateOrder(RedPType type, long issue, UserInfo userInfo, long basicAmountValue, long betCount, String betItem)
    {
        try {
            String key = type.getKey() + issue;
            RedPPeriodStatus runningStatus = RedPPeriodStatus.loadCache(false, type, issue);

            long betTotalAmountValue = basicAmountValue * betCount;
            ErrorResult verifyResult = runningStatus.verify(userInfo.getName(), betTotalAmountValue);
            if(verifyResult != SystemErrorResult.SUCCESS)
            {
                return verifyResult;
            }

            String orderno = RedPOrderIdHelper.nextOrderId(type);
            BigDecimal totalBetAmount = new BigDecimal(betTotalAmountValue);
            Date createtime = new Date();

            UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());

            // create order
            BigDecimal basicAmount = new BigDecimal(basicAmountValue);
            BigDecimal feemoney = LotteryHelper.getTotalFeemoney(basicAmount, betCount);
            mLotteryOrderService.addOrder(orderno, issue, type, userInfo, userAttr.getAgentid(), betItem, basicAmount, betCount, totalBetAmount, feemoney, null);

            // pay
            FundAccountType accountType = FundAccountType.Spot;
            ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
            ErrorResult result = mPayApiMgr.doBusinessDeduct(accountType, currencyType, BusinessType.GAME_LOTTERY, orderno, userInfo, totalBetAmount, null, null);
            if(result == SystemErrorResult.SUCCESS)
            {
                mLotteryOrderService.updateTxStatus(orderno, OrderTxStatus.WAITING);
                synchronized (type)
                {
                    // lrc 缓存起来
                    runningStatus.incre(userInfo.getName(), betItem, basicAmount, betCount, totalBetAmount, feemoney);
                    runningStatus.saveCache();
                }

                // 添加返佣
                mReturnWaterManager.doReturnWater(currencyType, ReturnWaterType.GAME, userInfo, orderno, feemoney);

                // 添加当前投注记录
                MyLotteryBetRecordCache.getInstance().addRecord(false, orderno, type, issue + StringUtils.getEmpty(), userInfo.getName(), null, new BigDecimal(betTotalAmountValue), feemoney, betItem, createtime);
            }
            return result;
        } catch (Exception e) {
            LOG.error("doCreateOrder error: ", e);
        }
        return SystemErrorResult.ERR_SYS_OPT_FAILURE;
    }

//    public boolean revokeOrder(LotteryOrderInfo model)
//    {
//        try {
//            UserInfo userInfo = mUserService.findByUsername(false, model.getUsername());
//            // 更新为退款中
//            mLotteryOrderService.updateTxStatus(model.getNo(), OrderTxStatus.REFUNDING);
//
//            // 执行退款
//            boolean rs = mRefundMgr.doRefund(BusinessType.GAME_LOTTERY, userInfo, model.getNo(), model.getBetAmount(), null, null, null);
//            if(rs)
//            {
//                // 完成订单
//                mLotteryOrderService.updateTxStatus(model.getNo(), OrderTxStatus.REFUND);
//                return true;
//            }
//        } catch (Exception e) {
//            LOG.error("revoke order error:", e);
//        }
//        return false;
//    }

    private class BetItemTask implements Runnable{
        private RedPType type;
        private long issue;
        private UserInfo userInfo;
        private long amountValue;
        private long betCount;
        private String betItem;

        public RedPType getType() {
            return type;
        }

        public void setType(RedPType type) {
            this.type = type;
        }

        public long getIssue() {
            return issue;
        }

        public void setIssue(long issue) {
            this.issue = issue;
        }


        public long getAmountValue() {
            return amountValue;
        }

        public void setAmountValue(long amountValue) {
            this.amountValue = amountValue;
        }

        public String getBetItem() {
            return betItem;
        }

        public void setBetItem(String betItem) {
            this.betItem = betItem;
        }

        @Override
        public void run() {
            doCreateOrder(type, issue, userInfo, amountValue, betCount, betItem);
            count.incrementAndGet();
        }

        public UserInfo getUserInfo() {
            return userInfo;
        }

        public void setUserInfo(UserInfo userInfo) {
            this.userInfo = userInfo;
        }

        public long getBetCount() {
            return betCount;
        }

        public void setBetCount(long betCount) {
            this.betCount = betCount;
        }
    }

}
