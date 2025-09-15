package com.inso.modules.admin.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.inso.modules.common.model.Status;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.MD5;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.core.model.Admin;
import com.inso.modules.admin.core.model.Menu;
import com.inso.modules.admin.core.model.Permission;
import com.inso.modules.admin.core.model.Role;
import com.inso.modules.admin.core.service.AdminService;
import com.inso.modules.admin.core.service.MenuService;
import com.inso.modules.admin.core.service.PermissionService;
import com.inso.modules.admin.core.service.RoleService;

@Component
public class AdminDBUpdate {
	
	private static Log LOG = LogFactory.getLog(AdminDBUpdate.class);
	
	private static final String BASE_ADMIN_ADDR = "/alibaba888/Liv2sky3soLa93vEr62/";
	
	@Autowired
	private MenuService menuService;
	
	@Autowired
	private PermissionService mPermissionService;
	
	@Autowired
	private RoleService mRoleService;
	
	@Autowired
	private AdminService mAdminService;
	
	
	public void update()
	{
		try {
			// 所有菜单
			List<Menu> menuList = menuService.queryAll();
			Map<String, String> menuMaps = Maps.newHashMap();
			if(!CollectionUtils.isEmpty(menuList))
			{
				for(Menu menu : menuList)
				{
					menuMaps.put(menu.getKey(), StringUtils.getEmpty());					
				}
			}
			
			// 所有权限
			List<Permission> allPermissionList = mPermissionService.queryAll();
			Map<String, String> allPermissionMaps = Maps.newHashMap();
			if(!CollectionUtils.isEmpty(allPermissionList))
			{
				for(Permission model : allPermissionList)
				{
					allPermissionMaps.put(model.getKey(), StringUtils.getEmpty());					
				}
			}
			
			// 更新所有菜单和权限
			updateMenuAndPermission(menuMaps, allPermissionMaps);
			
			// update admin  role
			if(allPermissionList == null || allPermissionList.isEmpty())
			{
				allPermissionList = mPermissionService.queryAll();
			}

			//addAdminRole(Admin.DEFAULT_ADMIN_GOPLE, allPermissionList, false);

			// 内部超级管理员, 只有限定ip才能登陆
			addAdminRole(Admin.DEFAULT_ADMIN_NY4TIME, allPermissionList, true);


			
		} catch (Exception e) {
			LOG.error("update error:", e);
		}
		
	}
	
	
	
	/**
	 * 添加默认admin角色
	 */
	public void addAdminRole(String adminName, List<Permission> allPermissionList, boolean isInitSuperPermission)
	{
		Role adminRole = mRoleService.findByName(Role.DEFAULT_SUPER_ADMIN_ROLE_NAME);
		if(adminRole == null)
		{
			mRoleService.addRole(Role.DEFAULT_SUPER_ADMIN_ROLE_NAME, "至高无上的角色!");	
			adminRole = mRoleService.findByName(Role.DEFAULT_SUPER_ADMIN_ROLE_NAME);
		}
		
		Admin admin = mAdminService.findAdminInfoByID(true, adminName);
		if(admin == null)
		{
			String password = MD5.encode(Admin.DEFAULT_ADMIN_PASSWORD);
			String mobile = StringUtils.getEmpty();
			String email = StringUtils.getEmpty();
			mAdminService.addAdmin(adminName, password, mobile, email, adminRole.getId(), "至高无上的权利!");

			admin = mAdminService.findAdminInfoByID(true, adminName);
		}

		if(!isInitSuperPermission)
		{
			return;
		}

		// 角色所有权限
		List<Permission> adminPermissionList = mPermissionService.queryAllByRoleid(adminRole.getId());
		Map<String, String> adminPermissionMaps = Maps.newHashMap();
		if(!CollectionUtils.isEmpty(adminPermissionList))
		{
			for(Permission model : adminPermissionList)
			{
				adminPermissionMaps.put(model.getKey(), StringUtils.getEmpty());					
			}
		}
		
		// 更新角色权限
		for(Permission permission : allPermissionList)
		{
			if(!adminPermissionMaps.containsKey(permission.getKey()))
			{
				mPermissionService.bindRoleAndPermission(adminRole.getId(), permission.getKey());
			}
		}
		
		
	}
	
	private void updateMenuAndPermission(Map<String, String> menuMaps, Map<String, String> permissionMaps)
	{
//		SystemRunningMode runningMode = SystemRunningMode.getSystemConfig();
		try {
			
			String note = "#";
			String path = "config/admin-menu-permissions.json";
			InputStream is = AdminDBUpdate.class.getClassLoader().getResourceAsStream(path);
			List<String> lines = IOUtils.readLines(is, StringUtils.UTF8);
			StringBuilder buffer = new StringBuilder();
			for(String line : lines)
			{
				if(!line.startsWith(note))
				{
					buffer.append(line);
				}
			}
			
			JSONArray jsonArray = FastJsonHelper.parseArray(buffer.toString());
			int rootMenuLen = jsonArray.size();
			for(int i = 0; i < rootMenuLen; i ++)
			{
				JSONObject parentNode = jsonArray.getJSONObject(i);
				
				String rootKey = parentNode.getString("root_key");
				String rootName = parentNode.getString("root_name");
				JSONArray childMenuList = parentNode.getJSONArray("childMenuList");
				boolean showInProd = parentNode.getBooleanValue("showInProd");
				if(!showInProd && MyEnvironment.isProd())
				{
					// 生产环境、如果为false则不显示
					continue;
				}

				String hiddenNode = parentNode.getString("hiddenNode");
				if(StringUtils.asBoolean(hiddenNode) && !MyEnvironment.isDev())
				{
					// 隐藏节点
					continue;
				}

				Status status = Status.getType(parentNode.getString("status"));
				if(status == Status.DISABLE)
				{
					continue;
				}

//				String showInMode = parentNode.getString("showInMode");
//				if(!("all".equalsIgnoreCase(showInMode) || runningMode.getKey().equalsIgnoreCase(showInMode) || MyEnvironment.isDev()))
//				{
//					continue;
//				}

				// handle root root
				if(!menuMaps.containsKey(rootKey))
				{
					menuService.addRootMenu(rootKey, rootName, i);
				}
				
				// handle child menu
				int childMenuLen = childMenuList.size();
				for(int k = 0; k < childMenuLen; k ++)
				{
					/*
					 * "key": "root_basic_platform",
                "name": "平台设置",
                "permissions": "edit,list"
					 */
					JSONObject childMenuNode = childMenuList.getJSONObject(k);
					String hiddenChildNodeValue = childMenuNode.getString("hiddenNode");
					if(!StringUtils.isEmpty(hiddenChildNodeValue) && StringUtils.asBoolean(hiddenChildNodeValue))
					{
						continue;
					}
					String childMenuKey = childMenuNode.getString("key");
					
					// 子菜单
					if(!menuMaps.containsKey(childMenuKey))
					{
						String childMenuName = childMenuNode.getString("name");
						String permissionkey = childMenuKey + "_list";
						String addr = BASE_ADMIN_ADDR + childMenuKey;
						menuService.addChildMenu(childMenuKey, rootKey, childMenuName, permissionkey, addr, k);
					}
					
					// 子菜单权限
					String[] permissions = StringUtils.split(childMenuNode.getString("permissions"), ',');
					for(String permission : permissions)
					{
						String permissionKey = childMenuKey + "_" + permission;
						if(!permissionMaps.containsKey(permissionKey))
						{
							Permission.MyType permissionType = Permission.MyType.getType(permission);
							mPermissionService.addPermission(permissionKey, permissionType.getName());
						}
					}
				}
				
			}
			
			
		} catch (IOException e) {
			LOG.error("updateMenuAndPermission error:", e);
		}
	}
	
	
	public static void main(String[] args) throws IOException
	{
	}


}
