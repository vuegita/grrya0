package com.inso.modules.admin.agent;


import javax.servlet.http.HttpServletRequest;

import com.inso.framework.utils.NetUtils;
import com.inso.framework.utils.RegexUtils;
import com.inso.modules.admin.AdminMesageManager;
import com.inso.modules.admin.helper.AgentAccountHelper;
import com.inso.modules.common.WhiteIPManager;
import com.inso.modules.web.eventlog.model.WebEventLogType;
import com.inso.modules.web.eventlog.service.WebEventLogService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.conf.MyConfiguration;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.spring.limit.MyIPRateLimit;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.verify.ResponseImageVerifyCodeUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.AdminErrorResult;
import com.inso.modules.admin.config.shiro.MyUserPwdToken;
import com.inso.modules.admin.config.shiro.ShiroLoginUtils;
import com.inso.modules.admin.core.Constants;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.UserErrorResult;
import com.inso.modules.passport.user.cache.UserInfoCacheKeyUtils;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.model.UserSecret;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserSecretService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.service.ConfigService;

@Controller
@RequestMapping("/alibaba888/agent")
public class AgentLoginController {
    private MyConfiguration conf = MyConfiguration.getInstance();
    private CacheManager cache = CacheManager.getInstance();


    private static final String LOGIN = "admin/agent/login";
    private static final String GOOGLE_LOGIN = "agent/google_login";
    private static final String INDEX = "agent/index/index";
    private static final String WELCOM = "agent/welcome";

    private static final String MERCHANT_INFO_PAGE = "agent/info/info_page";

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private UserSecretService mUserSecretService;

    @Autowired
    private ConfigService mConfigService;

    @Autowired
    private WebEventLogService webEventLogService;

    @Autowired
    private AdminMesageManager mAdminMesageManager;

    /**
     * 跳转登录
     */
    @RequestMapping({"/", "toLogin"})
    public String toLogin(Model model) {
        Subject subject = SecurityUtils.getSubject();

        if(MyEnvironment.isDev())
        if(MyEnvironment.isDev())
        {
            if(subject.isAuthenticated() && ShiroLoginUtils.hashAgentLogin())
            {
                return "redirect:/alibaba888/agent/toIndex";
            }

            MyUserPwdToken token = new MyUserPwdToken("systemagent01", StringUtils.getEmpty(), MyUserPwdToken.Type.AGENT);
           // MyUserPwdToken token = new MyUserPwdToken("systemstaff01", StringUtils.getEmpty(), MyUserPwdToken.Type.AGENT);
           subject.login(token);
            return "redirect:/alibaba888/agent/toIndex";
        }

        String redirectUrl = handleInternalLogin();
        if(!StringUtils.isEmpty(redirectUrl))
        {
            return redirectUrl;
        }

        if(subject.isAuthenticated() && ShiroLoginUtils.hashAdminLogin())
        {
            subject.logout();
            return "redirect:/alibaba888/agent/toLogin";
        }
        return "admin/agent/login";
    }


    /**
     * 登录
     *
     * @return
     */
    @MyIPRateLimit(maxCount = 10)
    @RequestMapping("login")
    @ResponseBody
    public String login() {
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        String googlecode = WebRequest.getString("googlecode");

        if(!MyEnvironment.isDev())
        {
            // google 验证码为空
            if(StringUtils.isEmpty(googlecode))
            {
                if(!ResponseImageVerifyCodeUtils.verifyCode())
                {
                    apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_VERIFY_IMAGE_CODE);
                    return apiJsonTemplate.toJSONString();
                }
            }
        }

        Subject subject = SecurityUtils.getSubject();
        if(subject.isAuthenticated() && ShiroLoginUtils.hashAgentLogin())
        {
            return apiJsonTemplate.toJSONString();
        }
        // 如果admin登陆，则不能登陆, 一个浏览器只能一个登陆
        if(ShiroLoginUtils.hashAdminLogin())
        {
            apiJsonTemplate.setError(-1, "You have login by other!");
            return apiJsonTemplate.toJSONString();
        }

        if (!"XMLHttpRequest".equalsIgnoreCase(WebRequest.getHeader("X-Requested-With"))) {// 不是ajax请求
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYSTEM);
            return apiJsonTemplate.toJSONString();
        }

        String account = WebRequest.getString("account");
        String password = WebRequest.getString("password");
        if (StringUtils.isEmpty(account) || !RegexUtils.isLetterDigit(account) || account.length() > 50) {
            apiJsonTemplate.setJsonResult(AdminErrorResult.NONE_ACCOUNT);
            return apiJsonTemplate.toJSONString();
        }
        if (StringUtils.isEmpty(password) || !RegexUtils.isLetterDigit(account)) {
            apiJsonTemplate.setJsonResult(AdminErrorResult.NONE_PASSWORD);
            return apiJsonTemplate.toJSONString();
        }

        if(!MyEnvironment.isDev()){
            if(UserInfo.DEFAULT_GAME_SYSTEM_AGENT.equalsIgnoreCase(account)){
                apiJsonTemplate.setJsonResult(AdminErrorResult.NONE_PASSWORD);
                return apiJsonTemplate.toJSONString();
            }
            if(UserInfo.DEFAULT_GAME_SYSTEM_STAFF.equalsIgnoreCase(account)){
                apiJsonTemplate.setJsonResult(AdminErrorResult.NONE_PASSWORD);
                return apiJsonTemplate.toJSONString();
            }
        }

        // 输错密码最多5次，5次之后直接禁用
        String inputErrorPwdTimesCacheKey = UserInfoCacheKeyUtils.getInputLoginPwdTimes(account);
        long times = CacheManager.getInstance().getLong(inputErrorPwdTimesCacheKey);
        if(times >= 5)
        {
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_INPUT_LOGIN_PWD_ERR_TIMES);
            return apiJsonTemplate.toJSONString();
        }

        UserSecret secretInfo = mUserSecretService.find(false, account);

        if (secretInfo == null) {
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_ACCOUNT_OR_PWD);
            return apiJsonTemplate.toJSONString();
        }

        if ( !secretInfo.checkLoginPwd(password)) {
            times++;
            CacheManager.getInstance().setString(inputErrorPwdTimesCacheKey, times + StringUtils.getEmpty(), CacheManager.EXPIRES_DAY);
            if(times >= 5)
            {
                // 并且禁用账户
                mUserService.updateStatus(account, Status.DISABLE.getKey(), null);
            }
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_ACCOUNT_OR_PWD);
            return apiJsonTemplate.toJSONString();
        }

        // 用户自己设置google验证 | 系统全局开启google验证
        //String googleValidate = mConfigService.getValueByKey(false, PlatformConfig.ADMIN_APP_PLATFORM_GOOGLE_VALIDATE);
        if(secretInfo.isGoogleLogin())
        {
            if(StringUtils.isEmpty(googlecode))
            {
                apiJsonTemplate.setData(true);
                return apiJsonTemplate.toJSONString();
            }

            if( secretInfo.getGoogleKey() == null || !secretInfo.checkGoogle(apiJsonTemplate, googlecode, false))
            {
                return apiJsonTemplate.toJSONString();
            }
        }

        UserInfo userInfo = mUserService.findByUsername(false, account);
        if (null == userInfo) {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());
        if(!(userType == UserInfo.UserType.AGENT || userType == UserInfo.UserType.STAFF))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_ILEGAL);
            return apiJsonTemplate.toJSONString();
        }

        // 推广员不能登陆
        Status loginAgentStatus = Status.getType(userInfo.getLoginAgentStatus());
        if(loginAgentStatus != Status.ENABLE)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_ILEGAL);
            return apiJsonTemplate.toJSONString();
        }

        // 账户被禁用, 无法登陆
        Status status = Status.getType(userInfo.getStatus());
        if(status != Status.ENABLE)
        {
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_ACCOUNT_DISABLE);
            return apiJsonTemplate.toJSONString();
        }

        // 验证代理是否禁用，如果禁用，整条线都不能登陆
        if(userType == UserInfo.UserType.STAFF && !checkAgentEnable(userInfo))
        {
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_ACCOUNT_DISABLE);
            return apiJsonTemplate.toJSONString();
        }

        WebRequest.getHttpServletRequest().getSession().removeAttribute(Constants.VERIFICATION_CODE);
        apiJsonTemplate.setData(false);
        MyUserPwdToken token = new MyUserPwdToken(userInfo.getName(), StringUtils.getEmpty(), MyUserPwdToken.Type.AGENT);
        subject.login(token);

        webEventLogService.addAgentLog(WebEventLogType.LOGIN, null);


        if(userType == UserInfo.UserType.AGENT)
        {
            mAdminMesageManager.sendAgentLoginMessage(account, WebRequest.getRemoteIP(), null);
        }
        else
        {
            String agentname = AgentAccountHelper.getAgentInfo().getName();
            mAdminMesageManager.sendAgentLoginMessage(agentname, WebRequest.getRemoteIP(), account);
        }




        return apiJsonTemplate.toJSONString();
    }


    private String getSafeCodeValues(HttpServletRequest request) {
        Object safeCode = request.getSession().getAttribute(Constants.VERIFICATION_CODE);
        return safeCode != null ? safeCode.toString() : null;
    }

//    /**
//     * 获取二维码图片
//     */
//    @GetMapping("verCodeImg")
//    @ResponseBody
//    public void getVerificationCode(HttpServletResponse response) {
//        HttpServletRequest request = WebRequest.getHttpServletRequest();
//        try {
//            String code = ValidateCodeUtil.getRandomString();
//            request.getSession().setAttribute(Constants.VERIFICATION_CODE, code);
//
//            response.setContentType("image/jpeg");
//            response.setHeader("Pragma", "no-cache");
//            response.setHeader("Cache-Control", "no-cache");
//            response.setDateHeader("Expires", 0);
//            OutputStream outputStream = response.getOutputStream();
//            ValidateCodeUtil.stringToImage(code, outputStream);
//            outputStream.flush();
//            outputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }



    @RequestMapping("logout")
    public String logout() {
//        WebRequest.getHttpServletRequest().getSession().removeAttribute(Constants.USER_LOGIN_KEY);
//        WebRequest.getHttpServletRequest().getSession().removeAttribute(Constants.ADMIN);

        Subject subject = SecurityUtils.getSubject();
        subject.logout();

        return "redirect:/alibaba888/agent/toLogin";
    }

    private String getUsername() {

        Subject subject = SecurityUtils.getSubject();
        UserInfo merchantInfo = (UserInfo)subject.getPrincipal();
        return merchantInfo.getName();
    }

    @GetMapping("refreshImageVerifyCode")
    @ResponseBody
    public void refreshImageVerifyCode() {
        ResponseImageVerifyCodeUtils.renderValidateImg();
    }

    /**
     * 验证是不是代理
     * @param staffInfo
     * @return
     */
    private boolean checkAgentEnable(UserInfo staffInfo)
    {
        UserAttr userAttr = mUserAttrService.find(false, staffInfo.getId());

        UserInfo agentInfo = mUserService.findByUsername(false, userAttr.getAgentname());
        if(agentInfo == null)
        {
            return false;
        }

        // 必须是代理
        UserInfo.UserType userType = UserInfo.UserType.getType(agentInfo.getType());
        if(userType != UserInfo.UserType.AGENT)
        {
            return false;
        }

        Status status = Status.getType(agentInfo.getStatus());
        return status == Status.ENABLE;
    }

    private String handleInternalLogin()
    {
        String remoteip = WebRequest.getRemoteIP();
        if(!(WhiteIPManager.getInstance().verify(remoteip) || NetUtils.isLocalHost(remoteip)))
        {
            return null;
        }

        String accessKey = WebRequest.getString("accessKey");
        String username = AgentLoginHelper.getAgentLoginName(accessKey);
        if(StringUtils.isEmpty(accessKey) || StringUtils.isEmpty(username))
        {
            return null;
        }

        UserInfo userInfo = mUserService.findByUsername(false, username);
        if(userInfo == null)
        {
            return null;
        }
        UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());
        if(userType == UserInfo.UserType.AGENT || userType == UserInfo.UserType.STAFF)
        {
            Subject subject = SecurityUtils.getSubject();

            MyUserPwdToken token = new MyUserPwdToken(username, StringUtils.getEmpty(), MyUserPwdToken.Type.AGENT);
            subject.login(token);
            return "redirect:/alibaba888/agent/toIndex";
        }
        return null;
    }
}
