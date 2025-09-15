package com.inso.modules.ad.mall.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class MallBuyerAddrInfo {

    /**
     address_id                 int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
     address_location           varchar(50) NOT NULL comment '位置明细,以逗号隔开',
     address_userid             int(11) UNSIGNED NOT NULL ,
     address_username           varchar(100) NOT NULL DEFAULT '' comment  '所属商家',
     address_status             varchar(50) NOT NULL comment 'enable|disable',
     address_createtime         datetime DEFAULT NULL comment '创建时间',
     address_remark             varchar(5000) NOT NULL DEFAULT '' COMMENT '',
     */

    private long id;
    private String location;
    private String phone;
    private long userid;
    private String username;
    private String status;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;

    public static String getColumnPrefix(){
        return "address";
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
