package com.inso.modules.admin.controller.ad;


import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.ad.core.model.AdCategoryInfo;
import com.inso.modules.ad.core.service.CategoryService;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.money.service.UserMoneyService;
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
public class CategoryController {

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

    @RequiresPermissions("root_ad_category_list")
    @RequestMapping("root_ad_category")
    public String toPage(Model model)
    {
//        AdEventType[] adEventTypeList = AdEventType.values();
//        model.addAttribute("adEventTypeList", adEventTypeList);
        return "admin/ad/category_list";
    }

    @RequiresPermissions("root_ad_category_list")
    @RequestMapping("getAdCategoryList")
    @ResponseBody
    public String getAdCategoryList()
    {
        String statusStr = WebRequest.getString("status");
        Status status = Status.getType(statusStr);

//        String vipTypeStr = WebRequest.getString("vipType");
//        VIPType vipType = VIPType.getType(vipTypeStr);

        ApiJsonTemplate template = new ApiJsonTemplate();
        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));

        RowPager<AdCategoryInfo> rowPager = mCategoryService.queryScrollPage(pageVo, status);
        template.setData(rowPager);
        return template.toJSONString();
    }

    @RequiresPermissions("root_ad_category_edit")
    @RequestMapping("toEditAdCategoryPage")
    public String toEditRootVIPConfigPage(Model model)
    {
        long id = WebRequest.getLong("id");
        if(id > 0)
        {
            AdCategoryInfo vipInfo = mCategoryService.findById(false, id);
            model.addAttribute("entity", vipInfo);
        }

//        VIPType[] vipTypeList = VIPType.values();
//        model.addAttribute("vipTypeList", vipTypeList);
        return "admin/ad/category_edit";
    }

    @RequiresPermissions("root_ad_category_edit")
    @RequestMapping("editAdCategoryInfo")
    @ResponseBody
    public String editAdCategoryInfo()
    {
        long id = WebRequest.getLong("id");

        String statusStr = WebRequest.getString("status");
        Status status = Status.getType(statusStr);

        String name = WebRequest.getString("name");
        BigDecimal minPrice = WebRequest.getBigDecimal("minPrice");
        BigDecimal maxPrice = WebRequest.getBigDecimal("maxPrice");
        BigDecimal returnRate = WebRequest.getBigDecimal("returnRate");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(StringUtils.isEmpty(name) )//|| !RegexUtils.isLetterDigit(name)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(status == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(minPrice == null || minPrice.compareTo(BigDecimal.ZERO) <= 0 )
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(maxPrice == null || maxPrice.compareTo(BigDecimal.ZERO) <= 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(returnRate == null || returnRate.compareTo(BigDecimal.ZERO) < 0 || returnRate.compareTo(BigDecimalUtils.DEF_1) >= 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(id > 0)
        {
            AdCategoryInfo categoryInfo = mCategoryService.findById(false, id);
            if(categoryInfo == null)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
                return apiJsonTemplate.toJSONString();
            }

            mCategoryService.updateInfo(categoryInfo, status, returnRate, name, minPrice, maxPrice);
        }
        else
        {
            // tegory(String key, String name, Status status, BigDecimal beginPrice, BigDecimal endPrice);
            mCategoryService.addCategory(name, name, returnRate, status, minPrice, maxPrice);
        }
        return apiJsonTemplate.toJSONString();
    }



}
