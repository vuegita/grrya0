package com.inso.modules.ad.mall.service;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.core.helper.AdHelper;
import com.inso.modules.ad.core.model.AdMaterielInfo;
import com.inso.modules.ad.mall.model.PurchaseOrderInfo;
import com.inso.modules.ad.mall.service.dao.PurchaseOrderDao;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.model.UserAttr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService{

    @Autowired
    private PurchaseOrderDao mPurchaseOrderDao;

    @Override
    public String addOrder(UserAttr userAttr, AdMaterielInfo materielInfo, BigDecimal price, long quantity, BigDecimal totalAmount, BigDecimal realAmount) {

        String orderno = AdHelper.nextOrderIdOfPurchase();
        mPurchaseOrderDao.addOrder(orderno, userAttr, materielInfo, price, quantity, OrderTxStatus.NEW, totalAmount, realAmount, null);
        return orderno;
    }

    @Override
    public void updateInfo(String orderno, OrderTxStatus status, JSONObject jsonObject) {
        mPurchaseOrderDao.updateInfo(orderno, status, null, jsonObject);
    }

    @Override
    public PurchaseOrderInfo findById(boolean purge, String orderno) {
        return mPurchaseOrderDao.findById(orderno);
    }

    @Override
    public RowPager<PurchaseOrderInfo> queryScrollPage(PageVo pageVo, long agentid, long staffid, long userid, OrderTxStatus txStatus, long categoryid, String sysOrderno) {
        return mPurchaseOrderDao.queryScrollPage(pageVo, agentid, staffid, userid, txStatus, categoryid, sysOrderno);
    }
}
