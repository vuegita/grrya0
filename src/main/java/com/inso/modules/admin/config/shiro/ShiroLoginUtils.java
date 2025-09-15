package com.inso.modules.admin.config.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.inso.modules.admin.core.model.Admin;
import com.inso.modules.passport.user.model.UserInfo;

public class ShiroLoginUtils {


    public static boolean hashAdminLogin()
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated())
        {
            return false;
        }

        Object object = subject.getPrincipal();
        if(object != null && (object instanceof Admin))
        {
            return true;
        }

        return false;
    }

    public static boolean hashAgentLogin()
    {
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated())
        {
            return false;
        }

        Object object = subject.getPrincipal();
        if(object != null && (object instanceof UserInfo))
        {
            return true;
        }

        return false;
    }
}
