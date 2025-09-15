package com.inso.modules.passport.user.service.dao;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.model.BuyVipOrderInfo;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.web.model.VIPInfo;
import com.inso.modules.web.model.VIPType;

import java.math.BigDecimal;

public interface BuyVipOrderDao {

    public void add(String orderno, UserAttr userAttr, VIPInfo vipInfo, OrderTxStatus status, BigDecimal amount);
    public void updateInfo(String orderno, OrderTxStatus status);

    public BuyVipOrderInfo findByNo(String orderno);
    public RowPager<BuyVipOrderInfo> queryScrollPage(PageVo pageVo, String orderno, long agentid, long staffid, long userid, OrderTxStatus status, VIPType vipType);

}
