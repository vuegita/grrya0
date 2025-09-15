package com.inso.framework.spring.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;

//@ControllerAdvice
public class GlobalExceptionHandler {

	// 日志记录工具
	private static final Log LOG = LogFactory.getLog(GlobalExceptionHandler.class);

	@ExceptionHandler(Throwable.class)
	@ResponseBody
	public String handleException(Throwable e) {
		ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
		
		ErrorResult result = GlobalExceptionManager.getInstanced().getResult(e.getClass());
		if(result != null)
		{
			apiJsonTemplate.setJsonResult(result);
		}
		else
		{
			LOG.error("un handle error:", e);
			apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
		}
		return apiJsonTemplate.toJSONString();
	}

	// @ExceptionHandler(value = Exception.class)
	// public HttpMessageConverters defaultErrorHandler(HttpServletRequest request,
	// Exception e) throws Exception {
	// LOG.error(e.toString());
	// // 1. 需要定义一个Convert转换消息的对象
	// FastJsonHttpMessageConverter fastConverter = new
	// FastJsonHttpMessageConverter();
	// // 2.添加fastjson的配置信息，比如是否要格式化返回的json数据
	// FastJsonConfig fastJsonConfig = new FastJsonConfig();
	// fastJsonConfig.setDateFormat(DateUtils.TYPE_YYYYMMDDHHMMSSSSS);
	// fastJsonConfig.setSerializerFeatures(
	// SerializerFeature.PrettyFormat,
	// SerializerFeature.WriteNullStringAsEmpty,
	// SerializerFeature.WriteNullListAsEmpty,
	// SerializerFeature.MapSortField,
	// // 循环引用
	// SerializerFeature.DisableCircularReferenceDetect);
	// // 3.在convert中添加配置信息
	// fastConverter.setFastJsonConfig(fastJsonConfig);
	// HttpMessageConverter<?> converter = fastConverter;
	// return new HttpMessageConverters(converter);
	//
	// }
}
