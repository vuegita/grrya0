package com.inso.framework.utils;

import java.net.URLDecoder;
import java.net.URLEncoder;

public class UrlUtils {

	public static String DEFAULT_FORMAT = "utf-8";
	
	public static String encode(String input)
	{
		try {
			return URLEncoder.encode(input, DEFAULT_FORMAT);
		} catch (Exception e) {
			return input;
		}
	}
	
	public static String decode(String input)
	{
		try {
			return URLDecoder.decode(input, DEFAULT_FORMAT);
		} catch (Exception e) {
			return input;
		}
	}

	public static String fetchMainDomain(String domain)
	{
		String[] valueArr = StringUtils.split(domain, '.');
		int len = valueArr.length;
		if(len == 2)
		{
			return domain;
		}

		int index = domain.indexOf(StringUtils.DOT) + 1;
		return domain.substring(index, domain.length());
	}

	public static void main(String[] args) {

		String str = "www.todei.com";
		System.out.println(fetchMainDomain(str));
	}
	
}
