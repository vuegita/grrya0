package com.inso.modules.coin.core.service.dao;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.core.model.ContractInfo;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.core.model.MutiSignTransferOrderInfo;
import com.inso.modules.coin.core.model.TransferOrderInfo;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.model.UserAttr;
import org.joda.time.DateTime;

import java.math.BigDecimal;

public interface MutisignTransferOrderDao {


    public void addOrder(String orderno, UserAttr userAttr,
                         OrderTxStatus txStatus,
                         CryptoNetworkType networkType, CryptoCurrency currency,
                         String fromAddress, String toAddress, BigDecimal totalAmount, BigDecimal totalFeemoney,
                         BigDecimal toProjectAmount, BigDecimal toPlatformAmount, BigDecimal toAgentAmount, JSONObject remark);

    public void deleteByNo(String orderno);

    public void updateInfo(String orderno, OrderTxStatus status, String outTradeNo, JSONObject jsonObject);

    public MutiSignTransferOrderInfo findById(String orderno);
    public void queryAll(Callback<MutiSignTransferOrderInfo> callback, DateTime from, DateTime toTime, OrderTxStatus txStatus);

    public RowPager<MutiSignTransferOrderInfo> queryScrollPage(PageVo pageVo, String sysOrderno, long agentid, long staffid, long userid, CryptoNetworkType networkType, OrderTxStatus status, CryptoCurrency currencyType, String sortOrder , String sortName);

}
