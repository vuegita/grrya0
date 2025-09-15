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
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.MD5;
import okhttp3.Request;
import okhttp3.Response;

import java.util.Date;
import java.util.Map;

public class BukasmsImpl implements SmsService {

    private static Log LOG = LogFactory.getLog(BukasmsImpl.class);

    private static final String SUCCESS = "success";

    private String url = "https://api.onbuka.com/v3/sendSms";
    private static HttpSesstionManager mHTTP = HttpSesstionManager.getInstance();

    private String mUrl;

    private String companyName;

    private  String appId  = "ySnewEVD";

    private  String apikey  = "0WrfxAIb";
    private  String apiSecret  = "Yzx6KBzq";

    public BukasmsImpl()
    {
        StringBuilder urlBuffer = new StringBuilder();

        urlBuffer.append("https://api.onbuka.com/v3/sendSms?appId=ySnewEVD");
        urlBuffer.append("&datetime=202210252014");

        long datetime = new Date().getTime(); //DateUtils.convertString(new Date());
        String signStr =apikey+apiSecret+datetime;
        String sign = MD5.encode(signStr);

        urlBuffer.append("&sign="+sign);
//        urlBuffer.append("&mobile=919610335370");
//        urlBuffer.append("&content=test 123456");

        this.mUrl = urlBuffer.toString();

        MyConfiguration conf = MyConfiguration.getInstance();
        this.companyName = conf.getString("prod.env.title");
    }

    @Override
    public void send(String mobile, String content, Callback<Boolean> callback) {

        Map<String, Object> data = Maps.newHashMap();


        long datetime = new Date().getTime()/1000; //DateUtils.convertString(new Date());
        String signStr =apikey+apiSecret+datetime;
        String sign = MD5.encode(signStr);


        data.put("appId", appId);
        data.put("numbers",mobile);
        //String msg= "test "+content; //"[" + companyName + "] Dear sir, Your verification code is "+content+". GSB";
        String msg = "[" + companyName + "] "+ content+" is the OTP to link your account. Never share your OTP with anyone. ";
        data.put("content",msg);

        LOG.info(msg);

        Map<String, String> header = Maps.newHashMap();
        header.put("Sign", sign);
        header.put("Timestamp", ""+datetime);
        header.put("Api-Key", apikey);


        mHTTP.asyncPost(url,data , HttpMediaType.JSON,header,new HttpCallback() {
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

                    int code = jsonObject.getIntValue("status");
                    if(code == 0)
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

        long datetime = new Date().getTime()/1000; //DateUtils.convertString(new Date());
        String signStr =apikey+apiSecret+datetime;
        String sign = MD5.encode(signStr);

        data.put("appId", appId);
        data.put("numbers",mobile);

        Map<String, String> header = Maps.newHashMap();
        header.put("Sign", sign);
        header.put("Timestamp", ""+datetime);
        header.put("Api-Key", apikey);


        String msg= smsContent;

        if(companyNameStatus){
            msg=  "[" + companyName + "] "+smsContent;
        }else{
            msg=  smsContent;
        }

        data.put("content",msg);

        LOG.info(msg);

        mHTTP.asyncPost(url,data , HttpMediaType.JSON,header,new HttpCallback() {
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

                    int code = jsonObject.getIntValue("status");
                    if(code == 0)
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
