package com.inso.modules.admin.core.service;

import java.util.List;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.admin.core.model.Admin;
import com.inso.modules.admin.core.model.AdminSecret;

public interface AdminService {
	
	public static String CACHE_KEY_ADMIN_ACCOUNT_INFO = "admin_account_info";
	
	public void addAdmin(String account, String password, String mobile, String email, long roleid, String remark);
	
	public Admin findAdminInfoByID(boolean purge, String account);
	
	public AdminSecret findAdminSecretByID(String account);
	
	public boolean existAccount(String account);
	
	public void updateRole(String account, long roleid);
	public void updateStatus(String account, boolean enable);
	public void updateMobile(String account, String mobile);
	public void updateEmail(String account, String email);
	public void updateRemark(String account, String remark);
	public void updatePassword(String account, String password);
	public void updateGoogleKey(String account, String googleKey);
	public void updateLastLoginIP(String account, String ip);

	public List<Admin> queryAllByRoleid(long roleid);
	public RowPager<Admin> queryScrollPage(PageVo pageVo, String username, String ignoreAdmin);

	public void deleteAdmin(String account);
	
}
