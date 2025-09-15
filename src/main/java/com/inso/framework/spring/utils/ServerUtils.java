package com.inso.framework.spring.utils;

import com.inso.framework.conf.MyConfiguration;
import com.inso.framework.spring.SpringBootManager;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.NetUtils;
import org.apache.commons.lang3.StringUtils;

public class ServerUtils {
	
	private static MyConfiguration conf = MyConfiguration.getInstance();
	
	public static String getApiServer()
	{
		String apiServer = conf.getString("api.server");
		if(StringUtils.isEmpty(apiServer))
		{
			apiServer = "http://" + NetUtils.getLocalHost() + ":" + SpringBootManager.getServerPort();
		}
		return apiServer;
	}
	
	public static String getStaticServer()
	{
		String server = conf.getString("static.server");
		if(StringUtils.isEmpty(server))
		{
			server = "http://" + NetUtils.getLocalHost() + ":" + SpringBootManager.getServerPort();
		}
		return server;
	}
	
	public static String getUploadServer()
	{
		String server = conf.getString("upload.server");
		if(StringUtils.isEmpty(server))
		{
			server = "http://" + NetUtils.getLocalHost() + ":" + SpringBootManager.getServerPort();
		}
		return server;
	}
	
	public static String getSocketServer()
	{
		String socketServer = conf.getString("socket.server");
		int port = conf.getInt("socketio.port");
		if(StringUtils.isEmpty(socketServer))
		{
			socketServer = "http://" + NetUtils.getLocalHost() + ":" + port;
		}
		return socketServer;
	}
	
	public static String getOfficialServer()
	{
		String server = conf.getString("official.server");
		if(StringUtils.isEmpty(server))
		{
			server = "http://" + NetUtils.getLocalHost() + ":" + SpringBootManager.getServerPort();
		}
		return server;
	}

	public static String getDomain()
	{
		String domain = WebRequest.getHttpServletRequest().getServerName();
		return "https://" + domain;
	}
	
	public static void main(String[] args)
	{
		System.out.println(getApiServer());
		System.out.println(getSocketServer());
	}

}
