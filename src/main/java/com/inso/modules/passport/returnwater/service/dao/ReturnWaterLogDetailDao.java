package com.inso.modules.passport.returnwater.service.dao;

import java.math.BigDecimal;
import java.util.List;

import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.passport.business.model.ReturnWaterLogDetail;
import com.inso.modules.passport.user.model.UserInfo;

public interface ReturnWaterLogDetailDao {

    public void addLog(int level, UserInfo userInfo, FundAccountType accountType, ICurrencyType currencyType, UserInfo childUserInfo);
    public ReturnWaterLogDetail findById(int level, long userid, long childid, FundAccountType accountType, ICurrencyType currencyType);
    public void updateAmount(int level, long userid, long childid, FundAccountType accountType, ICurrencyType currencyType, BigDecimal amount);
    public List<ReturnWaterLogDetail> queryByUserid(long userid, int level, int limit);
}
