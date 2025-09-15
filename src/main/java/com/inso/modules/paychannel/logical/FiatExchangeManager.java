package com.inso.modules.paychannel.logical;

import com.google.common.collect.Maps;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.modules.admin.config.PlatformConfig;
import com.inso.modules.common.config.PlarformConfig2;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.FiatCurrencyType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.web.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Component
public class FiatExchangeManager {

    @Autowired
    private ConfigService mConfigService;

    private Map<String, BigDecimal> mRateMaps = Maps.newConcurrentMap();

    private long maxExpires = MyEnvironment.isDev() ? 1000 : 60000;
    private long mLatestRefreshTime = System.currentTimeMillis();

    public BigDecimal getExchange(ICurrencyType currencyType)
    {
        String key = null;
        if(currencyType == FiatCurrencyType.INR)
        {
            key = PlatformConfig.ADMIN_APP_PLATFORM_USDT_TO_INR_PLATFORM_RATE;
        }
        else if(currencyType == FiatCurrencyType.MYR)
        {
            key = PlatformConfig.ADMIN_APP_PLATFORM_USDT_TO_MYR_PLATFORM_RATE;
        }
        else if(currencyType == FiatCurrencyType.MNT)
        {
            key = PlarformConfig2.ADMIN_APP_PLATFORM_USDT_TO_MNT_PLATFORM_RATE.getKey();
        }
        else if(currencyType == FiatCurrencyType.BRL)
        {
            key = PlarformConfig2.ADMIN_APP_PLATFORM_USDT_TO_BRL_PLATFORM_RATE.getKey();
        }
        else
        {
            return null;
        }
        refresh();
        BigDecimal rate = mRateMaps.get(key);
        if(rate == null)
        {
            rate = mConfigService.getBigDecimal(false, key);
            if(rate != null)
            {
                mRateMaps.put(key, rate);
            }
        }
        return rate;
    }

    private void refresh()
    {
        long t = System.currentTimeMillis();
        if(t - mLatestRefreshTime > maxExpires)
        {
            mRateMaps.clear();
        }
    }

    public BigDecimal doConvert(ICurrencyType fromCurrency, ICurrencyType toCurrency, BigDecimal amount)
    {
        return doConvert(null, fromCurrency, null, toCurrency, amount);
    }

    public BigDecimal doConvert(BigDecimal fromRate, ICurrencyType fromCurrency, BigDecimal toRate, ICurrencyType toCurrency, BigDecimal amount)
    {
        if(fromCurrency == toCurrency)
        {
            return amount;
        }

        if(fromCurrency == FiatCurrencyType.USD || fromCurrency == CryptoCurrency.USDT || fromCurrency == CryptoCurrency.USDC)
        {
            return convertUSDToTarget(toRate, toCurrency, amount);
        }
        else if(toCurrency == FiatCurrencyType.USD || toCurrency == CryptoCurrency.USDT || toCurrency == CryptoCurrency.USDC)
        {
            return convertFiatOrCrypto_2_USDAmount(fromRate, fromCurrency, amount);
        }
        else
        {
            BigDecimal usdAmount = convertFiatOrCrypto_2_USDAmount(fromRate, fromCurrency, amount);
            if(usdAmount != null)
            {
                return convertUSDToTarget(toRate, toCurrency, usdAmount);
            }
        }
        return null;
    }

    public BigDecimal convertFiatOrCrypto_2_USDAmount(ICurrencyType srcCurrencyType, BigDecimal srcAmount)
    {
        BigDecimal rate = getExchange(srcCurrencyType);
        return convertFiatOrCrypto_2_USDAmount(rate, srcCurrencyType, srcAmount);
    }

    public BigDecimal convertFiatOrCrypto_2_USDAmount(BigDecimal exchangeRate, ICurrencyType srcCurrencyType, BigDecimal srcAmount)
    {
        if(srcCurrencyType == FiatCurrencyType.INR || srcCurrencyType == FiatCurrencyType.MYR || srcCurrencyType == FiatCurrencyType.BRL)
        {
            if(exchangeRate == null)
            {
                exchangeRate = getExchange(srcCurrencyType);
            }
            // 77x=srcamount
            BigDecimal usdAmount = srcAmount.divide(exchangeRate, 2, RoundingMode.HALF_UP);
            return usdAmount;
        }
        else if(srcCurrencyType == FiatCurrencyType.USD || srcCurrencyType == CryptoCurrency.USDT || srcCurrencyType == CryptoCurrency.USDC)
        {
            return srcAmount;
        }
        else if(srcCurrencyType == FiatCurrencyType.CENT)
        {
            // 100Penny=1u,
            BigDecimal usdAmount = srcAmount.divide(BigDecimalUtils.DEF_100, 2, RoundingMode.HALF_UP);
            return usdAmount;
        }
        return null;
    }

    public BigDecimal convertUSDToTarget(ICurrencyType srcCurrencyType, BigDecimal srcAmount)
    {
        BigDecimal rate = getExchange(srcCurrencyType);
        return convertUSDToTarget(rate, srcCurrencyType, srcAmount);
    }

    public BigDecimal convertUSDToTarget(BigDecimal exchangeRate, ICurrencyType targetCurrencyType, BigDecimal srcAmount)
    {
        if(targetCurrencyType == FiatCurrencyType.USD || targetCurrencyType == CryptoCurrency.USDT || targetCurrencyType == CryptoCurrency.USDC)
        {
            return srcAmount;
        }
        else if(targetCurrencyType == FiatCurrencyType.INR || targetCurrencyType == FiatCurrencyType.MYR || targetCurrencyType == FiatCurrencyType.BRL)
        {
            if(exchangeRate == null)
            {
                exchangeRate = getExchange(targetCurrencyType);
            }
            // 77x=srcamount
            BigDecimal usdAmount = srcAmount.multiply(exchangeRate).setScale(2, RoundingMode.DOWN);
            return usdAmount;
        }
        else if(targetCurrencyType == FiatCurrencyType.CENT)
        {
            // 100Penny=1u,
            BigDecimal usdAmount = srcAmount.multiply(BigDecimalUtils.DEF_100);
            return usdAmount;
        }
        return null;
    }

    public static void testRun()
    {
    }

    private void test()
    {
    }

    public static void main(String[] args) {


    }

}
