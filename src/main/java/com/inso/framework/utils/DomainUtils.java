package com.inso.framework.utils;

import com.inso.framework.conf.MyConfiguration;

public class DomainUtils {
	
	public static String getServer()
	{
		MyConfiguration conf = MyConfiguration.getInstance();
		String port = System.getProperty("server.port");
		
		String localHost = NetUtils.getLocalHost();
		
		String staticServer = conf.getString("static.server");
		if(StringUtils.isEmpty(staticServer))
		{
			// => //127.0.0.1:port/static
			staticServer = "http://" + localHost + ":" + port;
		} 
		return staticServer;
	}
	
}
