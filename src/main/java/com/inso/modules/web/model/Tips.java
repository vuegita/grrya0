package com.inso.modules.web.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class Tips {

    public static String getColumnPrefix(){
        return "tips";
    }

    /**
     *
     tips_id                 int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

     tips_agentid            int(11)  NOT NULL DEFAULT 0,
     tips_agentname          varchar(50) NOT NULL DEFAULT '',
     tips_title              varchar(100) NOT NULL DEFAULT '' comment '标题',
     tips_content            varchar(3000) NOT NULL comment '描述',
     tips_type               varchar(255) NOT NULL DEFAULT '' comment '类型',

     tips_createtime         datetime NOT NULL,
     tips_status             varchar(20) NOT NULL DEFAULT 'enable' COMMENT 'enable|disable',
     tips_remark             varchar(2000) NOT NULL DEFAULT '' COMMENT '',
     *
     */
    private long id;

    private long agentid;
    private String agentname;

    private long belongAgentid;
    private String belongAgentname;

    private long staffid;
    private String staffname;


    private String title;
    private String content;
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


    public long getBelongAgentid() {
        return belongAgentid;
    }

    public void setBelongAgentid(long belongAgentid) {
        this.belongAgentid = belongAgentid;
    }

    public String getBelongAgentname() {
        return belongAgentname;
    }

    public void setBelongAgentname(String belongAgentname) {
        this.belongAgentname = belongAgentname;
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
