package com.inso.modules.admin.core;

import com.inso.framework.conf.MyConfiguration;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.google.GoogleUtil;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.core.helper.CoreSignHelper;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

/**
 * 核心安全管理器
 */
public class CoreSafeManager {

    private MyConfiguration conf = MyConfiguration.getInstance();

    private static final String KEY_PROJECT = "system.safe.project.salt";
    private static final String KEY_PLATFORM = "system.safe.platform.salt";

    private interface MyInternal {
        CoreSafeManager mgr = new CoreSafeManager();
    }

    /*** 强制白名单登陆 ***/
    private boolean isEnableWhiteIPLogin = false;

    private int errCount = 0;

    private CoreSafeManager(){
        this.isEnableWhiteIPLogin = conf.getBoolean("system.safe.whiteip.login");
    }

    public static CoreSafeManager getInstance()
    {
        return MyInternal.mgr;
    }

    public boolean verifyByWeb()
    {
        return verifyByWeb(true);
    }

    public boolean verifyByWeb(boolean forceAddBlack)
    {
        String code = WebRequest.getString("googleCode");
        return MyEnvironment.isDev() || verify(code, forceAddBlack);
    }

    public boolean isMaintenance()
    {
        return errCount >= 3;
    }

    private boolean verify(String code, boolean forceBlack)
    {
        if(errCount >= 3)
        {
            // 进入维护模式, 任何人都划转不了
            return false;
        }

        boolean rs = false;
        try {

            String encryptKey = conf.getString(KEY_PROJECT);
            rs = verify(encryptKey, code);
            if(rs)
            {
                return rs;
            }
            encryptKey = conf.getString(KEY_PLATFORM);
            rs = verify(encryptKey, code);

            if(!rs && forceBlack)
            {
                errCount ++;
            }
        } finally {
            if(rs)
            {
                errCount = 0;
            }
            if(errCount >= 3)
            {
                Subject subject = SecurityUtils.getSubject();
                subject.logout();
            }
        }
        return rs;
    }

    private boolean verify(String encryptKey, String code)
    {
        try {
            String googlekey = CoreSignHelper.safeDecrypt(encryptKey);
            return !StringUtils.isEmpty(googlekey) && GoogleUtil.checkGoogleCode(googlekey, code);
        } catch (Exception e) {
        }
        return false;
    }

    public boolean verifyWhiteIPLogin()
    {
        return isEnableWhiteIPLogin;
    }

    public static void main(String[] args) {
    }


}
