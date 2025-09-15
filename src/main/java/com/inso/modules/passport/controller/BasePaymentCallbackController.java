package com.inso.modules.passport.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.Model;

import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;

public class BasePaymentCallbackController {


    private static final String KEY_CACHE_CONTROL = "Cache-Control";
    private static final String KEY_EXPIRES = "Expires";
    private static final String KEY_PRAGMA = "Pragma";

    protected Log LOG = LogFactory.getLog(getClass());


    public String handlePayinReturn(Model model, boolean isRechargeSuccess)
    {
        if(isRechargeSuccess)
        {
            model.addAttribute("recharge_result", "Recharge success");
        }
        else
        {
            model.addAttribute("recharge_result", "Recharge fair");
        }
        return "passport/payment-checkout-finish";
    }

    public String showWaitProcessor(Model model)
    {
        model.addAttribute("recharge_result", "Processing ...");
        return "passport/payment-checkout-finish";
    }

    /**
     * 取消浏览器缓存
     * @param response
     */
    protected void disabledCachePage(HttpServletResponse response)
    {
        response.setHeader(KEY_CACHE_CONTROL, "no-cache");
        response.setHeader(KEY_CACHE_CONTROL, "no-store");
        response.setDateHeader(KEY_EXPIRES, 0);
        response.setHeader(KEY_PRAGMA, "no-cache");
    }

}
