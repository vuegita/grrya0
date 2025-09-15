//package com.inso.framework.redis.impl;
//
//import java.util.concurrent.TimeUnit;
//
//import org.redisson.api.RBucket;
//import org.redisson.api.RedissonClient;
//
//import com.inso.framework.redis.RedisService;
//import com.inso.framework.redis.client.MyRedissonClient;
//
///**
// * 目前是后端缓存是双主模式, pika不支持()
// * @author Administrator
// *
// */
//public class RedissionServiceImpl implements RedisService {
//
//	private MyRedissonClient client = MyRedissonClient.getInstance();
//
//	private RedissonClient getClient() {
//		return client.getClient();
//	}
//	
//	public String getString(String key) {
//		RBucket<String> bucket = getClient().getBucket(key);
//		return bucket.get();
//	}
//
//	public void setString(String key, String value) {
//		RBucket<String> bucket = getClient().getBucket(key);
//		bucket.set(value);
//	}
//
//	public void setString(String key, String value, int seconds) {
//		RBucket<String> bucket = getClient().getBucket(key);
//		bucket.set(value, seconds, TimeUnit.SECONDS);
//	}
//
//	public void delete(String key) {
//		RBucket<String> bucket = getClient().getBucket(key);
//		bucket.deleteAsync();
//	} 
//	
//	public static void main(String[] args)
//	{
////		RedissionServiceImpl service = MyBeanFactory.getInstance(RedissionServiceImpl.class);
////		service.setString("test", "test", 100);
////		
////		System.out.println(service.getString("test"));
//	}
//
//}
