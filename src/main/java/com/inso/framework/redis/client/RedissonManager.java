//package com.inso.framework.redis.client;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import org.redisson.Redisson;
//import org.redisson.api.RBucket;
//import org.redisson.api.RedissonClient;
//import org.redisson.config.Config;
//
//import com.inso.framework.conf.MyConfiguration;
//import com.inso.framework.log.Log;
//import com.inso.framework.log.LogFactory;
//
//public class MyRedissonClient {
//
//	private static Log LOG = LogFactory.getLog(MyRedissonClient.class);
//	
//	/*** Threads amount shared across all listeners of <code>RTopic</code> object ***/
//	private static int DEFAULT_SERVER_THREAD = 100;
//	/*** Threads amount shared between all redis clients used by Redisson ***/
//	private static int DEFAULT_CLIENT_THREAD = 500; 
//	
//	private static final int DEFAULT_MAXTOTAL =1000;
//	private static final int DEFAULT_MAXIDEL = 100;
//
//	private static int DEFAULT_CONN_TIMEOUT = 5000;
//	private static int DEFAULT_PING_TIMEOUT = 2000;
//	
//	private List<RedissonClient> mUseList = new ArrayList<RedissonClient>();
//	private int mCurrentIndex = 0;
//	private long delay = 5000; // 5s后开始
//	private long period = 2000; // 每2s检测
//	
//	private static final MyRedissonClient client = new MyRedissonClient();
//	
//	public static MyRedissonClient getInstance()
//	{
//		return client;
//	}
//
//	private MyRedissonClient() {
//		initSingle();
//	}
//	
//	private void initSingle()
//	{
//		MyConfiguration conf = MyConfiguration.getInstance();
//		String server1 = conf.getString("pika.master.server1");
//		
//		Config config = new Config();
//		config.setNettyThreads(DEFAULT_CLIENT_THREAD);
//		config.setThreads(DEFAULT_SERVER_THREAD);
//		
//		config.useSingleServer()
//		.setPingTimeout(DEFAULT_PING_TIMEOUT)
//		.setConnectTimeout(DEFAULT_CONN_TIMEOUT)
//		.setReconnectionTimeout(DEFAULT_CONN_TIMEOUT)
//		.setConnectionPoolSize(DEFAULT_MAXTOTAL)
//		.setConnectionMinimumIdleSize(DEFAULT_MAXIDEL)
//		.setRetryAttempts(2)
//		.setAddress("http://" +server1 + ":" + 9221);
//		
//		try {
//			RedissonClient redissonclient = newClient(config);
//			if(redissonclient == null)
//			{
//				redissonclient = newClient(config);
//			}
//			if(redissonclient == null)
//			{
//				redissonclient = newClient(config);
//			}
//			mUseList.add(redissonclient);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	private RedissonClient newClient(Config cfg)
//	{
//		try {
//			return Redisson.create(cfg);
//		} catch (Exception e) {
//			LOG.error("create client error:", e);
//		}
//		return null;
//	}
//	
//	private void initMasterSlave()
//	{
//		MyConfiguration conf = MyConfiguration.getInstance();
//		String server1 = conf.getString("pika.master.server1");
//		String server2 = conf.getString("pika.master.server2");
//		
//		// 注:主从模式=>当从挂了就会从主读取
//		// server1
//		Config server1Config = new Config();
//		server1Config.setNettyThreads(DEFAULT_CLIENT_THREAD);
//		server1Config.setThreads(DEFAULT_SERVER_THREAD);
//		
//		server1Config.useMasterSlaveServers()
//		.setPingTimeout(DEFAULT_PING_TIMEOUT)
//		.setConnectTimeout(DEFAULT_CONN_TIMEOUT)
//		.setReconnectionTimeout(DEFAULT_CONN_TIMEOUT)
//		.setMasterAddress(server1)
//		.addSlaveAddress(server2);
//		mUseList.add(Redisson.create(server1Config));
//		
//		// server2 
//		Config server2Config = new Config();
//		server2Config.setNettyThreads(DEFAULT_CLIENT_THREAD);
//		server2Config.setThreads(DEFAULT_SERVER_THREAD);
//		
//		server2Config.useMasterSlaveServers()
//		.setPingTimeout(DEFAULT_PING_TIMEOUT)
//		.setConnectTimeout(DEFAULT_CONN_TIMEOUT)
//		.setReconnectionTimeout(DEFAULT_CONN_TIMEOUT)
//		.setMasterAddress(server2)
//		.addSlaveAddress(server1);
//		mUseList.add(Redisson.create(server2Config));
//		
//		startTask();
//	}
//	
//	private void startTask()
//	{
//		String key = "com.inso.checkredis";
//		String value = "test";
//		TimerTask task = new TimerTask() {
//			public void run() {
//				try {
//					RBucket<String> bucket = getClient().getBucket(key);
//					bucket.set(value);
//				} catch (Exception e) {
//					changeNode();
//				}
//			}
//		};
//		Timer timer = new Timer();
//		timer.schedule(task, delay, period);
//	}
//
//	private void changeNode() {
//		int index = mCurrentIndex + 1;
//		if (index > mUseList.size() - 1)
//			index = 0;
//		this.mCurrentIndex = index;
//		LOG.info("change node and current index = " + mCurrentIndex);
//	}
//	
//	public RedissonClient getClient() {
//		return mUseList.get(mCurrentIndex);
//	}
//	
//}
