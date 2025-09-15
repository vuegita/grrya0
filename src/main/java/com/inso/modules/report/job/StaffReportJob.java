package com.inso.modules.report.job;


import java.util.Date;
import java.util.Map;

import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.alibaba.druid.util.LRUCache;
import com.google.common.collect.Maps;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.DateUtils;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.report.model.MemberReport;
import com.inso.modules.report.service.UserReportService;

/**
 * 员工统计
 * 由实时统计改成5分钟统计一次
 */
public class StaffReportJob implements Job {

    private static Log LOG = LogFactory.getLog(StaffReportJob.class);

    private UserReportService mUserReportService;
    private UserAttrService mUserAttrService;

    private static boolean isRunning = false;

    /*** ***/
    private LRUCache<String, UserAttr> mLRUCache = new LRUCache<>(100);

    public StaffReportJob()
    {
        this.mUserReportService = SpringContextUtils.getBean(UserReportService.class);
        this.mUserAttrService = SpringContextUtils.getBean(UserAttrService.class);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        if(isRunning)
        {
            return;
        }
        isRunning = true;
        try {
            DateTime dateTime = new DateTime();
            handle(dateTime);
        } catch (Exception e) {
            LOG.error("handle error:", e);
        } finally {
            isRunning = false;
        }
    }

    public void handle(DateTime dateTime)
    {
        String pdateStr = dateTime.toString(DateUtils.TYPE_YYYY_MM_DD);
        String startTime = DateUtils.getBeginTimeOfDay(pdateStr);
        String endTime = DateUtils.getEndTimeOfDay(pdateStr);

        Map<String, MemberReport> maps = Maps.newHashMap();

        mUserReportService.queryAllMemberReport(startTime, endTime, new Callback<MemberReport>() {
            @Override
            public void execute(MemberReport model) {
                try {
                    UserAttr userAttr = getUserAttr(model.getUserid(), model.getUsername());
                    if(userAttr.getDirectStaffid() <= 0)
                    {
                        // 没有员工，野会员
                        return;
                    }

                    MemberReport report = getCacheReport(maps, model, userAttr.getDirectStaffid(), userAttr.getDirectStaffname());
                    report.incre(model);
                } catch (Exception e) {
                    LOG.error("handle staff report error: ", e);
                }
            }
        });

        Date pdate = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD, pdateStr);
        for(MemberReport report : maps.values())
        {
            mUserReportService.updateReport(pdate, report);
        }
    }

    private MemberReport getCacheReport(Map<String, MemberReport> maps, MemberReport model, long userid, String username)
    {
        String key = model.getFundKey() + model.getCurrency() + username;
        MemberReport report = maps.get(key);

        if(report == null)
        {
            report = new MemberReport();
            report.setUserid(userid);
            report.setUsername(username);

            report.setFundKey(model.getFundKey());
            report.setCurrency(model.getCurrency());

            report.init();
            maps.put(key, report);
        }

        return report;
    }

    private UserAttr getUserAttr(long userid, String username)
    {
        UserAttr userAttr = mLRUCache.get(username);
        if(userAttr != null)
        {
            return userAttr;
        }

        userAttr = mUserAttrService.find(false, userid);
        mLRUCache.put(username, userAttr);
        return userAttr;
    }

    public void updateNow()
    {
        DateTime nowTime = new DateTime();
        handle(nowTime);
    }

    

}
