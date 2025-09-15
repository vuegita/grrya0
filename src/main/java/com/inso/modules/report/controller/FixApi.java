package com.inso.modules.report.controller;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.model.BusinessReport;
import com.inso.modules.passport.returnwater.ReturnRecordManager;
import com.inso.modules.report.job.BusinessReportJob;
import com.inso.modules.report.job.StaffReportJob;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.DateUtils;
import com.inso.modules.common.WhiteIPManager;
import com.inso.modules.report.job.UserReportJob;

@RequestMapping("/report/fixApi")
@RestController
public class FixApi {

    private static Log LOG = LogFactory.getLog(FixApi.class);

    private static ExecutorService mPool = Executors.newFixedThreadPool(1);

    @Autowired
    private ReturnRecordManager mReturnRecordManager;

    @RequestMapping("fixUserStatusDayReport")
    public String fixBusinessReport()
    {
        String remoteip = WebRequest.getRemoteIP();
        if(!WhiteIPManager.getInstance().verify(remoteip))
        {
            return "fair";
        }

        String startTimeString = WebRequest.getString("startTime");
        String endTimeString = WebRequest.getString("endTime");

        Date startTime = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD, startTimeString);
        Date endTime = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD, endTimeString);

        if(startTime == null || endTime == null)
        {
            return "fair";
        }

        DateTime startDateTime = new DateTime(startTime);
        DateTime endDateTime = new DateTime(endTime);

        int startDayOfYear = startDateTime.getDayOfYear();
        int endDayOfYear = endDateTime.getDayOfYear();

        if(endDayOfYear < startDayOfYear)
        {
            return "fair";
        }

        UserReportJob job = new UserReportJob();

        boolean stop = false;
        while (!stop)
        {
            if(startDateTime.getDayOfYear() > endDayOfYear)
            {
                stop = true;
                break;
            }

            job.handleTask(startDateTime);

            startDateTime = startDateTime.plusDays(1);
        }

        return "ok";
    }


    @RequestMapping("fixBusinessV2Report")
    public String fixBusinessV2Report()
    {
        String remoteip = WebRequest.getRemoteIP();
        if(!WhiteIPManager.getInstance().verify(remoteip))
        {
            return "fair";
        }

        String startTimeString = WebRequest.getString("startTime");
        String endTimeString = WebRequest.getString("endTime");

        Date startTime = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD, startTimeString);
        Date endTime = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD, endTimeString);

        if(startTime == null || endTime == null)
        {
            return "fair";
        }

        DateTime startDateTime = new DateTime(startTime);
        DateTime endDateTime = new DateTime(endTime);

        int startDayOfYear = startDateTime.getDayOfYear();
        int endDayOfYear = endDateTime.getDayOfYear();

        if(endDayOfYear < startDayOfYear)
        {
            return "fair";
        }

        BusinessReportJob job = new BusinessReportJob();

        boolean stop = false;
        while (!stop)
        {
            if(startDateTime.getDayOfYear() > endDayOfYear)
            {
                stop = true;
                break;
            }

            job.test2(startDateTime);

            startDateTime = startDateTime.plusDays(1);
        }

        return "ok";
    }

    // 员工
    @RequestMapping("fixStaffReport")
    public String fixStaffReport()
    {
        String remoteip = WebRequest.getRemoteIP();
        if(!WhiteIPManager.getInstance().verify(remoteip))
        {
            return "fair";
        }

        String startTimeString = WebRequest.getString("startTime");
        String endTimeString = WebRequest.getString("endTime");

        Date startTime = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD, startTimeString);
        Date endTime = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD, endTimeString);

        if(startTime == null || endTime == null)
        {
            return "fair";
        }

        StaffReportJob job = new StaffReportJob();
        mPool.submit(new Runnable() {
            @Override
            public void run() {
                DateTime startDateTime = new DateTime(startTime);
                DateTime endDateTime = new DateTime(endTime);

                int startDayOfYear = startDateTime.getDayOfYear();
                int endDayOfYear = endDateTime.getDayOfYear();

                if(endDayOfYear < startDayOfYear)
                {
                    return;
                }

                boolean stop = false;
                while (!stop)
                {
                    if(startDateTime.getDayOfYear() > endDayOfYear)
                    {
                        stop = true;
                        break;
                    }

                    job.handle(startDateTime);
                    startDateTime = startDateTime.plusDays(1);
                }
            }
        });


        return "ok";
    }

    @RequestMapping("fixUserMemberStatus")
    public String fixUserMemberStatus()
    {
        String remoteip = WebRequest.getRemoteIP();
        if(!WhiteIPManager.getInstance().verify(remoteip))
        {
            return "fair";
        }

        String startTimeString = WebRequest.getString("startTime");
        String endTimeString = WebRequest.getString("endTime");

        Date startTime = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD, startTimeString);
        Date endTime = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD, endTimeString);

        if(startTime == null || endTime == null)
        {
            return "fair";
        }

        DateTime startDateTime = new DateTime(startTime);
        DateTime endDateTime = new DateTime(endTime);

        int startDayOfYear = startDateTime.getDayOfYear();
        int endDayOfYear = endDateTime.getDayOfYear();

        if(endDayOfYear < startDayOfYear)
        {
            return "fair";
        }

        DateTime nowTime = DateTime.now();
        if(endDayOfYear > nowTime.getDayOfYear())
        {
            endDayOfYear = nowTime.getDayOfYear();
        }

        boolean stop = false;
        while (!stop)
        {
            LOG.info("Start date = " + startDateTime.toString(DateUtils.TYPE_YYYY_MM_DD));
            if(startDateTime.getDayOfYear() >= endDayOfYear)
            {
                stop = true;
                LOG.info("Stop date = " + startDateTime.toString(DateUtils.TYPE_YYYY_MM_DD));
                break;
            }


            mReturnRecordManager.doTask(startDateTime, true, true);
            startDateTime = startDateTime.plusDays(1);
        }

        return "ok";
    }


    @RequestMapping("fixBusinessReportJob")
    public String fixBusinessReportJob()
    {
        String remoteip = WebRequest.getRemoteIP();
        if(!WhiteIPManager.getInstance().verify(remoteip))
        {
            return "fair";
        }

        GameChildType gameChildType = GameChildType.getType(WebRequest.getString("gameType"));

        String startTimeString = WebRequest.getString("startTime");
        String endTimeString = WebRequest.getString("endTime");

        Date startTime = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD, startTimeString);
        Date endTime = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD, endTimeString);

        if(startTime == null || endTime == null)
        {
            return "fair";
        }

        DateTime startDateTime = new DateTime(startTime);
        DateTime endDateTime = new DateTime(endTime);

        int startDayOfYear = startDateTime.getDayOfYear();
        int endDayOfYear = endDateTime.getDayOfYear();

        if(endDayOfYear < startDayOfYear)
        {
            return "fair";
        }

        DateTime nowTime = DateTime.now();
        if(endDayOfYear > nowTime.getDayOfYear())
        {
            endDayOfYear = nowTime.getDayOfYear();
        }

        BusinessReportJob job = new BusinessReportJob();

        boolean stop = false;
        while (!stop)
        {
            LOG.info("Start date = " + startDateTime.toString(DateUtils.TYPE_YYYY_MM_DD));
            if(startDateTime.getDayOfYear() >= endDayOfYear)
            {
                stop = true;
                LOG.info("Stop date = " + startDateTime.toString(DateUtils.TYPE_YYYY_MM_DD));
                break;
            }

            job.addGameReport(startDateTime, gameChildType, false);
            startDateTime = startDateTime.plusDays(1);
        }

        return "ok";
    }
}
