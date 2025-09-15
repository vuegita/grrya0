package com.inso.framework.spring.interceptor;

/**
 * 继承Exception 会报错，无法初始化
 * @author Administrator
 *
 */
public class MyIPRateLimitException2 extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static MyIPRateLimitException2 mDefEX = new MyIPRateLimitException2();
    

}
