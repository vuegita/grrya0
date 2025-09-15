package com.inso.framework.spring.limit;

/**
 * 继承Exception 会报错，无法初始化
 * @author Administrator
 *
 */
public class MyIPRateLimitException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static MyIPRateLimitException mDefEX = new MyIPRateLimitException();
    

}
