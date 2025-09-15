package com.inso.modules.admin.agent;

import com.inso.framework.context.MyEnvironment;
import com.inso.modules.passport.user.model.UserSecret;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserSecretService;
import com.inso.modules.passport.user.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.inso.modules.admin.core.service.MenuService;
import com.inso.modules.admin.helper.AgentMenuHelper;
import com.inso.modules.passport.user.model.UserInfo;

@Controller
@RequestMapping("/alibaba888/agent")
public class IndexController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private UserSecretService mUserSecretService;

    @RequestMapping("toIndex")
    public String toIndex(Model model) {
         String username = getUsername();
        if (username == null){
            return "redirect:/alibaba888/agent/toLogin";
        }

        UserSecret secretInfo = mUserSecretService.find(false, username);
        if(secretInfo.isGoogleLogin() || MyEnvironment.isDev())
        {
            model.addAttribute("menuList", AgentMenuHelper.getMenuList());
        }
        else
        {
            model.addAttribute("menuList", AgentMenuHelper.getSecurityMenuList());
        }

        model.addAttribute("username", username);
        return "admin/agent/index/index";
    }


    @RequestMapping("toWelcome")
    public String toWelcome(){
        return "admin/agent/welcome";
    }


    private String getUsername() {

        try {
            Subject subject = SecurityUtils.getSubject();
            UserInfo merchantInfo = (UserInfo)subject.getPrincipal();
            return merchantInfo.getName();
        } catch (Exception exception) {
            Subject subject = SecurityUtils.getSubject();
            subject.logout();
            return null;
        }
    }
}
