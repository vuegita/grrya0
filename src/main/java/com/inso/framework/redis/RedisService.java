package com.inso.framework.redis;

import java.util.List;
import java.util.Map;

public interface RedisService {
	
	/** 
     * <p>设置key value并制定这个键值的有效期</p> 
     * @param key 
     * @param value 
     * @param seconds 单位:秒 
     */  
    public void setString(String key,String value, int expire);
    
	public List<Object> getStringByPipeline(String... keys);
	public void setStringByPipeline(Map<String, Object> keyValue);
	
    public String getString(String key);
    public void delete(String key);	
    public boolean exists(String key);
    
}
