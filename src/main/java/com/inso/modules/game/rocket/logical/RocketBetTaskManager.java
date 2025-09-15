package com.inso.modules.game.rocket.logical;

import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.modules.common.model.*;
import com.inso.modules.game.MyLotteryBetRecordCache;
import com.inso.modules.game.helper.BetFeemoneyHelper;
import com.inso.modules.game.rocket.helper.RocketHelper;
import com.inso.modules.game.rocket.model.RocketType;
import com.inso.modules.game.service.NewLotteryOrderService;
import com.inso.modules.passport.UserErrorResult;
import com.inso.modules.passport.business.RefundManager;
import com.inso.modules.passport.business.model.ReturnWaterType;
import com.inso.modules.passport.gift.helper.GiftStatusHelper;
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
public class RocketBetTaskManager {

    private static Log LOG = LogFactory.getLog(RocketBetTaskManager.class);

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private NewLotteryOrderService mLotteryOrderService;

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

    public RocketBetTaskManager()
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

    public boolean addItemToQueue(RocketType type, String issue, UserInfo userInfo, BigDecimal betAmount, String betItem)
    {
        if(isFull())
        {
            return false;
        }
        BetItemTask betItemTask = new BetItemTask();
        betItemTask.setType(type);
        betItemTask.setIssue(issue);
        betItemTask.setUserInfo(userInfo);
        betItemTask.setBetAmount(betAmount);
        betItemTask.setBetItem(betItem);

        if(queue.add(betItemTask))
        {
            count.incrementAndGet();
            return true;
        }
        return false;
    }

    public ErrorResult doCreateOrder(RocketType type, String issue, UserInfo userInfo, BigDecimal betAmount, String betItem)
    {
        try {
            RocketPeriodStatus runningStatus = RocketPeriodStatus.loadCache(false, type, issue);
            if(runningStatus == null)
            {
                return SystemErrorResult.ERR_SYS_OPT_FAILURE;
            }

            ErrorResult verifyResult = runningStatus.verify(userInfo.getName(), betAmount);
            if(verifyResult != SystemErrorResult.SUCCESS)
            {
                return verifyResult;
            }

            String orderno = RocketHelper.nextOrderId(issue, userInfo.getId(), false);
            Date createtime = new Date();

            UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());

            // create order
            BigDecimal feemoney = BetFeemoneyHelper.getTotalFeemoney(betAmount);

            // 限制并发
            FundAccountType accountType = FundAccountType.Spot;
            ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
            UserMoney userMoney = moneyService.findMoney(false, userInfo.getId(), accountType, currencyType);
            if(!userMoney.verify(betAmount))
            {
                return UserErrorResult.ERR_PAY_NOT_ENOUGH_BALANCE;
            }

            mLotteryOrderService.addOrder(orderno, issue, type, userInfo, userAttr, betItem, betAmount, 1, betAmount, feemoney, null);


            RemarkVO remarkVO = RemarkVO.create(type.getKey());

            // pay
            ErrorResult result = mPayApiMgr.doBusinessDeduct(accountType, currencyType, BusinessType.GAME_NEW_LOTTERY, orderno, userInfo, betAmount, feemoney, remarkVO);
            if(result == SystemErrorResult.SUCCESS)
            {
                mLotteryOrderService.updateTxStatus(userInfo.getId(), type, orderno, OrderTxStatus.WAITING, null);

                UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());
                if(userType == UserInfo.UserType.MEMBER)
                {
                    // lrc 缓存起来
                    runningStatus.incre(userInfo.getName(), betItem, betAmount, feemoney);
                    runningStatus.saveCache();

                    // 添加返佣
                    mReturnWaterManager.doReturnWater(currencyType, ReturnWaterType.GAME, userInfo, orderno, feemoney);
                }

                // 添加当前投注记录
                MyLotteryBetRecordCache.getInstance().addRecord(false, orderno, type, issue, userInfo.getName(), null, betAmount, feemoney, betItem, createtime);
                GiftStatusHelper.sendMessage(type, userInfo.getName(), betItem, betAmount);
            }
            else if(result == UserErrorResult.ERR_PAY_NOT_ENOUGH_BALANCE)
            {
                // 这里余额不足的概率不太可能
                //mBlackManager.addBlack(userInfo.getName());
            }
            return result;

        } catch (Exception e) {
            LOG.error("doCreateOrder error: ", e);
        }
        return SystemErrorResult.ERR_SYS_OPT_FAILURE;
    }

    private class BetItemTask implements Runnable{
        private RocketType type;
        private String issue;
        private UserInfo userInfo;
        private BigDecimal betAmount;
        private String betItem;

        public RocketType getType() {
            return type;
        }

        public void setType(RocketType type) {
            this.type = type;
        }

        public String getIssue() {
            return issue;
        }

        public void setIssue(String issue) {
            this.issue = issue;
        }


        public String getBetItem() {
            return betItem;
        }

        public void setBetItem(String betItem) {
            this.betItem = betItem;
        }

        @Override
        public void run() {
            try {
                doCreateOrder(type, issue, userInfo, betAmount, betItem);
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

        public BigDecimal getBetAmount() {
            return betAmount;
        }

        public void setBetAmount(BigDecimal betAmount) {
            this.betAmount = betAmount;
        }
    }

}
