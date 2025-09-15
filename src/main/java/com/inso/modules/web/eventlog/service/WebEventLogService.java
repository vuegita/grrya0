package com.inso.modules.web.eventlog.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.web.eventlog.model.WebEventLogInfo;
import com.inso.modules.web.eventlog.model.WebEventLogType;

public interface WebEventLogService {

    public void addAdminLog(WebEventLogType logType, String content);
    public void addAgentLog(WebEventLogType logType, String content);
    public void addMemberLog(WebEventLogType logType, String content, long userid, String remoteip, String userAgent);


    public RowPager<WebEventLogInfo> queryScrollPage(PageVo pageVo, WebEventLogType eventLogType, long agentid, WebEventLogType tbTable, String operator, String ignoreOperator);

}
