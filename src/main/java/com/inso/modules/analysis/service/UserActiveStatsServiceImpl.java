package com.inso.modules.analysis.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.analysis.model.UserActiveStatsInfo;
import com.inso.modules.analysis.service.dao.UserActiveStatsDao;
import com.inso.modules.passport.user.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserActiveStatsServiceImpl implements UserActiveStatsService{

    @Autowired
    private UserActiveStatsDao mUserActiveStatsDao;

    @Override
    public void addReport(Date pdate, UserActiveStatsInfo report) {
        mUserActiveStatsDao.addReport(pdate, report);
    }

    @Override
    public void delete(Date pdate, long userid, long hour) {
        mUserActiveStatsDao.delete(pdate, userid, hour);
    }

    @Override
    public RowPager<UserActiveStatsInfo> queryScrollPage(PageVo pageVo, UserInfo.UserType userType, long agentid, long staffid, long userid) {
        return mUserActiveStatsDao.queryScrollPage(pageVo, userType, agentid, staffid, userid);
    }
}
