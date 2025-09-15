package com.inso.modules.game.fruit.service.dao;

import java.math.BigDecimal;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.game.fruit.model.FruitBetItemType;
import com.inso.modules.game.fruit.model.FruitOrderInfo;
import com.inso.modules.game.fruit.model.FruitType;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.report.model.GameBusinessDay;


public interface FruitOrderDao {



    public void addOrder(String orderno, String issue, FruitType lotteryType, UserInfo userInfo, UserAttr userAttr, FruitBetItemType betItem, OrderTxStatus txStatus, BigDecimal basicAmount, long betCount, BigDecimal totalBetAmount, BigDecimal feemoney, JSONObject remark);

    public void updateTxStatus(String orderno, OrderTxStatus txStatus, FruitBetItemType openResult, BigDecimal winmoney, BigDecimal feemoney, JSONObject remark);

    public FruitOrderInfo findByNo(String orderno);

    public List<FruitOrderInfo> queryListByUserid(String createtime, long userid, int limit);

    public void queryAllByIssue(String issue, Callback<FruitOrderInfo> callback);

    public RowPager<FruitOrderInfo> queryScrollPage(PageVo pageVo, FruitType lotteryType, long userid, long agentid,long staffid, String systemNo, String issue, OrderTxStatus txStatus);

    public void queryAllMember(String startTimeString, String endTimeString, Callback<FruitOrderInfo> callback);

    public void queryAllMemberByTime(String startTimeString, String endTimeString, Callback<GameBusinessDay> callback);
}
