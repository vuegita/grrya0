package com.inso.modules.game.red_package.service;

import java.math.BigDecimal;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.game.red_package.model.RedPReceivOrderInfo;
import com.inso.modules.game.red_package.model.RedPType;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;

public interface RedPReceivOrderService {

    public void addOrder(String orderno, long rpid, RedPType type, UserInfo userInfo, UserAttr userAttr, BigDecimal amount, long index, JSONObject remark);
    public void updateTxStatus(String orderno, OrderTxStatus txStatus, JSONObject remark);

    public RedPReceivOrderInfo findByNo(String orderno);
    public List<RedPReceivOrderInfo> queryListByUserid(boolean purge, long userid, int offset);

    public void queryAllByRedPId(long id, Callback<RedPReceivOrderInfo> callback);
    public RowPager<RedPReceivOrderInfo> queryScrollPage(PageVo pageVo, RedPType lotteryType, long userid, String systemNo, long rpid, long agentid, long staffid, OrderTxStatus txStatus);
    public void queryAllMember(String startTimeString, String endTimeString, Callback<RedPReceivOrderInfo> callback);

    public void clearUserCache(long userid, RedPType childType);
}
