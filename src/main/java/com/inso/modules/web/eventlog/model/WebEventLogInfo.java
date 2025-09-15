package com.inso.modules.web.eventlog.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class WebEventLogInfo {

    /**
     *   log_type            varchar(255) NOT NULL comment '类型',
     *   log_title           varchar(100) NOT NULL default '' comment '标题',
     *   log_content         varchar(255) NOT NULL default '' comment '内容',
     *
     *   log_ip              varchar(100) NOT NULL default '' comment 'IP',
     *   log_useragent       varchar(500) NOT NULL default '' comment 'user-agent',
     *
     *   log_agentid         int(11) NOT NULL,
     *   log_agentname       varchar(255) NOT NULL default '' COMMENT '所属代理',
     *   log_userid          int(11) NOT NULL,
     *   log_username        varchar(255) NOT NULL default '' COMMENT '操作人'
     *
     *   log_createtime      datetime NOT NULL,
     *   log_remark          varchar(3000) NOT NULL DEFAULT '',
     */

    private long id;
    private String title;
    private String content;
    private String ip;
    private String useragent;

    private long agentid;
    private String agentname;

    private String operator;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;
    private String remark;

    public static String getColumnPrefix(){
        return "log";
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUseragent() {
        return useragent;
    }

    public void setUseragent(String useragent) {
        this.useragent = useragent;
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


    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
