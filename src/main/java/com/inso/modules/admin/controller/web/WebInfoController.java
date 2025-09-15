package com.inso.modules.admin.controller.web;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.modules.web.logical.WebInfoManager;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class WebInfoController {

    @Autowired
    private WebInfoManager mWebInfoManager;

    @RequiresPermissions("root_web_about_us_list")
    @RequestMapping("root_web_about_us")
    public String toAboutUsPage(Model model)
    {
        WebInfoManager.TargetType targetType = WebInfoManager.TargetType.ABOUT_US;
        String value = mWebInfoManager.getInfo(targetType);
        model.addAttribute("content", value);
        return "admin/web/web_info_about_us";
    }

    @RequiresPermissions("root_web_activity_content_list")
    @RequestMapping("root_web_activity_content")
    public String toActivityContentPage(Model model)
    {
        WebInfoManager.TargetType targetType = WebInfoManager.TargetType.ACTIVITY_CONTENT;
        String value = mWebInfoManager.getInfo(targetType);
        model.addAttribute("content", value);
        return "admin/web/web_info_activity_content";
    }


    @RequiresPermissions("root_web_banner_content_list")
    @RequestMapping("root_web_banner_content")
    public String toBannerContentPage(Model model)
    {
        WebInfoManager.TargetType targetType = WebInfoManager.TargetType.BANNER_CONTENT;
        String value = mWebInfoManager.getInfo(targetType);
        model.addAttribute("content", value);
        return "admin/web/web_info_banner_content";
    }





    @RequiresPermissions("root_web_private_policy_list")
    @RequestMapping("root_web_private_policy")
    public String toPrivatePolicyPage(Model model)
    {
        WebInfoManager.TargetType targetType = WebInfoManager.TargetType.PRIVATE_POLICY;
        String value = mWebInfoManager.getInfo(targetType);
        model.addAttribute("content", value);
        return "admin/web/web_info_private_policy";
    }

    @RequiresPermissions("root_web_rg_bet_rule_list")
    @RequestMapping("root_web_rg_bet_rule")
    public String toBetRulePage(Model model)
    {
        WebInfoManager.TargetType targetType = WebInfoManager.TargetType.GAME_RG_BET_RULE;
        String value = mWebInfoManager.getInfo(targetType);
        model.addAttribute("content", value);
        return "admin/web/web_info_rg_bet_rule";
    }

    @RequiresPermissions("root_web_ab_bet_rule_list")
    @RequestMapping("root_web_ab_bet_rule")
    public String toABBetRulePage(Model model)
    {
        WebInfoManager.TargetType targetType = WebInfoManager.TargetType.GAME_AB_BET_RULE;
        String value = mWebInfoManager.getInfo(targetType);
        model.addAttribute("content", value);
        return "admin/web/web_info_ab_bet_rule";
    }

    @RequiresPermissions("root_web_fruit_bet_rule_list")
    @RequestMapping("root_web_fruit_bet_rule")
    public String toFruitbateRulePage(Model model)
    {
        WebInfoManager.TargetType targetType = WebInfoManager.TargetType.GAME_FRUIT_BET_RULE;
        String value = mWebInfoManager.getInfo(targetType);
        model.addAttribute("content", value);
        return "admin/web/web_info_fruit_bet_rule";
    }

    @RequiresPermissions("root_web_rebate_rule_list")
    @RequestMapping("root_web_rebate_rule")
    public String toRebateRulePage(Model model)
    {
        WebInfoManager.TargetType targetType = WebInfoManager.TargetType.REBATE_RULE;
        String value = mWebInfoManager.getInfo(targetType);
        model.addAttribute("content", value);
        return "admin/web/web_rebate_rule";
    }


    @RequiresPermissions("root_web_Phone_area_code_list")
    @RequestMapping("root_web_phone_area_code")
    public String toPhoneAreaCode(Model model)
    {
        WebInfoManager.TargetType targetType = WebInfoManager.TargetType.PHONE_AREA_CODE;
        String value = mWebInfoManager.getInfo(targetType);
        model.addAttribute("content", value);
        return "admin/web/web_phone_area_code";
    }


    @RequiresPermissions("root_web_register_phone_area_code_list")
    @RequestMapping("root_web_register_phone_area_code")
    public String toRegitserPhoneAreaCode(Model model)
    {
        WebInfoManager.TargetType targetType = WebInfoManager.TargetType.REGISTER_PHONE_AREA_CODE;
        String value = mWebInfoManager.getInfo(targetType);
        model.addAttribute("content", value);
        return "admin/web/web_register_phone_area_code";
    }







    @RequestMapping("updateWebInfo")
    @ResponseBody
    public String updateWebInfo(Model model)
    {
        String type = WebRequest.getString("type");
        String content = WebRequest.getString("content");

        WebInfoManager.TargetType targetType = WebInfoManager.TargetType.getType(type);
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(targetType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
        }

        mWebInfoManager.saveContent(targetType, content);
        return apiJsonTemplate.toJSONString();
    }
}
