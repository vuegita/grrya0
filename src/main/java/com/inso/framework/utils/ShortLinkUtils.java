package com.inso.framework.utils;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class ShortLinkUtils {
	
	private static final String KEY = "pangugle@a*&$#lib" + UUID.randomUUID().toString();
	
	private static final String[] chars = new String[] { "a" , "b" , "c" , "d" , "e" , "f" , "g" , "h" ,
            "i" , "j" , "k" , "l" , "m" , "n" , "o" , "p" , "q" , "r" , "s" , "t" ,
            "u" , "v" , "w" , "x" , "y" , "z" , "0" , "1" , "2" , "3" , "4" , "5" ,
            "6" , "7" , "8" , "9" , "A" , "B" , "C" , "D" , "E" , "F" , "G" , "H" ,
            "I" , "J" , "K" , "L" , "M" , "N" , "O" , "P" , "Q" , "R" , "S" , "T" ,
            "U" , "V" , "W" , "X" , "Y" , "Z" 
     };

	public static String[] shortText(String src){   
        String hex = MD5.encode(KEY + src);   
        int hexLen = hex.length();   
        int subHexLen = hexLen / 8;   
        String[] shortString = new String[4];   
        for (int i = 0; i < subHexLen; i++) {   
            String outChars = "";   
            int j = i + 1;   
            String subHex = hex.substring(i * 8, j * 8);   
            long idx = Long.valueOf("3FFFFFFF", 16) & Long.valueOf(subHex, 16);   
               
            for (int k = 0; k < 6; k++) {   
                int index = (int) (Long.valueOf("0000003D", 16) & idx);   
                outChars += chars[index];   
                idx = idx >> 5;   
            }   
            shortString[i] = outChars;   
        }   
        return shortString;   
    }   
	
	public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException
	{
		String url = "http://www.pangugle.com/su/";   
		String[] shortURLs = shortText(url);
        for (String string : shortURLs) {   
            System.out.println((url + string));
        } 
	}
	
}
