package com.inso.modules.admin.controller.ad.mall;


import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.mall.model.MallStoreInfo;
import com.inso.modules.ad.mall.model.MallStoreLevel;
import com.inso.modules.ad.mall.model.PromotionInfo;
import com.inso.modules.ad.mall.service.MallStoreService;
import com.inso.modules.ad.mall.service.PromotionService;
import com.inso.modules.common.model.Status;
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

import java.math.BigDecimal;


@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class MallPromotionController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private UserMoneyService mUserMoneyService;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private PromotionService mPromotionService;

    @Autowired
    private MallStoreService mallStoreService;

    @RequiresPermissions("root_mall_promation_bid_list")
    @RequestMapping("root_mall_promation_bid")
    public String toPage(Model model)
    {
        MallStoreLevel.addFreemarker(model);
        return "admin/ad/mall/mall_merchant_promotion_list";
    }

    @RequiresPermissions("root_mall_promation_bid_list")
    @RequestMapping("getAdMallMerchantPromotionList")
    @ResponseBody
    public String getAdMallMerchantPromotionList()
    {
        Status status = Status.getType(WebRequest.getString("status"));

        String username = WebRequest.getString("username");
        String staffname = WebRequest.getString("staffname");
        String agentname = WebRequest.getString("agentname");

        long userid = mUserQueryManager.findUserid(username);
        long staffid = mUserQueryManager.findUserid(staffname);
        long agentid = mUserQueryManager.findUserid(agentname);

        ApiJsonTemplate template = new ApiJsonTemplate();
        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));

        RowPager<PromotionInfo> rowPager = mPromotionService.queryScrollPage(pageVo, agentid, staffid, userid, status);
        template.setData(rowPager);
        return template.toJSONString();
    }

    @RequiresPermissions("root_mall_promation_bid_edit")
    @RequestMapping("toEditAdMallMerchantPromotionPage")
    public String toEditAdMallMerchantPromotionPage(Model model)
    {
        long userid = WebRequest.getLong("id");
        if(userid > 0)
        {
            PromotionInfo entity = mPromotionService.findByUserId(MyEnvironment.isDev(), userid);
            model.addAttribute("entity", entity);
        }

        MallStoreLevel.addFreemarker(model);
        return "admin/ad/mall/mall_merchant_promotion_edit";
    }

    @RequiresPermissions("root_mall_promation_bid_edit")
    @RequestMapping("editAdMallPromotionInfo")
    @ResponseBody
    public String editAdMallPromotionInfo()
    {
        long userid = WebRequest.getLong("id");

        Status status = Status.getType(WebRequest.getString("status"));

        BigDecimal price = WebRequest.getBigDecimal("price");
        BigDecimal totalAmount = WebRequest.getBigDecimal("totalAmount");

        String username = WebRequest.getString("username");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(price == null || price.compareTo(BigDecimal.ZERO) <= 0 || totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) < 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(status == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(userid > 0)
        {
            PromotionInfo categoryInfo = mPromotionService.findByUserId(false, userid);
            if(categoryInfo == null)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
                return apiJsonTemplate.toJSONString();
            }

            mPromotionService.updateInfo(categoryInfo, price, totalAmount, status, null);
        }
        else
        {

            UserInfo userInfo = mUserQueryManager.findUserInfo(username);
            if(userInfo == null)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
                return apiJsonTemplate.toJSONString();
            }

            MallStoreInfo storeInfo = mallStoreService.findUserid(false, userInfo.getId());
            if(storeInfo == null)
            {
                apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "Pls enter merchant !");
                return apiJsonTemplate.toJSONString();
            }

            mPromotionService.addOrder(userInfo, price, totalAmount);
        }
        return apiJsonTemplate.toJSONString();
    }



}
