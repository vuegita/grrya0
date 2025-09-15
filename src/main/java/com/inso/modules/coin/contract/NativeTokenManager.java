package com.inso.modules.coin.contract;

import com.google.common.collect.Maps;
import com.inso.modules.coin.contract.processor.navtive.NativeTokenSupport;
import com.inso.modules.coin.contract.processor.navtive.eth.ETHNativeTokenProcessor;
import com.inso.modules.coin.contract.processor.navtive.tron.TRXNativeTokenProcessor;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.core.model.VMType;
import com.inso.modules.common.model.Status;

import java.math.BigDecimal;
import java.util.Map;

public class NativeTokenManager {

    private Map<String, NativeTokenSupport> maps = Maps.newHashMap();

    private interface MyInternal {
        public NativeTokenManager mgr = new NativeTokenManager();
    }

    private NativeTokenManager()
    {
        // ETH
        addSupport(new ETHNativeTokenProcessor(CryptoNetworkType.ETH_MAINNET));
        addSupport(new ETHNativeTokenProcessor(CryptoNetworkType.ETH_ROPSTEN));

        // MATIC
        addSupport(new ETHNativeTokenProcessor(CryptoNetworkType.MATIC_POLYGON));
        addSupport(new ETHNativeTokenProcessor(CryptoNetworkType.MATIC_MUMBAI));

        // BNB
        addSupport(new ETHNativeTokenProcessor(CryptoNetworkType.BNB_MAINNET));
        addSupport(new ETHNativeTokenProcessor(CryptoNetworkType.BNB_TESTNET));

        // TRX
        addSupport(new TRXNativeTokenProcessor(CryptoNetworkType.TRX_GRID));
        addSupport(new TRXNativeTokenProcessor(CryptoNetworkType.TRX_NILE));
    }

    public static NativeTokenManager getInstance()
    {
        return MyInternal.mgr;
    }

    private void addSupport(NativeTokenSupport support)
    {
        maps.put(support.getNeworkType().getKey(), support);
    }

    public BigDecimal getBalance(CryptoNetworkType networkType, String address)
    {
        NativeTokenSupport support = maps.get(networkType.getKey());
        return support.getBalance(address);
    }

    public Status verifyExistOwner(CryptoNetworkType networkType, String ownerAddress, String address)
    {
        if(networkType.getVmType() != VMType.TVM)
        {
            return null;
        }
        NativeTokenSupport support = maps.get(networkType.getKey());
        return support.verifyExistOwner(ownerAddress, address);
    }

    public static void main(String[] args) {
        NativeTokenManager mgr = NativeTokenManager.getInstance();
        String address = "333";
        BigDecimal balance = mgr.getBalance(CryptoNetworkType.MATIC_POLYGON, address);
        System.out.println(balance);
    }
}
