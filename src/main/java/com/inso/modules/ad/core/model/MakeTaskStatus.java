package com.inso.modules.ad.core.model;

import com.alibaba.druid.util.LRUCache;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.ad.AdErrorResult;
import com.inso.modules.passport.business.helper.TodayInviteFriendHelper;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 会员今日做任务状态
 */
public class MakeTaskStatus {

    /*** 默认会员不开VIP，最多能做60快, 免费只有10块 ***/

    /*** 缓存1天时间，再加600s ***/
    private static final int DEFAULT_EXPIRES = 86400 + 600;

    private static String ROOT_CACHE = MakeTaskStatus.class.getName() + "_maketaskstatus_";

    private static LRUCache<String, MakeTaskStatus> mLRUCache = new LRUCache<String, MakeTaskStatus>(500);

    /*** 会员id ***/
    private long userid;
    private long mVipLimitId;


    /*** 会员今日已完成总额度-升级后清0 ***/
    private BigDecimal userTotalMoneyOfDay = BigDecimal.ZERO;
    /*** 会员今日已完成免费额度-升级后清0 ***/
    private BigDecimal userFreeMoneyOfDay = BigDecimal.ZERO;


    // 临时变量
    private transient AdVipLimitInfo mVipLimitInfo;
    private transient DateTime mDateTime;
    private transient String mCacheKey;

    public static MakeTaskStatus loadCache(long userid)
    {
        DateTime dateTime = DateTime.now();
        int dayOfYear = dateTime.getDayOfYear();
        String uniqueKey = userid + StringUtils.getEmpty() + dayOfYear;

        MakeTaskStatus status = mLRUCache.get(uniqueKey);
        if(status == null)
        {
            String cachekey = ROOT_CACHE + uniqueKey;
            status = CacheManager.getInstance().getObject(cachekey, MakeTaskStatus.class);
            if(status == null)
            {
                status = new MakeTaskStatus();
            }
            // 初始化
            status.mCacheKey = cachekey;
            status.mDateTime = dateTime;
            status.userid = userid;

            mLRUCache.put(uniqueKey, status);
        }
        return status;
    }

    public void saveCache()
    {
        CacheManager.getInstance().setString(mCacheKey, FastJsonHelper.jsonEncode(this), DEFAULT_EXPIRES);
    }

    public ErrorResult verify(BigDecimal amount)
    {
        return verify_V1(amount);
    }


    public ErrorResult verify_V2(BigDecimal amount)
    {
        //vip0额度限制
//        BigDecimal currentTotalMakeMoney = totalMakeMoney.add(amount);
//        if(VipLevel<1 && currentTotalMakeMoney.compareTo(mVipLimitInfo.getTotalMoneyOfDay())>0){
//
//            return AdErrorResult.ERR_LIMIT_TOTAL_MONEY_OF_DAY;
//        }


        long limitMinInviteCount = mVipLimitInfo.getInviteCountOfDay();

        // 单笔最大金额限制
        if(amount.compareTo(mVipLimitInfo.getMaxMoneyOfSingle()) > 0)
        {
            return AdErrorResult.ERR_LIMIT_MAX_MONEY_OF_SINGLE;
        }

        // 今日可做总免费额度 + 邀请好友额度 + 购买VIP额度
        BigDecimal totayValidAmount = mVipLimitInfo.getFreeMoneyOfDay();

        boolean hasMakeInviteFriend = false;
        // 邀请好友额度
        int todayInviteCount = TodayInviteFriendHelper.getTodayRegCount(mDateTime, userid);
        if(todayInviteCount > 0 && limitMinInviteCount > 0 && mVipLimitInfo.getInviteMoneyOfDay().compareTo(BigDecimal.ZERO) > 0)
        {
            BigDecimal getAmount = BigDecimal.ZERO;
            if(todayInviteCount >= limitMinInviteCount)
            {
                getAmount = mVipLimitInfo.getInviteMoneyOfDay();
                // 如果超过今日邀请上限，
                if(todayInviteCount > limitMinInviteCount)
                {
                    BigDecimal extraAmount = getExtraInviteFriendAmount(todayInviteCount);
                    getAmount = getAmount.add(extraAmount);
                }
            }
            else
            {
                BigDecimal rate = new BigDecimal(todayInviteCount).divide(new BigDecimal(limitMinInviteCount), 2);
                getAmount = rate.multiply(mVipLimitInfo.getInviteMoneyOfDay());
            }
            totayValidAmount = totayValidAmount.add(getAmount);
        }

        // 邀请好友购买VIP额度
        int todayInviteRegAndBuyCount = TodayInviteFriendHelper.getTodayRegAndBuyVipCount(mDateTime, userid);
        if(todayInviteRegAndBuyCount > 0 && mVipLimitInfo.getBuyMoneyOfDay().compareTo(BigDecimal.ZERO) > 0)
        {
            BigDecimal inviteFriendAndBuyVipAmount = mVipLimitInfo.getBuyMoneyOfDay().add(new BigDecimal(todayInviteRegAndBuyCount));
            totayValidAmount = totayValidAmount.add(inviteFriendAndBuyVipAmount);
        }

        // 今日总上限
        BigDecimal currentTotalMoneyOfDay = userTotalMoneyOfDay.add(amount);
        if( currentTotalMoneyOfDay.compareTo(totayValidAmount) > 0)
        {
            if(hasMakeInviteFriend)
            {
                return AdErrorResult.ERR_LIMIT_FREE_MONEY_OF_DAY;
            }
            return AdErrorResult.ERR_LIMIT_FREE_MONEY_OF_DAY;
        }

        return SystemErrorResult.SUCCESS;
    }

    /**
     * 第一版本写法
     * @param amount
     * @return
     */
    @Deprecated
    public ErrorResult verify_V1(BigDecimal amount)
    {
        //vip0额度限制
        long limitMinInviteCount = mVipLimitInfo.getInviteCountOfDay();
//        BigDecimal currentTotalMakeMoney = userTotalMoneyOfDay.add(amount);
//        if(mVipLimitInfo.getVipLevel() < 1 && currentTotalMakeMoney.compareTo(mVipLimitInfo.getTotalMoneyOfDay())>0){
//
//            return AdErrorResult.ERR_LIMIT_TOTAL_MONEY_OF_DAY;
//        }

        // 单笔最大金额限制
        if(amount.compareTo(mVipLimitInfo.getMaxMoneyOfSingle()) > 0)
        {
            return AdErrorResult.ERR_LIMIT_MAX_MONEY_OF_SINGLE;
        }

        // 今日总上限
        BigDecimal currentTotalMoneyOfDay = userTotalMoneyOfDay.add(amount);
        if( currentTotalMoneyOfDay.compareTo(mVipLimitInfo.getTotalMoneyOfDay()) > 0)
        {
            return AdErrorResult.ERR_LIMIT_TOTAL_MONEY_OF_DAY;
        }

        // 免费金额限制
        BigDecimal currentFreeMoneyOfDay = userFreeMoneyOfDay.add(amount);
        int todayInviteCount = TodayInviteFriendHelper.getTodayRegCount(mDateTime, userid);
        if(todayInviteCount < limitMinInviteCount && currentFreeMoneyOfDay.compareTo(mVipLimitInfo.getFreeMoneyOfDay()) > 0)
        {
            return AdErrorResult.ERR_LIMIT_FREE_MONEY_OF_DAY;
        }

        BigDecimal validAmount = mVipLimitInfo.getFreeMoneyOfDay();
        // 邀请好友额度
        if(todayInviteCount >= limitMinInviteCount && mVipLimitInfo.getInviteMoneyOfDay().compareTo(BigDecimal.ZERO) > 0)
        {
            validAmount = validAmount.add(mVipLimitInfo.getInviteMoneyOfDay());
        }

        // 强制购买VIP额度
//        int todayInviteRegAndBuyCount = TodayInviteFriendHelper.getTodayRegAndBuyVipCount(mDateTime, userid);
//        if(todayInviteRegAndBuyCount >= mVipLimitInfo.getBuyCountOfDay() && mVipLimitInfo.getBuyMoneyOfDay().compareTo(BigDecimal.ZERO) > 0)
//        {
//            validAmount = validAmount.add(mVipLimitInfo.getBuyMoneyOfDay());
//        }

        if(currentTotalMoneyOfDay.compareTo(validAmount) > 0)
        {
            return AdErrorResult.ERR_LIMIT_FREE_MONEY_OF_DAY;
        }

        return SystemErrorResult.SUCCESS;
    }


    public void increAmount(BigDecimal amount)
    {
        this.userTotalMoneyOfDay = this.userTotalMoneyOfDay.add(amount);
        this.userFreeMoneyOfDay = this.userFreeMoneyOfDay.add(amount);
    }

    /**
     * 更新配置
     * @param limitInfo
     */
    public void updateConfig(AdVipLimitInfo limitInfo)
    {
        this.mVipLimitInfo = limitInfo;
        if(this.mVipLimitId != limitInfo.getId())
        {
            this.userTotalMoneyOfDay = BigDecimal.ZERO;
            this.userFreeMoneyOfDay = BigDecimal.ZERO;
            this.mVipLimitId = limitInfo.getId();
        }

    }

    /**
     * 获取额度邀请好友额度,等级配置如下
     * <=3   获取额度=Math.min(count, 3) * 1
     * <=5   获取额度= (Math.min(count, 5) - 3) * 0.8
     * <=10   获取额度= (Math.min(count, 10) - 5) * 0.5
     * <=15   获取额度= (Math.min(count, 15) - 10) * 0.3
     * > 15   获取额度= (count - 15) * 0.1
     * @param todayInviteCount
     * @return
     */
    private BigDecimal getExtraInviteFriendAmount(int todayInviteCount)
    {

        BigDecimal extraAmount = BigDecimal.ZERO;
        long limitMinInviteCount = mVipLimitInfo.getInviteCountOfDay();
        BigDecimal rate = BigDecimal.ZERO;
        long rsCount = todayInviteCount - limitMinInviteCount;
        if(rsCount > 0)
        {
            // 这个参数需要根据当地金额来配置
            // 邀请多5个人之内，每个人可得 1, 最多可得3
            rate = BigDecimalUtils.DEF_1;
            BigDecimal tmpAmount = new BigDecimal(Math.min(3, rsCount)).multiply(rate);
            extraAmount = extraAmount.add(tmpAmount);
        }
        if(rsCount > 3)
        {
            rate = new BigDecimal(0.8);
            BigDecimal tmpAmount = new BigDecimal(Math.min(5, rsCount) - 3).multiply(rate);
            extraAmount = extraAmount.add(tmpAmount);
        }
        if(rsCount > 5)
        {
            rate = new BigDecimal(0.5);
            BigDecimal tmpAmount = new BigDecimal(Math.min(10, rsCount) - 5).multiply(rate);
            extraAmount = extraAmount.add(tmpAmount);
        }
        if(rsCount > 10)
        {
            rate = new BigDecimal(0.3);
            BigDecimal tmpAmount = new BigDecimal(Math.min(15, rsCount) - 10).multiply(rate);
            extraAmount = extraAmount.add(tmpAmount);
        }
        if(rsCount > 15)
        {
            rate = new BigDecimal(0.1);
            BigDecimal tmpAmount = new BigDecimal(rsCount - 15).multiply(rate);
            extraAmount = extraAmount.add(tmpAmount);
        }
        return extraAmount;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }


    public BigDecimal getUserTotalMoneyOfDay() {
        return userTotalMoneyOfDay;
    }

    public void setUserTotalMoneyOfDay(BigDecimal userTotalMoneyOfDay) {
        this.userTotalMoneyOfDay = userTotalMoneyOfDay;
    }

    public BigDecimal getUserFreeMoneyOfDay() {
        return userFreeMoneyOfDay;
    }

    public void setUserFreeMoneyOfDay(BigDecimal userFreeMoneyOfDay) {
        this.userFreeMoneyOfDay = userFreeMoneyOfDay;
    }

    public long getmVipLimitId() {
        return mVipLimitId;
    }

    public void setmVipLimitId(long mVipLimitId) {
        this.mVipLimitId = mVipLimitId;
    }

    public static void main(String[] args) {
        MakeTaskStatus status = MakeTaskStatus.loadCache(1);
        AdVipLimitInfo vipInfo = new AdVipLimitInfo();
        vipInfo.setInviteCountOfDay(10);
        status.updateConfig(vipInfo);

        BigDecimal extra = status.getExtraInviteFriendAmount(20).setScale(2, RoundingMode.UP);
        System.out.println(extra);
    }
}
