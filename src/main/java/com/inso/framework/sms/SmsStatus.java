package com.inso.framework.sms;


import com.inso.framework.utils.StringUtils;

public class SmsStatus {
	public static final String TYPE_MOBILE = "m";
	public static final String TYPE_IP = "i";
	public static final int VERSION = 1;

	private String key;
	private String type;

	private long startTime;
	private long lastTime;
	/*** 处罚时间 ***/
	private long delayTime;
	private int sendCount;
	private int requestCount;

	public SmsStatus(String key, String type, long defaultDelayTime) {
		this.key = key;
		this.type = type;
		this.delayTime = defaultDelayTime;
		this.startTime = System.currentTimeMillis();
	}

	public void incrRequst() {
		this.requestCount++;
		this.lastTime = System.currentTimeMillis();
	}

	public void incrSend() {
		this.sendCount++;
		this.lastTime = System.currentTimeMillis();
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getSendCount() {
		return sendCount;
	}

	public void setSendCount(int sendCount) {
		this.sendCount = sendCount;
	}

	public int getRequestCount() {
		return requestCount;
	}

	public void setRequestCount(int requestCount) {
		this.requestCount = requestCount;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getLastTime() {
		return lastTime;
	}

	public void setLastTime(long lastTime) {
		this.lastTime = lastTime;
	}

	public long getDelayTime() {
		return delayTime;
	}

	public void setDelayTime(long delayTime) {
		this.delayTime = delayTime;
	}

	/**
	 * version=1 "1,starttime,lasttime,delay,request,send"
	 * 
	 * @return
	 */
	public String valueString() {
		StringBuffer buf = new StringBuffer();
		buf.append(VERSION).append(",").append(startTime).append(",").append(lastTime).append(",").append(delayTime)
				.append(",").append(requestCount).append(",").append(sendCount);
		return buf.toString();
	}

	public void asString(String str) {
		if (!StringUtils.isEmpty(str)) {
			String[] strs = str.split(",");
			if (strs.length == 6 && strs[0].equalsIgnoreCase("1")) {
				setStartTime(StringUtils.asLong(strs[1]));
				setLastTime(StringUtils.asLong(strs[2]));
				setDelayTime(StringUtils.asLong(strs[3]));
				setRequestCount(StringUtils.asInt(strs[4]));
				setSendCount(StringUtils.asInt(strs[5]));
			}
		} 
	}

}
