package com.inso.modules.ad.mall.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class MallStoreInfo {

    /**
     store_id                 int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
     store_name               varchar(50) NOT NULL comment '名称',
     store_merchantid         int(11) UNSIGNED NOT NULL ,
     store_merchantname       varchar(100) NOT NULL DEFAULT '' comment  '所属商家',
     store_status             varchar(50) NOT NULL comment 'enable|disable',
     store_createtime         datetime DEFAULT NULL comment '创建时间',
     store_remark             varchar(5000) NOT NULL DEFAULT '' COMMENT '',
     */

    private long id;
    private String name;
    private long userid;
    private String username;
    private String status;
    private String level;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;

    public static String getColumnPrefix(){
        return "store";
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
