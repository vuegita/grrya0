package com.inso.modules.admin.helper;

import com.inso.framework.spring.SpringContextUtils;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.modules.passport.user.model.UserInfo;

public class AgentAccountHelper {

    private static Log LOG = LogFactory.getLog(AgentAccountHelper.class);

    public static String getUsername() {

        try {
            Subject subject = SecurityUtils.getSubject();
            UserInfo merchantInfo = (UserInfo)subject.getPrincipal();
            return merchantInfo.getName();
        } catch (Exception e) {
            LOG.error("getUsername error", e);
            Subject subject = SecurityUtils.getSubject();
            subject.logout();
        }
        return null;
    }

    /**
     * 获取代理或员工代理id
     * @return
     */
    public static long getAdminAgentid()
    {
        Subject subject = SecurityUtils.getSubject();
        UserInfo admin = (UserInfo) subject.getPrincipal();
        long agentid = admin.getAgentid();
        // agentid = 2;
        return agentid;
    }

    public static UserInfo getAdminLoginInfo()
    {
        try {
            Subject subject = SecurityUtils.getSubject();
            UserInfo admin = (UserInfo) subject.getPrincipal();
//            if(admin.getType().equalsIgnoreCase(UserInfo.UserType.AGENT.getKey()))
//            {
//                return admin;
//            }
//
//            UserAttrService userAttrService = SpringContextUtils.getBean(UserAttrService.class);
//            UserAttr userAttr = userAttrService.find(false, admin.getId());
//
//            UserService userService = SpringContextUtils.getBean(UserService.class);
//            UserInfo adminInfo = userService.findByUsername(false, userAttr.getAgentname());
            return admin;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取当前登陆的代理用户
     * @return
     */
    public static UserInfo getAgentInfo()
    {
        try {
            Subject subject = SecurityUtils.getSubject();
            UserInfo admin = (UserInfo) subject.getPrincipal();
            if(admin.getType().equalsIgnoreCase(UserInfo.UserType.AGENT.getKey()))
            {
                return admin;
            }

            UserAttrService userAttrService = SpringContextUtils.getBean(UserAttrService.class);
            UserAttr userAttr = userAttrService.find(false, admin.getId());

            UserService userService = SpringContextUtils.getBean(UserService.class);
            UserInfo adminInfo = userService.findByUsername(false, userAttr.getAgentname());
            return adminInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断当前是不是代理登陆
     * @return
     */
    public static boolean isAgentLogin()
    {
        try {
            Subject subject = SecurityUtils.getSubject();
            UserInfo admin = (UserInfo) subject.getPrincipal();
            UserInfo.UserType userType = UserInfo.UserType.getType(admin.getType());
            return userType == UserInfo.UserType.AGENT;
        } catch (Exception e) {
            LOG.error("isAgentLogin error", e);
        }
        return false;
    }

    public static boolean isAgentOrStafffLogin()
    {
        try {
            Subject subject = SecurityUtils.getSubject();
            Object object =  subject.getPrincipal();
            if(object instanceof UserInfo)
            {
                return true;
            }
        } catch (Exception e) {
            LOG.error("isAgentLogin error", e);
        }
        return false;
    }

}
