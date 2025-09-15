//package com.inso.framework.utils;
//
//import java.nio.charset.StandardCharsets;
//
//import javax.crypto.Mac;
//
//import org.apache.commons.codec.digest.HmacAlgorithms;
//import org.apache.commons.codec.digest.HmacUtils;
//
//public class MyHmacUtils {
//	
//	private static final String DEFAULT_KEY = "key";
//	
//	public static String hmacMD5(String input)
//	{
//		return encode(input, HmacAlgorithms.HMAC_MD5);
//	}
//	
//    public static String hmacSHA256(String input) {
//    	return encode(input, HmacAlgorithms.HMAC_SHA_256);
//    }
//
//    public static String hmacSHA384(String input) {
//    	return encode(input, HmacAlgorithms.HMAC_SHA_384);
//    }
//
//    public static String hmacSHA512(String input) {
//    	return encode(input, HmacAlgorithms.HMAC_SHA_512);
//    }
//	
//    private static String encode(String input, HmacAlgorithms algorithm) {
//        Mac mac = HmacUtils.getInitializedMac(algorithm, DEFAULT_KEY.getBytes(StandardCharsets.UTF_8));
//        byte[] content = input.getBytes(StandardCharsets.UTF_8);
//        byte[] signResult = mac.doFinal(content);
//        return bytesToHex(signResult);
//    }
//
// 
// /**
//     * 自定义字节到十六进制转换器来获取十六进制的哈希值
//     */
//    private static String bytesToHex(byte[] hash) {
//        StringBuilder hexString = new StringBuilder();
//        for (byte b : hash) {
//            String hex = Integer.toHexString(0xff & b);
//            if (hex.length() == 1) {
//                hexString.append('0');
//            }
//            hexString.append(hex);
//        }
//        return hexString.toString();
//    }
//    
//    public static void main(String[] args)
//    {
//		String str = "pangugle";
////		System.out.println(encode(str));
//		
//		System.out.println("hmacMD5 = " + MyHmacUtils.hmacMD5(str));
//		System.out.println("hmacSHA256 = " + MyHmacUtils.hmacSHA256(str));
//		System.out.println("hmacSHA384 = " + MyHmacUtils.hmacSHA384(str)); 
//		System.out.println("hmacSHA512 = " + MyHmacUtils.hmacSHA512(str)); 
//    }
//    
//}
