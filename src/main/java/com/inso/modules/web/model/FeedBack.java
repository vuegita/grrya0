package com.inso.modules.web.model;

import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

public class FeedBack {

    public static String getColumnPrefix(){
        return "feedback";
    }

    /**
     *
     feedback_id       			    int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

     feedback_agentid       			int(11)  NOT NULL ,
     feedback_agentname    			varchar(50) NOT NULL ,
     feedback_staffid       			int(11)  NOT NULL ,
     feedback_staffname    			varchar(50) NOT NULL ,

     feedback_userid       			int(11)  NOT NULL comment '会员id',
     feedback_username    			    varchar(50) NOT NULL comment '会员用户名',

     feedback_type   			        varchar(20) NOT NULL comment '问题类型',
     feedback_title   			        varchar(50) NOT NULL,
     feedback_content   			    varchar(255) NOT NULL,
     feedback_reply   			        varchar(255) NOT NULL comment '回复内容',
     feedback_createtime  		        datetime NOT NULL,
     feedback_status                   varchar(20) NOT NULL DEFAULT 'enable' COMMENT 'waiting|finish',
     feedback_remark 			        varchar(500) NOT NULL DEFAULT '' COMMENT '',
     *
     */
    private long id;

    private long agentid;
    private String agentname;
    private long staffid;
    private String staffname;

    private long userid;
    private String username;

    private String type;
    private String title;
    private String content;
    private String reply;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;

    private String status;
    private String remark;

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
