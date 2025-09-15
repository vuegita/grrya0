package com.inso.modules.paychannel.logical;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.utils.MD5;
import com.inso.modules.coin.binance_activity.model.WalletInfo;
import com.inso.modules.coin.binance_activity.service.WalletService;
import com.inso.modules.common.MessageManager;
import com.inso.modules.common.model.RemarkVO;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.UserErrorResult;
import com.inso.modules.passport.business.model.RechargePresentType;
import com.inso.modules.passport.money.UserPayManager;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.business.service.RechargeOrderService;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.paychannel.logical.payment.PaymentProcessorManager;
import com.inso.modules.paychannel.model.ChannelInfo;
import com.inso.modules.paychannel.model.ChannelType;
import com.inso.modules.paychannel.service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class PaymentManager {

    private static String DEFAULT_SALT = "fsadfasdfo874923";

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private RechargeOrderService mRechargeOrderService;

    @Autowired
    private WalletService mWalletService;

    @Autowired
    private ChannelService mPayChannelService;

    @Autowired
    private UserPayManager mUserPayMgr;



    public Map<String, Object> doRechargeAction(ChannelInfo channelInfo, UserInfo userInfo, BigDecimal amount, RechargePresentType externalPresentType, String externalPresentId)
    {
        UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());

        Date createtime = new Date();
        JSONObject paymentinfo = channelInfo.getSecretInfo();

        RemarkVO remarkVO = RemarkVO.create(null);
        remarkVO.put(RemarkVO.KEY_CHANNEL_ID, channelInfo.getId());
        remarkVO.put(RemarkVO.KEY_CHANNEL_NAME, channelInfo.getName());

        // 银行卡信息
        remarkVO.put(RemarkVO.KEY_BANK_NAME, paymentinfo.getString(RemarkVO.KEY_BANK_NAME));
        remarkVO.put(RemarkVO.KEY_BANK_CODE, paymentinfo.getString(RemarkVO.KEY_BANK_CODE));
        remarkVO.put(RemarkVO.KEY_BANK_ACCOUNT, paymentinfo.getString(RemarkVO.KEY_BANK_ACCOUNT));

        if(externalPresentType != null)
        {
            remarkVO.put("externalPresentType", externalPresentType.getKey());
            remarkVO.put("externalPresentId", externalPresentId);
        }

        String txnid = mRechargeOrderService.createOrder(userInfo, userAttr, channelInfo, channelInfo.getProduct(), amount, createtime, remarkVO);
        String productInfo = MD5.encode(createtime.toString() + DEFAULT_SALT + txnid);
        Map<String, Object> maps = PaymentProcessorManager.getIntance().encryptPayinRequest(channelInfo, txnid, amount, productInfo, userInfo.getEmail(), userInfo.getPhone());
        return maps;
    }

    public String doRechargeAction2(ChannelInfo channelInfo, UserInfo userInfo, BigDecimal amount, RechargePresentType externalPresentType, String externalPresentId)
    {
        UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());

        Date createtime = new Date();
        JSONObject paymentinfo = channelInfo.getSecretInfo();

        RemarkVO remarkVO = RemarkVO.create(null);
        remarkVO.put(RemarkVO.KEY_CHANNEL_ID, channelInfo.getId());
        remarkVO.put(RemarkVO.KEY_CHANNEL_NAME, channelInfo.getName());

        // 银行卡信息
        remarkVO.put(RemarkVO.KEY_BANK_NAME, paymentinfo.getString(RemarkVO.KEY_BANK_NAME));
        remarkVO.put(RemarkVO.KEY_BANK_CODE, paymentinfo.getString(RemarkVO.KEY_BANK_CODE));
        remarkVO.put(RemarkVO.KEY_BANK_ACCOUNT, paymentinfo.getString(RemarkVO.KEY_BANK_ACCOUNT));

        if(externalPresentType != null)
        {
            remarkVO.put("externalPresentType", externalPresentType.getKey());
            remarkVO.put("externalPresentId", externalPresentId);
        }

        String txnid = mRechargeOrderService.createOrder(userInfo, userAttr, channelInfo, channelInfo.getProduct(), amount, createtime, remarkVO);

        return txnid;
    }

    public void doUSDTRechargeAction(WalletInfo model,BigDecimal balance,BigDecimal addBlance){


            List<ChannelInfo> list = mPayChannelService.queryOnlineList(false, ChannelType.PAYIN, null, null);
            ChannelInfo channelInfo = list.get(0);
            if (channelInfo == null) {
                //doUSDTRechargeAction(model, balance, addBlance);
                return;
            }

            UserInfo userInfo = mUserService.findByUsername(false, model.getUsername());
        synchronized (model.getAddress()) {

            String tradeNo = doRechargeAction2(channelInfo, userInfo, addBlance, null, null);
            if (tradeNo == null || tradeNo.isEmpty()) {
                //doUSDTRechargeAction(model, balance, addBlance);
                return;
            }


            BigDecimal merchantMoney = addBlance;

            Date createtime = new Date();
            String outTradeNo = model.getAddress();//MD5.encode(createtime.toString() + DEFAULT_SALT + model.getAddress());

            ErrorResult result = mUserPayMgr.doRechargeSuccessAction(tradeNo, merchantMoney, outTradeNo, BigDecimal.ZERO, null, addBlance);

            Status status = Status.getType(model.getStatus());
            mWalletService.updateInfo(model.getAddress(), status, balance, model.getZbamount(), null, null);

            String paynum = addBlance + "";
            MessageManager.getInstance().sendMessageTG(model, paynum);
        }

    }

}
