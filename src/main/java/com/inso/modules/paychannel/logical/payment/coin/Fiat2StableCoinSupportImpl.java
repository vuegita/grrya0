package com.inso.modules.paychannel.logical.payment.coin;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.modules.coin.contract.Token20Manager;
import com.inso.modules.coin.contract.helper.CoinAddressHelper;
import com.inso.modules.coin.contract.model.TransactionResult;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.core.model.VMType;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.FiatCurrencyType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.PayResponseForm;
import com.inso.modules.passport.business.model.UserWithdrawVO;
import com.inso.modules.passport.business.model.WithdrawOrder;
import com.inso.modules.paychannel.logical.FiatExchangeManager;
import com.inso.modules.paychannel.logical.payment.BasePaymentSupport;
import com.inso.modules.paychannel.logical.payment.model.PayoutResult;
import com.inso.modules.paychannel.model.ChannelInfo;
import com.inso.modules.paychannel.model.CoinPaymentInfo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

public class Fiat2StableCoinSupportImpl extends BasePaymentSupport {

//    private static String mSplit = "|";
//    private static String CALLBACK_URL = "/passport/payment/tajpay/payin";

    private static BigDecimal mINR2USDRate = new BigDecimal(83);

    private static FiatExchangeManager mFiatExchangeManager;

    @Override
    public Map<String, Object> encryptPayinRequest(ChannelInfo channel, String txnid, BigDecimal amount, String productinfo, String email, String phone) {
        return null;
    }

    @Override
    public boolean verifyPayinResponse(ChannelInfo channel, PayResponseForm form) {
        return false;
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
            BigDecimal transferAmount = convert2USD(orderInfo.getAmount().subtract(orderInfo.getFeemoney()));

            if(transferAmount == null)
            {
                PayoutResult result = new PayoutResult();
                result.setmTxStatus(OrderTxStatus.FAILED);
                result.setErrorMsg("当前币种配置异常!");
                return result;
            }

            String currencyAddr = remark.getString(UserWithdrawVO.KEY_CURRENCY_ADDR);
            int decimals = remark.getIntValue(UserWithdrawVO.KEY_CURRENCY_DECIMALS);
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


    private static FiatExchangeManager getFiatExchangeManager()
    {
        if(mFiatExchangeManager == null)
        {
            mFiatExchangeManager = SpringContextUtils.getBean(FiatExchangeManager.class);
        }
        return mFiatExchangeManager;
    }

    public static BigDecimal convert2USD(BigDecimal amount)
    {
        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
        if(currencyType == FiatCurrencyType.INR)
        {
            return amount.divide(mINR2USDRate, 2, RoundingMode.HALF_UP);
        }
        else if(currencyType == FiatCurrencyType.BRL)
        {
            return getFiatExchangeManager().convertFiatOrCrypto_2_USDAmount(currencyType, amount);
        }
        else if(currencyType == FiatCurrencyType.CENT)
        {
            return amount.divide(BigDecimalUtils.DEF_100, 2, RoundingMode.HALF_UP);
        }
        else if(currencyType == FiatCurrencyType.USD || currencyType == CryptoCurrency.USDT || currencyType == CryptoCurrency.USDC)
        {
            return amount;
        }
        return null;
    }

    public boolean verifyPayoutSign(ChannelInfo channel, PayResponseForm form)
    {
//        JSONObject paymentInfo = channel.getSecretInfo();
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
