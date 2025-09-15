package com.inso.framework.mq;

import java.io.IOException;

public class Test {
	
	
	public static void main(String[] args) throws InterruptedException, IOException
	{
		String queue = "im_send_message";
		
		String tags = null;
		
//		MQSupport mq = MQManager.getInstance().getMQ();
		
//		mq.subscribe(queue, tags, new Callback<String>() {
//			public void execute(String o) {
//				System.out.println("consuemr 2 " + o);
//			}
//		});
		
//		mq.consume(queue, tags, new Callback<String>() {
//			public void execute(String o) {
//				System.out.println("consuemr 2 " + o);
//			}
//		});
		
//		for(int i = 0; i < 10; i ++)
//		{
//			mq.sendTopic(queue, "i = " + i, tags);
//			ThreadUtils.sleep(1000);
//		}
		
		System.in.read();
	}

}
