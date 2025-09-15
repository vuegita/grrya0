package com.inso.framework.redis.impl;

import java.util.List;
import java.util.Map;

import com.inso.framework.redis.RedisService;
import com.inso.framework.redis.client.SharedJedisManager;

/**
 * 
 * @author Administrator
 *
 */
public class SharedServiceImpl implements RedisService {
	
	private SharedJedisManager client = SharedJedisManager.getInstance();

	// ============================== get ==============================
	public String getString(String key) {
		return client.getString(key);
	}

	public List<Object> getList(String... keys)
	{
		return client.getStringByPipeline(keys);
	}

	public void setString(String key, String value, int expire) {
		if(expire <= 0) 
		{
			client.setString(key, value);
		}
		else
		{
			client.setString(key, value, expire);
		}
	}

	public void delete(String key) {
		client.delete(key);
	}
	
	public boolean exists(String key)
	{
		return client.exists(key);
	}
	
	public List<Object> getStringByPipeline(String... keys)
	{
		return client.getStringByPipeline(keys);
	}
	public void setStringByPipeline(Map<String, Object> keyValue)
	{
		client.setStringByPipeline(keyValue);
	}

	public static void main(String[] args)
	{
//		SharedServiceImpl2 service = MyBeanFactory.getInstance(SharedServiceImpl2.class);
//		service.setString("test", "testvalue", 10);
//		System.out.println(service.getString("test"));
	}

}
