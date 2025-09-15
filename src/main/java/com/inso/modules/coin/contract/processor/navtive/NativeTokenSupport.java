package com.inso.modules.coin.contract.processor.navtive;

import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.common.model.Status;

import java.math.BigDecimal;

public interface NativeTokenSupport {

    public CryptoNetworkType getNeworkType();
    public BigDecimal getBalance(String address);

    public Status verifyExistOwner(String ownerAddress, String address);

}
