package com.inso.modules.passport.returnwater.service;

import java.math.BigDecimal;
import java.util.List;

import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.passport.business.model.ReturnWaterLogDetail;
import com.inso.modules.passport.user.model.UserInfo;

public interface ReturnWaterLogDetailService {

    /**
     *
     * @param level 所属等级
     */
//    public void addLogDetail(int level, UserInfo userInfo, FundAccountType accountType, ICurrencyType currencyType, UserInfo childUserInfo);

    public void updateAmount(int level, UserInfo userInfo, UserInfo childUserInfo, FundAccountType accountType, ICurrencyType currencyType, BigDecimal amount);

    public List<ReturnWaterLogDetail> queryByUserid(boolean purge, long userid, int level, int offset, int limit);
}
