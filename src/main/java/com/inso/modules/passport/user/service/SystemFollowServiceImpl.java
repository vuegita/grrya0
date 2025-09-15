package com.inso.modules.passport.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.passport.user.model.SystemFollowType;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.dao.SystemFollowDao;

@Service
public class SystemFollowServiceImpl implements SystemFollowService{

    @Autowired
    private SystemFollowDao mSystemFollowDao;

    @Override
    public void add(long userid, long agentid,long staffid, SystemFollowType type, String remark) {
        mSystemFollowDao.add(userid, agentid, staffid, type, remark);
    }

    @Override
    public void delete(long userid) {
        mSystemFollowDao.delete(userid);
    }

    @Override
    public RowPager<UserInfo> queryScrollPage(PageVo pageVo, long userid, long agentid, long staffid,SystemFollowType type) {
        return mSystemFollowDao.queryScrollPage(pageVo, userid, agentid, staffid, type);
    }
}
