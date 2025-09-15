package com.inso.modules.common.job;

import com.inso.framework.context.MyEnvironment;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.modules.coin.CoinInitManager;
import com.inso.modules.web.team.logical.TeamBuyInit;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class SystemInitJob implements Job {

    private static Log LOG = LogFactory.getLog(SystemInitJob.class);


    private CoinInitManager mCoinInitMgr;
    private TeamBuyInit mTeamBuyInit;

    public SystemInitJob()
    {
        this.mCoinInitMgr = SpringContextUtils.getBean(CoinInitManager.class);
        this.mTeamBuyInit = SpringContextUtils.getBean(TeamBuyInit.class);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            String fromBoot = context.getJobDetail().getJobDataMap().getString("fromBoot");

            if(!MyEnvironment.isDev())
            {
                mCoinInitMgr.init(fromBoot);
            }

            if(fromBoot != null && "admin".equalsIgnoreCase(fromBoot))
            {
                mTeamBuyInit.init();
            }
        } catch (Exception e) {
            LOG.error("handle error:", e);
        }

    }

}
