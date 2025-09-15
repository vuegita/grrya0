package com.inso.framework.utils;

import java.util.Date;

public class OrdernoUtils {
	
	
	public static String generateKey()
	{
		int num = (int)(Math.random()*900)+100;
		Date date = new Date();
		String timeString = DateUtils.convertString(date, DateUtils.TYPE_YYYYMMDDHHMMSSSSS);
		return timeString + num;
	}
	
	public static void main(String[] args)
	{
		System.out.println(generateKey().length());
	}

}
