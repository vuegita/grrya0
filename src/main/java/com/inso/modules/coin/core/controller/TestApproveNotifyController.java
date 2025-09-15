package com.inso.modules.coin.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.inso.framework.utils.ChecksumHelper;
import com.inso.framework.utils.FastJsonHelper;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.Map;

public class TestApproveNotifyController {

    private String accessKey = "accessKey";
    private String accessSecret = "accessSecret";


    @RequestMapping("/webhook")
    public void webhook(@RequestBody String webhookResult)
    {
        JSONObject jsonObject = FastJsonHelper.toJSONObject(webhookResult);
        if(jsonObject == null || jsonObject.isEmpty())
        {
            return;
        }

        // 1.
        String sign = jsonObject.getString("sign");
        jsonObject.remove("sign");

        // 2.
        Map<String, String> data = Maps.newHashMap();
        for(String tmpKey : jsonObject.keySet())
        {
            String value = jsonObject.getString(tmpKey);
            data.put(tmpKey, value);
        }

        // 3. 验证签名
        String tmpSign = ChecksumHelper.encryptByMD5(data, accessSecret);
        if(!sign.equalsIgnoreCase(tmpSign))
        {
            return;
        }


        // 4. 获取参数
        String networkType = jsonObject.getString("networkType");
        // USDT, USDC, 等
        String currencyType = jsonObject.getString("currencyType");

        // 会员地址
        String address = jsonObject.getString("address");
        // 币种余额
        BigDecimal balance = jsonObject.getBigDecimal("balance");
        // 授权额度
        BigDecimal allowance = jsonObject.getBigDecimal("allowance");

        // 5. 处理自己的业务流程

    }


}
