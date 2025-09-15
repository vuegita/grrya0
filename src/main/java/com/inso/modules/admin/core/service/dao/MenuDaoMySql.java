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
import com.inso.modules.admin.core.model.Menu;

@Service
public class MenuDaoMySql extends DaoSupport implements MenuDao{
	
	/*
	  `menu_id` int(11) NOT NULL AUTO_INCREMENT,
	  `menu_key` varchar(50) NOT NULL COMMENT '菜单节点key',
	  `menu_pkey` varchar(50) NOT NULL COMMENT '菜单父节点',
	  `menu_name` varchar(50) NOT NULL DEFAULT '' COMMENT '菜单名称',
	  `menu_icon` varchar(50) DEFAULT '' COMMENT '菜单图标class名',
	  `menu_level` int(2) DEFAULT '0' COMMENT '菜单层级',
	  `menu_addr` varchar(255) DEFAULT '' COMMENT '菜单URL（用于页面跳转）',
	  `menu_sort` int(11) DEFAULT NULL COMMENT '菜单排序',
	  `menu_enable_show` tinyint(1) DEFAULT 1 COMMENT '是否禁用 0:禁用 1:启用',
	  `menu_enable_safe` tinyint(1) DEFAULT 1 COMMENT '是否禁用 0:禁用 1:启用',
	  `menu_createtime` datetime DEFAULT NULL COMMENT '创建时间',*/
	
	/**
	 * 
	 * @param key  菜单节点key
	 * @param pkey 菜单父节点
	 * @param name 菜单名称
	 * @param icon 菜单图标class名
	 * @param level 菜单层级
	 * @param link 菜单link（用于页面跳转）
	 * @param sort 菜单排序
	 * @param enableShow  是否显示
	 * @param enableSafe    是否安全验证
	 */
	public void addMenu(String key, String pkey, String name, String permissionkey, String icon, int level, String link, int sort, boolean enableShow, boolean enableSafe)
	{
		
		Date date = new Date();
		
		LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
		
		keyvalue.put("menu_key", key);
		if(!StringUtils.isEmpty(pkey)) keyvalue.put("menu_pkey", pkey);
		keyvalue.put("menu_name", name);
		if(!StringUtils.isEmpty(icon)) keyvalue.put("menu_icon", icon);
		keyvalue.put("menu_level", level);
		if(!StringUtils.isEmpty(link)) keyvalue.put("menu_link", link);
		if(!StringUtils.isEmpty(permissionkey)) keyvalue.put("menu_permission_key", permissionkey);
		keyvalue.put("menu_sort", sort);
		keyvalue.put("menu_enable_show", enableShow);
		keyvalue.put("menu_enable_safe", enableSafe);
		keyvalue.put("menu_createtime", date);
		
		persistent("inso_admin_menu", keyvalue);
		
	}
	
	public List<Menu> queryAll()
	{
		String sql = "select * from inso_admin_menu where menu_enable_show = 1";
		return mSlaveJdbcService.queryForList(sql, Menu.class);
	}
	
//	public List<Menu> queryAllByAccount(String account)
//	{
//		String sql = "select A.* from inso_admin_menu as A " + 
//				"left join inso_admin_permission as B on B.permission_menuid = A.menu_id " + 
//				"inner join inso_admin_role as C on C.role_id = B.permission_roleid " + 
//				"inner join inso_admin as D on D.admin_roleid = C.role_id " + 
//				"where D.admin_account = ? and A.menu_enable_show = 1";
//		return mWriterJdbcService.queryForList(sql, Menu.class, account);
//	}
	
	public List<Menu> queryAllByRoleid(long roleid)
	{
		String sql = "select A.* from inso_admin_menu as A " + 
				"left join inso_admin_permission as B on B.permission_menuid = A.menu_id " + 
				"inner join inso_admin_role as C on C.role_id = B.permission_roleid " + 
				"where C.role_id = ? and A.menu_enable_show = 1";
		return mSlaveJdbcService.queryForList(sql, Menu.class, roleid);
	}
	
	public void updateName(String key, String name)
	{
		String sql = "update inso_admin_menu set menu_name = ? where menu_key = ?";
		mWriterJdbcService.executeUpdate(sql, name, key);
	}
	
	public void updateSort(String key, int sort)
	{
		String sql = "update inso_admin_menu set menu_sort = ? where menu_key = ?";
		mWriterJdbcService.executeUpdate(sql, sort, key);
	}
	
	public void updateSafe(String key, boolean safe)
	{
		String sql = "update inso_admin_menu set menu_enable_safe = ? where menu_key = ?";
		mWriterJdbcService.executeUpdate(sql, safe, key);
	}
	
	public void updatePKey(String key, String pkey)
	{
		String sql = "update inso_admin_menu set menu_pkey = ? where menu_key = ?";
		mWriterJdbcService.executeUpdate(sql, pkey, key);
	}

	public RowPager<Menu> queryScrollPage(PageVo pageVo)
	{
		List<Object> values = Lists.newArrayList();
		StringBuilder whereSQLBuffer = new StringBuilder(" where 1 = 1");
		String whereSQL = whereSQLBuffer.toString();
		String countsql = "select count(1) from inso_admin_menu " + whereSQL;
		long total = mSlaveJdbcService.count(countsql, values.toArray());

		StringBuilder select = new StringBuilder("select * from inso_admin_menu ");
		select.append(whereSQL);
		select.append(" order by menu_createtime desc ");
		select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
		List<Menu> list = mSlaveJdbcService.queryForList(select.toString(), Menu.class, values.toArray());
		RowPager<Menu> rowPage = new RowPager<>(total, list);
		return rowPage;
	}


	public Menu findByKey(String key)
	{
		String sql = "select * from inso_admin_menu where menu_key = ?";
		return mSlaveJdbcService.queryForObject(sql, Menu.class, key);
	}

	public List<Menu> queryAllRootNodeList()
	{
		String sql = "select * from inso_admin_menu where menu_level = 0";
		return mSlaveJdbcService.queryForList(sql, Menu.class);
	}

}
