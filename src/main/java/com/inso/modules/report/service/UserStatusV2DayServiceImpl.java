package com.inso.modules.report.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.report.cache.UserStatusV2CacheKeyHelper;
import com.inso.modules.report.model.UserStatusV2Day;
import com.inso.modules.report.service.dao.UserStatusV2DayDao;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class UserStatusV2DayServiceImpl implements UserStatusV2DayService{

    @Autowired
    private UserStatusV2DayDao mUserStatusV2DayDao;

    @Override
    public void addLog(Date date, UserStatusV2Day statusV2Day) {
        mUserStatusV2DayDao.addLog(date, statusV2Day);
    }

    @Override
    public void delete(Date date, UserStatusV2Day statusV2Day) {
        mUserStatusV2DayDao.delete(date, statusV2Day);
    }

    @Override
    public UserStatusV2Day findByUserid(boolean purge, DateTime dateTime, long userid) {
        String cachekey = UserStatusV2CacheKeyHelper.findByUserid(dateTime.getDayOfYear(), userid);
        UserStatusV2Day statusV2Day = CacheManager.getInstance().getObject(cachekey, UserStatusV2Day.class);
        if(purge || statusV2Day == null)
        {
            statusV2Day = mUserStatusV2DayDao.findByUserid(dateTime, userid);
            if(statusV2Day != null)
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(statusV2Day));
            }
        }
        return statusV2Day;
    }

    @Override
    public UserStatusV2Day queryByUserid(boolean purge, int typeHour, long userid) {
        String cachekey = UserStatusV2CacheKeyHelper.queryByUserid(typeHour, userid);
        UserStatusV2Day statusV2Day = CacheManager.getInstance().getObject(cachekey, UserStatusV2Day.class);
        if(purge || statusV2Day == null)
        {
            DateTime fromTime = DateTime.now().minusHours(typeHour);
            statusV2Day = mUserStatusV2DayDao.queryByUser(fromTime, userid);
            if(statusV2Day != null)
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(statusV2Day));
            }
        }
        return statusV2Day;
    }

    @Override
    public List<UserStatusV2Day> queryListByUser(boolean purge, DateTime fromTime, long userid) {
        String cachekey = UserStatusV2CacheKeyHelper.queryListByUserid(fromTime, userid);
        List<UserStatusV2Day> rsList = CacheManager.getInstance().getList(cachekey, UserStatusV2Day.class);
        if(purge || rsList == null)
        {
            rsList = mUserStatusV2DayDao.queryListByUser(fromTime, userid);
            if(rsList == null)
            {
                rsList = Collections.emptyList();
            }
            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(rsList));
        }
        return rsList;
    }

    @Override
    public RowPager<UserStatusV2Day> queryScrollPage(PageVo pageVo, long agentid, long staffid, long userid) {
        return mUserStatusV2DayDao.queryScrollPage(pageVo, agentid, staffid, userid);
    }
}
