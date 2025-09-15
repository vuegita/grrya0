package com.inso.modules.web.promotion_channel.service.dao;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.web.promotion_channel.model.PromotionChannelInfo;
import com.inso.modules.web.promotion_channel.model.PromotionChannelType;

import java.math.BigDecimal;

public interface PromotionChannelDao  {

    public long add(String name, PromotionChannelType channelType, String url, UserAttr userAttr, String contact, long subscribeCount, long viewCount, BigDecimal amount, Status status, String remark);
    public void updateInfo(long id, String name, long subscribeCount, long viewCount, String contact, BigDecimal amount, Status status, String remark);

    public PromotionChannelInfo findById(long id);

    public void deleteById(long id);
    public RowPager<PromotionChannelInfo> queryScrollPage(PageVo pageVo, long agentid, long staffid, String name, PromotionChannelType channelType, Status status);



}
