package com.inso.modules.passport.user.service.dao;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.AgentAppInfo;
import com.inso.modules.passport.user.model.UserInfo;

public interface AgentAppDao {

    public void add(UserInfo userInfo, String accessKey, String secret, String approveNotifyUrl, Status status);
    public void updateInfo(long agentid, String approveNotifyUrl, String secret, Status status);
    public AgentAppInfo findByAgentId(long agentid);
    public RowPager<AgentAppInfo> queryScrollPage(PageVo pageVo, long agentid, Status status);

}
