package com.inso.modules.admin.controller.paychannel;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.google.GoogleUtil;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.core.model.Admin;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.common.WhiteIPManager;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.UserErrorResult;
import com.inso.modules.passport.money.UserPayManager;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.money.model.UserMoney;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.paychannel.logical.CoinChannelManager;
import com.inso.modules.paychannel.model.ChannelInfo;
import com.inso.modules.paychannel.model.ChannelType;
import com.inso.modules.paychannel.model.CoinPaymentInfo;
import com.inso.modules.paychannel.model.PayProductType;
import com.inso.modules.paychannel.service.ChannelService;
import com.inso.modules.web.service.ConfigService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.List;

/**
 * 系统出款
 */
@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class SystemPayoutCoinController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserPayManager mUserPayManager;

    @Autowired
    private ConfigService mConfigService;

    @Autowired
    private ChannelService mChannelService;

    @Autowired
    private UserMoneyService mUserMoneyService;

    @RequiresPermissions("root_pay_system_payout_coin_list")
    @RequestMapping("root_pay_system_payout_coin")
    public String toPage(Model model)
    {
        model.addAttribute("username", UserInfo.DEFAULT_SYSTEM_ACCOUNT);

        CryptoNetworkType.addFreemarkerModel(model);
        ICurrencyType.addModel(model);

        List<CoinPaymentInfo> paymentInfoList = CoinChannelManager.getInstance().getChannelAllPaymentList(ChannelType.PAYOUT);
        model.addAttribute("channelPaymentInfoList", paymentInfoList);
        return "admin/paychannel/web_system_payout_coin";
    }

    @RequiresPermissions("root_pay_system_payout_coin_list")
    @PostMapping("/submit_system_payout_coin")
    @ResponseBody
    public String actionSubmitPayout()
    {
        String username = WebRequest.getString("username");
        BigDecimal amount = WebRequest.getBigDecimal("amount");
        String accountAddress = WebRequest.getString("accountAddress");
        long channelid = WebRequest.getLong("channelid");
//        CryptoNetworkType networkType = CryptoNetworkType.getType(WebRequest.getString("networkType"));
        CryptoCurrency currencyType = CryptoCurrency.getType(WebRequest.getString("currencyType"));

        String remoteip = WebRequest.getRemoteIP();
        String googlecode = WebRequest.getString("googlecode");

        ApiJsonTemplate apiTemplate = new ApiJsonTemplate();

        if(!WhiteIPManager.getInstance().verify(remoteip))
        {
            apiTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return  apiTemplate.toJSONString();
        }

        if(StringUtils.isEmpty(username))
        {
            apiTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return  apiTemplate.toJSONString();
        }

        if(StringUtils.isEmpty(accountAddress))
        {
            apiTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return  apiTemplate.toJSONString();
        }

        if(amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
        {
            apiTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return  apiTemplate.toJSONString();
        }

        ChannelInfo channelInfo = mChannelService.findById(false, channelid);
        if(channelInfo == null || !channelInfo.getType().equalsIgnoreCase(ChannelType.PAYOUT.getKey()))
        {
            apiTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return  apiTemplate.toJSONString();
        }

        if(!channelInfo.getProductType().equalsIgnoreCase(PayProductType.COIN.getKey()))
        {
            apiTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return  apiTemplate.toJSONString();
        }

        UserInfo userInfo = mUserService.findByUsername(false, username);
        if(userInfo == null)
        {
            apiTemplate.setJsonResult(UserErrorResult.ERR_ACCOUNT_NOT_EXIST);
            return  apiTemplate.toJSONString();
        }

        // 账户被禁用
        Status status = Status.getType(userInfo.getStatus());
        if(status != Status.ENABLE)
        {
            apiTemplate.setJsonResult(SystemErrorResult.ERR_DISABLE);
            return  apiTemplate.toJSONString();
        }

        Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
        if(MyEnvironment.isProd() && !WhiteIPManager.getInstance().verify(remoteip))
        {
            String googleKey = admin.getGooglekey();
            if (StringUtils.isEmpty(googleKey)) {
                apiTemplate.setJsonResult(SystemErrorResult.ERR_VERIFY_IMAGE_CODE);
                return  apiTemplate.toJSONString();
            }

            if (!GoogleUtil.checkGoogleCode(googleKey, googlecode)) {
                apiTemplate.setJsonResult(SystemErrorResult.ERR_VERIFY_IMAGE_CODE);
                return  apiTemplate.toJSONString();
            }
        }

        CoinPaymentInfo paymentInfo = FastJsonHelper.jsonDecode(channelInfo.getSecret(), CoinPaymentInfo.class);
        if(paymentInfo == null)
        {
            apiTemplate.setJsonResult(SystemErrorResult.ERR_DISABLE);
            return  apiTemplate.toJSONString();
        }

        FundAccountType accountType = FundAccountType.Spot;
        UserMoney userMoney = mUserMoneyService.findMoney(false, userInfo.getId(), accountType, currencyType);
        if(!userMoney.verify(amount))
        {
            apiTemplate.setJsonResult(UserErrorResult.ERR_PAY_NOT_ENOUGH_BALANCE);
            return apiTemplate.toJSONString();
        }

        // submit order
        ErrorResult result = mUserPayManager.createWithdrawOrderByCoin(userInfo, channelInfo, currencyType, accountAddress, amount);

        apiTemplate.setJsonResult(result);
        return apiTemplate.toJSONString();
    }

}
