package com.inso.modules.game.fm.service;

import java.math.BigDecimal;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.game.fm.model.FMOrderInfo;
import com.inso.modules.game.fm.model.FMType;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;

public interface FMOrderService {

    public void addOrder(String orderno, long rpid, UserInfo userInfo, UserAttr userAttr,
                         BigDecimal buyAmount, BigDecimal return_expected_amount, BigDecimal return_real_rate, long timeHorizon);
    public void updateTxStatus(String orderno, OrderTxStatus txStatus, BigDecimal return_real_amount, BigDecimal feemoney, JSONObject remark);
    public FMOrderInfo findByNo(String orderno);
    public List<FMOrderInfo> queryListByUserid(boolean purge, long userid,OrderTxStatus Status, int offset);
    public void queryAllByIssue(long issue, Callback<FMOrderInfo> callback);
    public FMOrderInfo findByNoAndUserid(boolean purge, long issue,long userid);

    public RowPager<FMOrderInfo> queryScrollPage(PageVo pageVo, FMType lotteryType, long userid, String systemNo, long issue, OrderTxStatus txStatus,long agentid,long staffid) ;
    public void queryAllMember(String startTimeString, String endTimeString, Callback<FMOrderInfo> callback) ;
    public void queryAllByEndtime(String startTimeString, String endTimeString, Callback<FMOrderInfo> callback);


    public void clearUserCache(long userid, OrderTxStatus Status);
}
