package com.inso.modules.passport.gift.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.gift.model.GiftConfigInfo;
import com.inso.modules.passport.gift.model.GiftPeriodType;
import com.inso.modules.passport.gift.model.GiftTargetType;

import java.math.BigDecimal;
import java.util.List;

public interface GiftConfigService {

    public void add(String title, String desc, GiftTargetType targetType, GiftPeriodType periodType, BigDecimal presentAmount, BigDecimal limitAmount, long sort, Status status, String presentAmountArrValue, Status presentAmountArrEnable);
    public void update(GiftConfigInfo GiftConfigInfo, String title, String desc, BigDecimal presentAmount, BigDecimal limitAmount, long sort, Status status, String presentAmountArrValue, Status presentAmountArrEnable);
    public GiftConfigInfo findById(boolean purge, long id);
    public List<GiftConfigInfo> queryAll(boolean purge);

    public RowPager<GiftConfigInfo> queryScrollPage(PageVo pageVo, GiftTargetType targetType, Status status);

}
