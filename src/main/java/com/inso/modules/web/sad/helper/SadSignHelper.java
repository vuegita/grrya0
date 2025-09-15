package com.inso.modules.web.sad.helper;

import com.inso.framework.utils.AESUtils;
import com.inso.framework.utils.Base64Utils;
import com.inso.framework.utils.MD5;
import com.inso.framework.utils.StringUtils;


public class SadSignHelper {

    private static final String DEFAULT_SALT = "09d93bec1b85d9e5";

    private static int DEFAULT_COUNT = 2;

    private static String baseEncrypt(String str)
    {
        String aseEncrypt = AESUtils.encrypt(str, DEFAULT_SALT);
        String base64Str = Base64Utils.encode(aseEncrypt);
        return base64Str;
    }

    private static String baseDecrypt(String str)
    {
        String base64Str = Base64Utils.decode(str);
        String srcStr = AESUtils.decrypt(base64Str, DEFAULT_SALT);
        return srcStr;
    }

    public static String safeEncrypt(String str)
    {
        String encrypt = str;
        for(int i = 0; i < DEFAULT_COUNT; i ++)
        {
            encrypt = baseEncrypt(encrypt);
        }
        return encrypt;
    }

    public static String safeDecrypt(String str)
    {
        try {
            String encrypt = str;
            for(int i = 0; i < DEFAULT_COUNT; i ++)
            {
                encrypt = baseDecrypt(encrypt);
            }
            return encrypt;
        } catch (Exception e) {
        }
        return StringUtils.getEmpty();
    }


    public static void main(String[] args) {

        System.out.println();
        System.out.println("=================================================");

        String input = MD5.encode("xxx");

        String value1 = safeEncrypt(input);
        System.out.println(value1);
    }

}
