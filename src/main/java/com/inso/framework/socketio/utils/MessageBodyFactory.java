package com.inso.framework.socketio.utils;

import com.inso.framework.socketio.model.MessageBody;
import com.inso.framework.socketio.model.MyProtocol;

public class MessageBodyFactory {
	
	public static MessageBody createBody(MyProtocol protocol, boolean qos)
	{
		MessageBody messageBody = new MessageBody();
		messageBody.setProtocol(protocol);
		messageBody.setQos(qos);
		
		return messageBody;
	}
	
	public static MessageBody createBody(MyProtocol protocol)
	{
		MessageBody messageBody = new MessageBody();
		messageBody.setProtocol(protocol);
		messageBody.setQos(false);
		return messageBody;
	}

}
