package com.inso.modules.web.team.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.defi_mining.model.MiningOrderInfo;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.web.team.model.TeamBusinessType;
import com.inso.modules.web.team.model.TeamBuyGroupInfo;
import com.inso.modules.web.team.model.TeamBuyRecordInfo;
import com.inso.modules.web.team.model.TeamConfigInfo;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

public interface TeamBuyGroupService {


    public long add(TeamConfigInfo configInfo, UserAttr userAttr, BigDecimal realInvesAmount);

    public void updateInfo(TeamBuyGroupInfo entity, long hasInviteCount, OrderTxStatus txStatus);

    public long updateInviteCountAndGetRecordId(TeamBuyGroupInfo entity, UserAttr userAttr, BigDecimal realInvesAmount);

    public TeamBuyGroupInfo findById(boolean purge, long id);
    public TeamBuyGroupInfo findLatest(boolean purge, long userid, TeamBusinessType businessType);

    public void deleteById(TeamBuyGroupInfo entity);
    public RowPager<TeamBuyGroupInfo> queryScrollPage(PageVo pageVo, long agentid, long staffid, long userid, TeamBusinessType businessType, OrderTxStatus status);

    public List<TeamBuyGroupInfo> queryListByUser(boolean purge, long userid, TeamBusinessType businessType, int limit);

    public void queryAll(DateTime fromTime, DateTime toTime, Callback<TeamBuyGroupInfo> callback);


}
