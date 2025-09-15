package com.inso.framework.mq;

import com.inso.framework.service.Callback;

public  interface MQSupport{
	
	/**
	 * 对于rocketmq 没有用
	 * @param topic
	 */
	public  void declareTopic(String topic);
	public  void deleteTopic(String topic);
	
	/**
	 * 消息消息
	 * @param topic
	 * @param body
	 * @return
	 */
	public  boolean sendMessage(String topic, String body);
	public  boolean sendMessage(String topic, String body, String tags);
	
	/**
	 * 消费消息, 消息不重复消息
	 * @param tags
	 * @param callback
	 */
	public void consume(String topic, String tags, Callback<String> callback);
	
	/**
	 * 订阅消息，消息重复消费
	 * @param tags
	 * @param callback
	 */
	public void subscribe(String topic, String tags, Callback<String> callback);
}
