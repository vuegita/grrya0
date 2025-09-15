package com.inso.modules.passport.share_holder.model;


import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class ShareHolderInfo {

	/**
	 *   app_id 						int(11) NOT NULL AUTO_INCREMENT,
	 *   app_agentid 				    int(11) NOT NULL comment '商户ID',
	 *   app_agentname                 varchar(50) NOT NULL comment '商户用户名',
	 *   app_access_key				varchar(255) NOT NULL,
	 *   app_access_secret	  		    varchar(255) NOT NULL,
	 *   app_approve_notify_url	    varchar(200) DEFAULT '' comment '回调地址',
	 *   app_status    	  		    varchar(255) NOT NULL,
	 *   app_createtime  				datetime DEFAULT NULL ,
	 */
	private long id;
	private long userid;
	private String username;

	private String lv1RwStatus;
	private String lv2RwStatus;


	private String systemStatus;

	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date createtime;

	public static String getColumnPrefix(){
		return "holder";
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public String getLv1RwStatus() {
		return lv1RwStatus;
	}

	public void setLv1RwStatus(String lv1RwStatus) {
		this.lv1RwStatus = lv1RwStatus;
	}

	public String getLv2RwStatus() {
		return lv2RwStatus;
	}

	public void setLv2RwStatus(String lv2RwStatus) {
		this.lv2RwStatus = lv2RwStatus;
	}

	public String getSystemStatus() {
		return systemStatus;
	}

	public void setSystemStatus(String systemStatus) {
		this.systemStatus = systemStatus;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}



}
