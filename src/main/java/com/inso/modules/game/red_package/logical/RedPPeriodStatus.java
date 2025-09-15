package com.inso.modules.game.red_package.logical;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.util.LRUCache;
import com.google.common.collect.Maps;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.GameErrorResult;
import com.inso.modules.game.model.RealtimeBetItemReport;
import com.inso.modules.game.red_package.model.RedPType;
import com.inso.modules.game.rg.helper.LotteryHelper;
import com.inso.modules.passport.MyConstants;

/**
 *
 */
public class RedPPeriodStatus {

    private static Log LOG = LogFactory.getLog(RedPPeriodStatus.class);

    public static final int EXPIRES = 3600 * 24;
    private static final String CACHE_KEY = MyConstants.DEFAULT_GAME_MODULE_NAME + "_red_package_period_bet_status_";

    /*** 用户总投注 ***/
    private static final String CACHE_KEY_USER_BET_MONEY = MyConstants.DEFAULT_GAME_MODULE_NAME + "_red_package_period_status_user_bet_money_";

    private static LRUCache<String, RedPPeriodStatus> mLruCache = new LRUCache<>(100);

    private RedPType type;
    /*** 红包id ***/
    private long issue;
    /*** 开奖结果 ***/
    private long openResult;
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

    public static RedPPeriodStatus loadCache(boolean purge, RedPType type, long issue)
    {
        if(type == null || issue < 1)
        {
            throw new RuntimeException("type or issue is null");
        }

        String uniqueid = type.getKey() + issue;
        String cachekey = CACHE_KEY + uniqueid;

        RedPPeriodStatus status = mLruCache.get(uniqueid);
        if(purge || status == null)
        {
            status = CacheManager.getInstance().getObject(cachekey, RedPPeriodStatus.class);
        }
        if(status == null)
        {
            status = new RedPPeriodStatus();
            status.setType(type);
            status.setIssue(issue);
            mLruCache.put(uniqueid, status);
        }
        status.init();

        return status;
    }

    public static RedPPeriodStatus tryLoadCache(boolean purge, GameChildType type, long issue)
    {
        String uniqueid = type.getKey() + issue;
        String cachekey = CACHE_KEY + uniqueid;

        RedPPeriodStatus status = mLruCache.get(uniqueid);
        if(purge || status == null)
        {
            status = CacheManager.getInstance().getObject(cachekey, RedPPeriodStatus.class);
        }
        return status;
    }

    public void saveCache()
    {
        this.isInit = true;
        String cachekey = CACHE_KEY + type.getKey() + issue;
        CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(this), EXPIRES);
    }

    public void incre(String username, String betItemType, BigDecimal basicAmount, long betCountValue, BigDecimal betAmount, BigDecimal feemoney)
    {
        // 如果中奖能中多少
        BigDecimal winAmountIfRealized = LotteryHelper.calcWinMoney(basicAmount, betCountValue, openResult, betItemType);

        // 投注金额累计
        RealtimeBetItemReport realtimeBetItemReport = mBetItemReportMap.get(betItemType);
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
//        if(CollectionUtils.isEmpty(mBetItemReportList))
//        {
//            ABBetItemType[] betItemTypeArray = ABBetItemType.values();
//
//            this.mBetItemReportList = new ArrayList<>(betItemTypeArray.length);
//
//            for(ABBetItemType item : betItemTypeArray)
//            {
//                RealtimeBetItemReport report = new RealtimeBetItemReport(item.getKey());
//                mBetItemReportList.add(report);
//            }
//        }
//
//        if(mBetItemReportMap.isEmpty())
//        {
//            for(RealtimeBetItemReport item : mBetItemReportList)
//            {
//                mBetItemReportMap.put(item.getOpenResult(), item);
//            }
//        }

    }

    public ErrorResult verifyTime()
    {
        long time = System.currentTimeMillis();
        if( !(time >= startTime.getTime() && time < endTime.getTime() - type.getDisableMillis()))
        {
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


}
