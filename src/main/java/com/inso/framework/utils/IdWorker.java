package com.inso.framework.utils;

import java.util.Date;

public class IdWorker {
	
	private static final int DEFAULT_NUMBER_ID_LEN = 21; // 数字id长度
   
	/**
	 * 数字id生成器
	 * 生成规则：yyyyMMddHHmmss + 随机数(len - 15), 每秒最多生成999999个
	 * @param len
	 * @return
	 */
	private static String generatorNumberId(int len)
	{
		if(len <= 15) {
			throw new RuntimeException("len > 15");
		}
		String timeString = DateUtils.convertString(new Date(System.currentTimeMillis()));
		return timeString + RandomStringUtils.generator0_9(len - 15);
	}
	
	/**
	 * 20位id
	 * @return
	 */
	public static String generatorNumberId()
	{
		return generatorNumberId(DEFAULT_NUMBER_ID_LEN);
	}
	
	
	public static void main(String[] args)
	{
		System.out.println(generatorNumberId());
	}


}
