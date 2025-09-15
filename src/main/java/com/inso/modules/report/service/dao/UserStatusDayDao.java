package com.inso.modules.report.service.dao;

import java.util.Date;
import java.util.List;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.report.model.UserStatusDay;
import org.joda.time.DateTime;

public interface UserStatusDayDao {

    public void addReport(Date pdate, long agentid, String agentname, long staffid, String staffname, UserStatusDay report);
    public void delete(Date pdate, long agentid, long staffid);
    public RowPager<UserStatusDay> queryScrollPage(PageVo pageVo, long agentid, long staffid);

    public UserStatusDay querySubStatsInfoByAgent(long userid, DateTime dateTime);

    public List<UserStatusDay> queryListByAgent(long userid, DateTime dateTime, int limit);
}
