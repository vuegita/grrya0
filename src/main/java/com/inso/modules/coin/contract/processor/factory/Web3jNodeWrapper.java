package com.inso.modules.coin.contract.processor.factory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import org.web3j.protocol.Web3j;

import java.util.List;
import java.util.Map;

public class Web3jNodeWrapper {

    private CryptoNetworkType mNetworkType;

    private Map<String, String> mKeyMaps = Maps.newHashMap();
    private List<Web3j> mWeb3jList = Lists.newArrayList();

    private int mSize = 0;
    private int currentIndex = 0;

    public Web3jNodeWrapper(CryptoNetworkType networkType)
    {
        this.mNetworkType = networkType;

        init();
    }

    private void init()
    {
        for(String apiServer : mNetworkType.getBackupApiServerList())
        {
            if(mKeyMaps.containsKey(apiServer))
            {
                continue;
            }

            Web3j web3j = Web3j.build(new org.web3j.protocol.http.HttpService(apiServer));
            mWeb3jList.add(web3j);

            mKeyMaps.put(apiServer, StringUtils.getEmpty());
        }
        this.mSize = mNetworkType.getBackupApiServerList().size();
    }

    public Web3j getNode()
    {
        int nextIndex = currentIndex;
        if(nextIndex >= mSize)
        {
            nextIndex = 0;
            this.currentIndex = 0;
        }
        else
        {
            this.currentIndex ++;
        }
        return mWeb3jList.get(nextIndex);
    }
}
