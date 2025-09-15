package com.inso.modules.passport.business.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.helper.IdGenerator;
import com.inso.modules.common.model.BusinessType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.common.model.RemarkVO;
import com.inso.modules.passport.business.cache.AgentWalletCacheUtils;
import com.inso.modules.passport.business.model.AgentWalletOrderInfo;
import com.inso.modules.passport.business.model.WithdrawOrder;
import com.inso.modules.passport.business.service.dao.AgentWalletOrderDao;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.paychannel.model.ChannelInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AgentWalletOrderServiceImpl implements AgentWalletOrderService{

    @Autowired
    private AgentWalletOrderDao mAgentWalletOrderDao;

    private IdGenerator mIdGenerateor = IdGenerator.newSingleWorder();

    @Override
    public String addOrder(BusinessType businessType, String outTradeNo, UserInfo userInfo, ChannelInfo channelInfo,
                         BigDecimal amount, BigDecimal feemoney, ICurrencyType currencyType, BigDecimal realAmount)
    {
        String orderno = mIdGenerateor.nextId();
        mAgentWalletOrderDao.addOrder(orderno, businessType, outTradeNo, userInfo, channelInfo, currencyType, amount, feemoney, OrderTxStatus.NEW, realAmount, null);
        return orderno;
    }

    @Override
    public void updateTxStatus(String orderno, OrderTxStatus txStatus, String checker, RemarkVO remark, String outTradeNo, BusinessType businessType) {
        mAgentWalletOrderDao.updateTxStatus(orderno, txStatus, checker, remark);

        String cachekey = AgentWalletCacheUtils.findByOutTradeNo(outTradeNo, businessType);
        CacheManager.getInstance().delete(cachekey);
    }

    @Override
    public AgentWalletOrderInfo findByOutTradeNo(boolean purge, String outTradeNo, BusinessType businessType) {
        String cachekey = AgentWalletCacheUtils.findByOutTradeNo(outTradeNo, businessType);
        AgentWalletOrderInfo orderInfo = CacheManager.getInstance().getObject(cachekey, AgentWalletOrderInfo.class);
        if(purge || orderInfo == null)
        {
            orderInfo = mAgentWalletOrderDao.findByOutTradeNo(outTradeNo, businessType);
            if(orderInfo != null)
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(orderInfo));
            }
        }
        return orderInfo;
    }

    @Override
    public void queryAll(String startTimeString, String endTimeString, Callback<AgentWalletOrderInfo> callback) {
        mAgentWalletOrderDao.queryAll(startTimeString, endTimeString, callback);
    }

    @Override
    public RowPager<AgentWalletOrderInfo> queryScrollPage(PageVo pageVo, long userid, String systemNo, String outTradeNo, OrderTxStatus txStatus, BusinessType businessType) {
        return mAgentWalletOrderDao.queryScrollPage(pageVo, userid, systemNo, outTradeNo, txStatus, businessType);
    }
}
