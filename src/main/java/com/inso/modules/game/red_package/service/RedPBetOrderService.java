package com.inso.modules.game.red_package.service;

import java.math.BigDecimal;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.game.red_package.model.RedPBetOrderInfo;
import com.inso.modules.game.red_package.model.RedPType;
import com.inso.modules.passport.user.model.UserInfo;

public interface RedPBetOrderService {

    public void addOrder(String orderno, long rpid, RedPType lotteryType, UserInfo userInfo, long agentid, String betItem, BigDecimal basicAmount, long betCount, BigDecimal amount, BigDecimal feemoney, JSONObject remark);
//    public void updateTxStatus(String orderno, OrderTxStatus txStatus, long openResult, BigDecimal winmoney, JSONObject remark);
    public void updateTxStatus(String orderno, OrderTxStatus txStatus);
    public void updateTxStatusToRealized(String orderno, long openResult, BigDecimal winmoney);
    public void updateTxStatusToFailed(String orderno, long openResult);

    public RedPBetOrderInfo findByNo(String orderno);
    public List<RedPBetOrderInfo> queryListByUserid(boolean purge, RedPType type, long userid, int offset);

    public void queryAllByIssue(long rpid, Callback<RedPBetOrderInfo> callback);

    public RowPager<RedPBetOrderInfo> queryScrollPage(PageVo pageVo, RedPType lotteryType, long userid, String systemNo, long rpid, OrderTxStatus txStatus);
    public void queryAllMember(String startTimeString, String endTimeString, Callback<RedPBetOrderInfo> callback);
}
