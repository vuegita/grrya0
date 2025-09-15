package com.inso.modules.admin.core.service.dao;

import java.util.List;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.admin.core.model.Menu;

public interface MenuDao {

	
	public void addMenu(String key, String pkey, String name, String permissionkey, String icon, int level, String link, int sort, boolean enableShow, boolean enableSafe);
	
	public List<Menu> queryAll();
	
	public List<Menu> queryAllByRoleid(long roleid);
	
	public void updateName(String key, String name);
	public void updateSort(String key, int sort);
	public void updateSafe(String key, boolean safe);
	public void updatePKey(String key, String pkey);

	public List<Menu> queryAllRootNodeList();
	public RowPager<Menu> queryScrollPage(PageVo pageVo);
	public Menu findByKey(String key);
}
