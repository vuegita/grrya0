package com.inso.framework.utils;

import java.util.List;

import com.google.common.collect.Lists;

public class SpiderUtils {
	
	private static List<String> mSpiderKeywordList = null;
	
	private static List<String> mSpiderIPList = null;
	
	static {
		synchronized (SpiderUtils.class) {
			if(mSpiderKeywordList == null)
			{
				mSpiderKeywordList = Lists.newArrayList();
				mSpiderKeywordList.add("spider");
				mSpiderKeywordList.add("google");
				mSpiderKeywordList.add("googlebot");
				mSpiderKeywordList.add("bingbot");
				mSpiderKeywordList.add("360spider");
				mSpiderKeywordList.add("yahoo");
				mSpiderKeywordList.add("yisouspider");
			}
			
			if(mSpiderIPList == null)
			{
				mSpiderIPList = Lists.newArrayList();
				
				// bingbot
				mSpiderIPList.add("207.46.13");
				
				// googlebot
				mSpiderIPList.add("203.208.60");
				
				// baidu
				mSpiderIPList.add("220.181.108");
				mSpiderIPList.add("116.179.32");
				
				// sogou
				mSpiderIPList.add("49.7.21");
			}
		}
	}
	
	public static boolean isSpider(String userAgent)
	{
		if(StringUtils.isEmpty(userAgent))
		{
			return false;
		}
		userAgent = userAgent.toLowerCase();
		for(String keyword : mSpiderKeywordList)
		{
			if(userAgent.contains(keyword))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 判断是不是爬虫ip, 精准验证
	 * @param ip
	 * @return
	 */
	public static boolean isSpiderbot(String ip)
	{
		if(StringUtils.isEmpty(ip))
		{
			return false;
		}
		for(String tmp : mSpiderIPList)
		{
			if(ip.startsWith(tmp))
			{
				return true;
			}
		}
		return false;
	}

}
