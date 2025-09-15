package com.inso.modules.passport.business;

import java.math.BigDecimal;
import java.util.Date;

import com.inso.framework.utils.UUIDUtils;
import com.inso.modules.admin.helper.AgentAccountHelper;
import com.inso.modules.common.model.*;
import com.inso.modules.passport.UserErrorResult;
import com.inso.modules.passport.business.service.BusinessOrderService;
import com.inso.modules.passport.money.PayApiManager;
import com.inso.modules.passport.money.model.UserMoney;
import com.inso.modules.passport.money.service.UserMoneyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;

@Component
public class PlatformPayManager {

    private static Log LOG = LogFactory.getLog(PlatformPayManager.class);

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private PayApiManager payManager;

    @Autowired
    private BusinessOrderService mBusinessOrderService;

    @Autowired
    private UserMoneyService userMoneyService;

    public ErrorResult addRechargeByAgentAndDeductAgentBalance(FundAccountType accountType, ICurrencyType currencyType, UserInfo userInfo, BigDecimal amount, String checker, String remark)
    {
        UserInfo agentInfo = AgentAccountHelper.getAgentInfo();
        return addRechargeByAgentAndDeductAgentBalanceFrom(accountType, currencyType, agentInfo, userInfo, amount, checker, remark);
    }

    public ErrorResult addRechargeByAgentAndDeductAgentBalanceFrom(FundAccountType accountType, ICurrencyType currencyType,UserInfo agentInfo, UserInfo userInfo, BigDecimal amount, String checker, String remark)
    {
        String deductRemark = StringUtils.getEmpty();
        ErrorResult result = null;
        UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());
        if(userType == UserInfo.UserType.MEMBER)
        {
            UserMoney agentMoney = userMoneyService.findMoney(false, agentInfo.getId(), accountType, currencyType);
            if(!agentMoney.verify(amount))
            {
                return UserErrorResult.ERR_PAY_NOT_ENOUGH_BALANCE;
            }

            deductRemark = "System Info: deduct id = " + UUIDUtils.getUUID() + ", and will recharge for  " + userInfo.getName();
            result = addDeduct(accountType, currencyType, agentInfo, amount, checker, deductRemark);
            if(result != SystemErrorResult.SUCCESS)
            {
                return result;
            }

            if(!StringUtils.isEmpty(remark))
            {
                deductRemark = deductRemark + " | " + remark;
            }
        }
        else
        {
            result = SystemErrorResult.SUCCESS;
        }
        result = addPresentation(accountType, currencyType, userInfo, amount,  checker, deductRemark);
        return result;
    }


    public ErrorResult addRecharge(FundAccountType accountType, ICurrencyType currencyType, UserInfo userInfo, BigDecimal amount, String checker, String remark)
    {
        String orderno = null;
        try {
            Date createtime = new Date();

            BusinessType businessType = BusinessType.PLATFORM_RECHARGE;

            RemarkVO remarkObj = getRemark(remark);

            UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());
            UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());
            if(userType == UserInfo.UserType.AGENT)
            {
                userAttr.setAgentname(userInfo.getName());
                userAttr.setAgentid(userInfo.getId());
            }
            else if(userType == UserInfo.UserType.STAFF)
            {
                userAttr.setDirectStaffname(userInfo.getName());
                userAttr.setDirectStaffid(userInfo.getId());
            }

            // 创建订单
            orderno = mBusinessOrderService.createOrder(accountType, currencyType, userAttr, businessType, amount, createtime, remarkObj);

            // 支付订单
            ErrorResult result = payManager.doPlatformRecharge(accountType, currencyType, businessType, orderno, userInfo, amount, null);

            if(result == SystemErrorResult.SUCCESS)
            {
                // 修改状态
                mBusinessOrderService.updateTxStatus(orderno, OrderTxStatus.REALIZED, checker, null);
            }
            else
            {
                // 修改状态
                mBusinessOrderService.updateTxStatus(orderno, OrderTxStatus.FAILED, checker, null);
            }
            return result;
        } catch (Exception e) {
            if(!StringUtils.isEmpty(orderno))
            {
                mBusinessOrderService.updateTxStatus(orderno, OrderTxStatus.FAILED, checker, null);
            }
            LOG.error("add recharge error:", e);

        }

        return SystemErrorResult.ERR_SYS_OPT_FAILURE;
    }

    public ErrorResult addPresentation(FundAccountType accountType, ICurrencyType currencyType, UserInfo userInfo, BigDecimal amount, String checker, String remark)
    {
        BusinessType businessType = BusinessType.PLATFORM_PRESENTATION;
        return addPresentation(accountType, currencyType, userInfo, amount, checker, remark, businessType);
    }


    public ErrorResult addPresentation(FundAccountType accountType, ICurrencyType currencyType, UserInfo userInfo, BigDecimal amount, String checker, String remark, BusinessType businessType)
    {
        String orderno = null;
        try {
            Date createtime = new Date();

            RemarkVO remarkObj = getRemark(remark);

            UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());

            UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());
            if(userType == UserInfo.UserType.AGENT)
            {
                userAttr.setAgentname(userInfo.getName());
                userAttr.setAgentid(userInfo.getId());
            }
            else if(userType == UserInfo.UserType.STAFF)
            {
                userAttr.setDirectStaffname(userInfo.getName());
                userAttr.setDirectStaffid(userInfo.getId());
            }

            // 创建订单
            orderno = mBusinessOrderService.createOrder(accountType, currencyType, userAttr, businessType, amount, createtime, remarkObj);

            // 支付订单
            ErrorResult result = payManager.doPlatformPresentation(accountType, currencyType, businessType, orderno, userInfo, amount, null);

            if(result == SystemErrorResult.SUCCESS)
            {
                // 修改状态
                mBusinessOrderService.updateTxStatus(orderno, OrderTxStatus.REALIZED, checker, null);
            }
            else
            {
                // 修改状态
                mBusinessOrderService.updateTxStatus(orderno, OrderTxStatus.FAILED, checker, null);
            }
            return result;
        } catch (Exception e) {
            if(!StringUtils.isEmpty(orderno))
            {
                mBusinessOrderService.updateTxStatus(orderno, OrderTxStatus.FAILED, checker, null);
            }
            LOG.error("add recharge error:", e);

        }

        return SystemErrorResult.ERR_SYS_OPT_FAILURE;
    }



    public ErrorResult addDeduct(FundAccountType accountType, ICurrencyType currencyType, UserInfo userInfo, BigDecimal amount, String checker, String remark)
    {
        String orderno = null;
        try {
            Date createtime = new Date();

            BusinessType businessType = BusinessType.PLATFORM_DEDUCT;

            RemarkVO remarkObj = getRemark(remark);

            UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());

            UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());
            if(userType == UserInfo.UserType.AGENT)
            {
                userAttr.setAgentname(userInfo.getName());
                userAttr.setAgentid(userInfo.getId());
            }
            else if(userType == UserInfo.UserType.STAFF)
            {
                userAttr.setDirectStaffname(userInfo.getName());
                userAttr.setDirectStaffid(userInfo.getId());
            }

            // 创建订单
            orderno = mBusinessOrderService.createOrder(accountType, currencyType, userAttr, businessType, amount, createtime, remarkObj);

            // 支付订单
            ErrorResult errorResult = payManager.doPlatformDeduct(accountType, currencyType, businessType, orderno, userInfo, amount, null);

            if(errorResult == SystemErrorResult.SUCCESS)
            {
                // 修改状态
                mBusinessOrderService.updateTxStatus(orderno, OrderTxStatus.REALIZED, checker, null);
            }
            else
            {
                // 修改状态
                mBusinessOrderService.updateTxStatus(orderno, OrderTxStatus.FAILED, checker, null);
            }

            return errorResult;
        } catch (Exception e) {
            if(!StringUtils.isEmpty(orderno))
            {
                mBusinessOrderService.updateTxStatus(orderno, OrderTxStatus.FAILED, checker, null);
            }
            LOG.error("add deduct error:", e);
        }
        return SystemErrorResult.ERR_SYS_OPT_FAILURE;
    }


    private RemarkVO getRemark(String errmsg)
    {
        return RemarkVO.create(errmsg);
    }
}
