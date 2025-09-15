package com.inso.modules.admin.core.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;
import com.inso.framework.utils.CollectionUtils;

public class Menu implements Comparable<Menu>, Serializable{
	
	private static final long serialVersionUID = 1L;
	
//	/*** 系统管理 ***/
//	public static String ROOT_SYS = "root_sys";
//	
//	/*** 客服管理 ***/
//	public static String ROOT_USER = "root_kefu";
//	
//	/*** 系统统计 ***/
//	public static String ROOT_STATS = "root_stats";
//	
//	/*** 会员管理 ***/
//	public static String ROOT_MEMBER = "root_stats";
	
	/*
	  `menu_id` int(11) NOT NULL AUTO_INCREMENT,
	  `menu_key` varchar(50) NOT NULL COMMENT '菜单节点key',
	  `menu_pkey` varchar(50) NOT NULL COMMENT '菜单父节点',
	  `menu_name` varchar(50) NOT NULL DEFAULT '' COMMENT '菜单名称',
	  `menu_icon` varchar(50) DEFAULT '' COMMENT '菜单图标class名',
	  `menu_level` int(2) DEFAULT '0' COMMENT '菜单层级',
	  `menu_addr` varchar(255) DEFAULT '' COMMENT '菜单URL（用于页面跳转）',
	  `menu_sort` int(11) DEFAULT NULL COMMENT '菜单排序',
	  `menu_enable_show` tinyint(1) DEFAULT 1 COMMENT '是否禁用 0:禁用 1:启用',
	  `menu_enable_safe` tinyint(1) DEFAULT 1 COMMENT '是否禁用 0:禁用 1:启用',
	  `menu_createtime` datetime DEFAULT NULL COMMENT '创建时间',*/
	
	private long id;
	private String key;
	private String pkey;
	private String name;
	private String icon;
	private int level;
	private String permissionKey;
	private String link;
	private int sort;
	private boolean enableShow;
	private boolean enableSafe;
	private Date createtime;
	
	private transient List<Menu> childList;
	private transient List<Permission> mChildMenuPermissionList;
	
	public List<Menu> getChildList() {
		if(childList == null)
		{
			this.childList = Lists.newArrayList();
		}
		return childList;
	}

	public void setChildList(List<Menu> list)
	{
		this.childList = list;
	}

	public List<Permission> getChildMenuPermissionList()
	{
		if(mChildMenuPermissionList == null)
		{
			mChildMenuPermissionList = Lists.newArrayList();
		}
		return mChildMenuPermissionList;
	}

	public boolean isCheckedAllPermission()
	{
		if(CollectionUtils.isEmpty(mChildMenuPermissionList))
		{
			return false;
		}

		for (Permission model : mChildMenuPermissionList)
		{
			if(!model.isChecked())
			{
				return false;
			}
		}
		return true;
	}


	public static String getColumnPrefix(){
        return "menu";
    }
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getPkey() {
		return pkey;
	}
	public void setPkey(String pkey) {
		this.pkey = pkey;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
	}
	public boolean isEnableShow() {
		return enableShow;
	}
	public void setEnableShow(boolean enableShow) {
		this.enableShow = enableShow;
	}
	public boolean isEnableSafe() {
		return enableSafe;
	}
	public void setEnableSafe(boolean enableSafe) {
		this.enableSafe = enableSafe;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	
	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	
	public boolean isRootNode()
	{
		return level == 0;
	}
	
	@Override
	public int compareTo(Menu o) {
		if(this.sort > o.getSort()) return 1;
		return -1;
	}


	public String getPermissionKey() {
		return permissionKey;
	}

	public void setPermissionKey(String permissionKey) {
		this.permissionKey = permissionKey;
	}
}
