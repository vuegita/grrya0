package com.inso.modules.passport.user;

import com.google.common.collect.Maps;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.passport.user.logical.AppNotifyManager;

import java.io.IOException;
import java.util.Map;

public class Test {

    public static void testApproveNotify()
    {

        AppNotifyManager mgr = new AppNotifyManager();
        String accessKey = "88dd155fd9654ffa948e0affbc3e1bd7";
        String salt = "b384753ac4c640d5a03cc197ee2960d3";


        String webhookUrl = "http://127.0.0.1:8283/coin/defiMiningApi/testNotify";


        long time = System.currentTimeMillis();

        Map<String, String> data = Maps.newHashMap();


        data.put("version", "1");
        data.put("accessKey", accessKey);
        data.put("time", time + StringUtils.getEmpty());

        data.put("networkType", CryptoNetworkType.TRX_GRID.getKey());
        data.put("currencyType", CryptoCurrency.USDT.getKey());

        data.put("address", "TNA3iB1qwJYrEn5HTZkb6GFhmGu9yMeTgv");

        data.put("balance", "0"); // 币种余额， 变动的
        data.put("allowance", "9999999999999999999999999999"); // 授权额度 - 变动的

        mgr.sendApproveNotify(salt, webhookUrl, data, new Callback<Boolean>() {
            @Override
            public void execute(Boolean o) {
                System.out.println("notify result = " + o);
            }
        });
    }

    public static void main(String[] args) throws IOException {

        testApproveNotify();

        System.in.read();
    }

}
