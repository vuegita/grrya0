package com.inso.modules.passport;

import java.util.Enumeration;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.utils.StringUtils;

public class PayResponseForm extends JSONObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void loadAllParameter(HttpServletRequest request)
	{
        Enumeration<?> temp = request.getParameterNames();
        if (null != temp) {
            while (temp.hasMoreElements()) {
                String key = (String) temp.nextElement();
                String value = request.getParameter(key);
                if(!StringUtils.isEmpty(value))
                {
                	put(key, value);	
                }
            }
        }
	}
	
	public void log()
	{
		Set<String> keys = keySet();
		for(String key : keys)
		{
			String value = getString(key);
			System.out.println("key = " + key + ", value = " + value);
		}
	}

	public String getNotEmptyValue(String key)
	{
		String value = getString(key);
		if(StringUtils.isEmpty(value))
		{
			return StringUtils.getEmpty();
		}
		return value;
	}
	
}
