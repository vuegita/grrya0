package com.inso.modules.admin.agent.controller.web;


import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.agent.AgentAuthManager;
import com.inso.modules.admin.helper.AgentAccountHelper;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.model.StaffkefuType;
import com.inso.modules.web.model.Tips;
import com.inso.modules.web.model.TipsType;
import com.inso.modules.web.service.StaffKefuService;
import com.inso.modules.web.service.TipsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/alibaba888/agent")
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
    private UserQueryManager mUserQueryManager;

    @Autowired
    private AgentAuthManager mAgentAuthManager;


    @RequestMapping("root_web_tips_agent")
    public String toBankCardPage(Model model)
    {
        return "admin/agent/web/agent_tips_list";
    }


    @RequestMapping("getWebAgentTipsList")
    @ResponseBody
    public String getWebAgentTipsList()
    {
        //已检查权限
        long agentid = AgentAccountHelper.getAdminAgentid();

        boolean isAgentLogin= AgentAccountHelper.isAgentLogin();

//        String time = WebRequest.getString("time");

        String agentname = WebRequest.getString("agentname");

        String staffname = WebRequest.getString("staffname");

        String statusString = WebRequest.getString("status");
        Status status = Status.getType(statusString);

        ApiJsonTemplate template = new ApiJsonTemplate();
        // 没有代理无法查询
        if(agentid <= 0)
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        if(!isAgentLogin){
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }
        UserInfo adminAgentInfo = mUserQueryManager.findUserInfo(agentname);
        if(adminAgentInfo!=null){
            if(UserInfo.UserType.AGENT.getKey().equalsIgnoreCase(adminAgentInfo.getType())){
                if(!mAgentAuthManager.verifyAgentData(adminAgentInfo.getId())){
                    template.setJsonResult(SystemErrorResult.ERR_SYSTEM);
                    return template.toJSONString();
                }
            }else if(UserInfo.UserType.STAFF.getKey().equalsIgnoreCase(adminAgentInfo.getType())){
                if(!mAgentAuthManager.verifyStaffData(adminAgentInfo.getId())){
                    template.setJsonResult(SystemErrorResult.ERR_SYSTEM);
                    return template.toJSONString();
                }
            }else{

                if(!mAgentAuthManager.verifyUserData(adminAgentInfo.getId())){
                    template.setJsonResult(SystemErrorResult.ERR_SYSTEM);
                    return template.toJSONString();
                }

            }



        }else{
           // adminAgentInfo=AgentAccountHelper.getAdminLoginInfo();
        }



        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
//        pageVo.parseTime(time);

        long staffid = mUserQueryManager.findUserid(staffname);

        // 如果是员工登陆，则员工只能查看自己下级会员的数据
        UserInfo currentLoginInfo = AgentAccountHelper.getAdminLoginInfo();
        if(UserInfo.UserType.STAFF.getKey().equalsIgnoreCase(currentLoginInfo.getType
                ()))
        {
            staffid = currentLoginInfo.getId();
        }

        long userid = -1;
        if(adminAgentInfo!=null){
            userid =adminAgentInfo.getId();
        }

        RowPager<Tips> rowPager = mTipsService.queryScrollPage(pageVo, userid, status ,agentid,  staffid);
        template.setData(rowPager);

        return template.toJSONString();
    }


    @RequestMapping("toAddWebAgentTipsPage")
    public String toAddWebStaffKefuPage(Model model)
    {
        //已检查权限
        boolean isAgentLogin= AgentAccountHelper.isAgentLogin();

        model.addAttribute("content", "");
        long id = WebRequest.getLong("id");
        if(id > 0)
        {
            Tips tips = mTipsService.findById(id);
            if(tips!=null){
                if(!mAgentAuthManager.verifyAgentData(tips.getBelongAgentid()) || !isAgentLogin){
                    return "admin/agent/err";
                }
            }

            model.addAttribute("agnetTips", tips);
            model.addAttribute("content", tips.getContent());
        }
        TipsType.addFreemarkerModel(model);

        StaffkefuType.addFreemarkerModel(model);
        return "admin/agent/web/agent_tips_add";
    }


    @RequestMapping("editWebAgentTips")
    @ResponseBody
    public String editWebAgentTips()
    {
        //已检查权限
        boolean isAgentLogin= AgentAccountHelper.isAgentLogin();
        long id = WebRequest.getLong("id");

        String agentname = WebRequest.getString("agentname");

        String title = WebRequest.getString("title");
     //   String type = WebRequest.getString("type");
        TipsType type = TipsType.getType(WebRequest.getString("type"));
        String content = WebRequest.getString("content");

        String statusString = WebRequest.getString("status");

        Status status = Status.getType(statusString);

        ApiJsonTemplate template = new ApiJsonTemplate();
        if(!isAgentLogin){
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }
     //   UserInfo staffInfo=AgentAccountHelper.getAdminLoginInfo();


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

//
        UserInfo staffInfo = mUserService.findByUsername(false, agentname);
        if(staffInfo == null)
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

//        if(!UserInfo.UserType.AGENT.getKey().equalsIgnoreCase(staffInfo.getType()))
//        {
//            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
//            return template.toJSONString();
//        }

        if(type.getKey().equals(TipsType.AGENT.getKey())){
            if(!UserInfo.UserType.AGENT.getKey().equalsIgnoreCase(staffInfo.getType()))
            {
                template.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return template.toJSONString();
            }

            if(!mAgentAuthManager.verifyAgentData(staffInfo.getId())){
                template.setJsonResult(SystemErrorResult.ERR_SYSTEM);
                return template.toJSONString();
            }


        }else if(type.getKey().equals(TipsType.STAFF.getKey())){
            if(!UserInfo.UserType.STAFF.getKey().equalsIgnoreCase(staffInfo.getType()))
            {
                template.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return template.toJSONString();
            }

            if(!mAgentAuthManager.verifyStaffData(staffInfo.getId())){
                template.setJsonResult(SystemErrorResult.ERR_SYSTEM);
                return template.toJSONString();
            }
        }else{
            if(!UserInfo.UserType.MEMBER.getKey().equalsIgnoreCase(staffInfo.getType()))
            {
                template.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return template.toJSONString();
            }

            if(!mAgentAuthManager.verifyUserData(staffInfo.getId())){
                template.setJsonResult(SystemErrorResult.ERR_SYSTEM);
                return template.toJSONString();
            }

        }



        if(id > 0)
        {
            Tips tips = mTipsService.findById(id);

            UserInfo agentUserInfo = AgentAccountHelper.getAgentInfo();
            if(tips.getBelongAgentid() ==  agentUserInfo.getId()){
                mTipsService.updateInfo(id, title, type, content, status, null);
            }

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

        if(!mAgentAuthManager.verifyAgentData(model.getBelongAgentid())){
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYSTEM);
            return apiJsonTemplate.toJSONString();
        }

        mTipsService.deleteById(tipsid);
        return apiJsonTemplate.toJSONString();

    }


}
