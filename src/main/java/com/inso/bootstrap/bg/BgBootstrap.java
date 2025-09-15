package com.inso.bootstrap.bg;

import com.inso.framework.context.MyEnvironment;
import com.inso.framework.quartz.QuartzManager;
import com.inso.framework.spring.SpringBootManager;
import com.inso.framework.spring.beans.SimpleNameGenerator;
import com.inso.modules.ad.mall.job.SalesReportJob;
import com.inso.modules.analysis.job.UserActiveStatsJob;
import com.inso.modules.coin.approve.job.ApproveNotifyMerchantJob;
import com.inso.modules.coin.approve.job.MonitorTransferJob;
import com.inso.modules.common.job.OverviewStatsJob;
import com.inso.modules.game.lottery_game_impl.NewLotteryBetTaskManager;
import com.inso.modules.passport.job.InviteFriendTaskJob;
import com.inso.modules.passport.money.UserPayManager;
import com.inso.modules.passport.returnwater.ReturnRecordManager;
import com.inso.modules.report.job.*;
import org.quartz.JobDataMap;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Date;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@ComponentScan(basePackages = {
        "com.inso.framework.spring",
        "com.inso.bootstrap",
        "com.inso.modules.common",
        "com.inso.modules.passport",
        "com.inso.modules.web",
        "com.inso.modules.paychannel",
        "com.inso.modules.game",
        "com.inso.modules.report",
        "com.inso.modules.analysis",
        "com.inso.modules.risk",
        "com.inso.modules.ad",
        "com.inso.modules.coin",
        },
        nameGenerator = SimpleNameGenerator.class)
@EnableTransactionManagement
@EnableAsync
public class BgBootstrap {

    private static final JobDataMap mDefaultMap = new JobDataMap();


    public static void main(String[] args) throws Exception {
        System.out.println("BgBootstrap ------------");
        SpringBootManager.run(BgBootstrap.class, "bootstrap.bg.server.port", "bg", args);

        // passport
        addPassportJob();

        // 统计
        addReport();

        // coini
        addCoin();

        //
        addAdStats();

        //
        fixData();

        test();

    }

    public static void addPassportJob()
    {
        QuartzManager quartz = QuartzManager.getInstance();

        // 赠送金额
        JobDataMap nowDataMap = new JobDataMap();
        nowDataMap.put("type", InviteFriendTaskJob.FIRE_TYPE_CALC_AMOUNT);
        quartz.submitCronJob(InviteFriendTaskJob.class, nowDataMap, "0 5 0 * * ?", null, null);

        // 每5分钟执行一次
        JobDataMap cronDataMap = new JobDataMap();
        cronDataMap.put("type", InviteFriendTaskJob.FIRE_TYPE_STATS_STATUS);
        quartz.submitCronJob(InviteFriendTaskJob.class, cronDataMap, "59 */30 * * * ?", null, null);
        quartz.submitDateJob(InviteFriendTaskJob.class, mDefaultMap, new Date(), null, null);
//        InviteFriendTaskJob job = new InviteFriendTaskJob();
//        job.test();

        // 每30分钟执行一次
        quartz.submitCronJob(OverviewStatsJob.class, createDataMapByType("cron"), "55 */30 * * * ?", null, null);
        quartz.submitCronJob(OverviewStatsJob.class, createDataMapByType("cron_purge"), "55 20 0 * * ?", null, null);
        quartz.submitDateJob(OverviewStatsJob.class, createDataMapByType("new"), new Date(), null, null);
    }

    private static JobDataMap createDataMapByType(String type)
    {
        JobDataMap map = new JobDataMap();
        map.put("type", type);
        return map;
    }

    public static void addReport()
    {
        QuartzManager quartz = QuartzManager.getInstance();

        // 员工统计
        quartz.submitCronJob(StaffReportJob.class, mDefaultMap, "59 */15 * * * ?", null, null);
//        StaffReportJob job = new StaffReportJob();
//        job.updateNow();

        // 平台统计
        quartz.submitCronJob(PlatformReportJob.class, mDefaultMap, "5 50 0 * * ?", null, null);

        // 用户增长统计
        quartz.submitCronJob(UserReportJob.class, mDefaultMap, "5 30 0 * * ?", null, null);
//        UserReportJob job = new UserReportJob();
//        job.test();

        // 业务统计
        quartz.submitCronJob(com.inso.modules.game.job.BusinessReportJob.class, mDefaultMap, "5 35 0 * * ?", null, null);

        // UserActiveStatsJob
        quartz.submitCronJob(UserActiveStatsJob.class, mDefaultMap, "59 59 * * * ?", null, null);
//        quartz.submitDateJob(UserActiveStatsJob.class, mDefaultMap, new Date(), null, null);

        // 业务统计
        quartz.submitCronJob(BusinessReportJob.class, mDefaultMap, "0 0 1 * * ?", null, null);
//        BusinessReportJob job = new BusinessReportJob();
//        job.test();

        quartz.submitDateJob(GameBusinessJob.class, mDefaultMap, new Date(), null, null);

    }

    public static void addCoin()
    {
        QuartzManager quartz = QuartzManager.getInstance();

        // approve通知管理
        JobDataMap approveDataMap1 = new JobDataMap();
        approveDataMap1.put("type", ApproveNotifyMerchantJob.TYPE_MQ);
        quartz.submitDateJob(ApproveNotifyMerchantJob.class, approveDataMap1, new Date(), null, null);

//        JobDataMap approveDataMap1 = new JobDataMap();
//        approveDataMap1.put("type", ApproveNotifyMerchantJob.TYPE_CRON_ALL);
//        quartz.submitCronJob(ApproveNotifyMerchantJob.class, mDefaultMap, "0 0 3 * * ?", null, null);
//
//        JobDataMap approveDataMap2 = new JobDataMap();
//        approveDataMap2.put("type", ApproveNotifyMerchantJob.TYPE_CRON_MINUS);
//        quartz.submitCronJob(ApproveNotifyMerchantJob.class, approveDataMap2, "0 * * * * ?", null, null);

        quartz.submitCronJob(MonitorTransferJob.class, mDefaultMap, "0 */5 * * * ?", null, null);
//        MonitorTransferJob job = new MonitorTransferJob();
//        job.test();


//        FixApproveJob job = new FixApproveJob();
//        job.fix();

//        SettleDayMiningRecordJob job2 = new SettleDayMiningRecordJob();
//        job2.test();

//        SettleStakingRecordJob job = new SettleStakingRecordJob();
//        job.testStart();

//        SettleOrderJob job = new SettleOrderJob();
//        job.testSyncWithdraw();

//        SettleCloudSolidRecordJob job = new SettleCloudSolidRecordJob();
//        job.testStart();
//
//        SettleCloudDayRecordJob job2 = new SettleCloudDayRecordJob();
//        job2.start();

    }

    public static void addAdStats()
    {
        QuartzManager quartz = QuartzManager.getInstance();
        quartz.submitCronJob(SalesReportJob.class, mDefaultMap, "0 0 1 * * ?", null, null);

        SalesReportJob job = new SalesReportJob();
        job.test();
    }

    public static void fixData()
    {
//        FixReportManager.fixSungame();
    }

    public static void test()
    {
//        ReturnRecordManager.testRun();
        //WebInfoManager.testRun();
        if(!MyEnvironment.isDev())
        {
            return;
        }
//        ActivityManager.test();
//        TestInviteMgr.test();

        //AdOrderManager.testRun();

//        FirstRechargeManager.testRun();

//        OverviewStatsJob.testRun();
//        ReturnRecordManager.testRun();
//        MailManager.testRun();

//        RechargeActiveManager.testRun();

//        ReturnWaterManager.testRun();
//        TestUserCode.testRun();

//        TodayInviteFriendManagerV2.testRun();


        BusinessReportJob.testRun();

//        NewLotteryBetTaskManager.testRun();

//        UserPayManager.testRun();
    }

}
