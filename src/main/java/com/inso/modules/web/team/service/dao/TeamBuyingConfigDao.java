package com.inso.modules.web.team.service.dao;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.web.team.model.TeamBusinessType;
import com.inso.modules.web.team.model.TeamConfigInfo;

import java.math.BigDecimal;
import java.util.List;

public interface TeamBuyingConfigDao  {

    public long add(UserInfo agentInfo, TeamBusinessType businessType, ICurrencyType currency, BigDecimal limitValidRechargeAmount, long level, BigDecimal limitMinAmount, long limitMinInviteCount,
                    String returnCreatorRate, Status status, BigDecimal returnJoinRate);

    public void updateInfo(long id, String returnCreatorRate, BigDecimal returnJoinRate, BigDecimal minAmount, long limitInviteCount, BigDecimal limitValidRechargeAmount, Status status);

    public TeamConfigInfo findById(long id);
    public RowPager<TeamConfigInfo> queryScrollPage(PageVo pageVo, long agentid, TeamBusinessType businessType, ICurrencyType currency, Status status);


    public List<TeamConfigInfo> getList(long agentid, TeamBusinessType businessType);

}
