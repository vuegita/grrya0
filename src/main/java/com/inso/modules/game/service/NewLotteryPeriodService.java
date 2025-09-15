package com.inso.modules.game.service;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.model.GameOpenMode;
import com.inso.modules.game.model.GamePeriodStatus;
import com.inso.modules.game.model.NewLotteryPeriodInfo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface NewLotteryPeriodService {

    public void add(String showIssue, GameChildType type, String issue, long gameid, GameOpenMode mode, Date startTime, Date endTime);

    public void updateStatusToWaiting(NewLotteryPeriodInfo periodInfo, String issue);
    public void updateStatusToFinish(NewLotteryPeriodInfo periodInfo, String openResult, String reference);
    public void updateOpenResult(NewLotteryPeriodInfo periodInfo, String openResult);
    public void updateReference(NewLotteryPeriodInfo periodInfo, String reference, JSONObject jsonObject);

    public void updateOpenMode(GameChildType type, String issue, GameOpenMode mode);

    public void updateAmount(GameChildType type, String issue, BigDecimal betAmount, BigDecimal winAmount, BigDecimal feeAmount, long betCount, long winCount, BigDecimal winAmount2);

    public void queryAll2(GameChildType type, String startTimeString, String endTimeString, GameChildType whereType, Callback<NewLotteryPeriodInfo> callback);

    public RowPager<NewLotteryPeriodInfo> queryScrollPage(PageVo pageVo, String issue, GameChildType type, GamePeriodStatus status);
    public NewLotteryPeriodInfo findCurrentRunning(GameChildType type);
    public NewLotteryPeriodInfo findByIssue(boolean purge, GameChildType type, String issue);
    public List<NewLotteryPeriodInfo> queryByTime(GameChildType lotteryType, String beginTime, String endTime, int limit);
    public long count(GameChildType type, String startTimeString, String endTimeString);
}
