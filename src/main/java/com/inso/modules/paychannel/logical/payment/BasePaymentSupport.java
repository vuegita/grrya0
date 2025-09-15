package com.inso.modules.paychannel.logical.payment;

import java.math.BigDecimal;
import java.util.Map;

import com.inso.modules.paychannel.model.ChannelInfo;

public abstract class BasePaymentSupport implements PaymentProcessor {

	public Map<String, Object> encryptPayinRequest(ChannelInfo channel, String txnid, String merchantTradeNo, BigDecimal amount, String productinfo, String email, String phone)
	{
		// 默认走原来的方式，如需要此参数，子类可重写
		return encryptPayinRequest(channel, txnid, amount, productinfo, email, phone);
	}




}
