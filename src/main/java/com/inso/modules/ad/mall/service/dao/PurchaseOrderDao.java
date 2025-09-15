package com.inso.modules.ad.mall.service.dao;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.core.model.AdMaterielInfo;
import com.inso.modules.ad.mall.model.PurchaseOrderInfo;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.model.UserAttr;

import java.math.BigDecimal;

public interface PurchaseOrderDao  {


    public void addOrder(String orderno, UserAttr userAttr, AdMaterielInfo materielInfo,
                         BigDecimal price, long quantity, OrderTxStatus txStatus,
                         BigDecimal totalAmount, BigDecimal realAmount, JSONObject jsonObject);
    public void updateInfo(String orderno, OrderTxStatus status, String outTradeNo, JSONObject jsonObject);

    public PurchaseOrderInfo findById(String orderno);
    public RowPager<PurchaseOrderInfo> queryScrollPage(PageVo pageVo, long agentid, long staffid, long userid, OrderTxStatus txStatus, long categoryid, String sysOrderno);



}
