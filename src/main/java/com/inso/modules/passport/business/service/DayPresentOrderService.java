package com.inso.modules.passport.business.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.common.model.RemarkVO;
import com.inso.modules.passport.business.model.DayPresentOrder;
import com.inso.modules.passport.business.model.PresentBusinessType;
import com.inso.modules.passport.user.model.UserAttr;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

public interface DayPresentOrderService {

    public String generateOutTradeNo(long userid, PresentBusinessType businessType, String taskid, DateTime creattime);

    public String createOrder(String tradeNo, ICurrencyType currencyType, UserAttr userAttr, PresentBusinessType businessType, BigDecimal amount, RemarkVO remark);
    public void updateTxStatus(String orderno, OrderTxStatus txStatus, long userid, PresentBusinessType businessType, String checker, RemarkVO remark);

    public DayPresentOrder find(boolean purge, String outTradeNo);
    public List<DayPresentOrder> queryByUser(boolean purge, long userid, PresentBusinessType businessType);
    public RowPager<DayPresentOrder> queryScrollPage(PageVo pageVo, long userid, long agentid, String systemNo, OrderTxStatus txStatus);

}
