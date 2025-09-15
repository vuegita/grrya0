package com.inso.framework.utils;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ArrayUtils;

public class BakRSAUtils {
	/**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    public static final String SIGN_ALGORITHMS = "SHA256withRSA";

    private static String ALGORITHM_RSA = "RSA";
    
    public static String pemToKey(String pem){
        if(pem==null) return "";
        if(pem.indexOf("KEY-----")>0){
            pem = pem.substring(pem.indexOf("KEY-----")+"KEY-----".length());
        }
        if(pem.indexOf("-----END")>0){
            pem = pem.substring(0,pem.indexOf("-----END"));
        }
        return pem.replace("\n","");
    }

    /**
     * 使用公钥将数据加密
     * @param sourceData
     * @param publicKey
     * @return
     */
    public static String publicEncrypt(String sourceData, String publicKey){
        return rsaEncrypt(sourceData,publicKey,false);
    }

    /**
     * 使用私钥将数据加密
     * @param sourceData
     * @param privateKey
     * @return
     */
    public static String privateEncrypt(String sourceData, String privateKey){
        return rsaEncrypt(sourceData,privateKey,true);
    }


    /**
     * 使用公钥解密
     * @param encryptedData
     * @param privateKey
     * @return
     */
    public static String publicDecrypt(String encryptedData, String privateKey) {
        return rsaDecrypt(encryptedData,privateKey,false);
    }

    /**
     * 使用私钥解密
     * @param encryptedData
     * @param privateKey
     * @return
     */
    public static String privateDecrypt(String encryptedData, String privateKey) {
        return rsaDecrypt(encryptedData,privateKey,true);
    }

    protected static String rsaEncrypt(String sourceData, String key,boolean isPrivate){
        try {
            Key key1 = isPrivate ? loadPrivateKey(key) : loadPublicKey(key);
            byte[] data = sourceData.getBytes();
            byte[] dataReturn = new byte[0];
            Cipher cipher = Cipher.getInstance(ALGORITHM_RSA);
            cipher.init(Cipher.ENCRYPT_MODE, key1);

            // 加密时超过117字节就报错。为此采用分段加密的办法来加密
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < data.length; i += MAX_ENCRYPT_BLOCK) {
                byte[] doFinal = cipher.doFinal(ArrayUtils.subarray(data, i,i + MAX_ENCRYPT_BLOCK));
                sb.append(new String(doFinal));
                dataReturn = ArrayUtils.addAll(dataReturn, doFinal);
            }

            return Base64.encodeBase64URLSafeString(dataReturn);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected static String rsaDecrypt(String encryptedData, String key,boolean isPrivate){
        try {
            Key key1 = isPrivate ? loadPrivateKey(key) : loadPublicKey(key);
            byte[] data = Base64.decodeBase64(encryptedData);

            Cipher cipher = Cipher.getInstance(ALGORITHM_RSA);
            cipher.init(Cipher.DECRYPT_MODE, key1);

            // 解密时超过128字节就报错。为此采用分段解密的办法来解密
            byte[] dataReturn = new byte[0];
            for (int i = 0; i < data.length; i += MAX_DECRYPT_BLOCK) {
                byte[] doFinal = cipher.doFinal(ArrayUtils.subarray(data, i, i + MAX_DECRYPT_BLOCK));
                dataReturn = ArrayUtils.addAll(dataReturn, doFinal);
            }

            return new String(dataReturn);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 私钥加签名
     * @param encryptData
     * @param privateKey
     * @return
     */
    public static String rsaSign(String encryptData, String privateKey) {
        try {
            Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
            signature.initSign(loadPrivateKey(privateKey));
            signature.update(encryptData.getBytes());
            byte[] signed = signature.sign();
            return Base64.encodeBase64URLSafeString(signed);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 公钥验签
     * @param encryptStr
     * @param sign
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static boolean verifySign(String encryptStr, String sign, String publicKey)throws Exception {
        try {
            Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
            signature.initVerify(loadPublicKey(publicKey));
            signature.update(encryptStr.getBytes());
            return signature.verify(Base64.decodeBase64(sign));
        }  catch (NoSuchAlgorithmException e) {
            throw new Exception(String.format("验证数字签名时没有[%s]此类算法", SIGN_ALGORITHMS));
        } catch (InvalidKeyException e) {
            throw new Exception("验证数字签名时公钥无效");
        } catch (SignatureException e) {
            throw new Exception("验证数字签名时出现异常");
        }
    }

    public static PublicKey loadPublicKey(String publicKeyStr) throws Exception {
        byte[] buffer = Base64.decodeBase64(publicKeyStr);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
        return keyFactory.generatePublic(keySpec);
    }

    public static PrivateKey loadPrivateKey(String privateKeyStr) throws Exception {
        byte[] buffer = Base64.decodeBase64(privateKeyStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
        return keyFactory.generatePrivate(keySpec);
    }

    public  static String urlsafe_encode (String encryptStr){
        return encryptStr.replaceAll("\\+","-").replaceAll("/","_").replaceAll("=","").replaceAll("(\r\n|\r|\n|\n\r)","");

    }

    public  static String urlsafe_decode(String encryptStr){
        encryptStr= encryptStr.replaceAll("-","+").replaceAll("_","/");
        int mob = encryptStr.length()%4;
        if(mob>0){
            encryptStr+="====".substring(mob);
        }
        return encryptStr;
    }
    
    private static void test1() throws Exception
    {
//    	 String publicKeyStr = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC6BSDlbRplhMMNZBKHX4xe8AwE" +
//                 "SpzHVfAcHHsX9FFSMuF91W3cxgT/g5n+qlLLFzCE3hWG/yX5NMAxR4mS3MlhyXKw" +
//                 "ko3tK9Ua691afod1lxORR3IaZ8nV7v5Bv8y4JDe4E3/f/bQIGzroWiJ0sXTcO41G" +
//                 "qvOw3G9leClSvjVnSwIDAQAB";
//         String privateKeyStr = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALoFIOVtGmWEww1k" +
//                 "EodfjF7wDARKnMdV8Bwcexf0UVIy4X3VbdzGBP+Dmf6qUssXMITeFYb/Jfk0wDFH" +
//                 "iZLcyWHJcrCSje0r1Rrr3Vp+h3WXE5FHchpnydXu/kG/zLgkN7gTf9/9tAgbOuha" +
//                 "InSxdNw7jUaq87Dcb2V4KVK+NWdLAgMBAAECgYBqCihhgJtOiarjBEvnrZkIOZCw" +
//                 "FZRfsWaJr9afph+BWw3dvH+/HYaV3YA4gwFlUlfPNgZRiTstX1u7+8q51HBa+08h" +
//                 "jPE8Q4GhoUY+sQ9MB8NXA6SWHNPPfMOYIeKEtKmNBdgIbtuhnob3o18rJNFIY+qC" +
//                 "i8djf4om93+AChmo6QJBAO31hd9qem7BnHXsxiMwS+iHlRjW9KxXva2zf+BNURSR" +
//                 "Z19cePReHJGE4v1C731MZlygTB5zKChQ8uZ3JLKJeX8CQQDIH4k/xbuhMb8dMdzl" +
//                 "AYN/CU+MgfWjlgbYjxOnTaLcbs5Mlz9v3/5I/FwqxPvzGuCjHkyh08oFfnQXvzdj" +
//                 "YMA1AkEApjgyOnzzZviBZXJueVgcPiKvSHmm0dg8W+Cd+72mXHqxPdCngPNYe2Ha" +
//                 "+VRPXDQI8LzcTwzbyUW6Vrh0/u2+2wJBAK1rZqx01VuimFLcWue4oBL+JolENXFF" +
//                 "GTmhAw8AIBmVjACjML3qBZmJ1vTZLtxEdlXkc9PojDCmnEPX2E+uD+ECQF2eX4EY" +
//                 "X95HDzQ4cm1kGQudjgfH1gZ+30DIindIHXNAOFpYeAUD7yUQP5tZO8nG38gybPJg" +
//                 "FoadlsSMIQIpksM=";
         
     	String publicKeyStr = "-----BEGIN PUBLIC KEY-----" + 
     			"MIGeMA0GCSqGSIb3DQEBAQUAA4GMADCBiAKBgG2b5ZdxRKCKGinY8vuL/NjQ8MR7" + 
     			"batoy1x+/Is7lZ4Y8wjIDSKChRnS07vjtZDApZEgOpa1AuuoGEs3Ig6WXGh6VWyv" + 
     			"eSSsVnFNo2L12+09NCshpaiFJg8M1TLq+Sw6lBQS2+bffSgQlYsAC7tnZzTT4Qq1" + 
     			"Xaivr7Uehg/51KNfAgMBAAE=" + 
     			"-----END PUBLIC KEY-----";
    	
     	String privateKeyStr = "-----BEGIN RSA PRIVATE KEY-----" + 
     			"MIICWwIBAAKBgG2b5ZdxRKCKGinY8vuL/NjQ8MR7batoy1x+/Is7lZ4Y8wjIDSKC" + 
     			"hRnS07vjtZDApZEgOpa1AuuoGEs3Ig6WXGh6VWyveSSsVnFNo2L12+09NCshpaiF" + 
     			"Jg8M1TLq+Sw6lBQS2+bffSgQlYsAC7tnZzTT4Qq1Xaivr7Uehg/51KNfAgMBAAEC" + 
     			"gYAk7FhaPMjOf9cyc6fevxiM/U2uEpouHN4sqYZvbMIJAQSV17Y9AtTNjyzOZXXb" + 
     			"bloX2lCCRjXWpsiQReFOIq67/jUfU/4qh3XEeP7lB/VfnfdSGgngilJJ/6fXG1Fq" + 
     			"e+CSUjWOedjI7ELoTkgnx0lOKEQnwn3gm1exmEDQ29wzAQJBALGO6D76yz8JJKBf" + 
     			"HJSWpEIdihQmZYcjylJ6Q5EYjIyyP6P2cgKHrAisxN3SdDdne+JHkOe8UAeihqNf" + 
     			"xCsJk98CQQCeCDZ6y+VVmMQACHmlh8HLFSThhrMQKIH0TRqc8/TJ8XtYadxZy8nr" + 
     			"UOi4wTntq4slOGVIZiMaxtGxN0hbIuCBAkEAnNv8Ds9uyMRiCA+eZyJiAEqqVjzJ" + 
     			"isaykYSTtlYyrIU4NbidZeDHuW4wCStIIj4YsoG44RB6vxP0z9XlroOqjwJAATo1" + 
     			"Gh0NSe3mz81BDJ0STz55y9qc92opIDmdFEz8wLjEaXphAL2Jcu9s70po5cSoTwCy" + 
     			"IK7SZPnYVRUF1ddegQJAAYzOz+o6MFBTNZ4aLr0eEwKhLQRq3bIcUv5vweMqLqZ2" + 
     			"DfcHyjzkIONuXtwzxhYqVb6RN3Qk78vduZ2L09D10g==" + 
     			"-----END RSA PRIVATE KEY-----";
     	
     	publicKeyStr = pemToKey(publicKeyStr);
     	privateKeyStr = pemToKey(privateKeyStr);
//     	publicKeyStr = RSAPkcsTransformer.formatPkcs1ToPkcs8(publicKeyStr);
//     	privateKeyStr = RSAPkcsTransformer.formatPkcs1ToPkcs8(privateKeyStr);
     	

         //加密
         String data = "i like java 模压模压模压模压顶替顶替顶替富商大贾克林霉素枯";
         String privateEncryptStr = BakRSAUtils.privateEncrypt(data, privateKeyStr);
         String publicEncryptStr = BakRSAUtils.publicEncrypt(data, publicKeyStr);
         String privateEncryptSign = BakRSAUtils.rsaSign(privateEncryptStr,privateKeyStr);
         String publicEncryptSign = BakRSAUtils.rsaSign(publicEncryptStr,privateKeyStr);

         System.out.println("source:" + data);
         System.out.println("private encryptStr: " + privateEncryptStr);
         System.out.println("public encryptStr: " + publicEncryptStr);
         System.out.println("private encrypt sign: " + privateEncryptSign);
         System.out.println("public encrypt sign: " + publicEncryptSign);
         System.out.println("public decrypt:" + BakRSAUtils.publicDecrypt(privateEncryptStr, publicKeyStr));
         System.out.println("private decrypt:" + BakRSAUtils.privateDecrypt(publicEncryptStr, privateKeyStr));
         System.out.println("verifySign1: " + BakRSAUtils.verifySign(privateEncryptStr,privateEncryptSign,publicKeyStr));
         System.out.println("verifySign2: " + BakRSAUtils.verifySign(publicEncryptStr,publicEncryptSign,publicKeyStr));

         System.out.println("\r\n");
         publicEncryptStr = "WopnO2LnolZ7XpOwA_ktOhfkkaQQJQgkJudk3ZH_-ob36GQFv968nE1UBXxNekA9pIHBcvcl0ZWfwFhk-kyOF2FmQvpPY9LkqiCV0T32vhJet0n93ti2PBoFILxvChjzdOgSG9M0flH78Vm696Q4mHo7VMt_XMoHDTd3Rbagvt8";
         privateEncryptStr = "Fwb5BtLRveCWbx7FkXarl1zVOdwDvbDTl7gv-vPHXpj-T2wm9GlUDn3X0wnHHXkE8cqAT6PcE0g0ide6beP9_ysHMLgnC6wVqkomIKsi6C9TcGd4d6XQBjeJgdgccvDcD-7pcKrV9W-_Z7jkYkwwrjPGPd_uckEHR_cDXyOX4PU";
         System.out.println("php >>>> private decrypt: " + BakRSAUtils.privateDecrypt(publicEncryptStr, privateKeyStr));
         System.out.println("php >>>> public decrypt: " + BakRSAUtils.publicDecrypt(privateEncryptStr, publicKeyStr));

         publicEncryptStr = "T2LFtY3dF_b6OBO07BN-3LtMSEBZqDukovDZ4HGCff8wosvlowf6IFJ3U7LFBIeHfiHBKiFuAV8-pFltCfTXtA4AwgVUnwbBMBWBfIJiLDi02ev30V-5BcYEuSF-cEdnSUd7WecrX4rHhzYLueGuj8H6c7RRbSbrJ6_3EFfU-K0";
         System.out.println("js >>>> private decrypt: " + BakRSAUtils.privateDecrypt(publicEncryptStr, privateKeyStr));
    }

    public  static  void main(String[ ] asdfs) throws Exception {
       
    	test1();
    	
//    	String pubkey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCxpeIca4Q5nAPurAOUOmUIHTyWROxCXyIEkT0Hv+fN91pNyto9o4c3Yy57Z9dgAmlFVdc1Plg/2bYOvb6EMZ5hw1By19GBYE0ejZK51ebhL2GGSvOBnsiREVSHTnlG59790bzIWXM+rtHICSZpmDjaPRQasv11HgddcfQx/BciqQIDAQAB";
//    	
//    	String privatekey = "MIICXAIBAAKBgQCxpeIca4Q5nAPurAOUOmUIHTyWROxCXyIEkT0Hv+fN91pNyto9" + 
//    			"o4c3Yy57Z9dgAmlFVdc1Plg/2bYOvb6EMZ5hw1By19GBYE0ejZK51ebhL2GGSvOB" + 
//    			"nsiREVSHTnlG59790bzIWXM+rtHICSZpmDjaPRQasv11HgddcfQx/BciqQIDAQAB" + 
//    			"AoGAHwon/uU+Xj6HZV2C5pRpcMiC/JKgYVxz+i5SZpc+bnuIuMz34ws+pgvbZE6Z" + 
//    			"kDn7oNTF3JS/ILDuoZa/wiNVU98guKOpGlrD6Nrfd5XfrJVkx3BDG1rDVJVt6QuI" + 
//    			"wCfyzQvADVbHywcIQO1uM4AVXP7bzAhqzshJCsud+itHYjECQQDXTlK7M6dE0Djg" + 
//    			"N9DRj0TP5TwlYUg5gkt28BO3dkBvenV5FpZTWjocG9NVrxRO6vQSXybsKtnD2LzQ" + 
//    			"Ybpzq27dAkEA0zl2ST7FYt12NuH/f+YTgdxydDU1fHcAICkQI9C1ZC8I1kQd+V/H" + 
//    			"gAPHR8vRiQtuvEWwMHV9NtGUV8eXAS0YPQJAC1gQGdbt4D1MUdv+/5uZ145PbfTD" + 
//    			"Nmx8B2c80rTDTlxYZinff8LIPwTkTKZt3n6Yn4Mt7NGRKbV4tU4A4elNbQJAKUep" + 
//    			"nlbuLIwrdj5kuJI2bcKve5aKhXCcTay4wscfLB0lFPrrALl/O72BTL7AjI/3bEQS" + 
//    			"UuYjOntrClyvXkN54QJBAL7ILbIThYsH2zDX++Y3pYktgyL39Y1UhH9oj4/voJPo" + 
//    			"QbnKQyeD9LlAXHFBA1myZUHox5mzSY5bxpuAWqitZS4=";
//    	
//    	
//    	String encryptDataString = "MTY2LDMyLDcsNCw0OCw0OCwxODAsNTIsMTY3LDIwNCw4MywxNzksMTY3LDQ0LDkyLDIwLDEyNSwxODcsMjEzLDI2LDM4LDI0OCw5MSwyNTQsMjQ3LDE0OSw5LDk0LDEzNCwxNywyMSwxNzksNjYsMCwyMjIsOSw0OSwyNTQsMTU2LDIzNCw0Nyw3MSwxNzgsMTIyLDQ1LDIxMywyMCwxMDMsMTU2LDE5MywyNTQsMjM5LDIyLDE5OCwxNzgsNTMsMTIyLDI0MiwxMjYsMTE0LDE1LDk2LDUzLDEyNCwzMSwxMjYsMjI0LDU0LDEzMiwyNTUsOTYsNTgsNTEsNjAsMjU0LDE1MywyMzgsNjIsMzgsMjM5LDU3LDc2LDEwOCwyMDYsMjIzLDI1MCwyOSwyOSw1OCwxMDcsMTA0LDIzNSwxNTgsMjA1LDEzNSwyMDYsMTkxLDQ5LDIzMywyMzAsMjM0LDE4NiwyMzMsMTE5LDEyNiw2MCwyMSw1MiwyMTIsMjIyLDI0OCwyMzEsMjIyLDE4OSwxMjQsMjQ1LDIyNywxNjYsMjExLDExLDIxMCwyMTMsMTk2LDE1MSwxNDMsMjE3LDIyMCwxODIsOTcsMjMsMTU3LDQ2LDcxLDIzNCwxNjIsMTAxLDEwOCw1OCwxNjcsMTg3LDQ3LDE2OCw0MywxNzUsOTMsMTExLDE3MSwyNDEsMTU1LDQyLDM2LDE1OSwyMDksMTkzLDkzLDEzNCwyMDEsNDgsNDcsMTk5LDcxLDEyNiw0Myw2NiwxMjEsMjAxLDU1LDY1LDI1MCwxMzAsMjMsMjU1LDE4NCw5Miw4NiwxNTksMTU1LDIzLDE5NiwzNyw5OCwxMzQsMTM5LDEzLDE3LDExNCwxNDgsMTY3LDEyMiwxNjUsNDcsOTIsNzEsMTg4LDQ0LDEwLDY2LDE2MywyMDUsMjIwLDI0NCwxMDksMTM2LDk4LDIzOCw4Niw4OCwzNCwzLDkzLDcyLDE4NCwxNTMsMTk2LDIyNiw0NSw4OSw2LDE1OCwxOCwxMjUsMTg4LDEzNSwyMzgsMTg2LDE5MCwxMTUsMjA0LDE1Niw0NiwxODcsMTE0LDIxMyw5NiwxMDYsMjAwLDk1LDIyMSw2MiwxMzcsMTk2LDI0MSwyMTMsMTUyLDIxOCwxNzUsMTMxLDMsMTcwLDE1OSwxNjUsMTgzLDEwOSwyMDksMTc4LDMzLDgzLDc0LDg3LDExOSwxODEsMTUzLDE3OSwyMCwxNTMsMTU3LDIzMywxODIsMjI5LDM3LDEyNywxMiwyNCwxODksMTIsMTcyLDIxMSwxMTgsNjAsMjM1LDIxMiw4OSw3OCwxOTgsMTc1LDExNiwxNTIsNDgsMTksOSwyMiw0NiwxNzEsMjA4LDE5MCwyMTgsOTAsMTY0LDE1NywyNDIsMjgsMTc4LDIzOCwxNzIsMTI3LDE2Myw1NSwyMTEsMTMwLDE2MSwyMTQsMTAxLDE1OSw5MywxNjcsNTAsNDEsNjAsMTQzLDE4NiwyNDQsMjEsMjQ1LDAsMTE1LDE1MSw5OCwxMjEsMjUsMTI4LDIyNiwyNDUsMjI4LDcwLDIyMiwxMzIsMTU0LDE1Miw4Nyw1OSwxMjgsNiwyMzYsMTMwLDI0OSwxNDMsMTQ5LDQzLDMwLDE5Niw1MiwzMSwxODcsMTgwLDE2OSw1NSw2LDI0OSwyMjUsOTIsMTY2LDM3LDI1MywyMjAsMTQzLDE0NiwxNDcsNDEsMTM2LDM4LDI1NSwxODgsMTk4LDE0NiwxNTcsMzksMTEzLDEwNywxMDgsMTExLDI0NywxNzIsNDUsMjQwLDE0MiwyMjUsMjIsMjMzLDUxLDkwLDIxNCwzLDE0OSwyMzksOTksMjMxLDE0NywxNCwxOTcsMjUwLDY1LDUyLDMyLDE2OCwxNzQsMTk0LDgzLDIzMiwyNTAsMjE2LDEyMiwzOSwyNTIsMTU5LDEwMCwxMjUsODcsMiw5MCwzMSwyOCwxNzIsNjgsMjMxLDI5LDE0Nyw3MSw5Nyw0LDQ3LDI1MywxMTYsMTQ5LDQzLDUwLDgsMTc0LDQ5LDIwNiwxOTMsMTU0LDE3MCwyMDAsMjQ5LDI0NiwzMywyMzYsMTQwLDQ5LDk2LDIxOSwzMyw1MSwxNjcsMjUxLDIzMywxMjAsMTk0LDI5LDcxLDEwNywyMywyMjQsMTU2LDE3MywyMTAsNCwyMTgsMjE3LDg0LDE2Miw5MywyMTcsNjUsODQsMjM0LDQxLDE3NywxMjEsMTQ3LDIxNCwxNjEsMTk5LDEyMCw5LDIzMyw4OCw1OSwxNjEsMjA2LDI0MCwxMDAsNzEsNjYsOTUsMTMwLDE3NCwxODMsMjQ3LDE4MCwxNDEsMTg0LDkwLDEyNCw5NSw1MywyMzcsMTA2LDMxLDEwMCw0MiwyMjU=";
//    	
//    	String decryptDataString = privateDecrypt(encryptDataString, privatekey);
//    	
//    	System.out.println(decryptDataString);
    	
    	
    }

}
