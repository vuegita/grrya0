package com.inso.modules.paychannel.logical.payment;


import java.math.BigDecimal;
import java.util.Map;

import com.inso.modules.passport.PayResponseForm;
import com.inso.modules.passport.business.model.WithdrawOrder;
import com.inso.modules.paychannel.logical.payment.model.PayoutResult;
import com.inso.modules.paychannel.model.ChannelInfo;

public interface PaymentProcessor {

	public Map<String, Object> encryptPayinRequest(ChannelInfo channel, String txnid, BigDecimal amount, String productinfo, String email, String phone);
	public boolean verifyPayinResponse(ChannelInfo channel, PayResponseForm form);


	public PayoutResult createPayout(ChannelInfo channel, WithdrawOrder orderInfo);
	public boolean verifyPayoutSign(ChannelInfo channel, PayResponseForm form);

	public PayoutResult getPayoutStatus(ChannelInfo channel, WithdrawOrder orderinfo);

}
