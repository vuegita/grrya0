package com.inso.modules.coin.withdraw.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.inso.framework.utils.MD5;
import com.inso.modules.coin.core.model.CryptoNetworkType;

import java.math.BigDecimal;
import java.util.Date;

public class CoinWithdrawChannel {

    /**
     channel_id                   int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

     channel_key    	           varchar(255) NOT NULL ,
     channel_dimension_type       varchar(255) NOT NULL comment '维度: platfrom|agent-id',

     channel_network_type         varchar(255) NOT NULL comment '网络类型',
     channel_trigger_privatekey   varchar(255) NOT NULL comment '账号私钥',
     channel_trigger_address      varchar(255) NOT NULL comment '账号地址',

     channel_fee_rate             decimal(18, 8) NOT NULL comment '手续费率',
     channel_single_feemoney      decimal(18, 8) NOT NULL comment '单笔再加',

     channel_status               varchar(20) NOT NULL comment '状态',
     channel_createtime  	       datetime DEFAULT NULL ,
     */

    private long id;

    private String key;
    private String dimensionType;

    private String networkType;
    private String triggerPrivatekey;
    private String triggerAddress;

    private BigDecimal feeRate;
    private BigDecimal singleFeemoney;
    private String status;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;
    private String remark;


    public static String getColumnPrefix(){
        return "channel";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public static String generateUsername(String address, CryptoNetworkType networkType)
    {
        String str = "coin_" + MD5.encode(address + networkType.getKey() + "afadfadf3248923").substring(0, 8);
        return str;
    }

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDimensionType() {
        return dimensionType;
    }

    public void setDimensionType(String dimensionType) {
        this.dimensionType = dimensionType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    public String getTriggerPrivatekey() {
        return triggerPrivatekey;
    }

    public void setTriggerPrivatekey(String triggerPrivatekey) {
        this.triggerPrivatekey = triggerPrivatekey;
    }

    public String getTriggerAddress() {
        return triggerAddress;
    }

    public void setTriggerAddress(String triggerAddress) {
        this.triggerAddress = triggerAddress;
    }

    public BigDecimal getFeeRate() {
        return feeRate;
    }

    public void setFeeRate(BigDecimal feeRate) {
        this.feeRate = feeRate;
    }

    public BigDecimal getSingleFeemoney() {
        return singleFeemoney;
    }

    public void setSingleFeemoney(BigDecimal singleFeemoney) {
        this.singleFeemoney = singleFeemoney;
    }
}
