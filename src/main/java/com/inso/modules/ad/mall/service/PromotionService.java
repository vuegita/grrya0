package com.inso.modules.ad.mall.service;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.mall.model.PromotionInfo;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserInfo;

import java.math.BigDecimal;

public interface PromotionService {

    public void addOrder(UserInfo userInfo, BigDecimal price, BigDecimal totalAmount);
    public void updateInfo(PromotionInfo entity, BigDecimal price, BigDecimal totalAmount, Status status, JSONObject jsonObject);

    public PromotionInfo findByUserId(boolean purge, long userid);
    public RowPager<PromotionInfo> queryScrollPage(PageVo pageVo, long agentid, long staffid, long userid, Status status);


}
