package com.inso.modules.paychannel.controller;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RegexUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.controller.BasePaymentCallbackController;
import com.inso.modules.passport.business.service.RechargeOrderService;
import com.inso.modules.paychannel.helper.ThirdReturnStatusHelper;
import com.inso.modules.paychannel.logical.payment.model.PaymentReturnStatusModel;
import com.inso.modules.paychannel.model.ChannelInfo;
import com.inso.modules.paychannel.model.PayProductType;
import com.inso.modules.paychannel.service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 印度UPI
 */
@RequestMapping("/pay/payment/wallet/upi")
@Controller
public class UPICallbackController extends BasePaymentCallbackController {

    @Autowired
    private RechargeOrderService mRechargeOrderService;

    @Autowired
    private ChannelService mChannelService;

    private boolean isProd = MyEnvironment.isProd();

    @RequestMapping(path = "/payin")
    @ResponseBody
    public String actionToPayinCallback()
    {
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        // 自己系统的订单号
        String txnid = WebRequest.getString("txnid");
        // 银行订单号-utr
        String outTradeNo = WebRequest.getString("outTradeNo");
        // 会员upi地址，记录是为了更好的确定
        String upiAddress = WebRequest.getString("upiAddress");

        if(StringUtils.isEmpty(outTradeNo) || !RegexUtils.isLetterDigit(outTradeNo) || outTradeNo.length() > 50)
        {
            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "UTR error!");
            return apiJsonTemplate.toJSONString();
        }

        if(StringUtils.isEmpty(upiAddress) || !RegexUtils.isUPI(upiAddress))
        {
            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "UPI Address error!");
            return apiJsonTemplate.toJSONString();
        }

        PaymentReturnStatusModel statusModel = ThirdReturnStatusHelper.getFrontAndDelete(PayProductType.UPI, txnid);
        if(statusModel == null)
        {
            // 防止前台一直刷新
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }

        ChannelInfo channelInfo = mChannelService.findById(false, statusModel.getCid());
        if(channelInfo == null || channelInfo.getProduct() != PayProductType.UPI)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }

        mRechargeOrderService.updateTxStatus(txnid, OrderTxStatus.PENDING, outTradeNo, null, null);
        return apiJsonTemplate.toJSONString();
    }


}
