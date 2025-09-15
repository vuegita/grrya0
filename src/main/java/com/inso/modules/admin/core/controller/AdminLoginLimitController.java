package com.inso.modules.admin.core.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.spring.web.WebRequest;
import com.inso.modules.admin.core.helper.LimitHelper;

/**
 * 系统限制操作
 * */
@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class AdminLoginLimitController {

    private CacheManager mCache = CacheManager.getInstance();

    /**
     * 跳转
     */
    @RequiresPermissions("root_sys_login_limit_list")
    @RequestMapping("root_sys_login_limit")
    public String toLoginLimit(Model model) {
        return "admin/core/login_limit";
    }

    @ResponseBody
    @RequiresPermissions("root_sys_login_limit_list")
    @RequestMapping("/removeAdminLoginLimit")
    public String removeAdminLoginLimit(){
        ApiJsonTemplate template = new ApiJsonTemplate();
        String admin = WebRequest.getString("admin");
        String ip = WebRequest.getString("ip");

        // 后台
        String ipCacheKey = LimitHelper.createIPCacheKey(ip);
        String accountCacheKey = LimitHelper.createAccountCacheKey(admin);
        mCache.delete(ipCacheKey);
        mCache.delete(accountCacheKey);

        return template.toJSONString();
    }

}
