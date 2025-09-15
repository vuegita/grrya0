package com.inso.modules.admin.controller.passport;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.helper.AdminAccountHelper;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.model.AgentConfigInfo;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.AgentConfigService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class AgentConfigController {

//    @Autowired
//    private ConfigService mConfigService;
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private UserAttrService mUserAttrService;
//
//    @Autowired
//    private ApproveAuthService mApproveAuthService;
//
    @Autowired
    private UserQueryManager mUserQueryManager;
//
//    @Autowired
//    private TransferOrderService mTransferOrderService;


    @Autowired
    private AgentConfigService mAgentConfigService;


    @RequiresPermissions("root_passport_agent_config_list")
    @RequestMapping("root_passport_agent_config")
    public String toList(Model model, HttpServletRequest request)
    {
        boolean isAdmin = AdminAccountHelper.isNy4timeAdminOrDEV();
        model.addAttribute("isSuperAdmin", isAdmin + StringUtils.getEmpty());

        AgentConfigInfo.AgentConfigType.addModel(model);

        return "admin/passport/user_agent_config_list";
    }

    @RequiresPermissions("root_passport_agent_config_list")
    @RequestMapping("getPassportAgentConfigList")
    @ResponseBody
    public String getDataList()
    {
        String time = WebRequest.getString("time");
        String agentname = WebRequest.getString("agentname");

        AgentConfigInfo.AgentConfigType type = AgentConfigInfo.AgentConfigType.getType(WebRequest.getString("type"));
        Status status = Status.getType(WebRequest.getString("status"));

        ApiJsonTemplate template = new ApiJsonTemplate();

        long agentid = mUserQueryManager.findUserid(agentname);

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        pageVo.parseTime(time);

        RowPager<AgentConfigInfo> rowPager = mAgentConfigService.queryScrollPage(pageVo, agentid, type, status);

        template.setData(rowPager);
        return template.toJSONString();
    }

    @RequiresPermissions("root_passport_agent_config_edit")
    @RequestMapping("toPassportAgentConfigInfoPage")
    public String toEdigPage(Model model, HttpServletRequest request)
    {
        long id = WebRequest.getLong("id");
        if(id > 0)
        {
            AgentConfigInfo entity = mAgentConfigService.findById(id);
            model.addAttribute("entity", entity);
        }

        AgentConfigInfo.AgentConfigType.addModel(model);
        return "admin/passport/user_agent_config_edit";
    }

    @RequiresPermissions("root_passport_agent_config_edit")
    @RequestMapping("upatePassportAgentConfigInfo")
    @ResponseBody
    public String updateInfo(Model model, HttpServletRequest request)
    {
        long id = WebRequest.getLong("id");
        String agentname = WebRequest.getString("agentname");
        String value = WebRequest.getString("value");
        value = StringUtils.getNotEmpty(value);

        AgentConfigInfo.AgentConfigType profitType = AgentConfigInfo.AgentConfigType.getType(WebRequest.getString("agentConfigType"));

        Status status = Status.getType(WebRequest.getString("status"));

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();


        if(agentname == null || status == null || value.length() > 100)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }


        try {
            if(id > 0)
            {
                AgentConfigInfo entity = mAgentConfigService.findById(id);
                mAgentConfigService.updateInfo(entity, value, status);
            }
            else
            {

                UserInfo userInfo = mUserQueryManager.findUserInfo(agentname);
                if(userInfo == null || !UserInfo.UserType.AGENT.getKey().equalsIgnoreCase(userInfo.getType()))
                {
                    apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "当前代理不存在或非代理用户! ");
                    return apiJsonTemplate.toJSONString();
                }

                mAgentConfigService.add(userInfo, profitType, value, status);
            }
        } catch (Exception e) {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST);
        }
        return apiJsonTemplate.toJSONString();
    }




}
