package com.inso.modules.admin.agent;

import com.inso.modules.admin.helper.AgentAccountHelper;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AgentAuthManager {

    @Autowired
    private UserAttrService mUserAttrService;

    public boolean verifyUserData(long userid)
    {
        UserInfo userInfo = AgentAccountHelper.getAdminLoginInfo();
        UserAttr userAttr = mUserAttrService.find(false, userid);
        if(userAttr == null)
        {
            return false;
        }
        if(AgentAccountHelper.isAgentLogin())
        {

            return userAttr.getAgentid() == userInfo.getId();
        }
        else
        {
            return userAttr.getDirectStaffid() == userInfo.getId();
        }
    }

    public boolean verifyAgentData(long agentid)
    {
        UserInfo userInfo = AgentAccountHelper.getAdminLoginInfo();
        if(AgentAccountHelper.isAgentLogin())
        {
            return agentid == userInfo.getId();
        }
        else
        {
            return false;
        }
    }

    public boolean verifyStaffData(long staffid)
    {
        UserInfo userInfo = AgentAccountHelper.getAdminLoginInfo();
        if(AgentAccountHelper.isAgentLogin())
        {
            UserAttr userAttr = mUserAttrService.find(false, staffid);
            if(userAttr == null)
            {
                return false;
            }
            return userAttr.getAgentid() == userInfo.getId();
        }
        else
        {
            return staffid == userInfo.getId();
        }
    }

}
