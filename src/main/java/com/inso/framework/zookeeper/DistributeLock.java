package com.inso.framework.zookeeper;

public interface DistributeLock {

	 /**
     * call back called when the lock 
     * is acquired
     */
    public boolean lockAcquired(long waitSeconds);
    
    public boolean lockAcquired();
    
    public boolean isLockAcquiredInThisProcess();
    
    /**
     * call back called when the lock is 
     * released.
     */
    public void lockReleased();
	
}
