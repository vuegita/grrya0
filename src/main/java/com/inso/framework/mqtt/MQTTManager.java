package com.inso.framework.mqtt;

import java.io.IOException;

import com.inso.framework.conf.MyConfiguration;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.ThreadUtils;

public class MQTTManager {
	
//	private MyMqttClient mAdminClient;
//	private MyMqttClient mWriteClient;
//	private MyMqttClient mReadClient;
	
	private MyConfiguration conf = MyConfiguration.getInstance();
	
	private interface MQTTManagerInternal {
		public MQTTManager mgr = new MQTTManager();
	}
	
	public static MQTTManager getInstanced()
	{
		return MQTTManagerInternal.mgr;
	}
	
	private MQTTManager() {
		String server = conf.getString("activemq.server");
		server = "tcp://192.168.1.171:1883";
//		this.mAdminClient = openClient(server, "admin");
//		this.mWriteClient = openClient(server, "writeuser");
//		this.mReadClient = openClient(server, "readuser");
	}
	
	private MyMqttClient openClient(String server, String username)
	{
		String password = conf.getString("activemq." + username + ".password");
		return new MyMqttClient(server, "readuser", "readuser");
	}
	
	public void send(String tp, String message)
	{
//		mAdminClient.send(tp, message);
	}
	
	public void subscribe(String tp, Callback<String> callback)
	{
//		mAdminClient.subscribe(tp, null);
//		mAdminClient.unsubscribe(tp);
//		mAdminClient.subscribe(tp, callback);
	}
	
	public static void main(String[] args) throws IOException
	{
		String queue = "mytp_queue_test";
		
		MQTTManager mqtt = new MQTTManager();
		
		mqtt.subscribe(queue, new Callback<String>() {
			public void execute(String o) {
				System.out.println("consume11111 = " + o);
			}
		});
		
		for(int i = 0; i <= 1000; i ++)
		{
			try {
				mqtt.send(queue, "i = " + i);
				ThreadUtils.sleep(1000);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
