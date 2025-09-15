package com.inso.modules.coin.core.logical;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.context.MyEnvironment;
import com.inso.modules.paychannel.helper.PaymentRequestHelper;

import java.math.BigDecimal;

public class BinanceApiManager  {

    protected PaymentRequestHelper mHttpHelper;

    private interface MyInternal {
        public BinanceApiManager mgr = new BinanceApiManager();
    }

    private BinanceApiManager()
    {
        this.mHttpHelper = PaymentRequestHelper.getInstance();
    }


    public static BinanceApiManager getInstance()
    {
        return MyInternal.mgr;
    }

    private static String URL_GET_LAST_PRICE = "https://api.binance.com/api/v3/ticker/price?symbol=";

    private static String KEY_PRICE = "price";



    public BigDecimal getLatestPrice(String baseCurrency, String quoteCurrency)
    {
        String url = URL_GET_LAST_PRICE + quoteCurrency + baseCurrency;
        JSONObject jsonObject = mHttpHelper.syncGetForJSONResult(url, null);
        if(jsonObject == null || jsonObject.isEmpty())
        {
            return null;
        }
        return jsonObject.getBigDecimal(KEY_PRICE);

    }

    public static void main(String[] args) {
        BinanceApiManager mgr = new BinanceApiManager();
//        mgr.loadSportKlineHistory(CryptoCurrency.USDT, CryptoCurrencyType.BTC, KlinePeriodType.MON_1, 100);

        BigDecimal lastPrice = mgr.getLatestPrice("USDT", "BTC");

        System.out.println(lastPrice);

    }
}
