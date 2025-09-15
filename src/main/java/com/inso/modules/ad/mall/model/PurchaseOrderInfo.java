package com.inso.modules.ad.mall.model;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.Date;

public class PurchaseOrderInfo {

    /**
     order_no           varchar(50) NOT NULL comment  '系统订单号',

     order_userid       int(11) UNSIGNED NOT NULL comment '商家ID',
     order_username     varchar(255) NOT NULL comment  '',
     order_agentid      int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
     order_agentname    varchar(50) NOT NULL comment  '',
     order_staffid      int(11) NOT NULL DEFAULT 0,
     order_staffname    varchar(50) NOT NULL comment  '',

     order_price              decimal(18,2) NOT NULL DEFAULT 0 comment '商品单价',
     order_quantity           int(11) UNSIGNED NOT NULL comment '采购数量',
     order_total_amount       decimal(18,2) NOT NULL DEFAULT 0 comment '采购总价',
     order_real_amount        decimal(18,2) NOT NULL DEFAULT 0 comment '实际总额',

     order_materielid         int(11) UNSIGNED NOT NULL ,
     order_categoryid         int(11) UNSIGNED NOT NULL ,

     order_status             varchar(50) NOT NULL comment '订单状态表示要确认会员是否完全任务,对应状态为 new|waiting|realized|failed',
     order_createtime         datetime DEFAULT NULL comment '创建时间',
     order_remark             varchar(5000) NOT NULL DEFAULT '' COMMENT '',
     */

    private String no;

    private long userid;
    private String username;
    private long agentid;
    private String agentname;
    private long staffid;
    private String staffname;

    private BigDecimal price;
    private long quantity;
    private BigDecimal totalAmount;
    private BigDecimal realAmount;

    private long materielid;
    private long categoryid;

    private String status;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;
    private String remark;

    public static String getColumnPrefix(){
        return "order";
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getRealAmount() {
        return realAmount;
    }

    public void setRealAmount(BigDecimal realAmount) {
        this.realAmount = realAmount;
    }

    public long getMaterielid() {
        return materielid;
    }

    public void setMaterielid(long materielid) {
        this.materielid = materielid;
    }

    public long getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(long categoryid) {
        this.categoryid = categoryid;
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

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
