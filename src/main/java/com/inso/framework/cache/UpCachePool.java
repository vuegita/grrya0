package com.inso.framework.cache;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UpCachePool {
	
	private ExecutorService mPool;
	
	private interface MyInterl {
		public UpCachePool mgr = new UpCachePool();
	}
	
	private UpCachePool() {
		this.mPool = Executors.newFixedThreadPool(20);
	}
	public static UpCachePool getIntance()
	{
		return MyInterl.mgr;
	}
	
	public void execute(Runnable runnable)
	{
		mPool.execute(runnable);
	}

}
