package com.inso.framework.reflect;

import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;

public class Test {
	
	private static Log LOG = LogFactory.getLog(Test.class);
	
	private String username;
	
	
	public static void test()
	{
		LOG.info("===============");
		
		try {
			// =================
			int i = 3 / 0;
		} catch(Exception e)
		{
			LOG.error("error:", e);
		}
	}

	public static void main(String[] args)
	{
		
		test();
	}

}
