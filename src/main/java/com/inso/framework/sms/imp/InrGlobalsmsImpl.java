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

public class InrGlobalsmsImpl implements SmsService {

    private static Log LOG = LogFactory.getLog(InrGlobalsmsImpl.class);

    private static final String SUCCESS = "success";

    private String url = "https://api.nodesms.com/send/json";

    private String account = "I003636";

    private String password = "Ep6gHf1iNM0i";

    private static HttpSesstionManager mHTTP = HttpSesstionManager.getInstance();

    private String mUrl;

    private String companyName;

    public InrGlobalsmsImpl()
    {
        StringBuilder urlBuffer = new StringBuilder();

        urlBuffer.append("https://api.nodesms.com/send/json?account=I6000000");
        urlBuffer.append("&password=12345678");

        urlBuffer.append("&mobile=919610335370");
        urlBuffer.append("&msg=test 123456");

        this.mUrl = urlBuffer.toString();

        MyConfiguration conf = MyConfiguration.getInstance();
        this.companyName = conf.getString("prod.env.title");
    }

    @Override
    public void send(String mobile, String content, Callback<Boolean> callback) {

        Map<String, Object> data = Maps.newHashMap();


        data.put("account", account);
        data.put("password", password);

        data.put("mobile","91"+mobile);
       // data.put("senderid","GOOBU");


        String msg= "[" + companyName + "] Dear sir, Your verification code is "+content+". GSB";//"test 123456"; //
        data.put("msg",msg);

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

        data.put("account", account);
        data.put("password", password);
        data.put("mobile","91"+mobile);

        String msg= smsContent;

        if(companyNameStatus){
            msg=  "[" + companyName + "] "+smsContent;
        }else{
            msg=  smsContent;
        }

        data.put("msg",msg);

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
