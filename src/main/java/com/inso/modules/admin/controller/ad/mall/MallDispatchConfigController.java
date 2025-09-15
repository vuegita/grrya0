package com.inso.modules.admin.controller.ad.mall;


import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.core.service.MaterielService;
import com.inso.modules.ad.mall.model.MallDispatchConfigInfo;
import com.inso.modules.ad.mall.model.MallStoreLevel;
import com.inso.modules.ad.mall.service.MallDispatchConfigService;
import com.inso.modules.ad.mall.service.MallStoreService;
import com.inso.modules.admin.helper.AdminAccountHelper;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.user.logical.UserQueryManager;
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
public class MallDispatchConfigController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private UserMoneyService mUserMoneyService;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private MaterielService materielService;

    @Autowired
    private MallDispatchConfigService mallDispatchConfigService;

    @Autowired
    private MallStoreService mallStoreService;

    @RequiresPermissions("root_mall_dispatch_config_list")
    @RequestMapping("root_mall_dispatch_config")
    public String toPage(Model model)
    {
        MallStoreLevel.addFreemarker(model);

        if(AdminAccountHelper.isNy4timeAdminOrDEV())
        {
            model.addAttribute("isSuperAdmin", "true");
        }
        else
        {
            model.addAttribute("isSuperAdmin", "false");
        }

        return "admin/ad/mall/mall_dispatch_config_list";
    }

    @RequiresPermissions("root_mall_dispatch_config_list")
    @RequestMapping("getAdMallDispatchConfigList")
    @ResponseBody
    public String getAdMallDispatchConfigList()
    {
        ApiJsonTemplate template = new ApiJsonTemplate();
        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));

        RowPager<MallDispatchConfigInfo> rowPager = mallDispatchConfigService.queryScrollPage(pageVo);
        template.setData(rowPager);
        return template.toJSONString();
    }

    @RequiresPermissions("root_mall_dispatch_config_edit")
    @RequestMapping("toEditAdMallDispatchConfigPage")
    public String toEditAdMallDispatchConfigPage(Model model)
    {
        MallStoreLevel levelType = MallStoreLevel.getType(WebRequest.getString("id"));
        if(levelType != null)
        {
            MallDispatchConfigInfo entity = mallDispatchConfigService.findByKey(true, levelType);
            model.addAttribute("entity", entity);
        }
        MallStoreLevel.addFreemarker(model);
        return "admin/ad/mall/mall_dispatch_config_edit";
    }

    @RequiresPermissions("root_mall_dispatch_config_edit")
    @RequestMapping("editAdMallDispatchConfigInfo")
    @ResponseBody
    public String editAdMallDispatchConfigInfo()
    {
        MallStoreLevel levelType = MallStoreLevel.getType(WebRequest.getString("id"));
        Status status = Status.getType(WebRequest.getString("status"));

        long minCount = WebRequest.getLong("minCount");
        long maxCount = WebRequest.getLong("maxCount");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(levelType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(minCount < 0 || maxCount < 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        MallDispatchConfigInfo entity = mallDispatchConfigService.findByKey(true, levelType);
        if(entity == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        mallDispatchConfigService.updateInfo(entity, status, minCount, maxCount);
        return apiJsonTemplate.toJSONString();
    }


    @RequiresPermissions("root_mall_dispatch_config_edit")
    @RequestMapping("batchAdMallDispatchConfigInfo")
    @ResponseBody
    public String batchAdMallDispatchConfigInfo()
    {
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        MallStoreLevel[] values = MallStoreLevel.values();

        int minStepLen = 5;
        int maxStepLen = 10;

        int minCount = 1;
        int maxCount = 10;
        for(MallStoreLevel type : values)
        {
            try {
                mallDispatchConfigService.addCategory(type, minCount, maxCount);
                minCount = minCount + minStepLen;
                maxCount = maxCount + maxStepLen;
            } catch (Exception e) {
            }
        }
        return apiJsonTemplate.toJSONString();

    }


}
