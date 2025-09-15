package com.inso.modules.game.job;

import java.util.Date;

import com.inso.modules.game.GameChildType;
import com.inso.modules.game.lottery_game_impl.btc_kline.model.BTCKlineType;
import com.inso.modules.game.model.NewLotteryPeriodInfo;
import com.inso.modules.game.service.NewLotteryPeriodService;
import com.inso.modules.web.SystemRunningMode;
import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.DateUtils;
import com.inso.modules.game.andar_bahar.model.ABPeriodInfo;
import com.inso.modules.game.andar_bahar.model.ABType;
import com.inso.modules.game.andar_bahar.service.ABPeriodService;
import com.inso.modules.game.model.BusinessReport;
import com.inso.modules.game.rg.model.LotteryPeriodInfo;
import com.inso.modules.game.rg.model.LotteryRGType;
import com.inso.modules.game.rg.service.LotteryPeriodService;
import com.inso.modules.game.service.BusinessReportService;

public class BusinessReportJob implements Job {

    private static Log LOG = LogFactory.getLog(BusinessReportJob.class);


    private BusinessReportService mBusinessReportService;
    private LotteryPeriodService mLotteryPeriodService;
    private ABPeriodService mABPeriodService;
    private NewLotteryPeriodService mNewLotteryPeriodService;

    public BusinessReportJob()
    {
        this.mBusinessReportService = SpringContextUtils.getBean(BusinessReportService.class);

        this.mLotteryPeriodService = SpringContextUtils.getBean(LotteryPeriodService.class);
        this.mABPeriodService = SpringContextUtils.getBean(ABPeriodService.class);
        this.mNewLotteryPeriodService = SpringContextUtils.getBean(NewLotteryPeriodService.class);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        if(!SystemRunningMode.isBCMode())
        {
            return;
        }

        // 昨天数据统计
        DateTime dateTime = DateTime.now().minusDays(1);

        // 红绿
        handleRGReport(dateTime);

        // AB
        handleABReport(dateTime);

        handleNewReport(dateTime, BTCKlineType.mArr);
    }

    /**
     * 红绿业务报表
     * @param dateTime
     */
    public void handleRGReport(DateTime dateTime)
    {
        String timeString = dateTime.toString(DateUtils.TYPE_YYYY_MM_DD);
        Date pdate = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD, timeString);

        String startTimeString = timeString + " 00:00:00";
        String endTimeString = timeString + " 23:59:59";

        LotteryRGType[] values = LotteryRGType.values();
        for (LotteryRGType type : values)
        {
            BusinessReport report = new BusinessReport();
            report.init();
            mLotteryPeriodService.queryAll(type, startTimeString, endTimeString, new Callback<LotteryPeriodInfo>() {
                @Override
                public void execute(LotteryPeriodInfo periodInfo) {
                    try {
                        report.incre(periodInfo.getTotalBetAmount(), periodInfo.getTotalBetCount(), periodInfo.getTotalWinAmount(), periodInfo.getTotalWinCount(), periodInfo.getTotalFeemoney(), null);
                    } catch (Exception e) {
                        LOG.error("handleRGReport error:", e);
                    }
                }
            });

            mBusinessReportService.delete(pdate, type);
            mBusinessReportService.addReport(pdate, type, report, null);
        }
    }

    /**
     * Aadar-Bahar业务报表
     * @param dateTime
     */
    public void handleABReport(DateTime dateTime)
    {
        String timeString = dateTime.toString(DateUtils.TYPE_YYYY_MM_DD);
        Date pdate = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD, timeString);

        String startTimeString = timeString + " 00:00:00";
        String endTimeString = timeString + " 23:59:59";

        ABType[] values = ABType.values();
        for (ABType type : values)
        {
            BusinessReport report = new BusinessReport();
            report.init();

            mABPeriodService.queryAll(type, startTimeString, endTimeString, new Callback<ABPeriodInfo>() {
                @Override
                public void execute(ABPeriodInfo periodInfo) {
                    try {
                        report.incre(periodInfo.getTotalBetAmount(), periodInfo.getTotalBetCount(), periodInfo.getTotalWinAmount(), periodInfo.getTotalWinCount(), periodInfo.getTotalFeemoney(), null);
                    } catch (Exception e) {
                        LOG.error("handleABReport error:", e);
                    }
                }
            });

            mBusinessReportService.delete(pdate, type);
            mBusinessReportService.addReport(pdate, type, report, null);
        }
    }


    /**
     * Aadar-Bahar业务报表
     * @param dateTime
     */
    public void handleNewReport(DateTime dateTime, GameChildType[] arr)
    {
        String timeString = dateTime.toString(DateUtils.TYPE_YYYY_MM_DD);
        Date pdate = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD, timeString);

        String startTimeString = timeString + " 00:00:00";
        String endTimeString = timeString + " 23:59:59";

        for (GameChildType type : arr)
        {
            BusinessReport report = new BusinessReport();
            report.init();

            mNewLotteryPeriodService.queryAll2(type, startTimeString, endTimeString, type, new Callback<NewLotteryPeriodInfo>() {
                @Override
                public void execute(NewLotteryPeriodInfo periodInfo) {
                    try {
                        report.incre(periodInfo.getTotalBetAmount(), periodInfo.getTotalBetCount(), periodInfo.getTotalWinAmount(), periodInfo.getTotalWinCount(), periodInfo.getTotalFeemoney(), periodInfo.getTotalWinAmount2());
                    } catch (Exception e) {
                        LOG.error("handleNewReport error:", e);
                    }
                }
            });

            mBusinessReportService.delete(pdate, type);
            mBusinessReportService.addReport(pdate, type, report, null);
        }
    }

    public static void testRun()
    {
        try {
            BusinessReportJob job = new BusinessReportJob();
            job.execute(null);
        } catch (JobExecutionException e) {
            e.printStackTrace();
        }
    }
}
