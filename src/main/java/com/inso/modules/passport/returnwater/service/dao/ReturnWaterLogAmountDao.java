package com.inso.modules.passport.returnwater.service.dao;

import java.math.BigDecimal;
import java.util.List;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.passport.returnwater.model.ReturnWaterLog;

public interface ReturnWaterLogAmountDao {

    public void addLog(long userid, String username, FundAccountType accountType, ICurrencyType currencyType);

    public void updateAmount(long userid, FundAccountType accountType, ICurrencyType currencyType, int level, BigDecimal amount);

    public ReturnWaterLog findByUserid(long userid, FundAccountType accountType, ICurrencyType currencyType);

    public List<ReturnWaterLog> queryByUser(long userid, FundAccountType accountType);

    public RowPager<ReturnWaterLog> queryScrollPage(PageVo pageVo, long userid);

}
