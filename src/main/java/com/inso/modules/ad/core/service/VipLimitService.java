package com.inso.modules.ad.core.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.core.model.AdVipLimitInfo;
import com.inso.modules.common.model.Status;
import com.inso.modules.web.model.VIPType;

import java.math.BigDecimal;
import java.util.List;

public interface VipLimitService {

    public void add(long vipid, BigDecimal totalMoneyOfDay, BigDecimal freeMoneyOfDay,
                    long inviteCountOfDay, BigDecimal inviteMoneyOfDay,
                    long buyCountOfDay, BigDecimal buyMoneyOfDay, BigDecimal maxMoneyOfSingle);

    public void updateInfo(AdVipLimitInfo limitInfo, BigDecimal totalMoneyOfDay, BigDecimal freeMoneyOfDay,
                           long inviteCountOfDay, BigDecimal inviteMoneyOfDay,
                           long buyCountOfDay, BigDecimal buyMoneyOfDay,
                           BigDecimal maxMoneyOfSingle, long paybackPeriod, Status status,
                           BigDecimal lv1RebateBalanceRate, BigDecimal lv2RebateBalanceRate,
                           BigDecimal lv1WithdrawlRate, BigDecimal lv2WithdrawlRate);

    public AdVipLimitInfo findById(boolean purge, long id);
    public AdVipLimitInfo findByVipId(boolean purge, long vipid);
    public RowPager<AdVipLimitInfo> queryScrollPage(PageVo pageVo, Status forceVipStatus);

    public List<AdVipLimitInfo> queryAllEnable(boolean purge,VIPType type);
}
