package com.inso.framework.sms.imp;

import java.util.List;
import java.util.Map;

import org.assertj.core.util.Lists;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.inso.framework.conf.MyConfiguration;
import com.inso.framework.http.HttpCallback;
import com.inso.framework.http.HttpMediaType;
import com.inso.framework.http.HttpSesstionManager;
import com.inso.framework.service.Callback;
import com.inso.framework.sms.SmsService;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;

import okhttp3.Request;
import okhttp3.Response;

public class LabsmsImpl implements SmsService {

    private static HttpSesstionManager mHTTP = HttpSesstionManager.getInstance();

    private String url = "https://api.labsmobile.com/json/send";

    private Map<String, String> basicAuthHeader;

    private String companyName;

    public LabsmsImpl()
    {
        MyConfiguration conf = MyConfiguration.getInstance();
        String username = conf.getString("sms.labsmobile.username");
        String password = conf.getString("sms.labsmobile.password");
        this.basicAuthHeader = HttpSesstionManager.buildBasicAuthorization(username, password);
        this.companyName = conf.getString("prod.env.title");
    }

    @Override
    public void send(String mobile, String content, Callback<Boolean> callback) {

        String msg=  "[" + companyName + "]El código de verificación por SMS es "+content+", válido por 5 minutos, no se lo diga a nadie.";

        Map<String, Object> maps = Maps.newHashMap();

        maps.put("message", msg);
        maps.put("tpoa", "Sender");

        List list = Lists.newArrayList();
        Map<String, Object> recipient = Maps.newHashMap();
        recipient.put("msisdn", mobile);
        list.add(recipient);

        maps.put("recipient", list);

        mHTTP.asyncPost(url, maps, HttpMediaType.JSON, basicAuthHeader, new HttpCallback() {
            @Override
            public void onSuccess(Request request, Response response, byte[] data) {
                String rs = new String(data);

                if(StringUtils.isEmpty(rs))
                {
                    callback.execute(false);
                    return;
                }
              //  System.out.println(rs);
                JSONObject jsonObject = FastJsonHelper.toJSONObject(rs);
                if(jsonObject == null || jsonObject.isEmpty())
                {
                    callback.execute(false);
                    return;
                }

                int code = jsonObject.getIntValue("code");
                if(code == 0)
                {
                    callback.execute(true);
                    return;
                }

                callback.execute(false);
            }

            public void onFailure(Throwable e)
            {
                callback.execute(false);
            }
        });


//        HttpResponse<String> response = Unirest.post("https://api.labsmobile.com/json/send")
//                .header("Content-Type", "application/json")
//                .basicAuth("admin@alhayajoyeria.com","mypassword")
//                .header("Cache-Control", "no-cache")
//                .body("{\"message\":\"Text of the SMS message\", \"tpoa\":\"Sender\",\"recipient\":[{\"msisdn\":\"5212221234567\"}]}")
//                .asString();
    }

    @Override
    public void send(String mobile, String content, String senderid, boolean companyNameStatus, String msg, Callback<Boolean> callback) {

    }


    @Override
    public void sendMore(String mobiles, String content, Callback<Boolean> callback) {

    }
}
