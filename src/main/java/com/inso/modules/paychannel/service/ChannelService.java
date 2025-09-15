package com.inso.modules.paychannel.service;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.paychannel.model.ChannelInfo;
import com.inso.modules.paychannel.model.ChannelStatus;
import com.inso.modules.paychannel.model.ChannelType;
import com.inso.modules.paychannel.model.PayProductType;

import java.math.BigDecimal;
import java.util.List;

public interface ChannelService {

    public void add(String title, JSONObject secret, PayProductType productType, ChannelStatus channelStatus, ChannelType type, ICurrencyType currencyType, String remark, long sort, BigDecimal feerate, BigDecimal extraFeemoney);
    public void delete(ChannelInfo channelInfo);
    public ChannelInfo findById(boolean purge, long channelid);
    public void updateInfo(ChannelInfo channelInfo, String title, JSONObject secret, ChannelStatus channelStatus, String remark ,long sort, BigDecimal feerate, BigDecimal extraFeemoney);

    public void queryAll(Callback<ChannelInfo> callback);
    public List<ChannelInfo> queryOnlineList(boolean purge, ChannelType type, PayProductType productType, ICurrencyType currencyType);
    public RowPager<ChannelInfo> queryScrollPage(PageVo pageVo, ChannelStatus status, ChannelStatus ignoreStatus, ChannelType type,String remark);

}
