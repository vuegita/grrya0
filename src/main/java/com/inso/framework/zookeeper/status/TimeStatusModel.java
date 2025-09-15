package com.inso.framework.zookeeper.status;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.inso.framework.utils.StringUtils;
import com.inso.framework.zookeeper.ZKClientManager;

/**
 * 
 * @author Administrator
 *
 */
public class TimeStatusModel {
	
	//
	private ZKClientManager mZKClient;
	private String mZKPath;
	
	// status
	private int mVersion = 1;
	private String mPosition;
	private String mUpdateTime;
	private boolean isUpdated = false;
	private TimeCondition mTimeCondition = TimeCondition.DAY;
	
	private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	
	public TimeStatusModel(ZKClientManager client, String zkPath)
	{
		this.mZKClient = client;
		this.mZKPath = zkPath;
	}
	
	public void setTimeCondition(TimeCondition condition)
	{
		this.mTimeCondition = condition;
	}
	
	public void setPosition(String position)
	{
		this.mPosition = position;
	}
	
	public String getPosition()
	{
		return mPosition;
	}
	
	/**
	 * 判断是否索引状态完成
	 * @return
	 */
	public boolean isUpdated()
	{
		return isUpdated;
	}
	
	private void setUpdateTime(String time)
	{
		Date date = new Date();
		String todayString = mDateFormat.format(date);
		// 不为空并且字符相等则表示索引完成
		if(!StringUtils.isEmpty(time) && time.length() == 14)
		{
			if (mTimeCondition == TimeCondition.DAY && todayString.equalsIgnoreCase(time))
			{
				isUpdated = true;
			}
			else if(mTimeCondition == TimeCondition.HOUR && time.startsWith(todayString.substring(0, 10)))
			{
				isUpdated = true;
			}
			mUpdateTime = time;
		}
		else
		{
			mUpdateTime = todayString;
		}
	}
	
	public void loadStatus()
	{
		String value = mZKClient.getData(mZKPath);
		asString(value);
	}
	
	public void saveStatus()
	{
		mZKClient.createPersistent(mZKPath, valueString());
	}
	
	private void asString(String value)
	{
		if(!StringUtils.isEmpty(value)) 
		{
			String[] valueArray = value.split(",");
			int version = StringUtils.asInt(valueArray[0]);
			if(version == 1)
			{
				mVersion = version;
				mPosition = StringUtils.asString(valueArray[1]);
				setUpdateTime(StringUtils.asString(valueArray[2]));
			}
		} else
		{
			// 初始化
			setUpdateTime(null);
		}
	}
	
	private String valueString()
	{
		StringBuffer buffer = new StringBuffer();
		if(mVersion == 1)
		{
			buffer.append(mVersion).append(",")
				  .append(mPosition).append(",")
				  .append(mUpdateTime);
		}
		return buffer.toString();
	}
	
	public static void main(String[] args)
	{
		SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		String result = mDateFormat.format(date);
		System.out.println(result);
		System.out.println(result.substring(0, 10));
//		CronIndexStatus status = new CronIndexStatus("0");
//		status.asString(null);
//		
//		System.out.println(status.isFinishedIndex);
//		
//		System.out.println(status.valueString());
	}
	

}
