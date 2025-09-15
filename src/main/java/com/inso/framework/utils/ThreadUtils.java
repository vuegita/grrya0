package com.inso.framework.utils;

public class ThreadUtils {

	public static final long DEFAULT_WAIT_TIME = 20 * 1000;
	
	public static void sleep(long millis)
	{
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}
	
	public static void sleep()
	{
		sleep(DEFAULT_WAIT_TIME);
	}
	
}
