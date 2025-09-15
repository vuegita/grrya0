package com.inso.modules.paychannel.logical.payment;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.PayResponseForm;
import com.inso.modules.passport.business.model.UserWithdrawVO;
import com.inso.modules.passport.business.model.WithdrawOrder;
import com.inso.modules.paychannel.helper.ThirdReturnStatusHelper;
import com.inso.modules.paychannel.logical.payment.bank.BankSupportImpl;
import com.inso.modules.paychannel.logical.payment.coin.CoinSupportImpl;
import com.inso.modules.paychannel.logical.payment.coin.Fiat2StableCoinSupportImpl;
import com.inso.modules.paychannel.logical.payment.model.PaymentReturnStatusModel;
import com.inso.modules.paychannel.logical.payment.model.PayoutResult;
import com.inso.modules.paychannel.logical.payment.tajpay.TajpaySupportImpl;
import com.inso.modules.paychannel.logical.payment.wallet.WalletSupportImpl;
import com.inso.modules.paychannel.model.ChannelInfo;
import com.inso.modules.paychannel.model.PayProductType;

import java.math.BigDecimal;
import java.util.Map;

public class PaymentProcessorManager {

	/*** 测试卡 ***/
	public static String TEST_CARD = "TEST123456789666";
	
	private interface MyInternal {
		public PaymentProcessorManager mgr = new PaymentProcessorManager();
	}
	
	private Map<String, PaymentProcessor> maps = Maps.newHashMap();

	private PaymentProcessorManager() {
		addProcessor(PayProductType.TAJPAY, new TajpaySupportImpl());
		addProcessor(PayProductType.BANK, new BankSupportImpl());
		addProcessor(PayProductType.Wallet, new WalletSupportImpl());

		addProcessor(PayProductType.COIN, new CoinSupportImpl());
		addProcessor(PayProductType.FIAT_2_STABLE_COIN, new Fiat2StableCoinSupportImpl());
	}
	
	private void addProcessor(PayProductType productType, PaymentProcessor processor)
	{
		maps.put(productType.getKey(), processor);
	}

	public static PaymentProcessorManager getIntance() {
		return MyInternal.mgr;
	}

	public Map<String, Object> encryptPayinRequest(ChannelInfo channel, String txnid, BigDecimal amount, String productinfo, String email, String phone)
	{
		PaymentProcessor processor = maps.get(channel.getProductType());
		Map<String, Object> result = processor.encryptPayinRequest(channel, txnid, amount, productinfo, email, phone);

		if(result != null && !result.isEmpty())
		{
			PayProductType productType = PayProductType.getType(channel.getProductType());

			PaymentReturnStatusModel statusModel = new PaymentReturnStatusModel();
			statusModel.setCid(channel.getId());
			statusModel.setTxnid(txnid);
			statusModel.setAmount(amount);
			statusModel.setProductType(productType);
			statusModel.setProductinfo(productinfo);

			// 缓存1小时间, 1小时没有回调的话，商户的客户早就关闭了支付了, 回调成功过后，会清除缓存
			ThirdReturnStatusHelper.save(productType, txnid, statusModel);
		}

		return result;
	}
//
//	public Map<String, String> encryptPayinRequest(PayChannel channel, String txnid, String merchantOrderId, BigDecimal amount, String productinfo, String email, String phone)
//	{
//		.PaymentProcessor processor = maps.get(channel.getProductType());
//		return processor.encryptPayinRequest(channel, txnid, merchantOrderId, amount, productinfo, email, phone);
//	}
//
//	public boolean submitTransactionInfo(PayinTransactionInfo transactionInfo)
//	{
//		.PaymentProcessor processor = maps.get(transactionInfo.getPayProductType().getKey());
//		return processor.submitTransactionToThird(transactionInfo);
//	}
//
	public boolean verifyPayinResponse(ChannelInfo channel, PayResponseForm form)
	{
		PaymentProcessor processor = maps.get(channel.getProductType());
		return processor.verifyPayinResponse(channel, form);
	}

	public PayoutResult getPayoutStatus(ChannelInfo channel, WithdrawOrder orderinfo)
	{
		PaymentProcessor processor = maps.get(channel.getProductType());
		return processor.getPayoutStatus(channel, orderinfo);
	}

	public PayoutResult createPayout(ChannelInfo channel, WithdrawOrder orderInfo)
	{
		JSONObject remark = orderInfo.getRemarkVO();
		String bankAccount = remark.getString(UserWithdrawVO.KEY_ACCOUNT);
		if(TEST_CARD.equalsIgnoreCase(bankAccount))
		{
			return PayoutResult.WAITING_RESULT;
		}

		PaymentProcessor processor = maps.get(channel.getProductType());
		PayoutResult payoutResult = processor.createPayout(channel, orderInfo);

		if(payoutResult != null && payoutResult.getmTxStatus() == OrderTxStatus.WAITING)
		{
			PayProductType productType = PayProductType.getType(channel.getProductType());

			PaymentReturnStatusModel statusModel = new PaymentReturnStatusModel();
			statusModel.setCid(channel.getId());
			statusModel.setTxnid(orderInfo.getNo());
//			statusModel.setAmount(amount);
			statusModel.setProductType(productType);
//			statusModel.setProductinfo(productinfo);

			// 缓存1小时间, 1小时没有回调的话，商户的客户早就关闭了支付了, 回调成功过后，会清除缓存
			ThirdReturnStatusHelper.bindPayout(productType, orderInfo.getNo(), statusModel);
		}
		return payoutResult;
	}
//
//	public PayoutResult getPayout(PayoutOrder orderInfo)
//	{
//		JSONObject remark = orderInfo.getRemarkObj();
//		long channelid = remark.getIntValue(OrderRemarkVo.channel_id);
//		PayChannel channel = OnlinePayChannel.getIntance().getChannel(channelid);
//		.PaymentProcessor processor = maps.get(channel.getProductType());
//		return processor.getPayout(channel, orderInfo);
//	}
	
}
