package com.inso.framework.mail;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.utils.StringUtils;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;

import com.inso.framework.conf.MyConfiguration;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;


public class MailBuilder {
	
	private MailConfig config;

	private int maxErrorCount = 5;
	private int currentErrorCount;
	
	public MailBuilder(MailConfig config){
		this.config = config;
	}

	public static MailBuilder getInstance(MyConfiguration conf){
		return getInstance(conf,null);
	}

	public static MailBuilder getInstance(MyConfiguration conf , String name){
		if (name==null || "".equals(name)) name="mail";

		String host = conf.getString(name+".smtp.host");
		if(StringUtils.isEmpty(host))
		{
			return null;
		}

		boolean enable = conf.getBoolean(name+".smtp.enable");
		if(!enable)
		{
			return null;
		}

		MailConfig config = new MailConfig();
		config.setHost(conf.getString(name+".smtp.host"));
		config.setPort(conf.getInt(name+".smtp.port",25));
		config.setAuth(conf.getBoolean(name+".smtp.auth", false));
		config.setUsername(conf.getString(name+".smtp.username"));
		config.setPassword(conf.getString(name+".smtp.password"));
		//from
		config.setFromAddress(conf.getString(name+".from.address"));
		config.setFromName(conf.getString(name+".from.name"));
		config.setFromAddress(conf.getString(name+".bounce.address",config.getFromAddress()));
		config.setDefaultCharset(conf.getString(name+".chareset","utf8"));
		//startTls and ssl
		config.setStartTlsEnabled(conf.getBoolean(name+".starttls.enabled", true));
		config.setStartTlsRequired(conf.getBoolean(name+".starttls.required", false));
		config.setSslOnConnect(conf.getBoolean(name+".ssl.enabled", false));
		config.setSslCheckServerIdentity(conf.getBoolean(name+".ssl.checkserveridentity", false));
		return new MailBuilder(config);
	}

	public static MailBuilder getInstance(String host, int port, String username, String password, boolean auth, String fromAddress, String fromName, boolean enableSSL)
	{
		MailConfig config = new MailConfig();
		config.setHost(host);
		config.setPort(port);
		config.setAuth(auth);
		config.setUsername(username);
		config.setPassword(password);
		//from
		config.setFromAddress(fromAddress);
		config.setFromName(fromName);
		config.setFromAddress(fromAddress);
		config.setDefaultCharset(StringUtils.UTF8);
		//startTls and ssl
		config.setStartTlsEnabled(enableSSL);
		config.setStartTlsRequired(false);
		config.setSslOnConnect(false);
		config.setSslCheckServerIdentity(false);
		return new MailBuilder(config);
	}

	public static MailBuilder getInstance(JSONObject conf, String name)
	{

		boolean enable = conf.getBooleanValue(name+".smtp.enable");
		if(!enable)
		{
			return null;
		}

		MailConfig config = new MailConfig();
		config.setHost(conf.getString(name+".smtp.host"));
		config.setPort(conf.getIntValue(name+".smtp.port"));
		config.setAuth(conf.getBoolean(name+".smtp.auth"));
		config.setUsername(conf.getString(name+".smtp.username"));
		config.setPassword(conf.getString(name+".smtp.password"));
		//from
		config.setFromAddress(conf.getString(name+".from.address"));
		config.setFromName(conf.getString(name+".from.name"));
		config.setDefaultCharset(StringUtils.UTF8);
		//startTls and ssl
		config.setStartTlsEnabled(true);
		config.setStartTlsRequired(conf.getBooleanValue(name+".starttls.required"));
		config.setSslOnConnect(conf.getBooleanValue(name+".ssl.enabled"));
		config.setSslCheckServerIdentity(conf.getBooleanValue(name+".ssl.checkserveridentity"));
		return new MailBuilder(config);
	}

	 public Email createEmail(Class<? extends Email> clazz) throws Exception {
	     Email email = clazz.newInstance();
		 initConf(email);
		 return email;
	 }
	 public void initConf(Email email) throws Exception
	 {
		 email.setStartTLSEnabled(config.isStartTlsEnabled());
		 email.setStartTLSRequired(config.isStartTlsRequired());
		 email.setSSLOnConnect(config.isSslOnConnect());
		 email.setSSLCheckServerIdentity(config.isSslCheckServerIdentity());
		 email.setHostName(config.getHost());
		 email.setSmtpPort(config.getPort());
		 if (config.getBounceAddress()!=null)
			 email.setBounceAddress(config.getBounceAddress());
		 email.setCharset(config.getDefaultCharset());
		 email.setFrom(config.getFromAddress(),config.getFromName(),config.getDefaultCharset());
		 if(config.isAuth()) {
//	            email.setAuthentication(config.getUsername(),config.getPassword());
			 email.setAuthenticator(new Authenticator() {
				 @Override
				 protected PasswordAuthentication getPasswordAuthentication() {
					 return new PasswordAuthentication(config.getUsername(), config.getPassword());
				 }
			 });
		 }
	 }
	 
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MyConfiguration conf = MyConfiguration.getInstance();
		MailBuilder builder = MailBuilder.getInstance(conf,"mail.passport");
		try {
			Email mail = builder.createEmail(SimpleEmail.class);
			mail.addCc("sdfasd@hotmail.com");
			mail.setSubject("this is a test mail");
			mail.setMsg("Simple mail text");
			mail.send();

			System.out.println("end==========");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getFromUsername()
	{
		return config.getUsername();
	}

	public boolean enable()
	{
		return currentErrorCount <= maxErrorCount;
	}

	public int getCurrentErrorCount() {
		return currentErrorCount;
	}

	public void setCurrentErrorCount(int currentErrorCount) {
		this.currentErrorCount = currentErrorCount;
	}
}
