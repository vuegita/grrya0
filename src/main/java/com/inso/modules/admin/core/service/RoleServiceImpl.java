package com.inso.modules.admin.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.admin.core.model.Role;
import com.inso.modules.admin.core.service.dao.RoleDao;

@Service
public class RoleServiceImpl implements RoleService{
	
	@Autowired
	private RoleDao mRoleDao;
	
	public List<Role> queryAll()
	{
		return mRoleDao.queryAll();
	}
	
	@Transactional
	public void addRole(String name, String remark)
	{
		mRoleDao.addRole(name.trim(), remark);
	}

	@Override
	public Role findByName(String name) {
		return  mRoleDao.findByName(name);
	}

	public Role findByID(long id)
	{
		return mRoleDao.findByID(id);
	}

	@Override
	@Transactional
	public void deleteRoleAndPermission(Role role) {
		mRoleDao.deleteRoleAndPermission(role.getId());
	}
	
	@Transactional
	public void updateRemark(String rolename, String remark)
	{
		mRoleDao.updateRemark(rolename, remark);
	}

	public RowPager<Role> queryScrollPage(PageVo pageVo)
	{
		return mRoleDao.queryScrollPage(pageVo);
	}

}
