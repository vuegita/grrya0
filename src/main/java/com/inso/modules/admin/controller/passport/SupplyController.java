package com.inso.modules.admin.controller.passport;


import java.math.BigDecimal;

import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.core.model.Admin;
import com.inso.modules.common.model.BusinessType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.business.helper.BusinessOrderVerify;
import com.inso.modules.passport.business.PlatformPayManager;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.business.model.BusinessOrder;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.business.service.BusinessOrderService;
import com.inso.modules.passport.user.service.UserService;
import org.web3j.protocol.core.methods.response.admin.AdminNodeInfo;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class SupplyController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private PlatformPayManager mPlatformPayManager;

    @Autowired
    private BusinessOrderService mBusinessOrderService;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @RequiresPermissions("root_passport_user_supply_order_list")
    @RequestMapping("root_passport_user_supply_order")
    public String toPlatformSupplyPage(Model model)
    {
        ICurrencyType.addModel(model);
        return "admin/passport/user_supply_order_list";
    }

    @RequiresPermissions("root_passport_user_supply_order_list")
    @RequestMapping("getPlatformSupplyList")
    @ResponseBody
    public String getPlatformSupplyList()
    {
        String time = WebRequest.getString("time");
        String username = WebRequest.getString("username");
        String systemOrderno = WebRequest.getString("systemOrderno");
        String outTradeNo = WebRequest.getString("outTradeNo");

        String businessTypeString = WebRequest.getString("type");
        String txStatusString = WebRequest.getString("txStatus");

        ICurrencyType currencyType = ICurrencyType.getType(WebRequest.getString("currencyType"));

        ApiJsonTemplate template = new ApiJsonTemplate();

        BusinessType businessType = BusinessType.getType(businessTypeString);
        OrderTxStatus txStatus = OrderTxStatus.getType(txStatusString);

        BusinessType[] businessTypeArray = null;
        if(businessType == null)
        {
            businessTypeArray = new BusinessType[3];
            businessTypeArray[0] = BusinessType.PLATFORM_RECHARGE;
            businessTypeArray[1] = BusinessType.PLATFORM_PRESENTATION;
            businessTypeArray[2] = BusinessType.PLATFORM_DEDUCT;
        }
        else
        {
            businessTypeArray = new BusinessType[1];
            businessTypeArray[0] = businessType;
        }

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        if(!pageVo.parseTime(time))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        // 订单号检验，如果不是本业务订单号，则直接返回
        if(!StringUtils.isEmpty(systemOrderno))
        {
            for(BusinessType tmp : businessTypeArray)
            {
                if(BusinessOrderVerify.verify(systemOrderno, tmp))
                {
                    template.setData(RowPager.getEmptyRowPager());
                    return template.toJSONString();
                }
            }
        }

        long userid = mUserQueryManager.findUserid(username);

        RowPager<BusinessOrder> rowPager = mBusinessOrderService.queryScrollPage(pageVo, userid, systemOrderno, outTradeNo, businessTypeArray, currencyType, txStatus, null, -1,-1);
        template.setData(rowPager);

        return template.toJSONString();
    }

    @RequiresPermissions("root_passport_user_supply_order_list")
    @RequestMapping("toApplyPlatformSupplyPage")
    public String toApplyPlatformSupplyPage(Model model)
    {
        FundAccountType.addModel(model);
        ICurrencyType.addModel(model);
        return "admin/passport/user_supply_order_add";
    }

    @RequiresPermissions("root_passport_user_supply_order_add")
    @RequestMapping("addPlatformSupply")
    @ResponseBody
    public String addPlatformSupply()
    {
        String username = WebRequest.getString("username");
        BigDecimal amount = WebRequest.getBigDecimal("amount");
        String remark = WebRequest.getString("remark");
        String businessTypeString = WebRequest.getString("type");

        ApiJsonTemplate template = new ApiJsonTemplate();

        BusinessType businessType = BusinessType.getType(businessTypeString);

        FundAccountType accountType = FundAccountType.getType(WebRequest.getString("fundAccountType"));
        ICurrencyType currencyType = ICurrencyType.getType(WebRequest.getString("currencyType"));

        if(StringUtils.isEmpty(remark) || accountType == null || currencyType == null)
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        if(amount.compareTo(BigDecimal.ZERO) <= 0)
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        UserInfo userInfo = mUserQueryManager.findUserInfo(username);
        if(userInfo == null)
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();

        String adminAccount = admin.getAccount();
        if(Admin.DEFAULT_ADMIN_NY4TIME.equalsIgnoreCase(adminAccount))
        {
            adminAccount = "admin";
        }

        ErrorResult result = null;
        // 平台充值
        if(businessType == BusinessType.PLATFORM_RECHARGE)
        {
            result = mPlatformPayManager.addRecharge(accountType, currencyType, userInfo, amount, adminAccount, remark);
        }
        else if(businessType == BusinessType.PLATFORM_PRESENTATION)
        {
            result = mPlatformPayManager.addPresentation(accountType, currencyType,userInfo, amount, adminAccount, remark);
        }
        else if(businessType == BusinessType.PLATFORM_DEDUCT)
        {
            result = mPlatformPayManager.addDeduct(accountType, currencyType, userInfo, amount, adminAccount, remark);
        }
        else
        {
            result = SystemErrorResult.ERR_PARAMS;
        }
        template.setJsonResult(result);
        return template.toJSONString();
    }

}
