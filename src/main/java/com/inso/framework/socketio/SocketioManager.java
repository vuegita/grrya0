package com.inso.framework.socketio;

import org.apache.commons.lang3.SystemUtils;

import com.corundumstudio.socketio.AckMode;
import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOServer;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;

public class SocketioManager {
	
	private static Log LOG = LogFactory.getLog(SocketioManager.class);
	
	public static int DEFAULT_PORT = 9999;
	
	private SocketIOServer mServer;
	
	private int port;
	
	public SocketioManager(int port)
	{
		if(port <= 0) port = DEFAULT_PORT;
		this.port = port;
		com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();

		config.setPort(port);
		// ServerSocketChannel在初始化阶段，只会注册到某一个eventLoop上，而这个eventLoop只会有一个线程在运行
        config.setBossThreads(1);
        config.setWorkerThreads(1000);
        config.setAllowCustomRequests(true);
        config.setAckMode(AckMode.AUTO_SUCCESS_ONLY);
        config.setPreferDirectBuffer(true);
        //# 协议升级超时时间（毫秒），默认10秒。HTTP握手升级为ws协议超时时间
        config.setUpgradeTimeout(10000);
        //Ping消息超时时间（毫秒），默认60秒
        config.setPingTimeout(20000);
        // Ping消息间隔（毫秒），默认25秒
        config.setPingInterval(20000);
        if(SystemUtils.IS_OS_LINUX)
        {
        	 config.setUseLinuxNativeEpoll(true);
        }
        
        SocketConfig socketConfig = new SocketConfig();
        // 禁用了Nagle算法，允许小包的发送。对于延时敏感型，同时数据传输量比较小的应用，开启TCP_NODELAY选项无疑是一个正确的选择。
        // 大并发下建议开启
        socketConfig.setTcpNoDelay(true);
//         是否延迟关闭
//        socketConfig.setSoLinger(0);
        socketConfig.setTcpKeepAlive(true);
        // 待处理连接队列, 三次握手后，等待accept
        socketConfig.setAcceptBackLog(1024);
        // 端口复用
        socketConfig.setReuseAddress(true);
        
        config.setSocketConfig(socketConfig);
        
        this.mServer = new SocketIOServer(config);
	}
	
	public void start()
	{
		mServer.start();
		LOG.info("socket.io启动成功,  port = " + port);
	}
	
	public SocketIOServer getServer()
	{
		return mServer;
	}
	

}
