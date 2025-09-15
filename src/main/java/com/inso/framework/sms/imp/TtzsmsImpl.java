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
import java.util.TimeZone;

public class TtzsmsImpl implements SmsService {

    private static Log LOG = LogFactory.getLog(TtzsmsImpl.class);

    private static final String SUCCESS = "success";

    private String url = "https://api.230sms.com/outauth/verifCodeSend";
    private static HttpSesstionManager mHTTP = HttpSesstionManager.getInstance();

    private String mUrl;

    private String companyName;

    private String apikey = "N3c8ePSHjdfoxK714l24Kg==";
    private String apisecret = "6b74d54ab74740e38f82a3a5d902d3a3";

    public TtzsmsImpl()
    {
        StringBuilder urlBuffer = new StringBuilder();

        urlBuffer.append("https://api.230sms.com/outauth/verifCodeSend?app_id=dpw4qMk6L9bhr72KQc");
        urlBuffer.append("&datetime=202210252014");


        String datetime =  DateUtils.convertString(new Date());
        String signStr =apikey+datetime+apisecret;
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

        TimeZone timeZone = TimeZone.getTimeZone("Asia/Shanghai"); // 设置时区为北京时间
        Date date = new Date();
        long time = date.getTime() + timeZone.getRawOffset();
        Date localTime = new Date(time);


        String datetime =  DateUtils.convertString(localTime);
        String signStr =apikey+datetime+apisecret;
        String sign = MD5.encode(signStr);

        data.put("timestamp", datetime);
        data.put("sign", sign);
        data.put("mobile",""+mobile);
        data.put("apikey",apikey);
        String msg= "[" + companyName + "] Dear sir, Your verification code is "+content+".";  //"test 123456"; //

        String str = mobile.substring(0, 2);
        if(str.equals("91")){
            data.put("senderid","HiKrit");
            msg = "[HiKredit] The OTP is "+ content +". It is only used to authorize the signing of the agreement.";
        }else{
            data.put("senderid","");
        }

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

        TimeZone timeZone = TimeZone.getTimeZone("Asia/Shanghai"); // 设置时区为北京时间
        Date date = new Date();
        long time = date.getTime() + timeZone.getRawOffset();
        Date localTime = new Date(time);

        String datetime =  DateUtils.convertString(localTime);
        String signStr =apikey+datetime+apisecret;
        String sign = MD5.encode(signStr);

        data.put("timestamp", datetime);
        data.put("sign", sign);
        data.put("mobile",""+mobile);
        data.put("apikey",apikey);


        String msg= smsContent;

        if(companyNameStatus){
            msg=  "[" + companyName + "] "+smsContent;
        }else{
            msg=  smsContent;
        }
        String str = mobile.substring(0, 2);
        if(str.equals("91")){
            data.put("senderid","HiKrit");
            msg = "[HiKredit] The OTP is "+ content +". It is only used to authorize the signing of the agreement.";
        }else{
            data.put("senderid","");
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
