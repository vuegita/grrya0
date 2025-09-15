package com.inso.modules.passport.user.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserVIPInfo;
import com.inso.modules.web.model.VIPInfo;
import com.inso.modules.web.model.VIPType;

import java.util.Date;

public interface UserVIPService {

    public void addVip(UserAttr userAttr, VIPInfo vipInfo, Status status);
    public void updateInfo(UserVIPInfo userVIPInfo, Status status, VIPInfo vipInfo, Date expiresTime);

    public UserVIPInfo findById(boolean purge, long id);
    public UserVIPInfo findByUserId(boolean purge, long userid, VIPType vipType);
    public RowPager<UserVIPInfo> queryScrollPage(PageVo pageVo, long agentid, long staffid, long userid, Status status, VIPType vipType);


}
