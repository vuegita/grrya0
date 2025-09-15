package com.inso.framework.bean;

import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;

import java.util.HashMap;


/**
 * 接口返回模块
 * 默认{error:0, errmsg:success}
 * @author Administrator
 *
 */
public class ApiJsonTemplate extends HashMap<String, Object> {

	private static String KEY_LANGUAGE = "language";

	private static final String EVENT_NAME = "eventType";
	
//	private Map<String, Object> dataMap = new HashMap<String, Object>();
//	private String json;
//
//	public String getJson() {
//		return json;
//	}
//
//	public void setJson(String json) {
//		this.json = json;
//	}
	
	public ApiJsonTemplate()
	{
		this(SystemErrorResult.SUCCESS);
	}
	
	public ApiJsonTemplate(ErrorResult result)
	{
		setJsonResult(result);
	}

	public void setEvent(String groupType, String msgType)
	{
		// 消息分组
		put("groupType", StringUtils.getNotEmpty(groupType));

		// 消息类型
		put("eventType", StringUtils.getNotEmpty(msgType));
	}
	
	public void setJsonResult(ErrorResult error)
	{
		put("code", error.getCode());
		put("msg", error.getError());
//		String language = WebRequest.getString(KEY_LANGUAGE);
//		LanguageType languageType = LanguageType.getType(language);

//		if( languageType == null || languageType == LanguageType.English )
//		{
//			put("msg", error.getError());
//		}
//
//		else if(languageType == LanguageType.Spanish)
//		{
//			put("msg", error.getSPError());
//		}
//
//		else if(languageType == LanguageType.Hindi)
//		{
//			put("msg", error.getYDError());
//		}

	}

	public void setError(int code, String msg)
	{
		put("code", code);
		put("msg", msg);
	}

	public void setUnShowError(ErrorResult result)
	{
		setUnShowError(result.getError());
	}

	public void setUnShowError(String msg)
	{
		put("code", SystemErrorResult.SUCCESS_NOT_SHOW_ERR.getCode());
		put("msg", msg);
	}
	
	public ApiJsonTemplate addKeyValue(String key, Object value)
	{
		put(key, value);
		return this;
	}
	
	/**
	 * success
	 * @param data
	 * @return
	 */
	public ApiJsonTemplate setData(Object data)
	{
		put("data", data);
		setJsonResult(SystemErrorResult.SUCCESS);
		return this;
	} 
	
	public String toJSONString()
	{
		return FastJsonHelper.jsonEncode(this);
	}


	public String toString()
	{
		return toJSONString();
	}


	
	public static String buildErrorResult(ErrorResult result)
	{
		return new ApiJsonTemplate(result).toJSONString();
	}
	
	public static void main(String[] args) 
	{
//		Map<String, Object> dataMap = new HashMap<String, Object>();
//		dataMap.put("key", "v1");
//
//		Map<String, Object> dataMap2 = new HashMap<String, Object>();
//		dataMap2.put("key", "v1");
//
//		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//
//		list.add(dataMap);
//		list.add(dataMap2);
		
		ApiJsonTemplate template = new ApiJsonTemplate();
		template.setData("aa");
		template.setJsonResult(SystemErrorResult.ERR_EXIST);
		System.out.println(template.toJSONString());
	}


}
