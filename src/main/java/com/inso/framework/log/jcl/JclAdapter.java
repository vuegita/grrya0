package com.inso.framework.log.jcl;

import org.apache.commons.logging.LogFactory;

import com.inso.framework.log.Log;
import com.inso.framework.log.LogAdapter;

public class JclAdapter implements LogAdapter{


	public Log getLog(String key) {
		return new JclLog(LogFactory.getLog(key));
	}

    public Log getLog(Class<?> key) {
        return new JclLog(LogFactory.getLog(key));
    }


}
