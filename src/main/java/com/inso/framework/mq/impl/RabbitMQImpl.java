//package com.inso.framework.mq.impl;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.Executors;
//
//import com.inso.framework.conf.MyConfiguration;
//import com.inso.framework.mq.MQSupport;
//import com.inso.framework.mq.MessageConsumer;
//import com.rabbitmq.client.Channel;
//import com.rabbitmq.client.Connection;
//import com.rabbitmq.client.ConnectionFactory;
//import com.rabbitmq.client.MessageProperties;
//import com.rabbitmq.client.QueueingConsumer;
//
//public class RabbitMQImpl extends MQSupport{
//	
//	private final static int DEFAULT_MAX_EXCUTOR_THREAD = 1000;
//	private final static String DEFAULT_EXCHANGE = "com.inso.mq.exchange.default";
//	public static final String NODE_COMMON = "mq.rabbit";
//	
//	private static Map<String, ConnectionFactory> mConnectionFactorys = new HashMap<String, ConnectionFactory>();
////	private static ConnectionFactory mFactory;
//	private Connection mConn = null;
//	private long delay = 5000;
//	/*** 外部关闭标识 ***/
//	private boolean isClose = false;
//	private Map<String, Channel> mapChannel = new HashMap<String, Channel>();
//	private Map<String, RabbitMessageConsumer> mapConsumer = new HashMap<String,RabbitMessageConsumer>();
//	
//	// 节点名称
//	private String name;
//	private boolean isLogNode;
//	
//	private ConnectionFactory createFactory(String name) {
//		MyConfiguration conf = MyConfiguration.getInstance();
//		String connectionInfo = "vhost=" + conf.getString(name + ".vhost") +
//								"&hostname=" + conf.getString(name + ".host") + 
//								"&port=" + conf.getString(name + ".port");
//		LOG.info(connectionInfo);
//		
//		ConnectionFactory factory = new ConnectionFactory();
//		factory.setVirtualHost(conf.getString(name + ".vhost"));
//		factory.setUsername(conf.getString(name + ".username"));
//		factory.setPassword(conf.getString(name + ".password"));
//		factory.setHost(conf.getString(name + ".host"));
//		factory.setPort(Integer.parseInt(conf.getString(name + ".port")));
//		// 
//		factory.setNetworkRecoveryInterval(1000 * 60); // 60s
//		factory.setConnectionTimeout(5000);
//		factory.setRequestedHeartbeat(60); // 心跳 60s
//		factory.setSharedExecutor(Executors.newFixedThreadPool(DEFAULT_MAX_EXCUTOR_THREAD));
//		// 连接自动恢复
//		factory.setAutomaticRecoveryEnabled(true);
//		return factory;
//	}
//	
//	public RabbitMQImpl(String name){
//		this.name = name;
//		open();
//	}
//	
//	public boolean open()  {
//		boolean rs = false;
//		try {
//			synchronized (mConnectionFactorys)
//			{
//				ConnectionFactory factory = mConnectionFactorys.get(name);
//				if(factory == null)
//				{
//					if(factory == null)factory = createFactory(name);
//					mConnectionFactorys.put(name, factory);
//				} 
//				if(mConn == null)mConn = factory.newConnection();
//				rs = true;
//			}
//		} catch (Exception e) {
//			LOG.error("open connection error:", e);
//		}
//		return rs;
//	}
//	
//	private Connection getConnection()
//	{
//		if(mConn == null)
//		{
//			synchronized (Connection.class) {
//				if(mConn == null)
//				{
//					open();
//				}
//			}
//		}
//		return mConn;
//	}
//	
//	private Channel getPublishChannel(String queue)
//	{
//		Channel channel = null;
//		try {
//			channel = mapChannel.get(queue);
//			if(channel == null)
//			{
//				synchronized (mapChannel) {
//					Connection conn = getConnection();
//					if(conn != null && mConn.isOpen())
//					{
//						channel = mConn.createChannel();
//						mapChannel.put(queue, channel);
//					}
//				}
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return channel;
//	}
//
//	public boolean basicPublish(String messageBody, String queue) {
//		boolean rs = safePublish(messageBody, queue);
//		if(!rs)
//		{
//			rs = safePublish(messageBody, queue);
//		}
//		if(!rs)
//		{
//			rs = safePublish(messageBody, queue);
//		}
//		if(!rs)
//		{
//			LOG.error("msg publish error:queue=" + queue + "&messagebody=" + messageBody);
//		}
//		return rs;
//	}
//	
//	private boolean safePublish(String messageBody, String queue)
//	{
//		boolean rs = false;
//		try {
//			Channel channel = getPublishChannel(queue);
//			channel.basicPublish(DEFAULT_EXCHANGE, queue, MessageProperties.PERSISTENT_TEXT_PLAIN, messageBody.getBytes());
//			rs = true;
//		} catch (Exception e) {
//			if(!isClose) {
//				sleep(delay);
//			} 
//			LOG.error("publish message error:", e);
//		}
//		return rs;
//	}
//
//	public void close(){
//		try {
//			// publish channel
//			Set<String> channelKeys = mapChannel.keySet();
//			for(String key : channelKeys)
//			{
//				Channel channel = mapChannel.get(key);
//				channel.close();
//			}
//			
//			// consumer
//			Set<String> consumerkeys = mapConsumer.keySet();
//			for(String key : consumerkeys)
//			{
//				RabbitMessageConsumer consumer = mapConsumer.get(key);
//				consumer.close();
//			}
//		} catch (Exception e) {
//			LOG.error("close connection error:", e);
//		} finally {
//			mapConsumer.clear();
//			mapChannel.clear();
//		}
//	}
//	
//	public void destroy()
//	{
//		isClose = true;
//		try {
//			close();
//			if(mConn != null) mConn.close();
//		} catch (Exception e) {
//			LOG.error("close connection error:", e);
//		} finally {
//			mConn = null;
//		}
//	}
//
//	/**
//	 * return String or null
//	 */
//	public String basicConsume(String queue) {
//		MessageConsumer result = getConsumer(queue);
//		String rs = result.getBody();
//		result.commit();
//		return rs;
//	}
//	
//	public MessageConsumer getConsumer(String queue) {
//		RabbitMessageConsumer result = mapConsumer.get(queue);
//		if(result == null) {
//			result = new RabbitMessageConsumer(queue);
//			mapConsumer.put(queue, result);
//		}
//		return result;
//	}
//	
//	public void queueDeclare(String queue) {
//		try {
//			Channel channel = getPublishChannel(queue);
//			channel.exchangeDeclare(DEFAULT_EXCHANGE, "topic", true, false, null);
//			channel.queueDeclare(queue, true, false, false, null);
//			channel.queueBind(queue, DEFAULT_EXCHANGE, queue);
//		} catch (Exception e) {
//			LOG.error("decalre queue error:", e);
//		}
//	}
//	
//	public void queueDelete(String queue)
//	{
//		try {
//			Channel channel = getPublishChannel(queue);
//			channel.queueDelete(queue);
//		} catch (IOException e) {
//			LOG.error("delete queue error:", e);
//		}
//	}
//	
//	private static void sleep(long millisecond) {
//		try {
//			Thread.sleep(millisecond);
//		} catch (InterruptedException e) {
//		}
//	}
//	
//	/**
//	 * 
//	 * @author Administrator
//	 *
//	 */
//	private class RabbitMessageConsumer implements MessageConsumer{
//		
//		private static final long serialVersionUID = 1L;
//		
//		private Channel myChannel;
//		private String queue;
//		private QueueingConsumer myConsumer;
//		private boolean isFinished = false;
//		private long mDeliveryTag; // > 1的自然整数
//		private int mRrefetchCount;
//		
//		public RabbitMessageConsumer(String queue) {
//			this.queue = queue;
//			this.mRrefetchCount = isLogNode ? 10 : 1; 
//			openChannel();
//		}
//		
//		private void openChannel() {
//			close();
//			Connection conn = getConnection();
//			if(conn == null || !conn.isOpen()) {
//				sleep(delay);
//				return;
//			}
//			try {
//				if(myChannel == null || !myChannel.isOpen()) {
//					myChannel = mConn.createChannel();
//					myChannel.basicQos(mRrefetchCount); //一次只接受一个
//					myConsumer = new QueueingConsumer(myChannel);
//					myChannel.basicConsume(queue, isLogNode, myConsumer);
//					
//				}
//			} catch (IOException e) {
//				LOG.error("create channel error:", e);
//			}
//		}
//		
//		@Override
//		public synchronized String getBody(){
//			if(isClose) return null;
//			try {
//				isFinished = false;
//				QueueingConsumer.Delivery delivery = myConsumer.nextDelivery();
//				byte[] body = delivery.getBody();
//				// 手动应答, myChannel.basicConsume(queue, /*** 参数false表示手动应答机制 ***/false, myConsumer);
//				mDeliveryTag = delivery.getEnvelope().getDeliveryTag();
//				return new String(body);
//			} catch (Exception e) {
//				if(!isClose) {
//					sleep(delay);
//					openChannel();
//				}
//			} 
//			return null;
//		}
//		
//		public void close() {
//			try {
//				if(!isFinished)
//				{
//					sleep(1000);
//				}
//				if(myChannel != null) myChannel.close();
//			} catch (Exception e) {
//				LOG.error("consumer close error:", e);
//			} finally {
//				myChannel = null;
//				myConsumer = null;
//			}
//		}
//
//		@Override
//		public void commit()
//		{
//			if(isLogNode) return;
//			boolean rs = basicAck();
//			if(!rs) rs = basicAck();
//			if(!rs) rs = basicAck();
//			isFinished = true;
//		}
//		
//		private boolean basicAck() {
//			boolean rs = false;
//			try {
//				if(mDeliveryTag != -1) 
//				{
//					myChannel.basicAck(mDeliveryTag, false);
//					mDeliveryTag = -1;
//				}
//				rs = true;
//			} catch (Exception e) {
//				if(!isClose) {
//					sleep(delay);
//					openChannel();
//				}
//			}
//			return rs;
//		}
//
//		@Override
//		public void destory() {
//			close();
//		}
//		
//	}
//	
//	public static void main(String[] args) throws InterruptedException, IOException
//	{
//		String queue = "com.inso.queue.test";
//		RabbitMQImpl managerConsumer = new RabbitMQImpl(NODE_COMMON);
//		managerConsumer.queueDeclare(queue);
//		
//		Thread thread = new Thread(new Runnable() {
//			public void run() {
//				while(true)
//				{
//					String rs = managerConsumer.basicConsume(queue);
//					System.out.println("consumer => " + rs);
//				}
//			}
//		});
//		thread.start();
//		
//		RabbitMQImpl manager = new RabbitMQImpl(NODE_COMMON);
//		for(int i = 0; i < 20; i ++)
//		{
//			String message = "test";
//			manager.basicPublish("index = " + i + " --- " + message, queue);
//			Thread.sleep(1000);
//		}
//		
// 		System.in.read();
//	}
//	
//}
