package com.inso.modules.passport.returnwater.service.dao;

import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.common.model.RemarkVO;
import com.inso.modules.passport.business.model.ReturnWaterOrder;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface ReturnFirstRechargeUpOrderDao {

    public void addOrder(int level, String orderno, String outTradeNo, UserInfo userInfo, UserAttr userAttr, FundAccountType accountType, ICurrencyType currencyType, OrderTxStatus txStatus, BigDecimal amount, Date createtime, RemarkVO remark);

    public void updateTxStatus(String orderno, OrderTxStatus txStatus, String checker, RemarkVO remark);

    public ReturnWaterOrder findByNo(String orderno);
    public ReturnWaterOrder findByOutTradeNo(String outTradeNo);

    public List<ReturnWaterOrder> queryScrollPageByUser(PageVo pageVo, long userid);
    public void queryAll(boolean onlyEntity, String startTimeString, String endTimeString, Callback<ReturnWaterOrder> callback);
    public void statsAmountByTime(boolean onlyEntity, String startTimeString, String endTimeString, Callback<ReturnWaterOrder> callback);

    public RowPager<ReturnWaterOrder> queryScrollPageByUser(PageVo pageVo, long userid, long agentid,long staffid, String systemNo, String refNo, OrderTxStatus txStatus, OrderTxStatus ignoreTxStatus);
}
