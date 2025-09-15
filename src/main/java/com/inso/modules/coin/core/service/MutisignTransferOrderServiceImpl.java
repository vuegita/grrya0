package com.inso.modules.coin.core.service;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.core.model.ContractInfo;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.core.model.MutiSignTransferOrderInfo;
import com.inso.modules.coin.core.model.TransferOrderInfo;
import com.inso.modules.coin.core.service.dao.MutisignTransferOrderDao;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.model.UserAttr;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class MutisignTransferOrderServiceImpl  implements MutisignTransferOrderService{

    @Autowired
    private MutisignTransferOrderDao mutisignTransferOrderDao;

    @Override
    public void addOrder(String orderno, UserAttr userAttr,  CryptoNetworkType networkType, CryptoCurrency currency,
                         String fromAddress, String toAddress, BigDecimal totalAmount, BigDecimal totalFeemoney,
                         BigDecimal toProjectAmount, BigDecimal toPlatformAmount, BigDecimal toAgentAmount, JSONObject remark) {
        mutisignTransferOrderDao.addOrder(orderno, userAttr, OrderTxStatus.NEW, networkType, currency, fromAddress, toAddress, totalAmount, totalFeemoney, toProjectAmount, toPlatformAmount, toAgentAmount, remark);
    }

    @Override
    public void deleteByNo(String orderno) {
        mutisignTransferOrderDao.deleteByNo(orderno);
    }

    @Override
    public void updateInfo(String orderno, OrderTxStatus status, String outTradeNo, JSONObject jsonObject) {
        mutisignTransferOrderDao.updateInfo(orderno, status, outTradeNo, jsonObject);
    }

    @Override
    public void queryAll(DateTime from, DateTime toTime, OrderTxStatus txStatus, Callback<MutiSignTransferOrderInfo> callback)
    {
        mutisignTransferOrderDao.queryAll(callback, from, toTime, txStatus);
    }

    @Override
    public MutiSignTransferOrderInfo findById(String orderno) {
        return mutisignTransferOrderDao.findById(orderno);
    }

    @Override
    public RowPager<MutiSignTransferOrderInfo> queryScrollPage(PageVo pageVo, String sysOrderno, long agentid, long staffid, long userid, CryptoNetworkType networkType, OrderTxStatus status, CryptoCurrency currencyType, String sortOrder, String sortName) {
        return mutisignTransferOrderDao.queryScrollPage(pageVo, sysOrderno, agentid, staffid, userid, networkType, status, currencyType, sortOrder, sortName);
    }
}
