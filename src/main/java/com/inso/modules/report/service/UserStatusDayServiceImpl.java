package com.inso.modules.report.service;

import java.util.*;

import com.google.common.collect.Maps;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.modules.game.MyLotteryBetRecordCache;
import com.inso.modules.passport.money.model.UserMoney;
import com.inso.modules.report.cache.UserStatusCacheKeyHelper;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.report.model.UserStatusDay;
import com.inso.modules.report.service.dao.UserStatusDayDao;

@Service
public class UserStatusDayServiceImpl implements UserStatusDayService{

    @Autowired
    private UserStatusDayDao mUserStatusDayDao;

    @Override
    public void addReport(Date pdate, long agentid, String agentname, long staffid, String staffname, UserStatusDay report) {
        mUserStatusDayDao.addReport(pdate, agentid, agentname, staffid, staffname, report);
    }

    @Override
    public void delete(Date pdate, long agentid, long staffid) {
        mUserStatusDayDao.delete(pdate, agentid, staffid);
    }

    @Override
    public RowPager<UserStatusDay> queryScrollPage(PageVo pageVo, long agentid, long staffid) {
        return mUserStatusDayDao.queryScrollPage(pageVo, agentid, staffid);
    }

    @Override
    public UserStatusDay querySubStatsInfoByAgent(boolean purge, long userid, DateTime dateTime, int periodOfDay) {
        String cachekey = UserStatusCacheKeyHelper.querySubStatsInfoByAgent(dateTime.getDayOfYear(), userid, periodOfDay);
        UserStatusDay entity = CacheManager.getInstance().getObject(cachekey, UserStatusDay.class);
        if(purge || entity == null)
        {
            entity = mUserStatusDayDao.querySubStatsInfoByAgent(userid, dateTime);
            if(entity == null)
            {
                entity = new UserStatusDay();
            }
            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(entity));
        }
        return entity;
    }

    @Override
    public List<UserStatusDay> queryListByAgent(boolean purge, long userid, int offset) {
        String cachekey = UserStatusCacheKeyHelper.queryListByAgent(userid);
        List<UserStatusDay> rsPageList = CacheManager.getInstance().getList(cachekey, UserStatusDay.class);
        if(purge || rsPageList == null)
        {
            DateTime dateTime = DateTime.now().minusDays(100);
            rsPageList = mUserStatusDayDao.queryListByAgent(userid, dateTime, 100);
            if(rsPageList == null)
            {
                rsPageList = Collections.emptyList();
            }
            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(rsPageList));
        }

        int rsIndex = 0;
        List rsList = new ArrayList();
        int size = rsPageList.size();
        for(int i = offset; i < size; i ++)
        {
            if(rsIndex >= 10)
            {
                break;
            }
            rsList.add(rsPageList.get(i));
            rsIndex ++;
        }
        return rsList;
    }
}
