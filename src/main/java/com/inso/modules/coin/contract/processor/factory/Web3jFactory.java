package com.inso.modules.coin.contract.processor.factory;

import com.google.common.collect.Maps;
import com.inso.modules.coin.core.model.CryptoNetworkType;

import java.util.Map;

public class Web3jFactory {


    private static Map<CryptoNetworkType, Web3jNodeWrapper> mWrapperMaps = Maps.newConcurrentMap();

    private interface MyInternal {
        public Web3jFactory mgr = new Web3jFactory();
    }

    private Web3jFactory()
    {
    }

    public static Web3jFactory getInstance()
    {
        return MyInternal.mgr;
    }

//    private void addServer(CryptoNetworkType networkType)
//    {
//        Web3jNodeWrapper wrapper = mWrapperMaps.get(networkType);
//        if(wrapper == null)
//        {
//            wrapper = new Web3jNodeWrapper(networkType);
//            mWrapperMaps.put(networkType, wrapper);
//        }
//    }

    public Web3jNodeWrapper getWrapper(CryptoNetworkType networkType)
    {
        Web3jNodeWrapper wrapper = mWrapperMaps.get(networkType);
        if(wrapper == null)
        {
            wrapper = new Web3jNodeWrapper(networkType);
            mWrapperMaps.put(networkType, wrapper);
        }
        return wrapper;
    }


}
