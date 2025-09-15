package com.inso.framework.socketio.utils;

import com.inso.framework.socketio.ServerEventManager;
import com.inso.framework.socketio.model.MessageBody;
import com.inso.framework.socketio.model.MyProtocol;

/**
 * 
 * @author Administrator
 *
 */
public class LocalSendUtils {
	
	private static ServerEventManager mChatServer = ServerEventManager.getIntance();

	/**
	 * 指定用户发送消息
	 * @param body
	 */
	public static void sendMessageToClient(MyProtocol protocal, boolean qos)
	{
		MessageBody messageBody = new MessageBody();
		messageBody.setProtocol(protocal);
		messageBody.setQos(qos);
		mChatServer.sendMessage(messageBody);
	}
	
	public static void sendMessageToClient(MessageBody body)
	{
		mChatServer.sendMessage(body);
	}
	
//	/**
//	 * 向所有client推送
//	 * @param body
//	 */
//	public static void sendMessageToAll(MyProtocol protocal, boolean qos)
//	{
//		MessageBody messageBody = new MessageBody();
//		messageBody.setProtocol(protocal);
//		messageBody.setQos(qos);
//		mChatServer.sendMessageToAll(messageBody);
//	}
}
