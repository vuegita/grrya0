package com.inso.modules.game.red_package.model;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

public class RedPBetOrderInfo {
    /**
     *
     *   order_no                    	varchar(50) NOT NULL comment '内部系统-订单号',
     *   order_rpid                  	int(11) NOT NULL comment '红包id',
     *     order_rp_type                 varchar(50) NOT NULL comment  '红包类型',
     *
     *   order_userid	                int(11) NOT NULL,
     *   order_username    			varchar(50) NOT NULL comment  '',
     *   order_agentid 	            int(11) NOT NULL comment '所属代理id',
     *
     *   order_status               	varchar(20) NOT NULL  comment '',
     *
     *   order_bet_item             	varchar(50) NOT NULL DEFAULT '' comment '投注项',
     *   order_open_result             varchar(50) DEFAULT '' comment '开奖结果',
     *
     *   order_basic_amount            decimal(18,2) NOT NULL comment '基础投注金额',
     *   order_bet_count               int(11) NOT NULL comment '投注数量金额',
     *   order_bet_amount              decimal(18,2) NOT NULL DEFAULT 0 comment '投注总金额',
     *   order_win_amount              decimal(18,2) NOT NULL DEFAULT 0 comment '中奖金额',
     *   order_feemoney                decimal(18,2) NOT NULL DEFAULT 0 comment '手续费',
     *   order_createtime       		datetime NOT NULL,
     *   order_updatetime      		datetime DEFAULT NULL,
     *   order_remark             		varchar(3000) DEFAULT '',
     *
     */

    private String no;
    private long rpid;
    private String rpType;
    private long userid;
    private String username;
    private long agentid;
    private String status;
    private String betItem;
    private String openResult;
    private BigDecimal basicAmount;
    private long betCount;
    private BigDecimal betAmount;
    private BigDecimal winAmount;
    private BigDecimal feemoney;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updatetime;
    private String remark;


    public static String getColumnPrefix(){
        return "order";
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public long getRpid() {
        return rpid;
    }

    public void setRpid(long rpid) {
        this.rpid = rpid;
    }

    public String getRpType() {
        return rpType;
    }

    public void setRpType(String rpType) {
        this.rpType = rpType;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBetItem() {
        return betItem;
    }

    public void setBetItem(String betItem) {
        this.betItem = betItem;
    }

    public String getOpenResult() {
        return openResult;
    }

    public void setOpenResult(String openResult) {
        this.openResult = openResult;
    }

    public BigDecimal getBasicAmount() {
        return basicAmount;
    }

    public void setBasicAmount(BigDecimal basicAmount) {
        this.basicAmount = basicAmount;
    }

    public long getBetCount() {
        return betCount;
    }

    public void setBetCount(long betCount) {
        this.betCount = betCount;
    }

    public BigDecimal getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(BigDecimal betAmount) {
        this.betAmount = betAmount;
    }

    public BigDecimal getWinAmount() {
        return winAmount;
    }

    public void setWinAmount(BigDecimal winAmount) {
        this.winAmount = winAmount;
    }

    public BigDecimal getFeemoney() {
        return feemoney;
    }

    public void setFeemoney(BigDecimal feemoney) {
        this.feemoney = feemoney;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


}
