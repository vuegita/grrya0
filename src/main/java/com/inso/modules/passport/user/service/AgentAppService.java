package com.inso.modules.passport.user.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.AgentAppInfo;
import com.inso.modules.passport.user.model.UserInfo;

public interface AgentAppService {

    public void add(UserInfo userInfo, String approveNotifyUrl, Status status);
    public void updateInfo(long agentid, String approveNotifyUrl, Status modifySecret, Status status);
    public AgentAppInfo findByAgentId(boolean purge, long agentid);
    public RowPager<AgentAppInfo> queryScrollPage(PageVo pageVo, long agentid, Status status);

}
