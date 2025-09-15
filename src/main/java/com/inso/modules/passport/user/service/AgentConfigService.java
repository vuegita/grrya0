package com.inso.modules.passport.user.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.AgentConfigInfo;
import com.inso.modules.passport.user.model.UserInfo;

public interface AgentConfigService {

    public long add(UserInfo agentInfo, AgentConfigInfo.AgentConfigType type, String value, Status status);

    public void updateInfo(AgentConfigInfo entity, String value, Status status);

    public AgentConfigInfo findById(long id);

    public AgentConfigInfo findByAgentId(boolean purge, long agentid, AgentConfigInfo.AgentConfigType type);
    public RowPager<AgentConfigInfo> queryScrollPage(PageVo pageVo, long agentid, AgentConfigInfo.AgentConfigType type, Status status);

}
