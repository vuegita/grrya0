package com.inso.modules.analysis.job;

import com.google.common.collect.Maps;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.DateUtils;
import com.inso.modules.analysis.logical.UserActiveStatsMgr;
import com.inso.modules.analysis.model.UserActiveStatsInfo;
import com.inso.modules.analysis.model.UserActiveStatsType;
import com.inso.modules.analysis.service.UserActiveStatsService;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.service.UserAttrService;
import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;
import java.util.Map;

public class UserActiveStatsJob implements Job {

    private static String ROOT_CACHE = UserActiveStatsJob.class.getName();


    private UserActiveStatsMgr mUserActiveStatsMgr = UserActiveStatsMgr.getInstance();

    private UserAttrService mUserAttrService;

    private UserActiveStatsService mUserActiveStatsService;

    private static boolean isRunning = false;

    public UserActiveStatsJob()
    {
        this.mUserAttrService = SpringContextUtils.getBean(UserAttrService.class);
        this.mUserActiveStatsService = SpringContextUtils.getBean(UserActiveStatsService.class);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        if(isRunning)
        {
            return;
        }
        isRunning = true;

        try {
            // 当前
            DateTime fireDateTime = new DateTime(context.getFireTime());
            doTask(fireDateTime);

            // 上1小时
            DateTime lastHourfireDateTime = fireDateTime.minusHours(1);
            doTask(lastHourfireDateTime);
        } finally {
            isRunning = false;
        }

    }

    private void doTask(DateTime fireDateTime)
    {
        String pdateString = fireDateTime.toString(DateUtils.TYPE_YYYY_MM_DD);
        Date pdate = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD, pdateString);

        long dayOfYear = fireDateTime.getDayOfYear();
        long hours = fireDateTime.getHourOfDay();


        String taskCacheKey = ROOT_CACHE + dayOfYear + hours;
        if(CacheManager.getInstance().exists(taskCacheKey))
        {
            // 存在则进去下一次动作
            return;
        }

        UserActiveStatsInfo memeberStatsInfo = new UserActiveStatsInfo();

        Map<String, UserActiveStatsInfo> staffStatsInfoMaps = Maps.newHashMap();

        // 1. 统计会员
        mUserAttrService.queryAllMember2(null, null, new Callback<UserAttr>() {
            @Override
            public void execute(UserAttr userAttr) {

                try {

                    if(userAttr.getDirectStaffid() <= 0)
                    {
                        return;
                    }

                    UserActiveStatsInfo staffStatsInfo = getStaffStatsInfo(staffStatsInfoMaps, userAttr);

                    // 如果在线时长为0, 则不可能存在其它数据
                    long onlineDuration = mUserActiveStatsMgr.getStats(userAttr.getUsername(), UserActiveStatsType.ONLINE_DURATION, dayOfYear, hours);
                    if(onlineDuration <= 0)
                    {
                        return;
                    }

                    memeberStatsInfo.clear();

                    memeberStatsInfo.setPdate(pdate);
                    memeberStatsInfo.setHours(hours);

                    memeberStatsInfo.setUserid(userAttr.getUserid());
                    memeberStatsInfo.setUsername(userAttr.getUsername());

                    memeberStatsInfo.setAgentid(userAttr.getAgentid());
                    memeberStatsInfo.setAgentname(userAttr.getAgentname());

                    memeberStatsInfo.setStaffid(userAttr.getDirectStaffid());
                    memeberStatsInfo.setStaffname(userAttr.getDirectStaffname());

                    long stayRgDuration = mUserActiveStatsMgr.getStats(userAttr.getUsername(), UserActiveStatsType.STAY_RG_DURATION, dayOfYear, hours);
                    long stayAbDuration = mUserActiveStatsMgr.getStats(userAttr.getUsername(), UserActiveStatsType.STAY_AB_DURATION, dayOfYear, hours);
                    long stayFruitDuration = mUserActiveStatsMgr.getStats(userAttr.getUsername(), UserActiveStatsType.STAY_FRUIT_DURATION, dayOfYear, hours);
                    long stayFmDuration = mUserActiveStatsMgr.getStats(userAttr.getUsername(), UserActiveStatsType.STAY_FM_DURATION, dayOfYear, hours);

                    memeberStatsInfo.setOnlineDuration(onlineDuration);
                    memeberStatsInfo.setStayRgDuration(stayRgDuration);
                    memeberStatsInfo.setStayAbDuration(stayAbDuration);
                    memeberStatsInfo.setStayFruitDuration(stayFruitDuration);
                    memeberStatsInfo.setStayFmDuration(stayFmDuration);

                    // 统计员工数据
                    staffStatsInfo.incre(memeberStatsInfo);

                    // 添加会员数据
                    mUserActiveStatsService.addReport(pdate, memeberStatsInfo);
                } catch (Exception e) {
                    //e.printStackTrace();
                    // 有可能重复添加
                }
            }
        });

        // 2. 统计员工数据
        for(Map.Entry<String, UserActiveStatsInfo> entry : staffStatsInfoMaps.entrySet())
        {
            try {
                UserActiveStatsInfo staffStatsInfo = entry.getValue();
                staffStatsInfo.setPdate(pdate);
                staffStatsInfo.setHours(hours);
                mUserActiveStatsService.addReport(pdate, entry.getValue());
            } catch (Exception e) {
                // 有可能重复添加
            }
        }

        // 3. 这个阶段已经完成，添加标识位
        CacheManager.getInstance().setString(taskCacheKey, "1", CacheManager.EXPIRES_DAY);
    }

    private UserActiveStatsInfo getStaffStatsInfo(Map<String, UserActiveStatsInfo> maps, UserAttr userAttr)
    {
        String key = userAttr.getDirectStaffname();
        UserActiveStatsInfo info = maps.get(key);
        if(info == null)
        {
            info = new UserActiveStatsInfo();
            info.setUserid(userAttr.getDirectStaffid());
            info.setUsername(userAttr.getDirectStaffname());

            info.setAgentid(userAttr.getAgentid());
            info.setAgentname(userAttr.getAgentname());

            maps.put(key, info);
        }

        return info;
    }

}
