package com.inso.modules.web.settle.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;
import java.util.Date;

public class SettleWithdrawReportInfo {

    /**

     report_id                     int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

     report_agentid                int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
     report_agentname              varchar(255) NOT NULL comment  '',

     report_createtime             datetime NOT NULL,
     report_remark                 varchar(5000) NOT NULL DEFAULT '' comment '',

     */

    private long id;

    private long agentid;
    private String agentname;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;

    private String remark;

    public static String getColumnPrefix(){
        return "report";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
}
