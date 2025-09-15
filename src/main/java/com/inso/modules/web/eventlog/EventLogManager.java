package com.inso.modules.web.eventlog;

import com.inso.framework.spring.SpringContextUtils;
import com.inso.modules.web.eventlog.model.WebEventLogType;
import com.inso.modules.web.eventlog.service.WebEventLogService;

public class EventLogManager {

    private WebEventLogService webEventLogService;

    private interface MyInternal {
        public EventLogManager mgr = new EventLogManager();
    }

    private EventLogManager()
    {
        this.webEventLogService = SpringContextUtils.getBean(WebEventLogService.class);
    }

    public static EventLogManager getInstance()
    {
        return MyInternal.mgr;
    }

    public void addAdminLog(WebEventLogType logType, String content)
    {
        webEventLogService.addAdminLog(logType, content);
    }
    public void addAgentLog(WebEventLogType logType, String content)
    {
        webEventLogService.addAgentLog(logType, content);
    }

    public void addMemberLog(WebEventLogType logType, String content, long userid, String remoteip, String userAgent)
    {
        webEventLogService.addMemberLog(logType, content, userid, remoteip, userAgent);
    }
}
