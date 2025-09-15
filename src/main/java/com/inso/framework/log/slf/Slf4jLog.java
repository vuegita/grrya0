package com.inso.framework.log.slf;

import com.inso.framework.log.Log;

public class Slf4jLog implements Log, java.io.Serializable{

	private static final long serialVersionUID = 1L;
	private final org.slf4j.Logger logger;

	public Slf4jLog(org.slf4j.Logger logger) {
		this.logger = logger;
	}

	public void trace(String msg) {
		logger.trace(msg);
	}


	public void trace(String msg, Throwable e) {
		logger.trace(msg, e);
	}

	public void debug(String msg) {
		logger.debug(msg);
	}


	public void debug(String msg, Throwable e) {
		logger.debug(msg, e);
	}

	public void info(String msg) {
		logger.info(msg);
	}

	public void info(String msg, Throwable e) {
		logger.info(msg, e);
	}

	public void warn(String msg) {
		logger.warn(msg);
	}

	public void warn(String msg, Throwable e) {
		logger.warn(msg, e);
	}

	public void error(String msg) {
		logger.error(msg);
	}

	public void error(String msg, Throwable e) {
		logger.error(msg, e);
	}
	
	public void alarm(String msg)
	{
		error(msg);
	}
	public void alarm(String msg, Throwable e)
	{
		error(msg, e);
	}
	
}
