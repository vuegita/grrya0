//package com.bc.framework.redis.impl;
//
//import com.taobao.common.tedis.Group;
//import com.taobao.common.tedis.commands.DefaultValueCommands;
//import com.taobao.common.tedis.config.ConfigManager;
//import com.taobao.common.tedis.core.ValueCommands;
//import com.taobao.common.tedis.group.TedisGroup;
//
//public class TedisServiceImpl {
//	
//	public void test()
//	{
//		String appName = "";
//		String version = "";
//		Group tedisGroup = new TedisGroup(appName, version);
//		tedisGroup.init();
//		ConfigManager config;
//		ValueCommands valueCommands = new DefaultValueCommands(tedisGroup.getTedis());
//		// 写入一条数据
//		valueCommands.set(1, "test", "test value object");
//		// 读取一条数据
//		valueCommands.get(1, "test");
//	}
//
//}
