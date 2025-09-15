package com.inso.modules.passport.business.controller;

import com.google.common.collect.Maps;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.CollectionUtils;
import com.inso.modules.common.helper.RequestTokenHelper;
import com.inso.modules.common.model.*;
import com.inso.modules.passport.UserErrorResult;
import com.inso.modules.passport.business.model.PromotionOrderInfo;
import com.inso.modules.passport.business.service.PromotionPresentOrderService;
import com.inso.modules.passport.money.PayApiManager;
import com.inso.modules.passport.money.model.UserMoney;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.user.limit.MyLoginRequired;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.AuthService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.logical.WebInfoManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RequestMapping("/passport/promotionApi")
@RestController
public class PromotionApi {

    @Autowired
    private UserService mUserService;

    @Autowired
    private AuthService mAuthService;

    @Autowired
    private PromotionPresentOrderService mPromotionPresentOrderService;

    @Autowired
    private PayApiManager mPayApiManager;

    @Autowired
    private UserMoneyService moneyService;

    @Autowired
    private WebInfoManager mWebInfoManager;

    @MyLoginRequired
    @RequestMapping("getPromotionInfo")
    public String getPromotionInfo()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);

        UserInfo userInfo = mUserService.findByUsername(false, username);

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        List<PromotionOrderInfo> rsList = mPromotionPresentOrderService.queryScrollPageByUser(true, userInfo.getId());
        if(CollectionUtils.isEmpty(rsList))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_NODATA);
            return apiJsonTemplate.toJSONString();
        }

        PromotionOrderInfo data = rsList.get(0);

        Map<String, Object> maps = Maps.newHashMap();
        // 1|2级扣款比例， 扣款金额为: 赠送金额 * 比例, 比例为0表示可直接领取
        maps.put("limitStatus1", data.getLimitStatus1());
        maps.put("limitRate1", data.getLimitRate1());

        maps.put("limitStatus2", data.getLimitStatus2());
        maps.put("limitRate2", data.getLimitRate2());

        // 扣款完成赠送金额
        maps.put("amount", data.getAmount());
        maps.put("showStatus", data.getShowStatus());

        apiJsonTemplate.setData(maps);
        return apiJsonTemplate.toJSONString();
    }

    @MyLoginRequired
    @RequestMapping("updateStatus")
    public String updateStatus()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);

        UserInfo userInfo = mUserService.findByUsername(false, username);

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        List<PromotionOrderInfo> rsList = mPromotionPresentOrderService.queryScrollPageByUser(true, userInfo.getId());
        if(CollectionUtils.isEmpty(rsList))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_NODATA);
            return apiJsonTemplate.toJSONString();
        }

        PromotionOrderInfo data = rsList.get(0);

        Status status = Status.getType(data.getStatus());
        if(status == Status.ENABLE)
        {
            return apiJsonTemplate.toJSONString();
        }

        mPromotionPresentOrderService.updateTxStatus(data.getNo(), data.getUserid(), null, Status.ENABLE, null, null, null, null, null);
        return apiJsonTemplate.toJSONString();
    }

    @MyLoginRequired
    @RequestMapping("receive")
    public String receive()
    {
        // 领取
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);

        UserInfo userInfo = mUserService.findByUsername(false, username);

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(!RequestTokenHelper.verifyGame(username))
        {
            // 并发限制
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_REQUESTS);
            return apiJsonTemplate.toJSONString();
        }

        List<PromotionOrderInfo> rsList = mPromotionPresentOrderService.queryScrollPageByUser(false, userInfo.getId());
        if(CollectionUtils.isEmpty(rsList))
        {
            return apiJsonTemplate.toJSONString();
        }

        PromotionOrderInfo data = rsList.get(0);

        OrderTxStatus txStatus = OrderTxStatus.getType(data.getStatus());
        if(txStatus == OrderTxStatus.REALIZED || txStatus == OrderTxStatus.FAILED)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_HAS_FINISHED);
            return apiJsonTemplate.toJSONString();
        }

        PromotionOrderInfo.SettleMode settleStatus = PromotionOrderInfo.SettleMode.getType(data.getSettleMode());
        if(settleStatus == PromotionOrderInfo.SettleMode.Direct)
        {
            ErrorResult result = settle(data, userInfo);
            apiJsonTemplate.setJsonResult(result);
            return apiJsonTemplate.toJSONString();
        }

        boolean isFirst = true;
        ICurrencyType currencyType = ICurrencyType.getType(data.getCurrency());
        BigDecimal deductAmount = BigDecimal.ZERO;
        String orderno = data.getNo();
        OrderTxStatus limitStatus1 = OrderTxStatus.getType(data.getLimitStatus1());
        OrderTxStatus limitStatus2 = OrderTxStatus.getType(data.getLimitStatus2());

        // 扣款赠送
        if(limitStatus1 == OrderTxStatus.NEW || limitStatus1 == OrderTxStatus.WAITING)
        {
            orderno += "1";
            deductAmount = data.getLimitRate1();
        }
        else if(limitStatus2 == OrderTxStatus.NEW || limitStatus2 == OrderTxStatus.WAITING)
        {
            orderno += "2";
            deductAmount = data.getAmount().multiply(data.getLimitRate2());
            isFirst = false;
        }
        else
        {
            if(limitStatus1 == OrderTxStatus.REALIZED && limitStatus2 == OrderTxStatus.REALIZED)
            {
                ErrorResult result = settle(data, userInfo);
                apiJsonTemplate.setJsonResult(result);
            }
            return apiJsonTemplate.toJSONString();

        }

        if(deductAmount == null || deductAmount.compareTo(BigDecimal.ZERO) <= 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYSTEM);
            return apiJsonTemplate.toJSONString();
        }

        UserMoney userMoney = moneyService.findMoney(false, userInfo.getId(), FundAccountType.Spot, currencyType);
        if(!userMoney.verify(deductAmount))
        {
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_PAY_NOT_ENOUGH_BALANCE);
            return apiJsonTemplate.toJSONString();
        }

        BusinessType businessType = BusinessType.USER_PROMOTION_PRESENT;
        ErrorResult rsResult = mPayApiManager.doBusinessDeduct(FundAccountType.Spot, currencyType, businessType, orderno, userInfo, deductAmount, null, null);
        if(rsResult != SystemErrorResult.SUCCESS)
        {
            apiJsonTemplate.setJsonResult(rsResult);
            return apiJsonTemplate.toJSONString();
        }

        //
        limitStatus1 = null;
        limitStatus2 = null;
        if(isFirst)
        {
            limitStatus1 = OrderTxStatus.REALIZED;
            mPromotionPresentOrderService.updateTxStatus(data.getNo(), userInfo.getId(), null, null, null, limitStatus1, null, null, null);
            // 一级扣款完成说明
//            String desc = mWebInfoManager.getInfo(WebInfoManager.TargetType.USER_PROMOTION_PRESENT_LV1_TIPS);
//            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), desc);
            return apiJsonTemplate.toJSONString();
        }
        else
        {
            limitStatus2 = OrderTxStatus.REALIZED;
            mPromotionPresentOrderService.updateTxStatus(data.getNo(), userInfo.getId(), null, null, null, null, null, limitStatus2, null);
            ErrorResult result = settle(data, userInfo);
            apiJsonTemplate.setJsonResult(result);
        }

        return apiJsonTemplate.toJSONString();
    }

    private ErrorResult settle(PromotionOrderInfo orderInfo, UserInfo userInfo)
    {
        OrderTxStatus txStatus = OrderTxStatus.getType(orderInfo.getStatus());
        if(txStatus == OrderTxStatus.REALIZED)
        {
            return SystemErrorResult.ERR_HAS_FINISHED;
        }
        else if(txStatus == OrderTxStatus.FAILED)
        {
            return SystemErrorResult.ERR_HAS_FINISHED;
        }

        BigDecimal amount = orderInfo.getAmount();
        String orderno = orderInfo.getNo();
        ICurrencyType currencyType = ICurrencyType.getType(orderInfo.getCurrency());
        BusinessType businessType = BusinessType.USER_PROMOTION_PRESENT;
        ErrorResult rsResult = mPayApiManager.doBusinessRecharge(FundAccountType.Spot, currencyType, businessType, orderno, userInfo, amount, null);
        if(rsResult != SystemErrorResult.SUCCESS)
        {
            return rsResult;
        }
        mPromotionPresentOrderService.updateTxStatus(orderno, userInfo.getId(), OrderTxStatus.REALIZED, null, null, null, null, null, null);
        return rsResult;
    }

}
