package com.inso.modules.passport.returnwater.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.helper.BusinessOrderHelper;
import com.inso.modules.common.model.*;
import com.inso.modules.passport.business.cache.BusinessCacheUtils;
import com.inso.modules.passport.business.model.ReturnWaterOrder;
import com.inso.modules.passport.returnwater.service.dao.ReturnFirstRechargeUpOrderDao;
import com.inso.modules.passport.returnwater.service.dao.ReturnWaterOrderDao;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class ReturnFirstRechargeUpOrderServiceImpl implements ReturnFirstRechargeUpOrderService {

    public static BusinessType mReturnWaterBusinessType = BusinessType.RETURN_FIRST_RECHARGE_TO_UP;

    @Autowired
    private ReturnFirstRechargeUpOrderDao mReturnWaterOrderDao;

    @Transactional
    public String createOrder(int level, String outTradeNo, UserInfo userInfo, UserAttr userAttr, FundAccountType accountType, ICurrencyType currencyType, BigDecimal amount, Date createtime, RemarkVO remark)
    {
        String orderno = BusinessOrderHelper.nextId(mReturnWaterBusinessType);
        mReturnWaterOrderDao.addOrder(level, orderno, outTradeNo, userInfo, userAttr, accountType, currencyType, OrderTxStatus.NEW, amount, createtime, remark);
        return orderno;
    }

    @Transactional
    public void updateTxStatus(int level, String orderno, OrderTxStatus txStatus, String checker, RemarkVO remark)
    {
        mReturnWaterOrderDao.updateTxStatus(orderno, txStatus, checker, remark);
    }

    public ReturnWaterOrder findByNo(String orderno)
    {
        ReturnWaterOrder order = mReturnWaterOrderDao.findByNo(orderno);
        return order;
    }
    public ReturnWaterOrder findByOutTradeNo(String outTradeno)
    {
        ReturnWaterOrder order = mReturnWaterOrderDao.findByOutTradeNo(outTradeno);
        return order;
    }

    @Override
    public List<ReturnWaterOrder> queryScrollPageByUser(PageVo pageVo, long userid, OrderTxStatus[] txStatusArray) {
        List<ReturnWaterOrder> list = null;
        if(pageVo.getOffset() <= 10)
        {
            pageVo.setLimit(100);

            String cachekey = BusinessCacheUtils.queryLatestPage_100(userid, mReturnWaterBusinessType);
            list = CacheManager.getInstance().getList(cachekey, ReturnWaterOrder.class);

            if(list == null)
            {
                list = mReturnWaterOrderDao.queryScrollPageByUser(pageVo, userid);
            }
            // 缓存
            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(list));
        }
        else
        {
            list = mReturnWaterOrderDao.queryScrollPageByUser(pageVo, userid);
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
            if(addIndex >= pageVo.getPageSize())
            {
                break;
            }

            ReturnWaterOrder orderInfo = list.get(i);
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
    public void queryAll(boolean onlyEntity, String startTimeString, String endTimeString, Callback<ReturnWaterOrder> callback) {
        mReturnWaterOrderDao.queryAll(onlyEntity, startTimeString, endTimeString, callback);
    }

    public void statsAmountByTime(boolean onlyEntity, String startTimeString, String endTimeString, Callback<ReturnWaterOrder> callback)
    {
        mReturnWaterOrderDao.statsAmountByTime(onlyEntity, startTimeString, endTimeString, callback);
    }
    @Override
    public RowPager<ReturnWaterOrder> queryScrollPage(PageVo pageVo, long userid, long agentid,long staffid, String systemNo, String refNo, OrderTxStatus txStatus, OrderTxStatus ignoreTxStatus)
    {
        RowPager<ReturnWaterOrder> rs = mReturnWaterOrderDao.queryScrollPageByUser(pageVo, userid, agentid, staffid, systemNo, refNo, txStatus, ignoreTxStatus);
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
    public void clearUserQueryPageCache(long userid)
    {
        String cachekey = BusinessCacheUtils.queryLatestPage_100(userid, mReturnWaterBusinessType);
        CacheManager.getInstance().delete(cachekey);
    }
}

