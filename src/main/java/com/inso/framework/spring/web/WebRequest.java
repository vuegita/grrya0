package com.inso.framework.spring.web;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.inso.framework.context.MyEnvironment;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;
import com.inso.framework.utils.UrlUtils;

public class WebRequest {
	
	private final static  String DEFAULT_ADD_SOURCE = "%2B";
	private final static  String DEFAULT_ADD_TARGET = "+";
	
	public static HttpServletRequest getHttpServletRequest(){  
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder  
                .getRequestAttributes())
                .getRequest();
        return request;  
    } 
	
	public static HttpServletResponse getHttpServletResponse(){  
        HttpServletResponse res = ((ServletRequestAttributes)RequestContextHolder  
                .getRequestAttributes()).getResponse();
        return res;  
    } 
	
	public static HttpSession getSession()
	{
		return (HttpSession) getHttpServletRequest().getSession();
	}
	
	public static String getHeader(String key)
	{
		HttpServletRequest request = getHttpServletRequest();
		return request.getHeader(key);
	}
	
	public static double getDouble(String key)
	{
		String value = getString(key);
		return StringUtils.asDouble(value);
	}

	public static float getFloat(String key)
	{
		String value = getString(key);
		return StringUtils.asFloat(value);
	}
	
	public static long getLong(String key)
	{
		String value = getString(key);
		return StringUtils.asLong(value);
	}
	
	public static int getInt(String key)
	{
		String value = getString(key);
		return StringUtils.asInt(value);
	}

	public static int getInt(String key, int minValue, int maxValue)
	{
		String value = getString(key);
		int intValue = StringUtils.asInt(value);
		if(intValue < minValue)
		{
			return minValue;
		}
		if(intValue > maxValue)
		{
			return maxValue;
		}
		return intValue;
	}

	public static <T> List<T> getList(String key, Class<T> cls)
	{
		String value = getString(key);
		if(StringUtils.isEmpty(value))
		{
			return Collections.emptyList();
		}
		return FastJsonHelper.parseArray(value, cls);
	}
	
	public static String getString(String key)
	{
		return getString(key, null); 
	}

	public static BigDecimal getBigDecimal(String key)
	{
		String value = getString(key);
		return StringUtils.asBigDecimal(value);
	}
	
	public static String[] getStrings(String key, String split)
	{
		String value = getString(key, null); 
		if(StringUtils.isEmpty(value)) return null;
		return value.split(split); 
	}
	
	public static boolean getBoolean(String key)
	{
		String value = getString(key);
		return StringUtils.asBoolean(value, false);
	}

	public static boolean getPurge()
	{
		String value = getString("purge");
		return StringUtils.asBoolean(value, false);
	}

	public static int getLimit()
	{
		String value = getString("limit");
		int limit = StringUtils.asInt(value);
		if (0 == limit) limit = 20;
		if (limit > 20) limit = 20;
		return limit;
	}
	
	public static String getLoginToken()
	{
		return getString("loginToken");
	}
	
	public static String getAccessToken()
	{
		try {
			String key = "accessToken";
			String value = getHeader(key);
			if(StringUtils.isEmpty(value))
			{
				value = getString(key);
			}
			return value;
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return StringUtils.getEmpty();
	}
	
	public static Date getDate(String key, String parttern)
	{
		String value = getString(key);
		return DateUtils.convertDate(parttern, value);
	}

	public static Date getDate(String key)
	{
		return getDate(key, DateUtils.TYPE_YYYYMMDDHHMMSS);
	}
	
	public static String getString(String key, String def)
	{
		HttpServletRequest request = getHttpServletRequest();
		String value = request.getParameter(key);
		if(StringUtils.isEmpty(value)) return def;
		value = UrlUtils.decode(value);
		value = value.replace(DEFAULT_ADD_SOURCE, DEFAULT_ADD_TARGET);
		value = value.trim();
		value = StringEscapeUtils.escapeSql(value);
		return value; 
	}
	
	public static String getCookie(String key)
	{
		HttpServletRequest request = getHttpServletRequest();
		Cookie[] cookies =  request.getCookies();
		if(cookies == null || cookies.length == 0) return null;
		
		for(Cookie cookie : cookies){
			if(cookie.getName().equals(key)){
				return cookie.getValue();
			}
	    }
		return null;
	}
	
	public static void setCookie(String key, String value, int expires)
	{
		HttpServletResponse response = getHttpServletResponse();
		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(expires);
		response.addCookie(cookie);
	}
	
	public static void logForm()
	{
		HttpServletRequest request = getHttpServletRequest();
		Enumeration<String> names = request.getParameterNames();
		while(names.hasMoreElements())
		{
			String name = names.nextElement();
			String value = request.getParameter(name);
			System.out.println(name + " = " + value);
		}
	}
	
	public static void logHeader()
	{
		HttpServletRequest request = getHttpServletRequest();
		Enumeration<String> names = request.getHeaderNames();
		while(names.hasMoreElements())
		{
			String name = names.nextElement();
			String value = request.getParameter(name);
			System.out.println(name + " = " + value);
		}
	}
	
	public static JSONObject getJSON(String key)
	{
		HttpServletRequest request = getHttpServletRequest();
		String value = request.getParameter(key);
		return FastJsonHelper.toJSONObject(value);
	}
	
	/*
	public static String replaceXSS(String value) {  
        if (value != null) {  
            try{  
                value = value.replace("+","%2B");   //'+' replace to '%2B'  
                value = URLDecoder.decode(value, "utf-8");  
            }catch(UnsupportedEncodingException e){  
            }catch(IllegalArgumentException e){  
        }  
              
            // Avoid null characters  
            value = value.replaceAll("\0", "");  
  
            // Avoid anything between script tags  
            Pattern scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);  
            value = scriptPattern.matcher(value).replaceAll("");  
  
            // Avoid anything in a src='...' type of e­xpression  
            scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);  
            value = scriptPattern.matcher(value).replaceAll("");  
  
            scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);  
            value = scriptPattern.matcher(value).replaceAll("");  
  
            // Remove any lonesome </script> tag  
            scriptPattern = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);  
            value = scriptPattern.matcher(value).replaceAll("");  
  
            // Remove any lonesome <script ...> tag  
            scriptPattern = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);  
            value = scriptPattern.matcher(value).replaceAll("");  
  
            // Avoid eval(...) e­xpressions  
            scriptPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);  
            value = scriptPattern.matcher(value).replaceAll("");  
  
            // Avoid e­xpression(...) e­xpressions  
            scriptPattern = Pattern.compile("e­xpression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);  
            value = scriptPattern.matcher(value).replaceAll("");  
  
            // Avoid javascript:... e­xpressions  
            scriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);  
            value = scriptPattern.matcher(value).replaceAll("");  
            // Avoid alert:... e­xpressions  
            scriptPattern = Pattern.compile("alert", Pattern.CASE_INSENSITIVE);  
            value = scriptPattern.matcher(value).replaceAll("");  
            // Avoid onload= e­xpressions  
            scriptPattern = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);  
            value = scriptPattern.matcher(value).replaceAll("");  
            scriptPattern = Pattern.compile("vbscript[\r\n| | ]*:[\r\n| | ]*", Pattern.CASE_INSENSITIVE);    
            value = scriptPattern.matcher(value).replaceAll("");  
        }             
        return value;  
    }  */
	
	/** 
     * 过滤特殊字符 
     */  
	/*
    public static String filter(String value) {  
        if (value == null) {  
            return null;  
        }          
        StringBuffer result = new StringBuffer(value.length());  
        for (int i=0; i<value.length(); ++i) {  
            switch (value.charAt(i)) {  
                case '<':  
                    result.append("<");  
                    break;  
                case '>':   
                    result.append(">");  
                    break;  
                case '"':   
                    result.append("\"");  
                    break;  
                case '\'':   
                    result.append("'");  
                    break;  
                case '%':   
                    result.append("%");  
                    break;  
                case ';':   
                    result.append(";");  
                    break;  
                case '(':   
                    result.append("(");  
                    break;  
                case ')':   
                    result.append(")");  
                    break;  
                case '&':   
                    result.append("&");  
                    break;  
                case '+':  
                    result.append("+");  
                    break;  
                default:  
                    result.append(value.charAt(i));  
                    break;  
            }    
        }  
        return result.toString();  
    }  */

	public static String getUserAgent()
	{
		String useragent = WebRequest.getHeader("user-agent");
		return useragent;
	}
	public static String getRemoteIP() {
		HttpServletRequest request = getHttpServletRequest();
		String ip = request.getHeader("X-Real-IP");
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			 ip = request.getHeader("x-forwarded-for"); 
		}
	    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	      ip = request.getHeader("Proxy-Client-IP"); 
	    } 
	    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	      ip = request.getHeader("WL-Proxy-Client-IP"); 
	    } 
	    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	      ip = request.getHeader("HTTP_CLIENT_IP"); 
	    } 
	    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	      ip = request.getHeader("HTTP_X_FORWARDED_FOR"); 
	    } 
	    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	      ip = request.getRemoteAddr(); 
	    } 
	    return ip; 
	  }

	public static String getRemoteIP(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
	
	public static void main(String args[]){  
        
//        String sql="1' or '1'='1";  
//        System.out.println("防SQL注入======> "+StringEscapeUtils.escapeSql(sql)); //防SQL注入  
//          
//        System.out.println("转义HTML,注意汉字======> "+StringEscapeUtils.escapeHtml("<font>chen磊  xing</font>"));   //转义HTML,注意汉字  
//        System.out.println("反转义HTML======> "+StringEscapeUtils.unescapeHtml("<font>chen磊  xing</font>")); //反转义HTML  
//          
//        System.out.println("转成Unicode编码======> "+StringEscapeUtils.escapeJava("张三"));  //转义成Unicode编码  
//        System.out.println("转义XML======> "+StringEscapeUtils.escapeXml("<name>张三</name>"));    //转义xml  
//        System.out.println("反转义XML======> "+StringEscapeUtils.unescapeXml("<name>张三</name>"));     //转义xml  
//          
		String str = "什么";
//		int len = ;
		System.out.println(UrlUtils.encode(str).indexOf("%"));
//		if(value.indexOf("%"))
    }  
	

}
