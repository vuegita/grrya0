package com.inso.modules.coin.core.model;

import com.google.common.collect.Maps;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.approve.logical.ContractInfoInit;
import com.inso.modules.common.model.CryptoCurrency;

import java.util.Map;

/**
 * 代币余额信息配置
 */
public class TokenAssertConfig {


    private static Map<String, TokenAssertInfo> mTokenInfoMaps = Maps.newConcurrentMap();
    private static Map<String, TokenAssertInfo> mNetwork2ContractMap = Maps.newConcurrentMap();

    static {
        synchronized (TokenAssertConfig.class)
        {
            if(mTokenInfoMaps.isEmpty())
            {
                ContractInfoInit.loadAllConfig(new Callback<TokenAssertInfo>() {
                    @Override
                    public void execute(TokenAssertInfo o) {
                        addConfig(o);
                    }
                });
            }

        }
    }

    private static void addConfig(CryptoNetworkType networkType, CryptoCurrency currency, String contractAddress, int decimals)
    {
        TokenAssertInfo tokenInfo = new TokenAssertInfo();
        tokenInfo.setContractAddress(contractAddress);
        tokenInfo.setDecimals(decimals);
        tokenInfo.setCurrencyType(currency);
        tokenInfo.setNetworkType(networkType);
        addConfig(tokenInfo);
    }

    private static void addConfig(TokenAssertInfo tokenInfo)
    {
        CryptoNetworkType networkType = tokenInfo.getNetworkType();
        CryptoCurrency currency = tokenInfo.getCurrencyType();
        String contractAddress = StringUtils.getNotEmpty(tokenInfo.getContractAddress());

        String network2CurrencyKey = networkType.getKey() + currency.getKey();
        String network2AddressKey = networkType.getKey() + contractAddress.toLowerCase();

        mTokenInfoMaps.put(network2CurrencyKey, tokenInfo);
        mNetwork2ContractMap.put(network2AddressKey, tokenInfo);
    }


    public static TokenAssertInfo getTokenInfo(CryptoNetworkType networkType, CryptoCurrency currency) {
        String key = networkType.getKey() + currency.getKey();
        return mTokenInfoMaps.get(key);
    }

    public static TokenAssertInfo getTokenInfo(CryptoNetworkType networkType, String tokenContractAddress) {
        if(StringUtils.isEmpty(tokenContractAddress))
        {
            return null;
        }
        tokenContractAddress = tokenContractAddress.toLowerCase();
        String key = networkType.getKey() + tokenContractAddress;
        return mNetwork2ContractMap.get(key);
    }

    public static void main(String[] args) {
        TokenAssertInfo assertInfo = TokenAssertConfig.getTokenInfo(CryptoNetworkType.BNB_MAINNET, CryptoCurrency.USDT);

        System.out.println(assertInfo.getDecimals());
    }
}
