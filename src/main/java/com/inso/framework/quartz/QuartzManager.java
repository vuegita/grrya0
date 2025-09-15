package com.inso.framework.quartz;

import java.util.Date;

import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;

public class QuartzManager {

	public static final Log LOG = LogFactory.getLog(QuartzManager.class);
	private Scheduler mScheduler = null;
	
	private static QuartzManager manager;
	
	public static QuartzManager getInstance()
	{
		synchronized (QuartzManager.class) {
			if(manager == null)
			{
				manager = new QuartzManager();
			}
		}
		return manager;
	}

	private QuartzManager() {
		init();
	}

	private void init() {
		try {
			SchedulerFactory factory = new StdSchedulerFactory("config/quartz.properties");
			mScheduler = factory.getScheduler();
			mScheduler.start();
		} catch (SchedulerException e) {
			LOG.error("init quartz error", e);
		}
	}

	public void deleteJobByGroup(String group)
	{
		try {
			for (String groupName : mScheduler.getJobGroupNames()) {
				for (JobKey jobKey : mScheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
					String jobName = jobKey.getName();
					String jobGroup = jobKey.getGroup();
					deleteJob(jobName, jobGroup);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 提交任务
	 * 
	 * @param taskClass
	 * @param dataMap
	 * @param cronExpression
	 */
	public void submitJob(JobModel jobmodel) {
		try {
			// 构建任务信息
			JobBuilder jobBuilder = JobBuilder.newJob();
			if (jobmodel.getJobId() != null) {
				JobKey jobKey = JobKey.jobKey(jobmodel.getJobId(), jobmodel.getJobGroup());
				jobBuilder.withIdentity(jobKey);
			}

			JobDetail job = jobBuilder.ofType(jobmodel.getJobClass()).usingJobData(jobmodel.getJobParams()).storeDurably().build();
			
			// 构建触发器
			TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger().forJob(job);
			if (JobModel.TIGGER_TYPE_DATE.equalsIgnoreCase(jobmodel.getTriggerType())) {
				if(jobmodel.getDate() != null) 
				{
					triggerBuilder.startAt(jobmodel.getDate());
				} else
				{
					triggerBuilder.startNow();
				}
			} else if (JobModel.TIGGER_TYPE_CRON.equalsIgnoreCase(jobmodel.getTriggerType())) {
				triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(jobmodel.getCronExpression()));
			} 
			Trigger trigger = triggerBuilder.build();

			mScheduler.addJob(job, true);
			mScheduler.scheduleJob(trigger);
		} catch (SchedulerException e) {
			LOG.error("schedule submitTask error:", e);
		}
	}

	public void submitCronJob(Class<? extends Job> taskClass, JobDataMap dataMap, String cronExpression, String mJobID, String mJobGroup) {
		JobModel jobModel = new JobModel(mJobID, mJobGroup, dataMap);
		jobModel.setJobClass(taskClass);
		jobModel.setTriggerType(JobModel.TIGGER_TYPE_CRON);
		jobModel.setCronExpression(cronExpression);
		submitJob(jobModel);
	}

	/**
	 * 提交指定时间相关的任务
	 * 
	 * @param taskClass
	 */
	public void submitDateJob(Class<? extends Job> taskClass, JobDataMap dataMap, Date date, String mJobID, String mJobGroup) {
		JobModel jobModel = new JobModel(mJobID, mJobGroup, dataMap);
		jobModel.setJobClass(taskClass);
		jobModel.setTriggerType(JobModel.TIGGER_TYPE_DATE);
		jobModel.setDate(date);
		submitJob(jobModel);
	}
	
	/**
	 * interrupt
	 */
	public void interruptJob(String jobID)
	{
		interruptJob(jobID, null);
	}
	
	public void interruptJob(String mJobID, String mJobGroup) {
		try {
			JobKey jobKey = JobKey.jobKey(mJobID, mJobGroup);
			mScheduler.interrupt(jobKey);
		} catch (Exception e) {
			LOG.error("interrupt job error:", e);
		}
	}

	/**
	 * 
	 * @param mJobID
	 */
	public void deleteJob(String mJobID) {
		deleteJob(mJobID, null);
	}

	public void deleteJob(String mJobID, String mJobGroup) {
		try {
			JobKey jobKey = JobKey.jobKey(mJobID, mJobGroup);
			mScheduler.deleteJob(jobKey);
		} catch (Exception e) {
			LOG.error("delete job error:", e);
		}
	}

	public void deleteJob(JobKey jobKey) {
		try {
			mScheduler.deleteJob(jobKey);
		} catch (Exception e) {
			LOG.error("delete job error:", e);
		}
	}
	
	public boolean checkExist(String mJobID, String mJobGroup)
	{
		boolean exist = false;
		try {
			JobKey jobKey = JobKey.jobKey(mJobID, mJobGroup);
			exist = mScheduler.checkExists(jobKey);
		} catch (SchedulerException e) {
			LOG.error("checkExist job error:", e);
		}
		return exist;
	}

	public void stop(boolean waitForJobsToComplete) {
		try {
			if (mScheduler != null) {
				mScheduler.shutdown(waitForJobsToComplete);
			}
		} catch (SchedulerException e) {
		}
		mScheduler = null;
	}
}
