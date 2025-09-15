package com.inso.modules.game.rg.service.dao;

import java.math.BigDecimal;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.game.rg.model.LotteryOrderInfo;
import com.inso.modules.game.rg.model.LotteryRGType;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.report.model.GameBusinessDay;

public interface LotteryOrderDao {

    public void addOrder(String orderno, String issue, LotteryRGType lotteryType, UserInfo userInfo, UserAttr userAttr, String betItem, OrderTxStatus txStatus, BigDecimal basicAmount, long betCount, BigDecimal amount, BigDecimal feemoney, JSONObject remark);
    public void updateTxStatus(String orderno, OrderTxStatus txStatus, long openResult, BigDecimal winmoney, BigDecimal feemoney, JSONObject remark);

    public LotteryOrderInfo findByNo(String orderno);
    public List<LotteryOrderInfo> queryListByUserid(String createtime, long userid, LotteryRGType rgType, int limit);

    public void queryAllByIssue(String issue, Callback<LotteryOrderInfo> callback);

    public RowPager<LotteryOrderInfo> queryScrollPage(PageVo pageVo, LotteryRGType lotteryType, long userid, long agentid, long staffid, String systemNo, String issue, OrderTxStatus txStatus,String sortName,String sortOrder);

    public void queryAllMember(String startTimeString, String endTimeString, Callback<LotteryOrderInfo> callback);

    public void queryAllMemberByTime(String startTimeString, String endTimeString, Callback<GameBusinessDay> callback);
}
