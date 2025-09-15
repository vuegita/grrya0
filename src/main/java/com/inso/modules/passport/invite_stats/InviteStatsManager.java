package com.inso.modules.passport.invite_stats;

import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.cache.LRUCache;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.passport.invite_stats.model.InviteStatsInfo;
import com.inso.modules.passport.invite_stats.service.InviteStatsService;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class InviteStatsManager {

    private static Log LOG = LogFactory.getLog(InviteStatsManager.class);

    private static final String ROOT_CACHE = InviteStatsManager.class.getName();


    private static final int DEF_EXPIRES = 86400 + 9600;

    @Autowired
    private UserService mUserService;

    @Autowired
    private InviteStatsService mAccessStatsService;

    private LRUCache<String, InviteStatsInfo> mLRUCache = new LRUCache<>(300);

    private InviteStatsInfo mDef = new InviteStatsInfo();

    public ErrorResult addCount(String key)
    {
        String username = mUserService.findNameByInviteCode(key);
        if(StringUtils.isEmpty(username))
        {
            return SystemErrorResult.ERR_EXIST_NOT;
        }

        UserInfo userInfo = mUserService.findByUsername(false, username);

        if(userInfo == null)
        {
            return SystemErrorResult.ERR_EXIST_NOT;
        }

        long ts = System.currentTimeMillis();
        DateTime dateTime = DateTime.now().withTime(0, 0, 0, 0);
        String uniqueKey = key + dateTime.getDayOfYear();

        Date pdate = dateTime.toDate();

        try {
            synchronized (uniqueKey)
            {
                InviteStatsInfo entityInfo = loadInfo(uniqueKey);
                entityInfo.setTotalCount(entityInfo.getTotalCount() + 1);
                //

                boolean purgeDb = entityInfo.getmPurgeDBTimeTs() <= 0 || ts - entityInfo.getmPurgeDBTimeTs() >= 300_000;
                if(purgeDb)
                {
                    entityInfo.setmPurgeDBTimeTs(ts);
                }
                String cacheKey = ROOT_CACHE + uniqueKey;
                CacheManager.getInstance().setString(cacheKey, FastJsonHelper.jsonEncode(entityInfo), DEF_EXPIRES);

                if(purgeDb)
                {
                    mAccessStatsService.updateInfo(pdate, dateTime.getDayOfYear(), key, userInfo, entityInfo.getTotalCount());
                }
            }
        } catch (Exception e) {
            LOG.error("handle error:", e);
        }

        return SystemErrorResult.SUCCESS;
    }

    private InviteStatsInfo loadInfo(String uniqueKey)
    {
        InviteStatsInfo statsInfo = mLRUCache.get(uniqueKey);
        if(statsInfo != null)
        {
            return statsInfo;
        }
        String cacheKey = ROOT_CACHE + uniqueKey;
        statsInfo = CacheManager.getInstance().getObject(cacheKey, InviteStatsInfo.class);
        if(statsInfo == null)
        {
            statsInfo = new InviteStatsInfo();
        }

        mLRUCache.put(uniqueKey, statsInfo);
        return statsInfo;
    }

    public InviteStatsInfo loadTodayStatsInfo(UserInfo userInfo)
    {
        DateTime dateTime = DateTime.now();
        String key = userInfo.getInviteCode();
        String uniqueKey = key + dateTime.getDayOfYear();

        String cacheKey = ROOT_CACHE + uniqueKey;
        InviteStatsInfo entityInfo = CacheManager.getInstance().getObject(cacheKey, InviteStatsInfo.class);
        if(entityInfo == null)
        {
            return mDef;
        }
        return entityInfo;
    }

    public void doTask(DateTime dateTime)
    {
        try {
            saveToDB(dateTime);
        } catch (Exception e) {
            LOG.error("handle error:", e);
        }
    }

    private void saveToDB(DateTime dateTime)
    {
        DateTime newDateTime = dateTime.withTime(0, 0, 0, 0);
        DateTime toDateTime = newDateTime.plusDays(1).minusSeconds(1);

        Date pdate = newDateTime.toDate();

        mAccessStatsService.queryAll(newDateTime, toDateTime, new Callback<InviteStatsInfo>() {
            @Override
            public void execute(InviteStatsInfo inviteStatsInfo) {

                try {
                    String key = inviteStatsInfo.getKey();
                    String uniqueKey = key + newDateTime.getDayOfYear();

                    String cacheKey = ROOT_CACHE + uniqueKey;
                    InviteStatsInfo entityInfo = CacheManager.getInstance().getObject(cacheKey, InviteStatsInfo.class);
                    if(entityInfo == null)
                    {
                        return;
                    }
                    mAccessStatsService.updateInfo(pdate, key, entityInfo.getTotalCount());
                } catch (Exception e) {
                    LOG.error("handle error:", e);
                }

            }
        });

    }



}
