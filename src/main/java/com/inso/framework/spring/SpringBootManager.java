package com.inso.framework.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import com.inso.framework.conf.MyConfiguration;
import com.inso.framework.context.MyAppContext;
import com.inso.framework.context.MyEnvironment;

@EnableAutoConfiguration
public class SpringBootManager {
	
//	private static final int DEFAULT_SERVER_PORT = 55555;
	
	private static int mServerPort = -1;
	
	public static void run(Class<?> clazz, String serverPortKey, String logName, String... args) {
		MyAppContext.init(clazz, logName);
		// 配置服务端口
		MyConfiguration conf = MyConfiguration.getInstance();
		int port = conf.getInt(serverPortKey);
		mServerPort = port;
		run(clazz, port, args);
	}
	
	public static int getServerPort()
	{
		return mServerPort;
	}
	
//	private static void run(Class<?> clazz, String... args)
//	{
//		MyAppContext.init(clazz);
//		run(clazz, DEFAULT_SERVER_PORT, args);
//	}
	
	private static void run(Class<?> clazz, int port, String... args) {
		System.setProperty("server.port", port + "");
		SpringApplication.run(clazz, args);
		System.out.println("==========> env  = " + MyEnvironment.getEnv());
		System.out.println("==========> port = " + port);
	}
	
}

