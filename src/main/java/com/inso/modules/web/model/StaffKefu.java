package com.inso.modules.web.model;

import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

public class StaffKefu {

    public static String getColumnPrefix(){
        return "kefu";
    }

    /**
     *
     *   kefu_agentfid       			int(11)  NOT NULL ,
     *   kefu_agentname    			varchar(50) NOT NULL ,
     *   kefu_staffid       			int(11)  NOT NULL ,
     *   kefu_staffname    			varchar(50) NOT NULL ,
     *
     *   kefu_title   			        varchar(100) NOT NULL,
     *   kefu_describe 		        varchar(255) NOT NULL,
     *   kefu_icon       		        varchar(255) NOT NULL DEFAULT '',
     *   kefu_whatsapp       	        varchar(255) NOT NULL DEFAULT '',
     *   kefu_telegram       	        varchar(255) NOT NULL DEFAULT '',
     *   kefu_createtime  		        datetime NOT NULL,
     *   kefu_status                   varchar(20) NOT NULL DEFAULT 'enable' COMMENT 'enable|disable',
     *   kefu_remark 			        varchar(2000) NOT NULL DEFAULT '' COMMENT '',
     *
     */
    private long id;

    private long agentid;
    private String agentname;
    private long staffid;
    private String staffname;

    private String title;
    private String describe;
    private String icon;
    private String status;
    private String whatsapp;
    private String telegram;
    private long groupid;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;
    private String remark;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
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

    public long getGroupid() {
        return groupid;
    }

    public void setGroupid(long groupid) {
        this.groupid = groupid;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public String getTelegram() {
        return telegram;
    }

    public void setTelegram(String telegram) {
        this.telegram = telegram;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
