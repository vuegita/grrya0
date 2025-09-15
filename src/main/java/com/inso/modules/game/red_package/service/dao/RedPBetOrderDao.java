package com.inso.modules.game.red_package.service.dao;

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

public interface RedPBetOrderDao {

    public void addOrder(String orderno, long rpid, RedPType lotteryType, UserInfo userInfo, long agentid, String betItem, OrderTxStatus txStatus, BigDecimal basicAmount, long betCount, BigDecimal amount, BigDecimal feemoney, JSONObject remark);
    public void updateTxStatus(String orderno, OrderTxStatus txStatus, long openResult, BigDecimal winmoney, BigDecimal feemoney, JSONObject remark);

    public RedPBetOrderInfo findByNo(String orderno);
    public List<RedPBetOrderInfo> queryListByUserid(RedPType type, String createtime, long userid, int limit);

    public void queryAllByIssue(long rpid, Callback<RedPBetOrderInfo> callback);

    public RowPager<RedPBetOrderInfo> queryScrollPage(PageVo pageVo, RedPType lotteryType, long userid, String systemNo, long rpid, OrderTxStatus txStatus);

    public void queryAllMember(String startTimeString, String endTimeString, Callback<RedPBetOrderInfo> callback);
}
