package com.inso.modules.ad.mall.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.inso.framework.utils.FastJsonHelper;

import java.math.BigDecimal;
import java.util.Date;

public class PromotionInfo {

    /**
     inventory_id                     int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

     inventory_userid                 int(11) UNSIGNED NOT NULL comment '商家ID',
     inventory_username               varchar(255) NOT NULL comment  '',

     inventory_price                  decimal(18,2) NOT NULL DEFAULT 0 comment '商品单价',
     inventory_quantity               int(11) UNSIGNED NOT NULL comment '库存数量',

     inventory_materielid             int(11) UNSIGNED NOT NULL ,
     inventory_categoryid             int(11) UNSIGNED NOT NULL ,

     inventory_status                 varchar(50) NOT NULL comment '订单状态表示要确认会员是否完全任务,对应状态为 new|waiting|realized|failed',
     inventory_createtime             datetime DEFAULT NULL comment '创建时间',
     inventory_remark                 varchar(5000) NOT NULL DEFAULT '' COMMENT '',
     */

    private long id;

    private long userid;
    private String username;

    private BigDecimal price;
    private BigDecimal totalAmount;

    private String status;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;
    private String remark;

    public static String getColumnPrefix(){
        return "promotion";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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


    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
