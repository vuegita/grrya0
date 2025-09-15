package com.inso.modules.passport.domain.service.dao;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.domain.model.AgentDomainInfo;
import com.inso.modules.passport.user.model.UserAttr;

public interface AgentDomainDao {

    public void add(UserAttr userAttr, String url, Status status);

    public void updateInfo(long id, String url, Status status);

    public AgentDomainInfo findByid(long id);

    public AgentDomainInfo findByUrl(String url);

    public void deleteInfo(long id);

    public RowPager<AgentDomainInfo> queryScrollPage(PageVo pageVo, long agentid, Status status);

}
