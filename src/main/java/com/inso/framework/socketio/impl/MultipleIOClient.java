package com.inso.framework.socketio.impl;

import com.corundumstudio.socketio.SocketIOClient;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.socketio.ServerEventManager;
import com.inso.framework.socketio.model.MessageBody;
import com.inso.framework.socketio.model.MyProtocol;
import com.inso.framework.socketio.utils.LoginProtocolUtils;

public class MultipleIOClient {
	
	private static String FROM_PC = "pc";
	
	private static Log LOG = LogFactory.getLog(MultipleIOClient.class);
	
	private SocketIOClient mPCClient = null;
	private SocketIOClient mAPPClient = null;
//	private List<SocketIOClient> mClientList = Lists.newArrayList();
	
	
	public void addClient(SocketIOClient client)
	{
		String from = client.getHandshakeData().getSingleUrlParam("from");
		if(FROM_PC.equalsIgnoreCase(from))
		{
			if(this.mPCClient != null)
			{
				removeClient(mPCClient, null);
			}
			this.mPCClient = client;
		}
		else
		{
			if(this.mAPPClient != null)
			{
				removeClient(mAPPClient, null);
			}
			this.mAPPClient = client;
		}
		log();
	}
	
	public boolean removeClient(SocketIOClient client, String userid) {
		String requestid = client.getHandshakeData().getSingleUrlParam("requestid");
		MyProtocol multiProtocol = LoginProtocolUtils.buildResponse(LoginProtocolUtils.ERROR_MULTY_CONN, requestid);
		
		if(client == mAPPClient)
		{
			ServerEventManager.getIntance().sendMessage(mAPPClient, multiProtocol);
			client.disconnect();
			this.mAPPClient = null;
		}
		else if(client == mPCClient)
		{
			ServerEventManager.getIntance().sendMessage(mPCClient, multiProtocol);
			client.disconnect();
			this.mPCClient = null;
		}
		log();
		return true;
	}
	
	public SocketIOClient removeClientByAccessToken(String accessToken) {
		if(mAPPClient != null)
		{
			SocketIOClient client = mAPPClient;
			String tmpToken = client.getHandshakeData().getSingleUrlParam(ServerEventManager.KEY_ACCESS_TOKEN);
			if(tmpToken.equalsIgnoreCase(accessToken))
			{
				removeClient(client, null);
				return client;
			}
		}
		
		if(mPCClient != null)
		{
			SocketIOClient client = mAPPClient;
			String tmpToken = client.getHandshakeData().getSingleUrlParam(ServerEventManager.KEY_ACCESS_TOKEN);
			if(tmpToken.equalsIgnoreCase(accessToken))
			{
				removeClient(client, null);
				return client;
			}
		}
		
		return null;
	}
	
	private void log()
	{
		//System.out.println("MultipleIOClient ============== : " + mClientList.size());
	}
	
	public void clear()
	{
		removeClient(mAPPClient, null);
		removeClient(mPCClient, null);
	}
	
	public boolean sendMessage(MessageBody body) 
	{
		boolean rs1 = sendMessage(mAPPClient, body);
		boolean rs2 = sendMessage(mPCClient, body);
		return rs1 || rs2;
	}
	
	private boolean sendMessage(SocketIOClient client, MessageBody body)
	{
		if(client == null)
		{
			return false;
		}
		ServerEventManager mgr = ServerEventManager.getIntance();
		String accessToken = client.getHandshakeData().getSingleUrlParam(ServerEventManager.KEY_ACCESS_TOKEN);
		if(accessToken.equalsIgnoreCase(body.getFromUserToken()))
		{
			LOG.error("don't send message to same client ...");
			return false;
		}
		mgr.sendMessage(client, body.getProtocol());
		return true;
	}
	
	public boolean isEmpty()
	{
		return mPCClient == null && mAPPClient == null;
	}
	

}
