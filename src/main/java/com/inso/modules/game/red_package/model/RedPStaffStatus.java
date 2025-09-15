package com.inso.modules.game.red_package.model;

import java.math.BigDecimal;

import org.joda.time.DateTime;

import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.GameErrorResult;

/**
 * 员工发送红包状态
 */
public class RedPStaffStatus {

    private static final String CACHE_KEY = RedPStaffStatus.class.getName();

    private long staffid;

    /*** 单笔最大金额 ***/
    private BigDecimal maxMoneyOfSingle;
    /*** 每日最大金额 ***/
    private BigDecimal maxMoneyOfDay;
    /*** 发送最大次数每天 ***/
    private long maxCountOfDay;

    /*** ***/
    private BigDecimal currentMoneyOfDay = BigDecimal.ZERO;
    private long currentCountOfDay;


    private transient String mDayOfYear;


    public static RedPStaffStatus loadCache(long staffid)
    {
        if(staffid < 1)
        {
            throw new RuntimeException("type or issue is null");
        }

        String cachekey = CACHE_KEY + staffid;
        RedPStaffStatus status = CacheManager.getInstance().getObject(cachekey, RedPStaffStatus.class);
        if(status == null)
        {
            status = new RedPStaffStatus();
            status.setStaffid(staffid);
            status.mDayOfYear = DateTime.now().getDayOfYear() + StringUtils.getEmpty();
        }
        return status;
    }

    public static RedPStaffStatus tryLoadCache(long staffid)
    {
        String dayOfYear = DateTime.now().getDayOfYear() + StringUtils.getEmpty();
        String cachekey = CACHE_KEY + dayOfYear + staffid;

        RedPStaffStatus status = CacheManager.getInstance().getObject(cachekey, RedPStaffStatus.class);
        if(status == null)
        {
            status = CacheManager.getInstance().getObject(cachekey, RedPStaffStatus.class);
        }
        return status;
    }

    public void saveCache()
    {
        String cachekey = CACHE_KEY + mDayOfYear + staffid;
        CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(this), CacheManager.EXPIRES_DAY);
    }


    /**
     *
     * @param amount 红包金额
     * @return
     */
    public ErrorResult verify(BigDecimal amount)
    {
        if(amount == null)
        {
            return SystemErrorResult.ERR_PARAMS;
        }

        if(amount.compareTo(maxMoneyOfSingle) > 0)
        {
            return GameErrorResult.ERR_REDP_STAFF_LIMIT_MAX_MONEY_OF_SINGLE;
        }

        if(amount.add(currentMoneyOfDay).compareTo(maxMoneyOfDay) > 0 )
        {
            return GameErrorResult.ERR_REDP_STAFF_LIMIT_MAX_MONEY_OF_DAY;
        }

        if(currentCountOfDay + 1 > maxCountOfDay)
        {
            return GameErrorResult.ERR_REDP_STAFF_LIMIT_MAX_COUNT_OF_DAY;
        }

        return SystemErrorResult.SUCCESS;
    }

    public void incre(BigDecimal amount)
    {
        this.currentMoneyOfDay = this.currentMoneyOfDay.add(amount);
        this.currentCountOfDay ++;
    }

    public BigDecimal getMaxMoneyOfSingle() {
        return maxMoneyOfSingle;
    }

    public void setMaxMoneyOfSingle(BigDecimal maxMoneyOfSingle) {
        this.maxMoneyOfSingle = maxMoneyOfSingle;
    }

    public BigDecimal getMaxMoneyOfDay() {
        return maxMoneyOfDay;
    }

    public void setMaxMoneyOfDay(BigDecimal maxMoneyOfDay) {
        this.maxMoneyOfDay = maxMoneyOfDay;
    }

    public long getMaxCountOfDay() {
        return maxCountOfDay;
    }

    public void setMaxCountOfDay(long maxCountOfDay) {
        this.maxCountOfDay = maxCountOfDay;
    }

    public BigDecimal getCurrentMoneyOfDay() {
        return currentMoneyOfDay;
    }

    public void setCurrentMoneyOfDay(BigDecimal currentMoneyOfDay) {
        this.currentMoneyOfDay = currentMoneyOfDay;
    }

    public long getCurrentCountOfDay() {
        return currentCountOfDay;
    }

    public void setCurrentCountOfDay(long currentCountOfDay) {
        this.currentCountOfDay = currentCountOfDay;
    }


    public long getStaffid() {
        return staffid;
    }

    public void setStaffid(long staffid) {
        this.staffid = staffid;
    }
}
