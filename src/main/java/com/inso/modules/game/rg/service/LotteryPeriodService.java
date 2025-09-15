package com.inso.modules.game.rg.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.game.model.GameOpenMode;
import com.inso.modules.game.model.GamePeriodStatus;
import com.inso.modules.game.rg.model.LotteryPeriodInfo;
import com.inso.modules.game.rg.model.LotteryRGType;

public interface LotteryPeriodService {

    public void add(LotteryRGType type, String issue, long gameid, GameOpenMode mode, Date startTime, Date endTime);
    public void updateStatusToWaiting(String issue);
    public void updateStatusToFinish(LotteryPeriodInfo periodInfo, long openResult);
    public void updateOpenResult(LotteryPeriodInfo periodInfo, long openResult);
    public void updateOpenMode(String issue, GameOpenMode mode);

    public void updateAmount(String issue, BigDecimal betAmount, BigDecimal winAmount, BigDecimal feeAmount, long betCount, long winCount);

    public void queryAll(LotteryRGType type, String startTimeString, String endTimeString, Callback<LotteryPeriodInfo> callback);

    public RowPager<LotteryPeriodInfo> queryScrollPage(PageVo pageVo, String issue, LotteryRGType type, GamePeriodStatus status);
    public LotteryPeriodInfo findCurrentRunning(LotteryRGType type);
//    public LotteryPeriodInfo findByTime(String time, LotteryType type);
    public LotteryPeriodInfo findByIssue(boolean purge, String issue);
    public List<LotteryPeriodInfo> queryByTime(LotteryRGType lotteryType, String beginTime, String endTime, int limit);
    public long count(LotteryRGType type, String startTimeString, String endTimeString);
}
