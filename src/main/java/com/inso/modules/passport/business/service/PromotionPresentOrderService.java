package com.inso.modules.passport.business.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.business.model.PromotionOrderInfo;
import com.inso.modules.passport.user.model.UserAttr;

import java.math.BigDecimal;
import java.util.List;

public interface PromotionPresentOrderService {

    public String addOrder(BigDecimal rate1, BigDecimal rate2, PromotionOrderInfo.SettleMode settleStatus, String tips,
                           UserAttr userAttr, OrderTxStatus txStatus, BigDecimal amount, BigDecimal feemoney);

    public void updateTxStatus(String orderno, long userid, OrderTxStatus txStatus, Status showStatus,
                               BigDecimal rate1, OrderTxStatus limit1TxStatus, BigDecimal rate2, OrderTxStatus limit2TxStatus, String tips);

    public PromotionOrderInfo findByNo(boolean purge, String orderno);

    public List<PromotionOrderInfo> queryScrollPageByUser(boolean purge, long userid);
    public RowPager<PromotionOrderInfo> queryScrollPageByUser(PageVo pageVo, long userid, String systemNo, OrderTxStatus txStatus, long agentid, long staffid);

}
