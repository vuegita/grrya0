package com.inso.framework.cache.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.inso.framework.cache.CacheService;
import com.inso.framework.utils.StringUtils;

public class LocalMemoryImpl implements CacheService{
	
	private Map<String, Object> maps = new HashMap<String,Object>();

	@Override
	public void setString(String key, String value, int seconds) {
		Map<String, Object> valueMaps = new HashMap<String, Object>();
		valueMaps.put("value", value);
		valueMaps.put("seconds", seconds);
		valueMaps.put("start", System.currentTimeMillis());
		maps.put(key, valueMaps);
	}

	@Override
	public String getString(String key) {
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> valueMaps = (Map<String, Object>) maps.get(key);
			if(valueMaps == null || valueMaps.isEmpty()) return null;
			String value = (String) valueMaps.get("value");
			int seconds = StringUtils.asInt(valueMaps.get("seconds"));
			long start = StringUtils.asLong(valueMaps.get("start"));
			if((System.currentTimeMillis() - start) / 1000 <= seconds)
			{
				return value;
			}
		} catch (Exception e) {
		}
		delete(key);
		return null;
	}

	@Override
	public void delete(String key) {
		maps.remove(key);
	}


	@Override
	public void setMultiKeys(Map<String, Object> keyValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T> List<T> getMultiString(Class<T> clazz, String... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exists(String key) {
		// TODO Auto-generated method stub
		return false;
	}

}
