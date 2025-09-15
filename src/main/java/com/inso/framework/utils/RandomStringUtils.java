package com.inso.framework.utils;

import org.apache.commons.text.RandomStringGenerator;

public class RandomStringUtils {
	
	private static char [][] pairs = {{'a','z'},{'0','9'}};
	
	private static RandomStringGenerator mA_To_Z_Generator = new RandomStringGenerator.Builder().withinRange('a', 'z').build();
	private static RandomStringGenerator mNumber_Generator = new RandomStringGenerator.Builder().withinRange('0', '9').build();
	private static RandomStringGenerator m0_To_Z_Generator = new RandomStringGenerator.Builder().withinRange(pairs).build();
	
	public static String generatorA_Z(int len)
	{
		return mA_To_Z_Generator.generate(len);
	}
	
	public static String generator0_9(int len)
	{
		return mNumber_Generator.generate(len);
	}
	
	public static String generator0_Z(int len)
	{
		return m0_To_Z_Generator.generate(len);
	}
	
	public static void main(String[] args)
	{
		String email = RandomStringUtils.generator0_Z(8) + "@gmail.com";
		System.out.println(email);
	}
	
}
