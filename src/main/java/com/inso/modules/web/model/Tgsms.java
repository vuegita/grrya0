package com.inso.modules.web.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class Tgsms {

    public static String getColumnPrefix(){
        return "tgsms";
    }

    /**
     *
     tgsms_id                 int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

     tgsms_staffid            int(11)  NOT NULL DEFAULT 0 ,
     tgsms_staffname          varchar(50) NOT NULL DEFAULT '' comment '' ,
     tgsms_agentid            int(11)  NOT NULL DEFAULT 0,
     tgsms_agentname          varchar(50) NOT NULL DEFAULT '',

     tgsms_rbtoken            varchar(255) NOT NULL DEFAULT '' comment '机器人token ',
     tgsms_chatid             varchar(255) NOT NULL comment 'tg群chatid ',
     tgsms_type               varchar(255) NOT NULL DEFAULT '' comment '类型',

     tgsms_createtime         datetime NOT NULL,
     tgsms_status             varchar(20) NOT NULL DEFAULT 'enable' COMMENT 'enable|disable',
     tgsms_remark             varchar(2000) NOT NULL DEFAULT '' COMMENT '',
     *
     */
    private long id;

    private long agentid;
    private String agentname;


    private long staffid;
    private String staffname;


    private String rbtoken;
    private String chatid;
    private String type;
    private String status;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;
    private String remark;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getChatid() {
        return chatid;
    }

    public void setChatid(String chatid) {
        this.chatid = chatid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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


    public String getRbtoken() {
        return rbtoken;
    }

    public void setRbtoken(String rbtoken) {
        this.rbtoken = rbtoken;
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
}
