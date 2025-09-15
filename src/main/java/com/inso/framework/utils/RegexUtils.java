package com.inso.framework.utils;

import com.inso.framework.spring.web.WebRequest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {
	 /**
     * 正则表达式：验证用户名
     */
    public static final String REGEX_USERNAME = "^[a-zA-Z]\\w{5,17}$";
 
    /**
     * 正则表达式：验证密码
     */
    public static final String REGEX_PASSWORD = "^[a-zA-Z0-9]{6,16}$";
 
    /**
     * 正则表达式：验证手机号 [0-9]{7,15}
     */
    public static final String REGEX_MOBILE = "[0-9]{7,15}";

    /**
     * 正则表达式：验证格伦比亚身份证号 [0-9]{8,10}
     */
    public static final String COP_ID_CARD = "[0-9]{7,10}$";
 
    /**
     * 正则表达式：验证邮箱
     */
    public static final String REGEX_EMAIL = "[0-9a-zA-Z_\\.\\-]+@[a-zA-Z0-9]+\\.[a-zA-Z0-9]+";
 
    /**
     * 正则表达式：验证汉字
     */
    public static final String REGEX_CHINESE = "^[\u4e00-\u9fa5],{0,}$";
 
    /**
     * 正则表达式：验证身份证
     */
    public static final String REGEX_ID_CARD = "(^\\d{18}$)|(^\\d{15}$)";
 
    /**
     * 正则表达式：验证URL
     */
    public static final String REGEX_URL = "http(s)?://[A-Z0-9a-z_\\-\\.\\?#=&\\{\\}\"\\/]+";

    /**
     * 正则表达式：验证缩略图
     */
    public static final String REGEX_THUMB = "[a-zA-Z0-9/\\._]+";
 
    /**
     * 正则表达式：验证IP地址
     */
    public static final String REGEX_IP_ADDR = "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)";
 
    
    public static final String REGEX_MATCH_LETTER_DIGIT = "^[a-z0-9A-Z]+$";
    public static final String REGEX_MATCH_DIGIT = "[0-9]+";

    public static final String REGEX_MATCH_LETTER_DIGIT_ = "[a-z0-9A-Z_]*";
    public static final String REGEX_MATCH_LETTER_DIGIT_ALL = "[a-z0-9A-Z_\\-]*";

    public static final String REGEX_MATCH_UPI = "[@a-z0-9A-Z_\\-]*";


    private static final String REGEX_BANK_NAME = "[a-z0-9A-Z\\s]+";

    public static boolean isLetterDigit(String str) {
    	return str.matches(REGEX_MATCH_LETTER_DIGIT);
	 }

    public static boolean isCoinAddress(String str) {
        return !StringUtils.isEmpty(str) && str.length() < 100 && str.matches(REGEX_MATCH_LETTER_DIGIT) ;
    }

	 public static boolean isUPI(String str)
     {
         return str.matches(REGEX_MATCH_UPI);
     }

    public static boolean isBankName(String str)
    {
        return str.matches(REGEX_BANK_NAME);
    }
    
    public static boolean isLetterOrDigitOrBottomLine(String str)
    {
    	 return str.matches(REGEX_MATCH_LETTER_DIGIT_);
    }
    
    public static boolean isLetterOrDigitOrDividerLine(String str)
    {
    	 return str.matches(REGEX_MATCH_LETTER_DIGIT_ALL);
    }

    public static boolean isDigit(String str) {
        return str.matches(REGEX_MATCH_DIGIT);
    }
    
    

    
    /**
     * 校验用户名
     * 
     * @param username
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isUsername(String username) {
        return Pattern.matches(REGEX_USERNAME, username);
    }
    
    
 
    /**
     * 校验密码
     * 
     * @param password
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isPassword(String password) {
        return Pattern.matches(REGEX_PASSWORD, password);
    }
 
    /**
     * 校验手机号
     * 
     * @param mobile
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isMobile(String mobile) {
        return Pattern.matches(REGEX_MOBILE, mobile);
    }


    /**
     * @return
     */
    private final static String[] ios_sys = { "iPhone", "iPad", "iPod" };
    public static boolean isIOSDevice(String userAgent) {
        // && userAgent != null && !userAgent.trim().equals("")
        boolean isMobile = false;
//        String userAgent = WebRequest.getHeader("user-agent");
        for (int i = 0; !isMobile && i < ios_sys.length; i++) {
            if (userAgent.contains(ios_sys[i])) {
                isMobile = true;
                break;
            }
        }
        return isMobile;//
    }

    /**
     * 验证格伦比亚身份证号
     *
     * @param idcard
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isCopIdCard(String idcard) {
        return Pattern.matches(COP_ID_CARD, idcard);
    }
    /**
     * 校验邮箱
     * 
     * @param email
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isEmail(String email) {
        if(email.length() > 70)
        {
            return false;
        }
        return Pattern.matches(REGEX_EMAIL, email);
    }
 
    /**
     * 校验汉字
     * 
     * @param chinese
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isChinese(String chinese) {
        return Pattern.matches(REGEX_CHINESE, chinese);
    }
 
    /**
     * 校验身份证
     * 
     * @param idCard
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isIDCard(String idCard) {
        return Pattern.matches(REGEX_ID_CARD, idCard);
    }
 
    /**
     * 校验URL
     * 
     * @param url
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isUrl(String url) {
        return Pattern.matches(REGEX_URL, url);
    }

    /**
     * 缩略图
     * @param url
     * @return
     */
    public static boolean isThumb(String url) {
        return Pattern.matches(REGEX_THUMB, url);
    }


    
    public static boolean isNumber(String number)
    {
    	return Pattern.matches("^[0-9]*?$", number);
    }
 
    /**
     * 校验IP地址
     * 
     * @param ipAddr
     * @return
     */
    public static boolean isIPAddr(String ipAddr) {
        return Pattern.matches(REGEX_IP_ADDR, ipAddr);
    }

    /**
     * 判断是否有汉字
     * @param c
     * @return
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    /**
     * 判断是不是有乱码
     * @param strName
     * @return
     */
    public static boolean isMessyCode(String strName) {
        Pattern p = Pattern.compile("\\s*|t*|r*|n*");
        Matcher m = p.matcher(strName);
        String after = m.replaceAll(StringUtils.getEmpty());
        String temp = after.replaceAll("\\p{P}", StringUtils.getEmpty());
        char[] ch = temp.trim().toCharArray();
        float chLength = ch.length;
        float count = 0;
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (!Character.isLetterOrDigit(c)) {
                if (!isChinese(c)) {
                    count = count + 1;
                }
            }
        }
        float result = count / chLength;
        if (result > 0.4) {
            return true;
        } else {
            return false;
        }

    }

    public static String toChinese(String msg){
        if(isMessyCode(msg)){
            try {
                return new String(msg.getBytes("ISO8859-1"), "UTF-8");
            } catch (Exception e) {
            }
        }
        return msg ;

    }
 
    public static void main(String[] args) {
        String mobile = "Gogo.bit999@gmail.com";
//        System.out.println(isBankName("Adfdf Adfasdfsadfasd"));
        System.out.println(RegexUtils.isEmail(mobile));
    }
}
