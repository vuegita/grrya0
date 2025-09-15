package com.inso.modules.passport.user.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.passport.user.model.SystemFollowType;
import com.inso.modules.passport.user.model.UserInfo;

public interface SystemFollowService {

    public void add(long userid, long agentid,long staffid, SystemFollowType type, String remark);
    public void delete(long userid);
    public RowPager<UserInfo> queryScrollPage(PageVo pageVo, long userid, long agentid, long staffid,SystemFollowType type);

}
