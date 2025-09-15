package com.inso.framework.cache;

import java.util.List;
import java.util.Map;

public interface CacheService {

	public void setString(String key, String value, int seconds);
	public String getString(String key);
	public void delete(String key);
	public boolean exists(String key);
	
	public <T> List<T> getMultiString(Class<T> clazz, String... keys);
	public void setMultiKeys(Map<String, Object> keyValue);
	
}
