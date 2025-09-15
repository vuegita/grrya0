package com.inso.modules.game.red_package.model;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

public class RedPPeriodInfo {
    /**
     *
     *   re_id       		   int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
     *   re_userid            int(11) NOT NULL,
     *   re_username 		   varchar(50) NOT NULL ,
     *
     *   re_total_bet_amount  decimal(18,2) NOT NULL DEFAULT 0 comment '投注总额',
     *   re_total_win_amount  decimal(18,2) NOT NULL DEFAULT 0 comment '中奖总额',
     *   re_total_feemoney    decimal(18,2) NOT NULL DEFAULT 0 comment '手续费',
     *   re_total_bet_count   int(11) NOT NULL DEFAULT 0 comment 'total bet order number',
     *   re_total_win_count   int(11) NOT NULL DEFAULT 0 comment 'total win order number',
     *   re_open_result       varchar(50) DEFAULT '' comment '开奖结果',
     *   re_open_mode         varchar(50) DEFAULT '' comment '开奖模式',
     *
     *   re_total_amount      decimal(18,2) NOT NULL comment '红包总金额',
     *   re_min_amount        decimal(18,2) NOT NULL comment '红包最小金额-最少值为1',
     *   re_max_amount        decimal(18,2) NOT NULL comment '红包最大金额',
     *   re_total_count       int(11) NOT NULL DEFAULT 0 comment '红包总个数',
     *   re_complete_count    int(11) NOT NULL DEFAULT 0 comment '红包已领取个数',
     *   re_complete_amount   decimal(18,2) NOT NULL DEFAULT 0 comment '红包已领取总金额',
     *   re_type              varchar(20) NOT NULL comment '红包类型',
     *   re_status            varchar(20) NOT NULL,
     *
     *   re_createtime        datetime DEFAULT NULL ,
     *   re_endtime           datetime DEFAULT NULL ,
     *   re_remark            varchar(1000) NOT NULL,
     *
     */

    private long id;

    private String orderno;

    private long userid;
    private String username;

    private BigDecimal totalBetAmount;
    private BigDecimal totalWinAmount;
    private BigDecimal totalFeemoney;
    private long totalWinCount;
    private long totalBetCount;
    private long openResult;
    private String openMode;

    private BigDecimal totalAmount;
    private long totalCount;
    private long completeCount;
    private BigDecimal completeAmount;
    private String rpType;
    private String status;

    /*** 创建主体 => RedpCreatorType ***/
    private String creatorType;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date endtime;
    private String remark;

    @JSONField(serialize = false, deserialize = false)
    public RedPCreatorType getRedPCreatorType()
    {
        return RedPCreatorType.getType(creatorType);
    }

    public static String getColumnPrefix(){
        return "period";
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public BigDecimal getTotalBetAmount() {
        return totalBetAmount;
    }

    public void setTotalBetAmount(BigDecimal totalBetAmount) {
        this.totalBetAmount = totalBetAmount;
    }

    public BigDecimal getTotalWinAmount() {
        return totalWinAmount;
    }

    public void setTotalWinAmount(BigDecimal totalWinAmount) {
        this.totalWinAmount = totalWinAmount;
    }

    public long getTotalWinCount() {
        return totalWinCount;
    }

    public void setTotalWinCount(long totalWinCount) {
        this.totalWinCount = totalWinCount;
    }

    public long getTotalBetCount() {
        return totalBetCount;
    }

    public void setTotalBetCount(long totalBetCount) {
        this.totalBetCount = totalBetCount;
    }

    public long getOpenResult() {
        return openResult;
    }

    public void setOpenResult(long openResult) {
        this.openResult = openResult;
    }

    public String getOpenMode() {
        return openMode;
    }

    public void setOpenMode(String openMode) {
        this.openMode = openMode;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public long getCompleteCount() {
        return completeCount;
    }

    public void setCompleteCount(long completeCount) {
        this.completeCount = completeCount;
    }

    public BigDecimal getCompleteAmount() {
        return completeAmount;
    }

    public void setCompleteAmount(BigDecimal completeAmount) {
        this.completeAmount = completeAmount;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public BigDecimal getTotalFeemoney() {
        return totalFeemoney;
    }

    public void setTotalFeemoney(BigDecimal totalFeemoney) {
        this.totalFeemoney = totalFeemoney;
    }

    public String getCreatorType() {
        return creatorType;
    }

    public void setCreatorType(String creatorType) {
        this.creatorType = creatorType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRpType() {
        return rpType;
    }

    public void setRpType(String rpType) {
        this.rpType = rpType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOrderno() {
        return orderno;
    }

    public void setOrderno(String orderno) {
        this.orderno = orderno;
    }
}
