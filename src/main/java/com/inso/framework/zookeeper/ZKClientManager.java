package com.inso.framework.zookeeper;

import java.io.IOException;
import java.util.List;

import com.inso.framework.conf.MyConfiguration;
import com.inso.framework.zookeeper.impl.CuratorZKClientImpl;
import com.inso.framework.zookeeper.impl.ZKClientSupport;
	
/**
 * zk相关的都用这个类来使用
 * @author Administrator
 *
 */
public class ZKClientManager {
	
	public static final int CONNECTION_TIMEOUT = 5000;
	
	private ZKClientSupport mZKSupport;

	private interface ZKClientManagerInternal {
		public ZKClientManager mgr = new ZKClientManager();
	}
	
	public static ZKClientManager getInstanced()
	{
		return ZKClientManagerInternal.mgr;
	}
	
	public ZKClientManager()
	{
		MyConfiguration conf = MyConfiguration.getInstance();
		this.mZKSupport = new CuratorZKClientImpl(conf);
	}
	
	public void createPersistent(String path, String data)
	{
		mZKSupport.createPersistent(path);
		mZKSupport.setData(path, data);
	}
	public void createEphemeral(String path, String data)
	{
		mZKSupport.createEphemeral(path);
		mZKSupport.setData(path, data);
	}
	public void delete(String path)
	{
		mZKSupport.delete(path);
	}
	public String getData(String path)
	{
		return mZKSupport.getData(path);
	}
	
	public boolean isConnected()
	{
		return mZKSupport.isConnected();
	}
	public void close()
	{
		mZKSupport.close();
	}
	
	public DistributeLock createLock(String path)
	{
		return mZKSupport.createLock(path);
	}
	
	public List<String> getChildren(String path)
	{
		return mZKSupport.getChildren(path);
	}
	
	public void test()
	{
		String path = "/shiqi/test";
		ZKClientManager zkClient = this;
		zkClient.createPersistent(path, "ccc");
//		zkClient.delete(path);
		
		System.out.println("start test getData .........");
		System.out.println(zkClient.getData(path));
		System.out.println("end   test getData .........");
		
		System.out.println("start test getChildren .........");
		System.out.println(zkClient.getChildren(path));
		System.out.println("end   test getChildren .........");
	}
	
	public static void main(String[] args) throws IOException
	{
		ZKClientManager zkClient = ZKClientManager.getInstanced();
//		zkClient.test();
//		
//		for(int i = 0; i < 10 ; i ++) {
//			System.out.println("test index " + (i + 1));
			DistributeLock lock = zkClient.createLock("/test/lock"); // create lock for /test/lock
			System.out.println(lock.lockAcquired(2)); // lock and wait 1s
			if(lock.isLockAcquiredInThisProcess())
			{
				System.out.println("isLockAcquiredInThisProcess");
				lock.lockReleased(); // unlock
			} else {
				System.out.println("unLock for /test/lock");
			}
			
			
//			DistributeLock lock2 = zkClient.createLock("/test/lock");
//			System.out.println("isLock = " + lock.isLockAcquiredInThisProcess() + ", " + lock2.lockAcquired(2));
			System.in.read();
//		}
	}
	
	
}
