package com.inso.framework.utils;

import com.inso.framework.cache.CacheManager;

public class ValidatorUtils {

	private static final String REGEX_MATCH_USER_NAME_ = "[a-z0-9_A-Z\\-\\.]+";
	
	public static boolean checkUsername(String username)
	{
		if(StringUtils.isEmpty(username)) return false;
		int usernameLen = username.length();

		if(usernameLen >= 5 && usernameLen <= 255)
		{
//			boolean isDigit = RegexUtils.isDigit(username);
//			boolean isCharDigit = username.matches(REGEX_MATCH_USER_NAME_);
//
//			return isCharDigit && !isDigit;

			return username.matches(REGEX_MATCH_USER_NAME_);
		}
		return false;
	}

	public static boolean checkSqlUsername(String username)
	{
		if(StringUtils.isEmpty(username)) return false;
		int usernameLen = username.length();

		if(usernameLen > 0 && usernameLen <= 20)
		{
			boolean isCharDigit = username.matches(REGEX_MATCH_USER_NAME_);
			return isCharDigit;
		}
		return false;
	}
	
	public static boolean checkPassword(String password)
	{
		if(!StringUtils.isEmpty(password) && password.length() == 32) 
		{
			return true;
		}
		return false;
	}
	
	public static boolean checkPassword(String pwd1, String pwd2)
	{
		if(checkPassword(pwd1) && pwd1.equalsIgnoreCase(pwd2)) 
		{
			return true;
		}
		return false;
	}
	
	public static boolean checkNickname(String nickname)
	{
		if(!StringUtils.isEmpty(nickname)) 
		{
			int len = nickname.length();
			return len > 0 && len <= 20;
		}
		return false;
	}
	
	public static boolean checkRolename(String rolename)
	{
		if(StringUtils.isEmpty(rolename)) return false;
		int usernameLen = rolename.length();
		if(usernameLen >= 5 && usernameLen <= 20 && RegexUtils.isLetterOrDigitOrBottomLine(rolename) && !RegexUtils.isDigit(rolename)) return true;
		return false;
	}
	
	public static boolean checkGroupName(String groupname) {
		if(StringUtils.isEmpty(groupname)) return false;
		int usernameLen = groupname.length();
		if(usernameLen > 0 && usernameLen <= 20) return true;
		return false;
	}
	
	/**
	 * 验证ip
	 * @param cachekey
	 * @param maxCount
	 * @param expires
	 * @return
	 */
	public static boolean checkIP(String cachekey, int maxCount, int expires)
	{
		long currentCount = CacheManager.getInstance().getLong(cachekey);
		if(currentCount < maxCount)
		{
			currentCount ++;
			CacheManager.getInstance().setString(cachekey, currentCount + StringUtils.getEmpty(), expires);
			return true;
		}
		return false;
	}
	
	public static void main(String[] args)
	{
		String username = "abc123";
//		System.out.println(RegexUtils.isLetterDigit(username));
//		System.out.println(RegexUtils.isDigit(username));

		System.out.println(checkUsername(username));
		
		//
//		String str = "7c4a8d09ca3762af61e59520943dc26494f8941b";
//		String password = MD5.encode("123456");
//		System.out.println(password + ", len = " + password.length());
//		System.out.println(str + ", len = " + str.length());
//		System.out.println(checkPassword(password));
		
	}
	
}
