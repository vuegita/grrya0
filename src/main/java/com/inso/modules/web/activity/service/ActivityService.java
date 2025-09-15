package com.inso.modules.web.activity.service;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.web.activity.model.ActivityBusinessType;
import com.inso.modules.web.activity.model.ActivityInfo;
import com.inso.modules.web.team.model.TeamBusinessType;
import org.joda.time.DateTime;

import java.math.BigDecimal;

public interface ActivityService {

    public long add(String title, ActivityBusinessType businessType,
                    BigDecimal limitMinInvesAmount, long limitMinInviteCount, BigDecimal basicPresentAmount, String extraPresentTier,
                    DateTime beginTime, DateTime endTime);

    public void updateInfo(ActivityInfo entity, String title, long finishInviteCount, long finishInvesCount, BigDecimal finishInvesAmount, BigDecimal finishPresentAmount,
                           OrderTxStatus txStatus, JSONObject remark);

    public ActivityInfo findById(boolean purge, long id);
    public ActivityInfo findLatestActive(boolean purge, ActivityBusinessType businessType);

    public void deleteById(ActivityInfo entity);
    public RowPager<ActivityInfo> queryScrollPage(PageVo pageVo, long agentid, long staffid, ActivityBusinessType businessType, OrderTxStatus status);

    public void queryAll(DateTime fromTime, DateTime toTime, Callback<ActivityInfo> callback);


}
