package com.inso.modules.paychannel;

import com.google.common.collect.Maps;
import com.inso.framework.http.HttpCallback;
import com.inso.framework.http.HttpMediaType;
import com.inso.framework.http.HttpSesstionManager;
import com.inso.framework.utils.ChecksumHelper;
import com.inso.framework.utils.StringUtils;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Map;

public class TestHttp {

    private static HttpSesstionManager httpSesstionManager = HttpSesstionManager.getInstance();


    public static void test1()
    {
        String str = "{\"msg\":\"success\",\"code\":200,\"data\":{\"real_price\":\"10000.00\",\"sign\":\"B3F9D80C6997D4A59BE77AE6ADC89949\",\"order_num\":\"2022092016482646949\",\"mch_id\":\"10000\",\"mch_order_num\":\"2209250115171660002\",\"sign_type\":\"MD5\",\"finish_time\":\"2022-09-20 17:29:58\"}}\n";
        String url = "http://127.0.0.1:8103/payment/callback/acepay/payin_webhook";
        httpSesstionManager.asyncPost(url, str, null);
    }


    public static void test2()
    {
        String appkeyid = "32916dfe74af4717b8820989e8c3ccb8";
        String salt = "c8449de46ce843f09df06ba7582767e3";

        String url = "https://www.topay.one/payment/payout";

        Map<String, Object> params = Maps.newHashMap();
        params.put("appkeyid", appkeyid);
        params.put("version", "V1");
        params.put("amount", "10.00");
        params.put("tradeNo", System.currentTimeMillis() + StringUtils.getEmpty());
        params.put("currency", "USDT");
        params.put("time", System.currentTimeMillis()  + StringUtils.getEmpty());
        params.put("name", "test");
        params.put("email", "sfdasdf@gmail.com");
        params.put("phone", "1243213214");
        params.put("bank_number", "00011020001773");
        params.put("bank_ifsc", "TRX (TronGrid)");
        params.put("accountType", "Coin");

        String sign = ChecksumHelper.encryptBySha256(params, salt);
        params.put("sign", sign);

        httpSesstionManager.asyncPost(url, params, HttpMediaType.FORM, new HttpCallback() {
            @Override
            public void onSuccess(Request request, Response response, byte[] data) {
                System.out.println(new String(data));
            }
        });
    }

    public static void test3()
    {

        String url = "https://www.topay.one/payment/callback/ftpay/payin_webhook";

        Map<String, Object> params = Maps.newHashMap();
        params.put("status", 1);
        params.put("realAmount", "200");
        params.put("orderNo", "10.00");


        httpSesstionManager.asyncPost(url, params, HttpMediaType.FORM, new HttpCallback() {
            @Override
            public void onSuccess(Request request, Response response, byte[] data) {
                System.out.println(new String(data));
            }
        });
    }

    public static void main(String[] args) throws IOException {
        test2();
        System.in.read();
    }

}
