package com.inso.modules.paychannel.service;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.paychannel.cache.ChannelCacheUtils;
import com.inso.modules.paychannel.model.ChannelInfo;
import com.inso.modules.paychannel.model.ChannelStatus;
import com.inso.modules.paychannel.model.ChannelType;
import com.inso.modules.paychannel.model.PayProductType;
import com.inso.modules.paychannel.service.dao.ChannelDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
public class ChannelServiceImpl implements ChannelService{

    @Autowired
    private ChannelDao mChannelDao;

    @Override
    @Transactional
    public void add(String title, JSONObject secret, PayProductType productType, ChannelStatus channelStatus, ChannelType type, ICurrencyType currencyType, String remark, long sort, BigDecimal feerate, BigDecimal extraFeemoney) {
        mChannelDao.add(title, secret, productType, channelStatus, type, currencyType, remark,sort, feerate, extraFeemoney);
    }

    @Override
    @Transactional
    public void delete(ChannelInfo channelInfo) {
        long channelid = channelInfo.getId();
        mChannelDao.delete(channelid);

        deleteCache(channelInfo);
    }

    @Override
    @Transactional
    public ChannelInfo findById(boolean purge, long channelid) {
        String cachekey = ChannelCacheUtils.createFindChannelCache(channelid);
        ChannelInfo model = CacheManager.getInstance().getObject(cachekey, ChannelInfo.class);
        if(purge || model == null)
        {
            model = mChannelDao.findById(channelid);
            if(model != null)
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(model), CacheManager.EXPIRES_DAY);
            }
        }
        return model;
    }

    @Override
    @Transactional
    public void updateInfo(ChannelInfo channelInfo, String title, JSONObject secret, ChannelStatus channelStatus, String remark,long sort, BigDecimal feerate, BigDecimal extraFeemoney) {
        long channelid = channelInfo.getId();
        mChannelDao.updateInfo(channelid, title, secret, channelStatus, remark,sort, feerate, extraFeemoney);

        deleteCache(channelInfo);
    }

    @Override
    public void queryAll(Callback<ChannelInfo> callback)
    {
        mChannelDao.queryAll(callback);
    }

    @Override
    public List<ChannelInfo> queryOnlineList(boolean purge, ChannelType type, PayProductType productType, ICurrencyType currencyType) {
        String cachekey = ChannelCacheUtils.createQueryChannelListCache(type, productType, currencyType);
        List<ChannelInfo> list = CacheManager.getInstance().getList(cachekey, ChannelInfo.class);
//        list=null;
        if(purge || list == null)
        {
            list = mChannelDao.queryAllList(ChannelStatus.ENABLE, type, productType, currencyType);
            if(list == null)
            {
                list = Collections.emptyList();
            }
            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(list));
        }
        return list;
    }

    @Override
    public RowPager<ChannelInfo> queryScrollPage(PageVo pageVo, ChannelStatus status, ChannelStatus ignoreStatus, ChannelType type,String remark) {
        return mChannelDao.queryScrollPage(pageVo, status, ignoreStatus, type, remark);
    }

    private void deleteCache(ChannelInfo channelInfo)
    {
        String singleCachekey = ChannelCacheUtils.createFindChannelCache(channelInfo.getId());
        CacheManager.getInstance().delete(singleCachekey);

        ChannelType channelType = channelInfo.getChannelType();
        ICurrencyType currencyType = null;
        if(channelInfo.getProduct() == PayProductType.TAJPAY && channelType == ChannelType.PAYOUT){
            currencyType = ICurrencyType.getType(channelInfo.getCurrencyType());
        }
        String onlineCachekey = ChannelCacheUtils.createQueryChannelListCache(channelType, channelInfo.getProduct(), currencyType);
        CacheManager.getInstance().delete(onlineCachekey);

    }
}
