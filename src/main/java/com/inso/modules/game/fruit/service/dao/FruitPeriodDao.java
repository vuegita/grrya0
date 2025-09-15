package com.inso.modules.game.fruit.service.dao;


import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.game.fruit.model.FruitBetItemType;
import com.inso.modules.game.fruit.model.FruitPeriodInfo;
import com.inso.modules.game.fruit.model.FruitType;
import com.inso.modules.game.model.GameOpenMode;
import com.inso.modules.game.model.GamePeriodStatus;

public interface FruitPeriodDao {


    public void add(FruitType type, String issue, long gameid, GameOpenMode mode, Date startTime, Date endTime);

    public void updateAmount(String issue, BigDecimal betAmount, BigDecimal winAmount, BigDecimal feeAmount, long betCount, long winCount);


    public void updateStatus(String issue, GamePeriodStatus status);


    public void updateOpenResult(String issue, FruitBetItemType openResult, GameOpenMode mode);


    public void updateOpenMode(String issue, GameOpenMode mode);


    public FruitPeriodInfo findByTime(String time, FruitType type);


    public FruitPeriodInfo findByIssue(String issue);

    public List<FruitPeriodInfo> queryByTime(FruitType type, String beginTime, String endTime, int limit);


    public long count(FruitType type, String startTimeString, String endTimeString);


    public void queryAll(FruitType type, String startTimeString, String endTimeString, Callback<FruitPeriodInfo> callback);


    public RowPager<FruitPeriodInfo> queryScrollPage(PageVo pageVo, String issue, FruitType type, GamePeriodStatus status);

}
