package com.inso.modules.passport.user.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.helper.IdGenerator;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.model.BuyVipOrderInfo;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.service.dao.BuyVipOrderDao;
import com.inso.modules.web.model.VIPInfo;
import com.inso.modules.web.model.VIPType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BuyVipOrderServiceImpl implements BuyVipOrderService {

    private static IdGenerator mIdGenerator = IdGenerator.newSingleWorder();

    @Autowired
    private BuyVipOrderDao mBuyVipOrderDao;

    @Override
    public String createOrder(UserAttr userAttr, VIPInfo vipInfo, BigDecimal amount) {
        String orderno = mIdGenerator.nextId();
        mBuyVipOrderDao.add(orderno, userAttr, vipInfo, OrderTxStatus.NEW, amount);
        return orderno;
    }

    @Override
    public void updateInfo(String orderno, OrderTxStatus status) {
        mBuyVipOrderDao.updateInfo(orderno, status);
    }

    @Override
    public BuyVipOrderInfo findByNo(boolean purge, String orderno) {
        return mBuyVipOrderDao.findByNo(orderno);
    }

    @Override
    public RowPager<BuyVipOrderInfo> queryScrollPage(PageVo pageVo, String orderno, long agentid, long staffid, long userid, OrderTxStatus status, VIPType vipType) {
        return mBuyVipOrderDao.queryScrollPage(pageVo, orderno, agentid, staffid, userid, status, vipType);
    }
}
