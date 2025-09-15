package com.inso.modules.admin.agent.controller.passport;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.framework.utils.ValidatorUtils;
import com.inso.modules.admin.agent.AgentAuthManager;
import com.inso.modules.admin.helper.AgentAccountHelper;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.model.SystemFollowType;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.SystemFollowService;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.SystemRunningMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 用户系统关注列表
 */
@Controller
@RequestMapping("/alibaba888/agent")
public class SystemFollowController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private SystemFollowService mSystemFollowService;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private AgentAuthManager mAgentAuthManager;


    @RequestMapping("toEditSystemUserFollowPage")
    public String toEditSystemUserFollowPage(Model model)
    {
        SystemFollowType[] values = SystemFollowType.values();
        model.addAttribute("followArray", values);
        return "admin/agent/passport/user_system_follow_edit";
    }


    @RequestMapping("addUserSystemFollow")
    @ResponseBody
    public String addUserSystemFollow()
    {
        //已检查权限
        long agentid = AgentAccountHelper.getAdminAgentid();

        String username = WebRequest.getString("username");
        String typeString = WebRequest.getString("type");
        String remark = WebRequest.getString("remark");

        SystemFollowType followType = SystemFollowType.getType(typeString);

        ApiJsonTemplate api = new ApiJsonTemplate();
        // 没有代理无法查询
        if(agentid <= 0)
        {
            api.setData(RowPager.getEmptyRowPager());
            return api.toJSONString();
        }

        if (!ValidatorUtils.checkUsername(username)) {
            api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "username is only character and number ! and range is 6 <= length <= 20");
            return api.toJSONString();
        }

        if(followType == null)
        {
            followType = SystemFollowType.SIMPLE;
        }

        if(!StringUtils.isEmpty(remark) && remark.length() > 200)
        {
            api.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return api.toJSONString();
        }

        try {
            UserInfo userInfo = mUserService.findByUsername(false, username);
            if(!mAgentAuthManager.verifyUserData(userInfo.getId())){
                api.setJsonResult(SystemErrorResult.ERR_SYSTEM);
                return api.toJSONString();
            }

            UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());
            mSystemFollowService.add(userAttr.getUserid(), userAttr.getAgentid(),userAttr.getDirectStaffid(), followType, remark);
        } catch (Exception exception) {
            api.setJsonResult(SystemErrorResult.ERR_EXIST);
        }
        return api.toJSONString();
    }



    @RequestMapping("root_passport_user_system_follow")
    public String toSystemSystemUserFollowListPage(Model model)
    {
        SystemFollowType[] values = SystemFollowType.values();
        model.addAttribute("followArray", values);

        Boolean  isCrypto = SystemRunningMode.getSystemConfig() == SystemRunningMode.CRYPTO;
        model.addAttribute("isCrypto", isCrypto);
        return "admin/agent/passport/user_system_follow_list";
    }


    @RequestMapping("getUserSystemFollowList")
    @ResponseBody
    public String getUserSystemFollowList()
    {
        //已检查权限
        long agentid = AgentAccountHelper.getAdminAgentid();

        String username = WebRequest.getString("username");
        String typeString = WebRequest.getString("type");
        SystemFollowType followType = SystemFollowType.getType(typeString);
        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));

        ApiJsonTemplate api = new ApiJsonTemplate();
        // 没有代理无法查询
        if(agentid <= 0)
        {
            api.setData(RowPager.getEmptyRowPager());
            return api.toJSONString();
        }

        long userid = mUserQueryManager.findUserid(username);


        // 如果是员工登陆，则员工只能查看自己下级会员的数据
        UserInfo currentLoginInfo = AgentAccountHelper.getAdminLoginInfo();
        long staffid = -1;
        if(UserInfo.UserType.STAFF.getKey().equalsIgnoreCase(currentLoginInfo.getType
                ()))
        {
            staffid = currentLoginInfo.getId();
        }
        RowPager<UserInfo> rowPager = mSystemFollowService.queryScrollPage(pageVo, userid, agentid , staffid, followType);
        api.setData(rowPager);
        return api.toJSONString();
    }


    @RequestMapping("deleteUserSystemFollow")
    @ResponseBody
    public String deleteSystemUserFollow()
    {
        //已检查权限
        String username = WebRequest.getString("username");

        ApiJsonTemplate api = new ApiJsonTemplate();

        if (!ValidatorUtils.checkUsername(username)) {
            api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "username is only character and number ! and range is 6 <= length <= 20");
            return api.toJSONString();
        }

        UserInfo userInfo = mUserService.findByUsername(false, username);

        if(userInfo == null)
        {
            api.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return api.toJSONString();
        }

        if(!mAgentAuthManager.verifyUserData(userInfo.getId())){
            api.setJsonResult(SystemErrorResult.ERR_SYSTEM);
            return api.toJSONString();
        }

        mSystemFollowService.delete(userInfo.getId());
        return api.toJSONString();
    }

}
