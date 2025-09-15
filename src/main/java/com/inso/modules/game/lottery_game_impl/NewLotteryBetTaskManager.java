package com.inso.modules.game.lottery_game_impl;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.helper.IdGenerator;
import com.inso.modules.common.model.*;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.MyLotteryBetRecordCache;
import com.inso.modules.game.helper.BetFeemoneyHelper;
import com.inso.modules.game.lottery_game_impl.base.IMessageAsyncNotify;
import com.inso.modules.game.lottery_game_impl.helper.GameBetTaskHelper;
import com.inso.modules.game.lottery_game_impl.pg.model.PgGameType;
import com.inso.modules.game.model.NewLotteryOrderInfo;
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
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 投注任务管理器
 */
@Component
public class NewLotteryBetTaskManager {

    private static Log LOG = LogFactory.getLog(NewLotteryBetTaskManager.class);

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

    private static IdGenerator mIdGenerator = IdGenerator.newSingleWorder();

    private static final int DEFAULT_CAPACITY = 800;
    private AtomicInteger count = new AtomicInteger();

    private BlockingQueue<BetItemTask> mTmpQueue = new ArrayBlockingQueue(DEFAULT_CAPACITY);
    private BlockingQueue<BetItemTask> queue = new ArrayBlockingQueue(DEFAULT_CAPACITY);

    private boolean isInit = false;
    private boolean stop = false;

    private long mLastRefresh = -1;

    public NewLotteryBetTaskManager()
    {
    }

    private void init()
    {
        if(isInit)
        {
            return;
        }
        isInit = true;
        synchronized (NewLotteryBetTaskManager.class)
        {
            for(int i = 0; i < DEFAULT_CAPACITY; i ++)
            {
                BetItemTask betItemTask = new BetItemTask();
                mTmpQueue.add(betItemTask);
            }

            new Thread(new Runnable() {
                public void run() {

                    while (!stop)
                    {
                        try {
                            BetItemTask itemInfo = queue.take();
                            GameBetTaskHelper.execTask(itemInfo);
                        } catch (Exception e) {
                            LOG.error("handle error:", e);
                        }
                    }

                }
            }).start();
        }
    }

    public boolean isFull()
    {
        long ts = System.currentTimeMillis();

        int rs = count.get();
        if(rs >= 300)
        {
            LOG.warn("bet-task-queue-size = " + rs);
        }
        else if(mLastRefresh == 0 || ts - mLastRefresh >= 10_000)
        {
            //LOG.info("bet-task-queue-size = " + rs);
            this.mLastRefresh = ts;
        }
        return rs >= DEFAULT_CAPACITY;
    }

    public boolean addItemToQueue(String sessionid, IMessageAsyncNotify asyncNotify, GameChildType type, String issue, UserInfo userInfo, BigDecimal totalBetAmount, int betCount, BigDecimal singleBetAmount, String betItem, String[] betItemArr)
    {
        if(isFull())
        {
            return false;
        }

        init();
        try {
            BetItemTask betItemTask = mTmpQueue.take();
            betItemTask.setmBetAsyncNotify(asyncNotify);
            betItemTask.setSessionid(sessionid);
            betItemTask.setType(type);
            betItemTask.setIssue(issue);
            betItemTask.setUserInfo(userInfo);
            betItemTask.setSingleBetAmount(singleBetAmount);
            betItemTask.setTotalBetAmount(totalBetAmount);
            betItemTask.setBetItem(betItem);
            betItemTask.setBetItemArr(betItemArr);
            betItemTask.setBetCount(betCount);

            if(queue.add(betItemTask))
            {
                count.incrementAndGet();
                return true;
            }
        } catch (Exception e) {
            LOG.error("handle error:", e);
        }
        return false;
    }

    public ErrorResult doCreateOrder(String orderno, NewLotteryOrderInfo oldOrderInfo, BetItemTask betItemTask, GameChildType gameType, String issue, UserInfo userInfo, BigDecimal totalBetAmount, int betCount, BigDecimal singleAmount, String betItem, String[] betItemArr)
    {
        try {
            NewLotteryPeriodStatus runningStatus = NewLotteryPeriodStatus.loadCache(false, gameType, issue);
            if(runningStatus == null)
            {
//                notifyResult(betItemTask, SystemErrorResult.ERR_SYS_OPT_FAILURE, null, null);
                LOG.error("err1: ingore order " + userInfo.getName() + ", amount = " + totalBetAmount);
                return SystemErrorResult.ERR_SYS_OPT_FAILURE;
            }

            ErrorResult verifyResult = runningStatus.verify(userInfo.getName(), totalBetAmount.floatValue());
            if(verifyResult != SystemErrorResult.SUCCESS)
            {
//                notifyResult(betItemTask, verifyResult, null, null);
                LOG.error("err2: ingore order " + userInfo.getName() + ", amount = " + totalBetAmount);
                return verifyResult;
            }

            FundAccountType accountType = FundAccountType.Spot;
            ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
            UserMoney userMoney = moneyService.findMoney(false, userInfo.getId(), accountType, currencyType);
            if(!userMoney.verify(totalBetAmount))
            {
//                notifyResult(betItemTask, UserErrorResult.ERR_PAY_NOT_ENOUGH_BALANCE, null, null);
                LOG.error("err3: ingore order " + userInfo.getName() + ", amount = " + totalBetAmount + ", 余额不足!");
                return UserErrorResult.ERR_PAY_NOT_ENOUGH_BALANCE;
            }

            if(StringUtils.isEmpty(orderno))
            {
                orderno = nextOrderId(gameType);
            }

            boolean externalGame = gameType instanceof PgGameType;

            UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());

            // create order
            BigDecimal feemoney = BetFeemoneyHelper.getSingleFeemoney(totalBetAmount);

            if(oldOrderInfo == null)
            {
                mLotteryOrderService.addOrder(orderno, issue, gameType, userInfo, userAttr, betItem, totalBetAmount, betCount, singleAmount, feemoney, null);
            }

            RemarkVO remarkVO = new RemarkVO();
            remarkVO.setMesage(gameType.getKey());

            BusinessType businessType = BusinessType.GAME_NEW_LOTTERY;
            if(externalGame)
            {
                businessType = BusinessType.GAME_PG_LOTTERY;
            }

            // pay
            // 限制并发
            ErrorResult result = null;
            if(totalBetAmount.compareTo(BigDecimal.ZERO) > 0)
            {
                result = mPayApiMgr.doBusinessDeduct(accountType, currencyType, businessType, orderno, userInfo, totalBetAmount, feemoney, remarkVO);
            }
            else if(externalGame && totalBetAmount.compareTo(BigDecimal.ZERO) == 0)
            {
                // 外部免费游戏
                mLotteryOrderService.updateTxStatus(userInfo.getId(), gameType, orderno, OrderTxStatus.WAITING, null);
                result = SystemErrorResult.SUCCESS;
                return result;
            }
            if(result == SystemErrorResult.SUCCESS)
            {
                mLotteryOrderService.updateTxStatus(userInfo.getId(), gameType, orderno, OrderTxStatus.WAITING, null);

                UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());
                if(userType == UserInfo.UserType.MEMBER)
                {
                    synchronized (NewLotteryBetTaskManager.class)
                    {
                        // lrc 缓存起来
                        runningStatus.incre(userInfo.getName(), totalBetAmount, betItemArr, singleAmount, feemoney);
                        runningStatus.saveCache();
                    }

                    if(!externalGame)
                    {
                        // 添加返佣 | 外部游戏不返佣
                        mReturnWaterManager.doReturnWater(currencyType, ReturnWaterType.GAME, userInfo, orderno, feemoney, totalBetAmount);
                    }
                }

                // 添加当前投注记录
                Date createtime = new Date();
                if(betCount <= 1)
                {
                    MyLotteryBetRecordCache.getInstance().addRecord(false, orderno, gameType, issue, userInfo.getName(), null, totalBetAmount, feemoney, betItem, createtime);
                    GiftStatusHelper.getInstance().save(gameType, userInfo.getName(), betItem, totalBetAmount);
                }
                else
                {
                    BigDecimal singleFeemoney = BetFeemoneyHelper.getSingleFeemoney(totalBetAmount);
                    for(String tmpItem : betItemArr)
                    {
                        MyLotteryBetRecordCache.getInstance().addRecord(false, orderno, gameType, issue, userInfo.getName(), null, singleAmount, singleFeemoney, tmpItem, createtime);
                        GiftStatusHelper.getInstance().save(gameType, userInfo.getName(), tmpItem, singleAmount);
                    }
                }

                notifyResult(betItemTask, SystemErrorResult.SUCCESS, orderno, userInfo, totalBetAmount, betItemArr);
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

    public ErrorResult settleExternalOrder(String orderno, GameChildType gameChildType, UserInfo userInfo, BigDecimal winAmount)
    {
        try {
//            String orderno = orderInfo.getNo();

//            GameChildType gameChildType = GameChildType.getType(orderInfo.getLotteryType());
            if(winAmount == null || winAmount.compareTo(BigDecimal.ZERO) <= 0)
            {
                mLotteryOrderService.updateTxStatusToFailed(userInfo.getId(), gameChildType, orderno, null, null);
                return SystemErrorResult.SUCCESS;
            }

            FundAccountType accountType = FundAccountType.Spot;
            ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
            ErrorResult result = mPayApiMgr.doBusinessRecharge(accountType, currencyType, BusinessType.GAME_PG_LOTTERY, orderno, userInfo, winAmount, null);

            if(result == SystemErrorResult.SUCCESS)
            {
                mLotteryOrderService.updateTxStatusToRealized(userInfo.getId(), gameChildType, orderno, null, winAmount, null, null);
            }

            return result;
        } catch (Exception e) {
            LOG.error("settleExternalOrder error: " + orderno, e);
            return SystemErrorResult.ERR_SYS_OPT_FAILURE;
        }
    }

    private void notifyResult(BetItemTask itemTask, ErrorResult result, String orderno, UserInfo userInfo, BigDecimal betAmount, String[] betItemArr)
    {
        try {
            if(itemTask == null || itemTask.getmBetAsyncNotify() == null)
            {
                return;
            }
            itemTask.getmBetAsyncNotify().onBetFinish(itemTask.getSessionid(), result, orderno, userInfo, betAmount, betItemArr);
        } catch (Exception e) {
            LOG.error("handle error:", e);
        }

    }

    /**
     * 生成订单号
     * @param lotteryType
     * @return
     */
    public static String nextOrderId(GameChildType lotteryType)
    {
        return mIdGenerator.nextId(lotteryType.getCode());
    }

    private class BetItemTask implements Runnable{
        private GameChildType type;
        private String issue;
        private UserInfo userInfo;
        private BigDecimal totalBetAmount;
        private BigDecimal singleBetAmount;
        private String betItem;
        private String[] betItemArr;
        private int betCount;

        private String sessionid;
        private IMessageAsyncNotify mBetAsyncNotify;

        public GameChildType getType() {
            return type;
        }

        public void setType(GameChildType type) {
            this.type = type;
        }

        public String getIssue() {
            return issue;
        }

        public void setIssue(String issue) {
            this.issue = issue;
        }

        @Override
        public void run() {
            try {
                doCreateOrder(null, null, this, type, issue, userInfo, totalBetAmount, betCount, singleBetAmount, betItem, betItemArr);
            }
            catch (Exception e)
            {
                LOG.error("submit order error:", e);
            }

            if(mBetAsyncNotify != null)
            {
                mBetAsyncNotify.close();
                mBetAsyncNotify = null;
            }

            count.decrementAndGet();
            mTmpQueue.add(this);
        }

        public UserInfo getUserInfo() {
            return userInfo;
        }

        public void setUserInfo(UserInfo userInfo) {
            this.userInfo = userInfo;
        }

        public int getBetCount() {
            return betCount;
        }

        public void setBetCount(int betCount) {
            this.betCount = betCount;
        }

        public String getBetItem() {
            return betItem;
        }

        public void setBetItem(String betItem) {
            this.betItem = betItem;
        }

        public BigDecimal getTotalBetAmount() {
            return totalBetAmount;
        }

        public void setTotalBetAmount(BigDecimal totalBetAmount) {
            this.totalBetAmount = totalBetAmount;
        }

        public BigDecimal getSingleBetAmount() {
            return singleBetAmount;
        }

        public void setSingleBetAmount(BigDecimal singleBetAmount) {
            this.singleBetAmount = singleBetAmount;
        }

        public String[] getBetItemArr() {
            return betItemArr;
        }

        public void setBetItemArr(String[] betItemArr) {
            this.betItemArr = betItemArr;
        }

        public IMessageAsyncNotify getmBetAsyncNotify() {
            return mBetAsyncNotify;
        }

        public void setmBetAsyncNotify(IMessageAsyncNotify mBetAsyncNotify) {
            this.mBetAsyncNotify = mBetAsyncNotify;
        }

        public String getSessionid() {
            return sessionid;
        }

        public void setSessionid(String sessionid) {
            this.sessionid = sessionid;
        }
    }


    private void test1()
    {
        String orderno = System.currentTimeMillis() + "";
        GameChildType gameChildType = PgGameType.PG_Fortune_Tiger;
        String username = "c_0xFA730bd82c7E8721aF28c8A0ed56Bf9041E94dEE";
        UserInfo userInfo = mUserService.findByUsername(false, username);

        String betItem = "";
        BigDecimal totalBetAmount = new BigDecimal(10);
        BigDecimal totalWinAmount = new BigDecimal(5);

        String issue = "";


        doCreateOrder(orderno, null, null, gameChildType, issue, userInfo, totalBetAmount, 1, totalBetAmount, betItem, null);


        settleExternalOrder(orderno, gameChildType, userInfo, totalWinAmount);

    }

    public static void testRun()
    {
        NewLotteryBetTaskManager mgr = SpringContextUtils.getBean(NewLotteryBetTaskManager.class);
        mgr.test1();

    }
}
