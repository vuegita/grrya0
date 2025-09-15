package com.inso.modules.ad.mall.service.dao;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.mall.model.PromotionInfo;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserInfo;

import java.math.BigDecimal;

public interface PromotionDao  {

    public void addOrder(UserInfo userInfo, BigDecimal price, BigDecimal totalAmount, Status status, JSONObject jsonObject);
    public void updateInfo(long id, BigDecimal price, BigDecimal totalAmount, Status status, JSONObject jsonObject);

    public PromotionInfo findByUserId(long userid);
    public RowPager<PromotionInfo> queryScrollPage(PageVo pageVo, long agentid, long staffid, long userid, Status status);


}
