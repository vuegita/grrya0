package com.inso.modules.paychannel.helper;

import org.springframework.ui.Model;

import com.inso.framework.bean.ErrorResult;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;

/**
 * Payin 回调管理器
 * @author Administrator
 *
 */
public class PaymentErrorHelper {
	
	private static Log LOG = LogFactory.getLog(PaymentErrorHelper.class);


	
	public static String doFailureResponse(Model model, boolean isPay, ErrorResult result)
	{
		return doFailureResponse(model, isPay, result.getCode(), result.getError());
	}

	public static String doFailureResponse(Model model, boolean isPay, int code, String error)
	{
		if(isPay)
		{
			model.addAttribute("title", "pay error!");
		}
		else
		{
			model.addAttribute("title", "return error!");
		}
		model.addAttribute("error", error);
		model.addAttribute("code", code);
		return "passport/payment-checkout-error";
	}
	

}
