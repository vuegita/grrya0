package com.inso.modules.game.fruit.logical;

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
import com.inso.framework.utils.RandomUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.GameErrorResult;
import com.inso.modules.game.fruit.helper.FruitHelper;
import com.inso.modules.game.fruit.model.FruitBetItemType;
import com.inso.modules.game.fruit.model.FruitType;
import com.inso.modules.game.model.RealtimeBetItemReport;
import com.inso.modules.passport.MyConstants;

/**
 *
 */
public class FruitPeriodStatus {

    private static Log LOG = LogFactory.getLog(FruitPeriodStatus.class);

    public static final int EXPIRES = 3600 * 24;
    private static final String CACHE_KEY = MyConstants.DEFAULT_GAME_MODULE_NAME + "_fruit_period_status_";

    /*** 用户总投注 ***/
    private static final String CACHE_KEY_USER_BET_MONEY = MyConstants.DEFAULT_GAME_MODULE_NAME + "_fruit_period_status_user_bet_money_";

    private static LRUCache<String, FruitPeriodStatus> mLruCache = new LRUCache<>(FruitType.values().length * 2);

    private FruitType type;
    /*** 期号 ***/
    private String issue;
    /*** 开奖结果 ***/
    private FruitBetItemType openResult;
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
    /*** 开始位置 ***/
    private long startIndex = -1;

    public FruitPeriodStatus() {
    }

    public List<RealtimeBetItemReport> getmBetItemReportList() {
        return mBetItemReportList;
    }

    public void setmBetItemReportList(List<RealtimeBetItemReport> mBetItemReportList) {
        this.mBetItemReportList = mBetItemReportList;
    }

    /*** 投注数据汇总 ***/
    private List<RealtimeBetItemReport> mBetItemReportList;

    private Map<String, RealtimeBetItemReport> mBetItemReportMap = Maps.newHashMap();

    public static FruitPeriodStatus loadCache(boolean purge, FruitType type, String issue)
    {
        if(type == null || StringUtils.isEmpty(issue))
        {
            throw new RuntimeException("type or issue is null");
        }
        String uniqueKey = type.getKey() + issue;
        String cachekey = CACHE_KEY + uniqueKey;

        // from lru
        FruitPeriodStatus status = mLruCache.get(uniqueKey);

        // from cache
        if(purge || status == null)
        {
            status = CacheManager.getInstance().getObject(cachekey, FruitPeriodStatus.class);
        }

        // new
        if(status == null)
        {
            status = new FruitPeriodStatus();
            status.setType(type);
            status.setIssue(issue);

            mLruCache.put(uniqueKey, status);
        }

        status.init();
        return status;
    }

    public static FruitPeriodStatus tryLoadCache(boolean purge, GameChildType type, String issue)
    {
        String uniqueKey = type.getKey() + issue;
        String cachekey = CACHE_KEY + uniqueKey;

        // from lru
        FruitPeriodStatus status = mLruCache.get(uniqueKey);

        // from cache
        if(purge || status == null)
        {
            status = CacheManager.getInstance().getObject(cachekey, FruitPeriodStatus.class);
        }
        return status;
    }

    public void saveCache()
    {
        this.isInit = true;
        String cachekey = CACHE_KEY + type.getKey() + issue;
        CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(this), EXPIRES);
    }

    public void incre(String username, FruitBetItemType betItemType, BigDecimal basicAmount, long betCountValue, BigDecimal betAmount, BigDecimal feemoney)
    {
        // 如果中奖能中多少
       // BigDecimal winAmountIfRealized = FruitHelper.calcWinMoney(basicAmount, betCountValue, betItemType, betItemType);

        // 如果中奖能中多少
        BigDecimal winAmountIfRealized = null;
        if(betItemType == FruitBetItemType.DW)
        {
            winAmountIfRealized = FruitHelper.calcWinMoney(basicAmount, betCountValue, FruitBetItemType.DW, betItemType);
            updateBetItem(betAmount, BigDecimal.ZERO, feemoney, FruitBetItemType.DW.getKey(), true);

            BigDecimal vioLetAmount  = FruitHelper.calcWinMoney(basicAmount, betCountValue, FruitBetItemType.XDW, betItemType);
            updateBetItem(betAmount, vioLetAmount, feemoney, FruitBetItemType.XDW.getKey(), true);
        }else if(betItemType == FruitBetItemType.SX)
        {
            winAmountIfRealized = FruitHelper.calcWinMoney(basicAmount, betCountValue, FruitBetItemType.SX, betItemType);
            updateBetItem(betAmount, BigDecimal.ZERO, feemoney, FruitBetItemType.SX.getKey(), true);

            BigDecimal vioLetAmount  = FruitHelper.calcWinMoney(basicAmount, betCountValue, FruitBetItemType.XSX, betItemType);
            updateBetItem(betAmount, vioLetAmount, feemoney, FruitBetItemType.XSX.getKey(), true);
        }else if(betItemType == FruitBetItemType.XG)
        {
            winAmountIfRealized = FruitHelper.calcWinMoney(basicAmount, betCountValue, FruitBetItemType.XG, betItemType);
            updateBetItem(betAmount, BigDecimal.ZERO, feemoney, FruitBetItemType.XG.getKey(), true);

            BigDecimal vioLetAmount  = FruitHelper.calcWinMoney(basicAmount, betCountValue, FruitBetItemType.XXG, betItemType);
            updateBetItem(betAmount, vioLetAmount, feemoney, FruitBetItemType.XXG.getKey(), true);
        }else if(betItemType == FruitBetItemType.QQ)
        {
            winAmountIfRealized = FruitHelper.calcWinMoney(basicAmount, betCountValue, FruitBetItemType.QQ, betItemType);
            updateBetItem(betAmount, BigDecimal.ZERO, feemoney, FruitBetItemType.QQ.getKey(), true);

            BigDecimal vioLetAmount  = FruitHelper.calcWinMoney(basicAmount, betCountValue, FruitBetItemType.XQQ, betItemType);
            updateBetItem(betAmount, vioLetAmount, feemoney, FruitBetItemType.XQQ.getKey(), true);
        }else if(betItemType == FruitBetItemType.HZ)
        {
            winAmountIfRealized = FruitHelper.calcWinMoney(basicAmount, betCountValue, FruitBetItemType.HZ, betItemType);
            updateBetItem(betAmount, BigDecimal.ZERO, feemoney, FruitBetItemType.HZ.getKey(), true);

            BigDecimal vioLetAmount  = FruitHelper.calcWinMoney(basicAmount, betCountValue, FruitBetItemType.XHZ, betItemType);
            updateBetItem(betAmount, vioLetAmount, feemoney, FruitBetItemType.XHZ.getKey(), true);
        }else if(betItemType == FruitBetItemType.PG)
        {
            winAmountIfRealized = FruitHelper.calcWinMoney(basicAmount, betCountValue, FruitBetItemType.PG, betItemType);
            updateBetItem(betAmount, BigDecimal.ZERO, feemoney, FruitBetItemType.PG.getKey(), true);

            BigDecimal vioLetAmount  = FruitHelper.calcWinMoney(basicAmount, betCountValue, FruitBetItemType.XPG, betItemType);
            updateBetItem(betAmount, vioLetAmount, feemoney, FruitBetItemType.XPG.getKey(), true);
        }else if(betItemType == FruitBetItemType.JZ)
        {
            winAmountIfRealized = FruitHelper.calcWinMoney(basicAmount, betCountValue, FruitBetItemType.JZ, betItemType);
            updateBetItem(betAmount, BigDecimal.ZERO, feemoney, FruitBetItemType.JZ.getKey(), true);

            BigDecimal vioLetAmount  = FruitHelper.calcWinMoney(basicAmount, betCountValue, FruitBetItemType.XJZ, betItemType);
            updateBetItem(betAmount, vioLetAmount, feemoney, FruitBetItemType.XJZ.getKey(), true);
        }else if(betItemType == FruitBetItemType.NM)
        {
            winAmountIfRealized = FruitHelper.calcWinMoney(basicAmount, betCountValue, FruitBetItemType.NM, betItemType);
            updateBetItem(betAmount, BigDecimal.ZERO, feemoney, FruitBetItemType.NM.getKey(), true);

            BigDecimal vioLetAmount  = FruitHelper.calcWinMoney(basicAmount, betCountValue, FruitBetItemType.XNM, betItemType);
            updateBetItem(betAmount, vioLetAmount, feemoney, FruitBetItemType.XNM.getKey(), true);
        }


        // 投注金额累计
        // 当前投注
        updateBetItem(betAmount, winAmountIfRealized, feemoney, betItemType.getKey(), false);


        // 投注金额累计
//        RealtimeBetItemReport realtimeBetItemReport = mBetItemReportMap.get(betItemType.getKey());
//        realtimeBetItemReport.incre(betAmount, winAmountIfRealized, feemoney);

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
        RealtimeBetItemReport fruitRealtimeBetItem = (RealtimeBetItemReport)mBetItemReportMap.get(betItem);
        fruitRealtimeBetItem.incre(betAmount, winAmount, feemoney, onlyUpdateWinAmount);
    }

    private void init()
    {
        if(CollectionUtils.isEmpty(mBetItemReportList))
        {
            FruitBetItemType[] betItemTypeArray = FruitBetItemType.values();

            this.mBetItemReportList = new ArrayList<>(betItemTypeArray.length);

            for(FruitBetItemType item : betItemTypeArray)
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

    @JSONField(serialize = false, deserialize = false)
    public List getBetItemReportList()
    {
        init();
        BigDecimal totalBetAmount = new BigDecimal(currentBetMoneyOfIssue);

        FruitBetItemType[] values = FruitBetItemType.values();
        for (FruitBetItemType tmp : values)
        {
            RealtimeBetItemReport andarReport = mBetItemReportMap.get(tmp.getKey());
            andarReport.update(totalBetAmount, null, null);
            if(tmp==FruitBetItemType.DW){
                RealtimeBetItemReport Report = mBetItemReportMap.get(FruitBetItemType.XDW.getKey());
                Report.update(totalBetAmount, null, null);
            }
        }

        return mBetItemReportList;
    }



    public FruitType getType() {
        return type;
    }

    public void setType(FruitType type) {
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

    public FruitBetItemType getOpenResult() {
        return openResult;
    }

    public void setOpenResult(FruitBetItemType openResult) {
        this.openResult = openResult;
        if(openResult== FruitBetItemType.QQ){
            this.setStartIndex(0);
        }else if(openResult== FruitBetItemType.PG){
            int n=RandomUtils.nextInt(4);
            if(n==0){
                this.setStartIndex(1);
            }else if(n==1){
                this.setStartIndex(7);
            }else if(n==2){
                this.setStartIndex(13);
            }else if(n==3){
                this.setStartIndex(19);
            }else{
                this.setStartIndex(1);
            }
        }else if(openResult== FruitBetItemType.XNM){
            this.setStartIndex(2);
        }else if(openResult== FruitBetItemType.NM){
            int n=RandomUtils.nextInt(2);
            if(n==0){
                this.setStartIndex(3);
            }else if(n==1){
                this.setStartIndex(15);
            }else{
                this.setStartIndex(3);
            }
        }else if(openResult== FruitBetItemType.SX){
            this.setStartIndex(4);
        }else if(openResult== FruitBetItemType.XSX){
            this.setStartIndex(5);
        }else if(openResult== FruitBetItemType.TAKEALL){
            int n=RandomUtils.nextInt(2);
            if(n==0){
                this.setStartIndex(6);
            }else if(n==1){
                this.setStartIndex(18);
            }else{
                this.setStartIndex(6);
            }
        }else if(openResult== FruitBetItemType.XHZ){
            this.setStartIndex(8);
        }else if(openResult== FruitBetItemType.JZ){
            int n=RandomUtils.nextInt(2);
            if(n==0){
                this.setStartIndex(9);
            }else if(n==1){
                this.setStartIndex(21);
            }else{
                this.setStartIndex(9);
            }
        }else if(openResult== FruitBetItemType.HZ){
            int n=RandomUtils.nextInt(2);
            if(n==0){
                this.setStartIndex(10);
            }else if(n==1){
                this.setStartIndex(22);
            }else{
                this.setStartIndex(10);
            }
        }else if(openResult== FruitBetItemType.XDW){
            this.setStartIndex(11);
        }else if(openResult== FruitBetItemType.DW){
            this.setStartIndex(12);
        }else if(openResult== FruitBetItemType.XPG){
            this.setStartIndex(14);
        }else if(openResult== FruitBetItemType.XG){
            this.setStartIndex(16);
        }else if(openResult== FruitBetItemType.XXG){
            this.setStartIndex(17);
        }else if(openResult== FruitBetItemType.XJZ){
            this.setStartIndex(20);
        }else if(openResult== FruitBetItemType.XQQ){
            this.setStartIndex(23);
        }


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
        FruitPeriodStatus status = FruitPeriodStatus.loadCache(false, FruitType.PRIMARY, key);

        BigDecimal basicAmount = new BigDecimal("10");
        long basicCount = 1;
        BigDecimal betAmount = basicAmount.multiply(new BigDecimal(basicCount));

        BigDecimal feemoney = new BigDecimal(1);

        status.incre("u1", FruitBetItemType.DW, basicAmount, basicCount, betAmount, feemoney);
//        status.incre("u1", "Red", basicAmount, basicCount, betAmount, feemoney);
//        status.incre("u1", "1", basicAmount, basicCount, betAmount, feemoney);

        status.saveCache();

        FruitPeriodStatus cacheStatus = FruitPeriodStatus.loadCache(true, FruitType.PRIMARY, key);
        cacheStatus.log();
    }

    public long getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(long startIndex) {
        this.startIndex = startIndex;
    }
}
