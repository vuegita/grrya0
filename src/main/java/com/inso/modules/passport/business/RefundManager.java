package com.inso.modules.passport.business;

import java.math.BigDecimal;
import java.util.Date;

import com.inso.modules.common.model.*;
import com.inso.modules.passport.business.service.BusinessOrderService;
import com.inso.modules.passport.money.PayApiManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.modules.passport.business.model.BusinessOrder;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;

/**
 * 退款管理器
 */
@Component
public class RefundManager {

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private BusinessOrderService mBusinessOrderService;

    @Autowired
    private PayApiManager payManager;

    public boolean doRedPackageRefund(FundAccountType accountType, ICurrencyType currencyType, UserInfo userInfo, String errorBusinessOrderno, BigDecimal amount, String checker, String errmsg)
    {
        return doRefund(accountType, currencyType, BusinessType.GAME_RED_PACKAGE, userInfo, errorBusinessOrderno, amount, null, checker, errmsg);
    }

    public boolean doWithdrawRefund(FundAccountType accountType, ICurrencyType currencyType, UserInfo userInfo, String errorBusinessOrderno, BigDecimal amount, BigDecimal deductFeemoney, String checker, String errmsg)
    {
        return doRefund(accountType, currencyType, BusinessType.USER_WITHDRAW, userInfo, errorBusinessOrderno, amount, deductFeemoney, checker, errmsg);
    }

    public boolean doFMRefund(FundAccountType accountType, ICurrencyType currencyType, UserInfo userInfo, String errorBusinessOrderno, BigDecimal amount, String checker, String errmsg)
    {
        return doRefund(accountType, currencyType, BusinessType.GAME_FINANCIAL_MANAGE, userInfo, errorBusinessOrderno, amount, null, checker, errmsg);
    }


    private boolean doRefund(FundAccountType accountType, ICurrencyType currencyType, BusinessType refundBusinessType, UserInfo userInfo, String errorBusinessOrderno, BigDecimal amount, BigDecimal deductFeemoney, String checker, String errmsg)
    {
        // 加锁
        synchronized (errorBusinessOrderno)
        {
            BusinessType businessType = BusinessType.REFUND;
            BusinessOrder refundOrder = mBusinessOrderService.findByOutTradeNo(businessType, errorBusinessOrderno);

            RemarkVO remarkObj = RemarkVO.create(errmsg);
            String refundOrderNo = null;

            if(deductFeemoney == null)
            {
                deductFeemoney = BigDecimal.ZERO;
            }

            // 已退款
            if(refundOrder != null)
            {
                OrderTxStatus txStatus = OrderTxStatus.getType(refundOrder.getStatus());
                if(txStatus == OrderTxStatus.REALIZED)
                {
                    return true;
                }
                refundOrderNo = refundOrder.getNo();
            }
            else
            {
                UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());
                Date createtime = new Date();

                refundOrderNo = mBusinessOrderService.createOrder(accountType, currencyType, errorBusinessOrderno, userAttr, businessType, amount, deductFeemoney, createtime, remarkObj);
            }

            //
            ErrorResult refundResult = payManager.doRefund(accountType, currencyType, refundBusinessType, refundOrderNo, userInfo, amount, deductFeemoney, remarkObj);
            if(refundResult == SystemErrorResult.SUCCESS)
            {
                // 更新退款订单状态为成功
                mBusinessOrderService.updateTxStatus(refundOrderNo, OrderTxStatus.REALIZED, checker , null);
                return true;
            }
        }

        return false;
    }



}
