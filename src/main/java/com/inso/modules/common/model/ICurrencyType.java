package com.inso.modules.common.model;

import com.google.common.collect.Lists;
import com.inso.framework.conf.MyConfiguration;
import com.inso.modules.web.SystemRunningMode;
import org.springframework.ui.Model;

import java.util.List;

public interface ICurrencyType {

    static final String CONFIG_KEY = "system.support.currency";

    public String getKey();

    // Fiat ~ Crypto
    public String getCategory();



    public static ICurrencyType getType(String key)
    {
        ICurrencyType currencyType = FiatCurrencyType.getType(key);
        if(currencyType == null)
        {
            currencyType = CryptoCurrency.getType(key);
        }
        return currencyType;
    }

    public static ICurrencyType getSupportCurrency()
    {
        MyConfiguration conf = MyConfiguration.getInstance();
        String currency = conf.getString(CONFIG_KEY);
        return getType(currency);
    }

    public static void addModel(Model model)
    {
        List<ICurrencyType> list = Lists.newArrayList();
        FiatCurrencyType[] fiatCurrencyTypes = FiatCurrencyType.values();

        CryptoCurrency[] cryptoCurrencyArr = CryptoCurrency.values();
        for(ICurrencyType tmp : cryptoCurrencyArr)
        {
            list.add(tmp);
        }

        if(SystemRunningMode.isBCMode() || SystemRunningMode.isFundsMode())
        {
            for(ICurrencyType tmp : fiatCurrencyTypes)
            {
                list.add(tmp);
            }
        }

        model.addAttribute("currencyTypeList", list);
        model.addAttribute("currentCurrency", ICurrencyType.getSupportCurrency());
    }

    public static void addCryptoModel(Model model)
    {
        List<ICurrencyType> list = Lists.newArrayList();
        if(SystemRunningMode.isCryptoMode())
        {
            CryptoCurrency[] cryptoCurrencyArr = CryptoCurrency.values();
            for(ICurrencyType tmp : cryptoCurrencyArr)
            {
                list.add(tmp);
            }
        }
        model.addAttribute("currencyTypeList", list);
    }

    public static void main(String[] args) {


        System.out.println(ICurrencyType.getSupportCurrency().getKey());
    }
}
