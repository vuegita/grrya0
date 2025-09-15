package com.inso.modules.web.settle.service.dao;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.common.model.*;
import com.inso.modules.passport.business.model.WithdrawOrder;
import com.inso.modules.passport.business.service.dao.WithdrawOrderDao;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.web.settle.model.SettleBusinessType;
import com.inso.modules.web.settle.model.SettleOrderInfo;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

public interface SettleOrderDao  {


    public void addOrder(CryptoNetworkType networkType, ICurrencyType currencyType, SettleBusinessType businessType,
                         String orderno, String ouTradeNo, UserAttr userAttr, OrderTxStatus txStatus,
                         BigDecimal amount, BigDecimal feemoney, Date createtime , String account, JSONObject remark);

    public void updateTxStatus(String orderno, OrderTxStatus txStatus, Date createtime, String checker, RemarkVO remark,OrderTxStatus settleStatus);
    public void updateTxStatus(String beneficiaryAccount, CryptoNetworkType networkType, CryptoCurrency currency, OrderTxStatus txStatus, Date createtime, BigDecimal validAmount, JSONObject jsonObject,String transferNo, BigDecimal transferAmount);

    public BigDecimal findWithdrowAmountBytransferNo(String  transferNo );

    public SettleOrderInfo findByOrderno(String orderno);

    public void queryAll(DateTime startTime, DateTime endTime, Callback<SettleOrderInfo> callback);
    public RowPager<SettleOrderInfo> queryScrollPage(PageVo pageVo, long userid, long agentid, long staffid, ICurrencyType currencyType,
                                                     String systemNo, String outTradeNo, OrderTxStatus txStatus, OrderTxStatus ignoreTxStatus ,
                                                     String beneficiaryAccount,String transferNo,OrderTxStatus settleStatus);

    public RowPager<SettleOrderInfo> queryScrollPage(PageVo pageVo, long userid, long agentid, long staffid, ICurrencyType currencyType,
                                                     String systemNo, String outTradeNo, OrderTxStatus txStatus, OrderTxStatus ignoreTxStatus ,
                                                     String beneficiaryAccount,String transferNo,OrderTxStatus settleStatus,long reportid);

}
