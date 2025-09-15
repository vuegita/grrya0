package com.inso.framework.utils;

public class NumberEncryptUtils {

    private static final String DEFAULT_SALT = "35300ac7d229f225";

    public static String encryptId(long id)
    {
        String rs = AESUtils.encrypt(id + StringUtils.getEmpty(), DEFAULT_SALT);
        return Base64Utils.encode(rs);
    }

    public static long decryptId(String content)
    {
        try {
            String decryByBase64 = Base64Utils.decode(content);
            String decryByAES = AESUtils.decrypt(decryByBase64, DEFAULT_SALT);
            return StringUtils.asLong(decryByAES);
        } catch (Exception e) {
        }
        return -1;
    }
}
