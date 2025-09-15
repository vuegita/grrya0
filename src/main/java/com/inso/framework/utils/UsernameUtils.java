package com.inso.framework.utils;

/**
 * 用户名工具类生成器
 * @author Administrator
 *
 */
public class UsernameUtils {
	
	
	public static String generator()
	{
		return RandomStringUtils.generator0_Z(16);
	}

	public static String parseUsername(String email)
	{
		int indexStr1 = email.indexOf("@");
		int indexStr2 = email.lastIndexOf(StringUtils.DOT);
		String str1 = "ep" + email.substring(0, indexStr1) + StringUtils.DIVIDER_LINE_BOTTOM + email.substring(indexStr1 + 1, indexStr2);
		return str1;
	}

	public static void main(String[] args) {
		String username = "dfa.sdf.fasdfs@gmail.com";
		System.out.println(parseUsername(username));
	}
}
