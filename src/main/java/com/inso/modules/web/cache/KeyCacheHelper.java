package com.inso.modules.web.cache;

public class KeyCacheHelper {
	
	private static final String ROOT_KEY = "inso_";

	public static String getRootKey()
	{
		return ROOT_KEY;
	}
	
	public static String getModuleForWebKey()
	{
		return getRootKey() + "web_";
	}
	
}
