package com.inso.modules.web.eventlog.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.core.model.Admin;
import com.inso.modules.admin.helper.AdminAccountHelper;
import com.inso.modules.admin.helper.AgentAccountHelper;
import com.inso.modules.common.WhiteIPManager;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.eventlog.model.WebEventLogInfo;
import com.inso.modules.web.eventlog.model.WebEventLogType;
import com.inso.modules.web.eventlog.service.dao.WebEvengLogDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WebEventLogServiceImpl implements WebEventLogService {

    private static Log LOG = LogFactory.getLog(WebEventLogServiceImpl.class);

    @Autowired
    private WebEvengLogDao mEventLogDao;

    @Autowired
    private UserService userService;

    @Autowired
    private UserAttrService userAttrService;


    @Transactional
    public void addAdminLog(WebEventLogType logType, String content)
    {
        try {
            if(!logType.isAdmin())
            {
                return;
            }
            String remoteip = WebRequest.getRemoteIP();


            remoteip = WhiteIPManager.getInstance().getPrivateIPAndReplate(remoteip);

            String userAgent = WebRequest.getHeader("user-agent");

            Admin admin = AdminAccountHelper.getAdmin();

            mEventLogDao.addLog(logType, logType.getName(), content, remoteip, userAgent, null, admin.getAccount());
        } catch (Exception e) {
            LOG.error("addAdmin event log error:", e);
        }
    }

    public void addAgentLog(WebEventLogType logType, String content)
    {
        try {
            if(!logType.isAdmin())
            {
                return;
            }
            String remoteip = WebRequest.getRemoteIP();
            remoteip = WhiteIPManager.getInstance().getPrivateIPAndReplate(remoteip);

            String userAgent = WebRequest.getHeader("user-agent");

            UserInfo agentInfo = AgentAccountHelper.getAgentInfo();
            UserInfo userInfo = AgentAccountHelper.getAdminLoginInfo();

            mEventLogDao.addLog(logType, logType.getName(), content, remoteip, userAgent, agentInfo, userInfo.getName());
        } catch (Exception e) {
            LOG.error("addAgent event log error:", e);
        }
    }

    public void addMemberLog(WebEventLogType logType, String content, long userid, String remoteip, String userAgent)
    {
        try {
            if(logType.isAdmin())
            {
                return;
            }

            if(StringUtils.isEmpty(remoteip))
            {
                remoteip = WebRequest.getRemoteIP();
                userAgent = WebRequest.getHeader("user-agent");
            }

            if(StringUtils.isEmpty(remoteip))
            {
                LOG.error("fetch remote ip empty ...");
                return;
            }

            if(userid <= 0)
            {
                return;
            }

            UserAttr userAttr = userAttrService.find(false, userid);

            UserInfo agentInfo = userService.findByUsername(false, userAttr.getAgentname());
            mEventLogDao.addLog(logType, logType.getName(), content, remoteip, userAgent, agentInfo, userAttr.getUsername());
        } catch (Exception e) {
            LOG.error("addMemberLog event log error:", e);
        }
    }

    public RowPager<WebEventLogInfo> queryScrollPage(PageVo pageVo, WebEventLogType eventLogType, long agentid, WebEventLogType tbTable, String operator, String ignoreOperator)
    {
        return mEventLogDao.queryScrollPage(pageVo, eventLogType, agentid, tbTable, operator, ignoreOperator);
    }

}
