package com.inso.modules.passport.business.service.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.*;
import com.inso.modules.passport.business.model.BusinessOrder;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import org.joda.time.DateTime;

public interface BusinessOrderDao {

    public void addOrder(FundAccountType accountType, ICurrencyType currencyType, String orderno, String outTradeNo, UserAttr userAttr, BusinessType businessType, OrderTxStatus txStatus, BigDecimal amount, BigDecimal feemoney, Date createtime, RemarkVO remark);

    public void updateTxStatus(String orderno, OrderTxStatus txStatus, String checker, RemarkVO remark);
    public void updateOutTradeNo(String orderno, String tradeNo);

    public BusinessOrder findByNo(String orderno);
    public BusinessOrder findByOutTradeNo(BusinessType businessType, String outTradeNo);

    public List<BusinessOrder> queryByAgent(DateTime fromTime, DateTime toTime, BusinessType[] arr, long agentid, UserInfo.UserType userType, long userid, int offset, int pageSize);

    public List<BusinessOrder> queryScrollPageByUser(PageVo pageVo, long userid, BusinessType businessType, long pageStart, long pageSize);
    public void queryAll(String startTimeString, String endTimeString, BusinessType businessType, Callback<BusinessOrder> callback);
    public RowPager<BusinessOrder> queryScrollPageByUser(PageVo pageVo, long userid, String systemNo, String refNo, BusinessType[] businessTypeArray, ICurrencyType currencyType, OrderTxStatus txStatus, OrderTxStatus ignoreTxStatus,long agentid,long staffid);
}
