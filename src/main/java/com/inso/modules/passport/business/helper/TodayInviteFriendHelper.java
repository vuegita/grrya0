package com.inso.modules.passport.business.helper;

import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.StringUtils;
import org.joda.time.DateTime;

/**
 * 好友邀请状态
 */
public class TodayInviteFriendHelper {

    private static final String ROOT_CACHE = TodayInviteFriendHelper.class.getName();

    private static final String USER_INVITE_REG_COUNT = ROOT_CACHE + "user_invite_reg_count";

    private static final String USER_INVITE_REG_AND_BUY_VIP_COUNT = ROOT_CACHE + "user_invite_reg_and_buy_count";

    /**
     * 会员今日邀请好友成功个数
     * @param userid
     */
    public static void increReg(long userid)
    {
        if(userid <= 0)
        {
            return;
        }
        String key = userid + StringUtils.getEmpty();
        synchronized (key)
        {
            DateTime dateTime = DateTime.now();
            int dayOfYear = dateTime.getDayOfYear();
            int count = getTodayRegCount(dateTime, userid);
            count += 1;

            String cachekey = USER_INVITE_REG_COUNT + key + dayOfYear;
            CacheManager.getInstance().setString(cachekey, count + StringUtils.getEmpty(), CacheManager.EXPIRES_DAY);
        }
    }

    public static int getTodayRegCount(DateTime dateTime, long userid)
    {
        String cachekey = USER_INVITE_REG_COUNT + userid + dateTime.getDayOfYear();
        Integer rs = CacheManager.getInstance().getObject(cachekey, Integer.class);
        if(rs == null)
        {
            return 0;
        }
        return rs;
    }

    public static int getTodayRegCount(long userid)
    {
        DateTime dateTime = new DateTime();
        return getTodayRegCount(dateTime, userid);
    }


    public static void increRegAndBuy(long userid)
    {
        if(userid <= 0)
        {
            return;
        }
        String key = userid + StringUtils.getEmpty();
        synchronized (key)
        {
            DateTime dateTime = DateTime.now();
            int dayOfYear = dateTime.getDayOfYear();
            int count = getTodayRegAndBuyVipCount(dateTime, userid);
            count += 1;

            String cachekey = USER_INVITE_REG_AND_BUY_VIP_COUNT + key + dayOfYear;
            CacheManager.getInstance().setString(cachekey, count + StringUtils.getEmpty(), CacheManager.EXPIRES_DAY);
        }
    }

    public static int getTodayRegAndBuyVipCount(DateTime dateTime, long userid)
    {
        String cachekey = USER_INVITE_REG_AND_BUY_VIP_COUNT + userid + dateTime.getDayOfYear();
        Integer rs = CacheManager.getInstance().getObject(cachekey, Integer.class);
        if(rs == null)
        {
            return 0;
        }
        return rs;
    }

    public static void main(String[] args) {
        increReg(1);
        increReg(1);
        increReg(1);

        System.out.println(getTodayRegCount(1));
    }

}
