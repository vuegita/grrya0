package com.inso.framework.socketio.model;

public class MessageBody {
	
	private static int DEFAULT_RETRY = 3;
	
	private boolean qos;
	private int retry;
	private MyProtocol protocol;
	private String fromUserToken;
	/*** 发送给谁 ***/
	private String toUserid;
	private boolean offline = false;
	
	public String getFromUserToken() {
		return fromUserToken;
	}
	public void setFromUserToken(String fromUserToken) {
		this.fromUserToken = fromUserToken;
	}
	public String getToUserid() {
		return toUserid;
	}
	public void setToUserid(String toUserid) {
		this.toUserid = toUserid;
	}
	
	public boolean isOffline() {
		return offline;
	}
	public void setOffline(boolean offline) {
		this.offline = offline;
	}
	public int getRetry() {
		return retry;
	}
	public void setRetry(int retry) {
		this.retry = retry;
	}
	
	public boolean getQos() {
		return qos;
	}
	public void setQos(boolean qos) {
		this.qos = qos;
	}
	
	public MyProtocol getProtocol() {
		return protocol;
	}
	public void setProtocol(MyProtocol protocol) {
		this.protocol = protocol;
	}
	
	public void increRetry()
	{
		retry ++;
	}
	
	public boolean verifyResend()
	{
		if(qos && retry < DEFAULT_RETRY)
		{
			return true;
		}
		return false;
	}

}
