package com.inso.framework.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class AESUtils {
	
	// 算法名
	public static final String KEY_ALGORITHM = "AES";
	
    //算法
    private static final String ALGORITHMSTR = "AES/ECB/PKCS5Padding";
 
    /**
     * AES加密为base 64 code
     * @param content 待加密的内容
     * @param encryptKey 加密密钥
     * @return 加密后的base 64 code
     * @throws Exception
     */
    public static String encrypt(String content, String encryptKey) {
    	try {
			KeyGenerator kgen = KeyGenerator.getInstance(AESUtils.KEY_ALGORITHM);
			kgen.init(128);
			Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey.getBytes(), AESUtils.KEY_ALGORITHM));  
			byte[] rs = cipher.doFinal(content.getBytes(StringUtils.UTF8));
			return Base64.encodeBase64String(rs);
		} catch (Exception e) {
		} 
    	return null;
    }  
 
 
    /**
     * 将base 64 code AES解密
     * @param encryptStr 待解密的base 64 code
     * @param decryptKey 解密密钥
     * @return 解密后的string
     * @throws Exception
     */
    public static String decrypt(String encryptStr, String decryptKey)  {
    	try {
			byte[] rs = Base64.decodeBase64(encryptStr);
			KeyGenerator kgen = KeyGenerator.getInstance(AESUtils.KEY_ALGORITHM);
			kgen.init(128);  
			Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptKey.getBytes(), AESUtils.KEY_ALGORITHM));
			byte[] decryptBytes = cipher.doFinal(rs);
			return new String(decryptBytes);
		} catch (Exception e) {
		} 
    	return null;
    }  
 
    /**
     * 测试
     */
    public static void main(String[] args) throws Exception {
    	String key = "0123456789abcdef";
        String content = "pangugle";
        System.out.println("加密前：" + content);
        System.out.println("加密密钥和解密密钥：" + key);
        String encrypt = encrypt(content, key);
        System.out.println("加密后：" + encrypt);
        String decrypt = decrypt(encrypt, key);
        System.out.println("解密后：" + decrypt);
        // zhiniu17704
    }

}
