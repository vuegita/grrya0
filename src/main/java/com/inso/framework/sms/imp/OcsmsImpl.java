package com.inso.framework.sms.imp;

import java.util.Map;

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

import com.inso.modules.admin.config.PlatformConfig;
import com.inso.modules.web.service.ConfigService;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class OcsmsImpl implements SmsService {

    private static Log LOG = LogFactory.getLog(OcsmsImpl.class);

    private static final String SUCCESS = "success";

    // "https://app.ocsms.co/smsapi/index?key=55FD5A5BFCEC64&campaign=1&routeid=14&type=text&contacts=97656XXXXX,98012XXXXX&senderid=DEMO&msg=Hello+People%2C+have+a+great+day&time=2021-05-29+07%3A13";
    //https://ousenyun.com/smsapi/index?key=35EB2353654686&campaign=0&routeid=14&type=text&contacts=97656XXXXX,98012XXXXX&senderid=DEMO&msg=Hello+People%2C+have+a+great+day

   // private String url = "https://app.ocsms.co/smsapi/index";

    private String url = "https://ousenyun.com/smsapi/index";
    private static HttpSesstionManager mHTTP = HttpSesstionManager.getInstance();

    private String mUrl;

    private String companyName;

    public OcsmsImpl()
    {
        StringBuilder urlBuffer = new StringBuilder();
       // urlBuffer.append("https://app.ocsms.co/smsapi/index?key=55FD5A5BFCEC64&campaign=1");

        urlBuffer.append("https://ousenyun.com/smsapi/index?key=36188EDA664A80&campaign=0");
//        urlBuffer.append("&routeid=14");
        urlBuffer.append("&type=text");
//        urlBuffer.append("&contacts=");
        urlBuffer.append("&senderid=DEMO");
//        urlBuffer.append("&msg=");

        this.mUrl = urlBuffer.toString();

        MyConfiguration conf = MyConfiguration.getInstance();
        this.companyName = conf.getString("prod.env.title");
    }

    @Override
    public void send(String mobile, String content, Callback<Boolean> callback) {

        Map<String, Object> data = Maps.newHashMap();
       // data.put("key", "55FD5A5BFCEC64");
        data.put("key", "36188EDA664A80");
        //data.put("campaign", 8);
        data.put("campaign", 0);
        //data.put("routeid", 1);
        data.put("routeid", 12);
        data.put("type","text");
        data.put("contacts","91"+mobile);
       // data.put("senderid","BBROVE");LAZALT BUPRTI GOOBU
        data.put("senderid","GOOBU");

        //String msg=  "[" + companyName + "]your SMS verification code is "+content+", valid for 5 minutes, OTP is confidential, do not disclose to anyone. -BBI";
        //String msg=  "[" + companyName + "] Your verification code is: "+content+", please do not share it with others. PCP";
        //String msg=  "Your OTP: "+content+", please do not tell anyone else. INDLAZA";
        String msg=  "[" + companyName + "] Dear sir, Your verification code is "+content+". GSB";
        data.put("msg",msg);

        LOG.info(msg);
//        System.out.println(msg);
//        boolean rs = false;
//        if(!rs)
//        {
//            return;
//        }

        mHTTP.asyncPost(url,data , HttpMediaType.FORM,new HttpCallback() {
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

                    String status = jsonObject.getString("result");

                    callback.execute(SUCCESS.equalsIgnoreCase(status));
                } catch (Exception e) {
                    callback.execute(false);
                }
            }
        });

//        String reqUrl = mUrl + "&contacts=" + mobile + "&msg=[Topclub]SMS verification code is "+content+", valid for 5 minutes, please don't tell others.";
//        System.out.println(reqUrl);
//        mHTTP.asyncGet(reqUrl, new HttpCallback() {
//            public void onSuccess(Request request, Response response, byte[] data) {
//                try {
//                    if(data == null)
//                    {
//                        return;
//                    }
//
//                    String rs = new String(data);
//                    JSONObject jsonObject = FastJsonHelper.toJSONObject(rs);
//                    if(jsonObject == null || jsonObject.isEmpty())
//                    {
//                        return;
//                    }
//
//                    String status = jsonObject.getString("result");
//
//                    callback.execute(SUCCESS.equalsIgnoreCase(status));
//                } catch (Exception e) {
//                    callback.execute(false);
//                }
//            }
//        });
    }

    @Override
    public void send(String mobile, String content, String senderid, boolean companyNameStatus, String smsContent,Callback<Boolean> callback) {
        Map<String, Object> data = Maps.newHashMap();
        data.put("key", "36188EDA664A80");
        data.put("campaign", 0);
        data.put("routeid", 12);
        data.put("type","text");
        data.put("contacts","91"+mobile);
        data.put("senderid",senderid);
        String msg= smsContent;

        if(companyNameStatus){
            msg=  "[" + companyName + "] "+smsContent;
        }else{
            msg=  smsContent;
        }

        data.put("msg",msg);

        LOG.info(msg);

        mHTTP.asyncPost(url,data , HttpMediaType.FORM,new HttpCallback() {
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

                    String status = jsonObject.getString("result");

                    callback.execute(SUCCESS.equalsIgnoreCase(status));
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
