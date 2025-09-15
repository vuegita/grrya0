package com.inso.modules.game.lottery_game_impl.btc_kline.helper;

import com.alibaba.fastjson.JSONArray;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;
import com.inso.framework.utils.ThreadUtils;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.paychannel.helper.PaymentRequestHelper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

public class BTCResultHelper {

    private static Log LOG = LogFactory.getLog(BTCResultHelper.class);

    private static String KLINE_URL = "https://api.binance.com/api/v3/klines?";
//    private static String KLINE_URL = "https://api.binance.com/fapi/v1/klines?";


    private static final String ROOT_CACHE = BTCResultHelper.class.getName()+ "_v2";

    private static CryptoCurrency mBTCCurrency = CryptoCurrency.BTC;

    private static boolean debug = false;

    private interface MyInternal {
        public BTCResultHelper mgr = new BTCResultHelper();
    }

    private BTCResultHelper()
    {
    }
    public static BTCResultHelper getInstance()
    {
        return MyInternal.mgr;
    }

    public String getKlineClosePrice(String issue, long openTime, int internal)
    {
        String prefixCacheKey = ROOT_CACHE + mBTCCurrency.getKey() + internal;
        String cachekey = prefixCacheKey + openTime;
        String result = CacheManager.getInstance().getString(cachekey);
        if(!debug && result != null)
        {
            return result;
        }

        result = loadKlineClosePrice(issue, openTime, mBTCCurrency.getKey(), internal, prefixCacheKey);
        return result;
    }

    private String loadKlineClosePrice(String issue, long endOpenTime, String quoteCurency, int internal, String prefixCacheKey)
    {
        long limitCount = (System.currentTimeMillis() - endOpenTime )/ (60_000 * internal) + 5;
        if(limitCount <= 30)
        {
            limitCount = 30;
        }
        if(limitCount >= 500)
        {
            limitCount = 500;
        }

        StringBuilder buffer = new StringBuilder();
        buffer.append(KLINE_URL);
        buffer.append("symbol=").append(quoteCurency).append(CryptoCurrency.USDT.getKey());
        buffer.append("&interval=").append(internal).append("m");
        buffer.append("&limit=").append(limitCount);

//        if(endOpenTime > 0)
//        {
//            long ts = endOpenTime + internal * 60_000 + 1000;
//            System.out.println(" begin time = " + new DateTime(endOpenTime).toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS));
//            System.out.println(" end time = " + new DateTime(ts).toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS));
//            buffer.append("&startTime=").append(endOpenTime - 60_000);
//            buffer.append("&endTime=").append(ts);
//        }

        String url = buffer.toString();

        JSONArray jsonArray = PaymentRequestHelper.getInstance().syncGetForJSONArray(url, null);

//        if(jsonArray == null)
//        {
//            for(int i = 0; i < 5; i ++)
//            {
//                jsonArray = PaymentRequestHelper.getInstance().syncGetForJSONArray(url, null);
//                if(jsonArray != null)
//                {
//                    break;
//                }
//                ThreadUtils.sleep(1000);
//                LOG.warn("retry load kline data for issue = " + issue + ", internal = " + internal);
//            }
//        }

        if(jsonArray == null || jsonArray.isEmpty())
        {
            LOG.warn("load kline error: internal = " + internal);
            return null;
        }

        int len = jsonArray.size();

        if(len < 2)
        {
            return null;
        }

        String result = null;

        if(debug)
        {
            FastJsonHelper.prettyJson(jsonArray);
        }

        // 去掉第一个0的位置
        for(int i = len - 2; i >= 0; i --)
        {
            JSONArray item = jsonArray.getJSONArray(i);
            BigDecimal closePrice = item.getBigDecimal(4).setScale(2, RoundingMode.DOWN);
            long openTime = item.getLong(0);

//            if(openTime > endOpenTime)
//            {
//                continue;
//            }

            String closePriceStr = closePrice.toString();

            if(endOpenTime == openTime)
            {
                result = closePriceStr;
            }

            String cachekey = prefixCacheKey + openTime;
            if(CacheManager.getInstance().exists(cachekey))
            {
                continue;
            }
            CacheManager.getInstance().setString(cachekey, closePriceStr, CacheManager.EXPIRES_HOUR_2);

;//            if(debug)
//            {
                int closeLen = closePriceStr.length();
                int endCloseValue = StringUtils.asInt(closePriceStr.substring(closeLen - 1, closeLen));
                DateTime dateTime = new DateTime(openTime, DateTimeZone.UTC);
                LOG.info( "internal = " + internal + "m, " + dateTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS) + ", open time = " + openTime + ",  closePrice = " + closePrice + ", endCloseValue = " + endCloseValue);
//            }

        }
        return result;
    }



    private void test()
    {
        String date = "2023-03-24 20:50:00";
        Date pdate = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, date);
        long ts = pdate.getTime();

        DateTime nowTime = DateTime.now();

//        ts = -1;
        String lastPrice = getKlineClosePrice(null, nowTime.minusMinutes(100).getMillis(), 1);
        System.out.println(lastPrice);
    }


    public static void main(String[] args) {
        debug = true;
        BTCResultHelper mgr = new BTCResultHelper();
        mgr.test();


    }
}
