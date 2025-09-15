package com.inso.modules.game.task_checkin.model;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

public class TaskCheckinOrderInfo {

//    order_no                    	varchar(50) NOT NULL comment '内部系统-订单号',
//    order_userid	                int(11) NOT NULL,
//    order_username    			varchar(50) NOT NULL comment  '',
//    order_agentid 	            int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
//    order_amount              	decimal(18,2) NOT NULL comment '赠送总额-从配置读取',
//    order_status               	varchar(20) NOT NULL  comment '', new | realized
//    order_pdate            		date NOT NULL comment '签到日期',
//    order_createtime       		datetime NOT NULL comment '签到时间',

    private String no;
    private long userid;
    private String username;
    private long agentid;
    private BigDecimal amount;
    private String status;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;

    public static String getColumnPrefix(){
        return "order";
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public String getUsername(){ return  username;}

    public void setUsername(String username){this.username=username; }

    public long getAgentid(){ return agentid;}

    public void setAgentid(long agentid){ this.agentid=agentid; }

    public BigDecimal getAmount(){ return amount; }

    public void setAmount(BigDecimal amount){ this.amount=amount; }

    public String getStatus(){ return status; }

    public void setStatus(String status){ this.status=status; }

    public Date getCreatetime(){ return createtime; }

    public void setCreatetime(Date createtime){ this.createtime=createtime; }

}
