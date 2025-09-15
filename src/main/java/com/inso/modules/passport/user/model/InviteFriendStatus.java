package com.inso.modules.passport.user.model;

import com.inso.framework.cache.CacheManager;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.passport.business.model.PresentBusinessType;
import org.joda.time.DateTime;

import java.math.BigDecimal;

public class InviteFriendStatus {

    private static final String ROOT_CACHE = InviteFriendStatus.class.getName() + "_v2";

    private static final int DEF_EXPRES = 86400 + 100;

    public static final int DEF_LIMIT_LATEST_DAY = MyEnvironment.isDev() ? 1000 : 7;

    private int limitLatestDay = DEF_LIMIT_LATEST_DAY;
    private long inviteCount = 0;
    private long rechargeCount = 0;

    private long receiveCount = 0;

    private BigDecimal historyTotalAmount;


    public static final InviteFriendStatus mDefaultStatus = new InviteFriendStatus();

    public static String getCacheKey(String parentName, DateTime fromTime, DateTime toTime, String extraId, PresentBusinessType presentBusinessType)
    {
        int weekOfYear = fromTime.getWeekOfWeekyear();
        if(presentBusinessType.isDayOnly())
        {
            weekOfYear = fromTime.getDayOfYear();
        }
        extraId = StringUtils.getNotEmpty(extraId) + presentBusinessType.getKey();
        if(toTime != null)
        {
            return ROOT_CACHE + weekOfYear + toTime.getDayOfYear() + parentName + extraId;
        }
        else
        {
            return ROOT_CACHE + weekOfYear + parentName + extraId;
        }

    }

    public void save(String username, DateTime fromTime, DateTime toTime, String extraId, PresentBusinessType presentBusinessType)
    {
        extraId = StringUtils.getNotEmpty(extraId);
        String cachekey = InviteFriendStatus.getCacheKey(username, fromTime, toTime, extraId, presentBusinessType);

        int expires = DEF_EXPRES;
        if(!StringUtils.isEmpty(extraId))
        {
            expires = 3600;
        }
        else if(toTime != null)
        {
            expires = (int)(toTime.getMillis() - fromTime.getMillis()) / 1000 + 7200;
        }

        CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(this), expires);
    }

    public void saveTodayCache(String username, PresentBusinessType presentBusinessType)
    {
        DateTime dateTime = new DateTime();
        save(username, dateTime, null, null, presentBusinessType);
    }

    public static InviteFriendStatus loadWeekCache(String username)
    {
        DateTime dateTime = new DateTime();
        String cachekey = InviteFriendStatus.getCacheKey(username, dateTime, null, StringUtils.getEmpty(), PresentBusinessType.INVITE_WEEK);
        InviteFriendStatus model = loadFromCache(cachekey, false);
        if(model == null)
        {
            return mDefaultStatus;
        }
        return model;
    }

    public static InviteFriendStatus loadFromCache(String cacheKey)
    {
        return loadFromCache(cacheKey, true);
    }

    public static InviteFriendStatus loadFromCacheOrDefault(String cacheKey)
    {
        InviteFriendStatus rs = loadFromCache(cacheKey, false);
        if(rs == null)
        {
            return mDefaultStatus;
        }

        return rs;
    }

    private static InviteFriendStatus loadFromCache(String cacheKey, boolean isInit)
    {
        InviteFriendStatus model = null;
        if(model == null)
        {
            model = CacheManager.getInstance().getObject(cacheKey, InviteFriendStatus.class);
        }
        if(model == null && isInit)
        {
            model = new InviteFriendStatus();
        }
        return model;
    }

    public long getInviteCount() {
        return inviteCount;
    }

    public void setInviteCount(long inviteCount) {
        this.inviteCount = inviteCount;
    }

    public long getRechargeCount() {
        return rechargeCount;
    }

    public void setRechargeCount(long rechargeCount) {
        this.rechargeCount = rechargeCount;
    }

    public void increInviteCount()
    {
        this.inviteCount = this.inviteCount + 1;
    }

    public void increRechargeCount()
    {
        this.rechargeCount = this.rechargeCount + 1;
    }


    public BigDecimal getHistoryTotalAmount() {
        return BigDecimalUtils.getNotNull(historyTotalAmount);
    }

    public void setHistoryTotalAmount(BigDecimal historyTotalAmount) {
        this.historyTotalAmount = historyTotalAmount;
    }

    public int getLimitLatestDay() {
        return limitLatestDay;
    }

    public void setLimitLatestDay(int limitLatestDay) {
        this.limitLatestDay = limitLatestDay;
    }

    public static void main(String[] args) {

        InviteFriendStatus model = InviteFriendStatus.loadWeekCache("u1");

        System.out.println(FastJsonHelper.jsonEncode(model));

        model.setInviteCount(10);

        model = InviteFriendStatus.loadWeekCache("u1");
        System.out.println(FastJsonHelper.jsonEncode(model));

    }

    public long getReceiveCount() {
        return receiveCount;
    }

    public void setReceiveCount(long receiveCount) {
        this.receiveCount = receiveCount;
    }
}
