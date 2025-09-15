package com.inso.modules.passport.returnwater.service;

import java.math.BigDecimal;
import java.util.List;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.passport.returnwater.model.ReturnWaterLog;

public interface ReturnWaterLogAmountService {

    // amount
//    public void addLog(long userid, String username, FundAccountType accountType, ICurrencyType currencyType);

//    public void updateChildCount(long userid, FundAccountType accountType, ICurrencyType currencyType, int level);
    public void updateTotalAmount(long userid, String username, ICurrencyType currencyType, int level, BigDecimal amount);

    public ReturnWaterLog findByUserid(boolean purge, long userid, String username, ICurrencyType currencyType);
    public List<ReturnWaterLog> queryByUser(boolean purge, long userid);

    public RowPager<ReturnWaterLog> queryScrollPageBy(PageVo pageVo, long userid);

}
