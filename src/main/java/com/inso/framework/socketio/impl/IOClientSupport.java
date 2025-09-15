package com.inso.framework.socketio.impl;

import com.corundumstudio.socketio.SocketIOClient;
import com.inso.framework.socketio.model.MessageBody;

public interface IOClientSupport {
	
	public boolean addClient(SocketIOClient client, String userid, String accessToken);
	public boolean removeClient(SocketIOClient client, String userid);

	public boolean existClient(String userid);
	
	public SocketIOClient kickOut(String userid, String accessToken);
	
	public boolean sendMessage(MessageBody body);
}
