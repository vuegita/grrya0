package com.inso.modules.admin.core.model;

import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

public class Role{
	

	/*** 超级管理员角色 ***/
	public static final String DEFAULT_SUPER_ADMIN_ROLE_NAME =  "super_admin";
	
	private long id;
	private String name;
	private String remark;
	private boolean enable;
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date createtime;
	
	public static String getColumnPrefix(){
        return "role";
    }
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	
}
