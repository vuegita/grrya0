package com.inso.modules.report.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.report.model.UserStatusV2Day;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.List;

public interface UserStatusV2DayService {

    public void addLog(Date date, UserStatusV2Day statusV2Day);
    public void delete(Date date, UserStatusV2Day statusV2Day);

    public UserStatusV2Day findByUserid(boolean purge, DateTime dateTime, long userid);
    public UserStatusV2Day queryByUserid(boolean purge, int typeHour, long userid);
    public List<UserStatusV2Day> queryListByUser(boolean purge, DateTime fromTime, long userid);

    public RowPager<UserStatusV2Day> queryScrollPage(PageVo pageVo, long agentid, long staffid, long userid);


}
