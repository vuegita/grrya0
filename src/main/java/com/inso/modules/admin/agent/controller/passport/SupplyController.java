package com.inso.modules.admin.agent.controller.passport;


import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.conf.MyConfiguration;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.agent.AgentAuthManager;
import com.inso.modules.admin.helper.AgentAccountHelper;
import com.inso.modules.common.config.PlarformConfig2;
import com.inso.modules.common.config.SystemConfig;
import com.inso.modules.common.model.BusinessType;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.business.PlatformPayManager;
import com.inso.modules.passport.business.helper.BusinessOrderVerify;
import com.inso.modules.passport.business.model.BusinessOrder;
import com.inso.modules.passport.business.service.BusinessOrderService;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.SystemRunningMode;
import com.inso.modules.web.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;

@Controller
@RequestMapping("/alibaba888/agent")
public class SupplyController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private PlatformPayManager mPlatformPayManager;

    @Autowired
    private BusinessOrderService mBusinessOrderService;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private AgentAuthManager mAgentAuthManager;

    @Autowired
    private ConfigService mConfigService;


    private boolean enableCustomPermission;
    private boolean enableSupplyAdd;
    private boolean enableSupplyDeduct;

    public SupplyController()
    {
        MyConfiguration conf = MyConfiguration.getInstance();
        String value = StringUtils.getNotEmpty(conf.getString("system.agent.suplly_mode"));

        this.enableCustomPermission = !StringUtils.isEmpty(value);
        this.enableSupplyAdd = value.contains("platform_recharge");
        this.enableSupplyDeduct = value.contains("deduct");
    }


    @RequestMapping("root_passport_user_supply_order")
    public String toPlatformSupplyPage(Model model)
    {
        ICurrencyType.addModel(model);
        return "admin/agent/passport/user_supply_order_list";
    }


    @RequestMapping("getPlatformSupplyList")
    @ResponseBody
    public String getPlatformSupplyList()
    {
        //已检查权限
        long agentid = AgentAccountHelper.getAdminAgentid();

        String time = WebRequest.getString("time");
        String username = WebRequest.getString("username");
        String systemOrderno = WebRequest.getString("systemOrderno");
        String outTradeNo = WebRequest.getString("outTradeNo");

        String businessTypeString = WebRequest.getString("type");
        String txStatusString = WebRequest.getString("txStatus");

        ICurrencyType currencyType = ICurrencyType.getType(WebRequest.getString("currencyType"));

        ApiJsonTemplate template = new ApiJsonTemplate();
        // 没有代理无法查询
        if(agentid <= 0)
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }


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

        // 如果是员工登陆，则员工只能查看自己下级会员的数据
        UserInfo currentLoginInfo = AgentAccountHelper.getAdminLoginInfo();
        long staffid = -1;
        if(UserInfo.UserType.STAFF.getKey().equalsIgnoreCase(currentLoginInfo.getType
                ()))
        {
            staffid = currentLoginInfo.getId();
        }
        RowPager<BusinessOrder> rowPager = mBusinessOrderService.queryScrollPage(pageVo, userid, systemOrderno, outTradeNo, businessTypeArray, currencyType, txStatus, null,agentid,staffid );
        template.setData(rowPager);

        return template.toJSONString();
    }


    @RequestMapping("toApplyPlatformSupplyPage")
    public String toApplyPlatformSupplyPage(Model model)
    {
        FundAccountType.addModel(model);
        ICurrencyType.addModel(model);
        return "admin/agent/passport/user_supply_order_add";
    }


    @RequestMapping("addPlatformSupply")
    @ResponseBody
    public String addPlatformSupply()
    {
        String username = WebRequest.getString("username");
        BigDecimal amount = WebRequest.getBigDecimal("amount");
        String remark = WebRequest.getString("remark");
        String businessTypeString = WebRequest.getString("type");

        ApiJsonTemplate template = new ApiJsonTemplate();
        // 没有代理无法查询
        if(!AgentAccountHelper.isAgentLogin())
        {
            template.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return template.toJSONString();
        }

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

        UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());
        if(!(userType == UserInfo.UserType.MEMBER || userType == UserInfo.UserType.TEST))
        {
            template.setError(-1, "只能向会员或着测试用户进行补单操作!");
            return template.toJSONString();
        }

        if(!mAgentAuthManager.verifyUserData(userInfo.getId())){
            template.setJsonResult(SystemErrorResult.ERR_SYSTEM);
            return template.toJSONString();
        }

        //Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
        // 如果是员工登陆，则员工只能查看自己下级会员的数据
        UserInfo currentLoginInfo = AgentAccountHelper.getAdminLoginInfo();
        if(currentLoginInfo == null)
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        if(!SystemRunningMode.isCryptoMode())
        {
            String agentWithdrawPermission = mConfigService.getValueByKey(false, PlarformConfig2.ADMIN_PLATFORM_USER_WITHDRAW_CHECK_AGENT_SWITCH.getKey());
            boolean enableAgentWithdrawAll = "enableAll".equalsIgnoreCase(agentWithdrawPermission);
            if(!enableAgentWithdrawAll)
            {
                template.setError(-1, "未开启代理补单权限 !!!");
                return template.toJSONString();
            }
        }

        ErrorResult result = null;
        // 平台充值
//        if(businessType == BusinessType.PLATFORM_RECHARGE)
//        {
//            if(userType == UserInfo.UserType.MEMBER)
//            {
//                result = mPlatformPayManager.addRechargeByAgentAndDeductAgentBalance(accountType, currencyType, userInfo, amount, currentLoginInfo.getName(), remark);
//                template.setJsonResult(result);
//                return template.toJSONString();
//            }
//
//            result = mPlatformPayManager.addRecharge(accountType, currencyType, userInfo, amount, currentLoginInfo.getName(), remark);
//        }
//        else
        if(businessType == BusinessType.PLATFORM_PRESENTATION)
        {
            if(enableCustomPermission)
            {
                BigDecimal limitMaxAmount = mConfigService.getBigDecimal(false, SystemConfig.PASSPORT_AGENT_SUPPLY_PRESENT_MAX_AMOUNT.getKey());
                if(limitMaxAmount != null && limitMaxAmount.compareTo(BigDecimal.ZERO) > 0 && amount.compareTo(limitMaxAmount) > 0)
                {
                    template.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "当前代理最大补单赠送金额为 = " + limitMaxAmount);
                    return template.toJSONString();
                }

                if(enableSupplyAdd)
                {
                    result = mPlatformPayManager.addPresentation(accountType, currencyType,userInfo, amount, currentLoginInfo.getName(), remark);
                }
                else
                {
                    template.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
                    return template.toJSONString();
                }

            }
            else
            {
                result = mPlatformPayManager.addPresentation(accountType, currencyType,userInfo, amount, currentLoginInfo.getName(), remark);
            }
        }
        else if(businessType == BusinessType.PLATFORM_DEDUCT)
        {
//            if(enableSupplyAdd && !enableSupplyDeduct)
//            {
//                template.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
//                return template.toJSONString();
//            }
            result = mPlatformPayManager.addDeduct(accountType, currencyType, userInfo, amount, currentLoginInfo.getName(), remark);
        }
        else
        {
            result = SystemErrorResult.ERR_SYS_OPT_FORBID;
        }
        template.setJsonResult(result);
        return template.toJSONString();
    }

}
