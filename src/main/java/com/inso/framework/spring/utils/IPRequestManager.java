package com.inso.framework.spring.utils;

import com.inso.framework.cache.LRUCache;

public class IPRequestManager {
	
	/*** 最大失败次数 ***/
	private static final int DEFAUL_MAX_FAIR_COUNT = 10;
	
	private LRUCache<String, Integer> maps = new LRUCache<>(1000);
	
	private interface MyInternal {
		public IPRequestManager mgr = new IPRequestManager();
	}
	
	private IPRequestManager()
	{
	}
	
	public static IPRequestManager getIntance()
	{
		return MyInternal.mgr;
	}
	
	public void addFair(String ip)
	{
		
	}
	
	public boolean validIP(String ip)
	{
		Integer count = maps.get(ip);
		return false;
	}

}
