package com.inso.modules.ad.mall.service;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.core.model.AdMaterielInfo;
import com.inso.modules.ad.mall.model.PurchaseOrderInfo;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.model.UserAttr;

import java.math.BigDecimal;

public interface PurchaseOrderService {


    public String addOrder(UserAttr userAttr, AdMaterielInfo materielInfo,
                         BigDecimal price, long quantity,
                         BigDecimal totalAmount, BigDecimal realAmount);

    public void updateInfo(String orderno, OrderTxStatus status, JSONObject jsonObject);

    public PurchaseOrderInfo findById(boolean purge, String orderno);
    public RowPager<PurchaseOrderInfo> queryScrollPage(PageVo pageVo, long agentid, long staffid, long userid, OrderTxStatus txStatus, long categoryid, String sysOrderno);



}
