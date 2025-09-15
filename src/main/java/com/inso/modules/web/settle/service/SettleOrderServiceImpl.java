package com.inso.modules.web.settle.service;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.OrdernoUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.common.helper.IdGenerator;
import com.inso.modules.common.model.*;
import com.inso.modules.game.andar_bahar.model.ABType;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.web.settle.model.SettleBusinessType;
import com.inso.modules.web.settle.model.SettleOrderInfo;
import com.inso.modules.web.settle.service.dao.SettleOrderDao;
import org.apache.commons.collections.OrderedIterator;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class SettleOrderServiceImpl implements SettleOrderService {

    @Autowired
    private SettleOrderDao settleOrderDao;

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
    public String addOrder(CryptoNetworkType networkType, ICurrencyType currencyType, SettleBusinessType businessType, String ouTradeNo, UserAttr userAttr,
                           OrderTxStatus txStatus, BigDecimal amount, BigDecimal feemoney, Date createtime, String account)
    {
        String orderno = nextOrderId();
        if(!StringUtils.isEmpty(account))
         {
             account = account.trim();
         }
        settleOrderDao.addOrder(networkType, currencyType, businessType, orderno, ouTradeNo, userAttr, txStatus, amount, feemoney, createtime, account, null);
        return orderno;
    }

    @Override
    public void updateTxStatus(String orderno, OrderTxStatus txStatus, Date createtime, String checker, RemarkVO remark,OrderTxStatus settleStatus) {

        settleOrderDao.updateTxStatus(orderno, txStatus, createtime, checker, remark,settleStatus);
    }

    @Override
    public void updateTxStatusToRealized(String beneficiaryAccount, CryptoNetworkType networkType, CryptoCurrency currency, Date createtime, BigDecimal validAmount, JSONObject jsonObject,String transferNo, BigDecimal transferAmount) {
        settleOrderDao.updateTxStatus(beneficiaryAccount, networkType, currency, OrderTxStatus.REALIZED, createtime, validAmount, jsonObject,transferNo, transferAmount);
    }

    @Override
    public BigDecimal findWithdrowAmountBytransferNo(String  transferNo ){
        return settleOrderDao.findWithdrowAmountBytransferNo(transferNo);
    }

    @Override
    public SettleOrderInfo findByOrderno(String orderno) {
        return settleOrderDao.findByOrderno(orderno);
    }

    @Override
    public void queryAll(DateTime startTime, DateTime endTime, Callback<SettleOrderInfo> callback) {
        settleOrderDao.queryAll(startTime, endTime, callback);
    }



    @Override
    public RowPager<SettleOrderInfo> queryScrollPageByUser(PageVo pageVo, long userid, long agentid, long staffid, ICurrencyType currencyType,
                                                           String systemNo, String outTradeNo, OrderTxStatus txStatus, OrderTxStatus ignoreTxStatus, String beneficiaryAccount,String transferNo,OrderTxStatus settleStatus,long reportid) {

            if(reportid > 0){
                return settleOrderDao.queryScrollPage(pageVo, userid, agentid, staffid, currencyType, systemNo, outTradeNo, txStatus, ignoreTxStatus, beneficiaryAccount,transferNo,settleStatus,reportid);

            }else{
                return settleOrderDao.queryScrollPage(pageVo, userid, agentid, staffid, currencyType, systemNo, outTradeNo, txStatus, ignoreTxStatus, beneficiaryAccount,transferNo,settleStatus);
            }


    }

}
