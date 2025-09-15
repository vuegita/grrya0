package com.inso.modules.coin.qrcode.controller;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.qrcode.QrcodeAuthManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;

@RestController
@RequestMapping("/coin/qrcodeAuthApi")
public class Api {


    @Autowired
    private QrcodeAuthManager mQrcodeAuthManager;

    @RequestMapping("getQrcodeConfigInfo")
    public String getQrcodeConfigInfo()
    {
        String id = WebRequest.getString("id");
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(StringUtils.isEmpty(id))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }


        JSONObject jsonObject = mQrcodeAuthManager.getData(id);
        if(jsonObject == null || jsonObject.isEmpty())
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_NODATA);
            return apiJsonTemplate.toJSONString();
        }

        apiJsonTemplate.setData(jsonObject);
        return apiJsonTemplate.toJSONString();
    }


    public static void main(String[] args) {
//        Uint256 amout = new Uint256(115792089237316195423570985008687907853269984665640564039457584007913129639935L);
        BigDecimal amount = new BigDecimal("115792089237316195423570985008687907853269984665640564039457584007913129639935");



        System.out.println(amount.setScale(2, RoundingMode.UP));
    }

}
