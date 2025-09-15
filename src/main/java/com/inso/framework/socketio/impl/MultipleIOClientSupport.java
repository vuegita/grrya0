package com.inso.framework.socketio.impl;

import java.util.Map;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.corundumstudio.socketio.SocketIOClient;
import com.google.common.collect.Maps;
import com.inso.framework.socketio.model.MessageBody;

public class MultipleIOClientSupport implements IOClientSupport{
	
	private GenericObjectPool<MultipleIOClient> mPool;
	private Map<String, MultipleIOClient> mClientMaps = Maps.newConcurrentMap();
	
	public MultipleIOClientSupport(int maxClientSize) {
		
		MultipleIOClientFactory factory = new MultipleIOClientFactory();
		
		//设置对象池的相关参数
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxIdle(maxClientSize);
        poolConfig.setMaxTotal(maxClientSize);
        poolConfig.setMinIdle(maxClientSize);
        poolConfig.setBlockWhenExhausted(true);
        //新建一个对象池,传入对象工厂和配置
        GenericObjectPool<MultipleIOClient> objectPool = new GenericObjectPool<>(factory, poolConfig);
        this.mPool = objectPool;
	}
	
	@Override
	public boolean addClient(SocketIOClient client, String userid, String accessToken) {
		MultipleIOClient multipleIOClient = mClientMaps.get(userid);
		if(multipleIOClient == null)
		{
			multipleIOClient = getBean();
		}
		if(multipleIOClient == null)
		{
			return false;
		}
		multipleIOClient.addClient(client);
		mClientMaps.put(userid, multipleIOClient);
		return true;
	}

	@Override
	public boolean removeClient(SocketIOClient client, String userid) {
		// TODO Auto-generated method stub
		MultipleIOClient multipleClient = mClientMaps.get(userid);
		if(multipleClient != null)
		{
			multipleClient.removeClient(client, userid);
			if(multipleClient.isEmpty())
			{
				mClientMaps.remove(userid);
				returnBean(multipleClient);
			}
		}
		return true;
	}
	
	private MultipleIOClient getBean()
	{
		try {
			return mPool.borrowObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void returnBean(MultipleIOClient bean)
	{
		if(bean == null) return;
		mPool.returnObject(bean);
	}
	
	public SocketIOClient kickOut(String userid, String accessToken)
	{
		MultipleIOClient client = mClientMaps.get(userid);
		return client.removeClientByAccessToken(accessToken);
	}

	@Override
	public boolean sendMessage(MessageBody body) {
		MultipleIOClient client = mClientMaps.get(body.getToUserid());
		if(client != null)
		{
			client.sendMessage(body);
			return true;
		}
		
		return false;
	}

	@Override
	public boolean existClient(String userid) {
		return mClientMaps.get(userid) != null;
	}




}
