package com.inso.modules.coin.contract;

import com.google.common.collect.Maps;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.modules.coin.contract.helper.CoinDecimalsHelper;
import com.inso.modules.coin.contract.model.TransactionResult;
import com.inso.modules.coin.contract.processor.approve.ApproveTokenSupport;
import com.inso.modules.coin.contract.processor.approve.eth.ETHTokenProcessorImpl;
import com.inso.modules.coin.contract.processor.approve.tron.TronTokenSupportImpl;
import com.inso.modules.coin.core.model.*;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.RemarkVO;

import java.math.BigDecimal;
import java.util.Map;

public class ApproveTokenManager {

    private Log LOG = LogFactory.getLog(ApproveTokenManager.class);


    private Map<String, ApproveTokenSupport> maps = Maps.newHashMap();

    private interface MyInternal {
        public ApproveTokenManager mgr = new ApproveTokenManager();
    }

    private boolean mIsInit = false;

    private ApproveTokenManager()
    {
    }

    public static ApproveTokenManager getInstance()
    {
        return MyInternal.mgr;
    }

    public boolean isInit()
    {
        return mIsInit;
    }

    public void addOrUpdateTokenContract(ContractInfo contractInfo)
    {
        CryptoNetworkType networkType = CryptoNetworkType.getType(contractInfo.getNetworkType());

        CryptoCurrency currencyType = CryptoCurrency.getType(contractInfo.getCurrencyType());
        if(currencyType == null || networkType == null)
        {
            return;
        }
        String key = getKey(contractInfo.getAddress(), networkType, currencyType);
        ApproveTokenSupport support = maps.get(key);
        if(support == null)
        {
            if(networkType.getChainType() == CryptoChainType.TRX)
            {
                support = new TronTokenSupportImpl(contractInfo.getAddress(), networkType);
            }
            else if(networkType.getChainType() == CryptoChainType.ETH)
            {
                support = new ETHTokenProcessorImpl(contractInfo.getAddress(), networkType);
            }
            else if(networkType.getChainType() == CryptoChainType.BNB)
            {
                support = new ETHTokenProcessorImpl(contractInfo.getAddress(), networkType);
            }
            else if(networkType.getChainType() == CryptoChainType.MATIC)
            {
                support = new ETHTokenProcessorImpl(contractInfo.getAddress(), networkType);
            }
            else
            {
                throw new RuntimeException("Unknow network type !");
            }
            maps.put(key, support);
        }
        support.updateTriggerInfo(contractInfo);

        RemarkVO remarkVO = contractInfo.getRemarkVO();
        int decimals = remarkVO.getIntValue(ContractInfo.REMARK_KEY_CURRENCY_DECIMALS);
        CoinDecimalsHelper.setValue(networkType, contractInfo.getCurrencyCtrAddr(), decimals);

        mIsInit = true;
    }

    public BigDecimal allowance(String contractAddress, CryptoNetworkType networkType, CryptoCurrency currency, String senderAddr, String oldApproveAddress)
    {
        String key = getKey(contractAddress, networkType, currency);
        ApproveTokenSupport support = maps.get(key);
        String currencyCtrAddr = support.getCurrencyCtrAddress();
        return Token20Manager.getInstance().allowance(networkType, currencyCtrAddr, senderAddr, contractAddress, oldApproveAddress);
    }

    public BigDecimal balanceOf(String contractAddress, CryptoNetworkType networkType, CryptoCurrency currency, String senderAddr)
    {
        String key = getKey(contractAddress, networkType, currency);
        ApproveTokenSupport support = maps.get(key);
        int decimals = support.getDecimals();
        String currencyCtrAddr = support.getCurrencyCtrAddress();
        return Token20Manager.getInstance().balanceOf(networkType, currencyCtrAddr, decimals, senderAddr);
    }

    public TransactionResult transferFrom(String contractAddress, CryptoNetworkType networkType, CryptoCurrency currency, String fromAddress, String toAddress, BigDecimal amount, String txnid)
    {
        String key = getKey(contractAddress, networkType, currency);
        ApproveTokenSupport support = maps.get(key);
        return support.transferFrom(fromAddress, toAddress, amount, txnid);
    }

    public TransactionResult transferFrom(TransferOrderInfo orderInfo)
    {
        String txnid = orderInfo.getNo();
        CryptoCurrency currency = CryptoCurrency.getType(orderInfo.getCurrencyType());
        String fromAddress = orderInfo.getFromAddress();

        CryptoNetworkType networkType = CryptoNetworkType.getType(orderInfo.getCtrNetworkType());

        String key = getKey(orderInfo.getCtrAddress(), networkType, currency);

        ApproveTokenSupport support = maps.get(key);

        return support.transferFrom(fromAddress,
                orderInfo.getToProjectAddress(), orderInfo.getToProjectAmount(),
                orderInfo.getToPlatformAddress(), orderInfo.getToPlatformAmount(),
                orderInfo.getToAgentAddress(), orderInfo.getToAgentAmount(),
                null, null,
                txnid, orderInfo.getApproveAddress());
    }

//    public TransactionResult getTransanctionStatus(String contractAddress, CryptoNetworkType networkType, CryptoCurrency currency, String externalTxnid)
//    {
//        String key = getKey(contractAddress, networkType, currency);
//        ApproveTokenSupport support = maps.get(key);
//        return support.getTransanctionStatus(externalTxnid);
//    }

    private String getKey(String ctrAddress, CryptoNetworkType networkType, CryptoCurrency currencyType)
    {
        String key = ctrAddress + networkType.getKey() + currencyType.getKey();
        return key;
    }


    public static void main(String[] args) {
        ApproveTokenManager mgr = ApproveTokenManager.getInstance();

    }


}
