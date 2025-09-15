package com.inso.modules.admin.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.admin.core.model.Menu;
import com.inso.modules.admin.core.service.dao.MenuDao;

@Service
public class MenuServiceImpl implements MenuService{

	@Autowired
	private MenuDao menuDao;
	
	
	public void addRootMenu(String key, String name, int sort)
	{
		menuDao.addMenu(key, null, name, null, null, 0, null, sort, true, true);
	}
	
	public void addChildMenu(String key, String pkey, String name, String permissionkey, String link, int sort)
	{
		menuDao.addMenu(key, pkey, name, permissionkey, null, 1, link, sort, true, true);
	}
	
	public List<Menu> queryAll()
	{
		return menuDao.queryAll();
	}
	
	@Override
	public List<Menu> queryAllByRoleid(long roleid){
		List<Menu> menus = queryAll();
		List<Menu> lists = Lists.newArrayList();
		for(Menu menu : menus)
		{
			lists.add(menu);
		}
		return menus;
	}
	
	public List<Menu> queryRoot() {
		List<Menu> menus = queryAll();
		List<Menu> rootList = Lists.newArrayList();
		for(Menu menu : menus)
		{
			if(menu.isRootNode())
			{
				rootList.add(menu);
			}
		}
		return rootList;
	}
	
	@Transactional
	public void updateName(String key, String name)
	{
		menuDao.updateName(key, name);
	}
	@Transactional
	public void updateSort(String key, int sort)
	{
		menuDao.updateSort(key, sort);
	}
	
	@Transactional
	public void updateSafe(String key, boolean safe)
	{
		menuDao.updateSafe(key, safe);
	}
	
	@Transactional
	public void updatePKey(String key, String pkey)
	{
		menuDao.updatePKey(key, pkey);
	}

	public List<Menu> queryAllRootNodeList()
	{
		return menuDao.queryAllRootNodeList();
	}


	public RowPager<Menu> queryScrollPage(PageVo pageVo)
	{
		return menuDao.queryScrollPage(pageVo);
	}

	public Menu findByKey(String key)
	{
		return menuDao.findByKey(key);
	}

}
