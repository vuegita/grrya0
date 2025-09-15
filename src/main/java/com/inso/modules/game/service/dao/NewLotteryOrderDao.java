package com.inso.modules.game.service.dao;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.model.NewLotteryOrderInfo;
import com.inso.modules.game.model.NewLotteryPeriodInfo;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.report.model.GameBusinessDay;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

public interface NewLotteryOrderDao {

    public void addOrder(String orderno, String issue, GameChildType lotteryType, UserInfo userInfo, UserAttr userAttr, String betItem, OrderTxStatus txStatus,
                         BigDecimal amount, int totalBetCount, BigDecimal singleBetAmount, BigDecimal feemoney, JSONObject remark);
    public void updateTxStatus(GameChildType lotteryType, String orderno, OrderTxStatus txStatus, String openResult,
                               BigDecimal winmoney, String betItem, BigDecimal feemoney, JSONObject remark, NewLotteryPeriodInfo periodInfo);

    public NewLotteryOrderInfo findByNo(GameChildType lotteryType, String orderno);

    public void updateCashoutItem(GameChildType lotteryType, String orderno, String betItem);
    public NewLotteryOrderInfo findByIssueAndUser(GameChildType lotteryType, String issue, long userid);

    public List<NewLotteryOrderInfo> queryListByUserid(DateTime fromTime, long userid, GameChildType rgType, int limit);

    public void queryAllByIssue(GameChildType lotteryType, String issue, Callback<NewLotteryOrderInfo> callback);
    public void queryAllByTime(GameChildType lotteryType, DateTime from, DateTime toTime, OrderTxStatus txStatus, Callback<NewLotteryOrderInfo> callback);
    public void queryAllPendingByTime(GameChildType lotteryType, DateTime from, DateTime toTime, Callback<NewLotteryOrderInfo> callback);

    public void statsAllByTime(GameChildType lotteryType, DateTime from, DateTime toTime, Callback<NewLotteryOrderInfo> callback);
    public void statsAllMemberByTime(GameChildType lotteryType, DateTime from, DateTime toTime, Callback<GameBusinessDay> callback);

    public RowPager<NewLotteryOrderInfo> queryScrollPage(PageVo pageVo, GameChildType lotteryType, long userid, long agentid, long staffid, GameChildType tbGameType, String systemNo, String issue, OrderTxStatus txStatus, String sortName, String sortOrder);

    public void queryAllMember(GameChildType lotteryType, String startTimeString, String endTimeString, Callback<NewLotteryOrderInfo> callback);

    public void queryAllMemberByTime(GameChildType lotteryType, String startTimeString, String endTimeString, Callback<GameBusinessDay> callback);
}
