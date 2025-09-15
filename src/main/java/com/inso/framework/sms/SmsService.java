package com.inso.framework.sms;


import com.inso.framework.service.Callback;

public interface SmsService {
	
	public void send(String mobile, String content, Callback<Boolean> callback);

	public void send(String mobile, String content, String senderid,boolean companyNameStatus, String smsContent, Callback<Boolean> callback);
	
	public void sendMore(String mobiles, String content, Callback<Boolean> callback);

}
