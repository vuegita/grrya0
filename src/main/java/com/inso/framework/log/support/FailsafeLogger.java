package com.inso.framework.log.support;

import com.inso.framework.log.Log;

public class FailsafeLogger implements Log{
	
	private Log logger;

	public FailsafeLogger(Log logger) {
		this.logger = logger;
	}

	private String appendContextMessage(String msg) {
	    return msg;
//	    return "[bootstrap=" + MyAppContext.getLogPrefix() + "] " + msg;
	}
	
	@Override
	public void trace(String msg) {
		try {
			logger.trace(msg);
		} catch (Throwable e) {
		}
	}

    public void trace(String msg, Throwable e) {
        try {
            logger.trace(appendContextMessage(msg), e);
        } catch (Throwable t) {
        }
    }

	public void debug(String msg, Throwable e) {
		try {
			logger.debug(appendContextMessage(msg), e);
		} catch (Throwable t) {
		}
	}


	public void debug(String msg) {
		try {
			logger.debug(appendContextMessage(msg));
		} catch (Throwable t) {
		}
	}

	public void info(String msg, Throwable e) {
		try {
			logger.info(appendContextMessage(msg), e);
		} catch (Throwable t) {
		}
	}

	public void info(String msg) {
		try {
			logger.info(appendContextMessage(msg));
		} catch (Throwable t) {
		}
	}

	public void warn(String msg, Throwable e) {
		try {
			logger.warn(appendContextMessage(msg), e);
		} catch (Throwable t) {
		}
	}

	public void warn(String msg) {
		try {
			logger.warn(appendContextMessage(msg));
		} catch (Throwable t) {
		}
	}

	public void error(String msg, Throwable e) {
		try {
			logger.error(appendContextMessage(msg), e);
		} catch (Throwable t) {
		}
	}

	public void error(String msg) {
		try {
			logger.error(appendContextMessage(msg));
		} catch (Throwable t) {
		}
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
