package com.inso.modules.passport.business.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.inso.modules.common.model.*;
import com.inso.modules.passport.user.model.UserInfo;
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
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.helper.BusinessOrderHelper;
import com.inso.modules.passport.business.cache.BusinessCacheUtils;
import com.inso.modules.passport.business.model.BusinessOrder;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.business.service.dao.BusinessOrderDao;

@Service
public class BusinessOrderServiceImpl implements BusinessOrderService {

    public static final String IGNORE_OUTTRADENO_PREFIX = "909";

    @Autowired
    private BusinessOrderDao mBusinessOrderDao;


    private BusinessType[] mBusinessTypeArray = new BusinessType[3];

    public BusinessOrderServiceImpl()
    {
        mBusinessTypeArray[0] = BusinessType.PLATFORM_RECHARGE;
        mBusinessTypeArray[1] = BusinessType.PLATFORM_PRESENTATION;
        mBusinessTypeArray[2] = BusinessType.PLATFORM_DEDUCT;
    }


    @Transactional
    public String createOrder(FundAccountType accountType, ICurrencyType currencyType, String outTradeNo, UserAttr userAttr, BusinessType businessType, BigDecimal amount, BigDecimal feemoney, Date createtime, RemarkVO remark)
    {
        if(businessType == null)
        {
            throw  new RuntimeException("error business type !!!");
        }
        String orderno = BusinessOrderHelper.nextId(businessType);
        // 也是唯一索引，不能为空
        if(StringUtils.isEmpty(outTradeNo))
        {
            outTradeNo = IGNORE_OUTTRADENO_PREFIX + orderno;
        }
        mBusinessOrderDao.addOrder(accountType, currencyType, orderno, outTradeNo, userAttr, businessType, OrderTxStatus.NEW, amount, feemoney, createtime, remark);
        return orderno;
    }

    @Transactional
    public String createOrder(FundAccountType accountType, ICurrencyType currencyType, UserAttr userAttr, BusinessType businessType, BigDecimal amount, Date createtime, RemarkVO remark)
    {
        if(businessType == null)
        {
            throw  new RuntimeException("error business type !!!");
        }

        String orderno = BusinessOrderHelper.nextId(businessType);
        // 也是唯一索引，不能为空
        String outTradeno = IGNORE_OUTTRADENO_PREFIX + orderno;
        BigDecimal feemoney = BigDecimal.ZERO;
        mBusinessOrderDao.addOrder(accountType, currencyType, orderno, outTradeno, userAttr, businessType, OrderTxStatus.NEW, amount, feemoney, createtime, remark);
        return orderno;
    }

    @Transactional
    public void updateTxStatus(String orderno, OrderTxStatus txStatus, String checker, RemarkVO remark)
    {
        mBusinessOrderDao.updateTxStatus(orderno, txStatus, checker, remark);
    }

    @Override
    public void updateOutTradeNo(String orderno, String tradeNo) {
        mBusinessOrderDao.updateOutTradeNo(orderno, tradeNo);
    }

    public BusinessOrder findByNo(String orderno)
    {
        BusinessOrder order = mBusinessOrderDao.findByNo(orderno);
//        order.clearIgnoreOutTradeNo();
        return order;
    }
    public BusinessOrder findByOutTradeNo(BusinessType businessType, String outTradeno)
    {
        BusinessOrder order = mBusinessOrderDao.findByOutTradeNo(businessType, outTradeno);
//        order.clearIgnoreOutTradeNo();
        return order;
    }

    @Override
    public List<BusinessOrder> queryByAgent(boolean purge, long agentid, UserInfo.UserType userType, long userid, int offset) {

        DateTime toTime = DateTime.now();
        DateTime fromTime = toTime.minusDays(60);

        String cachekey = BusinessCacheUtils.queryByAgentOrUser(agentid, userid);
        List<BusinessOrder> myList = null;
        if(offset <= 90)
        {
            myList = CacheManager.getInstance().getList(cachekey, BusinessOrder.class);
            if(purge || myList == null)
            {
                myList = mBusinessOrderDao.queryByAgent(fromTime, toTime, mBusinessTypeArray, agentid, userType, userid, offset, 100);

                if(myList == null)
                {
                    myList = Collections.emptyList();
                }

                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(myList), 300);
            }
        }
        else
        {
            myList = mBusinessOrderDao.queryByAgent(fromTime, toTime, mBusinessTypeArray, agentid, userType, userid, offset, 10);
            return myList;
        }

        if(CollectionUtils.isEmpty(myList))
        {
            return Collections.emptyList();
        }

        int addIndex = 0;
        List rsList = new ArrayList();
        int size = myList.size();
        for(int i = offset; i < size; i ++)
        {
            if(addIndex >= 10)
            {
                break;
            }
            rsList.add(myList.get(i));
            addIndex ++;
        }
        return rsList;
    }

    @Override
    public List<BusinessOrder> queryScrollPageByUser(PageVo pageVo, long userid, BusinessType businessType, OrderTxStatus[] txStatusArray) {
        List<BusinessOrder> list = null;
        if(pageVo.getOffset() <= 90)
        {
            pageVo.setLimit(100);

            String cachekey = BusinessCacheUtils.queryLatestPage_100(userid, businessType);
            list = CacheManager.getInstance().getList(cachekey, BusinessOrder.class);

            if(list == null)
            {
                list = mBusinessOrderDao.queryScrollPageByUser(pageVo, userid, businessType, 0, 100);
                if(CollectionUtils.isEmpty(list))
                {
                    list = Collections.emptyList();
                }

                // 缓存
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(list));
            }

        }
        else
        {
            list = mBusinessOrderDao.queryScrollPageByUser(pageVo, userid, businessType, pageVo.getOffset(), pageVo.getLimit());
            return list;
        }

        if(CollectionUtils.isEmpty(list))
        {
            return Collections.emptyList();
        }

        int addIndex = 0;
        List rsList = new ArrayList();
        int size = list.size();
        for(int i = pageVo.getOffset(); i < size; i ++)
        {
            if(addIndex >= 10)
            {
                break;
            }

            BusinessOrder orderInfo = list.get(i);
            if(txStatusArray != null)
            {
                OrderTxStatus txStatus = OrderTxStatus.getType(orderInfo.getStatus());
                for(OrderTxStatus tmp : txStatusArray)
                {
                    if(tmp == txStatus)
                    {
                        rsList.add(list.get(i));
                        addIndex ++;
                        break;
                    }
                }
                continue;
            }
            rsList.add(list.get(i));
            addIndex ++;
        }
        return rsList;
    }

    @Override
    public void queryAll(String startTimeString, String endTimeString, BusinessType businessType, Callback<BusinessOrder> callback) {
        mBusinessOrderDao.queryAll(startTimeString, endTimeString, businessType, callback);
    }

    @Override
    public RowPager<BusinessOrder> queryScrollPage(PageVo pageVo, long userid, String systemNo, String refNo, BusinessType[] businessTypeArray, ICurrencyType currencyType, OrderTxStatus txStatus, OrderTxStatus ignoreTxStatus,long agentid,long staffid)
    {
        RowPager<BusinessOrder> rs = mBusinessOrderDao.queryScrollPageByUser(pageVo, userid, systemNo, refNo, businessTypeArray, currencyType, txStatus, ignoreTxStatus, agentid, staffid);
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
    public void clearUserQueryPageCache(long userid, BusinessType businessType)
    {
        String cachekey = BusinessCacheUtils.queryLatestPage_100(userid, businessType);
        CacheManager.getInstance().delete(cachekey);
    }
}

