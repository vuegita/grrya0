package com.inso.modules.report.service.dao;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.report.model.UserStatusV2Day;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.List;

public interface UserStatusV2DayDao  {

    public void addLog(Date date, UserStatusV2Day statusV2Day);
    public void delete(Date date, UserStatusV2Day statusV2Day);

    public UserStatusV2Day findByUserid(DateTime date, long userid);
    public UserStatusV2Day queryByUser(DateTime fromTime, long userid);
    public List<UserStatusV2Day> queryListByUser(DateTime fromTime, long userid);

    public RowPager<UserStatusV2Day> queryScrollPage(PageVo pageVo, long agentid, long staffid, long userid);


}
