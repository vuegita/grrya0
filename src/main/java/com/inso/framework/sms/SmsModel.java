package com.inso.framework.sms;


import com.inso.framework.cache.CacheManager;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;

public class SmsModel {
	public Log LOG = LogFactory.getLog(SmsModel.class);
	private static final String KEY_CACHE = "sms-bg-black-sendmobilecode2:";
	private static final int KEY_EXPIRE = 86400; //1 days
	
	public static final long DEFAULT_MOBILE_DELAY = 60_000;
	public static final long DEFAULT_IP_DELAY = 10_000;
	
    private SmsStatus ipStatus;
    private SmsStatus mobileStatus;
    	
    public SmsModel(String ip,String mobile)
    {
    	ipStatus = new SmsStatus(ip,SmsStatus.TYPE_IP,DEFAULT_IP_DELAY);
    	mobileStatus = new  SmsStatus(mobile,SmsStatus.TYPE_MOBILE,DEFAULT_MOBILE_DELAY);
    }
    
    public void  loadStatus(CacheManager cache)
    {
//    	String jsonString = (String)memcached.get(MemcachedManager.PASSPORT_NODE, );
//    	ipStatus.asString(jsonString);
//    	jsonString = (String)memcached.get(MemcachedManager.PASSPORT_NODE, );
//    	mobileStatus.asString(jsonString);
    	
    	String jsonString = cache.getString(KEY_CACHE + ipStatus.getKey());
    	ipStatus.asString(jsonString);
    	
    	jsonString = cache.getString(KEY_CACHE + mobileStatus.getKey());
    	mobileStatus.asString(jsonString);
    }
  
    public void  saveStatus(CacheManager cache)
    {
//    	memcached.set(MemcachedManager.PASSPORT_NODE, KEY_CACHE + ipStatus.getKey(), ipStatus.valueString(), KEY_EXPIRE);
//    	memcached.set(MemcachedManager.PASSPORT_NODE, KEY_CACHE + mobileStatus.getKey(), mobileStatus.valueString(), KEY_EXPIRE);
    	
    	cache.setString(KEY_CACHE + ipStatus.getKey(), ipStatus.valueString(), KEY_EXPIRE);
    	cache.setString(KEY_CACHE + mobileStatus.getKey(), mobileStatus.valueString(), KEY_EXPIRE);
    	
    }
    
    public void incrRequst() {
    	ipStatus.incrRequst();
    	mobileStatus.incrRequst();
	}

	public void incrSend() {
		ipStatus.incrSend();
    	mobileStatus.incrSend();
	}
   
    public boolean checkValid(){
    	boolean validStatus = false;
		long currentTime = System.currentTimeMillis();
		if(currentTime - ipStatus.getLastTime() > ipStatus.getDelayTime())
		{
			if(ipStatus.getSendCount() >= 10)
			{
				return false;
			}
			if(mobileStatus.getSendCount() >= 10)
			{
				return false;
			}
			if(currentTime - mobileStatus.getLastTime() > mobileStatus.getDelayTime())
			{
				validStatus = true;
			} else
			{
				LOG.debug("mobile限制发送");
			}
		} else
		{
			LOG.debug("ip限制发送");
		}
		return validStatus;
    }
}
