package com.inso.modules.web.activity.service.dao;

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

public interface ActivityDao  {

    public long add(String title, UserAttr userAttr, ActivityBusinessType businessType, ICurrencyType currencyType,
                    BigDecimal limitMinInvesAmount, long limitMinInviteCount, BigDecimal basicPresentAmount, String extraPresentTier,
                    OrderTxStatus txStatus, DateTime beginTime, DateTime endTime);

    public void updateInfo(long id, String title, long finishInviteCount, long finishInvesCount, BigDecimal finishInvesAmount, BigDecimal finishPresentAmount, OrderTxStatus txStatus, JSONObject remark);

    public ActivityInfo findById(long id);
    public ActivityInfo findLatest(DateTime dateTime, ActivityBusinessType businessType);

    public void deleteById(long id);
    public RowPager<ActivityInfo> queryScrollPage(PageVo pageVo, long agentid, long staffid, ActivityBusinessType businessType, OrderTxStatus status);

    public void queryAll(DateTime fromTime, DateTime toTime, Callback<ActivityInfo> callback);


}
