package com.inso.modules.web.team.service.dao;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.web.team.model.TeamBusinessType;
import com.inso.modules.web.team.model.TeamBuyGroupInfo;
import com.inso.modules.web.team.model.TeamOrderInfo;

import java.math.BigDecimal;

public interface TeamOrderDao {

    public void addOrder(String orderno, String outTradeNo, UserAttr userAttr, TeamBuyGroupInfo groupInfo, long recordid, BigDecimal amount,
                         ICurrencyType currency, OrderTxStatus txStatus,
                         TeamBusinessType orderType, JSONObject remark);

    public void updateInfo(String orderno, OrderTxStatus status, JSONObject remark);

    public TeamOrderInfo findById(String orderno);
    public void deleteById(String orderno);
    public RowPager<TeamOrderInfo> queryScrollPage(PageVo pageVo, String sysOrderno, long agentid, long staffid, long userid, OrderTxStatus status);

}
