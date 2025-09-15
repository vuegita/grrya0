package com.inso.modules.paychannel.logical.helper;

import java.util.Map;
import java.util.TreeMap;

public interface PaymentParamsHelper {

	public static final String KEY_STATUS = "status";

	/*** 返回参数是不是返回URL的，直接重定向 ***/
	public static final String KEY_PAYIN_IS_S2S = "isS2S";
	public static final String KEY_PAYIN_FORWARD_URL = "payUrl";
	public static final String KEY_PAYIN_THIRD_ORDERID = "thirdOrderId";

	/////////////////////////////// payin 公共参数 //////////////////////////////////
	public static final String KEY_PAYIN_CONFIG_EVN = "env";
	/*** 环境对应的value只有test 和 prod 两个环境 ***/
	public static final String VALUE_PAYIN_CONFIG_EVN_TEST = "beta";
	public static final String VALUE_PAYIN_CONFIG_EVN_PROD = "prod";
	
	/*** 商城地址，用于重定向 ***/
	public static final String KEY_PAYIN_CONFIG_SHOP_SERVER = "shopServer";


	public static final String KEY_PAYIN_MERCHANTID = "merchantid";
	public static final String KEY_PAYIN_KEY = "key";
	public static final String KEY_PAYIN_SECRET = "secret";

	public static final String KEY_PAYIN_SALT = "salt";

	/*** 上游id ***/
	public static final String KEY_PAYMENTID = "paymentid";
	/*** 银行订单号 ***/
	public static final String KEY_UTR = "utr";
	
	/*** payout 支付类型 bank | vpa 相当于alipay|wxpay  ***/
//	public static final String KEY_PAYOUT_ACCOUNT_TYPE = "account_type";
//	public static final String VALUE_PAYOUT_ACCOUNT_TYPE_BANK = "bank";
//	public static final String VALUE_PAYOUT_ACCOUNT_TYPE_VPA = "vpa";
//	
//	public static final String KEY_PAYOUT_NAME = "name";
//	public static final String KEY_PAYOUT_BANK_NUMBER = "bank_number";
//	public static final String KEY_PAYOUT_BANK_IFSC = "bank_ifsc";
//	public static final String KEY_PAYOUT_VPA_ADDRESS = "vpa_address";

	public static String TX_STATUS_CAPTURED = "captured";

	/**
	 *
	 * @param treeMap
	 * @return
	 */
	public static String buildParams(Map<String, Object> treeMap)
	{
		if(!(treeMap instanceof TreeMap))
		{
			throw new RuntimeException("Pls use treemap");
		}
		String split = "&";
		String equator = "=";
		StringBuilder allFields = new StringBuilder();
		for (String key : treeMap.keySet()) {
			allFields.append(split);
			allFields.append(key);
			allFields.append(equator);
			allFields.append(treeMap.get(key));
		}
		allFields.deleteCharAt(0); // Remove first FIELD_SEPARATOR
		return allFields.toString();
	}
}

