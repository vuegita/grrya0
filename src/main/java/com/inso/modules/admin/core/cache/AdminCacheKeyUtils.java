package com.inso.modules.admin.core.cache;

public class AdminCacheKeyUtils {
	
	public static String createAdminSecret(String account)
	{
		return "pg_kefu_admin_account_secret_" + account;
	}
	
	public static String createAdminInfo(String account)
	{
		return "pg_kefu_admin_account_info_" + account;
	}

}
