package com.inso.modules.game.red_package.model;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

public class RedPReceivOrderInfo {

    /**
     *   order_no                    	varchar(50) NOT NULL comment '内部系统-订单号',
     *   order_rpid                  	int(11) NOT NULL comment '红包id',
     *   order_userid	                int(11) NOT NULL,
     *   order_username    			varchar(50) NOT NULL comment  '',
     *   order_rp_type                 varchar(50) NOT NULL comment  '红包类型',
     *   order_agentid 	            int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
     *   order_status               	varchar(20) NOT NULL  comment '',
     *   order_amount                  decimal(18,2) NOT NULL comment '领取金额',
     *   order_index                   int(11) NOT NULL comment '领取顺序',
     *   order_createtime       		datetime NOT NULL,
     *   order_remark             		varchar(1000) DEFAULT '',
     * @return
     */

    private String no;
    private long rpid;
    private long userid;
    private String username;
    private String rpType;
//    private long agentid;
    private String status;
    private BigDecimal amount;
    private long index;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;
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

    public String getRpType() {
        return rpType;
    }

    public void setRpType(String rpType) {
        this.rpType = rpType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
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


}
