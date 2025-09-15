package com.inso.framework.quartz;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobDataMap;

import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.StringUtils;

public class JobModel {
	public final static String TIGGER_TYPE_NOW="now";
	public final static String TIGGER_TYPE_DATE="date";
	public final static String TIGGER_TYPE_CRON="cron";
	private String  jobId;
	private String  jobGroup;
	private Class<? extends Job> jobClass;
	private String triggerType=TIGGER_TYPE_NOW;  //
	private Date date;
	private String cronExpression;
	private JobDataMap jobParams;
	
	public JobModel(){
		this(null,null);
	}
	public JobModel(String jobId){
		 this(jobId,null);
	}
	public JobModel(String jobId,String jobGroup){
		this(jobId, jobGroup, new JobDataMap());
	}
	public JobModel(String jobId,String jobGroup, JobDataMap dataMap){
		this.jobId = jobId;
		this.jobGroup = jobGroup;
		this.jobParams = dataMap;
	}
	
	//
	
	public String getTriggerType() {
		return triggerType;
	}
	public void setTriggerType(String triggerType) {
		if(triggerType != null) {
			this.triggerType = triggerType;
		}
	}
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	public String getJobGroup() {
		return jobGroup;
	}
	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}
	public Class<? extends Job> getJobClass() {
		return jobClass;
	}
	public void setJobClass(Class<? extends Job> jobClass) {
		this.jobClass = jobClass;
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setDateString(String dateString) {
//		this.date = date;
		if(!StringUtils.isEmpty(dateString)) {
			this.date = DateUtils.convertDate("yyyyMMddHHmmss", dateString);
		}
		
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getCronExpression() {
		return cronExpression;
	}
	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}
	public JobDataMap getJobParams() {
		return jobParams;
	}

	
	
	
	
}
