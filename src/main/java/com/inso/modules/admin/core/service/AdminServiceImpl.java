package com.inso.modules.admin.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.MD5;
import com.inso.framework.utils.RowPager;
import com.inso.modules.admin.core.cache.AdminCacheKeyUtils;
import com.inso.modules.admin.core.model.Admin;
import com.inso.modules.admin.core.model.AdminSecret;
import com.inso.modules.admin.core.service.dao.AdminDao;

@Service
public class AdminServiceImpl implements AdminService{

	@Autowired
	private AdminDao mAdminDao;

	@Override
	public void addAdmin(String account, String password, String mobile, String email, long roleid, String remark) {
		String salt = MD5.encode(account + password + System.currentTimeMillis());
		String encryPwd = AdminSecret.encryPassword(account, password, salt);
		mAdminDao.addAdmin(account, encryPwd, salt, mobile, email, roleid, remark);
	}

	@Override
	public boolean existAccount(String account) {
		Admin admin = findAdminInfoByID(false, account);
		return admin != null;
	}
	
	@Override
	public Admin findAdminInfoByID(boolean purge, String account) {
		String cachekey = AdminCacheKeyUtils.createAdminInfo(account);
		Admin admin = CacheManager.getInstance().getObject(cachekey, Admin.class);
		if(purge || admin == null)
		{
			admin = mAdminDao.findAdminInfoByID(account);
			if(admin != null)
			{
				CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(admin));
			}
		}
		return admin;
	}

	@Override
	public AdminSecret findAdminSecretByID(String account) {
		String cachekey = AdminCacheKeyUtils.createAdminSecret(account);
		AdminSecret adminSecret = CacheManager.getInstance().getObject(cachekey, AdminSecret.class);
		if(adminSecret == null)
		{
			adminSecret = mAdminDao.findAdminSecretByID(account);
			if(adminSecret != null)
			{
				CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(adminSecret));
			}
		}
		return adminSecret;
	}
	
	public void updateRole(String account, long roleid)
	{
		mAdminDao.updateRole(account, roleid);
		clearCache(account);
	}
	
	@Transactional
	public void updateStatus(String account, boolean enable)
	{
		mAdminDao.updateStatus(account, enable);
		clearCache(account);
	}
	
	@Transactional
	public void updatePassword(String account, String password)
	{
		String salt = MD5.encode(account + password + System.currentTimeMillis());
		String encryPwd = AdminSecret.encryPassword(account, password, salt);
		mAdminDao.updatePassword(account, encryPwd, salt);

		clearCache(account);
	}
	
	@Transactional
	public void updateGoogleKey(String account, String googleKey)
	{
		mAdminDao.updateGoogleKey(account, googleKey);

		clearCache(account);
	}
	
	@Transactional
	public void updateMobile(String account, String mobile)
	{
		mAdminDao.updateMobile(account, mobile);
		clearCache(account);
	}
	
	@Transactional
	public void updateEmail(String account, String email)
	{
		mAdminDao.updateEmail(account, email);
		clearCache(account);
	}
	
	@Transactional
	public void updateRemark(String account, String remark) {
		mAdminDao.updateRemark(account, remark);
		clearCache(account);
	}

	@Transactional
	public void updateLastLoginIP(String account, String ip)
	{
		mAdminDao.updateLastLoginIP(account, ip);
	}

	public List<Admin> queryAllByRoleid(long roleid)
	{
		return mAdminDao.queryAllByRoleid(roleid);
	}


	public RowPager<Admin> queryScrollPage(PageVo pageVo, String username, String ignoreAdmin)
	{
		return mAdminDao.queryScrollPage(pageVo, username, ignoreAdmin);
	}

	@Transactional
	public void deleteAdmin(String account)
	{
		mAdminDao.deleteAdmin(account);

		clearCache(account);
	}

	private void clearCache(String account)
	{
		String cachekey = AdminCacheKeyUtils.createAdminInfo(account);
		CacheManager.getInstance().delete(cachekey);

		String secretCacheKey = AdminCacheKeyUtils.createAdminSecret(account);
		CacheManager.getInstance().delete(secretCacheKey);
	}
}
