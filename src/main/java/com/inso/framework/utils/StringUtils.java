package com.inso.framework.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    public static final String EMPTY = "";
    public static final String BLANK = " ";
    private static final String NULL = "null";
    public static final String COMMA = ",";
    public static final char CH_COMMA = ',';
    public static final String URL_SPLIT = "/";
    public static final String QUESTION_MARK  = "?";
    public static final String CENTER_LINE = "-";
    public static final String DOT = ".";

    public static final String VERTICAL_LINE = "|";

    public static final String DIVIDER_LINE_BOTTOM = "_";


    
    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    public static final Pattern KVP_PATTERN = Pattern.compile("([_.a-zA-Z0-9][-_.a-zA-Z0-9]*)[=](.*)"); //key value pair pattern.

    public static final Pattern INT_PATTERN = Pattern.compile("^\\d+$");

    private static final Pattern mPatternHeadPeak = Pattern.compile("_(\\w)");
    
    public static final String UTF8 = "utf-8";

    public static final BigDecimal mBigdecimal_100 = new BigDecimal(100);
    
    
    public static String getBottomDividerLine()
    {
    	return DIVIDER_LINE_BOTTOM;
    }
    
    public static String getEmpty() {
        return EMPTY;
    }

    public static String getBlank() {
        return BLANK;
    }
    
    public String getNotNull(String input)
    {
    	if(isEmpty(input))
    	{
    		return EMPTY;
    	}
    	return input;
    }

    public static int asInt(Object input) {
        return asInt(input, 0);
    }

    public static int asInt(Object input, int defValue) {
        int rs = defValue;
        try {
            String str = asString(input);
            if (input != null) rs = Integer.parseInt(str);
        } catch (NumberFormatException e) {
        }
        return rs;
    }

    public static boolean asBoolean(String input) {
        return asBoolean(input, false);
    }

    public static boolean asBoolean(String input, boolean def) {
        boolean rs = def;
        try {
            if (input != null) {
                input = input.toUpperCase();
                if (input.equalsIgnoreCase("1") ||
                        input.equalsIgnoreCase("TRUE") ||
                        input.equalsIgnoreCase("YES")) {
                    rs = true;
                } else {
                    rs = false;
                }
            }
        } catch (Exception e) {
        }
        return rs;
    }

    public static long asLong(Object input) {
        long rs = 0;
        try {
            String str = asString(input);
            if (input != null) rs = Long.parseLong(str);
        } catch (NumberFormatException e) {
        }
        return rs;
    }

    public static float asFloat(Object input) {
        float rs = 0;
        try {
            String str = asString(input);
            if (input != null) rs = Float.parseFloat(str);
        } catch (NumberFormatException e) {
        }
        return rs;
    }

    public static double asDouble(Object input) {
        double rs = 0;
        try {
            String str = asString(input);
            if (input != null) rs = Double.parseDouble(str);
        } catch (NumberFormatException e) {
        }
        return rs;
    }

    public static String asString(Object input) {
        return asString(input, null);
    }

    public static String asString(Object input, String def) {
        String rs = def;
        try {
            if (input != null) {
                if (input instanceof String) {
                    rs = (String) input;
                } else {
                    rs = input.toString();
                }
            }
        } catch (Exception e) {
        }
        return rs;
    }

    public static BigDecimal asBigDecimal(String input)
    {
        try {
            return new BigDecimal(input);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public static boolean isEmpty(String input) {
        if (input == null || input.length() == 0 || input.equalsIgnoreCase(EMPTY) || NULL.equalsIgnoreCase(input)) {
            return true;
        }
        return false;
    }
    
    public static String getNotEmpty(String input)
    {
    	if(isEmpty(input))
    	{
    		return EMPTY;
    	}
    	return input;
    }

    public static boolean isMaxLength(String input, int length) {
        if (input == null || input.length() == 0 || input.equalsIgnoreCase(EMPTY) || input.length() > length) {
            return true;
        }
        return false;
    }

    /**
     * split.
     *
     * @param ch char.
     * @return string array.
     */
    public static String[] split(String str, char ch) {
        List<String> list = null;
        char c;
        int ix = 0, len = str.length();
        for (int i = 0; i < len; i++) {
            c = str.charAt(i);
            if (c == ch) {
                if (list == null)
                    list = new ArrayList<String>();
                list.add(str.substring(ix, i));
                ix = i + 1;
            }
        }
        if (ix > 0)
        {
        	list.add(str.substring(ix));
        }
        if(!CollectionUtils.isEmpty(list))
        {
        	return (String[]) list.toArray(EMPTY_STRING_ARRAY);
        }
        else if(list == null && !StringUtils.isEmpty(str))
        {
        	String[] array = new String[1];
        	array[0] = str;
        	return array;
        }
        else 
        {
        	return EMPTY_STRING_ARRAY;
        }
    }
    
    public static String[] split(String str)
    {
    	return split(str, CH_COMMA);
    }

    /**
     * join string.
     *
     * @param array String array.
     * @return String.
     */
    public static String join(String[] array) {
        if (array.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (String s : array)
            sb.append(s);
        return sb.toString();
    }

    /**
     * join string like javascript.
     *
     * @param array String array.
     * @param split split
     * @return String.
     */
    public static String join(String[] array, char split) {
        if (array.length == 0) return EMPTY;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (i > 0)
                sb.append(split);
            sb.append(array[i]);
        }
        return sb.toString();
    }

    /**
     * join string like javascript.
     *
     * @param array String array.
     * @param split split
     * @return String.
     */
    public static String join(String[] array, String split) {
        if (array.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (i > 0)
                sb.append(split);
            sb.append(array[i]);
        }
        return sb.toString();
    }

    public static String join(Collection<String> coll, String split) {
        if (coll.isEmpty()) return EMPTY;
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (String s : coll) {
            if (isFirst) isFirst = false;
            else sb.append(split);
            sb.append(s);
        }
        return sb.toString();
    }

    public static String join(String input, String split, int len) {
        if (len == 0) return EMPTY;
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (int i = 0; i < len; i++) {
            if (first) first = false;
            else sb.append(split);
            sb.append(input);
        }
        return sb.toString();
    }

    public static String upperFirst(String input) {
        char[] chString = input.toCharArray();
        char ch = chString[0];
        if (ch >= 97 && ch <= 122) {
            chString[0] -= 32;
            return String.valueOf(chString);
        } else {
            return input;
        }
    }

    public static String lowerFirst(String input) {
        char[] chString = input.toCharArray();
        char ch = chString[0];
        if (ch >= 65 && ch <= 90) {
            chString[0] += 32;
            return String.valueOf(chString);
        } else {
            return input;
        }
    }

    public static String listToString(List<?> list, char separator) {
        if (list == null) return EMPTY;
        Object[] array = list.toArray();
        int startIndex = 0;
        int endIndex = array.length;
        int bufSize = (endIndex - startIndex);
        if (bufSize <= 0) {
            return EMPTY;
        }

        bufSize *= ((array[startIndex] == null ? 16 : array[startIndex].toString().length()) + 1);
        StringBuffer buf = new StringBuffer(bufSize);

        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }

    /**
     * 头峰
     *
     * @param str
     * @return
     */
    public static String getHeadPeak(String str) {
        str = str.toLowerCase();
        StringBuffer sb = new StringBuffer();
        Matcher m = mPatternHeadPeak.matcher(str);
        while (m.find()) {
            m.appendReplacement(sb, m.group(1).toUpperCase());
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * 驼峰格式字符串转换为下划线格式字符串
     *
     * @param param
     * @return
     */
    public static String camelToUnderline(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append("_");
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
    
    /**
     * 比较两个字符串大小
     *  a < b,   aaaaaaaa < b, a < bbbbbb
     * @param str1
     * @param str2
     * @return
     */
    public static String max(String str1, String str2)
    {
		int lenStr1 = str1.length();
		int lenStr2 = str2.length();
		
		int len = Math.min(lenStr1, lenStr2);
		for(int i = 0; i < len; i ++)
		{
			int rs = str1.charAt(i) - str2.charAt(i);
			if(rs > 0)
			{
				return str1;
			} 
			if(rs < 0) {
				return str2;
			}
		}
		if(lenStr1 > lenStr2)
		{
			return str1;
		}
		else {
			return str2;
		}
    }

    public static void main(String[] args) {
        System.out.println(max("ac", "bc"));
    }

}
