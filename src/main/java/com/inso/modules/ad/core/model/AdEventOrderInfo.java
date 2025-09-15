package com.inso.modules.ad.core.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;
import java.util.Date;

public class AdEventOrderInfo {

    /**
     order_id           int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
     order_materiel_id  int(11) UNSIGNED NOT NULL comment '物料id',
     order_event_type   varchar(50) NOT NULL comment '事件类型=download|buy|like',

     order_userid       int(11) UNSIGNED NOT NULL comment '用户id',
     order_username     varchar(50) NOT NULL comment  '',
     order_agentid 	   int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
     order_agentname    varchar(50) NOT NULL comment  '',
     order_staffid	   int(11) NOT NULL DEFAULT 0,
     order_staffname    varchar(50) NOT NULL comment  '',

     order_status       varchar(50) NOT NULL comment '状态表示要确认会员是否完全任务,对应状态为 new|waiting|realized|failed',
     order_createtime   datetime DEFAULT NULL comment '创建时间',
     order_remark       varchar(512) NOT NULL DEFAULT '' COMMENT '',
     */

    public static final String SHOP_FROM_VALUE_BALANCE = "balance";
    public static final String SHOP_FROM_VALUE_INVENTORY = "inventory";

    private String no;
    private long materielId;
    private String materielName;
    private String eventType;

    private long userid;
    private String username;
    private long agentid;
    private String agentname;
    private long staffid;
    private String staffname;

    private BigDecimal price;
    private long quantity;
    private BigDecimal brokerage;
    private BigDecimal amount;
    private String status;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;
    private String remark;

    private String materielThumb;
    private String materielDesc;
    private BigDecimal materielPrice;
    private long materielCategoryid;

    //
    private long merchantid;
    private String merchantname;
    private String shippingStatus;
    private String shippingTrackno;
    private long buyerAddressid;
    private String buyerLocation;
    private String buyerPhone;
    private String shopFrom;


    public static String getColumnPrefix(){
        return "order";
    }

    public long getMaterielId() {
        return materielId;
    }

    public void setMaterielId(long materielId) {
        this.materielId = materielId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
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

    public String getMaterielName() {
        return materielName;
    }

    public void setMaterielName(String materielName) {
        this.materielName = materielName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }


    public String getMaterielThumb() {
        return materielThumb;
    }

    public void setMaterielThumb(String materielThumb) {
        this.materielThumb = materielThumb;
    }

    public String getMaterielDesc() {
        return materielDesc;
    }

    public void setMaterielDesc(String materielDesc) {
        this.materielDesc = materielDesc;
    }

    public BigDecimal getMaterielPrice() {
        return materielPrice;
    }

    public void setMaterielPrice(BigDecimal materielPrice) {
        this.materielPrice = materielPrice;
    }

    public long getMaterielCategoryid() {
        return materielCategoryid;
    }

    public void setMaterielCategoryid(long materielCategoryid) {
        this.materielCategoryid = materielCategoryid;
    }


    public long getMerchantid() {
        return merchantid;
    }

    public void setMerchantid(long merchantid) {
        this.merchantid = merchantid;
    }

    public String getMerchantname() {
        return merchantname;
    }

    public void setMerchantname(String merchantname) {
        this.merchantname = merchantname;
    }

    public String getShippingStatus() {
        return shippingStatus;
    }

    public void setShippingStatus(String shippingStatus) {
        this.shippingStatus = shippingStatus;
    }

    public String getShippingTrackno() {
        return shippingTrackno;
    }

    public void setShippingTrackno(String shippingTrackno) {
        this.shippingTrackno = shippingTrackno;
    }

    public long getBuyerAddressid() {
        return buyerAddressid;
    }

    public void setBuyerAddressid(long buyerAddressid) {
        this.buyerAddressid = buyerAddressid;
    }

    public String getBuyerPhone() {
        return buyerPhone;
    }

    public void setBuyerPhone(String buyerPhone) {
        this.buyerPhone = buyerPhone;
    }

    public String getBuyerLocation() {
        return buyerLocation;
    }

    public void setBuyerLocation(String buyerLocation) {
        this.buyerLocation = buyerLocation;
    }

    public BigDecimal getBrokerage() {
        return brokerage;
    }

    public void setBrokerage(BigDecimal brokerage) {
        this.brokerage = brokerage;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public String getShopFrom() {
        return shopFrom;
    }

    public void setShopFrom(String shopFrom) {
        this.shopFrom = shopFrom;
    }
}
