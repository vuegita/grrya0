package com.inso.modules.paychannel.service.dao;

import java.math.BigDecimal;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.paychannel.model.ChannelInfo;
import com.inso.modules.paychannel.model.ChannelStatus;
import com.inso.modules.paychannel.model.ChannelType;
import com.inso.modules.paychannel.model.PayProductType;

public interface ChannelDao {

    public void add(String title, JSONObject secret, PayProductType productType, ChannelStatus channelStatus, ChannelType type,
                    ICurrencyType currencyType, String remark, long sort, BigDecimal feerate, BigDecimal extraFeemoney);

    public void delete(long channelid);
    public ChannelInfo findById(long channelid);
    public void updateInfo(long channelid, String title, JSONObject secret, ChannelStatus channelStatus, String remark ,long sort, BigDecimal feerate, BigDecimal extraFeemoney);

    public void queryAll(Callback<ChannelInfo> callback);
    public List<ChannelInfo> queryAllList(ChannelStatus status, ChannelType type, PayProductType productType, ICurrencyType currencyType);
    public RowPager<ChannelInfo> queryScrollPage(PageVo pageVo, ChannelStatus status, ChannelStatus ignoreStatus, ChannelType type,String remark);

}
