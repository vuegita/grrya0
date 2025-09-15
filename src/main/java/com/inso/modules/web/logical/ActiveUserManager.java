package com.inso.modules.web.logical;

import java.util.concurrent.atomic.AtomicInteger;

import com.inso.modules.passport.money.service.MoneyOrderService;
import com.inso.modules.web.SystemRunningMode;
import org.joda.time.DateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inso.framework.cache.CacheManager;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.StringUtils;


@Component
public class ActiveUserManager {

    private static Log LOG = LogFactory.getLog(ActiveUserManager.class);

    //

    private static String ROOT_CACHE = ActiveUserManager.class.getName() + "_online";

    private static String HISTORY_ACTIVE = ActiveUserManager.class.getName() + "_online_active";

    public static boolean isEnable = false;

    private static String DEFAULT_VALUE = "1";

    private static AtomicInteger mCurrentCount = new AtomicInteger();

    @Autowired
    private MoneyOrderService moneyOrderService;

    public void updateHistoryActive(boolean forceUpdate)
    {
        try {
            if(SystemRunningMode.isCryptoMode())
            {
               return;
            }

            if(!forceUpdate && CacheManager.getInstance().exists(HISTORY_ACTIVE))
            {
                return;
            }

            //昨日活跃人数
            StringBuilder historyActiveUserCountOfBeforeDay = new StringBuilder();
            historyActiveUserCountOfBeforeDay.append("昨日 = ").append(moneyOrderService.countActive(false, 1));
            historyActiveUserCountOfBeforeDay.append("  |  3日 = ").append(moneyOrderService.countActive(false, 3));
            historyActiveUserCountOfBeforeDay.append("  |  7日 = ").append(moneyOrderService.countActive(false, 7));
            historyActiveUserCountOfBeforeDay.append("  |  15日 = ").append(moneyOrderService.countActive(false, 15));
            historyActiveUserCountOfBeforeDay.append("  |  30日 = ").append(moneyOrderService.countActive(false, 30));

            CacheManager.getInstance().setString(HISTORY_ACTIVE, historyActiveUserCountOfBeforeDay.toString(), CacheManager.EXPIRES_DAY);
        } catch (Exception e) {
            LOG.error("handle error:", e);
        }
    }

    public static String getHistoryActive()
    {
        return CacheManager.getInstance().getString(HISTORY_ACTIVE);
    }

    public static void increCount(String username,String agentname,String staffname)
    {
        DateTime nowTime = new DateTime();
        int minutes = nowTime.getMinuteOfHour();
        int segment = minutes / 5;

        String userCacheKey = ROOT_CACHE + "_username_" + username + segment;
        if(!CacheManager.getInstance().exists(userCacheKey))
        {

            CacheManager.getInstance().setString(userCacheKey, DEFAULT_VALUE, 300);
        }
        else
        {
            //LOG.info("========== exist " + username);
            return;
        }

        String countCacheKey = ROOT_CACHE + "_count_" + segment;
        synchronized (ActiveUserManager.class)
        {
            long totalCount = getCount()+1;
            //LOG.info("========== not exist " + username + ", count = " + totalCount);
            CacheManager.getInstance().setString(countCacheKey, totalCount + StringUtils.getEmpty(), 350);

            try{


                String agentCountCacheKey = ROOT_CACHE + "_count_" + segment + agentname;

                long totalagentCount = getAgentOrStaffCount(agentname)+1;
                CacheManager.getInstance().setString(agentCountCacheKey, totalagentCount + StringUtils.getEmpty(), 350);


                String staffCountCacheKey = ROOT_CACHE + "_count_" + segment + staffname;

                long totalstaffCount = getAgentOrStaffCount(staffname)+1;
                CacheManager.getInstance().setString(staffCountCacheKey, totalstaffCount + StringUtils.getEmpty(), 350);


            } catch (Exception e) {
               LOG.error("add agent or staff active count error:", e);
            }
    }

        updateToday(nowTime,username, agentname, staffname);

    }

    private static void updateToday(DateTime dateTime, String username ,String agentname,String staffname)
    {
        int dayOfYear = dateTime.getDayOfYear();
        String usertodayCacheKey = ROOT_CACHE + "today_active_username_" + username + dayOfYear;
        if(!CacheManager.getInstance().exists(usertodayCacheKey))
        {

            CacheManager.getInstance().setString(usertodayCacheKey, DEFAULT_VALUE, CacheManager.EXPIRES_DAY);
        }
        else
        {
            //LOG.info("========== exist " + username);
            return;
        }

        String countCacheKey = ROOT_CACHE + "today_active_count_" + dayOfYear;
        synchronized (ActiveUserManager.class)
        {
            long totalCount = CacheManager.getInstance().getLong(countCacheKey) + 1;
            //LOG.info("========== not exist " + username + ", count = " + totalCount);
            CacheManager.getInstance().setString(countCacheKey, totalCount + StringUtils.getEmpty(), CacheManager.EXPIRES_DAY);


            try{


                String countAgentCacheKey = ROOT_CACHE + "today_active_count_" + dayOfYear + agentname;

                long totalagentCount = getAgentOrStaffTodaycount(agentname)+1;
                CacheManager.getInstance().setString(countAgentCacheKey, totalagentCount + StringUtils.getEmpty(), CacheManager.EXPIRES_DAY);


                String countStaffCacheKey = ROOT_CACHE + "today_active_count_" + dayOfYear + staffname;

                long totalstaffCount = getAgentOrStaffTodaycount(staffname)+1;
                CacheManager.getInstance().setString(countStaffCacheKey, totalstaffCount + StringUtils.getEmpty(), CacheManager.EXPIRES_DAY);


            } catch (Exception e) {
                LOG.error("add agent or staff active totalCount error:", e);
            }

        }
    }

    /**
     * 获取今日活跃人数
     * @return
     */
    public  static long getTodaycount(){
        DateTime nowTime = new DateTime();
        int dayOfYear = nowTime.getDayOfYear();
        String countCacheKey = ROOT_CACHE + "today_active_count_" + dayOfYear;

        long count = CacheManager.getInstance().getLong(countCacheKey);
        return count;
    }

    /**
     * 获取今日代理或员工活跃人数
     * @return
     */
    public  static long getAgentOrStaffTodaycount(String adminname ){
        DateTime nowTime = new DateTime();
        int dayOfYear = nowTime.getDayOfYear();
        String countCacheKey = ROOT_CACHE + "today_active_count_" + dayOfYear + adminname;

        long count = CacheManager.getInstance().getLong(countCacheKey);
        return count;
    }




    /**
     *   获取当前活跃人数
     * @return
     */
    public static long getCount()
    {
        DateTime nowTime = new DateTime();
        int minutes = nowTime.getMinuteOfHour();
        int segment = minutes / 5;
        String countCacheKey = ROOT_CACHE + "_count_" + segment;

        long count = CacheManager.getInstance().getLong(countCacheKey);
        return count;
    }

    /**
     *   获取代理或员工当前活跃人数
     * @return
     */
    public static long getAgentOrStaffCount(String adminname)
    {
        DateTime nowTime = new DateTime();
        int minutes = nowTime.getMinuteOfHour();
        int segment = minutes / 5;
        String countCacheKey = ROOT_CACHE + "_count_" + segment + adminname;

        long count = CacheManager.getInstance().getLong(countCacheKey);
        return count;
    }



}
