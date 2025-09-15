package com.inso.modules.paychannel.logical.payment.tajpay;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.utils.StringUtils;


/**
 * 商户对接我们的支付系统，加密算法
 * @author Administrator
 *
 */
public class TajpayPayinHelper {

	public static final String PAYIN_CHECKOUT_ACTION_TEST = "http://www.tajpay.in/payment/payin";
	public static final String PAYIN_CHECKOUT_ACTION_PROD = "https://www.indiapay.io/payment/payin";
	public static final String PAYIN_CHECKOUT_ACTION_URL = "/payment/payin";
	
//	private static Log LOG = LogFactory.getLog(MerchantPaymentHelper.class);

	private static boolean isDebug = false;
	
	/*** Hash签名 ***/
	public static final String KEY_SIGN = "sign";
	
	public static final String KEY_APPKEYID = "appkeyid";
	public static final String KEY_APPKEYSECRET = "appkeysecret";
	/*** 商户订单号 ***/
	public static final String KEY_TRADENO = "tradeNo";
	/*** 订单金额 ***/
	public static final String KEY_AMOUNT = "amount";
	/*** 商户带过来的产品信息 ***/
	public static final String KEY_PRODUCTINFO = "productinfo";
	/*** 重定向回调URL, 后端return_url 是异步webhook, 必须返回ok ***/
	public static final String KEY_CALLBACK_URL = "callback_url";
	/*** 时间戳 ***/
	public static final String KEY_TIME = "time";
	/*** 回调时，回传回去 ***/
	public static final String KEY_STATUS = "status";
	/*** 单位 INR***/
	public static final String KEY_CURRENCY = "currency";
	/*** currency 对应的Value 一直为 INR***/
	public static final String VALEU_CURRENCY = "INR";
	/*** ***/
	public static final String KEY_EMAIL = "email";
	public static final String KEY_PHONE = "phone";
	
//	private static final String SIGN_REQUEST    = "appkeyid|tradeNo|amount|productinfo|time|appkeysecret|";
//	private static final String SIGN_RESPONSE = "appkeyid|tradeNo|amount|productinfo|time|appkeysecret|status";
	
	private static final String mSplit = "|";

	/**
	 *
	 * @param status  [ new | authorized | captured | realized | error ]
	 * @param isBack
	 * @return
	 */
	public static String signParameter(Map<String, Object> maps, String status, String salt, boolean isBack)
	{
		StringBuilder hashStringBuffer = new StringBuilder();

		hashStringBuffer.append(maps.get(TajpayPayinHelper.KEY_APPKEYID)).append(mSplit); // appkeyid
		hashStringBuffer.append(maps.get(TajpayPayinHelper.KEY_TRADENO)).append(mSplit); // tradeno
		hashStringBuffer.append(maps.get(TajpayPayinHelper.KEY_AMOUNT)).append(mSplit); // amount
		hashStringBuffer.append(maps.get(TajpayPayinHelper.KEY_PRODUCTINFO)).append(mSplit); // productinfo
		hashStringBuffer.append(maps.get(TajpayPayinHelper.KEY_TIME)).append(mSplit); // time
		hashStringBuffer.append(salt).append(mSplit); // appkeysecret

		if(!StringUtils.isEmpty(status))
		{
			hashStringBuffer.append(status);
			if(isBack)
			{
				hashStringBuffer.append(mSplit);
				hashStringBuffer.append("async");
			}
		}

		String hashString = hashStringBuffer.toString();


		String sign = DigestUtils.sha512Hex(hashString);
		maps.put(TajpayPayinHelper.KEY_SIGN, sign);

		// must to be remove appkeySecret
		maps.remove(TajpayPayinHelper.KEY_APPKEYSECRET);
		return sign;
	}

    public static void main(String[] args) {
    	isDebug = true;
		// request hash = sha512(appkeyid|tradeNo|amount|productinfo|time|appkeysecret|)
    	// response hash = sha512(appkeyid|tradeNo|amount|productinfo|time|appkeysecret|status)
    	// webhook hash = sha512(appkeyid|tradeNo|amount|productinfo|time|appkeysecret|status|async)
    	
        String appkeyid = "3bc84969a5f549639619656d72fd5726";
        String appkeySecret = "80ddd2f8ca704c5282453971545708b5";
        
        JSONObject maps = new JSONObject();
        maps.put("appkeyid", appkeyid);
        maps.put("tradeNo", "123456789");
        maps.put("amount", "100.00");
        maps.put("productinfo", "iPhone");
        maps.put("time", "1612429200011");

        String txnid = "123456789";
        BigDecimal amount = new BigDecimal("100");
        
//        String requestSign = signParameter(maps, appkeySecret);
//        System.out.println("request sign = " + requestSign);
//
//        String responseSign = signParameter(maps, appkeySecret, "realized", false);
//        System.out.println("response sign = " + responseSign);
//
//        String webhookSign = signParameter(maps, appkeySecret, "realized", true);
//        System.out.println("webhook sign = " + webhookSign);
    }

}
