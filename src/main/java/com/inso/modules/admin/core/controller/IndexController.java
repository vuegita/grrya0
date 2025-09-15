package com.inso.modules.admin.core.controller;

import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.collect.Lists;
import com.inso.modules.admin.core.helper.MenuHelper;
import com.inso.modules.admin.core.model.Admin;
import com.inso.modules.admin.core.model.Menu;
import com.inso.modules.admin.core.service.MenuService;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class IndexController {

    @Autowired
    private MenuService menuService;

    @RequiresPermissions("/alibaba888/Liv2sky3soLa93vEr62/toIndex")
    @RequestMapping("toIndex")
    public String toIndex(Model model) {
        Subject subject = SecurityUtils.getSubject();
        Admin admin = (Admin) subject.getPrincipal();

        List<Menu> menuList = menuService.queryAll();
        List<Menu> permissionMenuList = Lists.newArrayList();
        for(Menu menu : menuList)
        {
            if(menu.isRootNode())
            {
                permissionMenuList.add(menu);
            }
            else if(subject.isPermitted(menu.getPermissionKey()))
            {
                permissionMenuList.add(menu);
            }
            else
            {
                System.out.println("menu = " + menu.getKey());
            }
        }
        model.addAttribute("menuList", MenuHelper.getMenuTree(permissionMenuList));
        model.addAttribute("admin", admin);

        return "admin/core/index/index";
    }

    @RequiresPermissions("/alibaba888/Liv2sky3soLa93vEr62/toWelcome")
    @RequestMapping("toWelcome")
    public String toWelcome(){
        return "admin/core/welcome";
    }

}
