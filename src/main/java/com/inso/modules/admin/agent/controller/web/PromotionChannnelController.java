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
import com.inso.modules.web.promotion_channel.model.PromotionChannelInfo;
import com.inso.modules.web.promotion_channel.model.PromotionChannelType;
import com.inso.modules.web.promotion_channel.service.PromotionChannelService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;

@Controller
@RequestMapping("/alibaba888/agent")
public class PromotionChannnelController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private PromotionChannelService promotionChannelService;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private AgentAuthManager mAgentAuthManager;

    @RequestMapping("root_web_promotion_channel")
    public String toBankCardPage(Model model)
    {
        PromotionChannelType.addFreemarker(model);
        return "admin/agent/web/promotion_channel_list";
    }

    @RequestMapping("root_web_promotion_channel/getDataList")
    @ResponseBody
    public String getWebAgentTipsList()
    {
        String time = WebRequest.getString("time");
        String agentname = WebRequest.getString("agentname");
        String staffname = WebRequest.getString("staffname");
        String contact = WebRequest.getString("contact");

        Status status = Status.getType(WebRequest.getString("status"));
        PromotionChannelType type = PromotionChannelType.getType(WebRequest.getString("type"));

        String name = WebRequest.getString("name");

        long agentid = mUserQueryManager.findUserid(agentname);
        long staffid = mUserQueryManager.findUserid(staffname);

        if(AgentAccountHelper.isAgentLogin())
        {
            agentid = AgentAccountHelper.getAdminAgentid();
        }
        else
        {
            agentid = AgentAccountHelper.getAgentInfo().getId();
            staffid = AgentAccountHelper.getAdminLoginInfo().getId();
        }

        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        if(!pageVo.parseTime(time))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        RowPager<PromotionChannelInfo> rowPager = promotionChannelService.queryScrollPage(pageVo, agentid, staffid, name, type, status);
        template.setData(rowPager);

        return template.toJSONString();
    }

    @RequestMapping("root_web_promotion_channel/edit/page")
    public String toAddWebStaffKefuPage(Model model)
    {
        long id = WebRequest.getLong("id");
        if(id > 0)
        {
            PromotionChannelInfo tips = promotionChannelService.findById(id);
            if(!mAgentAuthManager.verifyStaffData(tips.getStaffid()))
            {
                return null;
            }

            model.addAttribute("entity", tips);
        }

        PromotionChannelType.addFreemarker(model);
        return "admin/agent/web/promotion_channel_edit";
    }

    @RequestMapping("root_web_promotion_channel/edit")
    @ResponseBody
    public String editWebAgentTips()
    {
        long id = WebRequest.getLong("id");

//        String staffname = WebRequest.getString("staffname");

        String name = WebRequest.getString("name");
        String remark = WebRequest.getString("remark");
        String url = WebRequest.getString("url");
        String contact = WebRequest.getString("contact");

        PromotionChannelType type = PromotionChannelType.getType(WebRequest.getString("type"));
        Status status = Status.getType(WebRequest.getString("status"));

        long viewCount = WebRequest.getLong("viewCount");
        long subscribeCount = WebRequest.getLong("subscribeCount");
        BigDecimal amount = WebRequest.getBigDecimal("amount");

        ApiJsonTemplate template = new ApiJsonTemplate();

        if(AgentAccountHelper.isAgentLogin())
        {
            template.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return template.toJSONString();
        }

        if(StringUtils.isEmpty(name) || name.length() > 100)
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        if(StringUtils.isEmpty(url) )
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        if(StringUtils.isEmpty(remark) || remark.length() > 255)
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        if(id > 0)
        {
            PromotionChannelInfo tips = promotionChannelService.findById(id);
            if(!mAgentAuthManager.verifyStaffData(tips.getStaffid()))
            {
                return null;
            }

            promotionChannelService.updateInfo(id, name, subscribeCount, viewCount, contact, amount, status, remark);
        }
        else
        {
            UserInfo staffInfo = AgentAccountHelper.getAdminLoginInfo();
            if(!UserInfo.UserType.STAFF.getKey().equalsIgnoreCase(staffInfo.getType()))
            {
                template.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return template.toJSONString();
            }

            UserAttr userAttr = mUserAttrService.find(false, staffInfo.getId());
            promotionChannelService.add(name, type, url, userAttr, contact, subscribeCount, viewCount, amount, status, remark);
        }
        return template.toJSONString();
    }

}
