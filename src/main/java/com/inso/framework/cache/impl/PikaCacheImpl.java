package com.inso.framework.cache.impl;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.inso.framework.cache.CacheService;
import com.inso.framework.redis.PikaManager;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;

public class PikaCacheImpl implements CacheService{
	
	private PikaManager pika;
	
	public PikaCacheImpl() {
		this.pika = PikaManager.getIntance();
	}
	
	@Override
	public void setString(String key, String value, int seconds) {
		pika.setString(key, value, seconds);
	}

	@Override
	public String getString(String key) {
		return pika.getString(key);
	}

	@Override
	public void delete(String key) {
		pika.delete(key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> getMultiString(Class<T> clazz, String... keys) {
		List<Object> list = pika.getStringByPipeline(keys);
		if(CollectionUtils.isEmpty(list))
		{
			return null;
		}
		List<T> result = Lists.newArrayList();
		if(clazz == String.class)
		{
			for(Object value : list)
			{
				String valueString = StringUtils.asString(value);
				if(StringUtils.isEmpty(valueString)) continue;
				result.add((T)valueString);
			}
		}
		else
		{
			for(Object value : list)
			{
				String valueString = StringUtils.asString(value);
				if(StringUtils.isEmpty(valueString)) continue;
				T model = FastJsonHelper.jsonDecode(valueString, clazz);
				if(model == null) continue;
				result.add(model);
			}
		}
		
		return result;
	}

	@Override
	public void setMultiKeys(Map<String, Object> keyValue) {
		pika.setStringByPipeline(keyValue);
	}

	@Override
	public boolean exists(String key) {
		return pika.exists(key);
	}
	

}
