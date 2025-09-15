package com.inso.modules.admin.core.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.google.GoogleUtil;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.AdminErrorResult;
import com.inso.modules.admin.config.PlatformConfig;
import com.inso.modules.admin.core.helper.CoreAdminHelper;
import com.inso.modules.admin.core.model.Admin;
import com.inso.modules.admin.core.model.AdminSecret;
import com.inso.modules.admin.core.service.AdminService;
import com.inso.modules.web.model.ConfigKey;
import com.inso.modules.web.service.ConfigService;


@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class GoogleValidateController {

    @Autowired
    private ConfigService mConfigService;

    @Autowired
    private AdminService mAdminService;


    @RequiresPermissions("root_basic_googleverify_list")
    @RequestMapping("/root_basic_googleverify")
    public String actionToRootBasicGoogleverify(Model model)
    {
        Subject subject = SecurityUtils.getSubject();
        Admin admin = (Admin) subject.getPrincipal();
        model.addAttribute("admin", admin);

        ConfigKey config = mConfigService.findByKey(false, PlatformConfig.ADMIN_APP_PLATFORM_GOOGLE_VALIDATE);
        model.addAttribute("config", config);

        return "admin/core/google_page";
    }

    @RequiresPermissions("root_basic_googleverify_edit")
    @RequestMapping("/updateBasicGoogleVerify")
    @ResponseBody
    public String updateBasicGoogleVerify()
    {
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        String loginAdmin = CoreAdminHelper.getAdminName();

        if(!loginAdmin.equalsIgnoreCase(Admin.DEFAULT_ADMIN_NY4TIME))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_ILEGAL);
            return apiJsonTemplate.toJSONString();
        }

        boolean enable = WebRequest.getBoolean("googleValidate");
        String googleCode = WebRequest.getString("googleCode");

        AdminSecret secret = mAdminService.findAdminSecretByID(loginAdmin);

        String googleKey = secret.getGooglekey();
        if (StringUtils.isEmpty(googleKey) || StringUtils.isEmpty(googleCode)) {
            apiJsonTemplate.setJsonResult(AdminErrorResult.ERR_ERROR_GOOGLE_KEY);
            return apiJsonTemplate.toJSONString();
        }

        if (!GoogleUtil.checkGoogleCode(googleKey, googleCode)) {
            apiJsonTemplate.setJsonResult(AdminErrorResult.ERR_ERROR_GOOGLE_CODE);
            return apiJsonTemplate.toJSONString();
        }

        if(enable)
        {
            mConfigService.updateValue(PlatformConfig.ADMIN_APP_PLATFORM_GOOGLE_VALIDATE, "1");
        }
        else
        {
            mConfigService.updateValue(PlatformConfig.ADMIN_APP_PLATFORM_GOOGLE_VALIDATE, "0");
        }


        // 更新缓存
        mConfigService.findByList(true, "admin_platform_config");
        return apiJsonTemplate.toJSONString();
    }




}
