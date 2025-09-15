package com.inso.modules.passport.business.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.inso.modules.common.model.*;
import com.inso.modules.paychannel.model.ChannelInfo;
import com.inso.modules.paychannel.model.PayProductType;
import org.joda.time.DateTime;
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
import com.inso.modules.passport.business.cache.WithdrawCacheUtils;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.business.model.WithdrawOrder;
import com.inso.modules.passport.business.service.dao.WithdrawOrderDao;

@Service
public class WithdrawOrderServiceImpl implements WithdrawOrderService {

    private BusinessType mWithdrawBusinessType = BusinessType.USER_WITHDRAW;

    @Autowired
    private WithdrawOrderDao mWithdrawOrderDao;

    private static OrderTxStatus[] mWaitingTxStatusArray = {OrderTxStatus.AUDIT, OrderTxStatus.WAITING};
    private static OrderTxStatus[] mFinishTxStatusArray = {OrderTxStatus.REALIZED, OrderTxStatus.FAILED};

    @Transactional
    public String createOrder(FundAccountType accountType, ICurrencyType currencyType, UserInfo userInfo, UserAttr userAttr, PayProductType productType, BigDecimal amount, BigDecimal feemoney, Date createtime, RemarkVO remark , String account, String idcard, ChannelInfo channelInfo)
    {
        String orderno = BusinessOrderHelper.nextId(mWithdrawBusinessType);
        mWithdrawOrderDao.addOrder(accountType, currencyType, orderno, userInfo, userAttr, OrderTxStatus.NEW, productType, amount, feemoney, createtime, remark,account,idcard, channelInfo);
        this.clearUserQueryPageCache(userInfo.getId(), true);
        this.clearUserQueryPageCache(userInfo.getId(), false);
        return orderno;
    }

    @Transactional
    public void updateTxStatus(String orderno, OrderTxStatus txStatus, String outTradeNo, String checker, RemarkVO remark)
    {
        mWithdrawOrderDao.updateTxStatus(orderno, txStatus, outTradeNo, checker, remark, -1);
        WithdrawOrder order = mWithdrawOrderDao.findByNo(orderno);
        this.clearUserQueryPageCache(order.getUserid(), true);
        this.clearUserQueryPageCache(order.getUserid(), false);
    }

    @Transactional
    public void changeTxStatusToAudit(WithdrawOrder orderInfo, RemarkVO remark)
    {
        this.clearUserQueryPageCache(orderInfo.getUserid(), true);
        this.clearUserQueryPageCache(orderInfo.getUserid(), false);

        long submitCount = orderInfo.getSubmitCount() + 1;
        mWithdrawOrderDao.updateTxStatus(orderInfo.getNo(), OrderTxStatus.AUDIT, null, null, remark, submitCount);
    }


//    @Override
//    public void updateOutTradeNo(String orderno, String tradeNo) {
//        mWithdrawOrderDao.updateOutTradeNo(orderno, tradeNo);
//    }

    public WithdrawOrder findByNo(String orderno)
    {
        WithdrawOrder order = mWithdrawOrderDao.findByNo(orderno);
        return order;
    }
    public WithdrawOrder findByOutTradeNo(String outTradeno)
    {
        WithdrawOrder order = mWithdrawOrderDao.findByOutTradeNo(outTradeno);
        return order;
    }

    @Override
    public List<WithdrawOrder> queryScrollPageByUser(PageVo pageVo, long userid, boolean isWaiting) {
        OrderTxStatus[] txStatusArray = mFinishTxStatusArray;
        if(isWaiting)
        {
            txStatusArray = mWaitingTxStatusArray;
        }
        List<WithdrawOrder> list = null;
        if(pageVo.getOffset() <= 90)
        {
            pageVo.setLimit(100);

            String cachekey = WithdrawCacheUtils.queryLatestPage_100(userid, isWaiting);
            list = CacheManager.getInstance().getList(cachekey, WithdrawOrder.class);

            if(list == null)
            {
                list = mWithdrawOrderDao.queryScrollPageByUser(pageVo, userid, txStatusArray, 0, 100);
                if(list == null)
                {
                    list = Collections.emptyList();
                }
                // 缓存
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(list));
            }
        }
        else
        {
            list = mWithdrawOrderDao.queryScrollPageByUser(pageVo, userid, txStatusArray, pageVo.getOffset(), pageVo.getLimit());
            return list;
        }

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
    public void queryAll(String startTimeString, String endTimeString, Callback<WithdrawOrder> callback) {
        mWithdrawOrderDao.queryAll(startTimeString, endTimeString, callback);
    }

    @Override
    public void queryAllByUpdateTime(DateTime startTime, DateTime enTime, Callback<WithdrawOrder> callback){
        mWithdrawOrderDao.queryAllByUpdateTime(startTime, enTime, callback);
    }

    @Override
    public RowPager<WithdrawOrder> queryScrollPage(PageVo pageVo, long userid, long agentid,long staffid, String systemNo, String refNo, OrderTxStatus txStatus, OrderTxStatus ignoreTxStatus,String beneficiaryAccount,String beneficiaryIdcard)
    {
        RowPager<WithdrawOrder> rs = mWithdrawOrderDao.queryScrollPageByUser(pageVo, userid, agentid, staffid, systemNo, refNo, txStatus, ignoreTxStatus,beneficiaryAccount,beneficiaryIdcard);
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
    public BigDecimal queryTotalWithdrawAmountScrollPage(PageVo pageVo, long userid, CryptoCurrency currencyType , OrderTxStatus txStatus){
        return mWithdrawOrderDao.queryTotalWithdrawAmountScrollPage(pageVo,userid,currencyType,txStatus).getTotalWithdrawAmount();
    }

    @Override
    public void clearUserQueryPageCache(long userid, boolean isWaiting)
    {
        String cachekey = WithdrawCacheUtils.queryLatestPage_100(userid, isWaiting);
        CacheManager.getInstance().delete(cachekey);
    }


}

