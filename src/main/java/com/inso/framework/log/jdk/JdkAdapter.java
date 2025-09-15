package com.inso.framework.log.jdk;

import com.inso.framework.log.Log;
import com.inso.framework.log.LogAdapter;

public class JdkAdapter implements LogAdapter{


	public Log getLog(Class<?> key) {
		return new JdkLog(java.util.logging.Logger.getLogger(key == null ? "" : key.getName()));
	}

	public Log getLog(String key) {
		return new JdkLog(java.util.logging.Logger.getLogger(key));
	}


}
