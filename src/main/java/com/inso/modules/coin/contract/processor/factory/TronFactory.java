package com.inso.modules.coin.contract.processor.factory;

import com.google.common.collect.Maps;
import com.inso.modules.coin.core.model.CryptoNetworkType;

import java.util.Map;

public class TronFactory {


    private static Map<CryptoNetworkType, TronNodeWrapper> mWrapperMaps = Maps.newConcurrentMap();

    private interface MyInternal {
        public TronFactory mgr = new TronFactory();
    }

    private TronFactory()
    {
    }

    public static TronFactory getInstance()
    {
        return MyInternal.mgr;
    }

    public TronNodeWrapper getWrapper(CryptoNetworkType networkType)
    {
        TronNodeWrapper wrapper = mWrapperMaps.get(networkType);
        if(wrapper == null)
        {
            wrapper = new TronNodeWrapper(networkType);
            mWrapperMaps.put(networkType, wrapper);
        }
        return wrapper;
    }

    public static void main(String[] args) {
        TronNodeWrapper nodeWrapper = TronFactory.getInstance().getWrapper(CryptoNetworkType.TRX_GRID);
        nodeWrapper.getNode();
        nodeWrapper.getNode();
        nodeWrapper.getNode();
        nodeWrapper.getNode();
    }

}
