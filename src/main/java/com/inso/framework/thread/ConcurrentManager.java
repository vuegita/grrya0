package com.inso.framework.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.inso.framework.conf.MyConfiguration;

public class ConcurrentManager {
	
	private ExecutorService mPool;
	
	private interface MyInternal {
		public ConcurrentManager mgr = new ConcurrentManager();
	}
	
	private ConcurrentManager()
	{
		MyConfiguration conf = MyConfiguration.getInstance();
		int poolSize = conf.getInt("server.fixedpool.threads");
		this.mPool = Executors.newFixedThreadPool(poolSize);
	}
	
	public static ConcurrentManager getIntance()
	{
		return MyInternal.mgr;
	}
	
	public void execute(Runnable task)
	{
		mPool.execute(task);
	}

}
