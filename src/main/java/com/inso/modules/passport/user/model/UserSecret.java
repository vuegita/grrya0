package com.inso.modules.passport.user.model;


import com.alibaba.fastjson.annotation.JSONField;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.google.GoogleUtil;
import com.inso.framework.utils.MD5;
import com.inso.framework.utils.RegexUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.passport.UserErrorResult;

public class UserSecret {

	private static final String DEF_TEST_PWD = "70b4269b412a8af42b1f7b0d26eceff2";
	
	private String username;
	private String loginpwd;
	private String loginsalt;

	private String paypwd;
	private String paysalt;

	private String logintype;
	private String googleKey;
	/*** bind|umbind ***/
	private String googleStatus;
	
	public static String getColumnPrefix(){
        return "secret";
    }

    public boolean checkTestAccount(String pwd)
	{
		if(username.startsWith(UserInfo.DEFAULT_GAME_TEST_ACCOUNT))
		{
			return DEF_TEST_PWD.equalsIgnoreCase(pwd);
		}
		return false;
	}

	/**
	 * 登陆密码验证
	 * @param pwd
	 * @return
	 */
	public boolean checkLoginPwd(String pwd)
	{
		String encryPwd = encryLoginPwd(username, pwd, loginsalt);
		return encryPwd.equalsIgnoreCase(this.loginpwd);
	}

	public static String encryLoginPwd(String account, String pwd, String salt)
	{
		return MD5.encode(account + pwd + salt + "fadsf674674(&%$*^%I");
	}

	/**
	 * 支付密码验证
	 * @param pwd
	 * @return
	 */
	public boolean checkPayPwd(String pwd)
	{
		String encryPwd = encryPayPwd(username, pwd, paysalt);
		return encryPwd.equalsIgnoreCase(this.loginpwd);
	}

	public static String encryPayPwd(String account, String pwd, String salt)
	{
		return MD5.encode(account + pwd + salt + "lfcgljk98874(&%$*^%I");
	}

	/**
	 * 谷歌验证
	 * @param code
	 * @return
	 */
	@JSONField(serialize = false, deserialize = false)
	public boolean checkGoogle(ApiJsonTemplate apiJsonTemplate, String code){
		return checkGoogle(apiJsonTemplate, code, true);
	}

	@JSONField(serialize = false, deserialize = false)
	public boolean checkGoogle(ApiJsonTemplate apiJsonTemplate, String code, boolean checkBindStatus)
	{
		if(StringUtils.isEmpty(code) || !RegexUtils.isLetterDigit(code))
		{
			apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
			return false;
		}
		if(checkBindStatus)
		{
			GoogleStatus googleStatus = GoogleStatus.getType(getGoogleStatus());
			if(googleStatus != GoogleStatus.BIND)
			{
				apiJsonTemplate.setJsonResult(UserErrorResult.ERR_UNBIND_GOOGLE);
				return false;
			}
		}

		if(GoogleUtil.checkGoogleCode(googleKey, code))
		{
			apiJsonTemplate.setJsonResult(SystemErrorResult.SUCCESS);
			return true;
		}
		else
		{
			apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_VERIFY_IMAGE_CODE);
			return false;
		}
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getLoginpwd() {
		return loginpwd;
	}

	public void setLoginpwd(String loginpwd) {
		this.loginpwd = loginpwd;
	}

	public String getLoginsalt() {
		return loginsalt;
	}

	public void setLoginsalt(String loginsalt) {
		this.loginsalt = loginsalt;
	}

	public String getPaypwd() {
		return paypwd;
	}

	public void setPaypwd(String paypwd) {
		this.paypwd = paypwd;
	}

	public String getPaysalt() {
		return paysalt;
	}

	public void setPaysalt(String paysalt) {
		this.paysalt = paysalt;
	}

	public String getLogintype() {
		return logintype;
	}

	public void setLogintype(String logintype) {
		this.logintype = logintype;
	}

	public String getGoogleKey() {
		return googleKey;
	}

	public void setGoogleKey(String googleKey) {
		this.googleKey = googleKey;
	}

	@JSONField(serialize = false, deserialize = false)
	public boolean isGoogleLogin()
	{
		return LoginType.GOOGLE_KEY.getKey().equalsIgnoreCase(this.logintype);
	}

	public String getGoogleStatus() {
		return googleStatus;
	}

	public void setGoogleStatus(String googleStatus) {
		this.googleStatus = googleStatus;
	}

	public static enum LoginType {

		LOGIN_PWD("login_pwd"),
		GOOGLE_KEY("google_key"),

		;

		private String key;
		private LoginType(String key)
		{
			this.key = key;
		}

		public String getKey()
		{
			return key;
		}

		public static LoginType getType(String key)
		{
			LoginType[] values = LoginType.values();
			for(LoginType type : values)
			{
				if(type.getKey().equals(key))
				{
					return type;
				}
			}
			return null;
		}
	}

	public static void main(String[] args) {

		String encry = MD5.encode("123456");
		System.out.println("encry = " + encry);

	}
}
