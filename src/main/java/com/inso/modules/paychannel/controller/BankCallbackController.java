package com.inso.modules.paychannel.controller;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.spring.limit.MyIPRateLimit;
import com.inso.framework.utils.RegexUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.common.model.RemarkVO;
import com.inso.modules.passport.controller.BasePaymentCallbackController;
import com.inso.modules.passport.PayResponseForm;
import com.inso.modules.passport.business.model.RechargeOrder;
import com.inso.modules.passport.business.service.RechargeOrderService;
import com.inso.modules.paychannel.helper.ThirdReturnStatusHelper;
import com.inso.modules.paychannel.logical.payment.PaymentProcessorManager;
import com.inso.modules.paychannel.logical.payment.model.PaymentReturnStatusModel;
import com.inso.modules.paychannel.model.ChannelInfo;
import com.inso.modules.paychannel.model.PayProductType;
import com.inso.modules.paychannel.service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequestMapping("/pay/payment/bank")
@Controller
public class BankCallbackController extends BasePaymentCallbackController {

    @Autowired
    private RechargeOrderService mRechargeOrderService;

    @Autowired
    private ChannelService mChannelService;

    private boolean isProd = MyEnvironment.isProd();

    @MyIPRateLimit(maxCount=10)
    @RequestMapping(path = "/payin")
    @ResponseBody
    public String actionToPayinCallback(HttpServletRequest req, HttpServletResponse response)
    {
        disabledCachePage(response);
        PayResponseForm form = new PayResponseForm();
        form.loadAllParameter(req);

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        // 自己系统的订单号
        String txnid = form.getString("txnid");
        // 银行订单号-utr
        String outTradeNo = form.getString("outTradeNo");
        // 会员转账银行卡卡号
        String bankAccount = form.getString("bankAccount");

        if(StringUtils.isEmpty(outTradeNo) || !RegexUtils.isLetterDigit(outTradeNo) || outTradeNo.length() > 50)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        PaymentReturnStatusModel statusModel = ThirdReturnStatusHelper.getFrontAndDelete(PayProductType.BANK, txnid);
        if(statusModel == null)
        {
            // 防止前台一直刷新
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }

        ChannelInfo channelInfo = mChannelService.findById(false, statusModel.getCid());
        if(channelInfo == null || channelInfo.getProduct() != PayProductType.BANK)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }

        boolean rs = PaymentProcessorManager.getIntance().verifyPayinResponse(channelInfo, form);
        if(!rs)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }

        RechargeOrder order = mRechargeOrderService.findByNo(txnid);
        RemarkVO remarkVO = order.getRemarkVO();
        remarkVO.setMesage(bankAccount);

        mRechargeOrderService.updateTxStatus(txnid, OrderTxStatus.PENDING, outTradeNo, null, remarkVO);
        return apiJsonTemplate.toJSONString();
    }


}
