package com.inso.modules.admin.agent.controller.web;


import com.inso.modules.admin.agent.AgentAuthManager;
import com.inso.modules.web.model.StaffkefuType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RegexUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.helper.AgentAccountHelper;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.model.StaffKefu;
import com.inso.modules.web.service.StaffKefuService;

import java.util.List;

@Controller
@RequestMapping("/alibaba888/agent/web")
public class StaffkefuController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private StaffKefuService mStaffKefuService;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private AgentAuthManager mAgentAuthManager;


    @RequestMapping("/staffkefu/page")
    public String toStaffkefuPage(Model model)
    {
        return "admin/agent/web/staff_kefu_list";
    }

    @RequestMapping("getWebStaffKefuList")
    @ResponseBody
    public String getWebStaffKefuList()
    {
//        String time = WebRequest.getString("time");

        long agentid = AgentAccountHelper.getAdminAgentid();
       // String agentname = WebRequest.getString("agentname");
        String staffname = WebRequest.getString("staffname");

        String statusString = WebRequest.getString("status");
        Status status = Status.getType(statusString);

        //long agentid = mUserQueryManager.findUserid(agentname);
        long staffid = mUserQueryManager.findUserid(staffname);

        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
//        pageVo.parseTime(time);

        UserInfo loginUserInfo = AgentAccountHelper.getAdminLoginInfo();
        // 当前登陆就是员工
        if(loginUserInfo.getType().equalsIgnoreCase(UserInfo.UserType.STAFF.getKey()))
        {
            staffid = loginUserInfo.getId();
        }
        RowPager<StaffKefu> rowPager = mStaffKefuService.queryScrollPage(pageVo, agentid, staffid, status);
        template.setData(rowPager);

        return template.toJSONString();
    }

    @RequestMapping("toAddWebStaffKefuPage")
    public String toAddWebStaffKefuPage(Model model)
    {
        long id = WebRequest.getLong("id");
        if(id > 0)
        {
            StaffKefu staffKefu = mStaffKefuService.findById(id);
            if(!mAgentAuthManager.verifyStaffData(staffKefu.getStaffid()))
            {
                return null;
            }

            model.addAttribute("staffKefu", staffKefu);
        }

        boolean isAgent = AgentAccountHelper.isAgentLogin();
        model.addAttribute("isAgent", isAgent);

        StaffkefuType.addFreemarkerModel(model);
        return "admin/agent/web/staff_kefu_add";
    }

//    @RequestMapping("deleteWebStaffKefu")
//    @ResponseBody
//    public String deleteWebStaffKefu()
//    {
//        long id = WebRequest.getLong("id");
//        ApiJsonTemplate template = new ApiJsonTemplate();
//        if(id > 0)
//        {
//            mStaffKefuService.deleteById(id);
//        }else{
//            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
//            return template.toJSONString();
//        }
//
//        return template.toJSONString();
//    }


    @RequestMapping("editWebStaffKefu")
    @ResponseBody
    public String editKefuMember()
    {
        UserInfo agentUserInfo = AgentAccountHelper.getAgentInfo();

        long id = WebRequest.getLong("id");

        String staffname = WebRequest.getString("staffname");

        String title = WebRequest.getString("title");
        String describe = WebRequest.getString("describe");
        String whatsapp = WebRequest.getString("whatsapp");
        String telegram = WebRequest.getString("telegram");

        String statusString = WebRequest.getString("status");

        Status status = Status.getType(statusString);

        ApiJsonTemplate template = new ApiJsonTemplate();
        // 没有代理无法查询
//        if(agentid <= 0)
//        {
//            template.setData(RowPager.getEmptyRowPager());
//            return template.toJSONString();
//        }


        if(!RegexUtils.isUrl(whatsapp) && !whatsapp.equals("0"))
        {
            template.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "whatsapp链接错误");
            return template.toJSONString();
        }

        if(!RegexUtils.isUrl(telegram) && !telegram.equals("0"))
        {
            template.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "telegram链接错误");
            return template.toJSONString();
        }

        if(StringUtils.isEmpty(title) || title.length() > 100)
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        if(StringUtils.isEmpty(describe) || describe.length() > 200)
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        if(StringUtils.isEmpty(whatsapp) && StringUtils.isEmpty(telegram))
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }


        UserInfo loginUserInfo = AgentAccountHelper.getAdminLoginInfo();
        // 当前登陆就是员工
        if(loginUserInfo.getType().equalsIgnoreCase(UserInfo.UserType.STAFF.getKey()))
        {
            staffname = loginUserInfo.getName();
        }

        UserInfo staffInfo = mUserService.findByUsername(false, staffname);
        if(staffInfo == null || !mAgentAuthManager.verifyStaffData(staffInfo.getId()))
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        if(!UserInfo.UserType.STAFF.getKey().equalsIgnoreCase(staffInfo.getType()))
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        UserAttr userAttr = mUserAttrService.find(false, staffInfo.getId());
        if(userAttr == null)
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        if(id > 0)
        {
            StaffKefu StaffKefu = mStaffKefuService.findById(id);
            if(StaffKefu.getAgentid() == agentUserInfo.getId()){
                mStaffKefuService.updateInfo(id, title, describe, null, whatsapp, telegram, status, null);
            }


        }
        else
        {
            StaffkefuType staffkefuType= StaffkefuType.getType(describe );
            List<StaffKefu> list= mStaffKefuService.findById(true ,userAttr, staffkefuType);
            if(list.size()>10){
                template.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "同一个员工不要重复添加同一客服类型的客服!");
                return template.toJSONString();
            }else{
                mStaffKefuService.addKefu(userAttr, title, describe, null, whatsapp, telegram);
            }


        }
        return template.toJSONString();
    }


}
