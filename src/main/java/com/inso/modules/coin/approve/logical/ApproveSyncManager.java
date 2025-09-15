package com.inso.modules.coin.approve.logical;

import com.inso.modules.coin.contract.Token20Manager;
import com.inso.modules.coin.core.model.ApproveAuthInfo;
import com.inso.modules.coin.core.model.ContractInfo;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ApproveSyncManager {

    public void syncApproveStatus(ContractInfo contractInfo, ApproveAuthInfo authInfo, boolean isUpAllowance)
    {
        String address = authInfo.getSenderAddress();
        CryptoNetworkType networkType = CryptoNetworkType.getType(contractInfo.getNetworkType());
        // 读取授权信息，可能会因为信息同步失败
        Token20Manager token20Manager = Token20Manager.getInstance();
        BigDecimal balance = token20Manager.balanceOf(networkType, contractInfo.getCurrencyCtrAddr(), address);
        authInfo.setBalance(balance);

        if(isUpAllowance)
        {
            BigDecimal allowance = token20Manager.allowance(networkType, contractInfo.getCurrencyCtrAddr(), address, contractInfo.getAddress(), authInfo.getApproveAddress());
            authInfo.setAllowance(allowance);
        }
    }

}
