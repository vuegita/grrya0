package com.inso.framework.cache;

import java.util.List;
import java.util.Map;

import com.beust.jcommander.internal.Maps;
import com.inso.framework.cache.impl.PikaCacheImpl;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;

public class CacheManager {

	public static final int EXPIRES_FOREVER = -1;
    public static final int EXPIRES_MINUTES_10 = 600;
    public static final int EXPIRES_HOUR = 3600;
    public static final int EXPIRES_HOUR_2 = 3600 * 2;
    public static final int EXPIRES_HOUR_5 = 3600 * 5;
    public static final int EXPIRES_DAY = 86405;
    public static final int EXPIRES_HOUR_36 = 129600;
    public static final int EXPIRES_WEEK = 86400 * 7;
    public static final int EXPIRES_MONTH = 86400 * 30;
    public static final int EXPIRES_FIVE_MINUTES = 300;
    //	private static final CacheSupport support = MyBeanFactory.getInstance(LocalMemoryImpl.class);
    private CacheService support;

    private interface CacheManagerInternal {
        public CacheManager mgr = new CacheManager();
    }

    public static CacheManager getInstance() {
        return CacheManagerInternal.mgr;
    }

    private CacheManager() {
    	support = new PikaCacheImpl();
    }

    public void setString(String key, String value, int seconds) {
        support.setString(key, value, seconds);
    }
    
    public void setString(String key, String value) {
        support.setString(key, value, EXPIRES_HOUR);
    }

    public String getString(String key) {
        return support.getString(key);
    }
    
    public boolean exists(String key)
    {
    	return support.exists(key);
    }
    
    public <T> T getObject(String key, Class<T> clazz)
    {
    	String value = getString(key);
    	//if(StringUtils.isEmpty(value)) return null;
    	return FastJsonHelper.jsonDecode(value, clazz);
    }
    
    public long getLong(String key)
    {
    	String value = getString(key);
    	return StringUtils.asLong(value);
    }

    public int getInt(String key)
    {
        String value = getString(key);
        return StringUtils.asInt(value);
    }

    public float getFloat(String key)
    {
        String value = getString(key);
        return StringUtils.asFloat(value);
    }
    
    public <T> List<T> getList(String key, Class<T> clazz)
    {
    	List<T> list = null;
    	String value = getString(key);
    	if(!StringUtils.isEmpty(value)) {
    		list = FastJsonHelper.parseArray(value, clazz);
    	}
    	return list;
    }

    public void delete(String key) {
        support.delete(key);
    }
    
	public <T> List<T> getMultiString(Class<T> clazz, String... keys)
	{
		return support.getMultiString(clazz, keys);
	}
	public void setMultiKeys(Map<String, Object> keyValue)
	{
		support.setMultiKeys(keyValue);
	}

    public static void main(String[] args) {
        CacheManager manager = CacheManager.getInstance();
        
        Map<String, Object> keyvalue = Maps.newHashMap();
        
        
        for(int i = 0; i < 3; i ++)
        {
        	
        	keyvalue.put("test" + i, "value = " + i);
        }
        manager.setMultiKeys(keyvalue);
        List<String> list = manager.getMultiString(String.class, "a0", "test1", "test2");
        System.out.println(FastJsonHelper.jsonEncode(list));
        
        
        manager.setString("test1111", "fdsfasdf");
        
        String rs = manager.getString("test1111");
        System.out.println(rs);
    }

}
