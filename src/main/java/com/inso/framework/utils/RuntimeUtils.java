package com.inso.framework.utils;

import java.lang.management.ManagementFactory;

public class RuntimeUtils {
	
	public static String getPID()
	{
		String name = ManagementFactory.getRuntimeMXBean().getName();
		return name.split("@")[0];  
	}
	
	public static void logMemory()
	{
		Runtime runtime = Runtime.getRuntime();
		long maxMemory = runtime.maxMemory() / 1024/ 1024; 
		long freeMemory = runtime.freeMemory() / 1024/ 1024;
		long totalMemory = runtime.totalMemory() / 1024/ 1024;
		
		System.out.println("可以获得最大内存：" + maxMemory + " M ");
		System.out.println("所分配的内存大小：" + freeMemory + " M ");
		System.out.println("已经分配的内存大小：" + totalMemory + " M ");
	}

	public static void main(String[] args)
	{
		String name = ManagementFactory.getRuntimeMXBean().getName();    
		System.out.println(name);    
		String pid = name.split("@")[0];    
		System.out.println("Pid is:" + pid);   
	}
	
}
