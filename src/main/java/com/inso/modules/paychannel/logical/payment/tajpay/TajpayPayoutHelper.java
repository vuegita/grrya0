package com.inso.modules.paychannel.logical.payment.tajpay;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.inso.framework.http.HttpMediaType;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.PayResponseForm;
import com.inso.modules.passport.business.model.UserWithdrawVO;
import com.inso.modules.passport.business.model.WithdrawOrder;
import com.inso.modules.paychannel.helper.PaymentRequestHelper;
import com.inso.modules.paychannel.logical.payment.model.PayoutResult;
import com.inso.modules.web.SystemRunningMode;
import org.apache.commons.codec.digest.DigestUtils;

import java.math.BigDecimal;
import java.util.Map;


/**
 * payout 签名
 * @author Administrator
 *
 */
public class TajpayPayoutHelper {

	public static final String REPEAT_ORDER_PREFIX_FLAG = "A";

	private static Log LOG = LogFactory.getLog(TajpayPayoutHelper.class);

	private static final String SPLIT = "|";

	private static final String KEY_APPKEYID = "appkeyid";
	private static final String KEY_TRADE_NO = "tradeNo";
	private static final String KEY_AMOUNT = "amount";
	private static final String KEY_CURRENCY = "currency";
	private static final String KEY_TIME = "time";
	private static final String KEY_NAME = "name";
	private static final String KEY_EMAIL = "email";
	private static final String KEY_PHONE = "phone";
	private static final String KEY_BANK_NUMBER = "bank_number";
	private static final String KEY_BANK_IFSC = "bank_ifsc";
	/*** 身份ID ***/
	private static final String KEY_ID_CARD = "idcard";
	private static final String KEY_SIGN = "sign";

	private static final String SUBMIT_SUCCESS = "success";
	private static final String URL_DEV = "http://127.0.0.1:8103/payment/payout";
	private static final String URL_TEST = "http://www.tajpay.in/payment/payout";
	private static final String URL_PROD = "https://www.indiapay.io/payment/payout";
	private static final String URL_PAYOUT = "/payment/payout";

//	private String mCallbackUrl;
//	private String mAppkeyId;
//	private String mAppkeySecret;
//
//	private boolean isProd = MyEnvironment.isProd();
//
//	private interface MyInternal {
//		public TajpayPayoutHelper mgr = new TajpayPayoutHelper();
//	}
//
//	public static TajpayPayoutHelper getInstance()
//	{
//		return MyInternal.mgr;
//	}

//	private TajpayPayoutHelper()
//	{
//		MyConfiguration conf = MyConfiguration.getInstance();
//		this.mAppkeyId = conf.getString("payment.tajpay.appkeyid");
//		this.mAppkeySecret = conf.getString("payment.tajpay.appkeysecret");
//		this.mCallbackUrl = conf.getString("payment.tajpay.callbackurl");
//	}

	public static PayoutResult doPayout(boolean isProd, PaymentTargetType targetType, String key, String salt, WithdrawOrder order)
	{

		BigDecimal settleAmount = order.getAmount().subtract(order.getFeemoney());


		String amountString = settleAmount.toString();

		JSONObject remark = order.getRemarkVO();

		String type = remark.getString(UserWithdrawVO.KEY_TYPE);
		String account = remark.getString(UserWithdrawVO.KEY_ACCOUNT);
		String ifsc = remark.getString(UserWithdrawVO.KEY_IFSC);
		String currencyType = remark.getString(UserWithdrawVO.KEY_CURRENCY_TYPE);
		String beneficiaryName = remark.getString(UserWithdrawVO.KEY_BENEFICIARYNAME);
		String beneficiaryEmail = remark.getString(UserWithdrawVO.KEY_BENEFICIARYEMAIL);
		String beneficiaryPhone = remark.getString(UserWithdrawVO.KEY_BENEFICIARYPHONE);
		// 身份id
		String idcard = remark.getString(UserWithdrawVO.KEY_IDCARD);

//		String transferType = remark.getString(UserWithdrawVO.KEY_TRANSFER_TYPE);
		BigDecimal transferAmount = remark.getBigDecimal(UserWithdrawVO.KEY_TRANSFER_AMOUNT);
		if(transferAmount != null)
		{
			amountString = transferAmount.toString();
		}

		long time = System.currentTimeMillis();

		Map<String, Object> maps = Maps.newHashMap();

		if(StringUtils.isEmpty(currencyType))
		{
			currencyType = "INR";
		}

		if(StringUtils.isEmpty(beneficiaryName))
		{
			beneficiaryName = "Arial";
		}

		if(StringUtils.isEmpty(beneficiaryEmail))
		{
			beneficiaryEmail = "testaaa@gmail.com";
		}

		if(StringUtils.isEmpty(beneficiaryPhone))
		{
			beneficiaryPhone = "9999999999";
		}

		String tradeNo = order.getNo();
		if(SystemRunningMode.isBCMode() && order.getSubmitCount() > 0)
		{
			tradeNo = REPEAT_ORDER_PREFIX_FLAG + order.getSubmitCount() + StringUtils.getBottomDividerLine() + tradeNo;
		}

		maps.put(KEY_APPKEYID, key);
		maps.put(KEY_TRADE_NO, tradeNo);
		maps.put(KEY_AMOUNT, amountString);
		maps.put(KEY_CURRENCY, currencyType);
		maps.put(KEY_TIME, time);
		maps.put(KEY_NAME, beneficiaryName);
		maps.put(KEY_EMAIL, beneficiaryEmail);
		maps.put(KEY_PHONE, beneficiaryPhone);
		maps.put(KEY_BANK_NUMBER, account);
		maps.put(KEY_BANK_IFSC, ifsc);
		maps.put(KEY_ID_CARD, idcard);
		maps.put("accountType", type);

//		LOG.info("submit payout: " + FastJsonHelper.jsonEncode(maps));

		String sign = signBank(tradeNo, amountString, beneficiaryName, beneficiaryEmail, ifsc, account, time, key, salt);
		maps.put(KEY_SIGN, sign);


		String url = URL_TEST;
		if(isProd)
		{
			url = targetType.getServer() + URL_PAYOUT;
		}

		JSONObject jsonResult = PaymentRequestHelper.getDefaultInstance().syncPostForJSONResult(url, HttpMediaType.FORM, maps, null);

		if(jsonResult == null || jsonResult.isEmpty())
		{
			return null;
		}

		int code = jsonResult.getIntValue("code");
		PayoutResult payoutResult = new PayoutResult();
		String msg = jsonResult.getString("msg");
		// 30012 重复提交
		if(code == 30012 || SUBMIT_SUCCESS.equalsIgnoreCase(msg))
		{
			payoutResult.setmTxStatus(OrderTxStatus.WAITING);
		}
		else
		{
			LOG.error(order.getNo() + " error for " + msg);
			payoutResult.setErrorMsg(msg);
			payoutResult.setmTxStatus(OrderTxStatus.FAILED);
		}
		return payoutResult;

	}
	
	public static String signBank(String tradeNo, String amount, String name, String email, String ifsc, String bankNumber, long time, String key, String salt)
	{
		String appkeyid = key;
		String secret = salt;

			// sha512(appkeyid|tradeNo|amount|name|ifsc|bankNumber|time|secret);
		StringBuilder buffer = new StringBuilder();
		buffer.append(appkeyid);
		buffer.append(SPLIT);
		buffer.append(tradeNo);
		buffer.append(SPLIT);
		buffer.append(amount);
		buffer.append(SPLIT);
		buffer.append(name);
		buffer.append(SPLIT);
		buffer.append(email);
		buffer.append(SPLIT);
		buffer.append(ifsc);
		buffer.append(SPLIT);
		buffer.append(bankNumber);
		buffer.append(SPLIT);
		buffer.append(time);
		buffer.append(SPLIT);
		buffer.append(secret);
		String hashSequence = buffer.toString();

		return DigestUtils.sha512Hex(hashSequence);
	}
	
//	public static boolean verifyBankSign(AppInfo appInfo, String tradeNo, String amount, String name, String email, String ifsc, String bankNumber, long time, String sign)
//	{
//		String tmpSign = signBank(appInfo.getAccessKeyId(), appInfo.getAccessKeySecret(), tradeNo, amount, name, email, ifsc, bankNumber, time);
//		return tmpSign.equalsIgnoreCase(sign);
//	}
	
	public static String signVPA(String appkeyid, String secret, String tradeNo, String amount, String name, String vpaAddress, long time)
	{
		// sha512(appkeyid|tradeNo|amount|name|vpaAddress|time|secret);
		StringBuilder buffer = new StringBuilder();
		buffer.append(appkeyid);
		buffer.append(SPLIT);
		buffer.append(tradeNo);
		buffer.append(SPLIT);
		buffer.append(amount);
		buffer.append(SPLIT);
		buffer.append(name);
		buffer.append(SPLIT);
		buffer.append(vpaAddress);
		buffer.append(SPLIT);
		buffer.append(time);
		buffer.append(SPLIT);
		buffer.append(secret);
		return DigestUtils.sha512Hex(buffer.toString());
	}
	
//	public static boolean verifyVPASign(AppInfo appInfo, String tradeNo, String amount, String name, String vpaAddress, long time, String sign)
//	{
//		String tmpSign = signVPA(appInfo.getAccessKeyId(), appInfo.getAccessKeySecret(), tradeNo, amount, name, vpaAddress, time);
//		return tmpSign.equalsIgnoreCase(sign);
//	}
	
	public static boolean verifySign(PayResponseForm form, String key, String salt)
	{
		String appkeyid = key;
		String secret = salt;
		// sha512(appkeyid|tradeNo|amount|time|status|secret);
		StringBuilder buffer = new StringBuilder();
		buffer.append(appkeyid);
		buffer.append(SPLIT);
		buffer.append(form.getString(KEY_TRADE_NO));
		buffer.append(SPLIT);
		buffer.append(form.getString(KEY_AMOUNT));
		buffer.append(SPLIT);
		buffer.append(form.getString(KEY_TIME));
		buffer.append(SPLIT);
		buffer.append(form.getString("status"));
		buffer.append(SPLIT);
		buffer.append(secret);

		String sign = form.getString(KEY_SIGN);

		String tmpSign = DigestUtils.sha512Hex(buffer.toString());
		return tmpSign.equalsIgnoreCase(sign);
	}
	
	public static void main(String[] args)
	{
		String appkeyid = "3bc84969a5f549639619656d72fd5726";
		String secret = "80ddd2f8ca704c5282453971545708b5";
		String tradeNo = "20201223977111";
		String amount = "100.00";
		long time = 1612576663043L;

		String name = "test";
		String email = "test@gmail.com";
		String bankNumber = "026291800001191";
		String bankIfsc = "YESB0000262";

//		PayoutTransactionStatus status = PayoutTransactionStatus.REALIZED;
//		String webhookSign = signResponseWebhook(appkeyid, secret, tradeNo, amount, time, status);
//		System.out.println("webhook sign = " + webhookSign);


//		String bankSign = signBank(appkeyid, secret, tradeNo, amount, name, email, bankIfsc, bankNumber, time);
//		System.out.println("request sign = " + bankSign);

//		String webhookSign = signResponseWebhook(appkeyid, secret, tradeNo, amount, time, PayoutTransactionStatus.REALIZED);
//		System.out.println("webhook sign = " + webhookSign);

	}
	
}
