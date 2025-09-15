package com.inso.modules.paychannel.logical;

import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.paychannel.model.ChannelType;
import com.inso.modules.paychannel.model.CoinPaymentInfo;

import java.util.Collections;
import java.util.List;

public class CoinChannelManager {

    private static final String ROOT_CACHE_KEY = CoinChannelManager.class.getName();

    // 网络通道列表-用于后台系统提币
    private static final String CACHE_KEY_CHANNEL_NETWORK_LIST = ROOT_CACHE_KEY + "_channel_netowork_list";

    // 代理通道列表
    private static final String CACHE_KEY_CHANNEL_AGENT_LIST = ROOT_CACHE_KEY + "_channel_agent_list";

    private interface MyInternal {
        public CoinChannelManager mgr = new CoinChannelManager();
    }

    public static CoinChannelManager getInstance()
    {
        return MyInternal.mgr;
    }

    ///////////////////////////////////////////////////////////
    public void updateChannelAllPaymentList(ChannelType channelType, List<CoinPaymentInfo> rsList)
    {
        if(CollectionUtils.isEmpty(rsList))
        {
            return;
        }
        String cachekey  = createChannelAllPaymentListCacheKey(channelType);
        CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(rsList), CacheManager.EXPIRES_DAY);
    }

    public List<CoinPaymentInfo> getChannelAllPaymentList(ChannelType channelType)
    {
        String cachekey  = createChannelAllPaymentListCacheKey(channelType);
        List<CoinPaymentInfo> rsList = CacheManager.getInstance().getList(cachekey, CoinPaymentInfo.class);
        if(CollectionUtils.isEmpty(rsList))
        {
            return Collections.emptyList();
        }
        return rsList;
    }

    ///////////////////////////////////////////////////////////
//    public void updateChannelCurrencyList(ChannelType channelType, List<String> rsList)
//    {
//        if(CollectionUtils.isEmpty(rsList))
//        {
//            return;
//        }
//        String cachekey  = createChannelListCacheKey(channelType, false);
//        CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(rsList), CacheManager.EXPIRES_DAY);
//    }
//
//    public List<String> getChannelCurrencyList(ChannelType channelType)
//    {
//        String cachekey  = createChannelListCacheKey(channelType, false);
//        List<String> rsList = CacheManager.getInstance().getList(cachekey, String.class);
//        if(CollectionUtils.isEmpty(rsList))
//        {
//            return Collections.emptyList();
//        }
//        return rsList;
//    }

    /**
     * 更新代理通道列表，前台会员调用
     * @param rsList
     */
    public void updateAgentPaymentInfoList(List<CoinPaymentInfo> rsList)
    {
        if(CollectionUtils.isEmpty(rsList))
        {
            return;
        }
        CoinPaymentInfo paymentInfo = rsList.get(0);
        ChannelType channelType = ChannelType.getType(paymentInfo.getChannelType());
        if(channelType == ChannelType.PAYIN)
        {
            for(CoinPaymentInfo model : rsList)
            {
                model.setAccountPrivateKey(StringUtils.getEmpty());
            }
        }

        CryptoNetworkType networkType = CryptoNetworkType.getType(paymentInfo.getNetworkType());
        String cachekey = createAgentPaymentListCacheKey(channelType, networkType, paymentInfo.getAgentName());
        CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(rsList), CacheManager.EXPIRES_DAY);
    }

    /**
     * 更新代理通道列表，前台会员调用
     * @param
     */
    public List<CoinPaymentInfo> getAgentPaymentInfoList(ChannelType channelType, CryptoNetworkType networkType, String agentname)
    {
        String cachekey = createAgentPaymentListCacheKey(channelType, networkType, agentname);
        List rsList = CacheManager.getInstance().getList(cachekey, CoinPaymentInfo.class);
        if(CollectionUtils.isEmpty(rsList))
        {
            return Collections.emptyList();
        }
        return rsList;
    }

    private String createAgentPaymentListCacheKey(ChannelType channelType, CryptoNetworkType networkType, String agentname)
    {
        agentname = StringUtils.getNotEmpty(agentname);
        String cachekey = CACHE_KEY_CHANNEL_AGENT_LIST + channelType.getKey() + networkType.getKey() + agentname;
        return cachekey;
    }

    private String createChannelAllPaymentListCacheKey(ChannelType channelType)
    {
        String cachekey = CACHE_KEY_CHANNEL_NETWORK_LIST + channelType.getKey();
        return cachekey;
    }

    public static void main(String[] args) {
    }

}
