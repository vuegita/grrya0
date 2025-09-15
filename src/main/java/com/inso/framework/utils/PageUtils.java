package com.inso.framework.utils;

import java.util.Map;

import com.beust.jcommander.internal.Maps;

/**
 * 分页工具类
 * @author Administrator
 *
 */
public class PageUtils {
	
	private static final String DEFAULT_SALT = "sdfaslkjsfdkj(&^%";
	
	public static long getTotalPage(long totalcount, long pagesize)
	{
		return (totalcount + pagesize - 1) / pagesize;
	}
	
	public static int getPageValue(int value, int min, int max)
	{
		if(value < min)
		{
			value = min;
		}
		if(value > max)
		{
			value = max;
		}
		return value;
	}
	
	private static String generatePageSign(long milliseconds, long page, String extra)
	{
		String sign = MD5.encode(DEFAULT_SALT + milliseconds + page + extra);
		return sign;
	}
	
	public static String createPageToken(long page, String extra)
	{
		extra = encryExtra(extra);
		long time = System.currentTimeMillis();
		String sign = generatePageSign(time, page, extra);
		Map<String, Object> data = Maps.newHashMap();
		data.put("time", time);
		data.put("page", page);
		data.put("sign", sign);
		data.put("extra", extra);
		String jsonString = FastJsonHelper.jsonEncode(data);
		return Base64Utils.encode(jsonString);
	}
	
	public static boolean verifyPageToken(String pageToken, long targetPage)
	{
		try {
			String jsonString = Base64Utils.decode(pageToken);
			Map<String, Object> data = FastJsonHelper.toMap(jsonString);
			long time = StringUtils.asLong(data.get("time"));
			String sign = StringUtils.asString(data.get("sign"));
			String extra = StringUtils.asString(data.get("extra"));
			return verifyPageSign(time, targetPage, sign, extra);
		} catch (Exception e) {
		}
		return false;
	}
	
	private static String encryExtra(String extra)
	{
		return MD5.encode(extra + DEFAULT_SALT);
	}
	
	private static boolean verifyPageSign(long time, long targetPage, String sign, String extra)
	{
		// 10s有效期
		if(System.currentTimeMillis() - time > 10000)
		{
			return false;
		}
		String encrySign = generatePageSign(time, targetPage, extra);
		return encrySign.equalsIgnoreCase(sign);
	}

}
