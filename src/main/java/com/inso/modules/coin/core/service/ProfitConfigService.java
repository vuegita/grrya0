package com.inso.modules.coin.core.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.core.model.ProfitConfigInfo;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserInfo;

import java.math.BigDecimal;
import java.util.List;

public interface ProfitConfigService {

    public long add(UserInfo agentInfo, ProfitConfigInfo.ProfitType profitType, CryptoCurrency currency, long level, BigDecimal minAmount, BigDecimal dailyRate, Status status);

    public void updateInfo(ProfitConfigInfo entityInfo, BigDecimal dailyRate, BigDecimal minAmount, CryptoCurrency currency, long level, Status status);
    public ProfitConfigInfo findById(long id);

    public void deleteById(long id);

    public RowPager<ProfitConfigInfo> queryScrollPage(PageVo pageVo, long agentid, ProfitConfigInfo.ProfitType profitType, CryptoCurrency currency, Status status);
    public List<ProfitConfigInfo> queryAllList(boolean purge, long agentid, ProfitConfigInfo.ProfitType profitType, CryptoCurrency currency);


}
