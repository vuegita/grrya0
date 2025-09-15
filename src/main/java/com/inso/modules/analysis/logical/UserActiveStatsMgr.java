package com.inso.modules.analysis.logical;

import com.alibaba.druid.util.LRUCache;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.analysis.model.UserActiveStatsType;
import org.joda.time.DateTime;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 用户活跃统计
 */
public class UserActiveStatsMgr {

    private static String ROOT_CACHE = UserActiveStatsMgr.class.getName();

    private interface MyInternal {
        public UserActiveStatsMgr mgr = new UserActiveStatsMgr();
    }

    private LRUCache<String, Long> mLRUCache = new LRUCache<>(100);

    private static Long DEFAULT_DURATION = Long.valueOf(0);

    private long dayOfYear;
    private long hourOfDay;


    private UserActiveStatsMgr()
    {
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
        pool.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                refreshData();
            }
        }, 0, 60, TimeUnit.SECONDS);

        refreshData();
    }

    public static UserActiveStatsMgr getInstance()
    {
        return MyInternal.mgr;
    }

    private void refreshData()
    {
        DateTime dateTime = DateTime.now();
        this.dayOfYear = dateTime.getDayOfYear();
        this.hourOfDay = dateTime.getHourOfDay();
    }


    public void addStats(String username, UserActiveStatsType type, long duration)
    {
        String key = username + dayOfYear + hourOfDay + type.getKey();
        String cachekey = ROOT_CACHE + key;

        long rs = mLRUCache.getOrDefault(key, DEFAULT_DURATION);

        if(rs <= 0)
        {
            rs = CacheManager.getInstance().getLong(cachekey);
        }
        rs += duration;

        mLRUCache.put(key, rs);
        CacheManager.getInstance().setString(cachekey, rs + StringUtils.getEmpty(), CacheManager.EXPIRES_DAY);
    }

    public long getStats(String username, UserActiveStatsType type, long dayOfYear, long hourOfDay)
    {
        String key = username + dayOfYear + hourOfDay + type.getKey();
        String cachekey = ROOT_CACHE + key;
        return CacheManager.getInstance().getLong(cachekey);
    }

    private long getCurrentStats(String username, UserActiveStatsType type)
    {
        String key = username + dayOfYear + hourOfDay + type.getKey();
        String cachekey = ROOT_CACHE + key;
        return CacheManager.getInstance().getLong(cachekey);
    }

    public static void main(String[] args) {

        String username = "test01";
        UserActiveStatsType type = UserActiveStatsType.STAY_RG_DURATION;

        UserActiveStatsMgr mgr = UserActiveStatsMgr.getInstance();
        mgr.addStats(username, type, 10);

        System.out.println(mgr.getCurrentStats(username, type));
    }



}
