package com.inso.framework.cache.model;

import java.io.IOException;

import com.inso.framework.cache.CacheManager;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.StringUtils;
import com.inso.framework.utils.ThreadUtils;

public class StatusModel {
	
	public static final Log LOG = LogFactory.getLog(StatusModel.class);

	public static final int DEFAULT_KEY_EXPIRE = 3600000; //1 hour
	public static final long DEFAULT_DELAY = 10000;
	public static final int MAX_REQUEST = 10;
	
	public static final int VERSION = 1;
	
	private int mKeyExpires = DEFAULT_KEY_EXPIRE;
	
	private int maxRequest = -1;
	private StatusInfo statusInfo;
	
	/**
	 * 
	 * @param key
	 * @param maxRequest 在这个范围内直接验证成功
	 */
	public StatusModel(String key, int maxRequest)
	{
		this.maxRequest = maxRequest;
		this.statusInfo = new StatusInfo(key, DEFAULT_DELAY);
	}

	public void loadStatus(CacheManager cache)
	{
		String jsonString = cache.getString(statusInfo.getKey());
		statusInfo.asString(jsonString);
	}
	
	public void saveStatus(CacheManager cache)
	{
		String json = statusInfo.valueString();
		cache.setString(statusInfo.getKey(), json, mKeyExpires);
	}
	
	public void incrRequst() {
		statusInfo.incrRequst();
	}

    public boolean checkValid()
    {
    	boolean validStatus = false;
    	if(maxRequest > 0 && statusInfo.getRequestCount() <= maxRequest)
    	{
    		return validStatus;
    	}else
    	{
    		long currentTime = System.currentTimeMillis();
    		if(currentTime - statusInfo.getLastTime() > statusInfo.getDelayTime())
    		{
    			validStatus = true;
    		}
    	}
    	return validStatus;
    }
    
    /**
     * 状态信息
     * @author Administrator
     *
     */
	public class StatusInfo
	{
		private String key;
		private long startTime;
		private long lastTime;
		/*** 处罚时间 ***/
		private long delayTime;
		private int requestCount;
		
		public StatusInfo(String key, long defaultDelayTime) {
			this.key = key;
			this.delayTime = defaultDelayTime;
			this.startTime = System.currentTimeMillis();
		}
		
		public void incrRequst() {
			this.requestCount++;
			this.lastTime = System.currentTimeMillis();
		}

		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
		public long getStartTime() {
			return startTime;
		}
		public void setStartTime(long startTime) {
			this.startTime = startTime;
		}
		public long getLastTime() {
			return lastTime;
		}
		public void setLastTime(long lastTime) {
			this.lastTime = lastTime;
		}
		public long getDelayTime() {
			return delayTime;
		}
		public void setDelayTime(long delayTime) {
			this.delayTime = delayTime;
		}
		public int getRequestCount() {
			return requestCount;
		}
		public void setRequestCount(int requestCount) {
			this.requestCount = requestCount;
		}
		
		public String valueString() {
			StringBuffer buf = new StringBuffer();
			buf.append(VERSION).append(",").append(startTime).append(",")
			   .append(lastTime).append(",").append(delayTime).append(",")
			   .append(requestCount);
			return buf.toString();
		}
		
		public void asString(String str) {
			if (!StringUtils.isEmpty(str)) {
				String[] strs = str.split(",");
				if (strs.length == 5 && strs[0].equalsIgnoreCase("1")) {
					setStartTime(StringUtils.asLong(strs[1]));
					setLastTime(StringUtils.asLong(strs[2]));
					setDelayTime(StringUtils.asLong(strs[3]));
					setRequestCount(StringUtils.asInt(strs[4]));
				}
			} 
		}
	}
	
	public static void main(String[] args) throws IOException
	{
		CacheManager cache = CacheManager.getInstance();
		cache.delete("test");
		for(int i = 0; i < 100; i ++)
		{
			StatusModel model = new StatusModel("test", 10);
			model.loadStatus(cache);
			if(model.checkValid())
			{
				LOG.info("验证成功...");
			} else
			{
				LOG.info("验证失败...");
			}
			model.incrRequst();
			model.saveStatus(cache);
			if(i == 0) ThreadUtils.sleep(1000);
			else if(i == 1) ThreadUtils.sleep(10000);
			else ThreadUtils.sleep(1000);
		}
		System.in.read();
	}
	
}
