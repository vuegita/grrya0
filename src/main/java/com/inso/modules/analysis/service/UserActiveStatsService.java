package com.inso.modules.analysis.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.analysis.model.UserActiveStatsInfo;
import com.inso.modules.passport.user.model.UserInfo;

import java.util.Date;

public interface UserActiveStatsService {

    public void addReport(Date pdate, UserActiveStatsInfo report);
    public void delete(Date pdate, long userid, long hour);
    public RowPager<UserActiveStatsInfo> queryScrollPage(PageVo pageVo, UserInfo.UserType userType, long agentid, long staffid, long userid);

}
