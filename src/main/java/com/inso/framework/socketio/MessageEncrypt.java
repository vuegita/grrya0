package com.inso.framework.socketio;

public interface MessageEncrypt {
	
	public String encrypt(String body);
	public String decrypt(String body);

}
