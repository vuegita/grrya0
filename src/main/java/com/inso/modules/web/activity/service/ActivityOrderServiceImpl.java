package com.inso.modules.web.activity.service;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.helper.IdGenerator;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.web.activity.model.ActivityBusinessType;
import com.inso.modules.web.activity.model.ActivityInfo;
import com.inso.modules.web.activity.model.ActivityOrderInfo;
import com.inso.modules.web.activity.service.dao.ActivityDao;
import com.inso.modules.web.activity.service.dao.ActivityOrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ActivityOrderServiceImpl implements ActivityOrderService{

    @Autowired
    private ActivityOrderDao mActivityOrderDao;

    private static IdGenerator mIdGenerator = IdGenerator.newSingleWorder();


    /**
     * 生成订单号
     * @return
     */
    public static String nextOrderId()
    {
        return mIdGenerator.nextId();
    }

    @Override
    public String addOrder(ActivityInfo activityInfo, String outTradeNo, UserAttr userAttr, BigDecimal amount, ICurrencyType currency, JSONObject remark) {

        String orderno = nextOrderId();
        mActivityOrderDao.addOrder(activityInfo, orderno, outTradeNo, userAttr, amount, currency, OrderTxStatus.NEW, remark);
        return orderno;
    }

    @Override
    public void updateInfo(String orderno, OrderTxStatus status, JSONObject remark) {
        mActivityOrderDao.updateInfo(orderno, status, remark);
    }

    @Override
    public ActivityOrderInfo findById(String orderno) {
        return mActivityOrderDao.findById(orderno);
    }

    @Override
    public void deleteById(String orderno) {
        mActivityOrderDao.deleteById(orderno);
    }

    @Override
    public RowPager<ActivityOrderInfo> queryScrollPage(PageVo pageVo, String sysOrderno, long agentid, long staffid, long userid, ActivityBusinessType businessType, OrderTxStatus status) {
        return mActivityOrderDao.queryScrollPage(pageVo, sysOrderno, agentid, staffid, userid, businessType, status);
    }
}
