package com.inso.modules.game.andar_bahar.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.game.andar_bahar.model.ABBetItemType;
import com.inso.modules.game.andar_bahar.model.ABPeriodInfo;
import com.inso.modules.game.andar_bahar.model.ABType;
import com.inso.modules.game.model.GameOpenMode;
import com.inso.modules.game.model.GamePeriodStatus;

public interface ABPeriodService {

    public void add(ABType type, String issue, long gameid, GameOpenMode mode, Date startTime, Date endTime);
    public void updateStatusToWaiting(String issue);
    public void updateStatusToFinish(ABPeriodInfo periodInfo, ABBetItemType openResult);
    public void updateOpenResult(ABPeriodInfo periodInfo, ABBetItemType openResult);

    public void updateOpenMode(String issue, GameOpenMode mode);

    public void updateAmount(String issue, BigDecimal betAmount, BigDecimal winAmount, BigDecimal feeAmount, long betCount, long winCount);

    public void queryAll(ABType type, String startTimeString, String endTimeString, Callback<ABPeriodInfo> callback);

    public RowPager<ABPeriodInfo> queryScrollPage(PageVo pageVo, String issue, ABType type, GamePeriodStatus status);
    public ABPeriodInfo findCurrentRunning(ABType type);
//    public ABPeriodInfo findByTime(String time, LotteryType type);
    public ABPeriodInfo findByIssue(boolean purge, String issue);
    public List<ABPeriodInfo> queryByTime(ABType lotteryType, String beginTime, String endTime, int limit);
    public long count(ABType type, String startTimeString, String endTimeString);
}
