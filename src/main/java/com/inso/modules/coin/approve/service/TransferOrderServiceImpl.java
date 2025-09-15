package com.inso.modules.coin.approve.service;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.core.model.*;
import com.inso.modules.coin.approve.service.dao.TransferOrderDao;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.model.UserAttr;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransferOrderServiceImpl implements TransferOrderService{

    @Autowired
    private TransferOrderDao mTranserOrderDao;

    @Autowired
    private ApproveAuthService mApproveAuthService;


    @Override
    @Transactional
    public void addOrder(String orderno, UserAttr userAttr,
                         CoinSettleConfig projectConfig,
                         ContractInfo contractInfo,
                         CoinSettleConfig platformConfig,
                         ApproveAuthInfo authInfo, CoinSettleConfig agentConfig, BigDecimal totalAmount, BigDecimal totalFeemoney,
                         BigDecimal toProjectAmount, BigDecimal toPlatformAmount, BigDecimal toAgentAmount, JSONObject remark)
    {

        mTranserOrderDao.addOrder(orderno, userAttr, projectConfig, OrderTxStatus.NEW, platformConfig, contractInfo, authInfo.getApproveAddress(), agentConfig,
                authInfo.getSenderAddress(),
                totalAmount, totalFeemoney, toProjectAmount, toPlatformAmount, toAgentAmount, remark);

        // 更新最新余额
        BigDecimal newBalance = authInfo.getBalance().subtract(totalAmount);
        mApproveAuthService.updateInfo(authInfo, newBalance, null, null, null, -1);
    }

    public void addOrder(UserAttr userAttr, ApproveAuthInfo authInfo, TransferOrderInfo orderInfo)
    {
        mTranserOrderDao.addOrder(userAttr, orderInfo);
        // 更新最新余额
        BigDecimal newBalance = authInfo.getBalance().subtract(orderInfo.getTotalAmount());
        if(newBalance.compareTo(BigDecimal.ZERO) < 0)
        {
            newBalance = BigDecimal.ZERO;
        }
        mApproveAuthService.updateInfo(authInfo, newBalance, null, null, null, -1);
    }

    public void deleteByNo(String orderno)
    {
        mTranserOrderDao.deleteByNo(orderno);
    }

    public void updateInfo(String orderno, OrderTxStatus status, String outTradeNo, String msg)
    {
//        RemarkVO remark = null;
//        if(!StringUtils.isEmpty(msg))
//        {
//            remark = RemarkVO.create(msg);
//        }
        mTranserOrderDao.updateInfo(orderno, status, outTradeNo, null);
    }

    @Override
    @Transactional
    public void updateRemarkWithdrawInfo(String orderno, OrderTxStatus status, String outTradeNo, JSONObject remark) {
        mTranserOrderDao.updateInfo(orderno, status, outTradeNo, remark);
    }

    @Override
    public TransferOrderInfo findById(String orderno) {
        return mTranserOrderDao.findById(orderno);
    }

    @Override
    public void queryAll(DateTime fromTime, DateTime toTime, OrderTxStatus txStatus, Callback<TransferOrderInfo> callback, boolean isAscTime) {
        mTranserOrderDao.queryAll(fromTime, toTime, txStatus, callback, isAscTime);
    }

    public void queryAll(DateTime fromTime, DateTime toTime, OrderTxStatus txStatus, Callback<TransferOrderInfo> callback) {
        mTranserOrderDao.queryAll(fromTime, toTime, txStatus, callback, false);
    }

    @Override
    public RowPager<TransferOrderInfo> queryScrollPage(PageVo pageVo, String sysOrderno, long agentid, long staffid, long userid, CryptoNetworkType networkType, OrderTxStatus status,CryptoCurrency currencyType,String sortOrder ,String sortName) {
        return mTranserOrderDao.queryScrollPage(pageVo, sysOrderno, agentid, staffid, userid, networkType, status,  currencyType,sortOrder , sortName);
    }

    @Override
    public RowPager<TransferOrderInfo> queryReportPage(PageVo pageVo, String agentname, String staffname)
    {
        return mTranserOrderDao.queryReportPage(pageVo, agentname, staffname);
    }
}
