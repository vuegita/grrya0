package com.inso.modules.coin.contract.processor.token20;

import com.inso.modules.coin.contract.model.TransactionResult;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.common.model.CryptoCurrency;

import java.math.BigDecimal;

public interface Token20Support {

    public CryptoNetworkType getNeworkType();

    public int getApproveCount(String address, CryptoCurrency currency);

    public int decimals(String tokenContractAdrress);
    public int decimals(String tokenContractAdrress, int defaultValue);

    public BigDecimal allowance(String tokenContractAdrress, String owner, String spender);
    public BigDecimal balanceOf(String tokenContractAdrress, int decimals, String account);
//    public TransactionResult transfer(String tokenContractAdrress, String toAddress, BigDecimal value, BigDecimal gasLimit, String triggerPrivateKey);
    public TransactionResult transfer(String tokenContractAdrress, int decimals, String toAddress, BigDecimal value, BigDecimal gasLimit, String triggerPrivateKey, String triggerAddress);

    public TransactionResult getTransanctionStatus(String externalTxnid);
}
