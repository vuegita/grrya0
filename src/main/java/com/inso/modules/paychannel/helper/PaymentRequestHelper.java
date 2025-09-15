package com.inso.modules.paychannel.helper;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.http.HttpMediaType;
import com.inso.framework.http.HttpSesstionManager;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;
import okhttp3.Credentials;

import java.io.IOException;
import java.util.Map;

public class PaymentRequestHelper {
	private static Log LOG = LogFactory.getLog(PaymentRequestHelper.class);

	/***  ***/
	public HttpSesstionManager mHttpMgr;

	public static boolean isDebug = false;

	private interface MyInternal {
		public PaymentRequestHelper mgr = new PaymentRequestHelper(false);
	}

	private interface MyProxyInternal {
		public PaymentRequestHelper mgr = new PaymentRequestHelper(true);
	}

	private PaymentRequestHelper(boolean isProxy)
	{
		if(isProxy)
		{
			int port = 51080;
			String proxyIP = "127.0.0.1";
//			if(MyEnvironment.isDev())
//			{
////				port = 61080;
//				proxyIP = "18.136.249.121";
//			}
			// 127.0.0.1
			//System.out.println("==========> PaymentRequestHelper  port = 51080 ");
			this.mHttpMgr = new HttpSesstionManager(15, 100, 200, proxyIP, port);
		}
		else
		{
			//System.out.println("==========> PaymentRequestHelper  port = null ");
			this.mHttpMgr = new HttpSesstionManager(15, 100, 200);
		}

	}

	public static PaymentRequestHelper getDefaultInstance()
	{
		return MyInternal.mgr;
	}

	public static PaymentRequestHelper getProxyInstance()
	{
		return MyProxyInternal.mgr;
	}

	public static PaymentRequestHelper getInstance()
	{
		if(MyEnvironment.isDev())
		{
			return PaymentRequestHelper.getProxyInstance();
		}

		return PaymentRequestHelper.getDefaultInstance();
	}

	public String syncPost(String url, HttpMediaType type, Map<String, Object> parameter, Map<String, String> header)
	{
		try {
			byte[] rs = mHttpMgr.syncPost(url, type, parameter, header);
			if(rs == null)
			{
				return null;
			}
			return new String(rs);
		} catch (IOException e) {
			LOG.error("request error:", e);
		}
		return null;
	}

	public JSONObject syncPostForJSONResult(String url, Object parameter, Map<String, String> header)
	{
		try {
			byte[] rs = mHttpMgr.syncPostByJSON(url, parameter, header);
			if(rs == null)
			{
				return null;
			}
			String json = new String(rs);
			if(isDebug)
			{
				System.out.println(json);
			}
			if(StringUtils.isEmpty(json))
			{
				return null;
			}
			JSONObject jsonObj = FastJsonHelper.toJSONObject(json);
			return jsonObj;
		} catch (Exception e) {
			LOG.error("request error:", e);
		}
		return null;
	}

	public JSONObject syncPostForJSONResult(String url, HttpMediaType type, Map<String, Object> parameter, Map<String, String> header)
	{
		String rs = syncPost(url, type, parameter, header);
		if(isDebug)
		{
			//System.out.println("rs = " + rs);
			LOG.info("rs: " + rs);
		}

		if(StringUtils.isEmpty(rs))
		{
			return null;
		}

		JSONObject json = FastJsonHelper.toJSONObject(rs);
		return json;
	}

	public JSONObject syncGetForJSONResult(String url, Map<String, String> header)
	{
		try {
			byte[] rs = mHttpMgr.syncGet(url, header);
			if(rs == null)
			{
				return null;
			}

			String json = new String(rs);
			if(StringUtils.isEmpty(json))
			{
				return null;
			}
			if(isDebug)
			{
				System.out.println(json);
			}
			return FastJsonHelper.toJSONObject(json);
		} catch (Exception e) {
			LOG.error("request error:", e);
		}
		return null;
	}

	public JSONArray syncGetForJSONArray(String url, Map<String, String> header)
	{
		try {
			byte[] rs = mHttpMgr.syncGet(url, header);
			if(rs == null)
			{
				return null;
			}

			String json = new String(rs);
			if(StringUtils.isEmpty(json))
			{
				return null;
			}
			if(isDebug)
			{
				System.out.println(json);
			}
			return FastJsonHelper.parseArray(json);
		} catch (Exception e) {
			LOG.error("request error:", e);
		}
		return null;
	}

	public static Map<String, String > buildBasicAuthorization(String key, String secret)
	{
		String credential = Credentials.basic(key, secret);
		Map<String, String> header = Maps.newHashMap();
		header.put("Authorization", credential);
		return header;
	}

	public static Map<String, String > buildBasicAuthorization(String token)
	{
		Map<String, String> header = Maps.newHashMap();
		header.put("Authorization", "Bearer " + token);
		return header;
	}
}
