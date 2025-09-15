package com.inso.modules.coin.core.service;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.core.model.MutiSignTransferOrderInfo;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.model.UserAttr;
import org.joda.time.DateTime;

import java.math.BigDecimal;

public interface MutisignTransferOrderService {


    public void addOrder(String orderno, UserAttr userAttr,
                         CryptoNetworkType networkType, CryptoCurrency currency,
                         String fromAddress, String toAddress, BigDecimal totalAmount, BigDecimal totalFeemoney,
                         BigDecimal toProjectAmount, BigDecimal toPlatformAmount, BigDecimal toAgentAmount, JSONObject remark);

    public void deleteByNo(String orderno);

    public void updateInfo(String orderno, OrderTxStatus status, String outTradeNo, JSONObject jsonObject);

    public void queryAll( DateTime from, DateTime toTime, OrderTxStatus txStatus, Callback<MutiSignTransferOrderInfo> callback);
    public MutiSignTransferOrderInfo findById(String orderno);
    public RowPager<MutiSignTransferOrderInfo> queryScrollPage(PageVo pageVo, String sysOrderno, long agentid, long staffid, long userid, CryptoNetworkType networkType, OrderTxStatus status, CryptoCurrency currencyType, String sortOrder , String sortName);

}
