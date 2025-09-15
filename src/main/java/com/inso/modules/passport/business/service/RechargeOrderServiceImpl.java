package com.inso.modules.passport.business.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.inso.modules.common.model.*;
import com.inso.modules.paychannel.model.ChannelInfo;
import com.inso.modules.paychannel.model.PayProductType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.helper.BusinessOrderHelper;
import com.inso.modules.passport.business.cache.RechargeCacheUtils;
import com.inso.modules.passport.business.model.RechargeOrder;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.business.service.dao.RechargeOrderDao;

@Service
public class RechargeOrderServiceImpl implements RechargeOrderService {

    private BusinessType mRechargeBusinessType = BusinessType.USER_RECHARGE;

    @Autowired
    private RechargeOrderDao mRechargeOrderDao;

    private static OrderTxStatus[] mWaitingTxStatusArray = {OrderTxStatus.NEW};
    private static OrderTxStatus[] mFinishTxStatusArray = {OrderTxStatus.REALIZED, OrderTxStatus.FAILED};

    @Transactional
    public String createOrder(UserInfo userInfo, UserAttr userAttr, ChannelInfo channelInfo, PayProductType productType, BigDecimal amount, Date createtime, RemarkVO remark)
    {
        String orderno = BusinessOrderHelper.nextId(mRechargeBusinessType);
        BigDecimal feemoney = BigDecimal.ZERO;

        FundAccountType accountType = FundAccountType.Spot;
        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
        mRechargeOrderDao.addOrder(channelInfo, accountType, currencyType, orderno, userInfo, userAttr, OrderTxStatus.NEW, productType, amount, feemoney, createtime, remark);
        return orderno;
    }

    @Transactional
    public void updateTxStatus(String orderno, OrderTxStatus txStatus, String outTradeNo, String checker, RemarkVO remark)
    {
        mRechargeOrderDao.updateTxStatus(orderno, txStatus, outTradeNo, checker, remark);
    }

    @Override
    public void updateAmount(String orerno, BigDecimal amount) {
        mRechargeOrderDao.updateAmount(orerno, amount);
    }

//    @Override
//    public void updateOutTradeNo(String orderno, OrderTxStatus txStatus, String tradeNo) {
//        mRechargeOrderDao.updateOutTradeNo(orderno, txStatus, tradeNo);
//    }

    public RechargeOrder findByNo(String orderno)
    {
        RechargeOrder order = mRechargeOrderDao.findByNo(orderno);
        return order;
    }
    public RechargeOrder findByOutTradeNo(String outTradeno)
    {
        RechargeOrder order = mRechargeOrderDao.findByOutTradeNo(outTradeno);
        return order;
    }

    @Override
    public List<RechargeOrder> queryScrollPageByUser(PageVo pageVo, long userid, boolean isWaiting) {
        OrderTxStatus[] txStatusArray = mFinishTxStatusArray;
        if(isWaiting)
        {
            txStatusArray = mWaitingTxStatusArray;
        }

        List<RechargeOrder> list = null;
        if(pageVo.getOffset() <= 90)
        {
            pageVo.setLimit(100);

            String cachekey = RechargeCacheUtils.queryLatestPage_100(userid, isWaiting);
            list = CacheManager.getInstance().getList(cachekey, RechargeOrder.class);

            if(list == null)
            {
                list = mRechargeOrderDao.queryScrollPageByUser(pageVo, userid, null, 0, 100);

                if(list == null)
                {
                    list = Collections.emptyList();
                }

                // 缓存
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(list), 600);
            }

        }
//        else
//        {
//            list = mRechargeOrderDao.queryScrollPageByUser(pageVo, userid, txStatusArray, pageVo.getOffset(), pageVo.getPageSize());
//            return list;
//        }

        if(CollectionUtils.isEmpty(list))
        {
            return Collections.emptyList();
        }

        int rsIndex = 0;
        List rsList = new ArrayList();
        int size = list.size();
        for(int i = pageVo.getOffset(); i < size; i ++)
        {
            if(rsIndex >= 10)
            {
                break;
            }
            rsList.add(list.get(i));
            rsIndex ++;
        }
        return rsList;
    }

    @Override
    public void queryAll(String startTimeString, String endTimeString, Callback<RechargeOrder> callback) {
        mRechargeOrderDao.queryAll(startTimeString, endTimeString, callback);
    }

    @Override
    public RowPager<RechargeOrder> queryScrollPage(PageVo pageVo, long userid, long agentid,long staffid, String systemNo, String refNo, OrderTxStatus txStatus, OrderTxStatus ignoreTxStatus, long channelid)
    {
        RowPager<RechargeOrder> rs = mRechargeOrderDao.queryScrollPageByUser(pageVo, userid, agentid, staffid, systemNo, refNo, txStatus, ignoreTxStatus, channelid);
//        if(rs.getTotal() > 0)
//        {
//            for(BusinessOrder order : rs.getList())
//            {
//                order.clearIgnoreOutTradeNo();
//            }
//        }
        return rs;
    }

    @Override
    public RowPager<RechargeOrder> queryScrollPageByUserOrderBy(PageVo pageVo, long userid, long agentid, long staffid, String systemNo, String refNo, OrderTxStatus txStatus, OrderTxStatus ignoreTxStatus, String sortName, String sortOrder, long channelid) {
        RowPager<RechargeOrder> rs = mRechargeOrderDao.queryScrollPageByUserOrderBy(pageVo, userid, agentid, staffid, systemNo, refNo, txStatus, ignoreTxStatus,sortName,sortOrder, channelid);
        return rs;
    }

    @Override
    public RowPager<RechargeOrder> queryFirstRechargeScrollPage(PageVo pageVo, long userid, long agentid,long staffid) {
        return mRechargeOrderDao.queryFirstRechargeScrollPage(pageVo, userid, agentid,staffid);
    }

    @Override
    public void clearUserQueryPageCache(long userid, boolean isWaiting)
    {
        String cachekey = RechargeCacheUtils.queryLatestPage_100(userid, isWaiting);
        CacheManager.getInstance().delete(cachekey);
    }
}

