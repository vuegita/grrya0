package com.inso.modules.admin.core.model;

import com.inso.framework.context.MyEnvironment;
import com.inso.framework.google.GoogleUtil;
import com.inso.framework.utils.MD5;
import com.inso.framework.utils.StringUtils;

public class AdminSecret {
	
	/*
	  `admin_account` varchar(20) NOT NULL  COMMENT '账号',
	  `admin_password` varchar(32) NOT NULL COMMENT '密码',
	  `admin_salt` varchar(255) DEFAULT '' COMMENT '备注',*/
	
	private String account;
	private String password;
	private String googlekey;
	private String salt;
	
	public static String getColumnPrefix(){
        return "admin";
    }
	
	public String getGooglekey() {
		return googlekey;
	}

	public void setGooglekey(String googlekey) {
		this.googlekey = googlekey;
	}
	
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSalt() {
		return salt;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}
	
	public boolean checkPassword(String pwd)
	{
		String encryPwd = encryPassword(account, pwd, salt);
		return encryPwd.equalsIgnoreCase(this.password);
	}
	
	public boolean existGoogleKey()
	{
		return !StringUtils.isEmpty(googlekey);
	}
	
	public boolean checkGoogleCode(String code)
	{
		try {
			return GoogleUtil.checkGoogleCode(googlekey, code) || MyEnvironment.isDev();
		} catch (Exception e) {
		}
		return false;
	}

	public static String encryPassword(String account, String pwd, String salt)
	{
		return MD5.encode(account + pwd + salt + "fsdljfasd(&%$");
	}

}
