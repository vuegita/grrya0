package com.inso.framework.utils;

import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;

public class MyAES {

    // 算法名
    public static final String KEY_ALGORITHM = "AES";
    // 加解密算法/模式/填充方式
    // 可以任意选择，为了方便后面与iOS端的加密解密，采用与其相同的模式与填充方式
    // ECB模式只用密钥即可对数据进行加密解密，CBC模式需要添加一个参数iv
    public static final String CIPHER_ALGORITHM = "AES/CBC/PKCS7Padding";
    private static final BouncyCastleProvider mProvider = new BouncyCastleProvider();

    static {
        Security.addProvider(mProvider);
    }

    /**
     * 编码
     *
     * @return
     */
    public static String base64Encode(byte[] input) {
        return new String(Base64.encode(input));
    }

    /**
     * 解码
     *
     * @return
     */
    public static byte[] base64Decode(String src) {
        try {
            return Base64.decode(src);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 生成密钥
    public static byte[] generateKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
            keyGenerator.init(128);
            SecretKey key = keyGenerator.generateKey();
            return key.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static AlgorithmParameters generateIV(byte[] iv) throws Exception {
        AlgorithmParameters params = AlgorithmParameters.getInstance(KEY_ALGORITHM);
        params.init(new IvParameterSpec(iv));
        return params;
    }

    // 转化成JAVA的密钥格式
    private static Key convertToKey(byte[] keyBytes) throws Exception {
        SecretKey secretKey = new SecretKeySpec(keyBytes, KEY_ALGORITHM);
        return secretKey;
    }

    // 加密
    public static byte[] encrypt(byte[] data, byte[] keyBytes, byte[] iv) {
        try {
            // 转化为密钥
            Key key = convertToKey(keyBytes);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            // 设置为加密模式
            cipher.init(Cipher.ENCRYPT_MODE, key, generateIV(iv));
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 解密
    public static String decrypt(String encryptedData, String key, String iv) {
        try {
            byte[] rs = decrypt(base64Decode(encryptedData), base64Decode(key), base64Decode(iv));
            return new String(rs);
        } catch (Exception e) {
        }
        return null;
    }

    private static byte[] decrypt(byte[] encryptedData, byte[] key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        Key sKeySpec = new SecretKeySpec(key, KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, sKeySpec, generateIV(iv));// 初始化
        byte[] result = cipher.doFinal(encryptedData);
        return result;
    }

    public static void test() {
        // 明文
        String plainTextString = "Hello aes";
        System.out.println("明文 : " + plainTextString);
        try {
            // 初始化密钥
            byte[] key = generateKey();
            // 初始化iv
            byte[] iv = "mhydgdflfuehfdkl".getBytes();

            String keyString = new String(Base64.encode(key));
            System.out.println("key = " + "" + keyString);

            byte[] keytmp = Base64.decode(keyString.getBytes());

            System.out.println("src 11111 = " + Base64.encode(key));
            System.out.println("src 22222 = " + keyString.getBytes());


            System.out.println("key   =========== " + (key == keytmp));


            System.out.println("iv = " + new String(base64Encode(iv)));

            // 进行加密
            byte[] encryptedData = encrypt(plainTextString.getBytes(), key, iv);
            // 输出加密后的数据
            System.out.println("encode str = " + new String(base64Encode(encryptedData)));

            // 进行解密
            byte[] data = decrypt(encryptedData, key, iv);
            System.out.println("解密得到的数据 : " + new String(data));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        test();
    }
}
