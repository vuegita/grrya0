package com.inso.modules.passport.domain.model;


import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class AgentDomainInfo {

	/**
	 domain_id               int(11) NOT NULL AUTO_INCREMENT ,
	 domain_url              varchar(255) NOT NULL comment '域名,如 https://www.baidu.com',
	 domain_agentid          int(11) NOT NULL,
	 domain_agentname        varchar(255) NOT NULL ,
	 domain_stafffid         varchar(255) NOT NULL ,
	 domain_staffname        varchar(255) NOT NULL ,
	 domain_status           varchar(20) NOT NULL,
	 domain_createtime       datetime DEFAULT NULL ,
	 */
	private long id;
	private long agentid;
	private String agentname;
	private long staffid;
	private String staffname;
	private String url;
	private String status;
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date createtime;

	public static String getColumnPrefix(){
		return "domain";
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
