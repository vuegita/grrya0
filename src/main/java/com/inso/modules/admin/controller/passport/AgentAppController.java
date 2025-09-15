package com.inso.modules.admin.controller.passport;


import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.model.AgentAppInfo;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.AgentAppService;
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
public class AgentAppController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private AgentAppService mAgentAppService;

    @RequiresPermissions("root_passport_agent_app_list")
    @RequestMapping("root_passport_agent_app")
    public String toPage(Model model)
    {
        return "admin/passport/user_agent_app_list";
    }

    @RequiresPermissions("root_passport_agent_app_list")
    @RequestMapping("getPassportAgentAppList")
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

        RowPager<AgentAppInfo> rowPager = mAgentAppService.queryScrollPage(pageVo, agentid, status);
        template.setData(rowPager);
        return template.toJSONString();
    }

    @RequiresPermissions("root_passport_agent_app_edit")
    @RequestMapping("toEditPassportAgentAppPage")
    public String toEditPassportUserVIPPage(Model model)
    {
        long agentid = WebRequest.getLong("agentid");
        if(agentid > 0)
        {
            AgentAppInfo vipInfo = mAgentAppService.findByAgentId(false, agentid);
            model.addAttribute("entity", vipInfo);
        }

        return "admin/passport/user_agent_app_edit";
    }

    @RequiresPermissions("root_passport_agent_app_edit")
    @RequestMapping("editPassportUserAgentApp")
    @ResponseBody
    public String editPassportUserAgentApp()
    {
        long agentid = WebRequest.getLong("agentid");
        String agentname = WebRequest.getString("agentname");

        String approveNotifyUrl = WebRequest.getString("approveNotifyUrl");

        Status modifySecret = Status.getType(WebRequest.getString("modifySecret"));

        Status status = Status.getType(WebRequest.getString("status"));

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(!StringUtils.isEmpty(approveNotifyUrl) && !approveNotifyUrl.matches("[a-zA-Z0-9/:\\.]+"))
        {
            apiJsonTemplate.setError(-1, "回调地址设置错误!");
            return apiJsonTemplate.toJSONString();
        }

        if(status == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(agentid > 0)
        {
            AgentAppInfo entity = mAgentAppService.findByAgentId(false, agentid);
            if(entity == null)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
                return apiJsonTemplate.toJSONString();
            }

            // 不更新VIP
            mAgentAppService.updateInfo(agentid, approveNotifyUrl, modifySecret, status);
        }
        else
        {
            if(StringUtils.isEmpty(agentname))
            {
                apiJsonTemplate.setError(-1, "请输入正确代理名!");
                return apiJsonTemplate.toJSONString();
            }

            UserInfo agentinfo = mUserService.findByUsername(false, agentname);
            if(agentinfo == null)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
                return apiJsonTemplate.toJSONString();
            }

            UserInfo.UserType userType = UserInfo.UserType.getType(agentinfo.getType());
            if(userType == null || userType != UserInfo.UserType.AGENT)
            {
                apiJsonTemplate.setError(-1, "请输入正确代理名!");
                return apiJsonTemplate.toJSONString();
            }

            if(mAgentAppService.findByAgentId(false, agentid) != null)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST);
                return apiJsonTemplate.toJSONString();
            }

            mAgentAppService.add(agentinfo, approveNotifyUrl, status);

            // 默认正常为赠送, 走正常流程
            //mWithdrawlLimitManager.increAmount(false, userInfo, vipInfo.getPrice());
        }
        return apiJsonTemplate.toJSONString();
    }





}
