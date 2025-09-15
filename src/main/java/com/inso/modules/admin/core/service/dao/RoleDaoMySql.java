package com.inso.modules.admin.core.service.dao;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.modules.admin.core.model.Role;

@Service
public class RoleDaoMySql extends DaoSupport implements RoleDao {

	@Transactional
	public void addRole(String name, String remark)
	{
		Date date = new Date();
		
		LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
		
		keyvalue.put("role_name", name);
		keyvalue.put("role_remark", remark);
		keyvalue.put("role_enable", true);
		keyvalue.put("role_createtime", date);
		
		persistent("inso_admin_role", keyvalue);
	}
	
	public List<Role> queryAll()
	{
		String sql = "select * from inso_admin_role";
		return mSlaveJdbcService.queryForList(sql, Role.class);
	}
	
	@Transactional
	public void updateStatus(long roleid, boolean enable)
	{
		String sql = "update inso_admin_role set role_enable = ? where role_id = ?";
		mWriterJdbcService.executeUpdate(sql, enable, roleid);
	}
	
	public Role findByName(String name)
	{
		String sql = "select * from inso_admin_role where role_name = ?";
		return mSlaveJdbcService.queryForObject(sql, Role.class, name); 
	}
	
	public Role findByID(long id)
	{
		String sql = "select * from inso_admin_role where role_id = ?";
		return mSlaveJdbcService.queryForObject(sql, Role.class, id); 
	}

	@Override
	public void deleteRoleAndPermission(long roleid) {
		// delete role
		String deleteRoleSql = "delete from inso_admin_role where role_id = ?";
		mWriterJdbcService.executeUpdate(deleteRoleSql, roleid);
		
		// delete role_permission
		String deleteRoleAndPermissionSql = "delete from inso_admin_role_permission where role_permission_roleid = ?";
		mWriterJdbcService.executeUpdate(deleteRoleAndPermissionSql, roleid);
	}
	
	@Transactional
	public void updateRemark(String rolename, String remark)
	{
		String sql = "update inso_admin_role set role_remark = ? where role_name = ?";
		mWriterJdbcService.executeUpdate(sql, remark, rolename);
	}

	public RowPager<Role> queryScrollPage(PageVo pageVo)
	{
		List<Object> values = Lists.newArrayList();
		StringBuilder whereSQLBuffer = new StringBuilder(" where 1 = 1");
		String whereSQL = whereSQLBuffer.toString();
		String countsql = "select count(1) from inso_admin_role " + whereSQL;
		long total = mSlaveJdbcService.count(countsql, values.toArray());

		StringBuilder select = new StringBuilder("select * from inso_admin_role ");
		select.append(whereSQL);
		select.append(" order by role_createtime desc ");
		select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
		List<Role> list = mSlaveJdbcService.queryForList(select.toString(), Role.class, values.toArray());
		RowPager<Role> rowPage = new RowPager<>(total, list);
		return rowPage;
	}
	
	
	
}
