package com.inso.modules.passport.user.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.model.BuyVipOrderInfo;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.web.model.VIPInfo;
import com.inso.modules.web.model.VIPType;

import java.math.BigDecimal;

public interface BuyVipOrderService {

    public String createOrder(UserAttr userAttr, VIPInfo vipInfo, BigDecimal amount);
    public void updateInfo(String orderno, OrderTxStatus status);

    public BuyVipOrderInfo findByNo(boolean purge, String orderno);
    public RowPager<BuyVipOrderInfo> queryScrollPage(PageVo pageVo, String orderno, long agentid, long staffid, long userid, OrderTxStatus status, VIPType vipType);

}
