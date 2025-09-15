package com.inso.modules.web.cache;

public class WebCacheHelper {
	
	public static String getConfigCacheKey(String key)
	{
		return KeyCacheHelper.getModuleForWebKey() + "find_configbykey" + key;
	} 
	
	public static String queryConfigListCacheKey(String prefixKey)
	{
		return KeyCacheHelper.getModuleForWebKey() + "queryConfigList" + prefixKey;
	} 

}
