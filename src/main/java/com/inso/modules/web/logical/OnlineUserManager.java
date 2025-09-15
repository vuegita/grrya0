package com.inso.modules.web.logical;

import com.inso.framework.cache.CacheManager;
import com.inso.framework.cache.LRUCache;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.StringUtils;
import org.joda.time.DateTime;

import java.util.concurrent.atomic.AtomicInteger;

public class OnlineUserManager {

    private static Log LOG = LogFactory.getLog(OnlineUserManager.class);

    //
    private static String ROOT_CACHE = OnlineUserManager.class.getName() + "_online";

    public static boolean isEnable = false;

    private static String DEFAULT_VALUE = "1";

    private static LRUCache<String, AtomicInteger> mLRUCache = new LRUCache<>(10);

    public static void increCount(String username)
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

        String uniqueKey = nowTime.getDayOfYear() + StringUtils.getEmpty() + segment;
        AtomicInteger atomicInteger = mLRUCache.get(uniqueKey);
        if(atomicInteger == null)
        {
            atomicInteger = new AtomicInteger();
            mLRUCache.put(uniqueKey, atomicInteger);
        }
        int value = atomicInteger.incrementAndGet();
        CacheManager.getInstance().setString(ROOT_CACHE + "_count_" + segment, value + StringUtils.getEmpty(), 350);
    }

    public static long getCount()
    {
        DateTime nowTime = new DateTime();
        int minutes = nowTime.getMinuteOfHour();
        int segment = minutes / 5;
        String countCacheKey = ROOT_CACHE + "_count_" + segment;

        long count = CacheManager.getInstance().getLong(countCacheKey);
        return count;
    }

}
