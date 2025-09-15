package com.inso.framework.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;

/**
 * 
 * @author zgz
 *
 */
public class FastJsonHelper {
	
	private static Log LOG = LogFactory.getLog(FastJsonHelper.class);

	public static final JSONObject empty = new JSONObject();
	
	public static String jsonEncode(Object obj)
	{
		return JSON.toJSONString(obj, 
				SerializerFeature.DisableCircularReferenceDetect,
				SerializerFeature.WriteNullStringAsEmpty,
				SerializerFeature.WriteNullListAsEmpty,
				SerializerFeature.WriteNullBooleanAsFalse,
				SerializerFeature.WriteMapNullValue);
	}
	
	public static <T> T jsonDecode(String json, Class<T> clazz)
	{
		try {
			return (T)JSON.parseObject(json, clazz);
		} catch (Exception e) {
			LOG.debug("parse object error and the json is = " + json , e);
		}
		return null;
	}
	
	public static JSONObject toJSONObject(String jsonString) {
		JSONObject json = null;
		try {
			json = JSON.parseObject(jsonString);
		} catch (Exception e) {
			//LOG.error("to json objec error, and the json " + jsonString, e);
		}
		return json;
	}
	
	public static <T> List<T> parseArray(String json, Class<T> clazz)
	{
		try {
			return JSONObject.parseArray(json, clazz);
		} catch (Exception e) {
			LOG.error("parse array error, and then json = " + json, e);
		}
		return null;
	}
	
	public static JSONArray parseArray(String json)
	{
		try {
			return JSONObject.parseArray(json);
		} catch (Exception e) {
			//LOG.error("parse array error, and then json = " + json, e);
		}
		return null;
	}
	
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> toMap(String jsonString) {
		Map<String, Object> map = JSON.parseObject(jsonString, Map.class);
		return map;
	}

	/**
	 * 获取第一个key值
	 *
	 * @return
	 */
	public static String getFirstKey(JSONObject jsonObject) {
		String obj = null;
		for (String str : jsonObject.keySet()) {
			obj = str;
			if (obj != null) {
				break;
			}
		}
		return  obj;
	}


	public static void prettyJson(Object json){
		if(json == null)
		{
			return;
		}
		String str = JSONObject.toJSONString(json,true);
		LOG.info("pretty json = " + str);
	}

	public static void main(String[] args) {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("test", 1);
		
		String jsonString = jsonEncode(json);
		System.out.println("对象转成JSON字符串 : " + jsonString);
		System.out.println("JSON字符串转成对象 : ");
		JSONObject jsonObj = jsonDecode(jsonString, JSONObject.class);
		System.out.println(jsonObj);
	}
	
}
