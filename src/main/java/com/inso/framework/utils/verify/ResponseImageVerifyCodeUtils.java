package com.inso.framework.utils.verify;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.inso.framework.cache.CacheManager;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.StringUtils;
import com.inso.framework.utils.ValidateCodeUtil;

public class ResponseImageVerifyCodeUtils {

    private static final String DEFAULT_SESSION_KEY = "img_verify_code";

    private static final String DEFAULT_CACHE_KEY = ResponseImageVerifyCodeUtils.class.getName() + "_img_verify_code_by_cache_";

    public static String renderValidateByCache(String key) {
        try {
            HttpServletResponse response = WebRequest.getHttpServletResponse();

            String code = ValidateCodeUtil.getRandomString();
            String remoteip = WebRequest.getRemoteIP();
            String cachekey = DEFAULT_CACHE_KEY + remoteip + key ;
            CacheManager.getInstance().setString(cachekey, code, CacheManager.EXPIRES_HOUR);

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
     * 输出验证码(image/jpeg)
     */
    public static String renderValidateImg() {
        try {
            HttpServletRequest request = WebRequest.getHttpServletRequest();
            HttpServletResponse response = WebRequest.getHttpServletResponse();

            String sessionKey = DEFAULT_SESSION_KEY;
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

    public static boolean verifyCode()
    {
//        return true;
        String code = WebRequest.getString("imgcode");
        if(StringUtils.isEmpty(code))
        {
            return false;
        }
        String sessionKey = DEFAULT_SESSION_KEY;
        HttpServletRequest request = WebRequest.getHttpServletRequest();
        String sessionCode = (String)request.getSession().getAttribute(sessionKey);

        boolean rs = code.equalsIgnoreCase(sessionCode);
        if(rs)
        {
            request.getSession().removeAttribute(sessionKey);
        }
        return rs;
    }

    public static boolean verifyCodebyCache(String key)
    {
//        return true;

        String imgcode = WebRequest.getString("imgcode");
        if(StringUtils.isEmpty(key) || StringUtils.isEmpty(imgcode))
        {
            return false;
        }

        String remoteip = WebRequest.getRemoteIP();
        String cachekey = DEFAULT_CACHE_KEY + remoteip + key;
        String value = CacheManager.getInstance().getString(cachekey);

        boolean rs = imgcode.equalsIgnoreCase(value);
        return rs;
    }

    public static String getCode()
    {
        String sessionKey = DEFAULT_SESSION_KEY;
        HttpServletRequest request = WebRequest.getHttpServletRequest();
        String sessionCode = (String)request.getSession().getAttribute(sessionKey);

        return sessionCode;
    }

}
