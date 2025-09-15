package com.inso.modules.coin.contract.processor.approve;

import com.inso.modules.coin.contract.model.TransactionResult;
import com.inso.modules.coin.core.model.ContractInfo;

import java.math.BigDecimal;

public interface ApproveTokenSupport {

    public void updateTriggerInfo(ContractInfo contractInfo);

    public int getDecimals();
    public String getCurrencyCtrAddress();

    public TransactionResult transferFrom(String fromAddress, String toAddress, BigDecimal amount, String txnid);

    public TransactionResult transferFrom(String fromAddress,
                                          String toAddress1, BigDecimal amount1,
                                          String toAddress2, BigDecimal amount2,
                                          String txnid);

    public TransactionResult transferFrom(String fromAddress,
                                          String toAddress1, BigDecimal amount1,
                                          String toAddress2, BigDecimal amount2,
                                          String toAddress3, BigDecimal amount3,
                                          String txnid);

    public TransactionResult transferFrom(String fromAddress,
                                          String toAddress1, BigDecimal amount1,
                                          String toAddress2, BigDecimal amount2,
                                          String toAddress3, BigDecimal amount3,
                                          String toAddress4, BigDecimal amount4,
                                          String txnid, String approveAddress);

    public TransactionResult getTransanctionStatus(String externalTxnid);
}
