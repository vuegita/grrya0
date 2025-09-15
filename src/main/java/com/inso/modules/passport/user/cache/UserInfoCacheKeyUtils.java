package com.inso.modules.passport.user.cache;

import com.inso.modules.passport.user.model.UserInfo;

public class UserInfoCacheKeyUtils {
	
	private static final String ROOT_CACHEK_KEY = UserInfoCacheKeyUtils.class.getName();

	public static String createUserInfoListKey(UserInfo.UserType userType)
	{
		return ROOT_CACHEK_KEY + "_findByUsername_" + userType.getKey();
	}

	public static String createUserInfoKey(String username)
	{
		return ROOT_CACHEK_KEY + "_findByUsername_v2_" + username;
	}
	
	public static String createAccessTokenToUsernameKey(String accessToken)
	{
		return ROOT_CACHEK_KEY + "_accessToken_" + accessToken;
	}
	
	public static String createAccessTokenToSignKey(String username)
	{
		return ROOT_CACHEK_KEY + "_accessToken_2_sign_" + username;
	}
	
	public static String createLoginTokenToUsernameKey(String loginToken)
	{
		return ROOT_CACHEK_KEY + "_loginToken_2_username_" + loginToken;
	}
	
	public static String createLoginTokenToSignKey(String username)
	{
		return ROOT_CACHEK_KEY + "_loginToken_2_sign_" + username;
	}

	public static String getUsernameByInviteCode(String inviteCode)
	{
		return ROOT_CACHEK_KEY + "getUsernameByInviteCode" + inviteCode;
	}

	public static String getUsernameByPhone(String phone)
	{
		return ROOT_CACHEK_KEY + "getUsernameByPhone" + phone;
	}

	public static String getFingerCountForIP(String registerIp)
	{
		return ROOT_CACHEK_KEY + "getFingerCountForIP" + registerIp;
	}

	public static String getFingerCountForDeviceToken(String deviceToken)
	{
		return ROOT_CACHEK_KEY + "getFingerCountForDeviceToken" + deviceToken;
	}

	public static String getUsernameByEmail(String email)
	{
		return ROOT_CACHEK_KEY + "getUsernameByEmail" + email;
	}

	public static String getUserAttrByUserid(long userid)
	{
		return ROOT_CACHEK_KEY + "getUserAttrByUserid" + userid;
	}

	public static String getInputLoginPwdTimes(String username)
	{
		return ROOT_CACHEK_KEY + "getInputLoginPwdTimes" + username;
	}

	public static String disableUserLogin(String username)
	{
		return ROOT_CACHEK_KEY + "disableUserLogin" + username;
	}

	public static String getPayLoginPwdTimes(String username)
	{
		return ROOT_CACHEK_KEY + "getPayLoginPwdTimes" + username;
	}

	public static String findUserSecretCacheKey(String username)
	{
		return ROOT_CACHEK_KEY + "findUserSecretCacheKey" + username;
	}

	public static String getTotalRechargeByParentidCacheKey(long userid)
	{
		return ROOT_CACHEK_KEY + "getTotalRechargeByParentidCacheKey" + userid;
	}


}