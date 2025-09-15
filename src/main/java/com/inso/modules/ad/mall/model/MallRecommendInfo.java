package com.inso.modules.ad.mall.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;
import java.util.Date;

public class MallRecommendInfo {

    /**
     *   mc_id                 int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
     *   mc_merchantid         int(11) UNSIGNED NOT NULL ,
     *   mc_merchantname       varchar(100) NOT NULL DEFAULT '' comment  '所属商家',
     *   mc_materielid         int(11) UNSIGNED NOT NULL ,
     *   mc_status             varchar(50) NOT NULL comment 'enable|disable',
     *   mc_createtime         datetime DEFAULT NULL comment '创建时间',
     *   mc_remark             varchar(5000) NOT NULL DEFAULT '' COMMENT '',
     */

    private long id;
    private long merchantid;
    private String merchantname;
    private long materielid;
    private long categoryid;
    private String type;
    private long sort;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;

    /*** 后台才有 ***/
    private BigDecimal price;

    public static String getColumnPrefix(){
        return "recommend";
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

    public long getMaterielid() {
        return materielid;
    }

    public void setMaterielid(long materielid) {
        this.materielid = materielid;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public long getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(long categoryid) {
        this.categoryid = categoryid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getSort() {
        return sort;
    }

    public void setSort(long sort) {
        this.sort = sort;
    }
}
