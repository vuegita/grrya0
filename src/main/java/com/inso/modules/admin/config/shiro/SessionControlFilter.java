package com.inso.modules.admin.config.shiro;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;

import com.inso.framework.cache.CacheManager;

/**
 * @Description 记录用户SessionId
 * @Author HBP
 * @Create 2018-12-21 14:29
 */
public class SessionControlFilter extends AccessControlFilter {

    private CacheManager mCache = CacheManager.getInstance();
    private int CACHE_TIME = 24 * 60 * 60; //缓存时间


    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        Subject subject = getSubject(request, response);
        if (!subject.isAuthenticated() && !subject.isRemembered()) {
            //如果没有登录
            return true;
        }

//        Session session = subject.getSession();
//        Admin admin = (Admin) subject.getPrincipal();
//        Serializable sessionId = session.getId();
//        mCache.setString(CacheKeyContants.getAdminSessionKey(admin.getAccount()), FastJsonHelper.jsonEncode(sessionId), CACHE_TIME);
        return true;
    }
}
