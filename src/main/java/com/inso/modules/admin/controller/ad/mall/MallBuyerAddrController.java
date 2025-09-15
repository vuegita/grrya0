package com.inso.modules.admin.controller.ad.mall;


import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RegexUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.ad.mall.model.MallBuyerAddrInfo;
import com.inso.modules.ad.mall.model.MallStoreLevel;
import com.inso.modules.ad.mall.service.MallBuyerAddrService;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.UserErrorResult;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.user.logical.UserQueryManager;
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
public class MallBuyerAddrController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private UserMoneyService mUserMoneyService;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private MallBuyerAddrService mallBuyerAddrService;

    @RequiresPermissions("root_ad_mall_buyer_address_list")
    @RequestMapping("root_ad_mall_buyer_address")
    public String toPage(Model model)
    {
        MallStoreLevel.addFreemarker(model);
        return "admin/ad/mall/mall_buyer_addr_list";
    }

    @RequiresPermissions("root_ad_mall_buyer_address_list")
    @RequestMapping("getAdMallBuyerAddrList")
    @ResponseBody
    public String getAdMallBuyerAddrList()
    {
        Status status = Status.getType(WebRequest.getString("status"));

        String username = WebRequest.getString("username");

        long userid = mUserQueryManager.findUserid(username);

        ApiJsonTemplate template = new ApiJsonTemplate();
        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));

        RowPager<MallBuyerAddrInfo> rowPager = mallBuyerAddrService.queryScrollPage(pageVo, status, userid);
        template.setData(rowPager);
        return template.toJSONString();
    }

    @RequiresPermissions("root_ad_mall_buyer_address_edit")
    @RequestMapping("toEditAdMallBuyerAddrPage")
    public String toEditAdMallBuyerAddrPage(Model model)
    {
        long userid = WebRequest.getLong("id");
        if(userid > 0)
        {
            MallBuyerAddrInfo vipInfo = mallBuyerAddrService.findUserid(MyEnvironment.isDev(), userid);
            model.addAttribute("entity", vipInfo);
        }

        MallStoreLevel.addFreemarker(model);
        return "admin/ad/mall/mall_buyer_addr_edit";
    }

    @RequiresPermissions("root_ad_mall_buyer_address_edit")
    @RequestMapping("editAdMallBuyerAddrInfo")
    @ResponseBody
    public String editAdMallBuyerAddrInfo()
    {
        long id = WebRequest.getLong("id");

        Status status = Status.getType(WebRequest.getString("status"));

        String location = WebRequest.getString("location");

        String username = WebRequest.getString("username");
        String phone = WebRequest.getString("phone");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(StringUtils.isEmpty(location) || !RegexUtils.isBankName(location) || location.length() > 500)//|| !RegexUtils.isLetterDigit(name)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(StringUtils.isEmpty(phone) || !RegexUtils.isMobile(phone))
        {
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_PHONE);
            return apiJsonTemplate.toJSONString();
        }

        if(status == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(id > 0)
        {
            MallBuyerAddrInfo categoryInfo = mallBuyerAddrService.findUserid(false, id);
            if(categoryInfo == null)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
                return apiJsonTemplate.toJSONString();
            }

            mallBuyerAddrService.updateInfo(categoryInfo, phone, status, location);
        }
        else
        {

            UserInfo userInfo = mUserQueryManager.findUserInfo(username);
            if(userInfo == null)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
                return apiJsonTemplate.toJSONString();
            }

            UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());
            if(userType != UserInfo.UserType.MEMBER)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
                return apiJsonTemplate.toJSONString();
            }
            mallBuyerAddrService.addCategory(userInfo, location, status, phone);
        }
        return apiJsonTemplate.toJSONString();
    }



}
