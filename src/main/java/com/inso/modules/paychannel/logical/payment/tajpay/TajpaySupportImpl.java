package com.inso.modules.paychannel.logical.payment.tajpay;

import java.math.BigDecimal;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.passport.PayResponseForm;
import com.inso.modules.passport.business.model.WithdrawOrder;
import com.inso.modules.paychannel.helper.EmailPhoneHelper;
import com.inso.modules.paychannel.logical.payment.BasePaymentSupport;
import com.inso.modules.paychannel.logical.payment.model.PayoutResult;
import com.inso.modules.paychannel.model.ChannelInfo;

public class TajpaySupportImpl extends BasePaymentSupport {

    private static String mSplit = "|";

    private static String CALLBACK_URL = "/passport/payment/tajpay/payin";

    @Override
    public Map<String, Object> encryptPayinRequest(ChannelInfo channel, String txnid, BigDecimal amount, String productinfo, String email, String phone) {

        JSONObject paymentInfo = channel.getSecretInfo();

        String key = paymentInfo.getString("key");
        String salt = paymentInfo.getString("salt");
        String env = paymentInfo.getString("env");
        String shopServer = paymentInfo.getString("shopServer");

        email = EmailPhoneHelper.nextEmail();
        phone = EmailPhoneHelper.nextPhone();

        String callbackUrl = shopServer + CALLBACK_URL;
        long time = System.currentTimeMillis() - 3000;

        Map<String, Object> maps = Maps.newHashMap();

        // must to be add appkeySecret
        maps.put(TajpayPayinHelper.KEY_APPKEYID, key);
        maps.put(TajpayPayinHelper.KEY_TRADENO, txnid);
        maps.put(TajpayPayinHelper.KEY_AMOUNT, amount.toString());
        maps.put(TajpayPayinHelper.KEY_CURRENCY, TajpayPayinHelper.VALEU_CURRENCY);

        maps.put(TajpayPayinHelper.KEY_PRODUCTINFO, productinfo);
        maps.put(TajpayPayinHelper.KEY_TIME, time);

        maps.put(TajpayPayinHelper.KEY_EMAIL, email);
        maps.put(TajpayPayinHelper.KEY_PHONE, phone);

        TajpayPayinHelper.signParameter(maps, null, salt, false);

        maps.put(TajpayPayinHelper.KEY_CALLBACK_URL, callbackUrl);

        return maps;
    }

    @Override
    public boolean verifyPayinResponse(ChannelInfo channel, PayResponseForm form) {
        JSONObject paymentInfo = channel.getSecretInfo();

        String key = paymentInfo.getString("key");
        String salt = paymentInfo.getString("salt");
        boolean isBack = form.getBooleanValue("isBack");

        String sign = StringUtils.asString(form.get(TajpayPayinHelper.KEY_SIGN));
        String status = StringUtils.asString(form.get(TajpayPayinHelper.KEY_STATUS));
        String tmpSign = TajpayPayinHelper.signParameter(form, status, salt, isBack);
        if(StringUtils.isEmpty(tmpSign))
        {
            return false;
        }
        return tmpSign.equalsIgnoreCase(sign);
    }

    @Override
    public PayoutResult createPayout(ChannelInfo channel, WithdrawOrder orderInfo) {
        JSONObject paymentInfo = channel.getSecretInfo();

        String key = paymentInfo.getString("key");
        String salt = paymentInfo.getString("salt");
        String env = paymentInfo.getString("env");
        boolean isProd = "prod".equalsIgnoreCase(env);

        String targetTypeStr = paymentInfo.getString("targetType");
        PaymentTargetType targetType = PaymentTargetType.getType(targetTypeStr);

        return TajpayPayoutHelper.doPayout(isProd, targetType, key, salt, orderInfo);
    }

    public boolean verifyPayoutSign(ChannelInfo channel, PayResponseForm form)
    {
        JSONObject paymentInfo = channel.getSecretInfo();
        String key = paymentInfo.getString("key");
        String salt = paymentInfo.getString("salt");
        return TajpayPayoutHelper.verifySign(form, key, salt);
    }

    @Override
    public PayoutResult getPayoutStatus(ChannelInfo channel, WithdrawOrder orderinfo) {
        return null;
    }


}
