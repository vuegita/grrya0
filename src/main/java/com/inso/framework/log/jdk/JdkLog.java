package com.inso.framework.log.jdk;

import java.util.logging.Level;

import com.inso.framework.log.Log;

public class JdkLog implements Log,java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private final java.util.logging.Logger logger;

	public JdkLog(java.util.logging.Logger logger) {
		this.logger = logger;
	}

	public void trace(String msg) {
		logger.log(Level.FINER, msg);
	}

	public void trace(Throwable e) {
		logger.log(Level.FINER, e.getMessage(), e);
	}

	public void trace(String msg, Throwable e) {
		logger.log(Level.FINER, msg, e);
	}

	public void debug(String msg) {
		logger.log(Level.FINE, msg);
	}

	public void debug(Throwable e) {
		logger.log(Level.FINE, e.getMessage(), e);
	}

	public void debug(String msg, Throwable e) {
		logger.log(Level.FINE, msg, e);
	}

	public void info(String msg) {
		logger.log(Level.INFO, msg);
	}

	public void info(String msg, Throwable e) {
		logger.log(Level.INFO, msg, e);
	}

	public void warn(String msg) {
		logger.log(Level.WARNING, msg);
	}

	public void warn(String msg, Throwable e) {
		logger.log(Level.WARNING, msg, e);
	}

	public void error(String msg) {
		logger.log(Level.SEVERE, msg);
	}

	public void error(String msg, Throwable e) {
		logger.log(Level.SEVERE, msg, e);
	}

	public void error(Throwable e) {
		logger.log(Level.SEVERE, e.getMessage(), e);
	}

	public void info(Throwable e) {
		logger.log(Level.INFO, e.getMessage(), e);
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
