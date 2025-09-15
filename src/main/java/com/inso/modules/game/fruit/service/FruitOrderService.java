package com.inso.modules.game.fruit.service;

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
import com.inso.modules.report.GameBusinessStatsService;

public interface FruitOrderService extends GameBusinessStatsService {





    public void addOrder(String orderno, String issue, FruitType lotteryType, UserInfo userInfo, UserAttr userAttr, FruitBetItemType betItem, BigDecimal basicAmount, long betCount, BigDecimal amount, BigDecimal feemoney, JSONObject remark) ;
    public void updateTxStatus(String orderno, OrderTxStatus txStatus, FruitBetItemType openResult, BigDecimal winmoney, JSONObject remark) ;
    public void updateTxStatus(String orderno, OrderTxStatus txStatus) ;
    public void updateTxStatusToRealized(String orderno, FruitBetItemType openResult, BigDecimal winmoney) ;
    public void updateTxStatusToFailed(String orderno, FruitBetItemType openResult) ;
    public FruitOrderInfo findByNo(String orderno);
    public List<FruitOrderInfo> queryListByUserid(String createtime, long userid, int offset) ;
    public void queryAllByIssue(String issue, Callback<FruitOrderInfo> callback) ;
    public RowPager<FruitOrderInfo> queryScrollPage(PageVo pageVo, FruitType lotteryType, long userid, long agentid,long staffid, String systemNo, String issue, OrderTxStatus txStatus) ;
    public void queryAllMember(String startTimeString, String endTimeString, Callback<FruitOrderInfo> callback);
}
