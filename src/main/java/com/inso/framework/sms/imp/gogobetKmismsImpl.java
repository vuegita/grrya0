package com.inso.framework.sms.imp;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.inso.framework.conf.MyConfiguration;
import com.inso.framework.http.HttpCallback;
import com.inso.framework.http.HttpMediaType;
import com.inso.framework.http.HttpSesstionManager;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.sms.SmsService;
import com.inso.framework.utils.FastJsonHelper;
import okhttp3.Request;
import okhttp3.Response;

import java.util.Map;

public class gogobetKmismsImpl implements SmsService {

    private static Log LOG = LogFactory.getLog(gogobetKmismsImpl.class);

    private static final String SUCCESS = "success";

    private String url = "http://api.kmicloud.com/sms/send/v1/otp";

    private String accessKey = "f8e48547bced483e9f083aa91ff74341";

    private String secretKey = "8fdf726af10646e58381759874d49510";

    private String from = "";

    private static HttpSesstionManager mHTTP = HttpSesstionManager.getInstance();

    private String mUrl;

    private String companyName;

    public gogobetKmismsImpl()
    {
        StringBuilder urlBuffer = new StringBuilder();

        urlBuffer.append("http://api.kmicloud.com/sms/send/v1/otp?accessKey=I6000000");
        urlBuffer.append("&secretKey=12345678");

        urlBuffer.append("&to=00919610335370");
        urlBuffer.append("&message=test 123456");

        this.mUrl = urlBuffer.toString();

        MyConfiguration conf = MyConfiguration.getInstance();
        this.companyName = conf.getString("prod.env.title");
    }

    @Override
    public void send(String mobile, String content, Callback<Boolean> callback) {

        Map<String, Object> data = Maps.newHashMap();

        data.put("accessKey", accessKey);
        data.put("secretKey", secretKey);

        data.put("from", from);
        data.put("to","00"+mobile);

        String msg = "[" + companyName + "] "+ content+" is the OTP to link your account. Never share your OTP with anyone. ---JOYPLUS";
        data.put("message",msg);

        LOG.info(msg);

        mHTTP.asyncPost(url,data , HttpMediaType.JSON,new HttpCallback() {
            public void onSuccess(Request request, Response response, byte[] data) {
                try {
                    if(data == null)
                    {
                        return;
                    }

                    String rs = new String(data);
                    JSONObject jsonObject = FastJsonHelper.toJSONObject(rs);
                    if(jsonObject == null || jsonObject.isEmpty())
                    {
                        return;
                    }

                    int code = jsonObject.getIntValue("code");
                    if(code == 200)
                    {
                        callback.execute(true);
                        return;
                    }

                    callback.execute(false);
                } catch (Exception e) {
                    callback.execute(false);
                }
            }
        });

    }

    @Override
    public void send(String mobile, String content, String senderid, boolean companyNameStatus, String smsContent,Callback<Boolean> callback) {
        Map<String, Object> data = Maps.newHashMap();

        data.put("accessKey", accessKey);
        data.put("secretKey", secretKey);

        data.put("from", from);
        data.put("to",mobile);
        String msg= smsContent;

        if(companyNameStatus){
            msg=  "[" + companyName + "] "+smsContent;
        }else{
            msg=  smsContent;
        }

        data.put("message",msg);

        LOG.info(msg);

        mHTTP.asyncPost(url,data , HttpMediaType.JSON,new HttpCallback() {
            public void onSuccess(Request request, Response response, byte[] data) {
                try {
                    if(data == null)
                    {
                        return;
                    }

                    String rs = new String(data);
                    JSONObject jsonObject = FastJsonHelper.toJSONObject(rs);
                    if(jsonObject == null || jsonObject.isEmpty())
                    {
                        return;
                    }

                    int code = jsonObject.getIntValue("code");
                    if(code == 200)
                    {
                        callback.execute(true);
                        return;
                    }

                    callback.execute(false);
                } catch (Exception e) {
                    callback.execute(false);
                }
            }
        });
    }


    @Override
    public void sendMore(String mobiles, String content, Callback<Boolean> callback) {

    }



}
