package com.inso.modules.web.team.service.dao;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.web.team.model.TeamBusinessType;
import com.inso.modules.web.team.model.TeamBuyRecordInfo;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

public interface TeamBuyRecordDao {

    public long add(long groupid, TeamBusinessType businessType, BigDecimal realInvesAmount, UserAttr userAttr, ICurrencyType currencyType, OrderTxStatus txStatus);
    public void updateInfo(long id, BigDecimal realInvesAmount, OrderTxStatus txStatus);

    public TeamBuyRecordInfo findById(long id);

    public void deleteById(long id);
    public RowPager<TeamBuyRecordInfo> queryScrollPage(PageVo pageVo, long agentid, long staffid, long userid, TeamBusinessType businessType, OrderTxStatus status);

    public List<TeamBuyRecordInfo> queryListByUser(DateTime fromTime, DateTime toTime, long userid, TeamBusinessType businessType, int limit);

    public List<TeamBuyRecordInfo> queryListByGroup(long groupid);

}
