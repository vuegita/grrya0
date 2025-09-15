package com.inso.modules.admin.core.controller;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.*;
import com.inso.modules.admin.AdminMesageManager;
import com.inso.modules.admin.core.CoreSafeManager;
import com.inso.modules.web.SystemRunningMode;
import com.inso.modules.web.eventlog.model.WebEventLogType;
import com.inso.modules.web.eventlog.service.WebEventLogService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.google.GoogleUtil;
import com.inso.framework.spring.web.WebRequest;
import com.inso.modules.admin.AdminErrorResult;
import com.inso.modules.admin.config.PlatformConfig;
import com.inso.modules.admin.config.shiro.MyUserPwdToken;
import com.inso.modules.admin.config.shiro.ShiroLoginUtils;
import com.inso.modules.admin.core.Constants;
import com.inso.modules.admin.core.helper.LimitHelper;
import com.inso.modules.admin.core.model.Admin;
import com.inso.modules.admin.core.model.AdminSecret;
import com.inso.modules.admin.core.service.AdminService;
import com.inso.modules.common.WhiteIPManager;
import com.inso.modules.web.service.ConfigService;

/**
 * 后台登录
 *
 * @author XXX
 * @create 2018-11-02 14:37
 */
@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class LoginController {

    private static Log LOG = LogFactory.getLog(LoginController.class);

    @Autowired
    private ConfigService mConfigService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private WebEventLogService webEventLogService;

    @Autowired
    private AdminMesageManager mAdminMesageManager;

    private CacheManager mCache = CacheManager.getInstance();
    /**
     * 跳转登录
     */
    @RequestMapping({"/", "/toLogin"})
    public String toLogin(Model model) {
        Subject subject = SecurityUtils.getSubject();
        String remoteip = WebRequest.getRemoteIP();

        if(MyEnvironment.isDev())
        {
            // 代理如果登陆，则不能登陆
            if(ShiroLoginUtils.hashAgentLogin())
            {
                return "redirect:/alibaba888/agent/toIndex";
            }

            // 开发环境自动登陆
            String account = Admin.DEFAULT_ADMIN_NY4TIME;
            String password = MD5.encode("123456");
            MyUserPwdToken token = new MyUserPwdToken(account, password, MyUserPwdToken.Type.ADMIN);
            subject.login(token);
        }

        if(SystemRunningMode.isCryptoMode())
        {
            if(CoreSafeManager.getInstance().verifyWhiteIPLogin())
            {
                if(!WhiteIPManager.getInstance().verify(remoteip))
                {
                    return "redirect:/alibaba888/admin/toLogin";
                }
            }
        }

        if(subject.isAuthenticated() && ShiroLoginUtils.hashAdminLogin())
        {
            return "redirect:/alibaba888/Liv2sky3soLa93vEr62/toIndex";
        }

        String googleValidate = mConfigService.getValueByKey(true, PlatformConfig.ADMIN_APP_PLATFORM_GOOGLE_VALIDATE);
        model.addAttribute("googleValidate", StringUtils.getNotEmpty(googleValidate));
        return "admin/core/login";
    }

    /**
     * 登录
     *
     * @return
     */
    @RequestMapping("/login")
    @ResponseBody
    public String login(HttpServletRequest request, HttpServletResponse response) {

//        HttpServletResponse response = (HttpServletResponse) res;
        String account = WebRequest.getString("username");
        String password = WebRequest.getString("password");
        String ip = WebRequest.getRemoteIP();

        LOG.info("login info: ip = " + ip + ", account = " + account);

        ApiJsonTemplate template = new ApiJsonTemplate();

        if(StringUtils.isEmpty(account) || !RegexUtils.isLetterDigit(account) || account.length() > 100)
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        if(StringUtils.isEmpty(password) || !RegexUtils.isLetterDigit(password))
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        if(SystemRunningMode.isCryptoMode() || SystemRunningMode.isFundsMode())
        {
            if(CoreSafeManager.getInstance().verifyWhiteIPLogin())
            {
                if(!WhiteIPManager.getInstance().verify(ip))
                {
                    template.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FAILURE);
                    return template.toJSONString();
                }
            }
        }

        if(CoreSafeManager.getInstance().isMaintenance())
        {
            template.setJsonResult(SystemErrorResult.ERR_SYS_MAINTAINED);
            return template.toJSONString();
        }

        Subject subject = SecurityUtils.getSubject();
        if(subject.isAuthenticated() && ShiroLoginUtils.hashAdminLogin())
        {
            return template.toJSONString();
        }
        // 商户如果登陆，则不能登陆
        if(ShiroLoginUtils.hashAgentLogin())
        {
            template.setError(-1, "You have login by Agent!");
            return template.toJSONString();
        }

        if (!"XMLHttpRequest".equalsIgnoreCase(((HttpServletRequest) request).getHeader("X-Requested-With"))) {// 不是ajax请求
            template.setJsonResult(SystemErrorResult.ERR_SYSTEM);
            return template.toJSONString();
        }

        //获取该ip的缓存错误次数, 白名单不检验
        String ipCacheKey = LimitHelper.createIPCacheKey(ip);
        String cacheTimes = mCache.getString(ipCacheKey);
        if (!WhiteIPManager.getInstance().verify(ip) && !StringUtils.isEmpty(cacheTimes)) {
            int times = Integer.parseInt(cacheTimes);
            //该ip的缓存错误次数等于3，加入到黑名单缓存中，并直接返回错误
            if (times >= 3) {
//                request.setAttribute("shiroLoginFailure", "pwdErrorThreeTimes");
//                return true;
            	template.setJsonResult(AdminErrorResult.ERR_ACCOUNTERRORTHERRTIMES);
              return template.toJSONString();
            }
        }

        //获取该账号密码输入错误缓存次数, 白名单不检验
        String accountCacheKey = LimitHelper.createAccountCacheKey(account);
        String cacheErrorAccountTimes = mCache.getString(accountCacheKey);
        if (!WhiteIPManager.getInstance().verify(ip) && !StringUtils.isEmpty(cacheErrorAccountTimes)) {
            int errorAccountTimes = Integer.parseInt(cacheErrorAccountTimes);
            //该账号的缓存错误次数等于3，加入到黑名单缓存中，并直接返回错误
            if (errorAccountTimes >= 3) {
                //输入错误超过三次
            	template.setJsonResult(AdminErrorResult.ERR_ACCOUNTERRORTHERRTIMES);
                return template.toJSONString();
            }
        }

        if (StringUtils.isEmpty(account)) {
//            request.setAttribute("shiroLoginFailure", "accountIsNull");
//            return true;
        	template.setJsonResult(AdminErrorResult.NONE_ACCOUNT);
          return template.toJSONString();
        }
        if (StringUtils.isEmpty(password)) {
//            request.setAttribute("shiroLoginFailure", "passwordIsNull");
//            return true;
           	template.setJsonResult(AdminErrorResult.NONE_ACCOUNT);
            return template.toJSONString();
        }

        //未开启谷歌验证时需要 验证码
        String googleValidate = mConfigService.getValueByKey(false, PlatformConfig.ADMIN_APP_PLATFORM_GOOGLE_VALIDATE);
        if (!MyEnvironment.isDev() && !BooleanUtils.isTrue(googleValidate)) {
            String verCode = request.getParameter("verCode");
            String verificationCodeValues = getVerificationCodeValues(request);
            
            
            if (StringUtils.isEmpty(verCode)) {
              template.setJsonResult(AdminErrorResult.NONE_VERCODE);
              return template.toJSONString();
            }
            
            if(!verCode.equalsIgnoreCase(verificationCodeValues))
            {
            	template.setJsonResult(AdminErrorResult.ERR_VERCODE);
                return template.toJSONString();
            }
        }

        // inple 账户必须是白名单才能登陆
//        if(Admin.DEFAULT_ADMIN_NY4TIME.equalsIgnoreCase(account) && !WhiteIPManager.getInstance().verify(ip))
//        {
//            template.setError(-1, "Account is error1");
//            return template.toJSONString();
//        }

        AdminSecret adminSecret = adminService.findAdminSecretByID(account);
        if (null == adminSecret) {
            template.setError(-1, "Account is error2");
            return template.toJSONString();
        }
        
        if(!adminSecret.checkPassword(password))
        {
        	  //密码错误次数，ip
            if (StringUtils.isEmpty(cacheTimes)) {
                mCache.setString(ipCacheKey, String.valueOf(1), CacheManager.EXPIRES_DAY);
            } else {
                int times = Integer.parseInt(cacheTimes);
                mCache.setString(ipCacheKey, String.valueOf(times + 1), CacheManager.EXPIRES_DAY);
            }
            //密码错误次数，账号
            if (StringUtils.isEmpty(cacheErrorAccountTimes)) {
                mCache.setString(accountCacheKey, String.valueOf(1), CacheManager.EXPIRES_FIVE_MINUTES);
            } else {
                int times = Integer.parseInt(cacheErrorAccountTimes);
                mCache.setString(accountCacheKey, String.valueOf(times + 1), CacheManager.EXPIRES_FIVE_MINUTES);
            }
          template.setJsonResult(AdminErrorResult.ERR_PASSWORD);
          return template.toJSONString();
        }
        
        
        //密码正确清缓存
        if (!StringUtils.isEmpty(cacheTimes)) {
            mCache.delete(ipCacheKey);
            mCache.delete(accountCacheKey);
        }

        String googleCode = request.getParameter("googleCode");
        if (BooleanUtils.isTrue(googleValidate)) {
            if (StringUtils.isEmpty(googleCode)) {
              template.setJsonResult(AdminErrorResult.ERR_NULL_GOOGLE_CODE);
              return template.toJSONString();
            }
            
            String googleKey = adminSecret.getGooglekey();
            if (StringUtils.isEmpty(googleKey)) {
              template.setJsonResult(AdminErrorResult.ERR_ERROR_GOOGLE_KEY);
              return template.toJSONString();
            }

            if (!GoogleUtil.checkGoogleCode(googleKey, googleCode)) {
              template.setJsonResult(AdminErrorResult.ERR_ERROR_GOOGLE_CODE);
              return template.toJSONString();
            }
        }
        
        try {
            MyUserPwdToken token = new MyUserPwdToken(account, password, MyUserPwdToken.Type.ADMIN);
			subject.login(token);
            loginlog(WebRequest.getRemoteIP());
		} catch (Exception e) {
			template.setJsonResult(SystemErrorResult.ERR_SYSTEM);
			return template.toJSONString();
		}

        webEventLogService.addAdminLog(WebEventLogType.LOGIN, null);

        mAdminMesageManager.sendSystemLoginMessage(account, ip);
        
        template.setJsonResult(SystemErrorResult.SUCCESS);
        return template.toJSONString();
    }

    @Async
    public void loginlog(String remoteIP){
        Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
//        String area = AddressUtils.getIpAddress(remoteIP);
//        area = area.replaceAll("XX","");
//        Admin admin = (Admin) request.getSession().getAttribute("admin");
//        adminService.updateLastLoginTimeAndIp(admin.getId(), new Date(), remoteIP,area);
        adminService.updateLastLoginIP(admin.getAccount(), remoteIP);
    }

    @RequestMapping("/logout")
    public String logout() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return "redirect:/alibaba888/Liv2sky3soLa93vEr62/toLogin";
    }


    /**
     * 获取二维码图片
     */
    @GetMapping("/verCodeImg")
    @ResponseBody
    public void getVerificationCode(HttpServletRequest request, HttpServletResponse response) {
        this.renderValidateImg(request, response, Constants.VERIFICATION_CODE);
    }

    /**
     * 输出验证码(image/jpeg)
     */
    protected String renderValidateImg(HttpServletRequest request, HttpServletResponse response, String sessionKey) {
        try {
            String code = ValidateCodeUtil.getRandomString();
            request.getSession().setAttribute(sessionKey, code);
            response.setContentType("image/jpeg");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            OutputStream outputStream = response.getOutputStream();
            ValidateCodeUtil.stringToImage(code, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 获取当前验证码内容
     *
     * @return String
     */
    private String getVerificationCodeValues(HttpServletRequest request) {
        Object verCode = request.getSession().getAttribute(Constants.VERIFICATION_CODE);
        return verCode != null ? verCode.toString() : null;
    }
    
}
