package com.inso.modules.game.andar_bahar.logical;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.util.LRUCache;
import com.alibaba.fastjson.annotation.JSONField;
import com.google.common.collect.Maps;
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
import com.inso.modules.game.andar_bahar.helper.ABCardHelper;
import com.inso.modules.game.andar_bahar.helper.ABHelper;
import com.inso.modules.game.andar_bahar.model.ABBetItemType;
import com.inso.modules.game.andar_bahar.model.ABType;
import com.inso.modules.game.model.RealtimeBetItemReport;
import com.inso.modules.passport.MyConstants;

/**
 *
 */
public class ABPeriodStatus {

//    private static Log LOG = LogFactory.getLog(ABPeriodStatus.class);

    public static final int EXPIRES = 3600 * 5 - 500;
    private static final String CACHE_KEY = MyConstants.DEFAULT_GAME_MODULE_NAME + "_andar_bahar_period_status_";

    /*** 用户总投注 ***/
    private static final String CACHE_KEY_USER_BET_MONEY = MyConstants.DEFAULT_GAME_MODULE_NAME + "_andar_bahar_period_status_user_bet_money_";

    private static LRUCache<String, ABPeriodStatus> mLruCache = new LRUCache<>(ABType.values().length * 2);

    private ABType type;
    /*** 期号 ***/
    private String issue;
    /*** 开奖结果 ***/
    private ABBetItemType openResult;
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

    /*** 牌的原始数字 ***/
    private int mCardOriginNumber;
    /*** 15张牌 ***/
    private List<Integer> mCardOriginNumberList;

    public List<RealtimeBetItemReport> getmBetItemReportList() {
        return mBetItemReportList;
    }

    public void setmBetItemReportList(List<RealtimeBetItemReport> mBetItemReportList) {
        this.mBetItemReportList = mBetItemReportList;
    }

    /*** 投注数据汇总 ***/
    private List<RealtimeBetItemReport> mBetItemReportList;

    private Map<String, RealtimeBetItemReport> mBetItemReportMap = Maps.newHashMap();

    public static ABPeriodStatus loadCache(boolean purge, ABType type, String issue)
    {
        if(type == null || StringUtils.isEmpty(issue))
        {
            throw new RuntimeException("type or issue is null");
        }
        String uniqueKey = type.getKey() + issue;
        String cachekey = CACHE_KEY + uniqueKey;

        // from lru
        ABPeriodStatus status = mLruCache.get(uniqueKey);

        // from cache
        if(purge || status == null)
        {
            status = CacheManager.getInstance().getObject(cachekey, ABPeriodStatus.class);
        }

        // new
        if(status == null)
        {
            status = new ABPeriodStatus();
            status.setType(type);
            status.setIssue(issue);

            mLruCache.put(uniqueKey, status);
        }

        status.init();
        return status;
    }

    public static ABPeriodStatus tryLoadCache(boolean purge, GameChildType type, String issue)
    {
        String uniqueKey = type.getKey() + issue;
        String cachekey = CACHE_KEY + uniqueKey;

        // from lru
        ABPeriodStatus status = mLruCache.get(uniqueKey);

        // from cache
        if(purge || status == null)
        {
            status = CacheManager.getInstance().getObject(cachekey, ABPeriodStatus.class);
        }
        return status;
    }

    public void saveCache()
    {
        this.isInit = true;
        String cachekey = CACHE_KEY + type.getKey() + issue;
        CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(this), EXPIRES);
    }

    public void incre(String username, ABBetItemType betItemType, BigDecimal basicAmount, long betCountValue, BigDecimal betAmount, BigDecimal feemoney)
    {
        // 如果中奖能中多少
        BigDecimal winAmountIfRealized = ABHelper.calcWinMoney(basicAmount, betCountValue, betItemType, betItemType);

        // 投注金额累计
        RealtimeBetItemReport realtimeBetItemReport = mBetItemReportMap.get(betItemType.getKey());
        realtimeBetItemReport.incre(betAmount, winAmountIfRealized, feemoney);

        // 更新当前用户
        float currentUserBetMoney = betAmount.floatValue();

        String cackey = CACHE_KEY_USER_BET_MONEY + username + issue;
        long userBetMoney = CacheManager.getInstance().getLong(cackey);
        userBetMoney += currentUserBetMoney;
        CacheManager.getInstance().setString(cackey, userBetMoney + StringUtils.getEmpty());

        this.currentBetMoneyOfIssue += currentUserBetMoney;
    }

    private void init()
    {
        if(CollectionUtils.isEmpty(mBetItemReportList))
        {
            ABBetItemType[] betItemTypeArray = ABBetItemType.values();

            this.mBetItemReportList = new ArrayList<>(betItemTypeArray.length);

            for(ABBetItemType item : betItemTypeArray)
            {
                RealtimeBetItemReport report = new RealtimeBetItemReport(item.getKey());
                mBetItemReportList.add(report);
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
        if( !(time >= startTime.getTime() && time <= (endTime.getTime() - type.getDisableMillis()) ))
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
        ErrorResult result =verifyTime();
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

    /**
     * 必须先设置好开奖结果，才能执行此方法, 只会更新一次
     *
     */
    public void updateCardList()
    {
        if(!CollectionUtils.isEmpty(mCardOriginNumberList))
        {
            return;
        }

        if(openResult == null)
        {
            throw new RuntimeException("Please checking open result !!!");
        }

        // 生成数据
        this.mCardOriginNumberList = ABCardHelper.getCardList(openResult, mCardOriginNumber);

//        LOG.info("issue = " + issue + ", open result = " + openResult.getKey() + ", open origin card = " + mCardOriginNumber);
        ABCardHelper.log(mCardOriginNumber, mCardOriginNumberList);
    }

    /**
     * 开奖结束获取开奖结果从这里获取
     * @return
     */
//    public ABBetItemType getOpenResult()
//    {
//
//    }

    @JSONField(serialize = false, deserialize = false)
    public List getBetItemReportList()
    {
        init();
        BigDecimal totalBetAmount = new BigDecimal(currentBetMoneyOfIssue);

        RealtimeBetItemReport andarReport = mBetItemReportMap.get(ABBetItemType.ANDAR.getKey());
        andarReport.update(totalBetAmount, null, null);

        RealtimeBetItemReport baharReport = mBetItemReportMap.get(ABBetItemType.BAHAR.getKey());
        baharReport.update(totalBetAmount, null, null);

        RealtimeBetItemReport tieReport = mBetItemReportMap.get(ABBetItemType.TIE.getKey());
        tieReport.update(totalBetAmount, null, null);

        return mBetItemReportList;
    }



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

    public ABBetItemType getOpenResult() {
        return openResult;
    }

    public void setOpenResult(ABBetItemType openResult) {
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

    public int getmCardOriginNumber() {
        return mCardOriginNumber;
    }

    public void setmCardOriginNumber(int mCardOriginNumber) {
        this.mCardOriginNumber = mCardOriginNumber;
    }

    public List<Integer> getmCardOriginNumberList() {
        return mCardOriginNumberList;
    }

    public void setmCardOriginNumberList(List<Integer> mCardOriginNumberList) {
        this.mCardOriginNumberList = mCardOriginNumberList;
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
        String key = System.currentTimeMillis() + "";
        ABPeriodStatus status = ABPeriodStatus.loadCache(false, ABType.PRIMARY, key);

        BigDecimal basicAmount = new BigDecimal("10");
        long basicCount = 1;
        BigDecimal betAmount = basicAmount.multiply(new BigDecimal(basicCount));

        BigDecimal feemoney = new BigDecimal(1);

        status.incre("u1", ABBetItemType.ANDAR, basicAmount, basicCount, betAmount, feemoney);
//        status.incre("u1", "Red", basicAmount, basicCount, betAmount, feemoney);
//        status.incre("u1", "1", basicAmount, basicCount, betAmount, feemoney);

        status.saveCache();

        ABPeriodStatus cacheStatus = ABPeriodStatus.loadCache(true, ABType.PRIMARY, key);
        cacheStatus.log();
    }

}
