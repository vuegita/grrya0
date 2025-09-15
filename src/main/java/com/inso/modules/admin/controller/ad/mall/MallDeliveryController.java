package com.inso.modules.admin.controller.ad.mall;


import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RegexUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.ad.core.service.MaterielService;
import com.inso.modules.ad.mall.model.MallDeliveryInfo;
import com.inso.modules.ad.mall.model.MallStoreLevel;
import com.inso.modules.ad.mall.service.MallDeliveryService;
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
public class MallDeliveryController {

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
    private MallDeliveryService mallDeliveryService;


    @RequiresPermissions("root_ad_mall_shipping_delivery_list")
    @RequestMapping("root_ad_mall_shipping_delivery")
    public String toPage(Model model)
    {
        MallStoreLevel.addFreemarker(model);
        return "admin/ad/mall/mall_delivery_list";
    }

    @RequiresPermissions("root_ad_mall_shipping_delivery_list")
    @RequestMapping("getAdMallDeliveryList")
    @ResponseBody
    public String getAdMallDeliveryList()
    {
        Status status = Status.getType(WebRequest.getString("status"));

        String orderno = WebRequest.getString("orderno");
        String trackno = WebRequest.getString("trackno");

        ApiJsonTemplate template = new ApiJsonTemplate();
        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));

        RowPager<MallDeliveryInfo> rowPager = mallDeliveryService.queryScrollPage(pageVo, orderno, status, trackno);
        template.setData(rowPager);
        return template.toJSONString();
    }

    @RequiresPermissions("root_ad_mall_shipping_delivery_edit")
    @RequestMapping("toEditAdMallDeliveryPage")
    public String toEditAdMallDeliveryPage(Model model)
    {
        long id = WebRequest.getLong("id");
        if(id > 0)
        {
            MallDeliveryInfo entity = mallDeliveryService.findById(MyEnvironment.isDev(), id);
            model.addAttribute("entity", entity);
        }
        return "admin/ad/mall/mall_delivery_edit";
    }

    @RequiresPermissions("root_ad_mall_shipping_delivery_edit")
    @RequestMapping("editAdMallDeliveryInfo")
    @ResponseBody
    public String editAdMallDeliveryInfo()
    {
        long id = WebRequest.getLong("id");

        String location = WebRequest.getString("location");
        Status status = Status.getType(WebRequest.getString("status"));

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(StringUtils.isEmpty(location) || !RegexUtils.isBankName(location) || location.length() > 250)
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
            MallDeliveryInfo entity = mallDeliveryService.findById(false, id);
            if(entity == null)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
                return apiJsonTemplate.toJSONString();
            }
            mallDeliveryService.updateInfo(entity, status, location);
        }
        return apiJsonTemplate.toJSONString();
    }



}

