package com.inso.modules.passport.business.service.dao;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.common.model.RemarkVO;
import com.inso.modules.passport.business.model.DayPresentOrder;
import com.inso.modules.passport.business.model.PresentBusinessType;
import com.inso.modules.passport.user.model.UserAttr;

import java.math.BigDecimal;
import java.util.List;

public interface DayPresentOrderDao {

    public void addOrder(String tradeNo, ICurrencyType currencyType, String orderno, UserAttr userAttr, PresentBusinessType businessType, OrderTxStatus txStatus, BigDecimal amount, BigDecimal feemoney, RemarkVO remark);
    public void updateTxStatus(String orderno, OrderTxStatus txStatus, String checker, RemarkVO remark);

    public DayPresentOrder find(String outTradeNo);
    public List<DayPresentOrder> queryByOutTradeNo(String prefixOutTradeno);
    public RowPager<DayPresentOrder> queryScrollPage(PageVo pageVo, long userid, long agentid, String systemNo, OrderTxStatus txStatus);

}
