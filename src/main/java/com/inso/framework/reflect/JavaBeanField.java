package com.inso.framework.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.inso.framework.utils.StringUtils;

public class JavaBeanField {
	private static final String DEFAULT_METHOD_NAME = "getColumnPrefix";
	public static final char UNDERLINE = '_';
	
	private Map<String, Field> maps = new HashMap<>();
	
	public JavaBeanField(Class<?> cls)
	{
		try {
			String columnPrefix = StringUtils.getEmpty();
			Method[] methos = cls.getMethods(); //当前类的所有方法，不包括父类的属性
			for(Method m : methos)
			{
				if(m.getName().equals(DEFAULT_METHOD_NAME)) {
					columnPrefix = (String) m.invoke(null);
					break;
				}
			}
			Field[] arrf=cls.getDeclaredFields();
			//遍历属性
			String newColumnPrefix = "";
			for(Field f : arrf){
			    //设置忽略访问校验
			    f.setAccessible(true);
			    if(!StringUtils.isEmpty(columnPrefix) && StringUtils.isEmpty(newColumnPrefix))
			    {
					newColumnPrefix = columnPrefix + UNDERLINE;
			    }
			    String dbFieldName = newColumnPrefix + StringUtils.camelToUnderline(f.getName());
			    maps.put(dbFieldName, f);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public Field getField(String key)
	{
		return maps.get(key);
	}



	
	

}
