package com.inso.modules.web.settle.service;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.common.model.*;
import com.inso.modules.passport.business.model.WithdrawOrder;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.web.settle.model.SettleBusinessType;
import com.inso.modules.web.settle.model.SettleOrderInfo;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.Date;

public interface SettleOrderService {


    public String addOrder(CryptoNetworkType networkType, ICurrencyType currencyType, SettleBusinessType businessType,
                           String ouTradeNo, UserAttr userAttr, OrderTxStatus txStatus,
                           BigDecimal amount, BigDecimal feemoney, Date createtime , String account);

    public void updateTxStatus(String orderno, OrderTxStatus txStatus, Date createtime, String checker, RemarkVO remark,OrderTxStatus settleStatus);
    public void updateTxStatusToRealized(String beneficiaryAccount, CryptoNetworkType networkType, CryptoCurrency currency, Date createtime, BigDecimal validAmount, JSONObject jsonObject,String transferNo, BigDecimal transferAmount);

    public BigDecimal findWithdrowAmountBytransferNo(String  transferNo);

    public SettleOrderInfo findByOrderno(String orderno);

    public void queryAll(DateTime startTime, DateTime endTime, Callback<SettleOrderInfo> callback);


    public RowPager<SettleOrderInfo> queryScrollPageByUser(PageVo pageVo, long userid, long agentid,long staffid, ICurrencyType currencyType,
                                                           String systemNo, String outTradeNo, OrderTxStatus txStatus, OrderTxStatus ignoreTxStatus ,
                                                           String beneficiaryAccount,String transferNo,OrderTxStatus settleStatus,long reportid);

}
