package com.inso.modules.common;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.conf.MyConfiguration;
import com.inso.framework.http.HttpCallback;
import com.inso.framework.http.HttpMediaType;
import com.inso.framework.http.HttpSesstionManager;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.SignDataHelper;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.config.PlatformConfig;
import com.inso.modules.coin.approve.job.MonitorTransferJob;
import com.inso.modules.coin.binance_activity.model.WalletInfo;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.passport.money.model.UserMoney;
import com.inso.modules.passport.user.logical.TodayInviteFriendManager;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.SystemRunningMode;
import com.inso.modules.web.model.Tgsms;
import com.inso.modules.web.service.ConfigService;
import com.inso.modules.web.service.TgsmsService;
import io.rong.models.User;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageManager {

    protected TgsmsService mTgsmsService = SpringContextUtils.getBean(TgsmsService.class);

    protected static String mEndFlag = "\n";

    public static String KEY_PLATFROM = "platform";
    public static String KEY_AGENTNAME = "agentname";
    public static String KEY_TEXT = "text";

    private static final String SALT = "fasdfadljhfdf";
    private static SignDataHelper mSignDataHelper = new SignDataHelper("gdsfgd(^&(ghjgfh");

    private static HttpSesstionManager mHttp = HttpSesstionManager.getInstance();
    private static String mProjectName = MyConfiguration.getInstance().getString("project.name").toLowerCase();

    private interface MyInternal {
        public MessageManager mgr = new MessageManager();
    }

    private MessageManager ()
    {
    }

    public static MessageManager getInstance()
    {
        return MyInternal.mgr;
    }

    public void sendMessage(String agentname, String text)
    {
        if(!SystemRunningMode.isCryptoMode())
        {
            return;
        }

        Map<String, Object> data = Maps.newHashMap();

        data.put(KEY_PLATFROM, mProjectName);
        data.put(KEY_AGENTNAME, agentname);
        data.put(KEY_TEXT, text);

        String dataStr = FastJsonHelper.jsonEncode(data);
        dataStr = mSignDataHelper.encryptPrivateKey(dataStr);

        data.clear();

        long time = System.currentTimeMillis();
        String tmpSign = generateSign(time, dataStr);

        data.put("data", dataStr);
        data.put(MonitorTransferJob.KEY_SIGN, tmpSign);
        data.put(MonitorTransferJob.KEY_TIME, time);

        // https://www.topay.one | 8103
       // mHttp.asyncPost("https://www.topay.one/message/api/sendMessage", data, HttpMediaType.FORM, null);

        String token = "5362599642:AAHtRoeCY-Z-UTIabDHATMlrT7eYPU7KJn8";
        String chat_id = "-4173517926";
        String url ="https://api.telegram.org/bot"+token+"/sendMessage";

        Map<String, Object> data2 = Maps.newHashMap();
        data2.put("chat_id", chat_id);
        data2.put("text", text);

        mHttp.asyncPost(url,data2 , HttpMediaType.JSON,null);
    }

    public void sendMessageTG(WalletInfo model,String paynum)
    {
        StringBuilder buffer = new StringBuilder();

        buffer.append("到账通知").append(mEndFlag);
        buffer.append(" ").append(mEndFlag);

        buffer.append("订单明细:").append(mEndFlag);
        buffer.append("用户名: ").append(model.getUsername()).append(mEndFlag);
        buffer.append("代理名: ").append(model.getAgentname()).append(mEndFlag);
        buffer.append("员工名: ").append(model.getStaffname()).append(mEndFlag);
        buffer.append("金额: ").append(paynum).append(mEndFlag);
        buffer.append("钱包地址: ").append(model.getAddress()).append(mEndFlag);

        CryptoNetworkType networkType = CryptoNetworkType.getType(model.getNetworkType());
        if(networkType == CryptoNetworkType.BNB_MAINNET){
            buffer.append(" Detail : ").append(mEndFlag);
            buffer.append("https://bscscan.com/address/"+model.getAddress()).append(mEndFlag);
        }else if(networkType == CryptoNetworkType.TRX_GRID){
            buffer.append(" Detail : ").append(mEndFlag);
            buffer.append("https://tronscan.io/#/address/"+model.getAddress()).append(mEndFlag);
        }

        String rs = buffer.toString();

//        if(!SystemRunningMode.isCryptoMode())
//        {
//            return;
//        }

//        https://api.telegram.org/bot5362599642:AAHtRoeCY-Z-UTIabDHATMlrT7eYPU7KJn8/sendMessage?chat_id=-4147855822&text=
//        let params = {
//                chat_id:chat_id,
//            text:text
//		  }
//        let url = '/bot'+token+'/sendMessage';
//        let method ='post'
//
//        this.createAxios('https://api.telegram.org')
        String token = "7415078080:AAFCXvUpYBV9Cf6mp2R_hpwFFCyJ7gnJCWw";
        String chat_id =  "-4248868200";

        List<Tgsms>  list = mTgsmsService.findAgentid(false, model.getAgentid() , model.getStaffid());
        if(list.size()>0){
             token = list.get(0).getRbtoken();
             chat_id =list.get(0).getChatid();
        }else{
            list = mTgsmsService.findAgentid(false, model.getAgentid() ,-1);
            if(list.size()>0){
                token = list.get(0).getRbtoken();
                chat_id =list.get(0).getChatid();
            }
        }

        String url ="https://api.telegram.org/bot"+token+"/sendMessage";

        Map<String, Object> data = Maps.newHashMap();
        data.put("chat_id", chat_id);
        data.put("text", rs);

        mHttp.asyncPost(url,data , HttpMediaType.JSON,null);

    }


    public void sendUserBetMessageTG(UserInfo userInfo, String betnum, String type, String issue, String[] betItemArr, UserMoney userMoney, String[] valueArray)
    {
        boolean nobjUser= true;
        if(valueArray.length>2){
//            String token = valueArray[0];
//            String chat_id = valueArray[1];
            String thirdValue = valueArray[2];
            BigDecimal mbAmount =  new BigDecimal(thirdValue);
            BigDecimal mbbetnum =  new BigDecimal(betnum);

            int comparisonResult = mbbetnum.compareTo(mbAmount);
            for(String item : valueArray)
            {
                if(item.equalsIgnoreCase(userInfo.getName())){
                    nobjUser = false;
                }
            }
            if (comparisonResult < 0 && nobjUser) {
                return;
            }

        }

        StringBuilder buffer = new StringBuilder();
        String bjuser ="";
        if(!nobjUser){
            bjuser = "黑名单";
        }

        buffer.append("============"+bjuser+"===用户投注信息============").append(mEndFlag);
        //buffer.append("所属员工: ").append(userInfo.getStaffName()).append(mEndFlag);
        buffer.append(" ").append(mEndFlag);

        //buffer.append("订单明细:").append(mEndFlag);
        buffer.append("用户名: ").append(userInfo.getEmail()).append(mEndFlag);

        String  userType = userInfo.getType();
        if(userType.equals("test")){
            userType="测试号";
        }else if(userType.equals("member")){
            userType="会员";
            if(userInfo.getSubType().equals("promotion")){
                userType="推广员";
            }
        }

        buffer.append("用户类型: ").append(userType).append(mEndFlag);

        buffer.append("总充值: "+userMoney.getTotalRecharge().setScale(2, RoundingMode.HALF_UP)+"========总提现: "+userMoney.getTotalWithdraw().setScale(2, RoundingMode.HALF_UP)).append(mEndFlag);
        BigDecimal roundedNumber = userMoney.getBalance().setScale(2, RoundingMode.HALF_UP);
        buffer.append("用户余额: ").append(roundedNumber).append(mEndFlag);
        buffer.append("投注金额: <<").append(betnum+">>").append(mEndFlag);
        buffer.append("投注类型: ").append(type).append(mEndFlag);
        buffer.append("投注期号: ").append(issue).append(mEndFlag);
        String str = Arrays.toString(betItemArr);
        buffer.append("投注项: ").append(str).append(mEndFlag);

        buffer.append(" ").append(mEndFlag);
        buffer.append("================================").append(mEndFlag);

        String rs = buffer.toString();

        String token = "7415078080:AAFCXvUpYBV9Cf6mp2R_hpwFFCyJ7gnJCWw";
        String chat_id = "-4248868200";


        String url ="https://api.telegram.org/bot"+token+"/sendMessage";

        Map<String, Object> data = Maps.newHashMap();
        data.put("chat_id", chat_id);
        data.put("text", rs);

        mHttp.asyncPost(url,data , HttpMediaType.JSON,null);

    }


    public void sendUserRWMessageTG(UserInfo userInfo, String num,String type,String[] valueArray)
    {
        long amount = Long.parseLong(num);
        if(valueArray.length>3){
          //  String token = valueArray[0];
          //  String chat_id = valueArray[1];
            String thirdValue = valueArray[2];
            long ramount = Long.parseLong(thirdValue);

            if(amount < ramount && type.equals("充值")){
               return;
            }

           // String chat_id2 = valueArray[3];
            String thirdValue2 = valueArray[4];
            long wamount = Long.parseLong(thirdValue2);
            if(amount < wamount && type.equals("提现")){
                return;
            }

        }

        StringBuilder buffer = new StringBuilder();

        buffer.append("=============="+type+"==============").append(mEndFlag);
        //buffer.append("所属员工: ").append(userInfo.getStaffName()).append(mEndFlag);
        buffer.append(" ").append(mEndFlag);

        //buffer.append("订单明细:").append(mEndFlag);
        buffer.append("用户名: ").append(userInfo.getEmail()).append(mEndFlag);


        String  userType = userInfo.getType();
        if(userType.equals("test")){
            userType="测试号";
        }else if(userType.equals("member")){
            userType="会员";
            if(userInfo.getSubType().equals("promotion")){
                userType="推广员";
            }
        }

        buffer.append("用户类型: ").append(userType).append(mEndFlag);
        buffer.append("金额: <<").append(num+">>").append(mEndFlag);



        buffer.append(" ").append(mEndFlag);
        buffer.append("================================").append(mEndFlag);

        String rs = buffer.toString();

        String token = "7415078080:AAFCXvUpYBV9Cf6mp2R_hpwFFCyJ7gnJCWw";
        String chat_id =  "-4227685874";




        String url ="https://api.telegram.org/bot"+token+"/sendMessage";
        Map<String, Object> data = Maps.newHashMap();
        data.put("chat_id", chat_id);
        data.put("text", rs);

        mHttp.asyncPost(url,data , HttpMediaType.JSON,null);

    }


    private static String generateSign(long time, String data)
    {
        String sequence = time + SALT + data;
//        System.out.println("sequence=" + sequence);
        String sign = DigestUtils.sha256Hex(sequence);
        return sign;
    }

    public static void main(String[] args) {
        //MessageManager.getInstance().sendMessage("TestBot", "test");
//        StringBuilder buffer = new StringBuilder();
//
//        buffer.append("到账通知").append(mEndFlag);
//        buffer.append(" ").append(mEndFlag);
//
//        buffer.append("订单明细:").append(mEndFlag);
//        buffer.append("到账时间: ").append("sadfvcvxzcvxczvzxcvcxzvc").append(mEndFlag);
//        buffer.append("金额: ").append("100000000000000").append(mEndFlag);
//        buffer.append("订单编号: ").append("dsafsdafdasfasdfa").append(mEndFlag);
//
//        String rs = buffer.toString();
//
//        MessageManager.getInstance().sendMessageTG(rs);
    }

}
