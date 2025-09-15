package com.inso.modules.paychannel.logical.payment.model;

import java.math.BigDecimal;

import com.alibaba.fastjson.annotation.JSONField;
import com.inso.modules.paychannel.model.PayProductType;

/**
 * 代收时，保存相关状态到缓存，回调时可使用
 * 比如callback_url 就是
 */
public class PaymentReturnStatusModel {

    /*** channelid ***/
    private long cid;
    private String txnid;
    private BigDecimal amount;
    private PayProductType productType;
    private String productinfo;

    @JSONField(serialize = false, deserialize = false)
    public boolean verifyProductType(PayProductType type)
    {
        return type == productType;
    }

    public long getCid() {
        return cid;
    }

    public void setCid(long cid) {
        this.cid = cid;
    }

    public PayProductType getProductType() {
        return productType;
    }

    public void setProductType(PayProductType productType) {
        this.productType = productType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getTxnid() {
        return txnid;
    }

    public void setTxnid(String txnid) {
        this.txnid = txnid;
    }

    public String getProductinfo() {
        return productinfo;
    }

    public void setProductinfo(String productinfo) {
        this.productinfo = productinfo;
    }
}
