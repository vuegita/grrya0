package com.inso.framework.mq;

import java.io.IOException;
import java.util.Map;

import com.google.common.collect.Maps;
import com.inso.framework.mq.impl.RedisMQImpl;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.ThreadUtils;

public class MQManager{
	
	Map<String, MQSupport> maps = Maps.newConcurrentMap();
	
	private interface ManagerInternal {
		public MQManager mgr = new MQManager();
	}
	
	public static MQManager getInstance()
	{
		return ManagerInternal.mgr;
	}
	
	private MQManager()
	{
		maps.put(MQType.REDIS.name(), new RedisMQImpl());
//		maps.put(MQImpl.ROCKETMQ.name(), new RocketMQImpl());
	}
	
	public MQSupport getMQ(MQType type)
	{
		return maps.get(type.name());
	}
	
	public static enum MQType {
		REDIS, // redis
		ROCKETMQ; // rocketmq
	}
	
	public static void main(String[] args) throws InterruptedException, IOException
	{
		String queue = "pangugle_test";
		
		String tags = null;
		
		MQSupport mq = MQManager.getInstance().getMQ(MQType.REDIS);
		
		mq.subscribe(queue, tags, new Callback<String>() {
			public void execute(String o) {
				System.out.println("consuemr 1 " + o);
			}
		});
		
//		mq.consume(queue, tags, new Callback<String>() {
//			public void execute(String o) {
//				System.out.println("consuemr 1 " + o);
//			}
//		});
		
		for(int i = 0; i < 1000; i ++)
		{
			mq.sendMessage(queue, "i = " + i, tags);
			ThreadUtils.sleep(1000);
		}
		
		System.in.read();
	}
	
}
