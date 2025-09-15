package com.inso.framework.bean;

import java.util.HashMap;

import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;


public class RecurHashMap extends HashMap<String, Object>{
	private static final long serialVersionUID = 1L;

	public static final Log LOG = LogFactory.getLog(RecurHashMap.class);
	
	public RecurHashMap addKeyValue(String key, Object value)
	{
		this.put(key, value);
		return this;
	}

	public RecurHashMap getChild(String key) {
		RecurHashMap child = (RecurHashMap) get(key);
		if(child == null)
		{
			child = new RecurHashMap();
			put(key, child);
		}
		return child;
	}

	public RecurHashMap removeWithKey(String key) {
		remove(key);
		return this;
	}
	
	
}
