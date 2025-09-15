package com.inso.framework.spring.limit;

import com.inso.framework.cache.CacheManager;

/**
 * 继承Exception 会报错，无法初始化
 * @author Administrator
 *
 */
public class MyIPRateLimitManager{

	
    private interface MyInternal {
    	public static MyIPRateLimitManager mgr = new MyIPRateLimitManager();
    }
    
    private MyIPRateLimitManager()
    {
    }
    
    public static MyIPRateLimitManager getInstance()
    {
    	return MyInternal.mgr;
    }
    
    public <T> String getCacheKey(String ip, Class<T> clazz, String methodName)
    {
    	return ip + clazz.getName() + methodName;
    }
    
    public <T> void clearCache(String ip, Class<T> clazz, String methodName)
    {
    	String cachekey = getCacheKey(ip, clazz, methodName);
    	CacheManager.getInstance().delete(cachekey);
    }

}
