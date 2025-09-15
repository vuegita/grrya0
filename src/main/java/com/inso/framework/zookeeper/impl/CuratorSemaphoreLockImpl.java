package com.inso.framework.zookeeper.impl;

import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;

import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.zookeeper.DistributeLock;

/**
 * 不可重入锁
 * @author Administrator
 *
 */
public class CuratorSemaphoreLockImpl implements DistributeLock{
	
	private static final Log LOG = LogFactory.getLog(CuratorSemaphoreLockImpl.class);
	
	private InterProcessSemaphoreMutex mLock;
	
	public CuratorSemaphoreLockImpl(CuratorFramework client, String lockPath)
	{
		this.mLock = new InterProcessSemaphoreMutex(client, lockPath);
	}
	
	public boolean lockAcquired(long waitSeconds) {
		boolean rs = false;
		try {
			rs = mLock.acquire(waitSeconds, TimeUnit.SECONDS);
		} catch (Exception e) {
			LOG.error("lock error:", e);
		}
		return rs;
	}
	
	public boolean lockAcquired()
	{
		return lockAcquired(5);
	}
	
	public boolean isLockAcquiredInThisProcess()
	{
		return mLock.isAcquiredInThisProcess();
	}

	public void lockReleased() {
		try {
			mLock.release();
		} catch (Exception e) {
			LOG.error("unlock error:", e);
		}
	}
	
}
