package com.inso.modules.web.team.job;

import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.modules.web.team.logical.TeamBuyGroupManager;
import com.inso.modules.web.team.model.TeamBuyGroupInfo;
import com.inso.modules.web.team.service.TeamBuyGroupService;
import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class SettleTeamBuyGroupJob implements Job {

    private static Log LOG = LogFactory.getLog(SettleTeamBuyGroupJob.class);

    private TeamBuyGroupService mGroupService;

    private TeamBuyGroupManager mTeamBuyGroupManager;

    public SettleTeamBuyGroupJob()
    {
        this.mGroupService = SpringContextUtils.getBean(TeamBuyGroupService.class);
        this.mTeamBuyGroupManager = SpringContextUtils.getBean(TeamBuyGroupManager.class);
    }


    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        DateTime toTime = new DateTime(context.getFireTime()).minusMinutes(1);
        DateTime fromTime = toTime.minusMinutes(20);

        bgTask(fromTime, toTime);
    }


    private void bgTask(DateTime fromTime, DateTime toTime)
    {
    }

    public void testStart()
    {
        DateTime dateTime = new DateTime();
        DateTime fromTime = dateTime.minusDays(10);
        bgTask(fromTime, dateTime);
    }
}
