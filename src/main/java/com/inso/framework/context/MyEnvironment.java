package com.inso.framework.context;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.StringUtils;

/**
 * 上下文环境
 * @author Administrator
 *
 */
public class MyEnvironment {
	
	
	private static Log LOG = LogFactory.getLog(MyEnvironment.class);

	private static final String ENV_KEY = "env";

	private static final String ENV_DEV = "dev";
	private static final String ENV_TEST = "test";
	private static final String ENV_BETA = "beta";
	private static final String ENV_PROD = "prod";

	private static final String DEFAULT_ENVIRONMENT =  ENV_DEV;// ENV_PROD; // "https://"+;

	private static final String CONF_DIR = "config";
	
	private static String CLASSPATH;
	private static String HOME_DIR;
	
	static
	{
		CLASSPATH = MyEnvironment.class.getResource("/").getPath();
		String pidPath = CLASSPATH.substring(0, CLASSPATH.length() - 1);
		int endIndex = pidPath.lastIndexOf("/");
		HOME_DIR = pidPath.substring(0, endIndex);
	}
	
	public static String getClasspath()
	{
		return CLASSPATH;
	}
	
	public static String getHome()
	{
		return HOME_DIR;
	}
	
	/**
	 * 获取当前环境变量(dev|test|prod)
	 * @return
	 */
	public static String getEnv()
	{
		String env = System.getProperty(ENV_KEY);
		return StringUtils.isEmpty(env) ? DEFAULT_ENVIRONMENT : env;
	//	return "prod";
//		return "test";
	}
	
	public static boolean isProd()	
	{
		String env = System.getProperty(ENV_KEY);
		return ENV_PROD.equalsIgnoreCase(env);
	}

	public static boolean isBeta()
	{
		String env = System.getProperty(ENV_KEY);
		return ENV_BETA.equalsIgnoreCase(env);
	}
	
	public static boolean isTest()
	{
		String env = System.getProperty(ENV_KEY);
		return ENV_TEST.equalsIgnoreCase(env) || StringUtils.isEmpty(env);
	}
	
	public static boolean isDev()
	{
		String env = System.getProperty(ENV_KEY);
	    return ENV_DEV.equalsIgnoreCase(env) || StringUtils.isEmpty(env);
		//return false;
	}
	
	public static String getConfigPath()
	{
		return CONF_DIR;
	}
	
	/**
	 * 加载配置到环境变量
	 */
	public static void loadEnvironment() {
		Properties ps = new Properties();
		loadConf(ps, "env.properties");
		Enumeration<?> it = ps.propertyNames();
		while (it.hasMoreElements()) {
			String key = (String) it.nextElement();
			if(!StringUtils.isEmpty(System.getProperty(key))) continue;
			String value = ps.getProperty(key);
			if(value.contains("${env}")) value = value.replace("${env}", getEnv());
			System.setProperty(key, value);
		}
	}
	
	public static void loadConf(Properties ps, String file) {
		try {
			if(!file.startsWith("/"))
			{
				String path = CONF_DIR  + "/" + file;
				ClassPathResource classPathResource = new ClassPathResource(path);
				if(classPathResource != null && classPathResource.exists())
				{
					Properties ret = PropertiesLoaderUtils.loadProperties(new EncodedResource(classPathResource, StringUtils.UTF8));
					ps.putAll(ret);
				}
				
			} else
			{
				File path = new File(file);
				if(!path.exists()) return;
				InputStream is = new FileInputStream(path);
				ps.load(new InputStreamReader(is, "utf-8")); //中文乱码问题
				is.close();
			}
			
			
		} catch (IOException e) {
			//throw new RuntimeException(e);
			LOG.warn("un exist for file :", e);
		}
	}
	
	public static void main(String[] args) throws IOException {
		loadEnvironment();
		Properties ps =  System.getProperties();
		Enumeration<?> it = ps.propertyNames();
		while (it.hasMoreElements()) {
			String key = (String) it.nextElement();
			String value = ps.getProperty(key);
			System.out.println(key + "=" + value);
		}
	}

}
