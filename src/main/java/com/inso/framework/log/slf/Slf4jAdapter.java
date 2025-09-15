package com.inso.framework.log.slf;


import com.inso.framework.log.Log;
import com.inso.framework.log.LogAdapter;

public class Slf4jAdapter implements LogAdapter{


	public Log getLog(Class<?> key) {
		return new Slf4jLog(org.slf4j.LoggerFactory.getLogger(key));
	}

	public Log getLog(String key) {
		return new Slf4jLog(org.slf4j.LoggerFactory.getLogger(key));
	}


}
