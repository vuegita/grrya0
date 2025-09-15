package com.inso.modules.passport.invite_stats.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.passport.invite_stats.model.InviteStatsInfo;
import com.inso.modules.passport.user.model.UserInfo;
import org.joda.time.DateTime;

import java.util.Date;

public interface InviteStatsService {
    public void updateInfo(Date pdate, int dayOfYear, String key, UserInfo userInfo, long totalCount);
    public void updateInfo(Date pdate, String key, long totalCount);

    public void queryAll(DateTime fromTime, DateTime toTime, Callback<InviteStatsInfo> callback);

    public RowPager<InviteStatsInfo> queryScrollPage(PageVo pageVo, long agentid, long staffid, String key, long userid);

}
