package com.inso.modules.passport.domain.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.domain.model.AgentDomainInfo;
import com.inso.modules.passport.user.model.UserAttr;

public interface AgentDomainService {

    public void add(UserAttr userAttr, String url, Status status);

    public void updateInfo(AgentDomainInfo entityInfo, String value, Status status);

    public AgentDomainInfo findByid(long id);
    public AgentDomainInfo findByUrl(boolean purge, String url);

    public void deleteInfo(AgentDomainInfo entityInfo);

    public RowPager<AgentDomainInfo> queryScrollPage(PageVo pageVo, long agentid, Status status);

}
