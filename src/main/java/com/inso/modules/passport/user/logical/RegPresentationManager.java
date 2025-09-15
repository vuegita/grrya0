package com.inso.modules.passport.user.logical;

import java.math.BigDecimal;
import java.util.Date;

import com.inso.modules.common.model.*;
import com.inso.modules.passport.business.service.BusinessOrderService;
import com.inso.modules.passport.money.PayApiManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.modules.admin.config.PlatformConfig;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.web.service.ConfigService;

/**
 * 注册赠送
 */
@Component
public class RegPresentationManager {

    private static Log LOG = LogFactory.getLog(RegPresentationManager.class);

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private BusinessOrderService mBusinessOrderService;

    @Autowired
    private PayApiManager payManager;

    @Autowired
    private ConfigService mConfigService;

    @Async
    public void addPresentation(UserInfo userInfo)
    {
        BigDecimal presentationAmount = mConfigService.getBigDecimal(false, PlatformConfig.ADMIN_APP_PLATFORM_USER_REGISTER_PRESENTATION_AMOUNT);
        if(presentationAmount == null || presentationAmount.floatValue() <= 0)
        {
            return;
        }

        try {
            //
            BusinessType businessType = BusinessType.REGISTER_PRESENTATION;
            FundAccountType accountType = FundAccountType.Spot;
            ICurrencyType currencyType = ICurrencyType.getSupportCurrency();

            RemarkVO remark = RemarkVO.create("Register Presentation");

            Date createtime = new Date();

            UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());

            //
            String orderno = mBusinessOrderService.createOrder(accountType, currencyType, userAttr, businessType, presentationAmount, createtime, remark);
            // 走平台赠送通道
            ErrorResult result = payManager.doPlatformPresentation(accountType, currencyType, businessType, orderno, userInfo, presentationAmount, remark);
            if(result == SystemErrorResult.SUCCESS)
            {
                mBusinessOrderService.updateTxStatus(orderno, OrderTxStatus.REALIZED, null, remark);
            }
        } catch (Exception e) {
            LOG.error("handle register presentation error:", e);
        }
    }

    @Async
    public void addPresentationParentuser(UserInfo userInfo)
    {
        BigDecimal presentationAmount = mConfigService.getBigDecimal(false, PlatformConfig.ADMIN_APP_PLATFORM_USER_REGISTER_PRESENTATION_PARENTUSER_AMOUNT);
        if(presentationAmount == null || presentationAmount.floatValue() <= 0)
        {
            return;
        }

        try {
            //
            BusinessType businessType = BusinessType.REGISTER_PRESENTATION;
            FundAccountType accountType = FundAccountType.Spot;
            ICurrencyType currencyType = ICurrencyType.getSupportCurrency();

            RemarkVO remark = RemarkVO.create("Register Presentation Parentuser");

            Date createtime = new Date();

            UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());

            //
            String orderno = mBusinessOrderService.createOrder(accountType, currencyType, userAttr, businessType, presentationAmount, createtime, remark);
            // 走平台赠送通道
            ErrorResult result = payManager.doPlatformPresentation(accountType, currencyType, businessType, orderno, userInfo, presentationAmount, remark);
            if(result == SystemErrorResult.SUCCESS)
            {
                mBusinessOrderService.updateTxStatus(orderno, OrderTxStatus.REALIZED, null, remark);
            }
        } catch (Exception e) {
            LOG.error("handle register presentation error:", e);
        }
    }



}
