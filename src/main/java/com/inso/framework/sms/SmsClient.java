package com.inso.framework.sms;


import java.io.IOException;

import com.inso.framework.cache.CacheManager;
import com.inso.framework.conf.MyConfiguration;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.sms.imp.*;
import com.inso.framework.utils.StringUtils;

/**
 * 短信发送客户端
 */
public class SmsClient {
	
	private Log LOG = LogFactory.getLog(OcsmsImpl.class);
	
	private static CacheManager cache = CacheManager.getInstance();
	private SmsService mSmsService = new OcsmsImpl();
//	private SmsService mSmsMoreService;

	private interface MyInternal {
		public SmsClient mgr = new SmsClient();
	}

	private SmsClient() {
		MyConfiguration conf = MyConfiguration.getInstance();

		// labsmobile|ocsms
		String channel = conf.getString("sms.active.channel");
		if("ocsms".equalsIgnoreCase(channel))
		{
			this.mSmsService = new OcsmsImpl();
		}
		else if("labsmobile".equalsIgnoreCase(channel))
		{
			mSmsService = new LabsmsImpl();
		}
		else if("cmsms".equalsIgnoreCase(channel))
		{
			mSmsService = new CmsmsImpl();
		}
		else if("Globalsms".equalsIgnoreCase(channel))
		{
			mSmsService = new GlobalsmsImpl();
		}
		else if("InrGlobalsms".equalsIgnoreCase(channel))
		{
			mSmsService = new InrGlobalsmsImpl();
		}
		else if("KmismsImpl".equalsIgnoreCase(channel))
		{
			mSmsService = new KmismsImpl();
		}
		else if("acePlayKmismsImpl".equalsIgnoreCase(channel))
		{
			mSmsService = new acePlayKmismsImpl();
		}
		else if("gogobetKmismsImpl".equalsIgnoreCase(channel))
		{
			mSmsService = new gogobetKmismsImpl();
		}
		else if("BukasmsImpl".equalsIgnoreCase(channel))
		{
			mSmsService = new BukasmsImpl();
		}
		else if("TtzsmsImpl".equalsIgnoreCase(channel))
		{
			mSmsService = new TtzsmsImpl();
		}
		else
		{
			LOG.error("pls enter config sms channel .........");
			return;
		}


	}

	public static SmsClient getInstance()
	{
		return MyInternal.mgr;
	}
	
//	public void sendMore(String mobiles, String content, Callback<Boolean> callback)
//	{
//	}
	
	/**
	 * 目前是用阿里云的
	 * @param mobile
	 * @param code
	 * @param ip
	 * @param callback
	 */
	public boolean send(String mobile, String code, String ip, String senderid,boolean companyNameStatus,String smsContent,Callback<Boolean> callback)
	{
//		String content = mValidCodeTemplate.replace("${validcode}", code);
		String content = code;
		if(StringUtils.isEmpty(mobile) || StringUtils.isEmpty(ip) || StringUtils.isEmpty(content))
		{
			callback.execute(false);
			return false;
		} 
		else
		{
			SmsModel model = new SmsModel(ip,mobile);
			model.loadStatus(cache);
			//detectBeforeSend(model);
			if (model.checkValid()){
//				if(!MyEnvironment.isProd() && mobile.startsWith("00"))
//				{
//					model.incrSend();
//					model.incrRequst();
//					//detectAfterSend(model);
//					model.saveStatus(cache);
//					callback.execute(true);
//					LOG.info("[注意国外手机号在非线上环境不发送][ mobile : " + mobile + ", ip : " + ip + " ], " + "content is " + content);
//					return true;
//				}
				MyConfiguration conf = MyConfiguration.getInstance();

				// labsmobile|ocsms
				String channel = conf.getString("sms.active.channel");
				if("ocsms".equalsIgnoreCase(channel))
				{
					mSmsService.send(mobile, content,senderid, companyNameStatus,smsContent, new Callback<Boolean>() {
						public void execute(Boolean o) {
							if (o){
								model.incrSend();
								model.incrRequst();
								//detectAfterSend(model);
								model.saveStatus(cache);
								LOG.info("[ mobile : " + mobile + ", ip : " + ip + " ], " + "content is " + content + ", send status = " + o.toString());
							}
							callback.execute(o);
						}
					});
				}else{
					mSmsService.send(mobile, content, new Callback<Boolean>() {
						public void execute(Boolean o) {
							if (o){
								model.incrSend();
								model.incrRequst();
								//detectAfterSend(model);
								model.saveStatus(cache);
								LOG.info("[ mobile : " + mobile + ", ip : " + ip + " ], " + "content is " + content + ", send status = " + o.toString());
							}
							callback.execute(o);
						}
					});
				}


				return true;
			}
		}
		return false;
	}
	
	private void test(int index)
	{
		LOG.debug("start send " + index + " ......");
		String content = "666666";
		send("9711903593", content, "192.168.1.11", "",true,content,new Callback<Boolean>() {//919610335370
			public void execute(Boolean o) {
				System.out.println(o);
			}
		});
	}
	
	public static void main(String[] args) throws IOException {
		SmsClient smsClient = new SmsClient();
	
//		smsClient.testStatus();
//		smsClient.testClean(mobile);
		int index = 0;
//		for(int i = 0; i < 10; i ++)
//		{
			smsClient.test(index ++);
//			ThreadUtils.sleep(61000);
//		}
//		System.in.read();

	}

}
