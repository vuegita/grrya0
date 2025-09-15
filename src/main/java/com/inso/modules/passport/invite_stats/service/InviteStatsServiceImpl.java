package com.inso.modules.passport.invite_stats.service;

import com.alibaba.druid.util.LRUCache;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.passport.invite_stats.model.InviteStatsInfo;
import com.inso.modules.passport.invite_stats.service.impl.InviteStatsDao;
import com.inso.modules.passport.user.model.UserInfo;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class InviteStatsServiceImpl implements InviteStatsService {

    private LRUCache<String, String> mLRUCache = new LRUCache<>(500);

    @Autowired
    private InviteStatsDao mAccessStatsDao;

    private static String defValue = "1";

    public boolean add(Date date, int dayOfYear, String key, UserInfo userInfo) {
        try {
            String uniqkey = dayOfYear + key;
            if(mLRUCache.containsKey(uniqkey))
            {
                return true;
            }
            mAccessStatsDao.add(date, key, userInfo);
            mLRUCache.put(uniqkey, defValue);
            return true;
        } catch (DuplicateKeyException e) {
            return true;
        }
    }


    public void updateInfo(Date pdate, int dayOfYear, String key, UserInfo userInfo, long totalCount)
    {
        boolean add = add(pdate, dayOfYear, key, userInfo);
        if(!add)
        {
            return;
        }

        // public void updateInfo(WebAccessStatsInfo entityInfo, Date pdate, WebAccessStatsType statsType, String key);
        mAccessStatsDao.updateInfo(pdate, key, totalCount);
    }

    public void updateInfo(Date pdate, String key, long totalCount)
    {
        mAccessStatsDao.updateInfo(pdate, key, totalCount);
    }

    @Override
    public void queryAll(DateTime fromTime, DateTime toTime, Callback<InviteStatsInfo> callback) {
        mAccessStatsDao.queryAll(fromTime, toTime, callback);
    }

    @Override
    public RowPager<InviteStatsInfo> queryScrollPage(PageVo pageVo, long agentid, long staffid, String key, long userid) {
        return mAccessStatsDao.queryScrollPage(pageVo, agentid, staffid, key, userid);
    }
}
