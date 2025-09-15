package com.inso.modules.web.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;
import java.util.Date;

public class VIPInfo {

    /**
     *   vip_id       			int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
     *   vip_type       		varchar(50) NOT NULL comment 'vip 类型,检举类型',
     *   vip_level       		int(11) UNSIGNED NOT NULL comment 'vip等级',
     *   vip_name       		varchar(50) NOT NULL comment 'vip名称',
     *   vip_status       		varchar(50) NOT NULL comment 'enable|disable',
     *   vip_createtime  		datetime DEFAULT NULL comment '创建时间',
     *   vip_remark            varchar(512) NOT NULL DEFAULT '' COMMENT '',
     */

    private long id;
    private String type;
    private long level;
    private String name;
    private String status;
    private BigDecimal price;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;
    private String remark;

    public static String getColumnPrefix(){
        return "vip";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getLevel() {
        return level;
    }

    public void setLevel(long level) {
        this.level = level;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
