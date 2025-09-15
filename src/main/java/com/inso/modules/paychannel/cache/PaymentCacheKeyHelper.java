package com.inso.modules.paychannel.cache;

import com.inso.modules.passport.MyConstants;
import com.inso.modules.paychannel.model.ChannelType;
import com.inso.modules.paychannel.model.PayProductType;

public class PaymentCacheKeyHelper {

	private static final String ROOT_CACHE_KEY = MyConstants.DEFAULT_PAY_CHANNEL_MODULE_NAME;

	public static String getMerchantPayinRequestCacheKey(String key)
	{
		return ROOT_CACHE_KEY + "payment_gateway_merchant_payin_request_" + key;
	}
	
	public static String getMerchantPayinHandleCacheKey(String key)
	{
		return ROOT_CACHE_KEY + "payment_gateway_merchant_payin_handle_" + key;
	}

	public static String getPaymentOrderInfoCacheKey(ChannelType channelType, long merchantid, String outTradeno)
	{
		return ROOT_CACHE_KEY + "payment_getpayinorderinfo_" + channelType.getKey() + merchantid + outTradeno;
	}
	
//	/**
//	 * 回调
//	 * @param key
//	 * @return
//	 */
//	public static String getPayinReturnCacheKey(String key)
//	{
//		return ROOT_CACHE_KEY + "payment_gateway_payin_return" + key;
//	}

	/**
	 * 针对此次支付过程绑定一个唯一key，回调回来可通过这个key获取相关临时数据
	 * key 要唯一，这是最重要的
	 * @return
	 */
	public static String getBindSelfOrderNo_2_ThirdOrderidCacheKey(PayProductType type, String key, boolean isFront)
	{
		return ROOT_CACHE_KEY + "payment_gateway_payin_bind_self_orderno_2_third_orderid_" + type.getKey() + key + isFront;
	}

	public static String getPayoutBindSelfOrderNo_2_ThirdOrderidCacheKey(PayProductType type, String key, boolean isFront)
	{
		return ROOT_CACHE_KEY + "payment_gateway_payin_bind_self_orderno_2_third_orderid_" + type.getKey() + key + isFront;
	}
	
	public static String getPaymentWebhookDataCacheKey(ChannelType type, long merchantid, String outTradeNo)
	{
		return ROOT_CACHE_KEY + "payment_webhook_data_mid_outradeno_" + type.getKey() + merchantid + outTradeNo;
	}
	
//	public static String getPayoutSupportAccessToken(PayProductType type, String key)
//	{
//		return ROOT_CACHE_KEY + "payment_payout_support_getAccessToken_" + type.getKey() + key;
//	}
//
//	public static String getPayoutSupportRefreshToken(PayProductType type, String key)
//	{
//		return ROOT_CACHE_KEY + "payment_payout_support_getRefreshToken_" + type.getKey() + key;
//	}

	/**
	 * 商户实时payment数据, 代收和代付数据，不一定准确, 系统重启可能会丢失数据
	 * @param dayOfYear
	 * @param channelType
	 * @param merchantid
	 * @return
	 */
	public static String getDayMerchantPaymentData(int dayOfYear, ChannelType channelType, long merchantid)
	{
		return ROOT_CACHE_KEY + "payment_getDayMerchantPaymentData_" + dayOfYear + channelType.getKey() + merchantid;
	}


	public static String notifyError(ChannelType type, String username)
	{
		return ROOT_CACHE_KEY + "payment_notify_many_error_" + type.getKey() + username;
	}
}
