package com.inso.modules.game.fm.service.dao;

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

public interface FMOrderDao {

    public void addOrder(String orderno, long rpid, UserInfo userInfo, UserAttr userAttr, OrderTxStatus txStatus,
                         BigDecimal buyAmount, BigDecimal return_expected_amount,BigDecimal order_return_real_rate, long timeHorizon);

    public void updateTxStatus(String orderno, OrderTxStatus txStatus, BigDecimal return_real_amount, BigDecimal feemoney, JSONObject remark);

    public FMOrderInfo findByNo(String orderno);
    public FMOrderInfo findByNoAndUserid(long issue,long userid);

    public List<FMOrderInfo> queryListByUserid(String createtime, long userid, OrderTxStatus Status,int limit);

    public void queryAllByIssue(long issue, Callback<FMOrderInfo> callback);
    public RowPager<FMOrderInfo> queryScrollPage(PageVo pageVo, FMType fmType, long userid, String systemNo, long fmid, OrderTxStatus txStatus,long agentid,long staffid);

    public void queryAllMember(String startTimeString, String endTimeString, Callback<FMOrderInfo> callback);

    public void queryAllByEndtime(String startTimeString, String endTimeString, Callback<FMOrderInfo> callback);
}
