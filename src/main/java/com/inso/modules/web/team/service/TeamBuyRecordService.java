package com.inso.modules.web.team.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.web.team.model.TeamBusinessType;
import com.inso.modules.web.team.model.TeamBuyRecordInfo;

import java.math.BigDecimal;
import java.util.List;

public interface TeamBuyRecordService {

    public long add(long groupid, TeamBusinessType businessType, BigDecimal realInvesAmount, UserAttr userAttr, ICurrencyType currencyType, OrderTxStatus txStatus);
    public void updateInfo(TeamBuyRecordInfo entity, BigDecimal realInvesAmount, OrderTxStatus txStatus);
    public TeamBuyRecordInfo findById(boolean purge, long id);
    public void deleteById(TeamBuyRecordInfo entity);
    public RowPager<TeamBuyRecordInfo> queryScrollPage(PageVo pageVo, long agentid, long staffid, long userid, TeamBusinessType businessType, OrderTxStatus status) ;
    public List<TeamBuyRecordInfo> queryListByUser(boolean purge, long userid, TeamBusinessType businessType);

    public List<TeamBuyRecordInfo> queryListByGroup(long groupid);
}
