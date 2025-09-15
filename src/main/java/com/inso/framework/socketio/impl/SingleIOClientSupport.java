package com.inso.framework.socketio.impl;

import java.util.Map;

import com.corundumstudio.socketio.SocketIOClient;
import com.google.common.collect.Maps;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.socketio.ServerEventManager;
import com.inso.framework.socketio.model.MessageBody;
import com.inso.framework.socketio.model.MyProtocol;
import com.inso.framework.socketio.utils.LoginProtocolUtils;

public class SingleIOClientSupport implements IOClientSupport {
	
	private Map<String, SocketIOClient> mClientMaps = Maps.newConcurrentMap();

	private static Log LOG = LogFactory.getLog(SingleIOClientSupport.class);
	
	@Override
	public boolean addClient(SocketIOClient client, String userid, String accessToken) {
		// 关闭当前连接
		SocketIOClient currentClient = mClientMaps.get(userid);
		if(currentClient != null)
		{
			String requestid = client.getHandshakeData().getSingleUrlParam("requestid");
			MyProtocol multiProtocol = LoginProtocolUtils.buildResponse(LoginProtocolUtils.ERROR_MULTY_CONN, requestid);
			ServerEventManager.getIntance().sendMessage(currentClient, multiProtocol);
			// 关闭
			currentClient.disconnect();
		}
		mClientMaps.put(userid, client);
		return true;
	}

	@Override
	public boolean removeClient(SocketIOClient client, String userid) {
		SocketIOClient cacheClient = mClientMaps.get(userid);
		if(cacheClient == client)
		{
			// 正常关闭连接
			mClientMaps.remove(userid);
			if(client.isChannelOpen()) client.disconnect();
			return true;
		}
		else
		{
			 // 有多个连接请求上来，新来的把前面踢下线
		}
		return false;
	}
	
	public SocketIOClient kickOut(String userid, String accessToken)
	{
		try {
			SocketIOClient client = mClientMaps.get(userid);
			ServerEventManager.getIntance().sendMessage(client, LoginProtocolUtils.MULTY_CONN);
			client.disconnect();
			return client;
		} catch (Exception e) {
		}
		return null;
	}

	@Override
	public boolean sendMessage(MessageBody body) {
		SocketIOClient client = mClientMaps.get(body.getToUserid());
		if(client != null)
		{
			String accessToken = client.getHandshakeData().getSingleUrlParam(ServerEventManager.KEY_ACCESS_TOKEN);
			if(accessToken.equalsIgnoreCase(body.getFromUserToken()))
			{
				LOG.error("don't send message to same client ...");
				return true;
			}
			ServerEventManager.getIntance().sendMessage(client, body.getProtocol());
			return true;
		}
		return false;
	}

	@Override
	public boolean existClient(String userid) {
		return mClientMaps.get(userid) != null;
	}

}
