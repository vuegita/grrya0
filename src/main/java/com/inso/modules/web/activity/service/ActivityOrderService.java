package com.inso.modules.web.activity.service;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.web.activity.model.ActivityBusinessType;
import com.inso.modules.web.activity.model.ActivityInfo;
import com.inso.modules.web.activity.model.ActivityOrderInfo;

import java.math.BigDecimal;

public interface ActivityOrderService {


    public String addOrder(ActivityInfo activityInfo, String outTradeNo, UserAttr userAttr,
                           BigDecimal amount, ICurrencyType currency, JSONObject remark);

    public void updateInfo(String orderno, OrderTxStatus status, JSONObject remark);

    public ActivityOrderInfo findById(String orderno);

    public void deleteById(String orderno);
    public RowPager<ActivityOrderInfo> queryScrollPage(PageVo pageVo, String sysOrderno, long agentid, long staffid, long userid, ActivityBusinessType businessType, OrderTxStatus status);

}
