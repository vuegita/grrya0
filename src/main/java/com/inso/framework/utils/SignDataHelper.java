package com.inso.framework.utils;


public class SignDataHelper {


    private static String DEFAULT_DEV_SALT = "d912ce0c37a72ae9";
    private static String DEFAULT_SALT = DEFAULT_DEV_SALT;

    private String mSalt;

    public SignDataHelper(String salt)
    {
        this.mSalt = MD5.encode(DEFAULT_SALT + salt).substring(0, 16);
    }


    public String encryptPrivateKey(String privateKey)
    {
        try {
            String encryptStr = AESUtils.encrypt(privateKey, mSalt);
            String base64 = Base64Utils.encode(encryptStr);
            return base64;
        } catch (Exception e) {
        }
        return null;
    }

    public String decryptPrivateKey(String privateKey)
    {
        try {
            String base64Value = Base64Utils.decode(privateKey);
            String decryptString = AESUtils.decrypt(base64Value, mSalt);
            return decryptString;
        } catch (Exception e) {
        }
        return null;
    }


}
