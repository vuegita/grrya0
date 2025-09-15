package com.inso.modules.web.activity.logical;

import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.*;
import com.inso.modules.passport.money.PayApiManager;
import com.inso.modules.passport.user.model.InviteFriendStatus;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.activity.model.ActivityBusinessType;
import com.inso.modules.web.activity.model.ActivityInfo;
import com.inso.modules.web.activity.model.ActivityOrderInfo;
import com.inso.modules.web.activity.service.ActivityOrderService;
import com.inso.modules.web.activity.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ActivityManager {

    private static Log LOG = LogFactory.getLog(ActivityManager.class);

    @Autowired
    private UserService mUserService;

    @Autowired
    private PayApiManager mPayApiManager;

    @Autowired
    private ActivityOrderService mActivityOrderService;

    @Autowired
    private ActivityService mActivityService;


    @Autowired
    private UserAttrService mUserAttrService;

    public void doPresent(ActivityInfo activityInfo, InviteFriendStatus rsStatusResult, UserInfo userInfo,
                          BigDecimal presentAmount, BigDecimal extraPresentAmount, BigDecimal extraPresentRate)
    {
        try {

            extraPresentAmount = BigDecimalUtils.getNotNull(extraPresentAmount);
            extraPresentRate = BigDecimalUtils.getNotNull(extraPresentRate);

            FundAccountType accountType = FundAccountType.Spot;
            ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
            UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());

            ActivityBusinessType businessType = ActivityBusinessType.getType(activityInfo.getBusinessType());

            String outradeNo = StringUtils.getEmpty() + businessType.getId() +"_" + activityInfo.getId() + userInfo.getId();

            RemarkVO remark = RemarkVO.create("web activity presentation ");

            remark.put("extraPresentAmount", extraPresentAmount);
            remark.put("extraPresentRate", extraPresentRate);
            remark.put("inviteCount", rsStatusResult.getInviteCount());
            remark.put("rechargeCount", rsStatusResult.getRechargeCount());
            remark.put("invesAmount", rsStatusResult.getHistoryTotalAmount());

            String orderno = mActivityOrderService.addOrder(activityInfo, outradeNo, userAttr, presentAmount, currencyType, remark);

            BusinessType payBusinessType = BusinessType.WEB_ACTIVITY_PRESENT_ORDER;
            ErrorResult errorResult = mPayApiManager.doPlatformPresentation(accountType, currencyType, payBusinessType, orderno, userInfo, presentAmount, null);
            if(errorResult == SystemErrorResult.SUCCESS)
            {
                mActivityOrderService.updateInfo(orderno, OrderTxStatus.REALIZED, null);
            }
        } catch (Exception e) {
            LOG.error("createOrder error:", e);
        }

    }

    public void handleOrderToRealized(ActivityOrderInfo orderInfo)
    {
        try {
            FundAccountType accountType = FundAccountType.Spot;
            ICurrencyType currencyType = ICurrencyType.getSupportCurrency();

            UserInfo userInfo = mUserService.findByUsername(false, orderInfo.getUsername());
            BigDecimal presentAmount = orderInfo.getAmount();

            String orderno = orderInfo.getNo();

            BusinessType payBusinessType = BusinessType.WEB_ACTIVITY_PRESENT_ORDER;
            ErrorResult errorResult = mPayApiManager.doPlatformPresentation(accountType, currencyType, payBusinessType, orderno, userInfo, presentAmount, null);
            if(errorResult == SystemErrorResult.SUCCESS)
            {
                mActivityOrderService.updateInfo(orderno, OrderTxStatus.REALIZED, null);
            }
        }
        catch (org.springframework.dao.DuplicateKeyException e)
        {
            LOG.error("dumplicated for " + orderInfo.getUsername());
        }
        catch (Exception e) {
            LOG.error("handleOrderToRealized error:", e);
        }
    }

    private void test2()
    {

        String username = "c_0xFA730bd82c7E8721aF28c8A0ed56Bf9041E";

        UserInfo userInfo = mUserService.findByUsername(false, username);

        if(userInfo == null)
        {
            return;
        }

        BigDecimal presentAmount = new BigDecimal(1000);
        BigDecimal extraPresentAmount = new BigDecimal(100);
        BigDecimal extraPresentRate = new BigDecimal(0.1);

        ActivityInfo activityInfo = mActivityService.findById(false, 2);

        InviteFriendStatus status = new InviteFriendStatus();
        status.setInviteCount(1000);
        status.setRechargeCount(200);
        status.setHistoryTotalAmount(new BigDecimal(2000000));

        doPresent(activityInfo, status, userInfo, presentAmount, extraPresentAmount, extraPresentRate);
    }


    public static void test()
    {
        ActivityManager mgr = SpringContextUtils.getBean(ActivityManager.class);
        mgr.test2();
    }



}
