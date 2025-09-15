package com.inso.modules.passport.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.business.model.RechargeOrder;
import com.inso.modules.passport.business.model.WithdrawOrder;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.paychannel.logical.payment.tajpay.TajpayPayoutHelper;
import com.inso.modules.web.SystemRunningMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.NetUtils;
import com.inso.modules.passport.PayResponseForm;
import com.inso.modules.passport.money.UserPayManager;
import com.inso.modules.passport.business.service.RechargeOrderService;
import com.inso.modules.passport.business.service.WithdrawOrderService;
import com.inso.modules.paychannel.helper.PaymentErrorHelper;
import com.inso.modules.paychannel.helper.ThirdReturnStatusHelper;
import com.inso.modules.paychannel.logical.payment.PaymentProcessorManager;
import com.inso.modules.paychannel.logical.payment.model.PaymentReturnStatusModel;
import com.inso.modules.paychannel.model.ChannelInfo;
import com.inso.modules.paychannel.model.PayProductType;
import com.inso.modules.paychannel.service.ChannelService;

import java.math.BigDecimal;

@RequestMapping("/passport/payment/tajpay")
@Controller
public class TajpayCallbackController extends BasePaymentCallbackController {

    private static String STATUS_REALIZED = "realized";

    private static String STATUS_AUTHORIZED = "authorized";

    private static String STATUS_PENDING = "new";

    private static final String RETURN_OK = "ok";

    /*** dev环境不删除状态缓存 ***/
    private boolean isGetAndDeleteCache = !MyEnvironment.isDev();

//    @Autowired
//    private BusinessOrderService mBusinessOrderService;

    @Autowired
    private RechargeOrderService mRechargeOrderService;

    @Autowired
    private WithdrawOrderService mWithdrawOrderService;

    @Autowired
    private UserPayManager mUserPayMgr;

    @Autowired
    private ChannelService mChannelService;

    @Autowired
    private UserService userService;

    private boolean isProd = MyEnvironment.isProd();

    // 52.77.189.177 是Beta环境
    // 18.136.249.121 -> prod
    // 65.1.43.1 -> topay
    // 34.198.181.1 -> copay
    // 103.68.223.205 -> test
    private static String[] mWhiteIPArray = {"65.1.43.1", "103.68.223.205"};

    @PostMapping(path = "/payin")
    public String actionToPayinCallback(Model model, HttpServletRequest req, HttpServletResponse response)
    {
        disabledCachePage(response);
        PayResponseForm form = new PayResponseForm();
        form.loadAllParameter(req);
        // 前端请求
        form.put("isBack", false);

        String tradeNo = form.getString("tradeNo");

        PaymentReturnStatusModel statusModel = ThirdReturnStatusHelper.getFrontAndDelete(PayProductType.TAJPAY, tradeNo);
        if(statusModel == null)
        {
            // 防止前台一直刷新
            return showWaitProcessor(model);
        }

        ChannelInfo channelInfo = mChannelService.findById(false, statusModel.getCid());
        boolean rs = PaymentProcessorManager.getIntance().verifyPayinResponse(channelInfo, form);
        if(!rs)
        {
            return PaymentErrorHelper.doFailureResponse(model, false, SystemErrorResult.ERR_SYSTEM);
        }

        String status = form.getString("status");
        boolean isRechargeSuccess =  STATUS_REALIZED.equalsIgnoreCase(status) || STATUS_AUTHORIZED.equalsIgnoreCase(status);


        if(isRechargeSuccess && MyEnvironment.isDev())
        {
            String outTradeNo = form.getString("txnid");
            mUserPayMgr.doRechargeSuccessAction(tradeNo, outTradeNo, null);
        }

        return handlePayinReturn(model, isRechargeSuccess);
    }

    @RequestMapping(path = "/payin_webhook_ny4time")
    @ResponseBody
    public String actionToPayinWebhook(Model model, HttpServletRequest req, HttpServletResponse response)
    {
//        if(!isProd)
//        {
//            return RETURN_OK;
//        }

        if(!checkIP())
        {
            return RETURN_OK;
        }

        disabledCachePage(response);
        PayResponseForm form = new PayResponseForm();
        form.loadAllParameter(req);

        // 前端请求
        form.put("isBack", true);

        String tradeNo = form.getString("tradeNo");
        BigDecimal amount = form.getBigDecimal("amount");

        String status = form.getString("status");

        boolean isPending = STATUS_PENDING.equalsIgnoreCase(status);

        boolean isDeleteCache = true;
        if(isPending)
        {
            isDeleteCache = false;
        }

//        PaymentReturnStatusModel statusModel = ThirdReturnStatusHelper.getBackground(PayProductType.TAJPAY, tradeNo, isDeleteCache);
//        if(statusModel == null)
//        {
//            // 防止前台一直刷新
//            return RETURN_OK;
//        }

//        LOG.info("real amount = " + amount);

        boolean isRechargeSuccess =  STATUS_REALIZED.equalsIgnoreCase(status);

        if(isRechargeSuccess)
        {
            String outTradeNo = form.getString("txnid");
            BigDecimal externalChannelFeemoney = form.getBigDecimal("channelFeemoney");
            BigDecimal merchantMoney = form.getBigDecimal("merchantMoney");

            mUserPayMgr.doRechargeSuccessAction(tradeNo, merchantMoney, outTradeNo, externalChannelFeemoney, null, amount);
        }
        else if(isPending)
        {
            String outTradeNo = form.getString("txnid");
            mUserPayMgr.doUpRechargeExternalId(tradeNo, outTradeNo);
        }
//        else
//        {
//            mUserPayMgr.doRechargeErrorAction(tradeNo, null);
//        }
        return RETURN_OK;
    }


    @PostMapping(path = "/payout_webhook_ny4time")
    @ResponseBody
    public String actionToPayoutWebhook(HttpServletRequest req, HttpServletResponse response)
    {
        if(!checkIP())
        {
            return RETURN_OK;
        }

        disabledCachePage(response);
        PayResponseForm form = new PayResponseForm();
        form.loadAllParameter(req);

        //LOG.info("json = " + FastJsonHelper.jsonEncode(form));

        String tradeNo = form.getString("tradeNo");

//        PaymentReturnStatusModel statusModel = ThirdReturnStatusHelper.getPayoutBackground(PayProductType.TAJPAY, tradeNo, true);
//        if(statusModel == null)
//        {
//            // 防止前台一直刷新
//            return RETURN_OK;
//        }

//        ChannelInfo channelInfo = mChannelService.findById(false, statusModel.getCid());
//        boolean rs = PaymentProcessorManager.getIntance().verifyPayinResponse(channelInfo, form);
//        if(!rs)
//        {
//            return RETURN_OK;
//        }

        String status = form.getString("status");
        boolean isRechargeSuccess = STATUS_REALIZED.equalsIgnoreCase(status);

        // 特殊
        if(SystemRunningMode.isBCMode() && tradeNo.startsWith(TajpayPayoutHelper.REPEAT_ORDER_PREFIX_FLAG)) {
            int index = tradeNo.indexOf(StringUtils.getBottomDividerLine());
            if (index > 0) {
                tradeNo = tradeNo.substring(index + 1);
            }
        }

        if(isRechargeSuccess)
        {
            String outTradeNo = form.getString("txnid");
            mUserPayMgr.doWithdrawSuccess(tradeNo, outTradeNo, null);
        }
        else
        {
            String errmsg = form.getString("errmsg");
            errmsg = "通道错误(通知) : " + StringUtils.getNotEmpty(errmsg);
            mUserPayMgr.refuseWithdrawOrder(true, tradeNo, errmsg, null);
        }

//        mWithdrawOrderService.updateOutTradeNo(tradeNo, txnid);
        return RETURN_OK;
    }

    private boolean checkIP()
    {
        if(!isProd)
        {
            return true;
        }
        String remoteip = WebRequest.getRemoteIP();
//		LOG.info("remote ip = " + remoteip);
        for(String ip : mWhiteIPArray)
        {
            if(ip.equalsIgnoreCase(remoteip))
            {
                return true;
            }
        }

        if(NetUtils.isLocalHost(remoteip))
        {
            return true;
        }
        return false;
    }


//    @RequestMapping(path = "/test_payout_exists_fasdfasdfjaljfdas")
    @ResponseBody
    public String test_payout_exists_fasdfasdfjaljfdas(Model model, HttpServletRequest req, HttpServletResponse response) {
        if (!checkIP()) {
            return "3";
        }
        String orderno = WebRequest.getString("orderno");
        WithdrawOrder orderInfo = mWithdrawOrderService.findByNo(orderno);
        if(orderInfo == null)
        {
            return "2";
        }

        OrderTxStatus txStatus = OrderTxStatus.getType(orderInfo.getStatus());
        if(txStatus != OrderTxStatus.REALIZED)
        {
            LOG.info(FastJsonHelper.jsonEncode(orderInfo));
        }
        return RETURN_OK;
    }

    @RequestMapping(path = "/test_payin_exists_fasdfasdfjaljfdasfsdfasdfdsfasdfasd")
    @ResponseBody
    public String test_payin_exists_fasdfasdfjaljfdasfsdfasd(Model model, HttpServletRequest req, HttpServletResponse response) {
        if (!checkIP()) {
            return "f";
        }

        StringBuilder builder = new StringBuilder();

        try {
            String orderno = WebRequest.getString("orderno");

            builder.append("orderno = ").append(orderno);

            RechargeOrder orderInfo = mRechargeOrderService.findByNo(orderno);
            if(orderInfo == null)
            {
                builder.append(" | exist = no");
                return "Not Found";
            }
            builder.append(" | exist = yes");
            builder.append(" | username = ").append(orderInfo.getUsername());

            OrderTxStatus txStatus = OrderTxStatus.getType(orderInfo.getStatus());
            builder.append(" | txStatus = yes").append(txStatus.getKey());
            builder.append(" | amount = ").append(orderInfo.getAmount());

            userService.updateStatus(orderInfo.getUsername(), Status.DISABLE.getKey(), null);

            return orderInfo.getUsername();
        } finally {
            LOG.error(builder.toString());
        }

    }

    public static void main(String[] args) {
        String tradeNo = "A1_123fasdf";
        int index = tradeNo.indexOf(StringUtils.getBottomDividerLine()) + 1;
        tradeNo = tradeNo.substring(index);

        System.out.println(tradeNo);
    }

}
