package com.inso.modules.admin.agent.controller.basic;

import javax.servlet.http.HttpServletResponse;

import com.inso.modules.admin.config.PlatformConfig;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.web.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.conf.MyConfiguration;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.google.GoogleImageUtils;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.StringUtils;
import com.inso.framework.utils.ValidatorUtils;
import com.inso.modules.admin.helper.AgentAccountHelper;
import com.inso.modules.passport.UserErrorResult;
import com.inso.modules.passport.user.model.GoogleStatus;
import com.inso.modules.passport.user.model.UserSecret;
import com.inso.modules.passport.user.service.UserSecretService;
import com.inso.modules.passport.user.service.UserService;

@Controller
@RequestMapping("/alibaba888/agent/basic/security")
public class SecurityController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserSecretService mUserSecretService;

    @Autowired
    private ConfigService mConfigService;

    @RequestMapping("page")
    public String toLogin(Model model) {

        String username = AgentAccountHelper.getUsername();
        boolean isAgentLogin=AgentAccountHelper.isAgentLogin();
        UserSecret secret = mUserSecretService.find(true, username);
        model.addAttribute("loginType", secret.getLogintype());
        model.addAttribute("bindGoogleStatus", secret.getGoogleStatus());
        if(isAgentLogin){
            model.addAttribute("isAgentLogin", "1");
        }
        boolean isNoOtp = mConfigService.getBoolean(false, PlatformConfig.ADMIN_PLATFORM_CONFIG_SMS_AGENT_OTP_SWITCH);
        model.addAttribute("isNoOtp", isNoOtp);

        UserInfo currentLoginInfo = AgentAccountHelper.getAdminLoginInfo();

        if(UserInfo.UserType.STAFF.getKey().equalsIgnoreCase(currentLoginInfo.getType()))
        {
            model.addAttribute("isStaffLogin", "1");

            boolean isStaffNoOtp = mConfigService.getBoolean(false, PlatformConfig.ADMIN_PLATFORM_CONFIG_SMS_STAFF_OTP_SWITCH);
            model.addAttribute("isStaffNoOtp", isStaffNoOtp);
        }



        return "admin/agent/basic/basic_security_page";
    }

    @RequestMapping(value = "updateGoogleStatus")
    @ResponseBody
    public String updateGoogleStatus()
    {
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        String captcha = WebRequest.getString("captcha");
        String username = AgentAccountHelper.getUsername();

        if(StringUtils.isEmpty(captcha))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        UserSecret secret = mUserSecretService.find(false, username);

        if(!secret.checkGoogle(apiJsonTemplate, captcha, false))
        {
            return  apiJsonTemplate.toJSONString();
        }

        if("bind".equalsIgnoreCase(secret.getGoogleStatus()))
        {
            mUserSecretService.updateGoogleInfo(username, GoogleStatus.UNBIND, null);
        }
        else
        {
            mUserSecretService.updateGoogleInfo(username, GoogleStatus.BIND, null);
        }

        return apiJsonTemplate.toJSONString();
    }

    @RequestMapping(value = "updateLoginType")
    @ResponseBody
    public String updateLoginType()
    {
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        String loginVerifyTypeString = WebRequest.getString("loginType");
        String captcha = WebRequest.getString("captcha");

        UserSecret.LoginType loginType = UserSecret.LoginType.getType(loginVerifyTypeString);

        String username = AgentAccountHelper.getUsername();

        if(StringUtils.isEmpty(captcha))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(loginType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        UserSecret secret = mUserSecretService.find(false, username);

        if(!secret.checkGoogle(apiJsonTemplate, captcha))
        {
            return  apiJsonTemplate.toJSONString();
        }

        mUserSecretService.updateLoginType(username, loginType.getKey());
        return apiJsonTemplate.toJSONString();
    }

    @RequestMapping(value = "updateLoginPassword")
    @ResponseBody
    public String updateLoginPassword()
    {
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        String username = AgentAccountHelper.getUsername();

        String oldpwd = WebRequest.getString("oldPassword");
        String newpwd = WebRequest.getString("newPassword");
        String newpwd2 = WebRequest.getString("confirmPassword");

        ApiJsonTemplate api = new ApiJsonTemplate();

        if(!ValidatorUtils.checkPassword(oldpwd))
        {
            api.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return api.toJSONString();
        }

        if(!ValidatorUtils.checkPassword(newpwd, newpwd2))
        {
            api.setJsonResult(UserErrorResult.ERR_PWD);
            return api.toJSONString();
        }

        if(oldpwd.equalsIgnoreCase(newpwd))
        {
            api.setJsonResult(UserErrorResult.ERR_PWD);
            return api.toJSONString();
        }

        UserSecret userSecret = mUserSecretService.find(false, username);
        if(!userSecret.checkLoginPwd(oldpwd))
        {
            api.setJsonResult(UserErrorResult.ERR_PWD);
            return api.toJSONString();
        }

        mUserSecretService.updateLoginPwd(username, newpwd);

        return apiJsonTemplate.toJSONString();
    }

    @RequestMapping(value = "getGoogleKeyEWM")
    @ResponseBody
    public void getGoogleKeyEWM(HttpServletResponse response) {
        String username = AgentAccountHelper.getUsername();
        UserSecret secret = mUserSecretService.find(false, username);

        MyConfiguration conf = MyConfiguration.getInstance();
        String projectName = conf.getString("project.name");

        String name = username + "@a" + projectName  +"-" + MyEnvironment.getEnv();
        String googleKey = secret.getGoogleKey();

        GoogleImageUtils.getGoogleKeyEWM(response, name, googleKey);
    }

    @RequestMapping(value = "getGoogleKeyEWMByAgent")
    @ResponseBody
    public void getGoogleKeyEWMByAgent(HttpServletResponse response) {
        String username = WebRequest.getString("username");

        if(StringUtils.isEmpty(username) || !AgentAccountHelper.isAgentLogin())
        {
            return;
        }

        UserSecret secret = mUserSecretService.find(false, username);

        MyConfiguration conf = MyConfiguration.getInstance();
        String projectName = conf.getString("project.name");

        String name = username + "@a" + projectName  +"-" + MyEnvironment.getEnv();
        String googleKey = secret.getGoogleKey();

        GoogleImageUtils.getGoogleKeyEWM(response, name, googleKey);
    }
}
