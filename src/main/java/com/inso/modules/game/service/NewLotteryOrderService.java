package com.inso.modules.game.service;

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
import com.inso.modules.report.GameBusinessStatsService;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

public interface NewLotteryOrderService extends GameBusinessStatsService{

    public void addOrder(String orderno, String issue, GameChildType lotteryType, UserInfo userInfo, UserAttr userAttr, String betItem, BigDecimal amount, int totalBetCount, BigDecimal singleBetAmount, BigDecimal feemoney, JSONObject remark);
//    public void updateTxStatus(String orderno, OrderTxStatus txStatus, long openResult, BigDecimal winmoney, JSONObject remark);

    /**
     *
     * @param lotteryType
     * @param orderno
     * @param txStatus
     * @param betItem  正常不能修改，除非是类似Football这样的游戏
     */
    public void updateTxStatus(long userid, GameChildType lotteryType, String orderno, OrderTxStatus txStatus, String betItem);
    public void updateTxStatusToRealized(long userid, GameChildType lotteryType, String orderno, String openResult, BigDecimal winmoney, NewLotteryPeriodInfo periodInfo, String betItem);
    public void updateTxStatusToFailed(long userid, GameChildType lotteryType, String orderno, String openResult, NewLotteryPeriodInfo periodInfo);

    public NewLotteryOrderInfo findByNo(GameChildType lotteryType, String orderno);
    public void updateCashoutItem(GameChildType lotteryType, String orderno, String betItem);
//    public NewLotteryOrderInfo findByIssueAndUser(boolean purge, GameChildType lotteryType, String issue, long userid);

    public List<NewLotteryOrderInfo> queryListByUserid(boolean purge, DateTime fromTime, long userid, GameChildType rgType, int offset);

    public void queryAllByIssue(GameChildType lotteryType, String issue, Callback<NewLotteryOrderInfo> callback);
    public void queryAllByTime(GameChildType lotteryType, DateTime from, DateTime toTime, OrderTxStatus txStatus, Callback<NewLotteryOrderInfo> callback);
    public void queryAllPendingByTime(GameChildType lotteryType, DateTime from, DateTime toTime, Callback<NewLotteryOrderInfo> callback);

    public void statsAllByTime(GameChildType lotteryType, DateTime from, DateTime toTime, Callback<NewLotteryOrderInfo> callback);;

    public RowPager<NewLotteryOrderInfo> queryScrollPage(PageVo pageVo, GameChildType lotteryType, long userid, long agentid, long staffid, GameChildType tbGameType, String systemNo, String issue, OrderTxStatus txStatus, String sortName, String sortOrder);
    public void queryAllMember(GameChildType lotteryType, String startTimeString, String endTimeString, Callback<NewLotteryOrderInfo> callback);
}
