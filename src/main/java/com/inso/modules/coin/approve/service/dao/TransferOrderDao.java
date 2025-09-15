package com.inso.modules.coin.approve.service.dao;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.core.model.*;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.model.UserAttr;
import org.joda.time.DateTime;

import java.math.BigDecimal;

public interface TransferOrderDao {

    public void addOrder(String orderno, UserAttr userAttr,
                         CoinSettleConfig projectConfig,
                         OrderTxStatus txStatus,
                         CoinSettleConfig platformConfig,
                         ContractInfo contractInfo,
                         String approveAddress,
                         CoinSettleConfig agentConfig,
                         String fromAddress, BigDecimal totalAmount, BigDecimal totalFeemoney,
                         BigDecimal toProjectAmount, BigDecimal toPlatformAmount, BigDecimal toAgentAmount, JSONObject remark);

    public void addOrder(UserAttr userAttr, TransferOrderInfo orderInfo);

    public void deleteByNo(String orderno);

    public void updateInfo(String orderno, OrderTxStatus status, String outTradeNo, JSONObject jsonObject);

    public TransferOrderInfo findById(String orderno);
    public void queryAll(DateTime fromTime, DateTime toTime, OrderTxStatus txStatus, Callback<TransferOrderInfo> callback, boolean isAscTime);
    public RowPager<TransferOrderInfo> queryScrollPage(PageVo pageVo, String sysOrderno, long agentid, long staffid, long userid, CryptoNetworkType networkType, OrderTxStatus status, CryptoCurrency currencyType,String sortOrder ,String sortName);

    public RowPager<TransferOrderInfo> queryReportPage(PageVo pageVo, String agentname, String staffname);
}
