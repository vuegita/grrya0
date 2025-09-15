package com.inso.modules.paychannel.logical;

import java.util.List;

import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.paychannel.model.PayProductType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.CollectionUtils;
import com.inso.modules.paychannel.model.ChannelInfo;
import com.inso.modules.paychannel.model.ChannelType;
import com.inso.modules.paychannel.service.ChannelService;

/**
 * 在线支付通道选择器
 * 
 * @author Administrator
 *
 */
@Component
public class OnlinePayChannel {

	private static Log LOG = LogFactory.getLog(OnlinePayChannel.class);

	@Autowired
	private ChannelService mPayChannelService;
	
	public ChannelInfo getPayinChannel(String name)
	{
		List<ChannelInfo> list = mPayChannelService.queryOnlineList(false, ChannelType.PAYIN, null, null);
		if(CollectionUtils.isEmpty(list))
		{
			return null;
		}

		for(ChannelInfo model : list)
		{
			if(model.getName().equalsIgnoreCase(name))
			{
				return model;
			}
		}
		return null;
	}

	public ChannelInfo getPayoutChannel(PayProductType productType, ICurrencyType currencyType)
	{
		List<ChannelInfo> list = mPayChannelService.queryOnlineList(false, ChannelType.PAYOUT, productType, currencyType);
		if(CollectionUtils.isEmpty(list))
		{
			return null;
		}
		return list.get(0);
	}
	
}
