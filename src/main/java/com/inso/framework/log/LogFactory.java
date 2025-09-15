package com.inso.framework.log;

import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import com.inso.framework.log.jcl.JclAdapter;
import com.inso.framework.log.jdk.JdkAdapter;
import com.inso.framework.log.slf.Slf4jAdapter;
import com.inso.framework.log.support.FailsafeLogger;
import com.inso.framework.utils.StringUtils;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.util.StatusPrinter;

public final class LogFactory {
	
	private LogFactory() {
	}

	private static volatile LogAdapter mLogAdapter;
	
	private static final ConcurrentMap<String, Log> mLogs = new ConcurrentHashMap<String, Log>();

	// 查找常用的日志框架
	static {
	    try {
	    	init();
			String logger = System.getProperty("pangugle.application.log", "slf4j");
			if ("slf4j".equals(logger)) {
				setLogAdapter(new Slf4jAdapter());
			} else if ("jcl".equals(logger)) {
				setLogAdapter(new JclAdapter());
			} 
//			else if ("log4j".equals(logger)) {
//				setLogAdapter(new Log4jAdapter());
//			} 
			else if ("jdk".equals(logger)) {
				setLogAdapter(new JdkAdapter());
			}
		} catch (Exception e) {
			try {
    			setLogAdapter(new Slf4jAdapter());
            } catch (Throwable e1) {
                try {
                	setLogAdapter(new JclAdapter());
                } catch (Throwable e2) {
//                    try {
//                    	setLogAdapter(new Log4jAdapter());
//                    } catch (Throwable e3) {
                    	setLogAdapter(new JdkAdapter());
//                    }
                }
            }
		}
	}
	
	public static void setLogAdapter(LogAdapter adapter) {
		mLogAdapter = adapter;
	}

	/**
	 * 获取日志输出器
	 * 
	 * @param key
	 *            分类键
	 * @return 日志输出器, 后验条件: 不返回null.
	 */
	public static Log getLog(Class<?> key) {
		Log log = mLogs.get(key.getName());
		if (log == null) {
			log = new FailsafeLogger(mLogAdapter.getLog(key));
			mLogs.putIfAbsent(key.getName(), log);
		}
		return log;
	}
	
	private static String getEnv()
	{
		String env = System.getProperty("env");
		return StringUtils.isEmpty(env) ? "dev" : env;
//		return "prod";
//		return "test";
	}

	public static void init()
	{
		try {
			String env = getEnv();
			String filename = "logback-" + env + ".xml";
			
			ResourceLoader resourceLoader = new DefaultResourceLoader();
			InputStream is = resourceLoader.getResource("classpath:config/" + filename).getInputStream();
//			InputStream is = LogFactory.class.getClassLoader().getResourceAsStream(MyEnvironment.getConfigPath() + "/" + filename);
//			String filepath = LogFactory.class.getClassLoader().getResource().getPath();
			loadLogback(is);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void loadLogback(InputStream inputStream)
	{
		try {
			if(inputStream == null)
			{
				throw new RuntimeException("logback config is not exist for ");
			} else
			{
				LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
				JoranConfigurator configurator = new JoranConfigurator();
				configurator.setContext(context);
				context.reset();
				configurator.doConfigure(inputStream);
				StatusPrinter.printInCaseOfErrorsOrWarnings(context);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	public static void loadLogback(String filepath)
	{
		try {
			File file = new File(filepath);
			if(!file.exists())
			{
				throw new RuntimeException("logback config is not exist for " + filepath);
			} else
			{
				LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
				JoranConfigurator configurator = new JoranConfigurator();
				configurator.setContext(context);
				context.reset();
				configurator.doConfigure(file);
				StatusPrinter.printInCaseOfErrorsOrWarnings(context);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	

}
