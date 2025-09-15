package com.inso.modules.coin.core.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.inso.framework.utils.MD5;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.Status;

import java.math.BigDecimal;
import java.util.Date;

public class CoinSettleConfig {

    /**
     config_agentid	      int(11) NOT NULL,
     config_agentname 	      varchar(255) NOT NULL ,

     config_network_type     varchar(255) NOT NULL comment '网络类型',
     config_receiv_address   varchar(255) NOT NULL comment '收款账号',
     config_share_ratio      decimal(18,3) NOT NULL comment '分成比例',

     config_status           varchar(20) NOT NULL comment '状态',
     config_createtime  	  datetime DEFAULT NULL ,
     config_remark           varchar(1000) NOT NULL DEFAULT '' comment '备注',
     */

    private long id;

    private String key;
    private String dimensionType;

    private String receivAddress;
    private String networkType;
    private BigDecimal shareRatio;
    private String status;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;
    private String remark;


    public static String getColumnPrefix(){
        return "config";
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

    public static void main(String[] args) {
        String username = generateUsername("abc", CryptoNetworkType.TRX_NILE);
        System.out.println(username);
    }

    public String getReceivAddress() {
        return receivAddress;
    }

    public void setReceivAddress(String receivAddress) {
        this.receivAddress = receivAddress;
    }

    public BigDecimal getShareRatio() {
        return shareRatio;
    }

    public void setShareRatio(BigDecimal shareRatio) {
        this.shareRatio = shareRatio;
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

    @JSONField(serialize = false, deserialize = false)
    public BigDecimal getShareAmount(BigDecimal totalAmount)
    {
        if(StringUtils.isEmpty(receivAddress))
        {
            return BigDecimal.ZERO;
        }
        BigDecimal value;
        if(Status.getType(status) == Status.ENABLE){
            value = totalAmount.multiply(shareRatio);
        }else{
            value = totalAmount.multiply(BigDecimal.ZERO);
        }

        return value;
    }

    @JSONField(serialize = false, deserialize = false)
    public boolean verify()
    {
        boolean enable = Status.getType(status) == Status.ENABLE;
        return !StringUtils.isEmpty(receivAddress) && enable;
    }

}
