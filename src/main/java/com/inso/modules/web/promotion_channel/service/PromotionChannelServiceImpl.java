package com.inso.modules.web.promotion_channel.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.web.promotion_channel.model.PromotionChannelInfo;
import com.inso.modules.web.promotion_channel.model.PromotionChannelType;
import com.inso.modules.web.promotion_channel.service.dao.PromotionChannelDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PromotionChannelServiceImpl implements PromotionChannelService{

    @Autowired
    private PromotionChannelDao promotionChannelDao;

    @Override
    public long add(String name, PromotionChannelType channelType, String url, UserAttr userAttr, String contact, long subscribeCount, long viewCount, BigDecimal amount, Status status, String remark) {
        return promotionChannelDao.add(name, channelType, url, userAttr, contact, subscribeCount, viewCount, amount, status, remark);
    }

    @Override
    public void updateInfo(long id, String name, long subscribeCount, long viewCount, String contact, BigDecimal amount, Status status, String remark) {
        promotionChannelDao.updateInfo(id, name, subscribeCount, viewCount, contact, amount, status, remark);
    }

    @Override
    public PromotionChannelInfo findById(long id) {
        return promotionChannelDao.findById(id);
    }

    @Override
    public void deleteById(long id) {
        promotionChannelDao.deleteById(id);
    }

    @Override
    public RowPager<PromotionChannelInfo> queryScrollPage(PageVo pageVo, long agentid, long staffid, String name, PromotionChannelType channelType, Status status) {
        return promotionChannelDao.queryScrollPage(pageVo, agentid, staffid, name, channelType, status);
    }
}
