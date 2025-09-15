package com.inso.modules.game.lottery_game_impl;

import com.alibaba.druid.util.LRUCache;
import com.alibaba.fastjson.annotation.JSONField;
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
import com.inso.modules.game.lottery_game_impl.football.model.FootballType;
import com.inso.modules.game.model.RealtimeBetItemReport;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class NewLotteryPeriodStatus {

    private static Log LOG = LogFactory.getLog(NewLotteryPeriodStatus.class);

    public static final int EXPIRES = 3600 * 5;
    public static final String CACHE_PERIOD_KEY = NewLotteryPeriodStatus.class.getName() + "_period_";

    /*** 用户总投注 ***/
    public static final String CACHE_USER_KEY = NewLotteryPeriodStatus.class.getName() + "_user_";

    private static LRUCache<String, NewLotteryPeriodStatus> mLruCache = new LRUCache<>(100);


    private transient GameChildType mGameType;
    private String type;
    /*** 期号 ***/
    private String issue;

    /*** 开奖数字 ***/
    private String openResult = StringUtils.getEmpty();
    private int openIndex = -1;

    /*** 每期最多投注金额 ***/
    private float limitMaxMoneyOfIssue;
    /*** 每个用户最多投注金额 ***/
    private float limitMaxMoneyOfUser;

    /*** 当前投注总额 ***/
    private transient AtomicDouble currentBetMoneyOfIssue;
    private float mTotalBetAmount;


    /*** 开盘时间 ***/
    private Date startTime;
    /*** 结束时间 ***/
    private Date endTime;
    private int disableSeconds;

    /*** 是否是 ***/
    private boolean isInit = false;

    /*** 投注数据汇总 ***/
    private List<RealtimeBetItemReport> mBetItemReportList;
    private Map<String, RealtimeBetItemReport> mBetItemReportMap = Maps.newHashMap();

    private static RateLimiter mRateLimiter = RateLimiter.create(5);
    private static NewLotteryPeriodStatus mEmpty = new NewLotteryPeriodStatus();

    static {
        mEmpty.empty = true;
    }

    private boolean empty = false;

    public static NewLotteryPeriodStatus loadCache(boolean purge, GameChildType type, String issue)
    {
        if(!type.autoBoot())
        {
            return mEmpty;
        }
        if(type == null || StringUtils.isEmpty(issue))
        {
            throw new RuntimeException("type or issue is null");
        }
        String uniqueKey = type.getKey() + issue;
        String cachekey = CACHE_PERIOD_KEY + uniqueKey;

        NewLotteryPeriodStatus status = mLruCache.get(uniqueKey);


        if(purge || status == null)
        {
            if(mRateLimiter.tryAcquire(1, 3, TimeUnit.SECONDS))
            {
                status = mLruCache.get(uniqueKey);
                if(status == null)
                {
                    status = CacheManager.getInstance().getObject(cachekey, NewLotteryPeriodStatus.class);
                }

                if(status == null)
                {
                    status = new NewLotteryPeriodStatus();
                    status.setType(type.getKey());
                    status.setIssue(issue);
                }

                mLruCache.put(uniqueKey, status);
            }
        }

        return status;
    }

//    public static NewLotteryPeriodStatus tryLoadResultCache(GameChildType type, String issue)
//    {
//        NewLotteryPeriodStatus status = tryLoadCache(false, type, issue);
//
//        if(status == null || status.getOpenIndex() >= 0)
//        {
//            return status;
//        }
//
//        long ts = System.currentTimeMillis() - status.getEndTime().getTime();
//        if(ts < type.getRefreshMillis())
//        {
//            status = tryLoadCache(true, type, issue);
//        }
//        return status;
//    }

    public static NewLotteryPeriodStatus tryLoadCache(boolean purge, GameChildType type, String issue)
    {
        if(!type.autoBoot())
        {
            return mEmpty;
        }
        String uniqueKey = type.getKey() + issue;
        String cachekey = CACHE_PERIOD_KEY + uniqueKey;

        NewLotteryPeriodStatus status = mLruCache.get(uniqueKey);

        if(purge || status == null)
        {
            status = CacheManager.getInstance().getObject(cachekey, NewLotteryPeriodStatus.class);
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
        if(this.empty)
        {
            return;
        }

        this.isInit = true;
        String cachekey = CACHE_PERIOD_KEY + type + issue;

        String value = FastJsonHelper.jsonEncode(this);
        CacheManager.getInstance().setString(cachekey, value, EXPIRES);
    }

    public void incre(String username, BigDecimal totalAmount, String[] betItemArr, BigDecimal betAmount, BigDecimal feemoney)
    {
        if(this.empty)
        {
            return;
        }

        init();

        BaseLotterySupport processor = MyLotteryManager.getInstance().getOpenProcessor(mGameType);

        for(String betItem : betItemArr)
        {
            betItem = betItem.toLowerCase();
            processor.handleStatsBetItem(username, mGameType, mBetItemReportMap, betItem, betAmount, feemoney);
        }

        // 更新当前用户
        float currentUserBetMoney = totalAmount.floatValue();

        String cackey = CACHE_USER_KEY + username + issue;
        long userBetMoney = CacheManager.getInstance().getLong(cackey);
        userBetMoney += currentUserBetMoney;
        CacheManager.getInstance().setString(cackey, userBetMoney + StringUtils.getEmpty());

        //
        double newValue = this.getCurrentBetMoneyOfIssue().addAndGet(currentUserBetMoney);
        this.mTotalBetAmount = (float) newValue;
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
            BaseLotterySupport processor = MyLotteryManager.getInstance().getOpenProcessor(mGameType);
            this.mBetItemReportList = processor.initRealBetItemReport(false);
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
        if( !(time >= startTime.getTime() && time <= (endTime.getTime() - mGameType.getDisableMilliSeconds())))
        {
            // 封盘
            return GameErrorResult.ERR_CURRENT_ISSUE_FINISH;
        }
        return SystemErrorResult.SUCCESS;
    }

    public ErrorResult verify(String username, float betMoney)
    {
        if(empty)
        {
            return SystemErrorResult.SUCCESS;
        }
        ErrorResult result = verifyTime();
        if(result != SystemErrorResult.SUCCESS)
        {
            return result;
        }

        // 总投注限制
        if(limitMaxMoneyOfIssue > 0 && limitMaxMoneyOfIssue - mTotalBetAmount < betMoney)
        {
            return GameErrorResult.ERR_LIMIT_TOTAL_AMOUNT;
        }

        // 用户投注总额限制
        if(limitMaxMoneyOfUser > 0)
        {
            String cackey = CACHE_USER_KEY + username + issue;
            long userBetMoney = CacheManager.getInstance().getLong(cackey);
            if(userBetMoney > limitMaxMoneyOfUser)
            {
                return GameErrorResult.ERR_LIMIT_USER_AMOUNT;
            }
        }
        return SystemErrorResult.SUCCESS;
    }


    @JSONField(serialize = false, deserialize = false)
    public List getBetItemReportList()
    {
        if(mBetItemReportList == null || mBetItemReportList.isEmpty())
        {
            BaseLotterySupport processor = MyLotteryManager.getInstance().getOpenProcessor(mGameType);
            return processor.initRealBetItemReport(true);
        }

        init();
        BigDecimal totalBetAmount = new BigDecimal(mTotalBetAmount);
        for(RealtimeBetItemReport tmp : mBetItemReportList)
        {
            tmp.update(totalBetAmount, null, null);
        }
        return mBetItemReportList;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        this.mGameType = GameChildType.getType(type);
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

    public List<RealtimeBetItemReport> getmBetItemReportList() {
        return mBetItemReportList;
    }

    public void setmBetItemReportList(List<RealtimeBetItemReport> mBetItemReportList) {
        this.mBetItemReportList = mBetItemReportList;
    }


    public int getOpenIndex() {
        return openIndex;
    }

    public void setOpenIndex(int openIndex) {
        this.openIndex = openIndex;
    }

    public float getLimitMaxMoneyOfIssue() {
        return limitMaxMoneyOfIssue;
    }

    public void setLimitMaxMoneyOfIssue(float limitMaxMoneyOfIssue) {
        this.limitMaxMoneyOfIssue = limitMaxMoneyOfIssue;
    }

    public float getLimitMaxMoneyOfUser() {
        return limitMaxMoneyOfUser;
    }

    public void setLimitMaxMoneyOfUser(float limitMaxMoneyOfUser) {
        this.limitMaxMoneyOfUser = limitMaxMoneyOfUser;
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
//        String key = "1120220718429";
//        NewLotteryPeriodStatus status = NewLotteryPeriodStatus.loadCache(false, LotteryRGType.PARITY, key);
//
//        BigDecimal basicAmount = new BigDecimal("10");
//        long basicCount = 1;
//        BigDecimal betAmount = basicAmount.multiply(new BigDecimal(basicCount));
//
//        BigDecimal feemoney = new BigDecimal(1);
//
//        status.saveCache();
//
//        NewLotteryPeriodStatus cacheStatus = NewLotteryPeriodStatus.loadCache(true, LotteryRGType.PARITY, key);
//        cacheStatus.log();
    }

    public String getOpenResult() {
        return openResult;
    }

    public void setOpenResult(String openResult) {
        this.openResult = openResult;
    }

    public int getDisableSeconds() {
        return disableSeconds;
    }

    public void setDisableSeconds(int disableSeconds) {
        this.disableSeconds = disableSeconds;
    }

    public void setCurrentBetMoneyOfIssue(AtomicDouble currentBetMoneyOfIssue) {
        this.currentBetMoneyOfIssue = currentBetMoneyOfIssue;
    }

    public AtomicDouble getCurrentBetMoneyOfIssue() {
        if(currentBetMoneyOfIssue == null)
        {
            currentBetMoneyOfIssue = new AtomicDouble();
        }
        return currentBetMoneyOfIssue;
    }

    public float getmTotalBetAmount() {
        return mTotalBetAmount;
    }

    public void setmTotalBetAmount(float mTotalBetAmount) {
        this.mTotalBetAmount = mTotalBetAmount;
    }
}
