package com.inso.modules.admin.core.service.dao;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.core.model.Admin;
import com.inso.modules.admin.core.model.AdminSecret;

@Service
public class AdminDaoMySql extends DaoSupport implements AdminDao {
	
	
	/*
	  `admin_id` int(11) NOT NULL AUTO_INCREMENT,
	  `admin_account` varchar(20) NOT NULL  COMMENT '账号',
	  `admin_password` varchar(32) NOT NULL COMMENT '密码',
	  `admin_remark` varchar(255) DEFAULT '' COMMENT '备注',
	  `admin_lastlogintime` datetime DEFAULT NULL COMMENT '最后登录时间',
	  `admin_lastloginip` char(15)  COMMENT '最后登录ip',
	  `admin_enable` tinyint(1) DEFAULT 1 COMMENT '是否禁用 0:禁用 1:启用',
	  `admin_roleid` int(11) DEFAULT NULL COMMENT '角色外键',
	  `admin_lastloginarea` varchar(50) DEFAULT '' COMMENT '登录地区',
	  `admin_googlekey` varchar(50) DEFAULT '' COMMENT '谷歌key',
	  `admin_createtime` datetime DEFAULT NULL COMMENT '创建时间',*/
	
	public void addAdmin(String account, String pwd, String salt, String mobile, String email, long roleid, String remark)
	{
		Date date = new Date();
		
		LinkedHashMap<String, Object> keyValues = Maps.newLinkedHashMap();
		keyValues.put("admin_account", account);
		keyValues.put("admin_password", pwd);
		keyValues.put("admin_salt", salt);
		keyValues.put("admin_roleid", roleid);
		if(!StringUtils.isEmpty(mobile)) keyValues.put("admin_mobile", mobile);
		if(!StringUtils.isEmpty(email)) keyValues.put("admin_email", email);
		keyValues.put("admin_createtime", date);
		if(!StringUtils.isEmpty(remark)) keyValues.put("admin_remark", remark);
		
		persistent("inso_admin", keyValues);
	}
	
	
	public AdminSecret findAdminSecretByID(String account)
	{
		String sql = "select admin_account, admin_password, admin_salt, admin_googlekey from inso_admin where admin_account = ?";
		AdminSecret model = mSlaveJdbcService.queryForObject(sql, AdminSecret.class, account);
		return model;
	}
	
	public Admin findAdminInfoByID(String account)
	{
		String sql = "select * from inso_admin where admin_account = ?"; 
		Admin model = mSlaveJdbcService.queryForObject(sql, Admin.class, account);
		return model;
	}
	
	public void updateRole(String account, long roleid)
	{
		String sql = "update inso_admin set admin_roleid = ? where admin_account = ?";
		mWriterJdbcService.executeUpdate(sql, roleid, account);
	}
	
	public boolean existByAccount(String account)
	{
		String sql = "select count(1) from inso_admin where admin_account = ?";
		return mSlaveJdbcService.count(sql, account) > 0;
	}
	
	public List<Admin> queryAll()
	{
		String sql = "select A.*, B.role_name as admin_rolename from inso_admin as A " + 
							"left join inso_admin_role as B on B.role_id=A.admin_roleid";
		return mSlaveJdbcService.queryForList(sql, Admin.class) ;
	}

	public List<Admin> queryAllByRoleid(long roleid)
	{
		String sql = "select * from inso_admin where admin_roleid = ? ";
		return mSlaveJdbcService.queryForList(sql, Admin.class, roleid) ;
	}
	
	public RowPager<Admin> queryScrollPage(PageVo pageVo, String username, String ignoreAdmin)
	{
		List<Object> values = Lists.newArrayList();
		StringBuilder whereSQLBuffer = new StringBuilder(" where 1 = 1");
		if (!StringUtils.isEmpty(username)) {
			values.add(username);
			whereSQLBuffer.append(" and admin_account = ? ");
		}

		if (!StringUtils.isEmpty(ignoreAdmin)) {
			values.add(ignoreAdmin);
			whereSQLBuffer.append(" and admin_account != ? ");
		}

		String whereSQL = whereSQLBuffer.toString();
		String countsql = "select count(1) from inso_admin " + whereSQL;
		long total = mSlaveJdbcService.count(countsql, values.toArray());

		StringBuilder select = new StringBuilder("select A.*, B.role_name as admin_rolename from inso_admin as A ");
		select.append("left join inso_admin_role as B on B.role_id=A.admin_roleid ");
		select.append(whereSQL);
		select.append(" order by admin_createtime desc ");
		select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
		List<Admin> list = mSlaveJdbcService.queryForList(select.toString(), Admin.class, values.toArray());
		RowPager<Admin> rowPage = new RowPager<>(total, list);
		return rowPage;
	}

	public void updateStatus(String account, boolean enable) {
		String sql = "update inso_admin set admin_enable = ? where admin_account = ?";
		mWriterJdbcService.executeUpdate(sql, enable, account);
		
	}

	public void updatePassword(String account, String password, String salt) {
		String sql = "update inso_admin set admin_password = ?, admin_salt = ? where admin_account = ?";
		mWriterJdbcService.executeUpdate(sql, password, salt, account);
	}
	
	public void updateGoogleKey(String account, String googleKey)
	{
		// admin_googlekey
		String sql = "update inso_admin set admin_googlekey = ? where admin_account = ?";
		mWriterJdbcService.executeUpdate(sql, googleKey, account);
	}

	public void updateMobile(String account, String mobile) {
		String sql = "update inso_admin set admin_mobile = ? where admin_account = ?";
		mWriterJdbcService.executeUpdate(sql, mobile, account);
	}
	
	public void updateEmail(String account, String email) {
		String sql = "update inso_admin set admin_email = ? where admin_account = ?";
		mWriterJdbcService.executeUpdate(sql, email, account);
	}
	
	public void updateRemark(String account, String remark) {
		String sql = "update inso_admin set admin_remark = ? where admin_account = ?";
		mWriterJdbcService.executeUpdate(sql, remark, account);
	}

	public void updateLastLoginIP(String account, String ip) {
		Date lastLoginTime = new Date();
		String sql = "update inso_admin set admin_lastloginip = ?, admin_lastlogintime = ? where admin_account = ?";
		mWriterJdbcService.executeUpdate(sql, ip, lastLoginTime, account);
	}

	public void deleteAdmin(String account)
	{
		String sql = "delete from inso_admin where admin_account = ?";
		mWriterJdbcService.executeUpdate(sql, account);
	}

	
//	public boolean checkPassword(String account, )
	
	
	


}
