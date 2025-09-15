package com.inso.modules.paychannel.logical.payment.wallet;

import com.inso.framework.utils.MD5;
import com.inso.modules.passport.PayResponseForm;
import com.inso.modules.passport.business.model.WithdrawOrder;
import com.inso.modules.paychannel.logical.payment.BasePaymentSupport;
import com.inso.modules.paychannel.logical.payment.model.PayoutResult;
import com.inso.modules.paychannel.model.ChannelInfo;

import java.math.BigDecimal;
import java.util.Map;

public class WalletSupportImpl extends BasePaymentSupport {

    private static String mSplit = "|";

    private static String CALLBACK_URL = "/pay/payment/bank/payin";

    private static String DEFAULT_SALT = "?><>ofsdf&^%jsdfuks";

    @Override
    public Map<String, Object> encryptPayinRequest(ChannelInfo channel, String txnid, BigDecimal amount, String productinfo, String email, String phone) {

//        JSONObject paymentInfo = channel.getSecretInfo();
//
//        String bankName = paymentInfo.getString("bankName");
//        String bankCode = paymentInfo.getString("bankCode");
//        String bankAccount = paymentInfo.getString("bankAccount");
//
//        String shopServer = paymentInfo.getString("shopServer");
//        String callbackUrl = shopServer + CALLBACK_URL;
//
//        Map<String, Object> maps = Maps.newHashMap();
//
//        long time = System.currentTimeMillis();
//
//        // must to be add appkeySecret
//        maps.put("bankName", bankName);
//        maps.put("bankCode", bankCode);
//        maps.put("bankAccount", bankAccount);
//        maps.put("amount", amount.toString());
//        maps.put("txnid", txnid);
//        // 支付产品类型
//        maps.put("productType", PayProductType.BANK.getKey());
//        // 支付链接-
//        maps.put("payUrl", StringUtils.getEmpty());
//        maps.put(TajpayPayinHelper.KEY_CALLBACK_URL, callbackUrl);
//        maps.put("time", time);
//        maps.put("sign", encrypParameter(time, txnid));

        return null;
    }

    private static String encrypParameter(long time, String txnid)
    {
        return MD5.encode(DEFAULT_SALT + time + txnid);
    }

    @Override
    public boolean verifyPayinResponse(ChannelInfo channel, PayResponseForm form) {
        long time = form.getLong("time");
        String sign = form.getString("sign");
        String txnid = form.getString("txnid");

        String tmpSign = encrypParameter(time, txnid);
        return tmpSign.equalsIgnoreCase(sign);
    }

    @Override
    public PayoutResult createPayout(ChannelInfo channel, WithdrawOrder orderInfo) {
//        JSONObject paymentInfo = channel.getSecretInfo();
//
//        String key = paymentInfo.getString("key");
//        String salt = paymentInfo.getString("salt");
//        String env = paymentInfo.getString("env");
//        boolean isProd = "prod".equalsIgnoreCase(env);
//
//        String targetTypeStr = paymentInfo.getString("targetType");
//        PaymentTargetType targetType = PaymentTargetType.getType(targetTypeStr);

//        return TajpayPayoutHelper.doPayout(isProd, targetType, key, salt, orderInfo);

        return PayoutResult.WAITING_RESULT;
    }

    public boolean verifyPayoutSign(ChannelInfo channel, PayResponseForm form)
    {
//        JSONObject paymentInfo = channel.getSecretInfo();
//        String key = paymentInfo.getString("key");
//        String salt = paymentInfo.getString("salt");
//        return TajpayPayoutHelper.verifySign(form, key, salt);

        return false;
    }

    @Override
    public PayoutResult getPayoutStatus(ChannelInfo channel, WithdrawOrder orderinfo) {
        return null;
    }


}
