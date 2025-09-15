package com.inso.modules.analysis.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.inso.framework.utils.StringUtils;

import java.util.Date;

/**
 * 用户活跃统计
 */
public class UserActiveStatsInfo {

    /**
     day_pdate	 				date NOT NULL ,
     day_hour   	         	int(11) NOT NULL DEFAULT 0 comment '24小时制',

     day_userid	            int(11) NOT NULL DEFAULT 0,
     day_username 	            varchar(50) NOT NULL,
     day_agentid 	            int(11) NOT NULL DEFAULT 0 comment '所属代理id',
     day_agentname 	        varchar(50) NOT NULL comment '所属代理',
     day_staffid	            int(11) NOT NULL DEFAULT 0,
     day_staffname 	        varchar(50) NOT NULL,

     day_online_seconds   		int(11) NOT NULL DEFAULT 0 comment '停留应用总时长',
     day_stay_rg_seconds       int(11) NOT NULL DEFAULT 0 comment '停留RG游戏总时长',
     day_stay_ab_seconds       int(11) NOT NULL DEFAULT 0 comment '停留AB游戏总时长',
     day_stay_fruit_seconds    int(11) NOT NULL DEFAULT 0 comment '停留水果机游戏总时长',
     day_stay_fm_seconds       int(11) NOT NULL DEFAULT 0 comment '停留理财总时长',
     day_remark         	    varchar(1000) NOT NULL DEFAULT '' comment  '',
     */

    @JSONField(format = "yyyy-MM-dd")
    private Date pdate;
    private long hours;

    private long userid;
    private String username;
    private long agentid;
    private String agentname;
    private long staffid;
    private String staffname;

    private long onlineDuration;
    private long stayRgDuration;
    private long stayAbDuration;
    private long stayFruitDuration;
    private long stayFmDuration;

    private String remark;

    public static String getColumnPrefix(){
        return "day";
    }

    public Date getPdate() {
        return pdate;
    }

    public void setPdate(Date pdate) {
        this.pdate = pdate;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


    public long getHours() {
        return hours;
    }

    public void setHours(long hours) {
        this.hours = hours;
    }

    public long getOnlineDuration() {
        return onlineDuration;
    }

    public void setOnlineDuration(long onlineDuration) {
        this.onlineDuration = onlineDuration;
    }

    public long getStayRgDuration() {
        return stayRgDuration;
    }

    public void setStayRgDuration(long stayRgDuration) {
        this.stayRgDuration = stayRgDuration;
    }

    public long getStayAbDuration() {
        return stayAbDuration;
    }

    public void setStayAbDuration(long stayAbDuration) {
        this.stayAbDuration = stayAbDuration;
    }

    public long getStayFruitDuration() {
        return stayFruitDuration;
    }

    public void setStayFruitDuration(long stayFruitDuration) {
        this.stayFruitDuration = stayFruitDuration;
    }

    public long getStayFmDuration() {
        return stayFmDuration;
    }

    public void setStayFmDuration(long stayFmDuration) {
        this.stayFmDuration = stayFmDuration;
    }

    public void clear()
    {
        this.pdate = null;
        this.hours = 0;

        this.userid = 0;
        this.username = StringUtils.getEmpty();
        this.agentid = 0;
        this.agentname = StringUtils.getEmpty();
        this.staffid = 0;
        this.staffname = StringUtils.getEmpty();

        this.onlineDuration = 0;
        this.stayRgDuration = 0;
        this.stayAbDuration = 0;
        this.stayFruitDuration = 0;
        this.stayFmDuration = 0;
    }


    public void incre(UserActiveStatsInfo model)
    {
        this.onlineDuration += model.getOnlineDuration();
        this.stayRgDuration += model.getStayRgDuration();
        this.stayAbDuration += model.getStayAbDuration();
        this.stayFruitDuration += model.getStayFruitDuration();
        this.stayFmDuration += model.getStayFmDuration();
    }

}
