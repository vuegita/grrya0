//package com.inso.framework.mq.impl;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import javax.jms.Connection;
//import javax.jms.ConnectionFactory;
//import javax.jms.DeliveryMode;
//import javax.jms.Destination;
//import javax.jms.JMSException;
//import javax.jms.Message;
//import javax.jms.MessageListener;
//import javax.jms.MessageProducer;
//import javax.jms.Session;
//import javax.jms.TextMessage;
//
//import org.apache.activemq.ActiveMQConnectionFactory;
//
//import com.inso.framework.log.Log;
//import com.inso.framework.log.LogFactory;
//import com.inso.framework.mq.MQSupport;
//import com.inso.framework.mq.MessageConsumer;
//import com.inso.framework.service.Callback;
//import com.inso.framework.utils.ThreadUtils;
//
//public class ActiveMQImpl extends MQSupport{
//	
//	private static Log LOG = LogFactory.getLog(ActiveMQImpl.class);
//	
//	 //connection的工厂
//    private ConnectionFactory factory;
//    //连接对象
//    private Connection connection;
//    
//    private Map<String, MyMessageHandler> maps = new HashMap<String, MyMessageHandler>();
//	
//	public ActiveMQImpl()
//	{
//		String username = "admin";
//		String password = "dflkdslkdfsalfdsafk";
//		String brokerURL = "tcp://192.168.1.171:61616";
//		
//		 try {
//			 
//			 //根据用户名，密码，url创建一个连接工厂
//            factory = new ActiveMQConnectionFactory(username, password, brokerURL);
//            //从工厂中获取一个连接
//            connection = factory.createConnection();
//            //测试过这个步骤不写也是可以的，但是网上的各个文档都写了
//            connection.start();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	@Override
//	public void queueDeclare(String queue) {
//		synchronized (maps) {
//			MyMessageHandler handler = maps.get(queue);
//			if(handler == null)
//			{
//				handler = new MyMessageHandler(queue);
//				maps.put(queue, handler);
//			}
//		}
//	}
//
//	@Override
//	public void queueDelete(String queue) {
//		
//	}
//
//	@Override
//	public boolean basicPublish(String messageBody, String queue) {
//		MyMessageHandler handler = maps.get(queue);
//		handler.sendMessage(messageBody);
//		return false;
//	}
//
//	@Override
//	public String basicConsume(String queue) {
//		return null;
//	}
//
//	@Override
//	public MessageConsumer getConsumer(String queue) {
//		return null;
//	}
//
//	@Override
//	public void close() {
//		
//	}
//
//	@Override
//	public void destroy() {
//		
//	}
//	
//	public void subcribe(String queue, Callback<String> callback)
//	{
//		MyMessageHandler handler = maps.get(queue);
//		handler.subcribe(callback);	
//	}
//	
//	private class MyMessageHandler {
//		
//		 //一个操作会话
//	    private Session session;
//	    //目的地，其实就是连接到哪个队列，如果是点对点，那么它的实现是Queue，如果是订阅模式，那它的实现是Topic
//	    private Destination destination;
//	    //生产者，就是产生数据的对象
//	    private MessageProducer producer;
//	    //消费者，就是接收数据的对象
//	    private  javax.jms.MessageConsumer consumer;
//	    
//	    public MyMessageHandler(String queue)
//	    {
//	    	try {
//				 //创建一个session
//	            //第一个参数:是否支持事务，如果为true，则会忽略第二个参数，被jms服务器设置为SESSION_TRANSACTED
//	            //第二个参数为false时，paramB的值可为Session.AUTO_ACKNOWLEDGE，Session.CLIENT_ACKNOWLEDGE，DUPS_OK_ACKNOWLEDGE其中一个。
//	            //Session.AUTO_ACKNOWLEDGE为自动确认，客户端发送和接收消息不需要做额外的工作。哪怕是接收端发生异常，也会被当作正常发送成功。
//	            //Session.CLIENT_ACKNOWLEDGE为客户端确认。客户端接收到消息后，必须调用javax.jms.Message的acknowledge方法。jms服务器才会当作发送成功，并删除消息。
//	            //DUPS_OK_ACKNOWLEDGE允许副本的确认模式。一旦接收方应用程序的方法调用从处理消息处返回，会话对象就会确认消息的接收；而且允许重复确认。
//	            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//	            //创建一个到达的目的地，其实想一下就知道了，activemq不可能同时只能跑一个队列吧，这里就是连接了一个名为"text-msg"的队列，这个会话将会到这个队列，当然，如果这个队列不存在，将会被创建
//	            destination = session.createQueue(queue);
//	            //从session中，获取一个消息生产者
//	            producer = session.createProducer(destination);
//	            //根据session，创建一个接收者对象
//	            consumer = session.createConsumer(destination);
//	            //设置生产者的模式，有两种可选
//	            //DeliveryMode.PERSISTENT 当activemq关闭的时候，队列数据将会被保存
//	            //DeliveryMode.NON_PERSISTENT 当activemq关闭的时候，队列里面的数据将会被清空
//	            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
//	            
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//	    }
//		
//	    public boolean sendMessage(String msg)
//	    {
//	    	try {
//				TextMessage textMsg = session.createTextMessage(msg);
//				producer.send(textMsg);
//				return true;
//			} catch (JMSException e) {
//				e.printStackTrace();
//			}
//	    	return false;
//	    }
//	    
//	    public void subcribe(Callback<String> callback)
//	    {
//	    	try {
//				consumer.setMessageListener(new MessageListener() {
//				    @Override
//				    public void onMessage(Message message) {
//				        try {
//				            //获取到接收的数据
//				            String text = ((TextMessage)message).getText();
//				            callback.execute(text);
//				        } catch (Exception e) {
//				        	LOG.error("handle message error:", e);
//				        }
//				    }
//				});
//			} catch (JMSException e) {
//				LOG.error("subcribe msg error:", e);
//			}
//	    }
//	    
//	}
//	
//	public static void main(String[] args)
//	{
//		String queue = "queue_test";
//		ActiveMQImpl impl = new ActiveMQImpl();
//		impl.queueDeclare(queue);
//		impl.subcribe(queue, new Callback<String>() {
//			
//			@Override
//			public void execute(String o) {
//				System.out.println("consumer ===> " + o);
//			}
//		});
//		
//		for(int i = 0; i < 100; i ++)
//		{
//			impl.basicPublish(" index = " + i, queue);
//			ThreadUtils.sleep(1000);
//		}
//		
//	}
//	
//
//}
