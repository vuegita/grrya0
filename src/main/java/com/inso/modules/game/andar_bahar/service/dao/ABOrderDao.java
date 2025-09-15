package com.inso.modules.game.andar_bahar.service.dao;

import java.math.BigDecimal;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.game.andar_bahar.model.ABBetItemType;
import com.inso.modules.game.andar_bahar.model.ABOrderInfo;
import com.inso.modules.game.andar_bahar.model.ABType;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.report.model.GameBusinessDay;

public interface ABOrderDao {

    public void addOrder(String orderno, String issue, ABType lotteryType, UserInfo userInfo, UserAttr userAttr, ABBetItemType betItem, OrderTxStatus txStatus, BigDecimal basicAmount, long betCount, BigDecimal amount, BigDecimal feemoney, JSONObject remark);
    public void updateTxStatus(String orderno, OrderTxStatus txStatus, ABBetItemType openResult, BigDecimal winmoney, BigDecimal feemoney, JSONObject remark);

    public ABOrderInfo findByNo(String orderno);
    public List<ABOrderInfo> queryListByUserid(String createtime, long userid, int limit);

    public void queryAllByIssue(String issue, Callback<ABOrderInfo> callback);

    public RowPager<ABOrderInfo> queryScrollPage(PageVo pageVo, ABType lotteryType, long userid, long agentid, long staffid, String systemNo, String issue, OrderTxStatus txStatus,String sortName,String sortOrder);

    public void queryAllMember(String startTimeString, String endTimeString, Callback<ABOrderInfo> callback);
    public void queryAllMemberByTime(String startTimeString, String endTimeString, Callback<GameBusinessDay> callback);
}
