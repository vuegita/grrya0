package com.inso.framework.context;

import com.inso.framework.utils.StringUtils;

public class MyAppContext {
	
//	private static String mLogPreffix;
	
	public static void init(Class<?> clazz, String logName)
	{
		String name = logName;
		if(StringUtils.isEmpty(name)) name = "other";

		// 
		System.setProperty("app.name", name);
		
		// 加载环境配置
		MyEnvironment.loadEnvironment();// 配置
	}
	

}
