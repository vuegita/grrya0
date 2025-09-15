package com.inso.modules.passport.user.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class BuyVipOrderInfo {

    public static final int DEFAULT_MAX_ADD_CARD_SIZE = 3;

    /**
     uv_id       			int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
     uv_userid       		int(11) UNSIGNED NOT NULL ,
     uv_vip_type       	varchar(50) NOT NULL comment 'vip 类型,检举类型',
     uv_vipid       		int(11) UNSIGNED NOT NULL ,
     uv_status             varchar(50) NOT NULL comment 'enable|disable',
     uv_expires_time       datetime DEFAULT NULL comment '过期时间-保留参数',
     uv_createtime         datetime DEFAULT NULL comment '时间',
     */

    private String no;
    private long userid;
    private String username;
    private long agentid;
    private String agentname;
    private long staffid;
    private String staffname;
    private long vipid;
    private long vipLevel;
    private String vipType;
    private String vipName;
    private String status;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date expirestime;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;


    public static String getColumnPrefix(){
        return "order";
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

    public long getVipid() {
        return vipid;
    }

    public void setVipid(long vipid) {
        this.vipid = vipid;
    }

    public String getVipType() {
        return vipType;
    }

    public void setVipType(String vipType) {
        this.vipType = vipType;
    }

    public Date getExpirestime() {
        return expirestime;
    }

    public void setExpirestime(Date expirestime) {
        this.expirestime = expirestime;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getStaffname() {
        return staffname;
    }

    public void setStaffname(String staffname) {
        this.staffname = staffname;
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

    public String getVipName() {
        return vipName;
    }

    public void setVipName(String vipName) {
        this.vipName = vipName;
    }

    public long getVipLevel() {
        return vipLevel;
    }

    public void setVipLevel(long vipLevel) {
        this.vipLevel = vipLevel;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }
}
