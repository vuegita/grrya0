package com.inso.modules.passport.returnwater.service;

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

public interface ReturnFirstRechargeUpOrderService {

    public String createOrder(int level, String outTradeNo, UserInfo userInfo, UserAttr userAttr, FundAccountType accountType, ICurrencyType currencyType, BigDecimal amount, Date createtime, RemarkVO remark);

    public void updateTxStatus(int level, String orderno, OrderTxStatus txStatus, String checker, RemarkVO remark);

    public ReturnWaterOrder findByNo(String orderno);
    public ReturnWaterOrder findByOutTradeNo(String outTradeNo);

    public List<ReturnWaterOrder> queryScrollPageByUser(PageVo pageVo, long userid, OrderTxStatus[] txStatus);
    public void queryAll(boolean onlyEntity, String startTimeString, String endTimeString, Callback<ReturnWaterOrder> callback);
    public void statsAmountByTime(boolean onlyEntity, String startTimeString, String endTimeString, Callback<ReturnWaterOrder> callback);

    public RowPager<ReturnWaterOrder> queryScrollPage(PageVo pageVo, long userid, long agentid,long staffid, String systemNo, String refNo, OrderTxStatus txStatus, OrderTxStatus ignoreTxStatus);


    /**
     * 清除用户page缓存
     * 充值操作-提现操作
     * @param userid
     */
    public void clearUserQueryPageCache(long userid);
}
