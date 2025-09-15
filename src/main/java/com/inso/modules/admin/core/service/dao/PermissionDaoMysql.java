package com.inso.modules.admin.core.service.dao;

import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;
import com.inso.framework.spring.DaoSupport;
import com.inso.modules.admin.core.model.Permission;

@Repository
public class PermissionDaoMysql extends DaoSupport implements PermissionDao{
	
	private static final String TABLE = "inso_admin_permission";
	
	public void addPermission(String key, String name)
	{
		LinkedHashMap<String, Object> keyValues = Maps.newLinkedHashMap();
		keyValues.put("permission_key", key);
		keyValues.put("permission_name", name);
		
		persistent(TABLE, keyValues);
	}
	
	
	public List<Permission> queryAll()
	{
		String sql = "select * from " + TABLE;
		Object[] values = null;
		return mSlaveJdbcService.queryForList(sql, Permission.class, values);
	}
	
	public List<Permission> queryAllByRoleid(long roleid) {
		// 
		String sql = "select B.* from inso_admin_role_permission as A " + 
				"left join inso_admin_permission as B on A.role_permission_permission_key = B.permission_key " + 
				"where A.role_permission_roleid = ?";
		return mSlaveJdbcService.queryForList(sql, Permission.class, roleid);
	}
	
	public void bindRoleAndPermission(long roleid, String permissionKey) {
		LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
		keyvalue.put("role_permission_roleid", roleid);
		keyvalue.put("role_permission_permission_key", permissionKey);
		persistent("inso_admin_role_permission", keyvalue);
	}

	public void unbindRoleAndPermission(long roleid, String permissionKey) {
		String sql = "delete from inso_admin_role_permission where role_permission_roleid = ? and role_permission_permission_key = ?";
		mWriterJdbcService.executeUpdate(sql, roleid, permissionKey);
	}

}
