//package com.bc.framework.log.log4j;
//
//import com.inso.framework.log.Log;
//
//public class Log4jLog implements Log, java.io.Serializable{
//
//	private static final long serialVersionUID = 1L;
//	private final org.apache.log4j.Logger logger;
//
//	public Log4jLog(org.apache.log4j.Logger logger) {
//		this.logger = logger;
//	}
//
//	public void trace(String msg) {
//		logger.trace(msg);
//	}
//
//
//	public void trace(String msg, Throwable e) {
//		logger.trace(msg, e);
//	}
//
//	public void debug(String msg) {
//		logger.debug(msg);
//	}
//
//
//	public void debug(String msg, Throwable e) {
//		logger.debug(msg, e);
//	}
//
//	public void info(String msg) {
//		logger.info(msg);
//	}
//
//	public void info(String msg, Throwable e) {
//		logger.info(msg, e);
//	}
//
//	public void warn(String msg) {
//		logger.warn(msg);
//	}
//
//	public void warn(String msg, Throwable e) {
//		logger.warn(msg, e);
//	}
//
//	public void error(String msg) {
//		logger.error(msg);
//	}
//
//	public void error(String msg, Throwable e) {
//		logger.error(msg, e);
//	}
//	
//}
