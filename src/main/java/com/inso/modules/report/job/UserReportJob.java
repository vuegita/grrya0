package com.inso.modules.report.job;

import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.DateUtils;
import com.inso.modules.report.logical.MemberStatusStats;
import com.inso.modules.report.logical.MemberSubLevelStats;
import com.inso.modules.report.model.UserStatusDay;
import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;

public class UserReportJob implements Job {

    private static Log LOG = LogFactory.getLog(UserReportJob.class);

    private MemberStatusStats memberStatusStats;
    private MemberSubLevelStats memberSubLevelStats;

    public UserReportJob()
    {
        this.memberStatusStats = SpringContextUtils.getBean(MemberStatusStats.class);


        this.memberSubLevelStats = SpringContextUtils.getBean(MemberSubLevelStats.class);

    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        // 昨天数据统计
        DateTime dateTime = DateTime.now().minusDays(1);

        handleTask(dateTime);
    }

    public void handleTask(DateTime dateTime)
    {
        memberStatusStats.doTask(dateTime);

        memberSubLevelStats.doTask(dateTime);
    }

    public void test()
    {
        String pdateStr = "2023-03-16 00:00:00";
        Date pdate = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, pdateStr);

        DateTime dateTime = DateTime.now();
//        DateTime dateTime = new DateTime(pdate);

        handleTask(dateTime);
    }



}
