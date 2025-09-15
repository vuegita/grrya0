package com.inso.modules.web.logical;


import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;

/**
 * 整个系统状态, 用户平滑过度整个重启过程
 * @author Administrator
 *
 */
public class SystemStatusManager {

	private static Log LOG = LogFactory.getLog(SystemStatusManager.class);

	private interface MyInternal {
		public SystemStatusManager mgr = new SystemStatusManager();
	}
	
	private boolean mIsRunning = true;
	
	private SystemStatusManager() {
	}
	
	public static SystemStatusManager getInstance()
	{
		return MyInternal.mgr;
	}
	
	public void stop()
	{
		mIsRunning = false;
		LOG.warn("System will stop after 10 seconds ......");
	}
	
	public boolean isRunning()
	{
		return mIsRunning;
	}
	
	
}
