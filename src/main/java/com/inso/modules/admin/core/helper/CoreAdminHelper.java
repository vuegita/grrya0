package com.inso.modules.admin.core.helper;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.inso.modules.admin.core.model.Admin;

public class CoreAdminHelper {


    public static String getAdminName()
    {
        Subject subject = SecurityUtils.getSubject();
        Admin admin = (Admin) subject.getPrincipal();
        return admin.getAccount();
    }

}
