package com.inso.modules.game.task_checkin.service.dao;

import java.math.BigDecimal;
import java.util.List;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.game.task_checkin.model.TaskCheckinOrderInfo;
import com.inso.modules.passport.user.model.UserAttr;


public interface TaskCheckinOrderDao {


    public void add(String orderno, long userid, String username, UserAttr userAttr, BigDecimal amount, OrderTxStatus txStatus);
    public void updateStatus(String orderno, OrderTxStatus txStatus);
    public RowPager<TaskCheckinOrderInfo> queryScrollPage(PageVo pageVo, String orderno, long userid, long agentid);


    public List<TaskCheckinOrderInfo> queryScrollPageByUser(String startTime, String endTime, long userid, long offset, long pagesize);

}
