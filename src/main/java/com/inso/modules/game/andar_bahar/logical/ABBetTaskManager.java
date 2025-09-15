package com.inso.modules.game.andar_bahar.logical;

import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.modules.common.model.BusinessType;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.game.MyLotteryBetRecordCache;
import com.inso.modules.game.andar_bahar.helper.ABHelper;
import com.inso.modules.game.andar_bahar.model.ABBetItemType;
import com.inso.modules.game.andar_bahar.model.ABType;
import com.inso.modules.game.andar_bahar.service.ABOrderService;
import com.inso.modules.passport.UserErrorResult;
import com.inso.modules.passport.business.RefundManager;
import com.inso.modules.passport.business.model.ReturnWaterType;
import com.inso.modules.passport.money.PayApiManager;
import com.inso.modules.passport.money.model.UserMoney;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.returnwater.ReturnWaterManager;
import com.inso.modules.passport.user.logical.BlackManager;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 投注任务管理器
 */
@Component
public class ABBetTaskManager {

    private static Log LOG = LogFactory.getLog(ABBetTaskManager.class);

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private ABOrderService mABOrderService;

    @Autowired
    private PayApiManager mPayApiMgr;

    @Autowired
    private RefundManager mRefundMgr;

    @Autowired
    private ReturnWaterManager mReturnWaterManager;

    @Autowired
    private BlackManager mBlackManager;

    @Autowired
    private UserMoneyService moneyService;

    private static final int DEFAULT_CAPACITY = 100;
    private AtomicInteger count = new AtomicInteger();
    private BlockingQueue<BetItemTask> queue = new ArrayBlockingQueue(DEFAULT_CAPACITY);

    private ExecutorService mThreadPool = Executors.newFixedThreadPool(10);

    private boolean stop = false;

    public ABBetTaskManager()
    {
        new Thread(new Runnable() {
            public void run() {

                while (!stop)
                {
                    try {
                        BetItemTask itemInfo = queue.take();
                        mThreadPool.execute(itemInfo);
                    } catch (Exception e) {
                        LOG.error("handle error:", e);
                    }
                }

            }
        }).start();
    }

    public boolean isFull()
    {
        return count.get() >= DEFAULT_CAPACITY;
    }

    public boolean addItemToQueue(ABType type, String issue, UserInfo userInfo, long amountValue, long betCount, ABBetItemType betItem)
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

    public ErrorResult doCreateOrder(ABType type, String issue, UserInfo userInfo, long basicAmountValue, long betCount, ABBetItemType betItem)
    {
        try {
            synchronized (userInfo.getName())
            {
                ABPeriodStatus runningStatus = ABPeriodStatus.loadCache(false, type, issue);

                // 超过这个时间全部不要
                ErrorResult errorResult = runningStatus.verifyTime();
                if(errorResult != SystemErrorResult.SUCCESS)
                {
                    return errorResult;
                }

                long betTotalAmountValue = basicAmountValue * betCount;
                if(betTotalAmountValue <= 0)
                {
                    return null;
                }
                ErrorResult verifyResult = runningStatus.verify(userInfo.getName(), betTotalAmountValue);
                if(verifyResult != SystemErrorResult.SUCCESS)
                {
                    return verifyResult;
                }

                String orderno = ABHelper.nextOrderId(type);
                BigDecimal totalBetAmount = new BigDecimal(betTotalAmountValue);
                Date createtime = new Date();

                UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());

                // create order
                BigDecimal basicAmount = new BigDecimal(basicAmountValue);
                BigDecimal feemoney = ABHelper.getTotalFeemoney(basicAmount, betCount);

                // 限制并发
                FundAccountType accountType = FundAccountType.Spot;
                ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
                UserMoney userMoney = moneyService.findMoney(false, userInfo.getId(), accountType, currencyType);
                if(!userMoney.verify(totalBetAmount))
                {
                    return UserErrorResult.ERR_PAY_NOT_ENOUGH_BALANCE;
                }

                mABOrderService.addOrder(orderno, issue, type, userInfo, userAttr, betItem, basicAmount, betCount, totalBetAmount, feemoney, null);

                // pay
                ErrorResult result = mPayApiMgr.doBusinessDeduct(accountType, currencyType, BusinessType.GAME_ANDAR_BAHAR, orderno, userInfo, totalBetAmount, feemoney, null);
                if(result == SystemErrorResult.SUCCESS)
                {
                    mABOrderService.updateTxStatus(orderno, OrderTxStatus.WAITING);

                    UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());
                    if(userType == UserInfo.UserType.MEMBER)
                    {
                        synchronized (type)
                        {
                            // lrc 缓存起来
                            runningStatus.incre(userInfo.getName(), betItem, basicAmount, betCount, totalBetAmount, feemoney);
                            runningStatus.saveCache();
                        }

                        // 添加返佣
                        mReturnWaterManager.doReturnWater(currencyType, ReturnWaterType.GAME, userInfo, orderno, feemoney);
                    }

                    // 添加当前投注记录
                    MyLotteryBetRecordCache.getInstance().addRecord(false, orderno, type, issue, userInfo.getName(), null, new BigDecimal(betTotalAmountValue), feemoney, betItem.getKey(), createtime);
                }
                else if(result == UserErrorResult.ERR_PAY_NOT_ENOUGH_BALANCE)
                {
                    // 这里余额不足的概率不太可能
                    //mBlackManager.addBlack(userInfo.getName());
                }
                return result;
            }
        } catch (Exception e) {
            LOG.error("doCreateOrder error: ", e);
        }
        return SystemErrorResult.ERR_SYS_OPT_FAILURE;
    }

    private class BetItemTask implements Runnable{
        private ABType type;
        private String issue;
        private UserInfo userInfo;
        private long amountValue;
        private long betCount;
        private ABBetItemType betItem;

        public ABType getType() {
            return type;
        }

        public void setType(ABType type) {
            this.type = type;
        }

        public String getIssue() {
            return issue;
        }

        public void setIssue(String issue) {
            this.issue = issue;
        }


        public long getAmountValue() {
            return amountValue;
        }

        public void setAmountValue(long amountValue) {
            this.amountValue = amountValue;
        }

        public ABBetItemType getBetItem() {
            return betItem;
        }

        public void setBetItem(ABBetItemType betItem) {
            this.betItem = betItem;
        }

        @Override
        public void run() {
            try {
                doCreateOrder(type, issue, userInfo, amountValue, betCount, betItem);
            } finally {
                count.decrementAndGet();
            }
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
