package com.inso.modules.passport.business.service;

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

public interface BusinessOrderService {

    public String createOrder(FundAccountType accountType, ICurrencyType currencyType, String outTradeNo, UserAttr userAttr, BusinessType businessType, BigDecimal amount, BigDecimal feemoney, Date createtime, RemarkVO remark);
    public String createOrder(FundAccountType accountType, ICurrencyType currencyType, UserAttr userAttr, BusinessType businessType, BigDecimal amount, Date createtime, RemarkVO remark);

    public void updateTxStatus(String orderno, OrderTxStatus txStatus, String checker, RemarkVO remark);
    public void updateOutTradeNo(String orderno, String tradeNo);

    public BusinessOrder findByNo(String orderno);
    public BusinessOrder findByOutTradeNo(BusinessType businessType, String outTradeNo);

    public List<BusinessOrder> queryByAgent(boolean purge, long agentid, UserInfo.UserType userType, long userid, int offset);

    public List<BusinessOrder> queryScrollPageByUser(PageVo pageVo, long userid, BusinessType businessType, OrderTxStatus[] txStatus);
    public void queryAll(String startTimeString, String endTimeString, BusinessType businessType, Callback<BusinessOrder> callback);
    public RowPager<BusinessOrder> queryScrollPage(PageVo pageVo, long userid, String systemNo, String refNo, BusinessType[] businessTypeArray, ICurrencyType currencyType, OrderTxStatus txStatus, OrderTxStatus ignoreTxStatus,long agentid,long staffid);


    /**
     * 清除用户page缓存
     * 充值操作-提现操作
     * @param userid
     * @param businessType
     */
    public void clearUserQueryPageCache(long userid, BusinessType businessType);
}
