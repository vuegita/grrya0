package com.inso.modules.admin.core.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inso.framework.utils.CollectionUtils;
import com.inso.modules.admin.core.model.Permission;
import com.inso.modules.admin.core.service.dao.PermissionDao;

@Service
public class PermissionServiceImpl implements PermissionService{
	
	@Autowired
	private PermissionDao mPermissionDao;

	@Override
	public List<Permission> queryAll() {
		return mPermissionDao.queryAll();
	}

	@Transactional
	public void addPermission(String key, String name) {
		mPermissionDao.addPermission(key, name);
	}

	@Override
	public List<Permission> queryAllByRoleid(long roleid) {
		List<Permission> list = mPermissionDao.queryAllByRoleid(roleid);
		if(CollectionUtils.isEmpty(list))
		{
			return Collections.emptyList();
		}
		return list;
	}

	@Override
	@Transactional
	public void bindRoleAndPermission(long roleid, String permissionid) {
		mPermissionDao.bindRoleAndPermission(roleid, permissionid);
	}

	@Transactional
	public void unbindRoleAndPermission(long roleid, String permissionid)
	{
		mPermissionDao.unbindRoleAndPermission(roleid, permissionid);
	}
	
	
	

}
