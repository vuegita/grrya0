package com.inso.modules.coin.core.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.inso.framework.utils.MD5;

import java.util.Date;

public class CoinAccountInfo {

    /**
     account_id            int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
     account_userid	    int(11) NOT NULL,
     account_username 	    varchar(255) NOT NULL ,

     account_address 		varchar(255) NOT NULL comment '地址',
     account_chain_type	varchar(255) NOT NULL comment '主链: ETH | TRX | BSC',

     account_remark        varchar(1000) NOT NULL DEFAULT '' comment '备注',
     */


    private long userid;
    private String username;

    private String agentname;
    private String staffname;
    private String parentname;
    private String grantfathername;

    private String address;
    private String networkType;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;
    private String remark;


    public static String getColumnPrefix(){
        return "account";
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getAgentname() {
        return agentname;
    }

    public void setAgentname(String agentname) {
        this.agentname = agentname;
    }

    public String getStaffname() {
        return staffname;
    }

    public void setStaffname(String staffname) {
        this.staffname = staffname;
    }


    public static String generateUsername(String address, CryptoNetworkType networkType)
    {
//        String str = "c_" + MD5.encode(address + networkType.getKey() + System.currentTimeMillis()).substring(0, 8) + "_" + address;
        String str = "c_" + address;
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

    public String getGrantfathername() {
        return grantfathername;
    }

    public void setGrantfathername(String grantfathername) {
        this.grantfathername = grantfathername;
    }

    public String getParentname() {
        return parentname;
    }

    public void setParentname(String parentname) {
        this.parentname = parentname;
    }
}
