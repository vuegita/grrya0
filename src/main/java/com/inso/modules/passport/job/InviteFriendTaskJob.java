package com.inso.modules.passport.job;

import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.modules.passport.business.MemberSubLevelManager;
import com.inso.modules.passport.user.logical.TodayInviteFriendManagerV2;
import com.inso.modules.web.SystemRunningMode;
import com.inso.modules.web.activity.logical.RangeInviteFriendManager;
import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.inso.framework.spring.SpringContextUtils;
import com.inso.modules.passport.user.logical.TodayInviteFriendManager;

public class InviteFriendTaskJob implements Job {

    private static Log LOG = LogFactory.getLog(InviteFriendTaskJob.class);

    public static final String FIRE_TYPE_STATS_STATUS = "stats_status";
    public static final String FIRE_TYPE_CALC_AMOUNT = "calc_amount";

    private TodayInviteFriendManager mInviteFriendManager;
    private TodayInviteFriendManagerV2 mTodayInviteFriendManagerV2;

    private RangeInviteFriendManager mRangeInviteFriendManager;

    private MemberSubLevelManager mStatsTopLevelManager;


    public InviteFriendTaskJob()
    {
        this.mInviteFriendManager = SpringContextUtils.getBean(TodayInviteFriendManager.class);
        this.mTodayInviteFriendManagerV2 = SpringContextUtils.getBean(TodayInviteFriendManagerV2.class);
        this.mRangeInviteFriendManager = SpringContextUtils.getBean(RangeInviteFriendManager.class);

        this.mStatsTopLevelManager = SpringContextUtils.getBean(MemberSubLevelManager.class);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            SystemRunningMode runningMode = SystemRunningMode.getSystemConfig();
            if(!(runningMode == SystemRunningMode.BC || runningMode == SystemRunningMode.FUNDS))
            {
                return;
            }

            String type = context.getJobDetail().getJobDataMap().getString("type");

            if(FIRE_TYPE_CALC_AMOUNT.equalsIgnoreCase(type))
            {
                // 昨天
                DateTime dateTime = new DateTime().minusDays(1);
                mInviteFriendManager.doTask(dateTime, true);

            }
            else
            {
                DateTime dateTime = new DateTime(context.getFireTime());

                mInviteFriendManager.doTask(dateTime, false);
                mTodayInviteFriendManagerV2.doTask(dateTime);
                mStatsTopLevelManager.doTask(dateTime);
            }
        } catch (Exception e) {
            LOG.error("handle error:", e);
        }

    }

    public void test()
    {
        DateTime dateTime = new DateTime().minusDays(0);
        mInviteFriendManager.doTask(dateTime, false);
//        mStatsTopLevelManager.doTask(dateTime);
    }

}
