package com.inso.framework.socketio.listener;

import com.inso.framework.socketio.model.MessageBody;

/**
 * 事件回调
 * @author Administrator
 *
 */
public interface ServerEventListener {
	
	/**
	 * 登陆成功回调
	 * @param userid
	 */
	public String onVerifyUserLogin(String accessToken, String ip, String extra);
	
	public String getFromPlatform(String accessToken);
	
	/**
	 * 登陆成功回调
	 * @param userid
	 */
	public void onUserLoginCallback(String userid);
	
	/**
	 * 用户退出回调
	 * @param userid
	 */
	public void onUserLogoutCallback(String userid);
	
	/**
	 * 客户端向服务端发送消息成功回调
	 * @param body
	 */
	public void onTransBuffer_C2S_CallBack(MessageBody body);
	
	/**
	 * 客户端向服务端发送消息成功回调
	 * @param body
	 */
	public void onTransBuffer_S2C_Failure_CallBack(MessageBody body);
	
}
