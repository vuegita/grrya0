package com.inso.framework.http;

import okhttp3.Request;
import okhttp3.Response;

public abstract class HttpCallback {
	
	
	public abstract void onSuccess(Request request, Response response, byte[] data);
	
	public void onFailure(Throwable e)
	{
		e.printStackTrace();
	}

	
}
