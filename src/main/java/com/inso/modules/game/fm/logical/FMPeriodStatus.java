package com.inso.modules.game.fm.logical;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.druid.util.LRUCache;
import com.alibaba.fastjson.annotation.JSONField;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.GameErrorResult;
import com.inso.modules.game.fm.model.FMType;
import com.inso.modules.passport.MyConstants;
import com.inso.modules.passport.user.model.UserInfo;

/**
 * 理财产品
 */
public class FMPeriodStatus {

    private static int DEFAULT_EXPIERS = CacheManager.EXPIRES_MONTH;

    private static Log LOG = LogFactory.getLog(FMPeriodStatus.class);

    private static final String CACHE_KEY = MyConstants.DEFAULT_GAME_MODULE_NAME + "_financial_mgr_period_status_";

    /*** 用户总投注 ***/
    private static final String CACHE_KEY_USER_BET_MONEY = MyConstants.DEFAULT_GAME_MODULE_NAME + "_financial_mgr_period_status_user_buy_money_";

    private FMType type;
    /*** 期号 ***/
    private long issue;
    /*** 每期最多投注金额 ***/
    private float maxMoneyOfIssue;
    /*** 每期用户每个最多投注金额 ***/
    private float userMaxMoneyOfIssue;
    /*** 每期每个最少投注金额 ***/
    private float userMinMoneyOfIssue;
    /*** 当前投注总额 ***/
    private float currentBetMoneyOfIssue;
//    /*** 每个用户最多投注金额 ***/
//    private float maxMoneyOfUser;
    /*** 开盘时间 ***/
    private Date beginSaleTime;
    /*** 结束时间 ***/
    private Date endSaleTime;
    /*** 是否是 ***/
    private boolean isInit = false;
    /*** 投资期限 ***/
    private long timeHorizon;

    /*** 缓存有效期 ***/
    @JSONField(serialize = false, deserialize = false)
    private int mExpires;

    private static LRUCache<String, FMPeriodStatus> mLruCache = new LRUCache<>(100);

    public static FMPeriodStatus loadCache(boolean purge, FMType type, long issue)
    {
        if(type == null || issue <= 0)
        {
            throw new RuntimeException("type or issue is null");
        }
        String uniqueid = StringUtils.getEmpty() + issue;
        String cachekey = CACHE_KEY + uniqueid;

        FMPeriodStatus status = mLruCache.get(uniqueid);
        if(purge || status == null)
        {
            status = CacheManager.getInstance().getObject(cachekey, FMPeriodStatus.class);
        }
        if(status == null)
        {
            status = new FMPeriodStatus();
            status.setType(type);
            status.setIssue(issue);
            mLruCache.put(uniqueid, status);
        }
        status.init();

        return status;
    }

    public static FMPeriodStatus tryLoadCache(boolean purge, long issue)
    {
        String uniqueid = StringUtils.getEmpty() + issue;
        String cachekey = CACHE_KEY + uniqueid;

        FMPeriodStatus status = mLruCache.get(uniqueid);
        if(purge || status == null)
        {
            status = CacheManager.getInstance().getObject(cachekey, FMPeriodStatus.class);
        }
        if(status != null)
        {
            status.init();
        }
        return status;
    }

    public void saveCache()
    {
        refreshExpires();

        this.isInit = true;
        String cachekey = CACHE_KEY + issue;
        //CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(this), this.mExpires);
        CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(this), DEFAULT_EXPIERS);
    }

    public void clearCache(long issue)
    {

        String cachekey = CACHE_KEY + issue;
        CacheManager.getInstance().delete(cachekey);

    }


    public void incre(UserInfo userInfo, BigDecimal buyAmount,long issue)
    {
        // 更新当前用户
        float currentUserBetMoney = buyAmount.floatValue();
        this.currentBetMoneyOfIssue += currentUserBetMoney;

        //
        String userCacheKey = CACHE_KEY_USER_BET_MONEY + userInfo.getName()+ issue;


        String value = CacheManager.getInstance().getString(userCacheKey);
        BigDecimal totalCurrentUserBetMoney= buyAmount;
        if(!StringUtils.isEmpty(value))
        {
            BigDecimal UserBetMoney=new BigDecimal(value);
            totalCurrentUserBetMoney=UserBetMoney.add(buyAmount);
        }

        CacheManager.getInstance().setString(userCacheKey, totalCurrentUserBetMoney.toString(), mExpires);
    }

    private void init()
    {
    }

    private void refreshExpires()
    {
        this.mExpires = (int)(endSaleTime.getTime() - System.currentTimeMillis()) / 1000 + 300;
        if(this.mExpires<= 0)
        {
            this.mExpires = 100;
        }
    }

    public ErrorResult verifyTime()
    {
        long time = System.currentTimeMillis();
        if( !(time >= beginSaleTime.getTime() && time < endSaleTime.getTime()))
        {
            // 封盘
            return GameErrorResult.ERR_CURRENT_ISSUE_FINISH;
        }
        return SystemErrorResult.SUCCESS;
    }

    public ErrorResult verify(UserInfo userInfo, float buyAmountValue ,long issue)
    {
//        ErrorResult result =verifyTime();
//        if(result != SystemErrorResult.SUCCESS)
//        {
//            return result;
//        }

        //
        if(maxMoneyOfIssue - currentBetMoneyOfIssue < userMinMoneyOfIssue || maxMoneyOfIssue - currentBetMoneyOfIssue<=0){
            return GameErrorResult.ERR_REDP_SALES_LESS_MINIMUM_SALES;
        }

        // 总投注限制
        if(maxMoneyOfIssue > 0 && maxMoneyOfIssue - currentBetMoneyOfIssue < buyAmountValue)
        {
            return GameErrorResult.ERR_LIMIT_TOTAL_AMOUNT;
        }



         //已购买金额
//        String userCacheKey = CACHE_KEY_USER_BET_MONEY + userInfo.getName() + issue;
//        String value = CacheManager.getInstance().getString(userCacheKey);
//        BigDecimal UserBetMoney=new BigDecimal("0");
//        if(!StringUtils.isEmpty(value)){
//            UserBetMoney=new BigDecimal(value);
//        }
//
//
//        if(userMaxMoneyOfIssue - UserBetMoney.floatValue() < buyAmountValue)
//        {
//           // return GameErrorResult.ERR_BET_EXIST;
//            return GameErrorResult.ERR_LIMIT_TOTAL_AMOUNT;
//        }

        return SystemErrorResult.SUCCESS;
    }


    public FMType getType() {
        return type;
    }

    public void setType(FMType type) {
        this.type = type;
    }

    public long getIssue() {
        return issue;
    }

    public void setIssue(long issue) {
        this.issue = issue;
    }

    public boolean isInit() {
        return isInit;
    }

    public void setInit(boolean init) {
        isInit = init;
    }


    public float getMaxMoneyOfIssue() {
        return maxMoneyOfIssue;
    }

    public void setMaxMoneyOfIssue(float maxMoneyOfIssue) {
        this.maxMoneyOfIssue = maxMoneyOfIssue;
    }

    public float getUserMaxMoneyOfIssue() {
        return userMaxMoneyOfIssue;
    }

    public void setUserMaxMoneyOfIssue(float userMaxMoneyOfIssue) {
        this.userMaxMoneyOfIssue = userMaxMoneyOfIssue;
    }

    public float getUserMinMoneyOfIssue() {
        return userMinMoneyOfIssue;
    }

    public void setUserMinMoneyOfIssue(float userMinMoneyOfIssue) {
        this.userMinMoneyOfIssue = userMinMoneyOfIssue;
    }

//    public float getMaxMoneyOfUser() {
//        return maxMoneyOfUser;
//    }
//
//    public void setMaxMoneyOfUser(float maxMoneyOfUser) {
//        this.maxMoneyOfUser = maxMoneyOfUser;
//    }

    public float getCurrentBetMoneyOfIssue() {
        return currentBetMoneyOfIssue;
    }

    public void setCurrentBetMoneyOfIssue(float currentBetMoneyOfIssue) {
        this.currentBetMoneyOfIssue = currentBetMoneyOfIssue;
    }

    public long getTimeHorizon() {
        return timeHorizon;
    }

    public void setTimeHorizon(long timeHorizon) {
        this.timeHorizon = timeHorizon;
    }

    public Date getBeginSaleTime() {
        return beginSaleTime;
    }

    public void setBeginSaleTime(Date beginSaleTime) {
        this.beginSaleTime = beginSaleTime;
    }

    public Date getEndSaleTime() {
        return endSaleTime;
    }

    public void setEndSaleTime(Date endSaleTime) {
        this.endSaleTime = endSaleTime;
    }
}
