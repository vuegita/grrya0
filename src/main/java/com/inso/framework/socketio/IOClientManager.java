package com.inso.framework.socketio;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.corundumstudio.socketio.SocketIOClient;
import com.google.common.collect.Maps;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.socketio.impl.IOClientSupport;
import com.inso.framework.socketio.impl.MultipleIOClientSupport;
import com.inso.framework.socketio.impl.SingleIOClientSupport;
import com.inso.framework.socketio.model.MessageBody;
import com.inso.framework.utils.StringUtils;

public class IOClientManager{
	
	private static Log LOG = LogFactory.getLog(IOClientManager.class);
	
	/*** 最大连接客户端大小 ***/
	private static final int DEFAULT_MAX_CONNECT = 20000;
	
	private AtomicInteger mSize = new AtomicInteger();
	private Map<String, String> mClient2UserMaps = Maps.newConcurrentMap();
	private IOClientSupport mSupport;
	
	private int maxClientCapacity = DEFAULT_MAX_CONNECT;
	
	public void build(boolean isSingleClient, int maxClientSize)
	{
		if(maxClientSize <= 0)
		{
			maxClientSize = DEFAULT_MAX_CONNECT;
		}
		if(isSingleClient)
		{
			this.mSupport = new SingleIOClientSupport();
		}
		else
		{
			this.mSupport = new MultipleIOClientSupport(maxClientSize);
		}
	}

//	public void sendMessage(SocketIOClient client, String bodyString)
//	{
//		client.sendEvent(DEFAULT_EVENT, bodyString);
//	}
	
	public boolean sendMessage(MessageBody body)
	{
		return mSupport.sendMessage(body);
	}
	
	public void setMaxClientCapacity(int capacity)
	{
		this.maxClientCapacity = capacity;
	}
	
	public boolean isAddClient()
	{
		return  mSize.get() < maxClientCapacity;
	}
	
	public String getUseridByClient(SocketIOClient client)
	{
		String clientid = client.getSessionId().toString();
		return mClient2UserMaps.get(clientid);
	}
	
	public boolean addClient(SocketIOClient client, String userid, String accessToken)
	{
		if(mSupport.addClient(client, userid, accessToken))
		{
			LOG.debug("connec client and userid = " + userid);
			mClient2UserMaps.put(client.getSessionId().toString(), userid);
			mSize.incrementAndGet();
			ServerEventManager.getIntance().getServerEventListener().onUserLoginCallback(userid);
			return true;
		}
		return false;
	}
	
	public boolean removeClient(SocketIOClient client)
	{
//		mSupport.removeClient(client, userid);
		
		String sessionid = client.getSessionId().toString();
		String userid = mClient2UserMaps.get(sessionid);
		LOG.debug("disconnect client and userid = " + userid + ", sessionid = " +sessionid);
		
		// 没有通过认证的，或连接已达上线
		if(StringUtils.isEmpty(userid)) return true;
        
		if(mSupport.removeClient(client, userid))
		{
			mClient2UserMaps.remove(client.getSessionId().toString());
			mSize.decrementAndGet();
			if(!mSupport.existClient(userid))
			{
				ServerEventManager.getIntance().getServerEventListener().onUserLogoutCallback(userid);
			}
			return true;
		}
		return false;
	}

	public void kickOut(String userid, String accessToken) {
		SocketIOClient kickClient = mSupport.kickOut(userid, accessToken);
		if(kickClient != null)
		{
			mClient2UserMaps.remove(kickClient.getSessionId().toString());
			mSize.decrementAndGet();
			if(!mSupport.existClient(userid))
			{
				ServerEventManager.getIntance().getServerEventListener().onUserLogoutCallback(userid);
			}
		}
	}
	
	
}
