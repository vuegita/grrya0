package com.inso.modules.report.service;

import java.util.Date;
import java.util.List;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.report.model.UserStatusDay;
import org.joda.time.DateTime;

public interface UserStatusDayService {

    public void addReport(Date pdate, long agentid, String agentname, long staffid, String staffname, UserStatusDay report);
    public void delete(Date pdate, long agentid, long staffid);
    public RowPager<UserStatusDay> queryScrollPage(PageVo pageVo, long agentid, long staffid);

    public UserStatusDay querySubStatsInfoByAgent(boolean purge, long userid, DateTime dateTime, int periodOfDay);

    public List<UserStatusDay> queryListByAgent(boolean purge, long userid, int offset);
}
