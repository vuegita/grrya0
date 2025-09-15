package com.inso.modules.report.job;


import java.util.Date;
import java.util.Map;

import com.google.common.collect.Maps;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.DateUtils;
import com.inso.modules.passport.money.service.MoneyOrderService;
import com.inso.modules.report.model.MemberReport;
import com.inso.modules.report.model.PlatformReport;
import com.inso.modules.report.service.PlatformReportService;
import com.inso.modules.report.service.UserReportService;

/**
 * 平台每日报表
 */
public class PlatformReportJob implements Job {

    private static Log LOG = LogFactory.getLog(PlatformReportJob.class);

    private MoneyOrderService moneyOrderService;

    private PlatformReportService mPlatformReportService;

    private UserReportService mUserReportService;

    public PlatformReportJob()
    {
        this.moneyOrderService = SpringContextUtils.getBean(MoneyOrderService.class);
        this.mPlatformReportService = SpringContextUtils.getBean(PlatformReportService.class);
        this.mUserReportService = SpringContextUtils.getBean(UserReportService.class);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        DateTime dateTime = new DateTime();
        DateTime yesterday = dateTime.minusDays(1);

        // 统计昨天
        handle(yesterday);

        // 统计昨天员工
        StaffReportJob staffReportJob = new StaffReportJob();
        staffReportJob.handle(yesterday);
    }

    public void handle(DateTime dateTime)
    {
        String pdateStr = dateTime.toString(DateUtils.TYPE_YYYY_MM_DD);
        Date date = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD, pdateStr);
        String startTime = DateUtils.getBeginTimeOfDay(pdateStr);
        String endTime = DateUtils.getEndTimeOfDay(pdateStr);

        Map<String, PlatformReport> maps = Maps.newHashMap();

        mUserReportService.queryAllMemberReport(startTime, endTime, new Callback<MemberReport>() {
            @Override
            public void execute(MemberReport o) {
                PlatformReport report = loadMap(maps, date, o);
                report.incre(o);
            }
        });


        for(String key : maps.keySet())
        {
            try {
                PlatformReport report = maps.get(key);
                FundAccountType accountType = FundAccountType.getType(report.getFundKey());
                ICurrencyType currencyType = ICurrencyType.getType(report.getCurrency());

                mPlatformReportService.delete(date, accountType, currencyType);
                mPlatformReportService.addReport(report);
            } catch (Exception e) {
                LOG.error("handle error:", e);
            }
        }
    }

    private PlatformReport loadMap(Map<String, PlatformReport> maps, Date date, MemberReport model)
    {
        String key = model.getFundKey() + model.getCurrency();
        PlatformReport report = maps.get(key);
        if(report == null)
        {
            report = new PlatformReport();
            report.setPdate(date);
            report.setFundKey(model.getFundKey());
            report.setCurrency(model.getCurrency());
            report.init();

            maps.put(key, report);
        }

        return report;
    }

    public void test()
    {
        String time = "2023-04-26 00:00:00";
        Date pdate = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, time);
        DateTime nowTime = new DateTime(pdate);
        handle(nowTime);
    }

    

}
