package com.inso.framework.zookeeper.impl;

import java.util.List;

import com.inso.framework.zookeeper.DistributeLock;

public interface ZKClientSupport
{
	public void createPersistent(String path);
	public void createEphemeral(String path);
	public void delete(String path);
	
	public void setData(String path, String data);
	public String getData(String path);
	
	public boolean isConnected();
	public void close();

	public List<String> getChildren(String path);
	
	public DistributeLock createLock(String path);

}
