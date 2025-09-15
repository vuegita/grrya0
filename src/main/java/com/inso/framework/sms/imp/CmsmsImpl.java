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
import com.inso.framework.sms.SmsClient;
import com.inso.framework.sms.SmsService;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.MD5;
import okhttp3.Request;
import okhttp3.Response;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

public class CmsmsImpl implements SmsService {

    private static Log LOG = LogFactory.getLog(CmsmsImpl.class);

    private static final String SUCCESS = "success";

    private String url = "https://api.cmessages.com/sms/send_verify";
    private static HttpSesstionManager mHTTP = HttpSesstionManager.getInstance();

    private String mUrl;

    private String companyName;

    public CmsmsImpl()
    {
        StringBuilder urlBuffer = new StringBuilder();

        urlBuffer.append("https://api.cmessages.com/sms/send_verify?app_id=dpw4qMk6L9bhr72KQc");
        urlBuffer.append("&datetime=202210252014");

        String appId = "dpw4qMk6L9bhr72KQc";

        String datetime =  DateUtils.convertString(new Date());
        String appKey = "B7U26X9EN36tR348mnqhafcQbvdDMk2F";
        String signStr =appId+datetime+appKey;

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
        String appId = "dpw4qMk6L9bhr72KQc";
        String datetime =  DateUtils.convertString(new Date());
        String appKey = "B7U26X9EN36tR348mnqhafcQbvdDMk2F";
        String signStr = appId+datetime+appKey;
        String sign = MD5.encode(signStr);


        data.put("app_id", appId);
        data.put("datetime", datetime);
        data.put("sign", sign);
       // data.put("type","text");
        data.put("mobile","91"+mobile);
       // data.put("senderid","GOOBU");


        String msg= "test 123456"; //"[" + companyName + "] Dear sir, Your verification code is "+content+". GSB";
        data.put("content",msg);

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
        String appId = "dpw4qMk6L9bhr72KQc";
        String datetime =  DateUtils.convertString(new Date());
        String appKey = "B7U26X9EN36tR348mnqhafcQbvdDMk2F";
        String signStr =appId+datetime+appKey;
        String sign = MD5.encode(signStr);


        data.put("app_id", appId);
        data.put("datetime", datetime);
        data.put("sign", sign);
        // data.put("type","text");
        data.put("mobile","91"+mobile);
        // data.put("senderid","GOOBU");


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
