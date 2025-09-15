package com.inso.modules.passport.business;

import com.inso.framework.cache.CacheManager;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.MD5;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.helper.AdminAccountHelper;
import com.inso.modules.admin.helper.AgentAccountHelper;
import org.springframework.stereotype.Component;

@Component
public class WithdrawAuth {

    private static final String ROOT_CACHE = WithdrawAuth.class.getName();


    private static final String ADMIN_CACHE = ROOT_CACHE + "_admin_";
    private static final String AGENT_CACHE = ROOT_CACHE + "_agent_";


    public boolean addAuth(boolean isAdmin, int expires)
    {
        String cachekey = null;
        String account = null;
        if(isAdmin)
        {
            cachekey = ADMIN_CACHE;
            account = AdminAccountHelper.getAdmin().getAccount();
        }
        else
        {
            cachekey = AGENT_CACHE;
            account = AgentAccountHelper.getUsername();
        }
        if(StringUtils.isEmpty(account))
        {
            return false;
        }
        String ip = WebRequest.getRemoteIP();
        cachekey  += MD5.encode(ip + account + WebRequest.getSession().getId());
        CacheManager.getInstance().setString(cachekey, "1", expires);
        return true;
    }


    public boolean verify(boolean isAdmin)
    {
        String cachekey = null;
        String account = null;
        if(isAdmin)
        {
            cachekey = ADMIN_CACHE;
            account = AdminAccountHelper.getAdmin().getAccount();
        }
        else
        {
            cachekey = AGENT_CACHE;
            account = AgentAccountHelper.getUsername();
        }
        if(StringUtils.isEmpty(account))
        {
            return false;
        }
        String ip = WebRequest.getRemoteIP();
        cachekey  += MD5.encode(ip + account + WebRequest.getSession().getId());
        return CacheManager.getInstance().exists(cachekey);
    }



}
