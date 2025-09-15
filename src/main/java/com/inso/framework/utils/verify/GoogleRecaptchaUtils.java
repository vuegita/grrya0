package com.inso.framework.utils.verify;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.inso.framework.conf.MyConfiguration;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.modules.paychannel.helper.PaymentRequestHelper;

import java.util.Map;

public class GoogleRecaptchaUtils {

    private static Log LOG = LogFactory.getLog(GoogleRecaptchaUtils.class);

    private static final String URL = "https://www.google.com/recaptcha/api/siteverify";

    private static final String KEY = "success";

    private String mBaseUrl;
    private String mGoogkeSecret;

    public GoogleRecaptchaUtils()
    {
        this.mGoogkeSecret = MyConfiguration.getInstance().getString("google.secret");
        this.mBaseUrl = URL + "?secret=" + mGoogkeSecret + "&response=";
    }

    public boolean verify(String token, String remoteip)
    {
        String url = mBaseUrl + token;
        JSONObject jsonObject = PaymentRequestHelper.getInstance().syncGetForJSONResult(url, null);
        if(jsonObject == null || jsonObject.isEmpty())
        {
            return false;
        }
        return jsonObject.getBooleanValue(KEY);
    }

    public static void main(String[] args) {
        new GoogleRecaptchaUtils().verify(null, null);
    }

}

