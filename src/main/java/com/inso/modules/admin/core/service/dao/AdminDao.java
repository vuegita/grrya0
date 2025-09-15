package com.inso.modules.admin.core.service.dao;

import java.util.List;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.admin.core.model.Admin;
import com.inso.modules.admin.core.model.AdminSecret;

public interface AdminDao {
	
	public void addAdmin(String account, String pwd, String salt, String mobile, String email, long roleid, String remark);
	
	public void updateRole(String account, long roleid);
	
	public Admin findAdminInfoByID(String account);
	public AdminSecret findAdminSecretByID(String account);
	public boolean existByAccount(String account);

	public List<Admin> queryAllByRoleid(long roleid);
	public List<Admin> queryAll();
	public RowPager<Admin> queryScrollPage(PageVo pageVo, String username, String ignoreAdmin);
	
	public void updateStatus(String account, boolean enable);
	
	public void updateGoogleKey(String account, String googleKey);
	public void updatePassword(String account, String password, String salt);
	public void updateMobile(String account, String mobile);
	public void updateEmail(String account, String email);
	public void updateRemark(String account, String remark);
	public void updateLastLoginIP(String account, String ip);

	public void deleteAdmin(String account);
}
