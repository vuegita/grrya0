package com.inso.modules.admin.core.helper;

import com.inso.framework.google.GoogleAuthenticator;
import com.inso.framework.google.GoogleUtil;
import com.inso.framework.utils.AESUtils;
import com.inso.framework.utils.Base64Utils;
import com.inso.framework.utils.MD5;
import com.inso.framework.utils.StringUtils;


public class CoreSignHelper {

    private static final String DEFAULT_SALT = "09d93bec1b85d9e5";

    private static int DEFAULT_COUNT = 3;

    private static String baseEncrypt(String str)
    {
        String encrypt = AESUtils.encrypt(str, DEFAULT_SALT);
        return encrypt;
    }

    private static String baseDecrypt(String str)
    {
        String srcStr = AESUtils.decrypt(str, DEFAULT_SALT);
        return srcStr;
    }

    public static String safeEncrypt(String str)
    {
        String encrypt = Base64Utils.encode(str);
        for(int i = 0; i < DEFAULT_COUNT; i ++)
        {
            encrypt = baseEncrypt(encrypt);
        }
        encrypt = Base64Utils.encode(encrypt);
        return encrypt;
    }

    public static String safeDecrypt(String str)
    {
        try {
            String encrypt = Base64Utils.decode(str);;
            for(int i = 0; i < DEFAULT_COUNT; i ++)
            {
                encrypt = baseDecrypt(encrypt);
            }
            encrypt = Base64Utils.decode(encrypt);
            return encrypt;
        } catch (Exception e) {
        }
        return StringUtils.getEmpty();
    }


    public static void main(String[] args) {

        String googleKey = GoogleAuthenticator.generateSecretKey();

        System.out.println("googleKey = " + googleKey);
        String encryptGoogleKey = safeEncrypt(googleKey);
        System.out.println(encryptGoogleKey);

        System.out.println("decrypt = " + safeDecrypt(encryptGoogleKey));
    }

}
