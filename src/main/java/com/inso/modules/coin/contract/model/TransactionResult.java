package com.inso.modules.coin.contract.model;

import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.OrderTxStatus;

public class TransactionResult {

    public static String ERR_IGNORE_RUNTIMEEXCEPTION = "java.lang.RuntimeException:";

    private String txnid;
    private String externalTxnid;
    private OrderTxStatus txStatus;

    private String msg;

    public String getExternalTxnid() {
        return externalTxnid;
    }

    public void setExternalTxnid(String externalTxnid) {
        this.externalTxnid = externalTxnid;
    }

    public OrderTxStatus getTxStatus() {
        return txStatus;
    }

    public void setTxStatus(OrderTxStatus txStatus) {
        this.txStatus = txStatus;
    }

    public String getTxnid() {
        return txnid;
    }

    public void setTxnid(String txnid) {
        this.txnid = txnid;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setClearMessage(String msg)
    {
        if(!StringUtils.isEmpty(msg) && msg.startsWith(ERR_IGNORE_RUNTIMEEXCEPTION))
        {
            int length = msg.length();
            msg = msg.substring(ERR_IGNORE_RUNTIMEEXCEPTION.length(), length);
        }
        this.msg = msg;
    }
}
