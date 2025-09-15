package com.inso.modules.paychannel.logical.payment.coin;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.contract.Token20Manager;
import com.inso.modules.coin.contract.helper.CoinAddressHelper;
import com.inso.modules.coin.contract.model.TransactionResult;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.core.model.VMType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.PayResponseForm;
import com.inso.modules.passport.business.model.UserWithdrawVO;
import com.inso.modules.passport.business.model.WithdrawOrder;
import com.inso.modules.paychannel.logical.payment.BasePaymentSupport;
import com.inso.modules.paychannel.logical.payment.model.PayoutResult;
import com.inso.modules.paychannel.logical.payment.tajpay.TajpayPayinHelper;
import com.inso.modules.paychannel.model.ChannelInfo;
import com.inso.modules.paychannel.model.CoinPaymentInfo;

import java.math.BigDecimal;
import java.util.Map;

public class CoinSupportImpl extends BasePaymentSupport {

    private static String mSplit = "|";

    private static String CALLBACK_URL = "/passport/payment/tajpay/payin";

    @Override
    public Map<String, Object> encryptPayinRequest(ChannelInfo channel, String txnid, BigDecimal amount, String productinfo, String email, String phone) {

        JSONObject paymentInfo = channel.getSecretInfo();

        String accountPrivateKey = paymentInfo.getString("accountPrivateKey");
        String accountAddress = paymentInfo.getString("accountAddress");
        String env = paymentInfo.getString("env");
        String shopServer = paymentInfo.getString("shopServer");

        String callbackUrl = shopServer + CALLBACK_URL;
        long time = System.currentTimeMillis() - 3000;

        Map<String, Object> maps = Maps.newHashMap();

        // must to be add appkeySecret
//        maps.put("accountAddress", accountAddress);
//        maps.put(TajpayPayinHelper.KEY_TRADENO, txnid);
//        maps.put(TajpayPayinHelper.KEY_AMOUNT, amount.toString());
//        maps.put(TajpayPayinHelper.KEY_CURRENCY, TajpayPayinHelper.VALEU_CURRENCY);
//
//        maps.put(TajpayPayinHelper.KEY_PRODUCTINFO, productinfo);
//        maps.put(TajpayPayinHelper.KEY_TIME, time);
//
//        maps.put(TajpayPayinHelper.KEY_EMAIL, email);
//        maps.put(TajpayPayinHelper.KEY_PHONE, phone);
//
//        TajpayPayinHelper.signParameter(maps, null, salt, false);
//
//        maps.put(TajpayPayinHelper.KEY_CALLBACK_URL, callbackUrl);

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

        CoinPaymentInfo paymentInfo = FastJsonHelper.jsonDecode(channel.getSecret(), CoinPaymentInfo.class);

        JSONObject remark = orderInfo.getRemarkVO();
        boolean isNativeToken = remark.getBooleanValue(UserWithdrawVO.KEY_IS_NATIVE_TOKEN);
        String account = remark.getString(UserWithdrawVO.KEY_ACCOUNT);


        CryptoNetworkType networkType = CryptoNetworkType.getType(paymentInfo.getNetworkType());

        if(networkType.getVmType() == VMType.EVM)
        {
            if(!CoinAddressHelper.veriryEVMAddress(paymentInfo.getAccountAddress()))
            {
                PayoutResult payoutResult = new PayoutResult();
                payoutResult.setmTxStatus(OrderTxStatus.FAILED);
                payoutResult.setErrorMsg("Address error !!!");
                return payoutResult;
            }
        }
        else if(networkType.getVmType() == VMType.TVM )
        {
            if(!CoinAddressHelper.veriryTVMAddress(paymentInfo.getAccountAddress()))
            {
                PayoutResult payoutResult = new PayoutResult();
                payoutResult.setmTxStatus(OrderTxStatus.FAILED);
                payoutResult.setErrorMsg("Address error !!!");
                return payoutResult;
            }
        }
        else
        {
            PayoutResult payoutResult = new PayoutResult();
            payoutResult.setmTxStatus(OrderTxStatus.FAILED);
            payoutResult.setErrorMsg("Address error !!!");
            return payoutResult;
        }

        if(isNativeToken)
        {

        }
        else
        {
            String currencyAddr = remark.getString(UserWithdrawVO.KEY_CURRENCY_ADDR);
            int decimals = remark.getIntValue(UserWithdrawVO.KEY_CURRENCY_DECIMALS);
            //数字货币划转手续费
            BigDecimal transferAmount = orderInfo.getAmount().subtract(orderInfo.getFeemoney());
            if(networkType.getVmType() == VMType.TVM ){
                transferAmount = transferAmount.subtract(BigDecimal.valueOf(2));
            }

            TransactionResult transactionResult = Token20Manager.getInstance().transfer(networkType,
                    currencyAddr, decimals,
                    account, transferAmount,
                    paymentInfo.getGasLimit(),
                    paymentInfo.getAccountPrivateKey(), paymentInfo.getAccountAddress());

            PayoutResult result = new PayoutResult();
            result.setmTxStatus(transactionResult.getTxStatus());
            result.setmPayoutId(transactionResult.getExternalTxnid());
            result.setErrorMsg(transactionResult.getMsg());

            return result;
        }

        return null;
    }

    public boolean verifyPayoutSign(ChannelInfo channel, PayResponseForm form)
    {
        JSONObject paymentInfo = channel.getSecretInfo();
        return false;
    }

    @Override
    public PayoutResult getPayoutStatus(ChannelInfo channel, WithdrawOrder orderinfo) {
        CoinPaymentInfo paymentInfo = FastJsonHelper.jsonDecode(channel.getSecret(), CoinPaymentInfo.class);
        CryptoNetworkType networkType = CryptoNetworkType.getType(paymentInfo.getNetworkType());

        TransactionResult result = Token20Manager.getInstance().getTransactionStatus(networkType, orderinfo.getOutTradeNo());
        if(result.getTxStatus() == OrderTxStatus.REALIZED )
        {
            return PayoutResult.SUCCESS_RESULT;
        }
        else if(result.getTxStatus() == OrderTxStatus.FAILED)
        {
            return PayoutResult.FAIT_RESULT;
        }
        return null;
    }


}
