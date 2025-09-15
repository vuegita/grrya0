package com.inso.modules.passport.business.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.common.model.RemarkVO;
import com.inso.modules.passport.business.model.RechargeOrder;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.paychannel.model.ChannelInfo;
import com.inso.modules.paychannel.model.PayProductType;

public interface RechargeOrderService {

    public String createOrder(UserInfo userInfo, UserAttr userAttr, ChannelInfo channelInfo, PayProductType productType, BigDecimal amount, Date createtime, RemarkVO remark);

    public void updateTxStatus(String orderno, OrderTxStatus txStatus, String outTradeNo, String checker, RemarkVO remark);

    public void updateAmount(String orerno, BigDecimal amount);
//    /**
//     * 更新外部订单号， 注意只作用
//     * @param orderno
//     * @param txStatus
//     * @param tradeNo
//     */
//    public void updateOutTradeNo(String orderno, OrderTxStatus txStatus, String tradeNo);

    public RechargeOrder findByNo(String orderno);
    public RechargeOrder findByOutTradeNo(String outTradeNo);

    public List<RechargeOrder> queryScrollPageByUser(PageVo pageVo, long userid, boolean isWaiting);
    public void queryAll(String startTimeString, String endTimeString, Callback<RechargeOrder> callback);
    public RowPager<RechargeOrder> queryScrollPage(PageVo pageVo, long userid, long agentid,long staffid, String systemNo, String refNo, OrderTxStatus txStatus, OrderTxStatus ignoreTxStatus, long channelid);

    public RowPager<RechargeOrder> queryScrollPageByUserOrderBy(PageVo pageVo, long userid, long agentid,long staffid, String systemNo, String refNo, OrderTxStatus txStatus, OrderTxStatus ignoreTxStatus,String sortName,String sortOrder, long channelid);
    /**
     * 首次充值列表
     * @param pageVo
     * @param userid
     * @return
     */
    public RowPager<RechargeOrder> queryFirstRechargeScrollPage(PageVo pageVo, long userid, long agentid,long staffid);

    /**
     * 清除用户page缓存
     * 充值操作-提现操作
     * @param userid
     */
    public void clearUserQueryPageCache(long userid, boolean isWaiting);
}
