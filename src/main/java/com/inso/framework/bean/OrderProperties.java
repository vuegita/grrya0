package com.inso.framework.bean;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

import com.inso.framework.context.MyEnvironment;

public class OrderProperties extends Properties{
	
	private static final long serialVersionUID = 1L;
	
	
	private final LinkedHashSet<Object> keys = new LinkedHashSet<Object>();
 
    public Enumeration<Object> keys() {
        return Collections.<Object> enumeration(keys);
    }
 
    public Object put(Object key, Object value) {
    	if(key == null) return null;
        keys.add(key);
        return super.put(key, value);
    }
 
    public Set<Object> keySet() {
        return keys;
    }
 
    public Set<String> stringPropertyNames() {
        Set<String> set = new LinkedHashSet<String>();
 
        for (Object key : this.keys) {
            set.add((String) key);
        }
 
        return set;
    }
    
    public static void main(String[] args)
    {
    	OrderProperties config = new OrderProperties();
    	MyEnvironment.loadConf(config, "menu.cfg");
    	
		Set<String> keys = config.stringPropertyNames();
		String rootKey = null;
		for(String key : keys)
		{
			if(key.startsWith("root_"))
			{
				rootKey = key.split("_")[1];
				String name = config.getProperty(key);
				System.out.println(key + " = " + name);
			}
			else if(key.startsWith(rootKey))
			{
				String name = config.getProperty(key);
				String url = "/admin/" + key;
				String permission = key + "_list";
				System.out.println(key + " = " + name + ", url = " + url + ", permission = " + permission);
			}
			else
			{
				throw new RuntimeException("菜单配置顺序异常,请先配置一级菜单,再配置二级菜单, for " + key);
			}
		}
    }


}
