package com.inso.modules.coin.core.service.dao;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.core.model.ProfitConfigInfo;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserInfo;

import java.math.BigDecimal;
import java.util.List;

public interface ProfitConfigDao {

    public long add(UserInfo agentInfo, ProfitConfigInfo.ProfitType profitType, CryptoCurrency currency, long level, BigDecimal minAmount, BigDecimal dailyRate, Status status);

    public void updateInfo(long id, BigDecimal dailyRate, BigDecimal minAmount, CryptoCurrency currency, long level, Status status);
    public ProfitConfigInfo findById(long id);
    public void deleteByid(long id);
//    public ProfitConfigInfo findByAgentId(long agentid);

    public RowPager<ProfitConfigInfo> queryScrollPage(PageVo pageVo, long agentid, ProfitConfigInfo.ProfitType profitType, CryptoCurrency currency, Status status);
    public List<ProfitConfigInfo> queryAllList(long agentid, ProfitConfigInfo.ProfitType profitType, CryptoCurrency currency, Status status);


}
