package com.inso.framework.log;

public interface Log{

    public void trace(String msg);
    public void trace(String msg, Throwable e);

	public void debug(String msg);
	public void debug(String msg, Throwable e);

	public void info(String msg);
	public void info(String msg, Throwable e);

	public void warn(String msg);
	public void warn(String msg, Throwable e);

	public void error(String msg);
	public void error(String msg, Throwable e);
	
	/**
	 * error + notice by email
	 * @param msg
	 */
	public void alarm(String msg);
	public void alarm(String msg, Throwable e);

}
