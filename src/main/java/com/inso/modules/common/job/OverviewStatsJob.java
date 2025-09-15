package com.inso.modules.common.job;

import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.mq.MQManager;
import com.inso.framework.mq.MQSupport;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.cloud_mining.logical.CloudStatsManager;
import com.inso.modules.common.AgentOverviewManager;
import com.inso.modules.common.PlatformOverviewManager;
import com.inso.modules.common.model.TodayMemberProfitLossByUserType;
import com.inso.modules.passport.invite_stats.InviteStatsManager;
import com.inso.modules.passport.returnwater.ReturnRecordManager;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.report.model.MemberReport;
import com.inso.modules.report.service.UserReportService;
import com.inso.modules.web.logical.ActiveUserManager;
import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;

/**
 * 平台概况统计
 */
public class OverviewStatsJob implements Job {

    private static Log LOG = LogFactory.getLog(OverviewStatsJob.class);

    private static final String QUEUE_NAME = "inso_queue_common_stats_overview";

    private PlatformOverviewManager mTotalStatsManager;
    private AgentOverviewManager mAgentOverviewManager;
    private ReturnRecordManager mReturnRecordManager;
    private ActiveUserManager mActiveUserManager;
    private InviteStatsManager mInviteStatsManager;

    private UserReportService mUserReportService;

    private UserService mUserService;

    private static MQSupport mq = MQManager.getInstance().getMQ(MQManager.MQType.REDIS);
    private static boolean isStartMQ = false;

    private long mPlatformRefreshTime = -1;
    private long mAgentRefreshTime = -1;

    private static boolean isRunningTask = false;

    public OverviewStatsJob()
    {
        this.mAgentOverviewManager = SpringContextUtils.getBean(AgentOverviewManager.class);
        this.mTotalStatsManager = SpringContextUtils.getBean(PlatformOverviewManager.class);
        this.mReturnRecordManager = SpringContextUtils.getBean(ReturnRecordManager.class);
        this.mActiveUserManager = SpringContextUtils.getBean(ActiveUserManager.class);
        this.mInviteStatsManager = SpringContextUtils.getBean(InviteStatsManager.class);

        this.mUserReportService = SpringContextUtils.getBean(UserReportService.class);
        this.mUserService = SpringContextUtils.getBean(UserService.class);

    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        synchronized (OverviewStatsJob.class)
        {
            if(!isStartMQ)
            {
                startMQ();
                isStartMQ = true;
            }

            // concurrent
            if(isRunningTask)
            {
                return;
            }
            isRunningTask = true;
        }

        try {
            String type = context.getJobDetail().getJobDataMap().getString("type");

            boolean isCronPurge = "cron_purge".equalsIgnoreCase(type);
            DateTime fireTime = new DateTime(context.getFireTime());

            LOG.info("Start OverviewStatsJob Task, type = " + type);

            if(isCronPurge)
            {
                DateTime yesterdayTIme = fireTime.minusDays(1);
                mInviteStatsManager.doTask(yesterdayTIme);
                LOG.info("Start doTask for return record ...");
                mReturnRecordManager.doTask(yesterdayTIme, true, true);
                LOG.info("End doTask for return record ...");
                mActiveUserManager.updateHistoryActive(true);
            }
            else
            {
                mTotalStatsManager.doStats();
                mAgentOverviewManager.doStats();

                mPlatformRefreshTime = System.currentTimeMillis();
                mAgentRefreshTime = System.currentTimeMillis();

                mReturnRecordManager.doTask(fireTime, false, false);

                handleProfitLoss(fireTime, true);
                mActiveUserManager.updateHistoryActive(false);
            }

            CloudStatsManager.getInstance().refresh();
        } catch (Exception e) {
            LOG.error("handle error:", e);
        } finally {
            LOG.info("End OverviewStatsJob Task ...");
            isRunningTask = false;
        }
    }

    /**
     * 用户盈亏榜单
     */
    private void handleProfitLoss(DateTime dateTime, boolean isInitSystemProfit)
    {
        try {
            TodayMemberProfitLossByUserType profitLoss = new TodayMemberProfitLossByUserType();

            String pdateStr = dateTime.toString(DateUtils.TYPE_YYYY_MM_DD);
            String startTime = DateUtils.getBeginTimeOfDay(pdateStr);
            String endTime = DateUtils.getEndTimeOfDay(pdateStr);

            mUserReportService.queryAllMemberReport(startTime, endTime, new Callback<MemberReport>() {
                @Override
                public void execute(MemberReport memberReport) {
                    profitLoss.addReport(memberReport);
                }
            });

            profitLoss.doFinish(dateTime, isInitSystemProfit);


            List<UserInfo> staffUserInfoList=mUserService.userListbyUserType(false, UserInfo.UserType.STAFF);
            for(UserInfo userInfo : staffUserInfoList)
            {
                TodayMemberProfitLossByUserType ProfitLossByUserType= new TodayMemberProfitLossByUserType();
                mUserReportService.queryAllMemberReportByUserId(startTime, endTime,-1,userInfo.getId(),new Callback<MemberReport>() {
                    @Override
                    public void execute(MemberReport memberReport) {
                        ProfitLossByUserType.addReport(memberReport);
                    }
                });

                ProfitLossByUserType.doFinish(dateTime,userInfo.getId());
            }

            List<UserInfo> agentUserInfoList=mUserService.userListbyUserType(false, UserInfo.UserType.AGENT);
            for(UserInfo userInfo : agentUserInfoList)
            {
                TodayMemberProfitLossByUserType ProfitLossByUserType= new TodayMemberProfitLossByUserType();
                mUserReportService.queryAllMemberReportByUserId(startTime, endTime,userInfo.getId(),-1,new Callback<MemberReport>() {
                    @Override
                    public void execute(MemberReport memberReport) {
                        ProfitLossByUserType.addReport(memberReport);
                    }
                });

                ProfitLossByUserType.doFinish(dateTime,userInfo.getId());
            }
        } catch (Exception e) {
            LOG.error("handle handleProfitLoss error:", e);
        }

    }

    private void startMQ()
    {
        mq.subscribe(QUEUE_NAME, null, new Callback<String>() {
            @Override
            public void execute(String msg) {
                boolean isAgent = StringUtils.asBoolean(msg);
                synchronized (OverviewStatsJob.class)
                {
                    long currentTime = System.currentTimeMillis();
                    if(isAgent)
                    {
                        // 10s内不刷新
                        if(currentTime - mAgentRefreshTime > 10000)
                        {
                            mAgentOverviewManager.doStats();
                            mAgentRefreshTime = currentTime;
                        }
                    }
                    else
                    {
                        // 10s内不刷新
                        if(currentTime - mPlatformRefreshTime > 10000)
                        {
                            mTotalStatsManager.doStats();
                            mPlatformRefreshTime = currentTime;


                            handleProfitLoss(new DateTime(), false);
                        }
                    }

                }
            }
        });
    }

    public static void sendMessage(boolean isAgent)
    {
        if(isAgent)
        {
            mq.sendMessage(QUEUE_NAME, "1");
        }
        else
        {
            mq.sendMessage(QUEUE_NAME, "0");
        }

    }

    private void test1()
    {
//        mReturnRecordManager.doTask(DateTime.now(), true);
    }

    public static void testRun()
    {
        OverviewStatsJob job = new OverviewStatsJob();
        job.test1();
    }



}
