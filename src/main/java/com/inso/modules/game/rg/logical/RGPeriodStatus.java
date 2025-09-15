package com.inso.modules.game.rg.logical;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.alibaba.druid.util.LRUCache;
import com.alibaba.fastjson.annotation.JSONField;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.GameErrorResult;
import com.inso.modules.game.model.RealtimeBetItemReport;
import com.inso.modules.game.rg.helper.LotteryHelper;
import com.inso.modules.game.lottery_game_impl.rg2.model.LotteryRgBetItemType;
import com.inso.modules.game.rg.model.LotteryRGType;
import com.inso.modules.passport.MyConstants;

/**
 *
 */
public class RGPeriodStatus {

    private static Log LOG = LogFactory.getLog(RGPeriodStatus.class);

    public static final int EXPIRES = 3600 * 5;
    private static final String CACHE_KEY = MyConstants.DEFAULT_GAME_MODULE_NAME + "_lottery_period_status_";

    /*** 用户总投注 ***/
    private static final String CACHE_KEY_USER_BET_MONEY = MyConstants.DEFAULT_GAME_MODULE_NAME + "_lottery_period_status_user_bet_money_";

    private static LRUCache<String, RGPeriodStatus> mLruCache = new LRUCache<>(LotteryRGType.values().length * 2);


    private LotteryRGType type;
    /*** 期号 ***/
    private String issue;
    /*** 开奖数字 ***/
    private long openResult = -1;
    /*** 每期最多投注金额 ***/
    private float maxMoneyOfIssue;
    /*** 当前投注总额 ***/
    private float currentBetMoneyOfIssue;
    /*** 每个用户最多投注金额 ***/
    private float maxMoneyOfUser;
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

    public static RGPeriodStatus loadCache(boolean purge, LotteryRGType type, String issue)
    {
        if(type == null || StringUtils.isEmpty(issue))
        {
            throw new RuntimeException("type or issue is null");
        }
        String uniqueKey = type.getKey() + issue;
        String cachekey = CACHE_KEY + uniqueKey;

        RGPeriodStatus status = mLruCache.get(uniqueKey);


        if(purge || status == null)
        {
            if(mRateLimiter.tryAcquire(1, 3, TimeUnit.SECONDS))
            {
                status = mLruCache.get(uniqueKey);
                if(status == null)
                {
                    status = CacheManager.getInstance().getObject(cachekey, RGPeriodStatus.class);
                }

                if(status == null)
                {
                    status = new RGPeriodStatus();
                    status.setType(type);
                    status.setIssue(issue);
                }

                mLruCache.put(uniqueKey, status);
            }
        }

        return status;
    }

    public static RGPeriodStatus tryLoadCache(boolean purge, LotteryRGType type, String issue)
    {
        String uniqueKey = type.getKey() + issue;
        String cachekey = CACHE_KEY + uniqueKey;

        RGPeriodStatus status = mLruCache.get(uniqueKey);

        if(purge || status == null)
        {
            status = CacheManager.getInstance().getObject(cachekey, RGPeriodStatus.class);
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

//    public void increAmount(String username, long amountValue)
//    {
//        String cackey = CACHE_KEY_USER_BET_MONEY + username + issue;
//        long userBetMoney = CacheManager.getInstance().getLong(cackey);
//        userBetMoney += amountValue;
//        CacheManager.getInstance().setString(cackey, userBetMoney + StringUtils.getEmpty());
//
//        this.currentBetMoneyOfIssue += amountValue;
//    }

    public void incre(String username, String betItem, BigDecimal basicAmount, long betCountValue, BigDecimal betAmount, BigDecimal feemoney)
    {
        init();

        LotteryRgBetItemType betItemType = LotteryRgBetItemType.getType(betItem);

        // 如果中奖能中多少
        BigDecimal winAmountIfRealized = null;
        if(betItemType == LotteryRgBetItemType.RED || betItemType == LotteryRgBetItemType.GREEN)
        {
            // 1 - 9 都可以
            if(betItemType == LotteryRgBetItemType.RED)
            {
                winAmountIfRealized = LotteryHelper.calcWinMoney(basicAmount, betCountValue, 2, betItem);
                updateBetItem(betAmount, winAmountIfRealized, feemoney, "2", true);
                updateBetItem(betAmount, winAmountIfRealized, feemoney, "4", true);
                updateBetItem(betAmount, winAmountIfRealized, feemoney, "6", true);
                updateBetItem(betAmount, winAmountIfRealized, feemoney, "8", true);

                BigDecimal vioLetAmount = LotteryHelper.calcWinMoney(basicAmount, betCountValue, 0, betItem);
                updateBetItem(betAmount, vioLetAmount, feemoney, "0", true);
            }
            else
            {
                winAmountIfRealized = LotteryHelper.calcWinMoney(basicAmount, betCountValue, 1, betItem);
                updateBetItem(betAmount, winAmountIfRealized, feemoney, "1", true);
                updateBetItem(betAmount, winAmountIfRealized, feemoney, "3", true);
                updateBetItem(betAmount, winAmountIfRealized, feemoney, "7", true);
                updateBetItem(betAmount, winAmountIfRealized, feemoney, "9", true);

                BigDecimal vioLetAmount = LotteryHelper.calcWinMoney(basicAmount, betCountValue, 5, betItem);
                updateBetItem(betAmount, vioLetAmount, feemoney, "5", true);
            }

        }
        else if(betItemType == LotteryRgBetItemType.VIOLET)
        {
            // 0 - 5
            winAmountIfRealized = LotteryHelper.calcWinMoney(basicAmount, betCountValue, 0, betItem);

            //
            updateBetItem(betAmount, winAmountIfRealized, feemoney, "0", true);
            updateBetItem(betAmount, winAmountIfRealized, feemoney, "5", true);
        }
        else
        {
            // 数字
            long openResult = StringUtils.asLong(betItem);
            winAmountIfRealized = LotteryHelper.calcWinMoney(basicAmount, betCountValue, openResult, betItem);
        }

        // 投注金额累计
        // 当前投注
        updateBetItem(betAmount, winAmountIfRealized, feemoney, betItem, false);
//        RealtimeBetItemReport rgRealtimeBetItem = (RealtimeBetItemReport)mBetItemReportMap.get(betItem);
//        rgRealtimeBetItem.incre(betAmount, winAmountIfRealized, feemoney);


        // 更新当前用户
        float currentUserBetMoney = betAmount.floatValue();

        String cackey = CACHE_KEY_USER_BET_MONEY + username + issue;
        long userBetMoney = CacheManager.getInstance().getLong(cackey);
        userBetMoney += currentUserBetMoney;
        CacheManager.getInstance().setString(cackey, userBetMoney + StringUtils.getEmpty());

        this.currentBetMoneyOfIssue += currentUserBetMoney;
    }

    private void updateBetItem(BigDecimal betAmount, BigDecimal winAmount, BigDecimal feemoney, String betItem, boolean onlyUpdateWinAmount)
    {
        RealtimeBetItemReport rgRealtimeBetItem = (RealtimeBetItemReport)mBetItemReportMap.get(betItem);
        rgRealtimeBetItem.incre(betAmount, winAmount, feemoney, onlyUpdateWinAmount);
    }

    private void init()
    {
        if(CollectionUtils.isEmpty(mBetItemReportList))
        {
            this.mBetItemReportList = new ArrayList<>(13);

            for(int i = 0; i < 10; i ++)
            {
                String key = StringUtils.getEmpty() + i;
                mBetItemReportList.add(new RealtimeBetItemReport(key));
            }

            mBetItemReportList.add(new RealtimeBetItemReport(LotteryRgBetItemType.RED.getKey()));
            mBetItemReportList.add(new RealtimeBetItemReport(LotteryRgBetItemType.GREEN.getKey()));
            mBetItemReportList.add(new RealtimeBetItemReport(LotteryRgBetItemType.VIOLET.getKey()));
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

    public ErrorResult verify(String username, float betMoney)
    {
        ErrorResult result = verifyTime();
        if(result != SystemErrorResult.SUCCESS)
        {
            return result;
        }

        // 总投注限制
        if(maxMoneyOfIssue > 0 && maxMoneyOfIssue - currentBetMoneyOfIssue < betMoney)
        {
            return GameErrorResult.ERR_LIMIT_TOTAL_AMOUNT;
        }

        // 用户投注总额限制
        if(maxMoneyOfUser > 0)
        {
            String cackey = CACHE_KEY_USER_BET_MONEY + username + issue;
            long userBetMoney = CacheManager.getInstance().getLong(cackey);
            if(userBetMoney > maxMoneyOfUser)
            {
                return GameErrorResult.ERR_LIMIT_USER_AMOUNT;
            }
        }


        return SystemErrorResult.SUCCESS;
    }


    @JSONField(serialize = false, deserialize = false)
    public List getBetItemReportList()
    {
        init();
        BigDecimal totalBetAmount = new BigDecimal(currentBetMoneyOfIssue);

        RealtimeBetItemReport redReport = mBetItemReportMap.get(LotteryRgBetItemType.RED.getKey());
        redReport.update(totalBetAmount, null, null);

        RealtimeBetItemReport greenReport = mBetItemReportMap.get(LotteryRgBetItemType.GREEN.getKey());
        greenReport.update(totalBetAmount, null, null);

        RealtimeBetItemReport violetReport = mBetItemReportMap.get(LotteryRgBetItemType.VIOLET.getKey());
        violetReport.update(totalBetAmount, null, null);

        for(int i = 0; i < 10; i ++)
        {
            String key = i + StringUtils.getEmpty();
            RealtimeBetItemReport report = mBetItemReportMap.get(key);
            if(i == 0 || i == 5)
            {
                report.update(totalBetAmount, null, violetReport);
                continue;
            }

            // green
            if(i == 1 || i == 3 || i == 7 || i == 9)
            {
                report.update(totalBetAmount, greenReport, violetReport);
                continue;
            }

            // red
            if(i == 2 || i == 4 || i == 6 || i == 8)
            {
                report.update(totalBetAmount, redReport, violetReport);
                continue;
            }
        }
        return mBetItemReportList;
    }

    public LotteryRGType getType() {
        return type;
    }

    public void setType(LotteryRGType type) {
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

    public long getOpenResult() {
        return openResult;
    }

    public void setOpenResult(long openResult) {
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
        RGPeriodStatus status = RGPeriodStatus.loadCache(false, LotteryRGType.PARITY, key);

        BigDecimal basicAmount = new BigDecimal("10");
        long basicCount = 1;
        BigDecimal betAmount = basicAmount.multiply(new BigDecimal(basicCount));

        BigDecimal feemoney = new BigDecimal(1);

        status.incre("u1", "Red", basicAmount, basicCount, betAmount, feemoney);
//        status.incre("u2", "Red", basicAmount, basicCount, betAmount, feemoney);
        status.incre("u1", "1", basicAmount, basicCount, betAmount, feemoney);

        status.saveCache();

        RGPeriodStatus cacheStatus = RGPeriodStatus.loadCache(true, LotteryRGType.PARITY, key);
        cacheStatus.log();
    }

}
