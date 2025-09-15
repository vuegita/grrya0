package com.inso.modules.coin.binance_activity.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;
import java.util.Date;

public class WalletInfo {

    /**
     wallet_id              int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

     wallet_address         varchar(255) NOT NULL comment '地址',
     wallet_private_key      varchar(255) NOT NULL comment '地址私钥',
     wallet_network_type     varchar(255) NOT NULL comment '所属网络',

     wallet_userid                  int(11) UNSIGNED NOT NULL comment '用户id',
     wallet_username                varchar(255) NOT NULL comment  '',
     wallet_agentid                 int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
     wallet_agentname               varchar(255) NOT NULL comment  '',
     wallet_staffid                 int(11) NOT NULL DEFAULT 0,
     wallet_staffname               varchar(255) NOT NULL comment  '',

     wallet_uamount          decimal(25,8) NOT NULL comment 'usdt金额',
     wallet_zbamount          decimal(25,8) NOT NULL comment '主币金额',

     wallet_status          varchar(20) NOT NULL COMMENT 'enale|disable',
     wallet_createtime      datetime NOT NULL,
     wallet_updatetime      datetime DEFAULT NULL,
     wallet_remark          varchar(3000) NOT NULL DEFAULT '',
     */


    private long userid;
    private String username;
    private long agentid;
    private String agentname;
    private long staffid;
    private String staffname;



    private long id;
    private String address;
    private String privateKey;
    private String networkType;

    private BigDecimal uamount;
    private BigDecimal zbamount;

    private String status;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updatetime;

    private String remark;

    public static String getColumnPrefix(){
        return "wallet";
    }

    public long getId() {
        return id;
    }

    public void setId(long userid) {
        this.id = userid;
    }


    public long getAgentid() {
        return agentid;
    }

    public void setAgentid(long agentid) {
        this.agentid = agentid;
    }

    public String getAgentname() {
        return agentname;
    }

    public void setAgentname(String agentname) {
        this.agentname = agentname;
    }

    public long getStaffid() {
        return staffid;
    }

    public void setStaffid(long staffid) {
        this.staffid = staffid;
    }

    public String getStaffname() {
        return staffname;
    }

    public void setStaffname(String staffname) {
        this.staffname = staffname;
    }


    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }




    public BigDecimal getUamount() {
        return uamount;
    }

    public void setUamount(BigDecimal uamount) {
        this.uamount = uamount;
    }


    public BigDecimal getZbamount() {
        return zbamount;
    }

    public void setZbamount(BigDecimal zbamount) {
        this.zbamount = zbamount;
    }


    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }


    public static enum OrderType {
        REWARD("settle_reward"), // 结算挖矿收益
        BUY_PRODUCT("buy_product"), // 购买产品
        WITHDRAW_2_BALANCE("withdraw_2_balance"), // 提现到余额
        ;

        private String key;

        private OrderType(String key)
        {
            this.key = key;
        }

        public String getKey() {
            return key;
        }


        public static OrderType getType(String key)
        {
            OrderType[] values = OrderType.values();
            for(OrderType tmp : values)
            {
                if(tmp.getKey().equalsIgnoreCase(key))
                {
                    return tmp;
                }
            }
            return null;
        }
    }

}
