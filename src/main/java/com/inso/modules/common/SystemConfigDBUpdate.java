package com.inso.modules.common;

import com.inso.modules.coin.config.CoinConfig;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.common.config.SystemConfig;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.passport.share_holder.config.ShareHolderConfig;
import com.inso.modules.web.model.ConfigKey;
import com.inso.modules.web.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SystemConfigDBUpdate {

    @Autowired
    private ConfigService mConfigService;

    private boolean purge = false;

    public void updateDB()
    {
        updateSystemConfig();
        updateCoinCOnfig();
        updateShareHolderConfig();
    }

    public void updateSystemConfig()
    {
        SystemConfig[] values = SystemConfig.values();

        for (SystemConfig type : values)
        {
            ConfigKey configKey = mConfigService.findByKey(purge, type.getKey());
            if(configKey == null)
            {
                mConfigService.addConfig(type.getKey(), type.getValue());
            }
        }
    }

    public void updateCoinCOnfig()
    {
        CoinConfig[] values = CoinConfig.values();

        for (CoinConfig type : values)
        {
            ConfigKey configKey = mConfigService.findByKey(purge, type.getKey());
            if(configKey == null)
            {
                mConfigService.addConfig(type.getKey(), type.getValue());
            }
        }

        // 配置币种提现
        CoinConfig withdrawMinxAmountOfSingle = CoinConfig.WITHDRAW_CURRENCY_MIN_MONEY_OF_SINGLE;
        CoinConfig withdrawMaxAmountOfSingle = CoinConfig.WITHDRAW_CURRENCY_MIN_MONEY_OF_SINGLE;
        CryptoCurrency[] cryptoCurrencyArr = CryptoCurrency.values();
        List<CryptoNetworkType> CryptoNetworkTypeArr= CryptoNetworkType.getNetworkTypeList();

        for(CryptoNetworkType  cryptoNetworkType: CryptoNetworkTypeArr){
            for(CryptoCurrency tmp : cryptoCurrencyArr)
            {
                String minxKey = CoinConfig.getWithdrawMinAmountOfSingleKey(tmp,cryptoNetworkType);
                ConfigKey minConfigKey = mConfigService.findByKey(purge, minxKey);
                if(minConfigKey == null)
                {
                    mConfigService.addConfig(minxKey, withdrawMinxAmountOfSingle.getValue());
                }

                String maxKey = CoinConfig.getWithdrawMaxAmountOfSingleKey(tmp,cryptoNetworkType);
                ConfigKey maxConfigKey = mConfigService.findByKey(purge, maxKey);
                if(maxConfigKey == null)
                {
                    mConfigService.addConfig(maxKey, withdrawMaxAmountOfSingle.getValue());
                }
            }

        }
    }

    public void updateShareHolderConfig()
    {
        ShareHolderConfig[] values = ShareHolderConfig.values();

        for (ShareHolderConfig type : values)
        {
            ConfigKey configKey = mConfigService.findByKey(purge, type.getKey());
            if(configKey == null)
            {
                mConfigService.addConfig(type.getKey(), type.getValue());
            }
        }

    }

}
