package com.inso.modules.game.controller;

import java.util.Date;

import org.joda.time.DateTime;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.WhiteIPManager;
import com.inso.modules.game.job.BusinessReportJob;

@RequestMapping("/game/fixApi")
@RestController
public class FixApi {

    @RequestMapping("fixBusinessReport")
    public String fixBusinessReport()
    {
        String remoteip = WebRequest.getRemoteIP();
        if(!WhiteIPManager.getInstance().verify(remoteip))
        {
            return "fair";
        }

        String type = WebRequest.getString("type");
        String startTimeString = WebRequest.getString("startTime");
        String endTimeString = WebRequest.getString("endTime");

        Date startTime = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD, startTimeString);
        Date endTime = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD, endTimeString);

        if(startTime == null || endTime == null || StringUtils.isEmpty(type))
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

            if("lottery".equalsIgnoreCase(type) || "all".equalsIgnoreCase(type))
            {
                job.handleRGReport(startDateTime);
            }
            if("ab".equalsIgnoreCase(type) || "all".equalsIgnoreCase(type))
            {
                job.handleABReport(startDateTime);
            }

            startDateTime = startDateTime.plusDays(1);
        }

        return "ok";
    }

}
