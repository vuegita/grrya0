package com.inso.modules.game.rg.service;

import java.math.BigDecimal;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.game.rg.model.LotteryOrderInfo;
import com.inso.modules.game.rg.model.LotteryRGType;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.report.GameBusinessStatsService;
import com.inso.modules.report.model.GameBusinessDay;

public interface LotteryOrderService extends GameBusinessStatsService {

    public void addOrder(String orderno, String issue, LotteryRGType lotteryType, UserInfo userInfo, UserAttr userAttr, String betItem, BigDecimal basicAmount, long betCount, BigDecimal amount, BigDecimal feemoney, JSONObject remark);
//    public void updateTxStatus(String orderno, OrderTxStatus txStatus, long openResult, BigDecimal winmoney, JSONObject remark);
    public void updateTxStatus(String orderno, OrderTxStatus txStatus);
    public void updateTxStatusToRealized(String orderno, long openResult, BigDecimal winmoney);
    public void updateTxStatusToFailed(String orderno, long openResult);

    public LotteryOrderInfo findByNo(String orderno);
    public List<LotteryOrderInfo> queryListByUserid(String createtime, long userid, LotteryRGType rgType, int offset);

    public void queryAllByIssue(String issue, Callback<LotteryOrderInfo> callback);

    public RowPager<LotteryOrderInfo> queryScrollPage(PageVo pageVo, LotteryRGType lotteryType, long userid, long agentid,long staffid, String systemNo, String issue, OrderTxStatus txStatus,String sortName,String sortOrder);
    public void queryAllMember(String startTimeString, String endTimeString, Callback<LotteryOrderInfo> callback);


}
