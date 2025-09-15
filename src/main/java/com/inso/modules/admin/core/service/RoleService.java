package com.inso.modules.admin.core.service;

import java.util.List;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.admin.core.model.Role;

public interface RoleService {
	
	public static String CACHE_KEY_ROLE_ALL = "admin_role_all";
	public static String CACHE_KEY_ROLE_NAME = "admin_role_name";
	
	public List<Role> queryAll();
	
	public void addRole(String name, String remark);

//	public Role findByID(long id);

	/**
	 * 后台初始化权限时调用
	 * @param name
	 * @return
	 */
	public Role findByName(String name);
	
	public void deleteRoleAndPermission(Role role);
	
	public void updateRemark(String rolename, String remark);

	public RowPager<Role> queryScrollPage(PageVo pageVo);
}
