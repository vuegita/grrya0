package com.inso.modules.game.rocket.logical;

import com.alibaba.druid.util.LRUCache;
import com.alibaba.fastjson.annotation.JSONField;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AtomicDouble;
import com.google.common.util.concurrent.RateLimiter;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.GameErrorResult;
import com.inso.modules.game.model.RealtimeBetItemReport;
import com.inso.modules.game.rocket.helper.RocketHelper;
import com.inso.modules.game.rocket.model.RocketType;
import com.inso.modules.passport.MyConstants;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
public class RocketPeriodStatus {

    private static Log LOG = LogFactory.getLog(RocketPeriodStatus.class);

    public static final int EXPIRES = 3600 * 5;
    private static final String CACHE_KEY = MyConstants.DEFAULT_GAME_MODULE_NAME + "_lottery_period_status_";

    /*** 用户总投注 ***/
    private static final String CACHE_KEY_USER_BET_MONEY = MyConstants.DEFAULT_GAME_MODULE_NAME + "_lottery_period_status_user_bet_money_";

    private static LRUCache<String, RocketPeriodStatus> mLruCache = new LRUCache<>(RocketType.values().length * 2);


    private RocketType type;
    /*** 期号 ***/
    private String issue;
    /*** 开奖数字 ***/
    private String openResult = StringUtils.getEmpty();
    private String currentResult;
    /*** 每期最多投注金额 ***/
    private float maxMoneyOfIssue;
    /*** 当前投注总额 ***/
    private float currentBetMoneyOfIssue;
    private AtomicDouble mCurrurentTotalBetMoney;
    /*** 当前下车总中奖金额 ***/
    private AtomicDouble cashoutWinAmountOfIssue;
    /*** 下注时下车总中奖金额 ***/
    private AtomicDouble betCashoutWinAmount;
    /*** 实时下车总中奖金额 ***/
    private AtomicDouble decreCashoutWinAmount;
    /*** 下车金额- ***/
    private AtomicDouble decreCashoutAmount;

    /*** 每个用户最多投注金额 ***/
    private float maxMoneyOfUser;

    /*** 当前下注总人数 ***/
    private int mTotalBetCount;
    /*** 当前下车总人数 ***/
    private AtomicInteger mCashoutCount;
    /*** 当前机器人 ***/
    private int mRobotBetCount;
    private int mRobotCashoutCount;

    /*** 开盘时间 ***/
    private Date startTime;
    /*** 结束时间 ***/
    private Date endTime;
    /*** 是否是 ***/
    private boolean isInit = false;

    /*** 投注数据汇总 ***/
    private List<RealtimeBetItemReport> mBetItemReportList;

    private Map<String, RealtimeBetItemReport> mBetItemReportMap = Maps.newHashMap();

    private static RateLimiter mRateLimiter = RateLimiter.create(1);

    public static RocketPeriodStatus loadCache(boolean purge, RocketType type, String issue)
    {
        if(type == null || StringUtils.isEmpty(issue))
        {
            throw new RuntimeException("type or issue is null");
        }
        String uniqueKey = type.getKey() + issue;
        String cachekey = CACHE_KEY + uniqueKey;

        RocketPeriodStatus status = mLruCache.get(uniqueKey);


        if(purge || status == null)
        {
            if(mRateLimiter.tryAcquire(1, 3, TimeUnit.SECONDS))
            {
                status = mLruCache.get(uniqueKey);
                if(status == null)
                {
                    status = CacheManager.getInstance().getObject(cachekey, RocketPeriodStatus.class);
                }

                if(status == null)
                {
                    status = new RocketPeriodStatus();
                    status.setType(type);
                    status.setIssue(issue);
                }

                mLruCache.put(uniqueKey, status);
            }
        }

        return status;
    }

    public static RocketPeriodStatus tryLoadCache(boolean purge, GameChildType type, String issue)
    {
        String uniqueKey = type.getKey() + issue;
        String cachekey = CACHE_KEY + uniqueKey;

        RocketPeriodStatus status = mLruCache.get(uniqueKey);

        if(purge || status == null)
        {
            status = CacheManager.getInstance().getObject(cachekey, RocketPeriodStatus.class);
        }

//        if(status != null)
//        {
//            mLruCache.put(uniqueKey, status);
//        }

//        if(status != null)
//        {
//            status.init();
//        }
        return status;
    }

    public void saveCache()
    {
        this.isInit = true;
        String cachekey = CACHE_KEY + type.getKey() + issue;

        String value = FastJsonHelper.jsonEncode(this);
        CacheManager.getInstance().setString(cachekey, value, EXPIRES);
    }

    public void incre(String username, String betItemType, BigDecimal betAmount, BigDecimal feemoney)
    {
        init();

        mTotalBetCount ++;

        float betItem = StringUtils.asFloat(betItemType);
        // 如果中奖能中多少
        BigDecimal winAmountIfRealized = RocketHelper.calcWinMoney(betItem, betItem, betAmount);
        // 投注金额累计 当前投注
        String newRangeBetItem = RocketHelper.getMaxBetItemValue(false, betItem);
        RealtimeBetItemReport rgRealtimeBetItem = (RealtimeBetItemReport)mBetItemReportMap.get(newRangeBetItem);
        rgRealtimeBetItem.incre(betAmount, winAmountIfRealized, feemoney);

        getBetCashoutWinAmount().addAndGet(winAmountIfRealized.doubleValue());

        // 更新当前用户
        double currentUserBetMoney = betAmount.doubleValue();

        String cackey = CACHE_KEY_USER_BET_MONEY + username + issue;
        CacheManager.getInstance().setString(cackey, currentUserBetMoney + StringUtils.getEmpty());


        double newValue = getTotalBetMoney().addAndGet(currentUserBetMoney);
        this.currentBetMoneyOfIssue = (float) newValue;
    }

    public void decryByCashout(String username, BigDecimal betAmount, BigDecimal cashoutResult, String autoCashout)
    {
        getmCashoutCount().incrementAndGet();

        getDecreCashoutAmount().addAndGet(betAmount.doubleValue());

        float openResult = StringUtils.asFloat(cashoutResult);
        BigDecimal winAmount = RocketHelper.calcWinMoney(openResult, openResult, betAmount);
        if(winAmount != null && winAmount.compareTo(BigDecimal.ZERO) > 0)
        {
            getCashoutWinAmountOfIssue().addAndGet(winAmount.doubleValue());
        }

        float autoCashoutValue = StringUtils.asFloat(autoCashout);
        if(autoCashoutValue <= 0)
        {
            return;
        }
        BigDecimal oldWinAmount = RocketHelper.calcWinMoney(autoCashoutValue, autoCashoutValue, betAmount);
        if(oldWinAmount != null && oldWinAmount.compareTo(BigDecimal.ZERO) > 0)
        {
            getDecreCashoutWinAmount().addAndGet(oldWinAmount.doubleValue());
        }
    }

    public void updateRobot(boolean incre, int count)
    {
        if(incre)
        {
            this.mRobotBetCount = count;
        }
        else
        {
            this.mRobotCashoutCount = count;
            if(this.mRobotCashoutCount >= this.mRobotBetCount)
            {
                this.mRobotCashoutCount = this.mRobotBetCount;
            }
        }
    }

    private void updateBetItem(BigDecimal betAmount, BigDecimal winAmount, BigDecimal feemoney, float betItem, boolean onlyUpdateWinAmount)
    {
        String key = RocketHelper.getMaxBetItemValue(true, betItem);
        RealtimeBetItemReport rgRealtimeBetItem = (RealtimeBetItemReport)mBetItemReportMap.get(key);
        rgRealtimeBetItem.incre(betAmount, winAmount, feemoney, onlyUpdateWinAmount);
    }

    private void init()
    {
        if(CollectionUtils.isEmpty(mBetItemReportList))
        {
            this.mBetItemReportList = Lists.newArrayList();

            float zero = 0;
            String zeroKey = RocketHelper.getMaxBetItemValue(false, zero);
            RealtimeBetItemReport zeroReport = new RealtimeBetItemReport(zeroKey);
            zeroReport.setMaxOpenResultValue(zero);

            mBetItemReportList.add(zeroReport);

            for(float value : RocketHelper.mBetItemSequenceArr)
            {
                String maxOpenValue = RocketHelper.getMaxBetItemValue(false, value);
                String key =  maxOpenValue ;

                RealtimeBetItemReport tmpReport = new RealtimeBetItemReport(key);
                tmpReport.setMaxOpenResultValue(StringUtils.asFloat(maxOpenValue));

                mBetItemReportList.add(tmpReport);
            }
        }

        if(mBetItemReportMap.isEmpty())
        {
            for(RealtimeBetItemReport item : mBetItemReportList)
            {
                mBetItemReportMap.put(item.getOpenResult(), item);
            }
        }

    }


    public ErrorResult verifyTime()
    {
        long time = System.currentTimeMillis();
        if( !(time >= startTime.getTime() && time <= (endTime.getTime() - type.getDisableMillis())))
        {
//            System.out.println("current time = " + DateUtils.convertString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, new Date(time)));
//            System.out.println("startTime time = " + DateUtils.convertString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, startTime));
//            System.out.println("endTime time = " + DateUtils.convertString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, endTime));
            // 封盘
            return GameErrorResult.ERR_CURRENT_ISSUE_FINISH;
        }
        return SystemErrorResult.SUCCESS;
    }

    public boolean existBet(String username)
    {
        String cackey = CACHE_KEY_USER_BET_MONEY + username + issue;
        return CacheManager.getInstance().exists(cackey);
    }

    public ErrorResult verify(String username, BigDecimal betMoney)
    {
        String cackey = CACHE_KEY_USER_BET_MONEY + username + issue;
        if(CacheManager.getInstance().exists(cackey))
        {
           return SystemErrorResult.ERR_EXIST;
        }
        ErrorResult result = verifyTime();
        if(result != SystemErrorResult.SUCCESS)
        {
            return result;
        }

        // 总投注限制
        if(maxMoneyOfIssue > 0 && maxMoneyOfIssue - currentBetMoneyOfIssue < betMoney.floatValue())
        {
            return GameErrorResult.ERR_LIMIT_TOTAL_AMOUNT;
        }

        // 用户投注总额限制
//        if(maxMoneyOfUser > 0)
//        {
//            String cackey = CACHE_KEY_USER_BET_MONEY + username + issue;
//            long userBetMoney = CacheManager.getInstance().getLong(cackey);
//            if(userBetMoney > maxMoneyOfUser)
//            {
//                return GameErrorResult.ERR_LIMIT_USER_AMOUNT;
//            }
//        }

        return SystemErrorResult.SUCCESS;
    }


    @JSONField(serialize = false, deserialize = false)
    public List getBetItemReportList()
    {
        init();
        BigDecimal totalBetAmount = new BigDecimal(currentBetMoneyOfIssue);
//        for(RealtimeBetItemReport item : mBetItemReportList)
//        {
//            item.setTotalBetAmount(totalBetAmount);
//        }


        for(RealtimeBetItemReport realReport : mBetItemReportList)
        {
            BigDecimal tmpSingleBetValue = BigDecimal.ZERO;
            if(realReport.getTotalBetAmount().compareTo(BigDecimal.ZERO) > 0)
            {
                tmpSingleBetValue = tmpSingleBetValue.add(realReport.getTotalBetAmount()).subtract(realReport.getTotalFeemoney());
            }

            for(RealtimeBetItemReport tmpReport : mBetItemReportList)
            {
                if(realReport.getOpenResult().equalsIgnoreCase(tmpReport.getOpenResult()))
                {
                    continue;
                }

                if(tmpReport.getTotalBetAmount() == null || tmpReport.getTotalBetAmount().compareTo(BigDecimal.ZERO) <= 0)
                {
                    continue;
                }

                if(realReport.getMaxOpenResultValue() < tmpReport.getMaxOpenResultValue() && realReport.getMaxOpenResultValue() > 0)
                {
                    tmpSingleBetValue = tmpSingleBetValue.add(tmpReport.getTotalBetAmount()).subtract(tmpReport.getTotalFeemoney());
                }
            }

            if(tmpSingleBetValue.compareTo(BigDecimal.ZERO) <= 0)
            {
                continue;
            }

            BigDecimal winAmount = tmpSingleBetValue.multiply(BigDecimal.valueOf(realReport.getMaxOpenResultValue()));
            realReport.setTotalWinAmount(winAmount);
            realReport.update(totalBetAmount, null, null);
        }

        return mBetItemReportList;
    }

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

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }


    public boolean isInit() {
        return isInit;
    }

    public void setInit(boolean init) {
        isInit = init;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getOpenResult() {
        return openResult;
    }

    public void setOpenResult(String openResult) {
        this.openResult = openResult;
    }

    public float getMaxMoneyOfIssue() {
        return maxMoneyOfIssue;
    }

    public void setMaxMoneyOfIssue(float maxMoneyOfIssue) {
        this.maxMoneyOfIssue = maxMoneyOfIssue;
    }

    public float getMaxMoneyOfUser() {
        return maxMoneyOfUser;
    }

    public void setMaxMoneyOfUser(float maxMoneyOfUser) {
        this.maxMoneyOfUser = maxMoneyOfUser;
    }

    public float getCurrentBetMoneyOfIssue() {
        return currentBetMoneyOfIssue;
    }

    public void setCurrentBetMoneyOfIssue(float currentBetMoneyOfIssue) {
        this.currentBetMoneyOfIssue = currentBetMoneyOfIssue;
    }
    public List<RealtimeBetItemReport> getmBetItemReportList() {
        return mBetItemReportList;
    }

    public void setmBetItemReportList(List<RealtimeBetItemReport> mBetItemReportList) {
        this.mBetItemReportList = mBetItemReportList;
    }


    @JSONField(serialize = false, deserialize = false)
    public AtomicDouble getTotalBetMoney() {
        if(mCurrurentTotalBetMoney == null)
        {
            this.mCurrurentTotalBetMoney = new AtomicDouble(currentBetMoneyOfIssue);
        }
        return mCurrurentTotalBetMoney;
    }

    private void log()
    {
        List list = getBetItemReportList();

        for(Object value : list)
        {
            System.out.println("= " + FastJsonHelper.jsonEncode(value));
        }

        System.out.println("======================================== ");
        System.out.println("======================================== ");
        System.out.println("======================================== ");
    }
    public static void main(String[] args) {
        String key = "1120220718429";
        RocketPeriodStatus status = RocketPeriodStatus.loadCache(false, RocketType.CRASH, key);

        BigDecimal basicAmount = new BigDecimal("10");
        long basicCount = 1;
        BigDecimal betAmount = basicAmount.multiply(new BigDecimal(basicCount));

        BigDecimal feemoney = new BigDecimal(1);

//        status.incre("u1", "Red", basicAmount, basicCount, betAmount, feemoney);
////        status.incre("u2", "Red", basicAmount, basicCount, betAmount, feemoney);
//        status.incre("u1", "1", basicAmount, basicCount, betAmount, feemoney);

        status.saveCache();
        RocketPeriodStatus cacheStatus = RocketPeriodStatus.loadCache(true, RocketType.CRASH, key);
//        cacheStatus.log();

//        status.getBetItemReportList();

        System.out.println(FastJsonHelper.jsonEncode(cacheStatus.getBetItemReportList()));
    }

    public AtomicDouble getCashoutWinAmountOfIssue() {
        if(cashoutWinAmountOfIssue == null)
        {
            cashoutWinAmountOfIssue = new AtomicDouble();
        }
        return cashoutWinAmountOfIssue;
    }

    public void setCashoutWinAmountOfIssue(AtomicDouble cashoutWinAmountOfIssue) {
        this.cashoutWinAmountOfIssue = cashoutWinAmountOfIssue;
    }

    public String getCurrentResult() {
        return currentResult;
    }

    public void setCurrentResult(String currentResult) {
        this.currentResult = currentResult;
    }

    public int getmTotalBetCount() {
        return mTotalBetCount;
    }

    public void setmTotalBetCount(int mTotalBetCount) {
        this.mTotalBetCount = mTotalBetCount;
    }

    public AtomicInteger getmCashoutCount() {
        if(mCashoutCount == null)
        {
            mCashoutCount = new AtomicInteger();
        }
        return mCashoutCount;
    }

    public void setmCashoutCount(AtomicInteger mCashoutCount) {
        this.mCashoutCount = mCashoutCount;
    }

    public int getmRobotBetCount() {
        return mRobotBetCount;
    }

    public void setmRobotBetCount(int mRobotBetCount) {
        this.mRobotBetCount = mRobotBetCount;
    }

    public int getmRobotCashoutCount() {
        return mRobotCashoutCount;
    }

    public void setmRobotCashoutCount(int mRobotCashoutCount) {
        this.mRobotCashoutCount = mRobotCashoutCount;
    }

    public AtomicDouble getBetCashoutWinAmount() {
        if(betCashoutWinAmount == null)
        {
            betCashoutWinAmount = new AtomicDouble();
        }
        return betCashoutWinAmount;
    }

    public void setBetCashoutWinAmount(AtomicDouble betCashoutWinAmount) {
        this.betCashoutWinAmount = betCashoutWinAmount;
    }

    public AtomicDouble getDecreCashoutWinAmount() {
        if(decreCashoutWinAmount == null)
        {
            decreCashoutWinAmount = new AtomicDouble();
        }
        return decreCashoutWinAmount;
    }

    public void setDecreCashoutWinAmount(AtomicDouble decreCashoutWinAmount) {
        this.decreCashoutWinAmount = decreCashoutWinAmount;
    }

    public AtomicDouble getDecreCashoutAmount() {
        if(decreCashoutAmount == null)
        {
            decreCashoutAmount = new AtomicDouble();
        }
        return decreCashoutAmount;
    }

    public void setDecreCashoutAmount(AtomicDouble decreCashoutAmount) {
        this.decreCashoutAmount = decreCashoutAmount;
    }
}
