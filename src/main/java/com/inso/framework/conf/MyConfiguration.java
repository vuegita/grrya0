package com.inso.framework.conf;

import java.util.Properties;
import java.util.Set;

import com.inso.framework.context.MyEnvironment;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.StringUtils;

public class MyConfiguration {
	
	private static Log LOG = LogFactory.getLog(MyConfiguration.class);

    private static final Properties src = new Properties();

    private interface MyConfigurationInternal {
        public static MyConfiguration conf = new MyConfiguration();
    }

    public static MyConfiguration getInstance() {
        return MyConfigurationInternal.conf;
    }

    private MyConfiguration() {
        synchronized (MyConfiguration.class) {
            if (src.isEmpty()) {
                MyEnvironment.loadConf(src, "site-default.cfg");
                MyEnvironment.loadConf(src, "site-" + MyEnvironment.getEnv() + ".cfg");
                
                MyEnvironment.loadConf(src, "/etc/mywg/custom-site-prod.cfg");
                
                String cfgFile = System.getProperty("site_config_file");
                if(!StringUtils.isEmpty(cfgFile))
                {
                	MyEnvironment.loadConf(src, cfgFile);
                }
            }
        }
    }

    public String getString(String key) {
        return src.getProperty(key);
    }

    public String[] getStrings(String key) {
        return src.getProperty(key).split(",");
    }

    public String getString(String key, String def) {
        return src.getProperty(key, def);
    }

    public boolean getBoolean(String key) {
        String value = src.getProperty(key);
        return StringUtils.asBoolean(value);
    }

    public boolean getBoolean(String key, boolean def) {
        String value = src.getProperty(key);
        return StringUtils.isEmpty(value) ? def : StringUtils.asBoolean(value);
    }

    public int getInt(String key, int def) {
        String value = src.getProperty(key);
        return StringUtils.isEmpty(value) ? def : StringUtils.asInt(value);
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }

    public float getFloat(String key) {
        return getFloat(key, 0);
    }

    public float getFloat(String key, float def) {
        String value = src.getProperty(key);
        return StringUtils.isEmpty(value) ? def : StringUtils.asFloat(value);
    }

    public void test()
    {
    	Set<Object> keyset = src.keySet();
    	for(Object key : keyset)
    	{
    		Object value = src.get(key);
    		System.out.println(key + "=" + value);
    	}
    }
    
    public static void main(String[] args) {
    	
    	MyConfiguration conf = MyConfiguration.getInstance();
    	String port = conf.getString("bootstrap.global.admin.server.port");
    	System.out.println("aaaaaaaaaaaaaaaa" + port);
    	
    	LOG.error("============");
    }


}
