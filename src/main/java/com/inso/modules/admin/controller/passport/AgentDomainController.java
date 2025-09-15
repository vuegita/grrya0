package com.inso.modules.admin.controller.passport;


import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.domain.model.AgentDomainInfo;
import com.inso.modules.passport.domain.service.AgentDomainService;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class AgentDomainController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private AgentDomainService agentConfigService;

    @RequiresPermissions("root_passport_agent_domain_list")
    @RequestMapping("root_passport_agent_domain")
    public String toPage(Model model)
    {
        return "admin/passport/user_agent_domain_list";
    }

    @RequiresPermissions("root_passport_agent_domain_list")
    @RequestMapping("getPassportAgentDomainList")
    @ResponseBody
    public String getDataList()
    {
        String time = WebRequest.getString("time");

        String agentname = WebRequest.getString("agentname");

        Status status = Status.getType(WebRequest.getString("status"));

        ApiJsonTemplate template = new ApiJsonTemplate();
        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        pageVo.parseTime(time);

        long agentid = mUserQueryManager.findUserid(agentname);;

        RowPager<AgentDomainInfo> rowPager = agentConfigService.queryScrollPage(pageVo, agentid, status);
        template.setData(rowPager);
        return template.toJSONString();
    }

    @RequiresPermissions("root_passport_agent_domain_edit")
    @RequestMapping("toEditPassportAgentDomainPage")
    public String toEditPassportAgentConfigPage(Model model)
    {
        long id = WebRequest.getLong("id");
        if(id > 0)
        {
            AgentDomainInfo vipInfo = agentConfigService.findByid(id);
            model.addAttribute("entity", vipInfo);
        }
        return "admin/passport/user_agent_domain_edit";
    }

    @RequiresPermissions("root_passport_agent_domain_edit")
    @RequestMapping("editPassportUserAgentDomain")
    @ResponseBody
    public String editPassportUserAgentDomain()
    {
        long id = WebRequest.getLong("id");
        String staffname = WebRequest.getString("staffname");

        String url = WebRequest.getString("url");

        Status status = Status.getType(WebRequest.getString("status"));

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(StringUtils.isEmpty(url))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(status == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(id > 0)
        {
            AgentDomainInfo entity = agentConfigService.findByid(id);
            if(entity == null)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
                return apiJsonTemplate.toJSONString();
            }

            // 不更新VIP
            agentConfigService.updateInfo(entity, url, status);
        }
        else
        {
            if(StringUtils.isEmpty(staffname))
            {
                apiJsonTemplate.setError(-1, "请输入正确员工名!");
                return apiJsonTemplate.toJSONString();
            }

            UserInfo staffInfo = mUserService.findByUsername(false, staffname);
            if(staffInfo == null)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
                return apiJsonTemplate.toJSONString();
            }

            UserInfo.UserType userType = UserInfo.UserType.getType(staffInfo.getType());
            if(userType == null || userType != UserInfo.UserType.STAFF)
            {
                apiJsonTemplate.setError(-1, "请输入正确员工名!");
                return apiJsonTemplate.toJSONString();
            }

            UserAttr userAttr = mUserAttrService.find(false, staffInfo.getId());

            agentConfigService.add(userAttr, url, status);

            // 默认正常为赠送, 走正常流程
            //mWithdrawlLimitManager.increAmount(false, userInfo, vipInfo.getPrice());
        }
        return apiJsonTemplate.toJSONString();
    }

    @RequiresPermissions("root_passport_agent_domain_edit")
    @RequestMapping("deletePassportUserAgentDomain")
    @ResponseBody
    public String deleteInfo()
    {
        long id = WebRequest.getLong("id");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        AgentDomainInfo vipInfo = agentConfigService.findByid(id);
        if(vipInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        agentConfigService.deleteInfo(vipInfo);

        return apiJsonTemplate.toJSONString();
    }




}
