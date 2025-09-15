package com.inso.framework.redis.client;

import java.util.List;

import com.inso.framework.conf.MyConfiguration;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

public class JedisManager {
	
	private static Log LOG = LogFactory.getLog(JedisManager.class);

	private interface MyJedisClientInternal {
		public JedisManager mgr = new JedisManager();
	}
	
	private JedisPool pool;
	
	public static JedisManager getInstanced()
	{
		return MyJedisClientInternal.mgr;
	}
	
	private JedisManager()
	{
		JedisPoolConfig config = new JedisPoolConfig();
         config.setMaxTotal(1000);
         config.setMaxIdle(50);
         config.setMinIdle(50);
 
         config.setTestOnBorrow(true);
         config.setTestOnReturn(true);
         
       //连接池耗尽的时候，是否阻塞，false会抛出异常，true阻塞直到超时，会抛出超时异常，默认为true
         config.setBlockWhenExhausted(false);
         
         MyConfiguration conf = MyConfiguration.getInstance();
         
         String[] hosts = conf.getStrings("pika.master.server1");
 		int port = conf.getInt("pika.master.port", 9221);
 		
 		// master
 		for(String host : hosts) {
 			LOG.info("jedis pikadb server = " + host);
 	         pool = new JedisPool(config, host, port, 1000*2);
 			break;
 		}
         
	}
	
	public JedisPool getPool()
	{
		return pool;
	}
	
	public Jedis getJedis()
	{
		try {
			return pool.getResource();
		} catch (Exception e) {
		}
		return null;
	}
	
	public void returnResource(Jedis jedis)
	{
		jedis.close();
	}
	
	public void testPubSubConsumer(String flag)
	{
		
		String channel = "test";
		Jedis jedis = getJedis();
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				jedis.subscribe(new JedisPubSub() {
					@Override
					public void onMessage(String channel, String message) {
						System.out.println(flag + " receiv msg = " + message);
						
					}

					@Override
					public void onSubscribe(String channel, int subscribedChannels) {
						System.out.println(String.format("subscribe redis channel success, channel %s, subscribedChannels %d",
				                channel, subscribedChannels));
					}

					@Override
					public void onUnsubscribe(String channel, int subscribedChannels) {
						System.out.println(String.format("unsubscribe redis channel, channel %s, subscribedChannels %d",
				                channel, subscribedChannels));
					}
				}, channel);				
			}
		});
		thread.start();
		
	}
	
	public void testPubSub()
	{
		
		String channel = "test";
		
		testPubSubConsumer("consumer 1 ");
		testPubSubConsumer("consumer 2 ");
		
		Jedis jedis2 = getJedis();
//		System.out.println(jedis2);
		for(int i = 0 ; i < 1000; i ++)
		{
			System.out.println("publish rs = " + jedis2.publish(channel, "i = " + i));
//			ThreadUtil.sleep(1000);
		}
	}
	
	public void testPush()
	{
		String queue = "test-queue";
		Jedis jedis = getJedis();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				boolean rs = true;
				while(rs)
				{
					List<String> dataList = jedis.blpop(2, queue);
					System.out.println(dataList);
				}
							
			}
		}).start();
		
		
		Jedis jedis2 = getJedis();
		for(int i = 0 ; i < 1000; i ++)
		{
			jedis2.lpush(queue, " i = " + i);
//			ThreadUtil.sleep(1000);
		}
	}
	
	public static void main(String[] args)
	{
		JedisManager client = JedisManager.getInstanced();
		
		client.testPubSub();
//		client.testPush();
	}
	
}
