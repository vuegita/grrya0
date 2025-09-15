package com.inso.modules.coin.withdraw.service;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.withdraw.model.CoinWithdrawChannel;
import com.inso.modules.coin.withdraw.model.CoinWithdrawOrderInfo;
import com.inso.modules.coin.withdraw.service.dao.CoinWithdrawOrderDao;
import com.inso.modules.common.helper.IdGenerator;
import com.inso.modules.common.model.BusinessType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.model.UserAttr;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CoinWithdrawOrderServiceImpl implements CoinWithdrawOrderService {

    @Autowired
    private CoinWithdrawOrderDao mCoinWithdrawOrderDao;

    private static IdGenerator mIdGenerator = IdGenerator.newSingleWorder();


    /**
     * 生成订单号
     * @return
     */
    public static String nextOrderId(BusinessType businessType)
    {
        return mIdGenerator.nextId(businessType.getCode());
    }

    @Override
    public String addOrder(UserAttr userAttr, CoinWithdrawChannel channelInfo, OrderTxStatus txStatus, BusinessType businessType, CryptoCurrency currency,
                         String toAddress, BigDecimal amount, BigDecimal feemoney)
    {
        String orderno = nextOrderId(businessType);
        mCoinWithdrawOrderDao.addOrder(orderno, userAttr, channelInfo, OrderTxStatus.NEW, businessType, currency, toAddress, amount, feemoney);
        return orderno;
    }

    @Override
    public void deleteByNo(String orderno) {
        mCoinWithdrawOrderDao.deleteByNo(orderno);
    }

    @Override
    public void updateInfo(String orderno, OrderTxStatus status, String outTradeNo, JSONObject jsonObject) {
        mCoinWithdrawOrderDao.updateInfo(orderno, status, outTradeNo, jsonObject);
    }

    @Override
    public CoinWithdrawOrderInfo findById(String orderno) {
        return mCoinWithdrawOrderDao.findById(orderno);
    }

    @Override
    public void queryAll(DateTime fromTime, DateTime toTime, OrderTxStatus txStatus, Callback<CoinWithdrawOrderInfo> callback) {
        mCoinWithdrawOrderDao.queryAll(fromTime, toTime, txStatus, callback);
    }

    @Override
    public RowPager<CoinWithdrawOrderInfo> queryScrollPage(PageVo pageVo, String sysOrderno, long agentid, long staffid, long userid, CryptoNetworkType networkType, OrderTxStatus status) {
        return mCoinWithdrawOrderDao.queryScrollPage(pageVo, sysOrderno, agentid, staffid, userid, networkType, status);
    }
}
