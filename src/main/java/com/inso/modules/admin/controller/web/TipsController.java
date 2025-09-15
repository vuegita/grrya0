package com.inso.modules.admin.controller.web;


import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.model.*;
import com.inso.modules.web.service.StaffKefuService;
import com.inso.modules.web.service.TgsmsService;
import com.inso.modules.web.service.TipsService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class TipsController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private StaffKefuService mStaffKefuService;

    @Autowired
    private TipsService mTipsService;

    @Autowired
    private TgsmsService mTgsmsService;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @RequiresPermissions("root_web_tips_agent_list")
    @RequestMapping("root_web_tips_agent")
    public String toBankCardPage(Model model)
    {
        return "admin/web/agent_tips_list";
    }


    @RequiresPermissions("root_web_tips_agent_list")
    @RequestMapping("getWebAgentTipsList")
    @ResponseBody
    public String getWebAgentTipsList()
    {
//        String time = WebRequest.getString("time");
        String agentname = WebRequest.getString("agentname");

        String statusString = WebRequest.getString("status");
        Status status = Status.getType(statusString);

        long agentid = mUserQueryManager.findUserid(agentname);


        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
//        pageVo.parseTime(time);

        RowPager<Tips> rowPager = mTipsService.queryScrollPage(pageVo, agentid, status,-1,-1);
        template.setData(rowPager);

        return template.toJSONString();
    }

    @RequiresPermissions("root_web_tips_agent_add")
    @RequestMapping("toAddWebAgentTipsPage")
    public String toAddWebStaffKefuPage(Model model)
    {
        model.addAttribute("content", "");
        long id = WebRequest.getLong("id");
        if(id > 0)
        {
            Tips tips = mTipsService.findById(id);
            model.addAttribute("agnetTips", tips);
            model.addAttribute("content", tips.getContent());
        }

        TipsType.addFreemarkerModel(model);

        StaffkefuType.addFreemarkerModel(model);
        return "admin/web/agent_tips_add";
    }

    @RequiresPermissions({"root_web_tips_agent_add", "root_web_tips_agent_edit"})
    @RequestMapping("editWebAgentTips")
    @ResponseBody
    public String editWebAgentTips()
    {
        long id = WebRequest.getLong("id");

        String agentname = WebRequest.getString("agentname");

        String title = WebRequest.getString("title");
        //String type = WebRequest.getString("type");
        String content = WebRequest.getString("content");

        String statusString = WebRequest.getString("status");


        TipsType type = TipsType.getType(WebRequest.getString("type"));
        Status status = Status.getType(statusString);

        ApiJsonTemplate template = new ApiJsonTemplate();

        if(StringUtils.isEmpty(title) || title.length() > 100)
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        if(StringUtils.isEmpty(content) || content.length() > 2500)
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }



        UserInfo staffInfo = mUserService.findByUsername(false, agentname);
        if(staffInfo == null)
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        if(type.getKey().equals(TipsType.AGENT.getKey())){
            if(!UserInfo.UserType.AGENT.getKey().equalsIgnoreCase(staffInfo.getType()))
            {
                template.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return template.toJSONString();
            }
        }else if(type.getKey().equals(TipsType.STAFF.getKey())){
        if(!UserInfo.UserType.STAFF.getKey().equalsIgnoreCase(staffInfo.getType()))
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }
        }else{
            if(!UserInfo.UserType.MEMBER.getKey().equalsIgnoreCase(staffInfo.getType()))
            {
                template.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return template.toJSONString();
            }

        }






//        UserAttr userAttr = mUserAttrService.find(false, staffInfo.getId());
//        if(userAttr == null)
//        {
//            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
//            return template.toJSONString();
//        }

        if(id > 0)
        {
            mTipsService.updateInfo(id, title, type, content, status, null);
        }
        else
        {

            List<Tips> list= mTipsService.findAgentid(true ,staffInfo.getId());
            if(list.size()>0){
                template.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "同一个用户名不要重复添加公告!");
                return template.toJSONString();
            }else{

                UserAttr userAttr = mUserAttrService.find(false, staffInfo.getId());
                if(userAttr == null)
                {
                    template.setJsonResult(SystemErrorResult.ERR_PARAMS);
                    return template.toJSONString();
                }

                mTipsService.addTips(userAttr, title, type, content);
            }
        }
        return template.toJSONString();
    }



    @RequiresPermissions("root_web_tips_agent_list")
    @RequestMapping("deleteAgentTips")
    @ResponseBody
    public String deleteAgentTips()
    {
        long tipsid = WebRequest.getLong("tipsid");
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(tipsid <= 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        Tips model = mTipsService.findById(tipsid);
        if(model == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        mTipsService.deleteById(tipsid);
        return apiJsonTemplate.toJSONString();

    }

































    @RequiresPermissions("root_web_tgsms_agent_list")
    @RequestMapping("root_web_tgsms_agent")
    public String totgsmslistPage(Model model)
    {
        return "admin/web/agent_tgsms_list";
    }



    @RequiresPermissions("root_web_tgsms_agent_list")
    @RequestMapping("getWebAgentTgsmsList")
    @ResponseBody
    public String getWebAgentTgsmsList()
    {

        String agentname = WebRequest.getString("agentname");

        String statusString = WebRequest.getString("status");
        Status status = Status.getType(statusString);

        long agentid = mUserQueryManager.findUserid(agentname);


        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
//        pageVo.parseTime(time);

        RowPager<Tgsms> rowPager = mTgsmsService.queryScrollPage(pageVo, status,agentid,-1);
        template.setData(rowPager);

        return template.toJSONString();
    }



    @RequiresPermissions("root_web_tgsms_agent_add")
    @RequestMapping("toAddWebAgentTgsmsPage")
    public String toAddWebAgentTgsmsPage(Model model)
    {
        //model.addAttribute("content", "");
        long id = WebRequest.getLong("id");
        if(id > 0)
        {
            Tgsms tips = mTgsmsService.findById(id);
            model.addAttribute("agnetTips", tips);
//            model.addAttribute("content", tips.getContent());

        }

        TipsType.addFreemarkerModel(model);

        StaffkefuType.addFreemarkerModel(model);
        return "admin/web/agent_tgsms_add";
    }




    @RequiresPermissions({"root_web_tgsms_agent_add", "root_web_tgsms_agent_edit"})
    @RequestMapping("editWebAgentTgsms")
    @ResponseBody
    public String editWebAgentTgsms()
    {
        long id = WebRequest.getLong("id");

        String agentname = WebRequest.getString("agentname");

        String title = WebRequest.getString("rbtoken");
        //String type = WebRequest.getString("type");
        String content = WebRequest.getString("chatid");

        String statusString = WebRequest.getString("status");


        TipsType type = TipsType.getType(WebRequest.getString("type"));
        Status status = Status.getType(statusString);

        ApiJsonTemplate template = new ApiJsonTemplate();

        if(StringUtils.isEmpty(title) || title.length() > 250)
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        if(StringUtils.isEmpty(content) || content.length() > 250)
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }



        UserInfo staffInfo = mUserService.findByUsername(false, agentname);
        if(staffInfo == null)
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        if(type.getKey().equals(TipsType.AGENT.getKey())){
            if(!UserInfo.UserType.AGENT.getKey().equalsIgnoreCase(staffInfo.getType()))
            {
                template.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return template.toJSONString();
            }
        }else if(type.getKey().equals(TipsType.STAFF.getKey())){
            if(!UserInfo.UserType.STAFF.getKey().equalsIgnoreCase(staffInfo.getType()))
            {
                template.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return template.toJSONString();
            }
        }else{
            if(!UserInfo.UserType.MEMBER.getKey().equalsIgnoreCase(staffInfo.getType()))
            {
                template.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return template.toJSONString();
            }

        }


        if(id > 0)
        {
            mTgsmsService.updateInfo(id, title, type, content, status, null);
        }
        else
        {

//            List<Tips> list= mTgsmsService.findAgentid(true ,staffInfo.getId());
//            if(list.size()>0){
//                template.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "同一个用户名不要重复添加公告!");
//                return template.toJSONString();
//            }else{
//
//                UserAttr userAttr = mUserAttrService.find(false, staffInfo.getId());
//                if(userAttr == null)
//                {
//                    template.setJsonResult(SystemErrorResult.ERR_PARAMS);
//                    return template.toJSONString();
//                }
//
//                mTipsService.addTips(userAttr, title, type, content);
//            }

            UserAttr userAttr = mUserAttrService.find(false, staffInfo.getId());
            if(userAttr == null)
            {
                template.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return template.toJSONString();
            }

            mTgsmsService.addTgsms(userAttr, title, type, content);

        }
        return template.toJSONString();
    }



    @RequiresPermissions("root_web_tgsms_agent_list")
    @RequestMapping("deleteAgentTgsms")
    @ResponseBody
    public String deleteAgentTgsms()
    {
        long tipsid = WebRequest.getLong("tipsid");
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(tipsid <= 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        Tgsms model = mTgsmsService.findById(tipsid);
        if(model == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        mTgsmsService.deleteById(tipsid);
        return apiJsonTemplate.toJSONString();

    }



}
