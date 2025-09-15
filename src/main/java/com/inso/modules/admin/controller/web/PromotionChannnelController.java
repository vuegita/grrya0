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
import com.inso.modules.web.model.StaffkefuType;
import com.inso.modules.web.model.Tips;
import com.inso.modules.web.model.TipsType;
import com.inso.modules.web.promotion_channel.model.PromotionChannelInfo;
import com.inso.modules.web.promotion_channel.model.PromotionChannelType;
import com.inso.modules.web.promotion_channel.service.PromotionChannelService;
import com.inso.modules.web.service.StaffKefuService;
import com.inso.modules.web.service.TipsService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class PromotionChannnelController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private PromotionChannelService promotionChannelService;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @RequiresPermissions("root_web_promotion_channel_list")
    @RequestMapping("root_web_promotion_channel")
    public String toBankCardPage(Model model)
    {
        PromotionChannelType.addFreemarker(model);
        return "admin/web/promotion_channel_list";
    }

    @RequiresPermissions("root_web_promotion_channel_list")
    @RequestMapping("root_web_promotion_channel/getDataList")
    @ResponseBody
    public String getWebAgentTipsList()
    {
        String time = WebRequest.getString("time");
        String agentname = WebRequest.getString("agentname");
        String staffname = WebRequest.getString("staffname");

        Status status = Status.getType(WebRequest.getString("status"));
        PromotionChannelType type = PromotionChannelType.getType(WebRequest.getString("type"));

        String name = WebRequest.getString("name");

        long agentid = mUserQueryManager.findUserid(agentname);
        long staffid = mUserQueryManager.findUserid(staffname);

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

    @RequiresPermissions("root_web_promotion_channel_edit")
    @RequestMapping("root_web_promotion_channel/edit/page")
    public String toAddWebStaffKefuPage(Model model)
    {
        long id = WebRequest.getLong("id");
        if(id > 0)
        {
            PromotionChannelInfo tips = promotionChannelService.findById(id);
            model.addAttribute("entity", tips);
        }

        PromotionChannelType.addFreemarker(model);
        return "admin/web/promotion_channel_edit";
    }

    @RequiresPermissions({"root_web_promotion_channel_edit"})
    @RequestMapping("root_web_promotion_channel/edit")
    @ResponseBody
    public String editWebAgentTips()
    {
        long id = WebRequest.getLong("id");

        String staffname = WebRequest.getString("staffname");

        String name = WebRequest.getString("name");
        String contact = WebRequest.getString("contact");
        String remark = WebRequest.getString("remark");
        String url = WebRequest.getString("url");


        PromotionChannelType type = PromotionChannelType.getType(WebRequest.getString("type"));
        Status status = Status.getType(WebRequest.getString("status"));

        long viewCount = WebRequest.getLong("viewCount");
        long subscribeCount = WebRequest.getLong("subscribeCount");
        BigDecimal amount = WebRequest.getBigDecimal("amount");

        ApiJsonTemplate template = new ApiJsonTemplate();

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

        UserInfo staffInfo = mUserService.findByUsername(false, staffname);
        if(staffInfo == null)
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        if(!UserInfo.UserType.STAFF.getKey().equalsIgnoreCase(staffInfo.getType()))
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }


        if(id > 0)
        {
            promotionChannelService.updateInfo(id, name, subscribeCount, viewCount, contact, amount, status, remark);
        }
        else
        {
            UserAttr userAttr = mUserAttrService.find(false, staffInfo.getId());
            promotionChannelService.add(name, type, url, userAttr, contact, subscribeCount, viewCount, amount, status, remark);
        }
        return template.toJSONString();
    }



    @RequiresPermissions("root_web_promotion_channel_list")
    @RequestMapping("root_web_promotion_channel/delete")
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

        PromotionChannelInfo model = promotionChannelService.findById(tipsid);
        if(model == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        promotionChannelService.deleteById(tipsid);
        return apiJsonTemplate.toJSONString();

    }


}
