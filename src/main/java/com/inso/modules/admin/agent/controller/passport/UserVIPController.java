package com.inso.modules.admin.agent.controller.passport;


import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.framework.utils.ValidatorUtils;
import com.inso.modules.ad.core.service.CategoryService;
import com.inso.modules.admin.agent.AgentAuthManager;
import com.inso.modules.admin.helper.AgentAccountHelper;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.model.UserVIPInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.passport.user.service.UserVIPService;
import com.inso.modules.web.model.VIPInfo;
import com.inso.modules.web.model.VIPType;
import com.inso.modules.web.service.VIPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@Controller
@RequestMapping("/alibaba888/agent/passport")
public class UserVIPController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private UserMoneyService mUserMoneyService;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private CategoryService mCategoryService;

    @Autowired
    private VIPService mVIPService;

    @Autowired
    private UserVIPService mUserVIPService;

    @Autowired
    private AgentAuthManager mAgentAuthManager;

    @RequestMapping("/user_vip/page")
    public String toUserVipPage(Model model)
    {
        String agentname = AgentAccountHelper.getUsername();
        UserInfo agentInfo = mUserService.findByUsername(false, agentname);

        String value = "0";
        if(agentInfo != null && agentInfo.getType().equalsIgnoreCase(UserInfo.UserType.AGENT.getKey()))
        {
            value = "1";
        }
        model.addAttribute("isAgent", value);


        VIPType[] vipTypeList = VIPType.values();
        model.addAttribute("vipTypeList", vipTypeList);

        return "admin/agent/passport/user_vip_list";
    }

    @RequestMapping("getPassportUserVIPList")
    @ResponseBody
    public String getPassportUserVIPList()
    {
        long agentid = AgentAccountHelper.getAdminAgentid();


        String time = WebRequest.getString("time");

        String username = WebRequest.getString("username");


        String statusStr = WebRequest.getString("status");
        Status status = Status.getType(statusStr);

        String vipTypeStr = WebRequest.getString("vipType");
        VIPType vipType = VIPType.getType(vipTypeStr);

        ApiJsonTemplate template = new ApiJsonTemplate();
        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        pageVo.parseTime(time);

        // 没有代理无法查询
        if(agentid <= 0)
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }
        // 如果是员工登陆，则员工只能查看自己下级会员的数据
        UserInfo currentLoginInfo = AgentAccountHelper.getAdminLoginInfo();
        long staffid = -1;
        if(UserInfo.UserType.STAFF.getKey().equalsIgnoreCase(currentLoginInfo.getType()))
        {
            staffid = currentLoginInfo.getId();
        }

        long userid = mUserQueryManager.findUserid(username);


        RowPager<UserVIPInfo> rowPager = mUserVIPService.queryScrollPage(pageVo, agentid, staffid, userid, status, vipType);
        template.setData(rowPager);
        return template.toJSONString();
    }


    @RequestMapping("toEditPassportUserVIPPage")
    public String toEditPassportUserVIPPage(Model model)
    {
        //已检查权限
        long id = WebRequest.getLong("id");
        if(id > 0)
        {
            UserVIPInfo vipInfo = mUserVIPService.findById(false, id);
            if(vipInfo!=null){
                if(!mAgentAuthManager.verifyUserData(vipInfo.getUserid())){
                    return "admin/agent/err";
                }
            }
            model.addAttribute("entity", vipInfo);
        }

        List<VIPInfo> allVipList = mVIPService.queryAllEnable(false, VIPType.AD);
        model.addAttribute("allVipList", allVipList);

        VIPType[] vipTypeList = VIPType.values();
        model.addAttribute("vipTypeList", vipTypeList);
        return "admin/agent/passport/user_vip_edit";
    }


    @RequestMapping("editPassportUserVIP")
    @ResponseBody
    public String editPassportUserVIP()
    {
        //已检查权限
        long id = WebRequest.getLong("id");

        String statusStr = WebRequest.getString("status");
        Status status = Status.getType(statusStr);

        String username = WebRequest.getString("username");

        long vipid = WebRequest.getLong("vipid");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(StringUtils.isEmpty(username) || !ValidatorUtils.checkUsername(username))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(status == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        VIPInfo vipInfo = mVIPService.findById(false, vipid);
        if(vipInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        if(id > 0)
        {
            UserVIPInfo userVIPInfo = mUserVIPService.findById(false, id);
            if(userVIPInfo == null)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
                return apiJsonTemplate.toJSONString();
            }
            if(!mAgentAuthManager.verifyUserData(userVIPInfo.getUserid())){
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYSTEM);
                return apiJsonTemplate.toJSONString();
            }


            mUserVIPService.updateInfo(userVIPInfo, status, vipInfo, null);
        }
        else
        {
            UserInfo userInfo = mUserService.findByUsername(false, username);
            if(userInfo == null)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
                return apiJsonTemplate.toJSONString();
            }
            if(!mAgentAuthManager.verifyUserData(userInfo.getId())){
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYSTEM);
                return apiJsonTemplate.toJSONString();
            }
            UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());
            mUserVIPService.addVip(userAttr, vipInfo, status);
        }
        return apiJsonTemplate.toJSONString();
    }



}
