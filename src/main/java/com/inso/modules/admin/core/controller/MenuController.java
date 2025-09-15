package com.inso.modules.admin.core.controller;

import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.core.model.Menu;
import com.inso.modules.admin.core.service.MenuService;

import jodd.util.StringUtil;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class MenuController {

    @Autowired
    private MenuService menuService;


    @RequiresPermissions("root_sys_menu_list")
    @RequestMapping("root_sys_menu")
    public String toMenuList()
    {
        return "admin/core/sys/menu_list";
    }

    @RequiresPermissions("root_sys_menu_list")
    @RequestMapping("getMenuList")
    @ResponseBody
    public String getMenuList()
    {
        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        ApiJsonTemplate template = new ApiJsonTemplate();

        RowPager<Menu> rowPager = menuService.queryScrollPage(pageVo);
        template.setData(rowPager);

        return template.toJSONString();
    }

    @RequiresPermissions("root_sys_menu_edit")
    @RequestMapping(value = "toEditMenu")
    public String toEditMenu(Model model) {
        String key = WebRequest.getString("key");
        Menu menu = menuService.findByKey(key);
        if (!menu.isRootNode()) {
            //查询一级菜单
            List<Menu> menuList = menuService.queryAllRootNodeList();
            model.addAttribute("menuList", menuList);
        }
        model.addAttribute("menu", menu);
        return "/admin/core/sys/menu_edit";
    }

    @RequiresPermissions("root_sys_menu_edit")
    @RequestMapping(value = "editMenu")
    @ResponseBody
    public String editMenu() {
        String key = WebRequest.getString("key");
        String pkey = WebRequest.getString("pkey");
        String sort = WebRequest.getString("sort");
        String name = WebRequest.getString("name");

        ApiJsonTemplate template = new ApiJsonTemplate();

        Menu menu = menuService.findByKey(key);
        if(menu == null)
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        if(!menu.getName().equalsIgnoreCase(name))
        {
            menuService.updateName(key, name);
        }

        if(!StringUtils.isEmpty(pkey) && !pkey.equalsIgnoreCase(menu.getPkey()))
        {
            menuService.updatePKey(key, pkey);
        }

        if(!StringUtil.isEmpty(sort))
        {
            menuService.updateSort(key, StringUtils.asInt(sort));
        }

        return template.toJSONString();
    }

}
