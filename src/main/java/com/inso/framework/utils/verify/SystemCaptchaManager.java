package com.inso.framework.utils.verify;

import com.inso.framework.conf.MyConfiguration;
import com.inso.framework.spring.web.WebRequest;
import com.inso.modules.web.SystemRunningMode;

public class SystemCaptchaManager {

    private String supportType;

    private static final String CAPTCHA_TYPE_SYSTEM = "system";
    private static final String CAPTCHA_TYPE_GOOGLE = "google";

    private GoogleRecaptchaUtils googleRecaptchaUtils = new GoogleRecaptchaUtils();

    private interface MyInternal {
        public SystemCaptchaManager mgr = new SystemCaptchaManager();
    }

    private SystemCaptchaManager()
    {
        this.supportType = MyConfiguration.getInstance().getString("system.captcha.type");
    }

    public static SystemCaptchaManager getInstance()
    {
        return MyInternal.mgr;
    }

    public boolean verify(String key, String remoteip)
    {
//        boolean rs = true;
//        if(rs)
//        {
//            return rs;
//        }
        // 都是用 imgcode
        String captcha = WebRequest.getString("imgcode");
        if(CAPTCHA_TYPE_GOOGLE.equalsIgnoreCase(supportType))
        {
            return googleRecaptchaUtils.verify(captcha, remoteip);
        }
        return ResponseImageVerifyCodeUtils.verifyCodebyCache(key);
    }

    private boolean verify2(String key, String imgcode, String remoteip)
    {
        // 都是用 imgcode
        if(CAPTCHA_TYPE_GOOGLE.equalsIgnoreCase(supportType))
        {
            boolean rs = googleRecaptchaUtils.verify(imgcode, remoteip);
            if(!rs && !SystemRunningMode.isDevOrTest)
            {
                return rs;
            }
        }
        return ResponseImageVerifyCodeUtils.verifyCodebyCache(key);
    }

    public static void main(String[] args) {
        String imgCode = "03AKH6MRHX53pfaLp-PWpVZ_IX9Yq6eIwt19VUzKjooKi-cPeD9Ji8xn1ZYCDTy1qBiOKBv6X9nl2uSGEEWsQqcKSzzmjHg-7s0lxcxCKQWI8-Vc0YFShkCTgQQUUkfPobTEaBSPzYy6jKKJOlWutSoyvAB3JPKmIHjXzrbSQ0W4OHDP5gkpalkTS_mQ3HHGSZVmz9W7neQfD3gvTd3pIMKG729Z_T51xIcjjEqtEC0Kgbm8hVSNbZ5C23eiQRv83LOTjXsoFgO85HDcDM_9Gu-rPdK6Q5nAbt1mRgqZ_o2uDpd09LFHwCrFWAZo8MFafhH_YyiTBmRYXZPcHWFTpu7UHC1qAGc9UmU9l07acszUBkGwDmLG8vS-7bkgxJtqOOOgMyzSEqEWoQE0ytopoNnCYt4BRXDXaLrs8sYcU7KVyndmtMiutHcqNSz2BQjxAWTE7JzMmg25_OJM_LTdU_gYtUgGNIKnauHtPeZodOiTVm3A0TKSisGG9NaVvMjGTADOE3bfxDHlzHTyo6qD6G3x9siG51eSVSAA";
        SystemCaptchaManager.getInstance().verify2("a", imgCode, null);

    }

}
