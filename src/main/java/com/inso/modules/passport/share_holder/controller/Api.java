package com.inso.modules.passport.share_holder.controller;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.UserErrorResult;
import com.inso.modules.passport.business.PlatformPayManager;
import com.inso.modules.passport.money.model.UserMoney;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.returnwater.model.ReturnWaterLog;
import com.inso.modules.passport.returnwater.service.ReturnWaterLogCountService;
import com.inso.modules.passport.share_holder.config.ShareHolderConfig;
import com.inso.modules.passport.share_holder.model.ShareHolderInfo;
import com.inso.modules.passport.share_holder.service.ShareHolderService;
import com.inso.modules.passport.user.limit.MyLoginRequired;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.AuthService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.SystemRunningMode;
import com.inso.modules.web.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/passport/shareHolderApi")
public class Api {

    @Autowired
    private UserService mUserService;

    @Autowired
    private AuthService mOauth2Service;

    @Autowired
    private ConfigService mConfigService;

    @Autowired
    private ShareHolderService mShareHolderService;

    @Autowired
    private ReturnWaterLogCountService mReturnWaterLogCountService;

    @Autowired
    private PlatformPayManager mPlatformManager;

    @Autowired
    private UserMoneyService moneyService;


    @MyLoginRequired
    @RequestMapping("/doApply")
    public String doApply()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mOauth2Service.getAccountByAccessToken(accessToken);
        long basicAmount = WebRequest.getLong("amount");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();



        UserInfo userInfo = mUserService.findByUsername(false, username);
        if(userInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }


        if(SystemRunningMode.isFundsMode())
        {
            FundAccountType accountType = FundAccountType.Spot;
            ICurrencyType currencyType = ICurrencyType.getSupportCurrency();

            BigDecimal mixAmount = new BigDecimal(5000);
            BigDecimal amount = new BigDecimal(basicAmount);
            if(amount.compareTo(mixAmount) < 0){
                apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "The minimum participation amount cannot be less than 5000");
                return apiJsonTemplate.toJSONString();
            }


            UserMoney userMoney = moneyService.findMoney(false, userInfo.getId(), accountType, currencyType);
            if(!userMoney.verify(amount))
            {
                apiJsonTemplate.setJsonResult(UserErrorResult.ERR_PAY_NOT_ENOUGH_BALANCE);
                return apiJsonTemplate.toJSONString();
            }

            String remark = "Deduct for join share holder!";
            ErrorResult errorResult = mPlatformManager.addDeduct(accountType, currencyType, userInfo, amount, null, remark);
            if(errorResult == SystemErrorResult.SUCCESS)
            {
                long remainingCount =  mConfigService.getLong(false,ShareHolderConfig.REMAINING_COUNT.getKey());
                if(remainingCount>=1){
                    remainingCount--;
                }
                String key = ShareHolderConfig.REMAINING_COUNT.getSubkey();
                mConfigService.updateValue("passport_share_holder:" + key, remainingCount + StringUtils.getEmpty());



                mShareHolderService.add(userInfo, Status.DISABLE, Status.DISABLE, Status.ENABLE);
            }

            apiJsonTemplate.setJsonResult(errorResult);
        }
        else
        {
            ReturnWaterLog returnWaterLog = mReturnWaterLogCountService.findByUserid(false, userInfo.getId());
            if(returnWaterLog == null)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
                return apiJsonTemplate.toJSONString();
            }

            long count = mConfigService.getLong(false, ShareHolderConfig.LV1_LIMIT_MIN_INVITE_COUNT.getKey());
            if(returnWaterLog.getLevel1Count() < count)
            {
                apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "Limit min invite friend count is " + count + ", please contract us if you have any question!");
                return apiJsonTemplate.toJSONString();
            }

            ShareHolderInfo shareHolderInfo = mShareHolderService.findByUserId(false, userInfo.getId());
            if(shareHolderInfo != null)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST);
                return apiJsonTemplate.toJSONString();
            }

            mShareHolderService.add(userInfo, Status.DISABLE, Status.DISABLE, Status.APPLY);
        }

        return apiJsonTemplate.toJSONString();
    }

    @MyLoginRequired
    @RequestMapping("/getShareHolderInfo")
    public String getShareHolderInfo()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mOauth2Service.getAccountByAccessToken(accessToken);

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        UserInfo userInfo = mUserService.findByUsername(false, username);
        if(userInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        ShareHolderInfo shareHolderInfo = mShareHolderService.findByUserId(false, userInfo.getId());
        if(shareHolderInfo == null)
        {
           // apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        apiJsonTemplate.setData(shareHolderInfo);
        return apiJsonTemplate.toJSONString();
    }

}
