package com.inso.modules.paychannel.model;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.inso.framework.utils.FastJsonHelper;

public class ChannelInfo {
    /**
     *   channel_id       			int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
     *   channel_title             varchar(100) NOT NULL comment '' ,
     *   channel_secret            varchar(1000) NOT NULL comment '支付通道秘钥相关信息' ,
     *   channel_status            varchar(20) NOT NULL DEFAULT 'enable' COMMENT 'enable|disable|test=不对外放可通过个别设置',
     *   channel_type              varchar(100) NOT NULL comment '通道类型' ,
     *   channel_createtime 		datetime NOT NULL ,
     *   channel_remark            varchar(255) DEFAULT '',
     */

    private long id;
    private String name;
    private String secret;
    private String status;
    private String type;
    /*** 产品类型 ***/
    private String productType;
    private String currencyType;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;
    private String remark;
    private long sort;

    private BigDecimal feerate;
    private BigDecimal extraFeemoney;

    @JSONField(serialize = false, deserialize = false)
    private JSONObject secretInfo;

    public static String getColumnPrefix(){
        return "channel";
    }

    public JSONObject getSecretInfo() {
        if(secretInfo == null)
        {
            secretInfo = FastJsonHelper.toJSONObject(secret);
        }
        return secretInfo;
    }

    public long getSort() {
        return sort;
    }

    public void setSort(long sort) {
        this.sort = sort;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    @JSONField(serialize = false, deserialize = false)
    public ChannelType getChannelType()
    {
        return ChannelType.getType(type);
    }

    @JSONField(serialize = false, deserialize = false)
    public PayProductType getProduct()
    {
        return PayProductType.getType(productType);
    }

    public String getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }

    public BigDecimal getFeerate() {
        return feerate;
    }

    public void setFeerate(BigDecimal feerate) {
        this.feerate = feerate;
    }

    public BigDecimal getExtraFeemoney() {
        return extraFeemoney;
    }

    public void setExtraFeemoney(BigDecimal extraFeemoney) {
        this.extraFeemoney = extraFeemoney;
    }
}
