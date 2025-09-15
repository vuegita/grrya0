package com.inso.modules.game.fm.model;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

public class FMProductInfo {
    /**
     *
     *   product_id       		            int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
     *   product_title                     varchar(20) NOT NULL comment '标题',
     *   product_desc                      varchar(100) NOT NULL comment '介绍',
     *   product_time_horizon              int(11) NOT NULL comment ' 投资期限（1日，3日，7日）',
     *
     *   product_return_expected_start   	decimal(18,2) NOT NULL comment '预期收益范围start',
     *   product_return_expected_end	    decimal(18,2) NOT NULL comment '预期收益范围 end',
     *   product_return_real_rate   	    decimal(18,2) NOT NULL comment '实际收益率',
     *
     *   product_sale_estimate             int(11) NOT NULL DEFAULT 0 comment '预售总份额',
     *   product_sale_real                 int(11) NOT NULL DEFAULT 0 comment '实际总份额',
     *   product_sale_actual               int(11) NOT NULL DEFAULT 0 comment '实际已售份额',
     *
     *   product_limit_min_sale            int(11) NOT NULL DEFAULT 0 comment '限售最小额度',
     *   product_limit_max_sale            int(11) NOT NULL DEFAULT 0 comment '限售最大额度',
     *   product_limit_min_bets            int(11) NOT NULL DEFAULT 0 comment '最低投注额',
     *   product_limit_min_balance         decimal(18,2) NOT NULL comment '最低帐户余额',
     *
     *   product_status     			    varchar(50) NOT NULL comment 'new=草稿 | saling=销售中 | saled=已售磬 | finish=结束' ,
     *
     *  product_begin_sale_time           datetime NOT NULL comment '开售时间',
     *  product_end_sale_time             datetime NOT NULL comment '停售时间',
     *   product_createtime       		    datetime NOT NULL comment '创建时间',
     *   product_endtime                   datetime NOT NULL comment '结束时间',
     */

    private long id;
    private String title;
    private String desc;
    private long timeHorizon;

    private String type;

    private BigDecimal returnExpectedStart;
    private BigDecimal returnExpectedEnd;
    private BigDecimal returnRealRate;
    private BigDecimal returnRealInterest;

    private long saleEstimate;
    private long saleReal;
    private long saleActual;

    private long limitMinSale;
    private long limitMaxSale;
    private long limitMinBets;
    private BigDecimal limitMinBalance;
    private String status;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date endtime;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date beginSaleTime;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date endSaleTime;


    public static String getColumnPrefix(){
        return "product";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public long getTimeHorizon() {
        return timeHorizon;
    }

    public void setTimeHorizon(long timeHorizon) {
        this.timeHorizon = timeHorizon;
    }

    public BigDecimal getReturnExpectedStart() {
        return returnExpectedStart;
    }

    public void setReturnExpectedStart(BigDecimal returnExpectedStart) {
        this.returnExpectedStart = returnExpectedStart;
    }

    public BigDecimal getReturnExpectedEnd() {
        return returnExpectedEnd;
    }

    public void setReturnExpectedEnd(BigDecimal returnExpectedEnd) {
        this.returnExpectedEnd = returnExpectedEnd;
    }

    public BigDecimal getReturnRealRate() {
        return returnRealRate;
    }

    public void setReturnRealRate(BigDecimal returnRealRate) {
        this.returnRealRate = returnRealRate;
    }

    public long getSaleEstimate() {
        return saleEstimate;
    }

    public void setSaleEstimate(long saleEstimate) {
        this.saleEstimate = saleEstimate;
    }

    public long getSaleReal() {
        return saleReal;
    }

    public void setSaleReal(long saleReal) {
        this.saleReal = saleReal;
    }

    public long getSaleActual() {
        return saleActual;
    }

    public void setSaleActual(long saleActual) {
        this.saleActual = saleActual;
    }

    public long getLimitMinSale() {
        return limitMinSale;
    }

    public void setLimitMinSale(long limitMinSale) {
        this.limitMinSale = limitMinSale;
    }

    public long getLimitMaxSale() {
        return limitMaxSale;
    }

    public void setLimitMaxSale(long limitMaxSale) {
        this.limitMaxSale = limitMaxSale;
    }

    public long getLimitMinBets() {
        return limitMinBets;
    }

    public void setLimitMinBets(long limitMinBets) {
        this.limitMinBets = limitMinBets;
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

    public Date getEndtime() {
        return endtime;
    }

    public void setEndtime(Date endtime) {
        this.endtime = endtime;
    }


    public BigDecimal getLimitMinBalance() {
        return limitMinBalance;
    }

    public void setLimitMinBalance(BigDecimal limitMinBalance) {
        this.limitMinBalance = limitMinBalance;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getReturnRealInterest() {
        return returnRealInterest;
    }

    public void setReturnRealInterest(BigDecimal returnRealInterest) {
        this.returnRealInterest = returnRealInterest;
    }


    public Date getBeginSaleTime() {
        return beginSaleTime;
    }

    public void setBeginSaleTime(Date beginSaleTime) {
        this.beginSaleTime = beginSaleTime;
    }

    public Date getEndSaleTime() {
        return endSaleTime;
    }

    public void setEndSaleTime(Date endSaleTime) {
        this.endSaleTime = endSaleTime;
    }
}
