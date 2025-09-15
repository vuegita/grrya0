package com.inso.modules.passport.controller;

import com.google.common.collect.Maps;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.conf.MyConfiguration;
import com.inso.framework.google.GoogleAuthenticator;
import com.inso.framework.google.GoogleImageUtils;
import com.inso.framework.google.GoogleUtil;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RegexUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.framework.utils.UrlUtils;
import com.inso.modules.common.MySmsManager;
import com.inso.modules.common.config.PlarformConfig2;
import com.inso.modules.common.model.SmsServiceType;
import com.inso.modules.passport.UserErrorResult;
import com.inso.modules.passport.user.limit.MyLoginRequired;
import com.inso.modules.passport.user.model.GoogleStatus;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.model.UserSecret;
import com.inso.modules.passport.user.service.AuthService;
import com.inso.modules.passport.user.service.UserSecretService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.eventlog.EventLogManager;
import com.inso.modules.web.eventlog.model.WebEventLogType;
import com.inso.modules.web.eventlog.service.WebEventLogService;
import com.inso.modules.web.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RequestMapping("/passport/2faApi")
@RestController
public class MFA2Api {

    private static Log LOG = LogFactory.getLog(MFA2Api.class);

    @Autowired
    private UserService userService;
    @Autowired
    private AuthService mOauth2Service;

    @Autowired
    private UserSecretService mUserSecretService;

    @Autowired
    private WebEventLogService webEventLogService;

    @Autowired
    private MySmsManager mySmsManager;

    @Autowired
    private ConfigService mConfigService;

    /**
     * @api {post} /passport/2faApi/bindGoogleMFA
     * @apiDescription  获取用户信息
     * @apiName bindGoogleMFA
     * @apiGroup passport-mfa-api
     * @apiVersion 1.0.0
     *
     * @apiParam {String}  accessToken
     *
     * @apiSuccess  {String}  code    错误码
     * @apiSuccess  {String}  msg   错误信息
     *
     * @apiSuccessExample {json} Success-Response:
     *       {
     *         "code": 200,
     *         "msg": "success",
     *       }
     */
    @RequestMapping(value = "getGoogle2FAInfo")
    @ResponseBody
    @MyLoginRequired
    public String getGoogle2FAInfo()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mOauth2Service.getAccountByAccessToken(accessToken);

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        UserSecret secret = mUserSecretService.find(false, username);
        GoogleStatus googleStatus = GoogleStatus.getType(secret.getGoogleStatus());
        Map<String, Object> maps = Maps.newHashMap();
        if(googleStatus == GoogleStatus.UNBIND)
        {
            String googkey = null; //GoogleAuthenticator.generateSecretKey();
            maps.put("key", googkey);
        }
        maps.put("status", secret.getGoogleStatus());
        apiJsonTemplate.setData(maps);
        return apiJsonTemplate.toJSONString();
    }

    /**
     * @api {post} /passport/2faApi/bindGoogle2FA
     * @apiDescription  获取用户信息
     * @apiName bindGoogleMFA
     * @apiGroup passport-mfa-api
     * @apiVersion 1.0.0
     *
     * @apiParam {String}  accessToken
     *
     * @apiSuccess  {String}  code    错误码
     * @apiSuccess  {String}  msg   错误信息
     *
     * @apiSuccessExample {json} Success-Response:
     *       {
     *         "code": 200,
     *         "msg": "success",
     *       }
     */
    @RequestMapping(value = "bindGoogle2FA")
    @ResponseBody
    @MyLoginRequired
    public String bindGoogle2FA()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mOauth2Service.getAccountByAccessToken(accessToken);
        String captcha = WebRequest.getString("captcha");
        String googleKey = WebRequest.getString("googleKey");
        String remoteip = WebRequest.getRemoteIP();
        String userAgent = WebRequest.getUserAgent();
        String emailCode = WebRequest.getString("emailCode");


        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(StringUtils.isEmpty(captcha))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(StringUtils.isEmpty(googleKey) || !RegexUtils.isLetterDigit(googleKey) || googleKey.length() != 16)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(!GoogleUtil.checkGoogleCode(googleKey, captcha))
        {
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_UNBIND_GOOGLE);
            return  apiJsonTemplate.toJSONString();
        }

        UserInfo userInfo = userService.findByUsername(false, username);
        boolean forceEmailVerifyWhenBind2Wf = mConfigService.getBoolean(false, PlarformConfig2.ADMIN_USER_FORCE_EMAIL_VERIFY_OF_BIND_GOGLE.getKey());
        if(forceEmailVerifyWhenBind2Wf)
        {
            ErrorResult res = mySmsManager.verify( SmsServiceType.USER_BIND_2WF, remoteip, userInfo.getEmail(), emailCode);
            if ( res != SystemErrorResult.SUCCESS ){
                apiJsonTemplate.setError(SystemErrorResult.ERR_PARAMS.getCode(), "Email OTP error !");
                return apiJsonTemplate.toJSONString();
            }
        }

        UserSecret secret = mUserSecretService.find(false, username);
//        if(!secret.checkGoogle(apiJsonTemplate, captcha, false))
//        {
//            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_UNBIND_GOOGLE);
//            return  apiJsonTemplate.toJSONString();
//        }

//        if (!secret.checkLoginPwd(password))
//        {
//            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_PWD);
//            return  apiJsonTemplate.toJSONString();
//        }

        GoogleStatus googleStatus = GoogleStatus.getType(secret.getGoogleStatus());
        if(googleStatus == GoogleStatus.BIND)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST);
            return apiJsonTemplate.toJSONString();
        }

        mUserSecretService.updateGoogleInfo(username, GoogleStatus.BIND, googleKey);


        addLog(userInfo.getId(), userInfo.getName(), remoteip, userAgent);
        return apiJsonTemplate.toJSONString();
    }

    /**
     * @api {post} /passport/2faApi/create2FAQrcode
     * @apiDescription  获取用户信息
     * @apiName create2FAQrcode
     * @apiGroup passport-mfa-api
     * @apiVersion 1.0.0
     *
     * @apiParam {String}  accessToken
     *
     * @apiSuccess  {String}  code    错误码
     * @apiSuccess  {String}  msg   错误信息
     *
     * @apiSuccessExample {json} Success-Response:
     *       {
     *         "code": 200,
     *         "msg": "success",
     *       }
     */
    @RequestMapping(value = "create2FAQrcode")
    @ResponseBody
    @MyLoginRequired
    public void create2FAQrcode(HttpServletResponse response) {

        String accessToken = WebRequest.getAccessToken();
        String username = mOauth2Service.getAccountByAccessToken(accessToken);

        String googleKey = WebRequest.getString("googleKey");
        if(StringUtils.isEmpty(googleKey) || !RegexUtils.isLetterDigit(googleKey))
        {
            return;
        }

        UserSecret secret = mUserSecretService.find(false, username);

        GoogleStatus googleStatus = GoogleStatus.getType(secret.getGoogleStatus());
        if(googleStatus == GoogleStatus.BIND)
        {
            return;
        }

        String domain = WebRequest.getHttpServletRequest().getServerName();
        String mainDomain = UrlUtils.fetchMainDomain(domain);

        String name = mainDomain + "@" + username;
//        String googleKey = secret.getGoogleKey();

        GoogleImageUtils.getGoogleKeyEWM(response, name, googleKey);
    }

    @Async
    public void addLog(long userid, String username, String remoteip, String userAgent)
    {
        try {
            if(StringUtils.isEmpty(remoteip))
            {
                return;
            }
            webEventLogService.addMemberLog(WebEventLogType.MEMBER_GOOGLE_BIND, null, userid, remoteip, userAgent);
        } catch (Exception e) {
            LOG.error("handle error:", e);
        }
    }
}
