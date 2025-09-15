package com.inso.modules.admin.core.controller;

import java.util.List;
import java.util.Map;

import com.inso.modules.common.WhiteIPManager;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.config.shiro.ShiroRealm;
import com.inso.modules.admin.core.helper.MenuHelper;
import com.inso.modules.admin.core.model.Admin;
import com.inso.modules.admin.core.model.Menu;
import com.inso.modules.admin.core.model.Permission;
import com.inso.modules.admin.core.model.Role;
import com.inso.modules.admin.core.service.AdminService;
import com.inso.modules.admin.core.service.MenuService;
import com.inso.modules.admin.core.service.PermissionService;
import com.inso.modules.admin.core.service.RoleService;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class RoleController {

    @Autowired
    private AdminService mAdminService;

    @Autowired
    private RoleService mRoleService;

    @Autowired
    private MenuService menuService;


    @Autowired
    private PermissionService mPermissionService;


    @RequiresPermissions("root_sys_role_list")
    @RequestMapping("root_sys_role")
    public String toListPage() {
        return "admin/core/sys/role_list";
    }

    @RequiresPermissions("root_sys_role_list")
    @RequestMapping("getRoleList")
    @ResponseBody
    public String getRoleList() {
        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        ApiJsonTemplate template = new ApiJsonTemplate();
        RowPager<Role> rowPager = mRoleService.queryScrollPage(pageVo);
        template.setData(rowPager);
        return template.toJSONString();
    }

    @RequiresPermissions("root_sys_role_edit")
    @RequestMapping(value = "toEditRole")
    public String toEditRole(Model model) {
        String roleName = WebRequest.getString("roleName");
        Role role = mRoleService.findByName(roleName);
        if(role != null)
        {
            model.addAttribute("role", role);
        }
        return "admin/core/sys/role_edit";
    }

    @RequiresPermissions("root_sys_role_edit")
    @RequestMapping(value = "addOrEditRole")
    @ResponseBody
    public String addOrEditRole() {
        String roleName = WebRequest.getString("roleName");
        String remark = WebRequest.getString("remark");
        String remoteip = WebRequest.getRemoteIP();

        ApiJsonTemplate template = new ApiJsonTemplate();

        if(!WhiteIPManager.getInstance().verify(remoteip))
        {
            template.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FAILURE);
            return template.toJSONString();
        }

        Role role = mRoleService.findByName(roleName);
        if(role == null)
        {
            if(StringUtils.isEmpty(roleName) || StringUtils.isEmpty(remark))
            {
                template.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return template.toJSONString();
            }

            mRoleService.addRole(roleName, remark);
        }
        else
        {
            if(!role.getRemark().equalsIgnoreCase(remark))
            {
                mRoleService.updateRemark(role.getName(), remark);
            }
        }

        return template.toJSONString();
    }

    @RequiresPermissions("root_sys_role_delete")
    @RequestMapping(value = "deleteRole", method = RequestMethod.POST)
    @ResponseBody
    public String deleteRole() {
        String roleName = WebRequest.getString("roleName");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(Role.DEFAULT_SUPER_ADMIN_ROLE_NAME.equalsIgnoreCase(roleName))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return apiJsonTemplate.toJSONString();
        }

        Role role = mRoleService.findByName(roleName);
        if(role == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        List<Admin> adminList = mAdminService.queryAllByRoleid(role.getId());
        if(!CollectionUtils.isEmpty(adminList))
        {
            for(Admin admin : adminList)
            {
                mAdminService.deleteAdmin(admin.getAccount());
            }
        }

        mRoleService.deleteRoleAndPermission(role);
        return apiJsonTemplate.toJSONString();
    }


    @RequiresPermissions("root_sys_role_delete")
    @RequestMapping(value = "getAdminSizeByRole")
    @ResponseBody
    public String getAdminSizeByRole()
    {
        String rolename = WebRequest.getString("roleName");
        Role role = mRoleService.findByName(rolename);
        List<Admin> adminList = mAdminService.queryAllByRoleid(role.getId());
        ApiJsonTemplate template = new ApiJsonTemplate();
        if(CollectionUtils.isEmpty(adminList))
        {
            template.setData(0);
        }
        else
        {
            template.setData(adminList.size());
        }
        return template.toJSONString();
    }


    @RequiresPermissions("root_sys_role_edit")
    @RequestMapping("toEditRolePermissionPage")
    public String toEditRolePermissionPage(Model model) {
        String roleName = WebRequest.getString("roleName");

        Role role = mRoleService.findByName(roleName);

        // 查询所有二级子菜单
        List<Menu> menuList = menuService.queryAll();
        List<Menu> childMenuList = Lists.newArrayList();
        for (Menu menu : menuList)
        {
            if(!menu.isRootNode())
            {
                childMenuList.add(menu);
            }
        }

        Map<String, Menu> allMenuMaps = Maps.newHashMap();
        for(Menu menu : childMenuList)
        {
            allMenuMaps.put(menu.getKey(), menu);
        }
        List<Menu> allMenu = MenuHelper.getMenuTree(menuList);

        // 查询所有权限
        List<Permission> allPermissionList = mPermissionService.queryAll();
        List<Permission> rolePermissionList = mPermissionService.queryAllByRoleid(role.getId());

        Map<String, Permission> allPermissionMaps = Maps.newHashMap();
        for(Permission tmp : allPermissionList)
        {
            allPermissionMaps.put(tmp.getKey(), tmp);
        }

        // 默认选择已添加的权限
        if(!CollectionUtils.isEmpty(rolePermissionList))
        {
            for(Permission tmp : rolePermissionList)
            {
                Permission allPermissionModel = allPermissionMaps.get(tmp.getKey());
                allPermissionModel.setChecked(true);
            }
        }

        // 把权限分配到各个所属菜单
        for(Permission tmp : allPermissionList)
        {
            String menuKey = tmp.getMenuKey();
            Menu menu =  allMenuMaps.get(menuKey);
            menu.getChildMenuPermissionList().add(tmp);
        }

        model.addAttribute("role", role);
        model.addAttribute("menuList", allMenu);

        return "admin/core/sys/role_permission";
    }


    @RequiresPermissions("root_sys_role_edit")
    @RequestMapping(value = "saveRolePermissions")
    @ResponseBody
    public String saveRolePermissions() {
        String roleName = WebRequest.getString("roleName");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(Role.DEFAULT_SUPER_ADMIN_ROLE_NAME.equalsIgnoreCase(roleName))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return apiJsonTemplate.toJSONString();
        }

        Role role = mRoleService.findByName(roleName);
        if(role == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        List<Admin> adminList = mAdminService.queryAllByRoleid(role.getId());
        if(!CollectionUtils.isEmpty(adminList))
        {
            for(Admin admin : adminList)
            {
                mAdminService.deleteAdmin(admin.getAccount());
            }
        }

        mRoleService.deleteRoleAndPermission(role);
        return apiJsonTemplate.toJSONString();
    }


    @RequiresPermissions("root_sys_role_edit")
    @RequestMapping(value = "updateRolePermissions")
    @ResponseBody
    public String updateRolePermissions() {
        String roleName = WebRequest.getString("roleName");
        String[] permissionKeyList = WebRequest.getStrings("permissiontKeyList", ",");
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(Role.DEFAULT_SUPER_ADMIN_ROLE_NAME.equalsIgnoreCase(roleName))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return apiJsonTemplate.toJSONString();
        }

        if(permissionKeyList == null || permissionKeyList.length == 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        Role role = mRoleService.findByName(roleName);
        if(role == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        // 查询所有权限
        List<Permission> allPermissionList = mPermissionService.queryAll();
        List<Permission> rolePermissionList = mPermissionService.queryAllByRoleid(role.getId());

        Map<String, Permission> allPermissionMaps = Maps.newHashMap();
        for(Permission tmp : allPermissionList)
        {
            allPermissionMaps.put(tmp.getKey(), tmp);
        }

        Map<String, Permission> rolePermissionMaps = Maps.newHashMap();
        if(!CollectionUtils.isEmpty(rolePermissionList))
        {
            for(Permission tmp : rolePermissionList)
            {
                rolePermissionMaps.put(tmp.getKey(), tmp);
            }
        }

        Map<String, String> needPermissionMaps = Maps.newHashMap();
        for(String tmp : permissionKeyList)
        {
            needPermissionMaps.put(tmp, StringUtils.getEmpty());
        }


        boolean isUpdated = false;
        for(Permission tmp : allPermissionList)
        {
            // add
            if(needPermissionMaps.containsKey(tmp.getKey()))
            {
                if(!rolePermissionMaps.containsKey(tmp.getKey()))
                {
                    mPermissionService.bindRoleAndPermission(role.getId(), tmp.getKey());
                    isUpdated = true;
                }
            }
            else
            {
                // delete
                if(rolePermissionMaps.containsKey(tmp.getKey()))
                {
                    mPermissionService.unbindRoleAndPermission(role.getId(), tmp.getKey());
                    isUpdated = true;
                }
            }
        }

        // 退出
        ShiroRealm.stickAllIgnoreSuperAdmin();
        return apiJsonTemplate.toJSONString();
    }

}
