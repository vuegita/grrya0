package com.inso.modules.passport.user.service.dao;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserVIPInfo;
import com.inso.modules.web.model.VIPInfo;
import com.inso.modules.web.model.VIPType;

import java.util.Date;

public interface UserVIPDao  {

    public void add(UserAttr userAttr, VIPInfo vipInfo, Status status);
    public void updateInfo(long id, Status status, VIPInfo vipInfo, Date expiresTime);

    public UserVIPInfo findById(long id);
    public UserVIPInfo findByUserId(long userid, VIPType vipType);

    public RowPager<UserVIPInfo> queryScrollPage(PageVo pageVo, long agentid, long staffid, long userid, Status status, VIPType vipType);


}
