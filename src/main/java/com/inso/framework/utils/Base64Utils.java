package com.inso.framework.utils;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;

public class Base64Utils {

	public static String encode(String str){
		String destr="";
		try{  
		      byte[] encodeBase64 = Base64.encodeBase64(str.getBytes("UTF-8"));
		      destr= new String(encodeBase64,"utf-8");
		} catch(UnsupportedEncodingException e){  
			//
	    }  
		return destr;
	}
	
	public static byte[] decode(byte[] input)
	{
		return Base64.decodeBase64(input);
	}
	
	public static byte[] encode(byte[] input)
	{
		return Base64.encodeBase64(input);
	}
	
	public static String decode(String str){
		String destr="";
		try{  
		      byte[] decodeBase64 = Base64.decodeBase64(str.getBytes("UTF-8"));
		      destr= new String(decodeBase64,"utf-8");
		} catch(UnsupportedEncodingException e){  
			//
	    }  
		return destr;
	}
	
	public static void main(String[] args)
	{
		String str = "pangugle";
		System.out.println("原始字符串 = " + str);
		
		String encryptStr = Base64Utils.encode(str);
		System.out.println("加密 = " + encryptStr);
		System.out.println("解密 = " + Base64Utils.decode(encryptStr));
	}
	
}
