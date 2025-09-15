package com.inso.modules.game.andar_bahar.service.dao;

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

public interface ABPeriodDao {

    public void add(ABType type, String issue, long gameid, GameOpenMode mode, Date startTime, Date endTime);

    public void updateAmount(String issue, BigDecimal betAmount, BigDecimal winAmount, BigDecimal feeAmount, long betCount, long winCount);
    public void updateStatus(String issue, GamePeriodStatus status);

    public void updateOpenResult(String issue, ABBetItemType openResult, GameOpenMode mode);
    public void updateOpenMode(String issue, GameOpenMode mode);

    public void queryAll(ABType type, String startTimeString, String endTimeString, Callback<ABPeriodInfo> callback);

    public RowPager<ABPeriodInfo> queryScrollPage(PageVo pageVo, String issue, ABType type, GamePeriodStatus status);
    public ABPeriodInfo findByTime(String time, ABType type);
    public ABPeriodInfo findByIssue(String issue);
    public List<ABPeriodInfo> queryByTime(ABType lotteryType, String beginTime, String endTime, int limit);
    public long count(ABType type, String startTimeString, String endTimeString);
}
