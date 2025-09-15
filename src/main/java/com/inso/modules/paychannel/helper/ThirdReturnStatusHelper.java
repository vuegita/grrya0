package com.inso.modules.paychannel.helper;


import com.inso.framework.cache.CacheManager;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.modules.paychannel.cache.PaymentCacheKeyHelper;
import com.inso.modules.paychannel.logical.payment.model.PaymentReturnStatusModel;
import com.inso.modules.paychannel.model.PayProductType;

/**
 * 第三方回调状态
 * 回调成功直接删除状态，防止重新提交
 * @author Administrator
 *
 */
public class ThirdReturnStatusHelper {

	/*** 后端缓存 28小时, 正常会有人设置24小时到账 ***/
	private static final int BG_EXPIRES = 3600 * 28;


	private static boolean isDev = MyEnvironment.isDev();

	/**
	 * 
	 * @param type 支付产品类型
	 * @param key 订单号，可以是我们的订单号，也可以是第三方的订单号，看第三方设计是否有返回我们的订单号，如果有则用我们自己的订单号
	 * @param value 要保存的状态信息，可以是字符串，也可以是JSON
	 */
	public static void save(PayProductType type, String key, PaymentReturnStatusModel value)
	{
		 // 缓存1小时间, 1小时没有回调的话，商户的客户早就关闭了支付了, 回调成功过后，会清除缓存
        String frontCachekey = PaymentCacheKeyHelper.getBindSelfOrderNo_2_ThirdOrderidCacheKey(type, key, true);
        CacheManager.getInstance().setString(frontCachekey, FastJsonHelper.jsonEncode(value), CacheManager.EXPIRES_HOUR_5);
        
        
        // webhook 会使用此数据
        String bgCachekey = PaymentCacheKeyHelper.getBindSelfOrderNo_2_ThirdOrderidCacheKey(type, key, false);
        CacheManager.getInstance().setString(bgCachekey, FastJsonHelper.jsonEncode(value), BG_EXPIRES);
	}

	public static PaymentReturnStatusModel getFrontAndDelete(PayProductType type, String key)
	{
		String cachekey = PaymentCacheKeyHelper.getBindSelfOrderNo_2_ThirdOrderidCacheKey(type, key, true);
		PaymentReturnStatusModel value = CacheManager.getInstance().getObject(cachekey, PaymentReturnStatusModel.class);
		if(!isDev)
		{
			CacheManager.getInstance().delete(cachekey);
		}
		return value;
	}
	
	public static PaymentReturnStatusModel getBackground(PayProductType type, String key)
	{
		return getBackground(type, key, false);
	}

	public static PaymentReturnStatusModel getBackground(PayProductType type, String key, boolean delete)
	{
		String cachekey = PaymentCacheKeyHelper.getBindSelfOrderNo_2_ThirdOrderidCacheKey(type, key, false);
		PaymentReturnStatusModel value = CacheManager.getInstance().getObject(cachekey, PaymentReturnStatusModel.class);
		if(delete && !isDev)
		{
			CacheManager.getInstance().delete(cachekey);
		}
		return value;
	}


	public static void bindPayout(PayProductType type, String key, PaymentReturnStatusModel value)
	{
		// webhook 会使用此数据
		String bgCachekey = PaymentCacheKeyHelper.getPayoutBindSelfOrderNo_2_ThirdOrderidCacheKey(type, key, false);
		CacheManager.getInstance().setString(bgCachekey, FastJsonHelper.jsonEncode(value), BG_EXPIRES);
	}

	public static PaymentReturnStatusModel getPayoutBackground(PayProductType type, String key, boolean delete)
	{
		String cachekey = PaymentCacheKeyHelper.getPayoutBindSelfOrderNo_2_ThirdOrderidCacheKey(type, key, false);
		PaymentReturnStatusModel value = CacheManager.getInstance().getObject(cachekey, PaymentReturnStatusModel.class);
		if(delete && !isDev)
		{
			CacheManager.getInstance().delete(cachekey);
		}
		return value;
	}

}
