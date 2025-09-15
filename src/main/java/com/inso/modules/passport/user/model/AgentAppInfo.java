package com.inso.modules.passport.user.model;


import com.alibaba.fastjson.annotation.JSONField;
import com.inso.framework.google.GoogleUtil;
import com.inso.framework.utils.MD5;

import java.util.Date;

public class AgentAppInfo {

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
	private long agentid;
	private String agentname;
	private String accessKey;
	private String accessSecret;


	private String approveNotifyUrl;

	private String status;

	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date createtime;

	public static String getColumnPrefix(){
		return "app";
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

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getAccessSecret() {
		return accessSecret;
	}

	public void setAccessSecret(String accessSecret) {
		this.accessSecret = accessSecret;
	}

	public String getApproveNotifyUrl() {
		return approveNotifyUrl;
	}

	public void setApproveNotifyUrl(String approveNotifyUrl) {
		this.approveNotifyUrl = approveNotifyUrl;
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


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
