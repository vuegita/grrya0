package com.inso.modules.admin.core.service.dao;

import java.util.List;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.admin.core.model.Role;

public interface RoleDao {
	
	public void addRole(String name, String remark);
	public List<Role> queryAll();
	public void updateStatus(long roleid, boolean enable);
	public Role findByName(String name);
	public Role findByID(long id);
	
	public void deleteRoleAndPermission(long roleid);
	
	public void updateRemark(String rolename, String remark);

	public RowPager<Role> queryScrollPage(PageVo pageVo);
}
