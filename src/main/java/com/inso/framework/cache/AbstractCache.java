package com.inso.framework.cache;

import java.util.List;
import java.util.Map;

import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;

public abstract class AbstractCache implements CacheService{

	@Override
	public void setString(String key, String value, int seconds) {
		CacheManager.getInstance().setString(key, value, seconds);
	}
	
	public void setString(String key, String value) {
		setString(key, value, getDefaultExpires());
	}

	@Override
	public String getString(String key) {
		return CacheManager.getInstance().getString(key);
	}

	@Override
	public void delete(String key) {
		CacheManager.getInstance().delete(key);
	}

	@Override
	public boolean exists(String key) {
		return CacheManager.getInstance().exists(key);
	}

	@Override
	public <T> List<T> getMultiString(Class<T> clazz, String... keys) {
		return CacheManager.getInstance().getMultiString(clazz, keys);
	}

	@Override
	public void setMultiKeys(Map<String, Object> keyValue) {
		CacheManager.getInstance().setMultiKeys(keyValue);
	}
	
	public long getLong(String key)
    {
    	return CacheManager.getInstance().getLong(key);
    }
	
	public boolean getBoolean(String key)
    {
		String value = CacheManager.getInstance().getString(key);
    	return StringUtils.asBoolean(value);
    }
    
    public <T> T getObject(String key, Class<T> clazz)
    {
    	String value = getString(key);
    	if(StringUtils.isEmpty(value)) return null;
    	return FastJsonHelper.jsonDecode(value, clazz);
    }
	
	public abstract int getDefaultExpires();
}
