package com.inso.modules.coin;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Maps;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.paychannel.helper.PaymentRequestHelper;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

public class BinanceApiManager {

    private static String KLINE_URL = "https://api.binance.com/api/v3/klines?";

    private static String URL_GET_LAST_PRICE = "https://api.binance.com/api/v3/ticker/price?symbol=";

    private static String KEY_PRICE = "price";

    private Map<Integer, Integer> maps = Maps.newTreeMap();

    private File mFile = new File("C:/Users/Administrator/Desktop/aaaaaa.txt");

    private int count = 0;

    private interface MyInternal {
        public BinanceApiManager mgr = new BinanceApiManager();
    }

    private BinanceApiManager()
    {
    }
    public static BinanceApiManager getInstance()
    {
        return MyInternal.mgr;
    }

    public long getKlineClosePrice(long endTime, String quoteCurency, int internal)
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append(KLINE_URL);
        buffer.append("symbol=").append(quoteCurency).append(CryptoCurrency.USDT.getKey());
        buffer.append("&interval=").append(internal).append("m");
        buffer.append("&limit=500");

        if(endTime > 0)
        {
            buffer.append("&endTime=").append(endTime);
        }

        String url = buffer.toString();

        JSONArray jsonArray = PaymentRequestHelper.getInstance().syncGetForJSONArray(url, null);

        if(jsonArray == null || jsonArray.isEmpty())
        {
            return -1;
        }


        StringBuilder rsBuffer = new StringBuilder();

        long lastTime = -1;
        String endFlag = "\n";
        int len = jsonArray.size();
        for(int i = 0; i < len; i ++)
        {
            JSONArray item = jsonArray.getJSONArray(i);
            BigDecimal closePrice = item.getBigDecimal(4).setScale(2, RoundingMode.DOWN);
            long closeTime = item.getLong(6);

            String closePriceStr = closePrice.toString();
            int closeLen = closePriceStr.length();
            int endCloseValue = StringUtils.asInt(closePriceStr.substring(closeLen - 1, closeLen));

            Integer value = maps.get(endCloseValue);
            if(value == null)
            {
                value = 1;
            }
            else
            {
                value ++;
            }

            maps.put(endCloseValue, value);

            rsBuffer.append(closeTime).append(StringUtils.VERTICAL_LINE).append(closePrice);
            rsBuffer.append(StringUtils.VERTICAL_LINE).append(endCloseValue);
            rsBuffer.append(endFlag);

            lastTime = closeTime;
//            DateTime dateTime = new DateTime(closeTime);
//            System.out.println(dateTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS) + ",  closePrice = " + closePrice);

            count ++;
        }

        System.out.println("current count = " + count);


        try {
            FileUtils.writeStringToFile(mFile, rsBuffer.toString(), StringUtils.UTF8, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lastTime;
    }



    private void test()
    {
        DateTime dateTime = DateTime.now().plusYears(1);

        maps.put(0, 196004);
        maps.put(1, 212008);
        maps.put(2, 215954);
        maps.put(3, 200006);
        maps.put(4, 216005);
        maps.put(5, 192002);
        maps.put(6, 208011);
        maps.put(7, 200003);
        maps.put(8, 188003);
        maps.put(9, 172004);


        long ts = 1678615019999l;
        for(int i = 0; i < 2000; i ++)
        {
            ts = getKlineClosePrice(ts, CryptoCurrency.BTC.getKey(), 1);
        }




        String rs = FastJsonHelper.jsonEncode(maps);
        try {
            FileUtils.writeStringToFile(mFile, "\n\n", StringUtils.UTF8, true);
            FileUtils.writeStringToFile(mFile, "Total count = " + count, StringUtils.UTF8, true);
            FileUtils.writeStringToFile(mFile, "\n\n", StringUtils.UTF8, true);
            FileUtils.writeStringToFile(mFile, rs, StringUtils.UTF8, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FastJsonHelper.prettyJson(maps);
    }


    public static void main(String[] args) {
        BinanceApiManager mgr = new BinanceApiManager();
        mgr.test();
//        BigDecimal lastPrice = mgr.getKlineClosePrice(-1, CryptoCurrency.BTC.getKey(), 1);
//        System.out.println(lastPrice);
    }
}
