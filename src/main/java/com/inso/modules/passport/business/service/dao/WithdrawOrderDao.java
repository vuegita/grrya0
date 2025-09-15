package com.inso.modules.passport.business.service.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.*;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.business.model.WithdrawOrder;
import com.inso.modules.paychannel.model.ChannelInfo;
import com.inso.modules.paychannel.model.PayProductType;
import org.joda.time.DateTime;

public interface WithdrawOrderDao {

    public void addOrder(FundAccountType accountType, ICurrencyType currencyType, String orderno, UserInfo userInfo, UserAttr userAttr, OrderTxStatus txStatus, PayProductType productType, BigDecimal amount, BigDecimal feemoney, Date createtime, RemarkVO remark , String account, String idcard, ChannelInfo channelInfo);

    public void updateTxStatus(String orderno, OrderTxStatus txStatus, String outTradeNo, String checker, RemarkVO remark, long submitCount);
    public void updateOutTradeNo(String orderno, String tradeNo);

    public WithdrawOrder findByNo(String orderno);
    public WithdrawOrder findByOutTradeNo(String outTradeNo);

    public List<WithdrawOrder> queryScrollPageByUser(PageVo pageVo, long userid, OrderTxStatus[] txStatusArray, long offset, long pagesize);
    public void queryAll(String startTimeString, String endTimeString, Callback<WithdrawOrder> callback);
    public void queryAllByUpdateTime(DateTime startTime, DateTime enTime, Callback<WithdrawOrder> callback);

    public RowPager<WithdrawOrder> queryScrollPageByUser(PageVo pageVo, long userid, long agentid,long staffid, String systemNo, String refNo, OrderTxStatus txStatus, OrderTxStatus ignoreTxStatus,String beneficiaryAccount,String beneficiaryIdcard);

    public WithdrawOrder queryTotalWithdrawAmountScrollPage(PageVo pageVo, long userid, CryptoCurrency currencyType , OrderTxStatus txStatus);
}
