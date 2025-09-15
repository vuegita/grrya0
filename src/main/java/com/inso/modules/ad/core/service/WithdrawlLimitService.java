package com.inso.modules.ad.core.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.core.model.WithdrawlLimitInfo;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserInfo;

import java.math.BigDecimal;

public interface WithdrawlLimitService {

    public void add(UserInfo userInfo, BigDecimal amount);
    public void updateInfo(long userid, BigDecimal amount);

    public WithdrawlLimitInfo findByUserId(boolean purge, long userid);
    public RowPager<WithdrawlLimitInfo> queryScrollPage(PageVo pageVo, Status status, long userid);

}
