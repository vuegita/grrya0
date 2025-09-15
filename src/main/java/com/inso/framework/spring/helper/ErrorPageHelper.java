package com.inso.framework.spring.helper;

import org.springframework.ui.Model;

import com.inso.framework.bean.ErrorResult;
import com.inso.framework.utils.StringUtils;

/**
 * 公共错误页面帮助类
 * @author Administrator
 *
 */
public class ErrorPageHelper {
	
	
	public static String toError(Model model, int code, String msg)
	{
		if(StringUtils.isEmpty(msg))
		{
			msg = "非法操作";
		}
		model.addAttribute("code", code);
		model.addAttribute("msg", msg);
		return "common/error";
	}
	
	public static String toError(Model model, String msg)
	{
		return toError(model, 0, msg);
	}
	
	public static String toError(Model model)
	{
		return toError(model, 0, null);
	}

	public static String toError(Model model, ErrorResult result)
	{
		return toError(model, result.getCode(), result.getError());
	}
}
