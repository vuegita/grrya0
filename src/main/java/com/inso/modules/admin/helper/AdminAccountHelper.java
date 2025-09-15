package com.inso.modules.admin.helper;

import com.inso.framework.context.MyEnvironment;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.modules.admin.core.model.Admin;

public class AdminAccountHelper {

    private static Log LOG = LogFactory.getLog(AdminAccountHelper.class);


    public static boolean isSupperAdmin()
    {
        try {
            Subject subject = SecurityUtils.getSubject();
            if(subject.getPrincipal() instanceof Admin) {
                Admin admin = (Admin)subject.getPrincipal();
                return admin.getAccount().equalsIgnoreCase(Admin.DEFAULT_ADMIN_GOPLE) || admin.getAccount().equalsIgnoreCase(Admin.DEFAULT_ADMIN_NY4TIME);
            }
        } catch (Exception e) {
            //LOG.error("isSupperAdmin error", e);
        }
        return false;
    }

    public static boolean isNy4timeAdmin()
    {
        try {
            Subject subject = SecurityUtils.getSubject();
            if(subject.getPrincipal() instanceof Admin)
            {
                Admin admin = (Admin)subject.getPrincipal();
                boolean rs = admin.getAccount().equalsIgnoreCase(Admin.DEFAULT_ADMIN_NY4TIME);
                return rs;
            }
        } catch (Exception e) {
            //LOG.error("isNy4timeAdmin error", e);
        }
        return false;
    }

    public static boolean isNy4timeAdminOrDEV()
    {
        if(MyEnvironment.isDev())
        {
            return true;
        }
        return isNy4timeAdmin();
    }

    public static Admin getAdmin()
    {
        try {
            Subject subject = SecurityUtils.getSubject();
            Admin admin = (Admin)subject.getPrincipal();
            return admin;
        } catch (Exception e) {
            LOG.error("getUsername error", e);
        }
        return null;
    }

}
