package com.inso.framework.reflect;

import java.util.HashMap;
import java.util.Map;

public class MyFieldFactory {
	
	private static Map<String, JavaBeanField> maps = new HashMap<String, JavaBeanField>();
	
	public static void clear()
	{
		maps.clear();
	}
	
	public static JavaBeanField getField(Class<?> cls)
	{
		String key = cls.getName();
		JavaBeanField beanField = maps.get(key);
		if(beanField == null) {
			synchronized (maps) {
				beanField = maps.get(key);
				if(beanField == null ) {
					beanField = new JavaBeanField(cls);
					maps.put(key, beanField);
				}
			}
		}
		return beanField;
	}

}
