package com.inso.modules.common;

import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.sms.SmsClient;
import com.inso.framework.utils.RandomStringUtils;
import com.inso.framework.utils.RegexUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.SmsServiceType;
import com.inso.modules.passport.mail.MailManager;
import com.inso.modules.passport.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 短信发送管理器
 */
@Component
public class MySmsManager {

    private static Log LOG = LogFactory.getLog(MySmsManager.class);

    private static final String ROOT_CACHE = MySmsManager.class.getName();

    private static final String Email_SEND_COUNT_CACHE_KEY = ROOT_CACHE + "_send_count";

    private static final String SEND_STATIC_CODE_CACHE_KEY = ROOT_CACHE + "_static_code";

    // 10分钟内有效
    private static int EXPIRES = 600;
    /*** 用户短信码 ***/
    private static String CACHE_KEY_USER = "mywg_common_sms_verify_code_service_ip_mobile_";
    /*** 系统万能码 ***/
    private static String CACHE_KEY_SYS = "mywg_cmmon_sms_verify_code_service_system_";

    @Autowired
    private MailManager mailManager;

    @Autowired
    private UserService mUserService;

//    private interface MyInternal {
//        MySmsManager mgr = new MySmsManager();
//    }
//
//    private MySmsManager()
//    {
//    }
//
//    public static MySmsManager getInstance()
//    {
//        return MyInternal.mgr;
//    }

    /**
     * 验证确认短信验证码
     * @param type
     * @param ip
     * @param mobile
     * @param code
     * @return
     */
    public ErrorResult verify(SmsServiceType type, String ip, String mobile, String code)
    {
//        if(StringUtils.isEmpty(mobile))
//        {
//            return SystemErrorResult.ERR_PARAMS;
//        }


        if(StringUtils.isEmpty(code) || !RegexUtils.isDigit(code))
        {
            return SystemErrorResult.ERR_VERIFY_IMAGE_CODE;
        }

        // 验证是不是系统短信码
        if(type.isCanUsingSystemCode() && verifySystemCode(code))
        {
            return SystemErrorResult.SUCCESS;
        }

        // 用户发送的
        String cachekey = generateUserCacheKey(type, ip, mobile);
        String value = CacheManager.getInstance().getString(cachekey);
        if(code.equalsIgnoreCase(value))
        {
            return SystemErrorResult.SUCCESS;
        }
        return SystemErrorResult.ERR_VERIFY_IMAGE_CODE;
    }

    /**
     * 发送验证码-每天一个手机号最多只能发送10个,每个ip最多发送10个
     * @param type
     * @param ip
     * @param mobile
     * @param code
     * @return
     */
    public ErrorResult send(SmsServiceType type, String ip, String mobile, String code,String senderid, boolean companyNameStatus ,String smsContent, boolean isSend)
    {
//        if(StringUtils.isEmpty(mobile) || type == null)
//        {
//            return SystemErrorResult.ERR_PARAMS;
//        }
//
//        if(StringUtils.isEmpty(code) || code.length() != 6)
//        {
//            return SystemErrorResult.ERR_PARAMS;
//        }

        boolean rs = false;
        if(RegexUtils.isMobile(mobile))
        {
            SmsClient.getInstance().send(mobile, code, ip,senderid , companyNameStatus, smsContent, new Callback<Boolean>() {
                @Override
                public void execute(Boolean o) {
                    // 成功或失败
                }
            });
        }
        else if(RegexUtils.isEmail(mobile))
        {
            if(isSend)
            {
                if(!checkEmailSend(mobile, type))
                {
                    return SystemErrorResult.ERR_SYS_OPT_FORBID;
                }
                if(type == SmsServiceType.USER_BIND_2WF)
                {
                    mailManager.sendBind2FA(mobile, smsContent);
                }
                else if(type == SmsServiceType.USER_REG)
                {
                    mailManager.sendUserRegCode(mobile, smsContent);
                }
            }
        }
        else
        {
            return SystemErrorResult.ERR_SYS_OPT_FORBID;
        }

        String cachekey = generateUserCacheKey(type, ip, mobile);
        CacheManager.getInstance().setString(cachekey, code, EXPIRES);
        return SystemErrorResult.SUCCESS;
    }

    private String generateUserCacheKey(SmsServiceType type, String ip, String mobile)
    {
        return CACHE_KEY_USER + ip + mobile + type.getKey();
    }

    /**
     * 生成系统万能码
     * @return
     */
    public String saveSystemCode()
    {
        String code = RandomStringUtils.generator0_9(6);
        String cachekey = CACHE_KEY_SYS + code;
        CacheManager.getInstance().setString(cachekey, code, EXPIRES);
        return code;
    }

    private boolean verifySystemCode(String code)
    {
        String systemCacheKey = CACHE_KEY_SYS + code;
        String value = CacheManager.getInstance().getString(systemCacheKey);
        if(StringUtils.isEmpty(value))
        {
            return false;
        }

        boolean rs = value.equalsIgnoreCase(code);
        if(rs)
        {
            CacheManager.getInstance().delete(systemCacheKey);
        }
        return rs;
    }

    private boolean checkEmailSend(String email, SmsServiceType type)
    {
        if(type == SmsServiceType.USER_REG && mUserService != null)
        {
            // 邮件已存在
            String username = mUserService.findNameByEmail(email);
            if(!StringUtils.isEmpty(username))
            {
                LOG.info("Send Email " + email + " limit for " + type.getKey() + ",  Exist!");
                return false;
            }
        }

        String cahcekey_24 = Email_SEND_COUNT_CACHE_KEY + "_24h_v1_" + email;
        int count_24 = CacheManager.getInstance().getInt(cahcekey_24);
        if(count_24 > 8)
        {
            LOG.info("Send Email " + email + " limit for " + type.getKey() + ", count_24 >= 8 for " + count_24);
            return false;
        }

        String cahcekey_5 = Email_SEND_COUNT_CACHE_KEY + "_5h1_v1_" + email;
        int count_5 = CacheManager.getInstance().getInt(cahcekey_5);
        if(count_5 >= 5)
        {
            LOG.info("Send Email " + email + " limit for " + type.getKey() + ", count_5 >= 5 for " + count_5);
            return false;
        }

        String cahcekey_1 = Email_SEND_COUNT_CACHE_KEY + "_1h_v1_" + email;
        int count_1 = CacheManager.getInstance().getInt(cahcekey_1);
        if(count_1 >= 3)
        {
            LOG.info("Send Email " + email + " limit for " + type.getKey() + ", count_1 >=3 for " + count_1);
            return false;
        }

        count_24 += 1;
        CacheManager.getInstance().setString(cahcekey_24, count_24 + StringUtils.getEmpty(), CacheManager.EXPIRES_HOUR_36);

        count_5 += 1;
        CacheManager.getInstance().setString(cahcekey_5, count_5 + StringUtils.getEmpty(), CacheManager.EXPIRES_HOUR_5);

        count_1 += 1;
        CacheManager.getInstance().setString(cahcekey_1, count_1 + StringUtils.getEmpty(), CacheManager.EXPIRES_HOUR);
        return true;
    }

    public String getStaticCodeByKey(String modileOrEmail, SmsServiceType type)
    {
        if(!type.isUsingStaticCode())
        {
            return RandomStringUtils.generator0_9(6);
        }
        String cachekey = SEND_STATIC_CODE_CACHE_KEY + type.getKey() + modileOrEmail;
        String value = CacheManager.getInstance().getString(cachekey);
        if(!StringUtils.isEmpty(value))
        {
            return value;
        }
        value = RandomStringUtils.generator0_9(6);
        CacheManager.getInstance().setString(cachekey, value, CacheManager.EXPIRES_MINUTES_10);
        return value;
    }

    public static void main(String[] args) {

        String email = "test@gmail.com";
        MySmsManager mgr = new MySmsManager();
        for(int i = 0 ; i < 10; i ++)
        {
//            boolean rs = mgr.checkEmailSend(email, SmsServiceType.USER_REG);
            String rs = mgr.getStaticCodeByKey(email, SmsServiceType.USER_REG);
            System.out.println("rs = " + rs);
        }

    }

}
