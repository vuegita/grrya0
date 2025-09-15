package com.inso.modules.ad.core.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;
import java.util.Date;

public class AdCategoryInfo {

    /**
     category_id                 int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
     category_name               varchar(50) NOT NULL comment '名称',
     category_status             varchar(50) NOT NULL comment 'enable|disable',
     category_createtime         datetime DEFAULT NULL comment '创建时间',
     */

    private long id;
    private String key;
    private String name;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private BigDecimal returnRate;
    private String status;
    private long sort;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;

    public static String getColumnPrefix(){
        return "category";
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

    public long getSort() {
        return sort;
    }

    public void setSort(long sort) {
        this.sort = sort;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public BigDecimal getReturnRate() {
        return returnRate;
    }

    public void setReturnRate(BigDecimal returnRate) {
        this.returnRate = returnRate;
    }
}
