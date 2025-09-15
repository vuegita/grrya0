package com.inso.modules.admin.core.helper;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import com.inso.modules.admin.core.model.Menu;

public class MenuHelper {

    public static List<Menu> getMenuTree(List<Menu> menuList){
        Map<String,Menu> menuMap = new LinkedHashMap<String, Menu>();
        // 判断菜单列表是否为空
        if(CollectionUtils.isEmpty(menuList)) return menuList;
        for ( Menu menu : menuList ){
            if(menu.isRootNode()){
                menuMap.put(menu.getKey(), menu);
            }
        }

        for ( Menu menu : menuList ){
        	 if(menu.isRootNode()){
                 continue;
             }
        	 
            // 查找父级菜单
        	Menu parentMenu = menuMap.get(menu.getPkey());
            if ( null == parentMenu )
            {
            	continue;
            }
                
            List<Menu> childList = parentMenu.getChildList();
            childList.add(menu);
        }

        menuList.clear();
        for ( Map.Entry<String, Menu> entry : menuMap.entrySet() ){
            List<Menu> childList = entry.getValue().getChildList();
            if(!CollectionUtils.isEmpty(childList))
            {
                menuList.add(entry.getValue());
            }
        }

        // sort root
        Collections.sort(menuList);

        // sort child
        for(Menu parent : menuList)
        {
            List<Menu> childList = parent.getChildList();
            if(!CollectionUtils.isEmpty(childList))
            {
                Collections.sort(childList);
            }
        }

        return menuList;
    }

}
