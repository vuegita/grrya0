package com.inso.modules.web.team.service.dao;

import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.defi_mining.model.MiningOrderInfo;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.web.team.model.TeamBusinessType;
import com.inso.modules.web.team.model.TeamBuyGroupInfo;
import com.inso.modules.web.team.model.TeamConfigInfo;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

public interface TeamBuyingGroupDao {


    public long add(TeamConfigInfo configInfo, UserAttr userAttr, OrderTxStatus txStatus, BigDecimal realInvesAmount);
    public void updateInfo(long id, long hasInviteCount, OrderTxStatus txStatus);

    public TeamBuyGroupInfo findById(long id);
    public TeamBuyGroupInfo findLatest(long userid, TeamBusinessType businessType);

    public void deleteById(long id);
    public RowPager<TeamBuyGroupInfo> queryScrollPage(PageVo pageVo, long agentid, long staffid, long userid, TeamBusinessType businessType, OrderTxStatus status);

    public List<TeamBuyGroupInfo> queryListByUser(DateTime fromTime, DateTime toTime, long userid, TeamBusinessType businessType, int limit);

    public void queryAll(DateTime fromTime, DateTime toTime, Callback<TeamBuyGroupInfo> callback);


}
