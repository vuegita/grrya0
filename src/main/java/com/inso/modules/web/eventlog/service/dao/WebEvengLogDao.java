package com.inso.modules.web.eventlog.service.dao;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.web.eventlog.model.WebEventLogInfo;
import com.inso.modules.web.eventlog.model.WebEventLogType;

public interface WebEvengLogDao  {



    public void addLog(WebEventLogType logType, String title, String content, String ip, String userAgent, UserInfo agentInfo, String operator);



    public RowPager<WebEventLogInfo> queryScrollPage(PageVo pageVo, WebEventLogType eventLogType, long agentid, WebEventLogType tbTable, String operator, String ignoreOperator);

}
