package com.inso.modules.passport.user.service.dao;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.AgentConfigInfo;
import com.inso.modules.passport.user.model.UserInfo;

public interface AgentConfigDao  {

    public long add(UserInfo agentInfo, AgentConfigInfo.AgentConfigType type, String value, Status status);

    public void updateInfo(long id, String value, Status status);

    public AgentConfigInfo findById(long id);

    public AgentConfigInfo findByAgentId(long agentid, AgentConfigInfo.AgentConfigType type);
    public RowPager<AgentConfigInfo> queryScrollPage(PageVo pageVo, long agentid, AgentConfigInfo.AgentConfigType type, Status status);

}
