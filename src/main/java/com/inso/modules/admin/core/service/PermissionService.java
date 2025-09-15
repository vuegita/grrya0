package com.inso.modules.admin.core.service;

import java.util.List;

import com.inso.modules.admin.core.model.Permission;

public interface PermissionService {
	
	public void addPermission(String key, String name);
	public List<Permission> queryAll();
	public List<Permission> queryAllByRoleid(long roleid);
	

//	
	public void bindRoleAndPermission(long roleid, String permissionKey);
//	public void bindRoleAndPermission(long roleid, List<Long> permissionidList);
	public void unbindRoleAndPermission(long roleid, String permissionKey);

}
