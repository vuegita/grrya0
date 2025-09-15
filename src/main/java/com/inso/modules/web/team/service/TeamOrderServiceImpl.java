package com.inso.modules.web.team.service;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.helper.IdGenerator;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.web.team.model.TeamBusinessType;
import com.inso.modules.web.team.model.TeamBuyGroupInfo;
import com.inso.modules.web.team.model.TeamOrderInfo;
import com.inso.modules.web.team.service.dao.TeamOrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TeamOrderServiceImpl implements TeamOrderService{

    @Autowired
    private TeamOrderDao mTeamOrderDao;

    private static IdGenerator mIdGenerator = IdGenerator.newSingleWorder();


    /**
     * 生成订单号
     * @return
     */
    public static String nextOrderId()
    {
        return mIdGenerator.nextId();
    }

    @Override
    public String addOrder(String outTradeNo, UserAttr userAttr, TeamBuyGroupInfo groupInfo, long recordid, BigDecimal amount, ICurrencyType currency, TeamBusinessType orderType) {
        String orderno = nextOrderId();
        mTeamOrderDao.addOrder(orderno, outTradeNo, userAttr, groupInfo, recordid, amount, currency, OrderTxStatus.WAITING, orderType, null);
        return orderno;
    }

    @Override
    public void updateInfo(String orderno, OrderTxStatus status, JSONObject remark) {
        mTeamOrderDao.updateInfo(orderno, status, remark);
    }

    @Override
    public TeamOrderInfo findById(boolean purge, String orderno) {
        return mTeamOrderDao.findById(orderno);
    }

    @Override
    public void deleteById(String orderno) {
        mTeamOrderDao.deleteById(orderno);
    }

    @Override
    public RowPager<TeamOrderInfo> queryScrollPage(PageVo pageVo, String sysOrderno, long agentid, long staffid, long userid, OrderTxStatus status) {
        return mTeamOrderDao.queryScrollPage(pageVo, sysOrderno, agentid, staffid, userid, status);
    }
}
