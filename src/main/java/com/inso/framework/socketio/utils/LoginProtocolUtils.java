package com.inso.framework.socketio.utils;

import java.util.HashMap;
import java.util.Map;

import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.socketio.model.MyProtocol;

public class LoginProtocolUtils {
	
	private static String DEFAULT_EVENT_TYPE = "login";
	
	public static final int ERROR_MULTY_CONN = 10002;
	
	public static MyProtocol INVALID_TOKEN = buildResponse(10001, "invalid token");
	public static MyProtocol MULTY_CONN = buildResponse(ERROR_MULTY_CONN, "multi client conn");
	public static MyProtocol MAX_CONN_ERR = buildResponse(10003, "server error");
	public static MyProtocol CONN_SUCCESS = buildResponse(SystemErrorResult.SUCCESS.getCode(), SystemErrorResult.SUCCESS.getError());
	
	public static MyProtocol buildResponse(int code, String msg)
	{
		MyProtocol protocol = new MyProtocol();
		protocol.setEvent(DEFAULT_EVENT_TYPE);
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("code", code);
		data.put("msg", msg);
		protocol.setData(data);
		return protocol;
	}
	

}
