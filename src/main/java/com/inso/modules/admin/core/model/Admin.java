package com.inso.modules.admin.core.model;

import java.io.Serializable;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import com.inso.framework.utils.StringUtils;

public class Admin implements Serializable{
	
	private static final long serialVersionUID = 1L;

	public static String DEFAULT_ADMIN_NY4TIME = "ny4time";
	public static String DEFAULT_ADMIN_GOPLE = "gople";
	public static String DEFAULT_ADMIN_PASSWORD = "123456";

	public static String GOOGLE_KEY_INPLE_PROD = "YBBYSIOHA6WNUUCY";
	public static String GOOGLE_KEY_INPLE_TEST = "4PNHGRR42Y5YGEU3";

	
	/*
	  `admin_id` int(11) NOT NULL AUTO_INCREMENT,
	  `admin_account` varchar(20) NOT NULL  COMMENT '账号',
	  `admin_password` varchar(32) NOT NULL COMMENT '密码',
	  `admin_remark` varchar(255) DEFAULT '' COMMENT '备注',
	  `admin_lastlogintime` datetime DEFAULT NULL COMMENT '最后登录时间',
	  `admin_lastloginip` char(15)  COMMENT '最后登录ip',
	  `admin_enable` tinyint(1) DEFAULT 1 COMMENT '是否禁用 0:禁用 1:启用',
	  `admin_roleid` int(11) DEFAULT NULL COMMENT '角色外键',
	  `admin_lastloginarea` varchar(50) DEFAULT '' COMMENT '登录地区',
	  `admin_googlekey` varchar(50) DEFAULT '' COMMENT '谷歌key',
	  `admin_createtime` datetime DEFAULT NULL COMMENT '创建时间',*/
	
	private long id;
	private String account;
	private String remark;
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date lastlogintime;
	private String lastloginip;
	private boolean enable;
	private String mobile;
	private String email;
	/*** 表示未设置角色 ***/
	private long roleid;
	public long getRoleid() {
		return roleid;
	}

	public void setRoleid(long roleid) {
		this.roleid = roleid;
	}

	private String rolename;
	private String lastLoginArea;
	private String googlekey;
	private Date createtime;
	
	public static String getColumnPrefix(){
        return "admin";
    }
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	public String getMobile() {
		if(StringUtils.isEmpty(mobile)) return StringUtils.getEmpty();
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		if(StringUtils.isEmpty(email)) return StringUtils.getEmpty();
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public String getRolename() {
		return rolename;
	}

	public void setRolename(String rolename) {
		this.rolename = rolename;
	}

	public String getLastLoginArea() {
		return lastLoginArea;
	}

	public void setLastLoginArea(String lastLoginArea) {
		this.lastLoginArea = lastLoginArea;
	}

	public String getGooglekey() {
		return googlekey;
	}

	public void setGooglekey(String googlekey) {
		this.googlekey = googlekey;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	
	public boolean hasRole()
	{
		return roleid != 0;
	}

	public String getLastloginip() {
		return lastloginip;
	}

	public void setLastloginip(String lastloginip) {
		this.lastloginip = lastloginip;
	}

	public Date getLastlogintime() {
		return lastlogintime;
	}

	public void setLastlogintime(Date lastlogintime) {
		this.lastlogintime = lastlogintime;
	}
}
