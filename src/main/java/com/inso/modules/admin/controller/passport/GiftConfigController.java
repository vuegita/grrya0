package com.inso.modules.admin.controller.passport;


import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.gift.model.GiftConfigInfo;
import com.inso.modules.passport.gift.model.GiftPeriodType;
import com.inso.modules.passport.gift.model.GiftTargetType;
import com.inso.modules.passport.gift.service.GiftConfigService;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;


@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class GiftConfigController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private UserQueryManager mUserQueryManager;


    @Autowired
    private GiftConfigService mGiftConfigService;

    @RequiresPermissions("root_gift_config_list")
    @RequestMapping("root_gift_config")
    public String toPage(Model model)
    {
        model.addAttribute("targetTypeArr", GiftTargetType.mArr);
        model.addAttribute("periodTypeArr", GiftPeriodType.mArr);
        return "admin/passport/user_gift_config_list";
    }

    @RequiresPermissions("root_gift_config_list")
    @RequestMapping("root_gift_config/getDataList")
    @ResponseBody
    public String getDataList()
    {
        String time = WebRequest.getString("time");

        Status status = Status.getType(WebRequest.getString("status"));
        GiftTargetType targetType = GiftTargetType.getType(WebRequest.getString("targetType"));

        ApiJsonTemplate template = new ApiJsonTemplate();
        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        pageVo.parseTime(time);


        RowPager<GiftConfigInfo> rowPager = mGiftConfigService.queryScrollPage(pageVo, targetType, status);
        template.setData(rowPager);
        return template.toJSONString();
    }

    @RequiresPermissions("root_gift_config_edit")
    @RequestMapping("root_gift_config/edit/page")
    public String toEditPassportUserVIPPage(Model model)
    {
        long id = WebRequest.getLong("id");
        if(id > 0)
        {
            GiftConfigInfo vipInfo = mGiftConfigService.findById(true, id);
            model.addAttribute("entity", vipInfo);
        }

        model.addAttribute("targetTypeArr", GiftTargetType.mArr);
        model.addAttribute("periodTypeArr", GiftPeriodType.mArr);

        return "admin/passport/user_gift_config_edit";
    }

    @RequiresPermissions("root_gift_config_edit")
    @RequestMapping("root_gift_config/edit")
    @ResponseBody
    public String editPassportUserAgentApp()
    {
        long id = WebRequest.getLong("id");

        String title = WebRequest.getString("title");
        String desc = WebRequest.getString("desc");
        BigDecimal presentAmount = WebRequest.getBigDecimal("presentAmount");
        BigDecimal limitAmount = WebRequest.getBigDecimal("limitAmount");
        long sort = WebRequest.getLong("sort");

        String presentAmountArrValue = WebRequest.getString("presentArrValue");
        Status presentAmountArrEnable = Status.getType(WebRequest.getString("presentArrEnable"));

        Status status = Status.getType(WebRequest.getString("status"));
        GiftTargetType targetType = GiftTargetType.getType(WebRequest.getString("targetType"));
        GiftPeriodType periodType = GiftPeriodType.getType(WebRequest.getString("periodType"));

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();


        if(StringUtils.isEmpty(title) || StringUtils.isEmpty(desc) || sort < 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(status == null || targetType == null || periodType == null || presentAmountArrEnable == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(targetType.isOnlyDay())
        {
            periodType = GiftPeriodType.Day;
            presentAmountArrValue = StringUtils.getEmpty();
            presentAmountArrEnable = Status.DISABLE;
        }
        else if(targetType == GiftTargetType.BET_TURNOVER)
        {
            if(StringUtils.isEmpty(presentAmountArrValue))
            {
                apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "赠送范围金额不能为空!");
                return apiJsonTemplate.toJSONString();
            }

            String[] presentAmountArr = StringUtils.split(presentAmountArrValue, ',');
            for(String tmp : presentAmountArr)
            {
                if(StringUtils.asInt(tmp) <= 0)
                {
                    apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "赠送范围金额必须为整数!");
                    return apiJsonTemplate.toJSONString();
                }
            }

            presentAmountArrEnable = Status.DISABLE;
        }

        try {
            if(id > 0)
            {
                GiftConfigInfo entity = mGiftConfigService.findById(false, id);
                if(entity == null)
                {
                    apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
                    return apiJsonTemplate.toJSONString();
                }
                mGiftConfigService.update(entity, title, desc, presentAmount, limitAmount, sort, status, presentAmountArrValue, presentAmountArrEnable);
            }
            else
            {
                mGiftConfigService.add(title, desc, targetType, periodType, presentAmount, limitAmount, sort, status, presentAmountArrValue, presentAmountArrEnable);
            }
        } catch (Exception e) {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST);
        }
        return apiJsonTemplate.toJSONString();
    }





}
