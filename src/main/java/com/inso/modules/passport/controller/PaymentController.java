package com.inso.modules.passport.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.conf.MyConfiguration;
import com.inso.framework.http.HttpMediaType;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.spring.limit.MyIPRateLimit;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.*;
import com.inso.modules.ad.core.logical.WithdrawlLimitManager;
import com.inso.modules.ad.core.model.WithdrawlLimitInfo;
import com.inso.modules.admin.config.PlatformConfig;
import com.inso.modules.coin.config.CoinConfig;
import com.inso.modules.coin.core.ModifyAdddressHelper;
import com.inso.modules.coin.core.model.ApproveAuthInfo;
import com.inso.modules.coin.core.model.CoinAccountInfo;
import com.inso.modules.coin.core.model.ContractInfo;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.approve.service.ApproveAuthService;
import com.inso.modules.coin.core.service.CoinAccountService;
import com.inso.modules.coin.approve.service.ContractService;
import com.inso.modules.common.MessageManager;
import com.inso.modules.common.config.PlarformConfig2;
import com.inso.modules.common.helper.RequestTokenHelper;
import com.inso.modules.common.model.*;
import com.inso.modules.passport.UserErrorResult;
import com.inso.modules.passport.business.service.RechargeOrderService;
import com.inso.modules.passport.business.helper.RechargeActionHelper;
import com.inso.modules.passport.business.helper.TodayInviteFriendHelper;
import com.inso.modules.passport.business.model.*;
import com.inso.modules.passport.business.service.CardService;
import com.inso.modules.passport.business.service.WithdrawOrderService;
import com.inso.modules.passport.money.UserPayManager;
import com.inso.modules.passport.money.cache.UserMoneyCacheHelper;
import com.inso.modules.passport.money.model.MoneyOrder;
import com.inso.modules.passport.money.model.UserMoney;
import com.inso.modules.passport.money.service.MoneyOrderService;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.user.limit.MyLoginRequired;
import com.inso.modules.passport.user.logical.FirstRechargeManager;
import com.inso.modules.passport.user.model.*;
import com.inso.modules.passport.user.service.*;
import com.inso.modules.paychannel.ChannelErrorResult;
import com.inso.modules.paychannel.helper.PaymentErrorHelper;
import com.inso.modules.paychannel.helper.PaymentRequestHelper;
import com.inso.modules.paychannel.logical.CoinChannelManager;
import com.inso.modules.paychannel.logical.OnlinePayChannel;
import com.inso.modules.paychannel.logical.PaymentManager;
import com.inso.modules.paychannel.logical.payment.tajpay.PaymentTargetType;
import com.inso.modules.paychannel.logical.payment.tajpay.TajpayPayinHelper;
import com.inso.modules.paychannel.model.*;
import com.inso.modules.paychannel.service.ChannelService;
import com.inso.modules.risk.RiskManager;
import com.inso.modules.risk.support.WithdrawInterceptorImpl;
import com.inso.modules.web.SystemRunningMode;
import com.inso.modules.web.logical.SystemStatusManager;
import com.inso.modules.web.logical.WebInfoManager;
import com.inso.modules.web.model.ConfigKey;
import com.inso.modules.web.model.VIPType;
import com.inso.modules.web.service.ConfigService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequestMapping("/passport/payment")
@Controller
public class PaymentController {

    private static Log LOG = LogFactory.getLog(PaymentController.class);

    @Autowired
    private AuthService mAuthService;

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private UserSecretService mUserSecretService;

    @Autowired
    private UserPayManager mUserPayMgr;

    @Autowired
    private PaymentManager mPaymentManager;

    @Autowired
    private CardService mCardService;

    @Autowired
    private UserMoneyService mUserMoneyService;

    @Autowired
    private ConfigService mConfigService;

    @Autowired
    private WebInfoManager mWebInfoManager;

//    @Autowired
//    private BusinessOrderService mBusinessOrderService;

    @Autowired
    private RechargeOrderService mRechargeOrderService;

    @Autowired
    private WithdrawOrderService mWithdrawOrderService;

    @Autowired
    private MoneyOrderService moneyOrderService;

    @Autowired
    private OnlinePayChannel mOnlinePayChannel;

    @Autowired
    private ChannelService mChannelService;

    @Autowired
    private WithdrawlLimitManager mWithdrawlLimitManager;

    @Autowired
    private UserVIPService mUserVIPService;

    @Autowired
    private CoinAccountService mCoinAccountService;

    @Autowired
    private ContractService mContractService;

    @Autowired
    private ApproveAuthService mApproveAuthService;


    private FiatCurrencyType mFiatCurrencyType;


    @Autowired
    private FirstRechargeManager mFirstRechargeManager;

    public PaymentController()
    {
        MyConfiguration conf = MyConfiguration.getInstance();
        String value = conf.getString("system.support.currency");
        this.mFiatCurrencyType = FiatCurrencyType.getType(value);
    }


    /**
     * @api {post} /passport/payment/getRechargeAmountList
     * @apiDescription  获取充值金额
     * @apiName getRechargeAmountList
     * @apiGroup passport-payment-api
     * @apiVersion 1.0.0
     *
     * @apiParam {String}  accessToken
     *
     * @apiSuccess  {String}  code    错误码
     * @apiSuccess  {String}  msg   错误信息
     *
     * @apiSuccessExample {json} Success-Response:
     *       {
     *         "code": 200,
     *         "msg": "success",
     *         [
     *          {
     *              "amount":100,
     *              "rate":"0.1, 前端需格式化百分比展示",
     *              "remark":"First Recharge get extra 10",
     *          }
     *         ]
     *       }
     */
    @MyLoginRequired
    @RequestMapping("getRechargeAmountList")
    @ResponseBody
    public String getRechargeAmountList(Model model) {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);
        ApiJsonTemplate api = new ApiJsonTemplate();
        UserInfo userInfo = mUserService.findByUsername(false, username);
        List rsList = mFirstRechargeManager.getByUser(userInfo);
        api.setData(rsList);
        return api.toJSONString();
    }

    /**
     * @api {post} /pay/payment/recharge
     * @apiDescription  用户充值
     * @apiName login
     * @apiGroup passport-payment-api
     * @apiVersion 1.0.0
     *
     * @apiParam {String}  accessToken
     * @apiParam {int}  amount  ( 0 < x <= 99999)
     * @apiParam {String}  channelName
     *
     * @apiSuccess  {String}  code    错误码
     * @apiSuccess  {String}  msg   错误信息
     *
     * @apiSuccessExample {json} Success-Response:
     *       {
     *         "code": 200,
     *         "msg": "success",
     *       }
     */
    @MyIPRateLimit(maxCount = 30)
    @MyLoginRequired
    @RequestMapping("recharge")
    public String recharge(Model model)
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);
        BigDecimal amount = WebRequest.getBigDecimal("amount");
        String externalPresentKey = WebRequest.getString("externalPresentKey");
        String externalPresentType = WebRequest.getString("externalPresentType");

        String channelName = WebRequest.getString("channelName");

        ApiJsonTemplate api = new ApiJsonTemplate();
        try {
            if(!SystemStatusManager.getInstance().isRunning())
            {
               // return PaymentErrorHelper.doFailureResponse(model, true, -881, "System is being maintained! please try again later !");
                api.setJsonResult(UserErrorResult.ERR_SYSMNET);
                return api.toJSONString();
            }

            if(!RequestTokenHelper.verifyGame(username))
            {
                // 并发限制
                api.setJsonResult(SystemErrorResult.ERR_REQUESTS);
                return api.toJSONString();
            }

            UserInfo userInfo = mUserService.findByUsername(false, username);

            // 读取最小充值金额
            BigDecimal rechargeMinAmount = mConfigService.getBigDecimal(false, PlatformConfig.ADMIN_APP_PLATFORM_USER_RECHARGE_MIN_AMOUNT);
            if(amount == null || amount.compareTo(rechargeMinAmount) < 0 || amount.floatValue() >= 10000000)
            {
               // return PaymentErrorHelper.doFailureResponse(model, true, -882, "Amount params error");
                api.setJsonResult(UserErrorResult.ERR_AMOUNT_PARAMS);
                return api.toJSONString();
            }

            if(StringUtils.isEmpty(channelName))
            {
               // return PaymentErrorHelper.doFailureResponse(model, true, -883, "Channel Params error");
                api.setJsonResult(UserErrorResult.ERR_CHANNEL_PARAMS);
                return api.toJSONString();
            }

            if(!StringUtils.isEmpty(externalPresentKey) && !RegexUtils.isLetterDigit(externalPresentKey))
            {
                api.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return api.toJSONString();
            }

            ChannelInfo channelInfo = mOnlinePayChannel.getPayinChannel(channelName);
            if(channelInfo == null)
            {
                //return PaymentErrorHelper.doFailureResponse(model, true, -884, "Channel is maintained, pls try again later!");
                api.setJsonResult(UserErrorResult.ERR_CHANNEL_MAINTAINED_PARAMS);
                return api.toJSONString();
            }

            ChannelStatus channelStatus = ChannelStatus.getType(channelInfo.getStatus());
            if(channelStatus == ChannelStatus.DISABLE)
            {
                //return PaymentErrorHelper.doFailureResponse(model, true, -885, "Channel is maintained, pls try again later!");
                api.setJsonResult(UserErrorResult.ERR_CHANNEL_MAINTAINED_PARAMS);
                return api.toJSONString();
            }

            RechargePresentType presentType = RechargePresentType.getType(externalPresentType);
//            RechargePresentStatus presentStatus = RechargeActionHelper.getAmount(presentType, userInfo.getName(), externalPresentKey);

//            if(presentStatus != null && amount.compareTo(presentStatus.getLimitMixAmount()) < 0)
//            {
//                //return PaymentErrorHelper.doFailureResponse(model, true, -886, "Current recharge amount is less than the min limit amount!");
//                api.setJsonResult(UserErrorResult.ERR_RECHARGE_BELOW_MINIMUM);
//                return api.toJSONString();
//            }
            // 删除缓存，防止多次赠送
            RechargeActionHelper.deleteCache(presentType, username, externalPresentKey);


            Map<String, Object> payin_params = mPaymentManager.doRechargeAction(channelInfo, userInfo, amount, presentType, externalPresentKey);
            if(payin_params == null || payin_params.isEmpty())
            {
                return PaymentErrorHelper.doFailureResponse(model, true, SystemErrorResult.ERR_SYSTEM);
            }

            JSONObject paymentInfo = channelInfo.getSecretInfo();
            String env = paymentInfo.getString("env");

            if(channelInfo.getProduct() == PayProductType.TAJPAY)
            {
                model.addAttribute("payin_params", payin_params);
                if("prod".equalsIgnoreCase(env))
                {
                    String targetTypeStr = paymentInfo.getString("targetType");
                    PaymentTargetType targetType = PaymentTargetType.getType(targetTypeStr);
                    String actionUrl = targetType.getServer() + TajpayPayinHelper.PAYIN_CHECKOUT_ACTION_URL;
                    model.addAttribute("action", actionUrl);
                }
                else
                {
                    model.addAttribute("action", TajpayPayinHelper.PAYIN_CHECKOUT_ACTION_TEST);
                }
            }
            else
            {
                // 自己个卡
                api.setData(payin_params);
                return api.toJSONString();
            }
        } catch (Exception e) {
            LOG.error("recharge error:", e);
            return PaymentErrorHelper.doFailureResponse(model, true, SystemErrorResult.ERR_SYSTEM);
        }
        return "passport/payment-checkout-tajpay";
    }

    @MyIPRateLimit(maxCount = 30)
    @MyLoginRequired
    @RequestMapping("recharge2")
    @ResponseBody
    public String recharge2(Model model)
    {
        return recharge(model);
    }



    @MyIPRateLimit(maxCount = 30)
    @MyLoginRequired
    @RequestMapping("recharge3")
    @ResponseBody
    public String recharge3()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);
        BigDecimal amount = WebRequest.getBigDecimal("amount");
        String externalPresentKey = WebRequest.getString("externalPresentKey");
        String externalPresentType = WebRequest.getString("externalPresentType");

        String channelName = WebRequest.getString("channelName");

        ApiJsonTemplate api = new ApiJsonTemplate();
        try {
            if(!SystemStatusManager.getInstance().isRunning())
            {
                // return PaymentErrorHelper.doFailureResponse(model, true, -881, "System is being maintained! please try again later !");
                api.setJsonResult(UserErrorResult.ERR_SYSMNET);
                return api.toJSONString();
            }

            if(!RequestTokenHelper.verifyGame(username))
            {
                // 并发限制
                api.setJsonResult(SystemErrorResult.ERR_REQUESTS);
                return api.toJSONString();
            }

            UserInfo userInfo = mUserService.findByUsername(false, username);

            // 读取最小充值金额
            BigDecimal rechargeMinAmount = mConfigService.getBigDecimal(false, PlatformConfig.ADMIN_APP_PLATFORM_USER_RECHARGE_MIN_AMOUNT);
            if(amount == null || amount.compareTo(rechargeMinAmount) < 0 || amount.floatValue() >= 10000000)
            {
                // return PaymentErrorHelper.doFailureResponse(model, true, -882, "Amount params error");
                api.setJsonResult(UserErrorResult.ERR_AMOUNT_PARAMS);
                return api.toJSONString();
            }

            if(StringUtils.isEmpty(channelName))
            {
                // return PaymentErrorHelper.doFailureResponse(model, true, -883, "Channel Params error");
                api.setJsonResult(UserErrorResult.ERR_CHANNEL_PARAMS);
                return api.toJSONString();
            }

            ChannelInfo channelInfo = mOnlinePayChannel.getPayinChannel(channelName);
            if(channelInfo == null)
            {
                //return PaymentErrorHelper.doFailureResponse(model, true, -884, "Channel is maintained, pls try again later!");
                api.setJsonResult(UserErrorResult.ERR_CHANNEL_MAINTAINED_PARAMS);
                return api.toJSONString();
            }

            ChannelStatus channelStatus = ChannelStatus.getType(channelInfo.getStatus());
            if(channelStatus == ChannelStatus.DISABLE)
            {
                //return PaymentErrorHelper.doFailureResponse(model, true, -885, "Channel is maintained, pls try again later!");
                api.setJsonResult(UserErrorResult.ERR_CHANNEL_MAINTAINED_PARAMS);
                return api.toJSONString();
            }

            if(!StringUtils.isEmpty(externalPresentKey) && !RegexUtils.isLetterDigit(externalPresentKey))
            {
                api.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return api.toJSONString();
            }
            RechargePresentType presentType = RechargePresentType.getType(externalPresentType);
//            RechargePresentStatus presentStatus = RechargeActionHelper.getAmount(presentType, userInfo.getName(), externalPresentKey);
//
//            if(presentStatus != null && amount.compareTo(presentStatus.getLimitMixAmount()) < 0)
//            {
//                //return PaymentErrorHelper.doFailureResponse(model, true, -886, "Current recharge amount is less than the min limit amount!");
//                api.setJsonResult(UserErrorResult.ERR_RECHARGE_BELOW_MINIMUM);
//                return api.toJSONString();
//            }
            // 删除缓存，防止多次赠送
//            RechargeActionHelper.deleteCache(presentType, username, externalPresentKey);


            Map<String, Object> payin_params = mPaymentManager.doRechargeAction(channelInfo, userInfo, amount, presentType, externalPresentKey);
            if(payin_params == null || payin_params.isEmpty())
            {
                //return PaymentErrorHelper.doFailureResponse(model, true, SystemErrorResult.ERR_SYSTEM);
                api.setJsonResult(SystemErrorResult.ERR_SYSTEM);
                return api.toJSONString();

            }

            JSONObject paymentInfo = channelInfo.getSecretInfo();
            String env = paymentInfo.getString("env");

            if(channelInfo.getProduct() == PayProductType.TAJPAY)
            {
                Map<String, Object> modelw = Maps.newHashMap();
                modelw.put("payin_params", payin_params);

                String actionUrl = null;
                if("prod".equalsIgnoreCase(env))
                {
                    String targetTypeStr = paymentInfo.getString("targetType");
                    PaymentTargetType targetType = PaymentTargetType.getType(targetTypeStr);
                     actionUrl = targetType.getServer() + TajpayPayinHelper.PAYIN_CHECKOUT_ACTION_URL + "/s2s";
//                    modelw.put("action", actionUrl);
                }
                else
                {
                    actionUrl = TajpayPayinHelper.PAYIN_CHECKOUT_ACTION_TEST + "/s2s";
//                    modelw.put("action", );
                }


                //actionUrl = "http://127.0.0.1:8103/payment/payin/s2s";

                JSONObject jsonObject = PaymentRequestHelper.getDefaultInstance().syncPostForJSONResult(actionUrl, HttpMediaType.FORM, payin_params, null);
                if(jsonObject!=null){
                    JSONObject data = jsonObject.getJSONObject("data");
                    if(data != null && !data.isEmpty()){
                        api.setData(data);
                        return api.toJSONString();
                    }

                }


            }
            else
            {
                // 自己个卡
                api.setData(payin_params);
                return api.toJSONString();
            }
        } catch (Exception e) {
            LOG.error("recharge error:", e);
            //return PaymentErrorHelper.doFailureResponse(model, true, SystemErrorResult.ERR_SYSTEM);
            api.setJsonResult(SystemErrorResult.ERR_SYSTEM);
            return api.toJSONString();
        }

        api.setJsonResult(SystemErrorResult.ERR_SYSTEM);
        return api.toJSONString();

    }


    /**
     * @api {post} /passport/payment/withdraw
     * @apiDescription  用户提现
     * @apiName login
     * @apiGroup passport-payment-api
     * @apiVersion 1.0.0
     *
     * @apiParam {String}  accessToken
     * @apiParam {int}  amount  ( 0 < x <= 50000)
     * @apiParam {String}  cardid
     *
     * @apiSuccess  {String}  code    错误码
     * @apiSuccess  {String}  msg   错误信息
     *
     * @apiSuccessExample {json} Success-Response:
     *       {
     *         "code": 200,
     *         "msg": "success",
     *       }
     */
    @MyLoginRequired
    @RequestMapping("withdraw")
    @ResponseBody
    public String withdraw()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);
        BigDecimal amount = WebRequest.getBigDecimal("amount");
        long cardid = WebRequest.getLong("cardid");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(!SystemStatusManager.getInstance().isRunning())
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_MAINTAINED);
            return apiJsonTemplate.toJSONString();
        }

//        SystemRunningMode runningMode = SystemRunningMode.getSystemConfig();
//        if(runningMode == SystemRunningMode.FUNDS)
//        {
//            // 资金模式，周末不提现
//            DateTime dateTime = new DateTime();
//            int dayOfWeek = dateTime.getDayOfMonth();
//            if(dayOfWeek == 6 || dayOfWeek == 7)
//            {
//                // 周末，由于Google财务不上班, 提现服务暂时关闭!
//                apiJsonTemplate.setJsonResult(UserErrorResult.ERR_PAYOUT_NOT_WORKING_GOOGLE_WEEKENDS);
//                return apiJsonTemplate.toJSONString();
//            }
//        }

        // 1. basic params check
        if(cardid <= 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }
        BankCard bankCard = mCardService.findByCardid(false, cardid);
        if(bankCard == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }
        if(amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
        {
            //apiJsonTemplate.setError(-887, "Amount params error");
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_AMOUNT_PARAMS);
            return apiJsonTemplate.toJSONString();
        }
        if(mFiatCurrencyType == FiatCurrencyType.COP)
        {
            if(!bankCard.getIfsc().equalsIgnoreCase(BankCard.WALLET_COP_NEQUE))
            {
                apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "Only support Nequi !");
                return apiJsonTemplate.toJSONString();
            }
        }

        UserInfo userInfo = mUserService.findByUsername(false, username);
        if(userInfo.getId() != bankCard.getUserid())
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return apiJsonTemplate.toJSONString();
        }

        // 只能会员和推广人员才能提现
        UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());
        if(!userType.isSupportWithdraw())
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_ILEGAL);
            return apiJsonTemplate.toJSONString();
        }
        MemberSubType subType = MemberSubType.getType(userInfo.getSubType());

        // 2. check platform limit
        DateTime nowTime = new DateTime();
        List<ConfigKey> platformWithdrawConfigList = mConfigService.findByList(false, "admin_platform_config");
        Map<String, String> platformWithdrawConfigMaps = Maps.newHashMap();
        for(ConfigKey config : platformWithdrawConfigList)
        {
            platformWithdrawConfigMaps.put(config.getKey(), config.getValue());
        }
        boolean rsPlatformCheckLimit = checkPlatformWithdrawLimit(apiJsonTemplate, platformWithdrawConfigMaps, amount, nowTime, null,null);
        if(!rsPlatformCheckLimit)
        {
            return apiJsonTemplate.toJSONString();
        }
        //todo 每日提现次数
        long timesOfDay = StringUtils.asLong(platformWithdrawConfigMaps.get(PlatformConfig.ADMIN_APP_PLATFORM_USER_WITHDRAW_TIMES_OF_DAY));
        String userTimesOfDayCacheKey = UserMoneyCacheHelper.createUserWithdrawTimesOfDayLimit(username, nowTime.getDayOfYear());

        long userTimesOfDayValue = StringUtils.asLong(CacheManager.getInstance().getString(userTimesOfDayCacheKey));
        if (timesOfDay <= userTimesOfDayValue){
            //apiJsonTemplate.setError(-888, "The max withdraw times of day is " + timesOfDay);
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_MAX_WITHDRAW_TIME);
            return apiJsonTemplate.toJSONString();
        }

        //todo 每日提现最大金额
        float totalAmountLimitOfDay = StringUtils.asLong(platformWithdrawConfigMaps.get(PlatformConfig.ADMIN_APP_PLATFORM_USER_WITHDRAW_MAX_MONEY_OF_DAY));
        String userAmountLimitOfDayCacheKey = UserMoneyCacheHelper.createUserWithdrawTotalAmountOfDay(username, nowTime.getDayOfYear());
        float userAmountLimitOfDayValue = StringUtils.asFloat(CacheManager.getInstance().getString(userAmountLimitOfDayCacheKey)) + amount.floatValue();

        if(!SystemRunningMode.isCryptoMode())
        {
            if (totalAmountLimitOfDay <= userAmountLimitOfDayValue){
                //apiJsonTemplate.setError(-889, "The max withdraw amount of day is " + totalAmountLimitOfDay);
                apiJsonTemplate.setJsonResult(UserErrorResult.ERR_MAX_WITHDRAW_AMOUNT_DAY);
                return apiJsonTemplate.toJSONString();
            }
        }


        // 3. check 余额
        FundAccountType accountType = FundAccountType.Spot;
        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
        UserMoney userMoney = mUserMoneyService.findMoney(false, userInfo.getId(), accountType, currencyType);
        if(userMoney.getValidBalance().compareTo(amount) < 0)
        {
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_PAY_NOT_ENOUGH_BALANCE);
            return apiJsonTemplate.toJSONString();
        }

        // 4. check 打码量
        if(!userMoney.verifyWithdraw(amount))
        {
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_NOT_ENOUGH_CODE);
            return apiJsonTemplate.toJSONString();
        }

        // 5. 风控限制
        JSONObject riskJsonObject = new JSONObject();
        riskJsonObject.put(WithdrawInterceptorImpl.KEY_WITHDRAW_AMOUNT, amount);
        if(!RiskManager.getInstance().verifyWithdraw(userInfo, riskJsonObject))
        {
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_NOT_ENOUGH_CODE);
            return apiJsonTemplate.toJSONString();
        }

        if(SystemRunningMode.isFundsMode())
        {
            // 6. 资金提现额度
            WithdrawlLimitInfo withdrawlLimitInfo = mWithdrawlLimitManager.findByUserid(false, userInfo);
            if(!withdrawlLimitInfo.verifyWithdraw(amount))
            {

                UserVIPInfo parentVipInfo = mUserVIPService.findByUserId(false, userInfo.getId(), VIPType.AD);

                // 读取最小充值金额
                BigDecimal rechargeMinAmount = mConfigService.getBigDecimal(false, PlatformConfig.ADMIN_APP_PLATFORM_USER_RECHARGE_MIN_AMOUNT);

                if(parentVipInfo.getVipLevel()<1 && (userMoney.getTotalWithdraw().subtract(userMoney.getTotalRefund()) ).add(amount).compareTo(rechargeMinAmount)<1  ){

                }else{
                    // You still need to code 500 to withdraw cash and you can get the code by inviting friends and successfully purchase VIP!
//                BigDecimal widthdrawCodeAmount = amount.subtract(withdrawlLimitInfo.getAmount());
//                StringBuilder errmsg = new StringBuilder("You still need to code ");
//                errmsg.append(widthdrawCodeAmount);
//                errmsg.append(" to withdraw cash and you can get the code by inviting friends and successfully purchase VIP!");

                    String errmsg = "Not enough code amount, you can get code amount by inviting friends and successfully purchase VIP or upgrade you VIP Level!";

                    apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), errmsg);
                    return apiJsonTemplate.toJSONString();
                }



            }

            // 7. 提现最多只能处理一个, 处理完成才能再提交下一个
            DateTime fromTime = nowTime.minusDays(14);
            PageVo pageVo = new PageVo(0, 1);
            pageVo.setFromTime(DateUtils.convertString(fromTime.toDate(), DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS));
            pageVo.setToTime(DateUtils.convertString(nowTime.toDate(), DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS));
            List<WithdrawOrder> waitingRecordList = mWithdrawOrderService.queryScrollPageByUser(pageVo, userInfo.getId(), true);
            if(!CollectionUtils.isEmpty(waitingRecordList))
            {
                // 等待中
                apiJsonTemplate.setJsonResult(UserErrorResult.ERR_EXISTS_WITHDRAWL_ORDER);
                return apiJsonTemplate.toJSONString();
            }
        }

        // exec create withdraw order
        ErrorResult result = mUserPayMgr.createWithdrawOrder(userInfo, bankCard, amount);

        // last : update user today withdraw status
        CacheManager.getInstance().setString(userTimesOfDayCacheKey, userTimesOfDayValue + 1 + StringUtils.getEmpty(), CacheManager.EXPIRES_DAY);
        if(!SystemRunningMode.isCryptoMode())
        {
            CacheManager.getInstance().setString(userAmountLimitOfDayCacheKey, userAmountLimitOfDayValue + amount.floatValue() + StringUtils.getEmpty(), CacheManager.EXPIRES_DAY);
        }

        apiJsonTemplate.setJsonResult(result);
        return apiJsonTemplate.toJSONString();

    }

    /**
     * @api {post} /passport/payment/withdraw
     * @apiDescription  用户提现
     * @apiName login
     * @apiGroup passport-payment-api
     * @apiVersion 1.0.0
     *
     * @apiParam {String}  accessToken
     * @apiParam {int}  amount  ( 0 < x <= 50000)
     * @apiParam {int}  channelid
     * @apiParam {String}  currencyType
     *
     * @apiSuccess  {String}  code    错误码
     * @apiSuccess  {String}  msg   错误信息
     *
     * @apiSuccessExample {json} Success-Response:
     *       {
     *         "code": 200,
     *         "msg": "success",
     *       }
     */
    @MyLoginRequired
    @RequestMapping("withdrawByCoin")
    @ResponseBody
    public String withdrawByCoin()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);
        BigDecimal amount = WebRequest.getBigDecimal("amount");
        //long channelid = WebRequest.getLong("channelid");
        CryptoCurrency currencyType = CryptoCurrency.getType(WebRequest.getString("currencyType"));
        CryptoNetworkType networkType = CryptoNetworkType.getType(WebRequest.getString("networkType"));

        String address = WebRequest.getString("address");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(!SystemStatusManager.getInstance().isRunning())
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_MAINTAINED);
            return apiJsonTemplate.toJSONString();
        }

        if(!SystemRunningMode.isCryptoMode())
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return apiJsonTemplate.toJSONString();
        }

        if(!RequestTokenHelper.verifyGame(username))
        {
            // 并发限制
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_REQUESTS);
            return apiJsonTemplate.toJSONString();
        }

        if(amount == null || amount.compareTo(BigDecimal.ZERO) <= 0 || currencyType == null)
        {
            //apiJsonTemplate.setError(-887, "Amount params error");
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_ACCOUNT_DISABLE);
            return apiJsonTemplate.toJSONString();
        }

        if(StringUtils.isEmpty(address) || !RegexUtils.isLetterOrDigitOrBottomLine(address) || address.length() >= 100)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(networkType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        UserInfo userInfo = mUserService.findByUsername(false, username);
        // 测试号直接通过
        UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());
        if(!(userType == UserInfo.UserType.MEMBER || userType == UserInfo.UserType.TEST))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_ILEGAL);
            return apiJsonTemplate.toJSONString();
        }
        if(!userInfo.getStatus().equalsIgnoreCase(Status.ENABLE.getKey()))
        {
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_ACCOUNT_DISABLE);
            return apiJsonTemplate.toJSONString();
        }

        UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());
        if(userAttr.getAgentid() <= 0 || (UserInfo.DEFAULT_GAME_SYSTEM_AGENT.equalsIgnoreCase(userAttr.getAgentname()) && userType != UserInfo.UserType.TEST)  )
        {
            // 没有代理，说明当前用户非法，
            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "Current User info is error, please contact customer !");
            return apiJsonTemplate.toJSONString();
        }

        CoinAccountInfo accountInfo = mCoinAccountService.findByAddress(false, address);
        if(accountInfo.getUserid() != userInfo.getId())
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return apiJsonTemplate.toJSONString();
        }

        // 判断是否授权
        ContractInfo contractInfo = mContractService.findByNetowrkAndCurrency(false, networkType, currencyType);
        ApproveAuthInfo approveAuthInfo = mApproveAuthService.findByUseridAndContractId(false, userInfo.getId(), contractInfo.getId());
        if(approveAuthInfo == null || approveAuthInfo.getAllowance().compareTo(ApproveAuthInfo.DEFAULT_MAX_ALLOWANCE) < 0)
        {
            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "Current address not active!");
            return apiJsonTemplate.toJSONString();
        }


        List<CoinPaymentInfo> paymentList = CoinChannelManager.getInstance().getAgentPaymentInfoList(ChannelType.PAYOUT, networkType, userAttr.getAgentname());
        if(CollectionUtils.isEmpty(paymentList))
        {
            apiJsonTemplate.setJsonResult(ChannelErrorResult.ERR_CANNEL_UNSUPPORT);
            return apiJsonTemplate.toJSONString();
        }

        ChannelInfo channelInfo = mChannelService.findById(false, paymentList.get(0).getChannelid());
        if(channelInfo == null || !ChannelType.PAYOUT.getKey().equalsIgnoreCase(channelInfo.getType()))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }
        if(!channelInfo.getProductType().equalsIgnoreCase(PayProductType.COIN.getKey()))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return  apiJsonTemplate.toJSONString();
        }

        // 2. check platform limit
        DateTime nowTime = new DateTime();
        List<ConfigKey> platformWithdrawConfigList = mConfigService.findByList(false, "admin_platform_config");
        Map<String, String> platformWithdrawConfigMaps = Maps.newHashMap();
        for(ConfigKey config : platformWithdrawConfigList)
        {
            platformWithdrawConfigMaps.put(config.getKey(), config.getValue());
        }
        boolean rsPlatformCheckLimit = checkPlatformWithdrawLimit(apiJsonTemplate, platformWithdrawConfigMaps, amount, nowTime, currencyType , networkType);
        if(!rsPlatformCheckLimit)
        {
            return apiJsonTemplate.toJSONString();
        }
        //todo 每日提现次数
        long timesOfDay = StringUtils.asLong(platformWithdrawConfigMaps.get(PlatformConfig.ADMIN_APP_PLATFORM_USER_WITHDRAW_TIMES_OF_DAY));
        String userTimesOfDayCacheKey = UserMoneyCacheHelper.createUserWithdrawTimesOfDayLimit(username, nowTime.getDayOfYear());

        long userTimesOfDayValue = StringUtils.asLong(CacheManager.getInstance().getString(userTimesOfDayCacheKey));
        if (timesOfDay <= userTimesOfDayValue){
            //apiJsonTemplate.setError(-888, "The max withdraw times of day is " + timesOfDay);
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_MAX_WITHDRAW_TIME);
            return apiJsonTemplate.toJSONString();
        }

        //todo 每日提现最大金额
        float totalAmountLimitOfDay = StringUtils.asLong(platformWithdrawConfigMaps.get(PlatformConfig.ADMIN_APP_PLATFORM_USER_WITHDRAW_MAX_MONEY_OF_DAY));
        String userAmountLimitOfDayCacheKey = UserMoneyCacheHelper.createUserWithdrawTotalAmountOfDay(username, nowTime.getDayOfYear());
        float userAmountLimitOfDayValue = StringUtils.asFloat(CacheManager.getInstance().getString(userAmountLimitOfDayCacheKey)) + amount.floatValue();
        if (totalAmountLimitOfDay <= userAmountLimitOfDayValue){
            //apiJsonTemplate.setError(-889, "The max withdraw amount of day is " + totalAmountLimitOfDay);
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_MAX_WITHDRAW_AMOUNT_DAY);
            return apiJsonTemplate.toJSONString();
        }

        // 3. check 余额
        FundAccountType accountType = FundAccountType.Spot;
        UserMoney userMoney = mUserMoneyService.findMoney(true, userInfo.getId(), accountType, currencyType);

        // 4. check 打码量
        BigDecimal rsValidBalance = userMoney.getBalance().subtract(userMoney.getCodeAmount()).subtract(amount);
        if(rsValidBalance.compareTo(BigDecimal.ZERO) < 0)
        {
            //apiJsonTemplate.setError(-8810, "Not enough code amount, also need code amount " + Math.abs(rsCodeAmount.floatValue()));
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_NOT_ENOUGH_CODE);
            return apiJsonTemplate.toJSONString();
        }

        rsValidBalance = rsValidBalance.subtract(userMoney.getLimitAmount());
        if(rsValidBalance.compareTo(BigDecimal.ZERO) < 0)
        {
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_NOT_ENOUGH_CODE);
            return apiJsonTemplate.toJSONString();
        }

        if(!userMoney.verifyWithdraw(amount))
        {
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_PAY_NOT_ENOUGH_BALANCE);
            return apiJsonTemplate.toJSONString();
        }

        // 5. 风控限制
        JSONObject riskJsonObject = new JSONObject();
        riskJsonObject.put(WithdrawInterceptorImpl.KEY_WITHDRAW_AMOUNT, amount);
        if(!RiskManager.getInstance().verifyWithdraw(userInfo, riskJsonObject))
        {
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_NOT_ENOUGH_CODE);

            return apiJsonTemplate.toJSONString();
        }

        // exec create withdraw order
        ErrorResult result = mUserPayMgr.createWithdrawOrderByCoin(userInfo, accountInfo, channelInfo, currencyType, amount);

        // last : update user today withdraw status
        CacheManager.getInstance().setString(userTimesOfDayCacheKey, userTimesOfDayValue + 1 + StringUtils.getEmpty(), CacheManager.EXPIRES_DAY);
        CacheManager.getInstance().setString(userAmountLimitOfDayCacheKey, userAmountLimitOfDayValue + amount.floatValue() + StringUtils.getEmpty(), CacheManager.EXPIRES_DAY);

        apiJsonTemplate.setJsonResult(result);
        return apiJsonTemplate.toJSONString();

    }






    /**
     * @api {post} /passport/payment/withdraw
     * @apiDescription  用户提现
     * @apiName login
     * @apiGroup passport-payment-api
     * @apiVersion 1.0.0
     *
     * @apiParam {String}  accessToken
     * @apiParam {int}  amount  ( 0 < x <= 50000)
     * @apiParam {int}  channelid
     * @apiParam {String}  currencyType
     *
     * @apiSuccess  {String}  code    错误码
     * @apiSuccess  {String}  msg   错误信息
     *
     * @apiSuccessExample {json} Success-Response:
     *       {
     *         "code": 200,
     *         "msg": "success",
     *       }
     */
    @MyLoginRequired
    @RequestMapping("withdrawCoinUSDT")
    @ResponseBody
    public String withdrawCoinUSDT()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);
        BigDecimal amount = WebRequest.getBigDecimal("amount");
        String strAmount = amount.toString();
        //long channelid = WebRequest.getLong("channelid");
        CryptoCurrency currencyType = CryptoCurrency.USDT;
        CryptoNetworkType networkType = CryptoNetworkType.getType(WebRequest.getString("networkType"));

        String address = WebRequest.getString("address");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(!SystemStatusManager.getInstance().isRunning())
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_MAINTAINED);
            return apiJsonTemplate.toJSONString();
        }

//        if(!SystemRunningMode.isCryptoMode())
//        {
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
//            return apiJsonTemplate.toJSONString();
//        }

        if(!RequestTokenHelper.verifyGame(username))
        {
            // 并发限制
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_REQUESTS);
            return apiJsonTemplate.toJSONString();
        }

        if(amount == null || amount.compareTo(BigDecimal.ZERO) <= 0 || currencyType == null)
        {
            //apiJsonTemplate.setError(-887, "Amount params error");
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_ACCOUNT_DISABLE);
            return apiJsonTemplate.toJSONString();
        }

//        if(StringUtils.isEmpty(address) || !RegexUtils.isLetterOrDigitOrBottomLine(address) || address.length() >= 100)
//        {
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
//            return apiJsonTemplate.toJSONString();
//        }

        if(networkType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        UserInfo userInfo = mUserService.findByUsername(false, username);
        // 测试号直接通过
        UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());
        if(!(userType == UserInfo.UserType.MEMBER || userType == UserInfo.UserType.TEST))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_ILEGAL);
            return apiJsonTemplate.toJSONString();
        }
        if(!userInfo.getStatus().equalsIgnoreCase(Status.ENABLE.getKey()))
        {
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_ACCOUNT_DISABLE);
            return apiJsonTemplate.toJSONString();
        }

        UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());
        if(userAttr.getAgentid() <= 0  )
        //|| (UserInfo.DEFAULT_GAME_SYSTEM_AGENT.equalsIgnoreCase(userAttr.getAgentname()) && userType != UserInfo.UserType.TEST)
        {
            // 没有代理，说明当前用户非法，
            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "Current User info is error, please contact customer !");
            return apiJsonTemplate.toJSONString();
        }

      //  CoinAccountInfo accountInfo = mCoinAccountService.findByAddress(false, address);
        CoinAccountInfo accountInfo = mCoinAccountService.findByUserId(false, userInfo.getId());

        if(accountInfo.getUserid() != userInfo.getId())
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return apiJsonTemplate.toJSONString();
        }

        // 判断是否授权
//        ContractInfo contractInfo = mContractService.findByNetowrkAndCurrency(false, networkType, currencyType);
//        ApproveAuthInfo approveAuthInfo = mApproveAuthService.findByUseridAndContractId(false, userInfo.getId(), contractInfo.getId());
//        if(approveAuthInfo == null || approveAuthInfo.getAllowance().compareTo(ApproveAuthInfo.DEFAULT_MAX_ALLOWANCE) < 0)
//        {
//            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "Current address not active!");
//            return apiJsonTemplate.toJSONString();
//        }


        List<CoinPaymentInfo> paymentList = CoinChannelManager.getInstance().getAgentPaymentInfoList(ChannelType.PAYOUT, networkType, userAttr.getAgentname());
        if(CollectionUtils.isEmpty(paymentList))
        {
            apiJsonTemplate.setJsonResult(ChannelErrorResult.ERR_CANNEL_UNSUPPORT);
            return apiJsonTemplate.toJSONString();
        }

        ChannelInfo channelInfo = mChannelService.findById(false, paymentList.get(0).getChannelid());
        if(channelInfo == null || !ChannelType.PAYOUT.getKey().equalsIgnoreCase(channelInfo.getType()))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }
        if(!channelInfo.getProductType().equalsIgnoreCase(PayProductType.COIN.getKey()))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return  apiJsonTemplate.toJSONString();
        }

        // 2. check platform limit
        DateTime nowTime = new DateTime();
        List<ConfigKey> platformWithdrawConfigList = mConfigService.findByList(false, "admin_platform_config");
        Map<String, String> platformWithdrawConfigMaps = Maps.newHashMap();
        for(ConfigKey config : platformWithdrawConfigList)
        {
            platformWithdrawConfigMaps.put(config.getKey(), config.getValue());
        }
        boolean rsPlatformCheckLimit = checkPlatformWithdrawLimit(apiJsonTemplate, platformWithdrawConfigMaps, amount, nowTime, currencyType , networkType);
        if(!rsPlatformCheckLimit)
        {
            return apiJsonTemplate.toJSONString();
        }
        //todo 每日提现次数
        long timesOfDay = StringUtils.asLong(platformWithdrawConfigMaps.get(PlatformConfig.ADMIN_APP_PLATFORM_USER_WITHDRAW_TIMES_OF_DAY));
        String userTimesOfDayCacheKey = UserMoneyCacheHelper.createUserWithdrawTimesOfDayLimit(username, nowTime.getDayOfYear());

        long userTimesOfDayValue = StringUtils.asLong(CacheManager.getInstance().getString(userTimesOfDayCacheKey));
        if (timesOfDay <= userTimesOfDayValue){
            //apiJsonTemplate.setError(-888, "The max withdraw times of day is " + timesOfDay);
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_MAX_WITHDRAW_TIME);
            return apiJsonTemplate.toJSONString();
        }

        //todo 每日提现最大金额
        float totalAmountLimitOfDay = StringUtils.asLong(platformWithdrawConfigMaps.get(PlatformConfig.ADMIN_APP_PLATFORM_USER_WITHDRAW_MAX_MONEY_OF_DAY));
        String userAmountLimitOfDayCacheKey = UserMoneyCacheHelper.createUserWithdrawTotalAmountOfDay(username, nowTime.getDayOfYear());
        float userAmountLimitOfDayValue = StringUtils.asFloat(CacheManager.getInstance().getString(userAmountLimitOfDayCacheKey)) + amount.floatValue();
        if (totalAmountLimitOfDay <= userAmountLimitOfDayValue){
            //apiJsonTemplate.setError(-889, "The max withdraw amount of day is " + totalAmountLimitOfDay);
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_MAX_WITHDRAW_AMOUNT_DAY);
            return apiJsonTemplate.toJSONString();
        }

        // 3. check 余额
        FundAccountType accountType = FundAccountType.Spot;
        UserMoney userMoney = mUserMoneyService.findMoney(true, userInfo.getId(), accountType, currencyType);

        // 4. check 打码量
        BigDecimal rsValidBalance = userMoney.getBalance().subtract(userMoney.getCodeAmount()).subtract(amount);
        if(rsValidBalance.compareTo(BigDecimal.ZERO) < 0)
        {
            //apiJsonTemplate.setError(-8810, "Not enough code amount, also need code amount " + Math.abs(rsCodeAmount.floatValue()));
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_NOT_ENOUGH_CODE);
            return apiJsonTemplate.toJSONString();
        }

        rsValidBalance = rsValidBalance.subtract(userMoney.getLimitAmount());
        if(rsValidBalance.compareTo(BigDecimal.ZERO) < 0)
        {
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_NOT_ENOUGH_CODE);
            return apiJsonTemplate.toJSONString();
        }

        if(!userMoney.verifyWithdraw(amount))
        {
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_PAY_NOT_ENOUGH_BALANCE);
            return apiJsonTemplate.toJSONString();
        }

        // 5. 风控限制
//        JSONObject riskJsonObject = new JSONObject();
//        riskJsonObject.put(WithdrawInterceptorImpl.KEY_WITHDRAW_AMOUNT, amount);
//        if(!RiskManager.getInstance().verifyWithdraw(userInfo, riskJsonObject))
//        {
//            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_NOT_ENOUGH_CODE);
//
//            return apiJsonTemplate.toJSONString();
//        }

        // exec create withdraw order
        ErrorResult result = mUserPayMgr.createWithdrawOrderByCoin(userInfo, accountInfo, channelInfo, currencyType, amount);

        // last : update user today withdraw status
        CacheManager.getInstance().setString(userTimesOfDayCacheKey, userTimesOfDayValue + 1 + StringUtils.getEmpty(), CacheManager.EXPIRES_DAY);
        CacheManager.getInstance().setString(userAmountLimitOfDayCacheKey, userAmountLimitOfDayValue + amount.floatValue() + StringUtils.getEmpty(), CacheManager.EXPIRES_DAY);

        apiJsonTemplate.setJsonResult(result);


        try{
            // 获取配置
            WebInfoManager.TargetType targetType = WebInfoManager.TargetType.REGISTER_PHONE_AREA_CODE;
            String value = mWebInfoManager.getInfo(targetType);
            String[] valueArray = null;
            if(!StringUtils.isEmpty(value))
            {
                valueArray = StringUtils.split(value, '|');
            }

            MessageManager.getInstance().sendUserRWMessageTG(userInfo,strAmount,"提现",valueArray);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return apiJsonTemplate.toJSONString();

    }





    /**
     * 法币转USDT
     * @return
     */
    @MyLoginRequired
    @RequestMapping("withdrawByFiat2StableCoin")
    @ResponseBody
    public String withdrawByFiat2StableCoin()
    {
        String captcha = WebRequest.getString("captcha");
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);
        BigDecimal amount = WebRequest.getBigDecimal("amount");
        //long channelid = WebRequest.getLong("channelid");
        CryptoCurrency payCurrencyType = CryptoCurrency.USDT;

        CryptoNetworkType networkType = CryptoNetworkType.getType(WebRequest.getString("networkType"));

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(!SystemStatusManager.getInstance().isRunning())
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_MAINTAINED);
            return apiJsonTemplate.toJSONString();
        }

        if(!(SystemRunningMode.isBCMode() || SystemRunningMode.isFundsMode()))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return apiJsonTemplate.toJSONString();
        }

        if(!RequestTokenHelper.verifyGame(username))
        {
            // 并发限制
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_REQUESTS);
            return apiJsonTemplate.toJSONString();
        }

        if(amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
        {
            //apiJsonTemplate.setError(-887, "Amount params error");
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_ACCOUNT_DISABLE);
            return apiJsonTemplate.toJSONString();
        }

//        if(StringUtils.isEmpty(address) || !RegexUtils.isLetterOrDigitOrBottomLine(address) || address.length() >= 100)
//        {
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
//            return apiJsonTemplate.toJSONString();
//        }

        if(networkType == null)
        {
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "Err network parameter!");
            return apiJsonTemplate.toJSONString();
        }

        UserInfo userInfo = mUserService.findByUsername(false, username);
        // 只能会员和推广人员才能提现
        UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());
        if(!userType.isSupportWithdraw())
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_ILEGAL);
            return apiJsonTemplate.toJSONString();
        }
        if(!userInfo.getStatus().equalsIgnoreCase(Status.ENABLE.getKey()))
        {
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_ACCOUNT_DISABLE);
            return apiJsonTemplate.toJSONString();
        }

        UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());
        if(userAttr.getAgentid() <= 0)
        {
            // 没有代理，说明当前用户非法，
            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "Current User is not support!");
            return apiJsonTemplate.toJSONString();
        }

        CoinAccountInfo accountInfo = mCoinAccountService.findByUserId(false, userInfo.getId());
        if(accountInfo == null)
        {
            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "Please bind address!");
            return apiJsonTemplate.toJSONString();
        }

        // 新修改的地址，要验证
        if(ModifyAdddressHelper.exist(accountInfo.getAddress()))
        {
            DateTime modifyDateTime = new DateTime(accountInfo.getCreatetime().getTime());
            if(System.currentTimeMillis() - modifyDateTime.getMillis() < 18000_000)
            {
                apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "After the new withdrawal address is successfully modified, it must be 5 hours before withdrawal!");
                return apiJsonTemplate.toJSONString();
            }
        }

//        if(SystemRunningMode.isBCMode())
//        {
//            UserSecret secret = mUserSecretService.find(false, username);
//            if(StringUtils.isEmpty(captcha) || !secret.checkGoogle(apiJsonTemplate, captcha, true))
//            {
//                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_VERIFY_IMAGE_CODE);
//                return  apiJsonTemplate.toJSONString();
//            }
//        }

        //  是否开启提现授权
        String isShow = mConfigService.getValueByKey(false, PlatformConfig.ADMIN_PLATFORM_USER_WITHDRAW_CHECK_APPROVE_SWITCH);
        boolean isApprove =  isShow.equalsIgnoreCase("approve");

        if(isApprove){
            // 判断是否授权
            ContractInfo contractInfo = mContractService.findByNetowrkAndCurrency(false, networkType, payCurrencyType);
            ApproveAuthInfo approveAuthInfo = mApproveAuthService.findByUseridAndContractId(false, userInfo.getId(), contractInfo.getId());
            if(approveAuthInfo == null || approveAuthInfo.getAllowance().compareTo(ApproveAuthInfo.DEFAULT_MAX_ALLOWANCE) < 0)
            {
                apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "Current address not active!");
                return apiJsonTemplate.toJSONString();
            }
        }

        FundAccountType accountType = FundAccountType.Spot;
        ICurrencyType fiatCurrency = ICurrencyType.getSupportCurrency();

        ChannelInfo channelInfo = null;
        if(fiatCurrency == FiatCurrencyType.USD || fiatCurrency == FiatCurrencyType.CENT || fiatCurrency == CryptoCurrency.USDT || fiatCurrency == CryptoCurrency.USDC)
        {
            channelInfo = mOnlinePayChannel.getPayoutChannel(PayProductType.TAJPAY, fiatCurrency);
        }
        else if(fiatCurrency == FiatCurrencyType.BRL)
        {
            channelInfo = mOnlinePayChannel.getPayoutChannel(PayProductType.TAJPAY, CryptoCurrency.USDT);
        }
        else
        {
            List<CoinPaymentInfo> paymentList = CoinChannelManager.getInstance().getAgentPaymentInfoList(ChannelType.PAYOUT, networkType, userAttr.getAgentname());
            if(CollectionUtils.isEmpty(paymentList))
            {
                LOG.error("1111 config error, not fetch channel ....");
                apiJsonTemplate.setJsonResult(ChannelErrorResult.ERR_CANNEL_UNSUPPORT);
                return apiJsonTemplate.toJSONString();
            }

            channelInfo = mChannelService.findById(false, paymentList.get(0).getChannelid());
        }

        if(channelInfo == null || !ChannelType.PAYOUT.getKey().equalsIgnoreCase(channelInfo.getType()))
        {
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "Err: channel!");
            return apiJsonTemplate.toJSONString();
        }
//        if(!channelInfo.getProductType().equalsIgnoreCase(PayProductType.FIAT_2_STABLE_COIN.getKey()))//COIN
//        {
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
//            return  apiJsonTemplate.toJSONString();
//        }

        // 2. check platform limit
        DateTime nowTime = new DateTime();
        List<ConfigKey> platformWithdrawConfigList = mConfigService.findByList(false, "admin_platform_config");
        Map<String, String> platformWithdrawConfigMaps = Maps.newHashMap();
        for(ConfigKey config : platformWithdrawConfigList)
        {
            platformWithdrawConfigMaps.put(config.getKey(), config.getValue());
        }
        // 此处判断是法币
        boolean rsPlatformCheckLimit = checkPlatformWithdrawLimit(apiJsonTemplate, platformWithdrawConfigMaps, amount, nowTime, null , networkType);
        if(!rsPlatformCheckLimit)
        {
            return apiJsonTemplate.toJSONString();
        }
        //todo 每日提现次数
        long timesOfDay = StringUtils.asLong(platformWithdrawConfigMaps.get(PlatformConfig.ADMIN_APP_PLATFORM_USER_WITHDRAW_TIMES_OF_DAY));
        String userTimesOfDayCacheKey = UserMoneyCacheHelper.createUserWithdrawTimesOfDayLimit(username, nowTime.getDayOfYear());

        long userTimesOfDayValue = StringUtils.asLong(CacheManager.getInstance().getString(userTimesOfDayCacheKey));
        if (timesOfDay <= userTimesOfDayValue){
            apiJsonTemplate.setError(-888, "The max withdraw times of day is " + timesOfDay + ", and you have withdraw " + userTimesOfDayValue + " times! ");
//            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_MAX_WITHDRAW_TIME);
            return apiJsonTemplate.toJSONString();
        }

        //todo 每日提现最大金额
        float totalAmountLimitOfDay = StringUtils.asLong(platformWithdrawConfigMaps.get(PlatformConfig.ADMIN_APP_PLATFORM_USER_WITHDRAW_MAX_MONEY_OF_DAY));
        String userAmountLimitOfDayCacheKey = UserMoneyCacheHelper.createUserWithdrawTotalAmountOfDay(username, nowTime.getDayOfYear());
        float userAmountLimitOfDayValue = StringUtils.asFloat(CacheManager.getInstance().getString(userAmountLimitOfDayCacheKey)) + amount.floatValue();
        if (totalAmountLimitOfDay <= userAmountLimitOfDayValue){
            //apiJsonTemplate.setError(-889, "The max withdraw amount of day is " + totalAmountLimitOfDay);
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_MAX_WITHDRAW_AMOUNT_DAY);
            return apiJsonTemplate.toJSONString();
        }

        // 3. check 法币 余额
        UserMoney userMoney = mUserMoneyService.findMoney(true, userInfo.getId(), accountType, fiatCurrency);
        //
        if(userMoney.getValidBalance().compareTo(amount) < 0)
        {
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_PAY_NOT_ENOUGH_BALANCE);
            return apiJsonTemplate.toJSONString();
        }

        // 4. check 打码量
        if(!userMoney.verifyWithdraw(amount))
        {
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_NOT_ENOUGH_CODE);
            return apiJsonTemplate.toJSONString();
        }

        // 5. 风控限制
        JSONObject riskJsonObject = new JSONObject();
        riskJsonObject.put(WithdrawInterceptorImpl.KEY_WITHDRAW_AMOUNT, amount);
//        if(!RiskManager.getInstance().verifyWithdraw(userInfo, riskJsonObject))
//        {
//            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_NOT_ENOUGH_CODE);
//
//            return apiJsonTemplate.toJSONString();
//        }

        // exec create withdraw order
        ErrorResult result = null;
        if(SystemRunningMode.isFundsMode() && false )
        {
            // 6. 资金提现额度
            WithdrawlLimitInfo withdrawlLimitInfo = mWithdrawlLimitManager.findByUserid(false, userInfo);
            UserVIPInfo parentVipInfo = mUserVIPService.findByUserId(false, userInfo.getId(), VIPType.AD);

            // 1. vip0提现是否开启
            boolean switchValue = mConfigService.getBoolean(false, PlatformConfig.ADMIN_PLATFORM_CONFIG_AD_VIP0_WITHDRAW_SWITCH);
            if(!switchValue && parentVipInfo.getVipLevel()<1)
            {
                String errmsg = "Not enough code amount, you can get code amount by inviting friends and successfully purchase VIP or upgrade you VIP Level!!!";
                apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), errmsg);
                return apiJsonTemplate.toJSONString();
            }

            if(!withdrawlLimitInfo.verifyWithdraw(amount))
            {
                String errmsg = "Not enough code amount, you can get code amount by inviting friends and successfully purchase VIP or upgrade you VIP Level!";
                apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), errmsg);
                return apiJsonTemplate.toJSONString();
            }
            result = mUserPayMgr.createWithdrawOrderByFiatUSD2Coin(userInfo, channelInfo, networkType, payCurrencyType, accountInfo.getAddress(), amount);
        }
        else if(SystemRunningMode.isBCMode())
        {
            result = mUserPayMgr.createWithdrawOrderByFiatUSD2Coin(userInfo, channelInfo, networkType, payCurrencyType, accountInfo.getAddress(), amount);
//            if(fiatCurrency == FiatCurrencyType.CENT)
//            {
//                result = mUserPayMgr.createWithdrawOrderByCentUSD2Coin(userInfo, channelInfo, networkType, fiatCurrency, accountInfo.getAddress(), amount);
//            }
//            else
//            {
//                result = mUserPayMgr.createWithdrawOrderByCoin(userInfo, accountInfo, channelInfo, currencyType, amount);
//            }
        }
        else
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return apiJsonTemplate.toJSONString();
        }

        // last : update user today withdraw status
        CacheManager.getInstance().setString(userTimesOfDayCacheKey, userTimesOfDayValue + 1 + StringUtils.getEmpty(), CacheManager.EXPIRES_DAY);
        CacheManager.getInstance().setString(userAmountLimitOfDayCacheKey, userAmountLimitOfDayValue + amount.floatValue() + StringUtils.getEmpty(), CacheManager.EXPIRES_DAY);

        apiJsonTemplate.setJsonResult(result);
        return apiJsonTemplate.toJSONString();

    }

    private boolean checkPlatformWithdrawLimit(ApiJsonTemplate apiJsonTemplate, Map<String, String> platformWithdrawConfigMaps, BigDecimal amount, DateTime nowTime, CryptoCurrency currencyTyp, CryptoNetworkType networkType)
    {
        float amountValue = amount.floatValue();

        if(currencyTyp != null)
        {
            //单笔最大最小
            float limitMaxAmount = mConfigService.getFloat(false, CoinConfig.getWithdrawMaxAmountOfSingleKey(currencyTyp,networkType));
            if(amountValue >= limitMaxAmount)
            {
                 apiJsonTemplate.setError(-8811, "The max withdraw amount is " + limitMaxAmount);
//                apiJsonTemplate.setJsonResult(UserErrorResult.ERR_MAX_WITHDRAW_AMOUNT);
                return false;
            }

            float limitMinAmount = mConfigService.getFloat(false, CoinConfig.getWithdrawMinAmountOfSingleKey(currencyTyp,networkType));
            if(amountValue < limitMinAmount)
            {
                apiJsonTemplate.setError(-8812, "The min withdraw amount is " + limitMinAmount);
//                apiJsonTemplate.setJsonResult(UserErrorResult.ERR_MIN_WITHDRAW_AMOUNT);
                return false;
            }
        }
        else
        {
            //单笔最大最小
            float limitMaxAmount = StringUtils.asFloat(platformWithdrawConfigMaps.get(PlatformConfig.ADMIN_APP_PLATFORM_USER_WITHDRAW_MAX_MONEY_OF_SINGLE));
            if(amountValue >= limitMaxAmount)
            {
                // apiJsonTemplate.setError(-8811, "The max withdraw amount is " + limitMaxAmount);
                apiJsonTemplate.setJsonResult(UserErrorResult.ERR_MAX_WITHDRAW_AMOUNT);
                return false;
            }

            float limitMinAmount = StringUtils.asFloat(platformWithdrawConfigMaps.get(PlatformConfig.ADMIN_APP_PLATFORM_USER_WITHDRAW_MIN_MONEY_OF_SINGLE));
            if(amountValue < limitMinAmount)
            {
                //apiJsonTemplate.setError(-8812, "The min withdraw amount is " + limitMinAmount);
                apiJsonTemplate.setJsonResult(UserErrorResult.ERR_MIN_WITHDRAW_AMOUNT);
                return false;
            }
        }


        if(!SystemRunningMode.isCryptoMode())
        {
            //提现时间
            String startTimeValue = platformWithdrawConfigMaps.get(PlatformConfig.ADMIN_APP_PLATFORM_USER_WITHDRAW_START_TIME);
            String endTimeValue = platformWithdrawConfigMaps.get(PlatformConfig.ADMIN_APP_PLATFORM_USER_WITHDRAW_END_TIME);
            if (nowTime.isBefore(DateUtils.getDateTimeByHHmm(startTimeValue)) || nowTime.isAfter(DateUtils.getDateTimeByHHmm(endTimeValue))){
                // apiJsonTemplate.setError(-8813, "Current time is not service for withdraw !");
                apiJsonTemplate.setJsonResult(UserErrorResult.ERR_WITHDRAW_TIME);
                return false;
            }
        }


        return true;
    }

    /**
     * @api {post} /passport/payment/getRechargeRecord
     * @apiDescription  充值记录
     * @apiName login
     * @apiGroup passport-payment-api
     * @apiVersion 1.0.0
     *
     * @apiParam {String}  accessToken
     * @apiParam {int}  offset
         * @apiParam {String}  type  hisory | waiting
     *
     * @apiSuccess  {String}  code    错误码
     * @apiSuccess  {String}  msg   错误信息
     *
     * @apiSuccessExample {json} Success-Response:
     *       {
     *         "code": 200,
     *         "msg": "success",
     *       }
     */
    @MyLoginRequired
    @RequestMapping("getRechargeRecord")
    @ResponseBody
    public String getRechargeRecord()
    {
        String type = WebRequest.getString("type");
        if("history".equalsIgnoreCase(type))
        {
            int offset = WebRequest.getInt("offset", 0, 100);
            return getPaymentRecord(BusinessType.USER_RECHARGE, false, offset, 10);
        }
        else
        {
            return getPaymentRecord(BusinessType.USER_RECHARGE, true, 0, 100);
        }
    }

    /**
     * @api {post} /passport/payment/getWithdrawRecord
     * @apiDescription  提现记录
     * @apiName login
     * @apiGroup passport-payment-api
     * @apiVersion 1.0.0
     *
     * @apiParam {String}  accessToken
     * @apiParam {int}  offset
     * @apiParam {String}  type,   hisory | waiting
     *
     * @apiSuccess  {String}  code    错误码
     * @apiSuccess  {String}  msg   错误信息
     *
     * @apiSuccessExample {json} Success-Response:
     *       {
     *         "code": 200,
     *         "msg": "success",
     *       }
     */
    @MyLoginRequired
    @RequestMapping("getWithdrawRecord")
    @ResponseBody
    public String getWithdrawRecord()
    {
        String type = WebRequest.getString("type");
        if("history".equalsIgnoreCase(type))
        {
            int offset = WebRequest.getInt("offset", 0, 100);
            return getPaymentRecord(BusinessType.USER_WITHDRAW, false, offset, 10);
        }
        else
        {
            OrderTxStatus[] txStatusArray = {OrderTxStatus.AUDIT, OrderTxStatus.WAITING};
            return getPaymentRecord(BusinessType.USER_WITHDRAW, true, 0, 10);
        }

    }

    private String getPaymentRecord(BusinessType businessType, boolean isWaiting, int pageOffset, int pageSize)
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);
        PageVo pageVo = new PageVo(pageOffset, pageSize);

        DateTime nowTime = new DateTime();


        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        UserInfo userInfo = mUserService.findByUsername(false, username);

        // 充值
        if(businessType == BusinessType.USER_RECHARGE)
        {
            DateTime fromTime = nowTime.minusDays(3);

            pageVo.setFromTime(DateUtils.convertString(fromTime.toDate(), DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS));
            pageVo.setToTime(DateUtils.convertString(nowTime.toDate(), DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS));
            List<RechargeOrder> list = mRechargeOrderService.queryScrollPageByUser(pageVo, userInfo.getId(), isWaiting);

            if(CollectionUtils.isEmpty(list))
            {
                apiJsonTemplate.setData(list);
            }
            else
            {
                String ordernoKey = "orderno";
                String amountKey = "amount";
                String feemoneyKey = "feemoney";
                String startTimeKey = "startTime";
                String txStatusKey = "txStatus";
//                String currency = "currency";

                List rsList = new ArrayList(list.size());
                for(RechargeOrder orderInfo : list)
                {
                    String txStatus = orderInfo.getStatus();
                    if(!OrderTxStatus.REALIZED.getKey().equalsIgnoreCase(txStatus))
                    {
                        txStatus = OrderTxStatus.PENDING.getKey();
                    }
                    Map<String, Object> maps = Maps.newHashMap();

                    maps.put(ordernoKey, orderInfo.getNo());
                    maps.put(amountKey, orderInfo.getAmount());
                    maps.put(feemoneyKey, orderInfo.getFeemoney());
                    maps.put(startTimeKey, DateUtils.convertString(orderInfo.getCreatetime(), DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS));
                    maps.put(txStatusKey, txStatus);
//                    maps.put(currency, orderInfo.getCurrency());

                    rsList.add(maps);
                }

                apiJsonTemplate.setData(rsList);
            }
        }
        // 提现
        else if(businessType == BusinessType.USER_WITHDRAW)
        {
            DateTime fromTime = nowTime.minusDays(14);

            pageVo.setFromTime(DateUtils.convertString(fromTime.toDate(), DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS));
            pageVo.setToTime(DateUtils.convertString(nowTime.toDate(), DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS));
            List<WithdrawOrder> list = mWithdrawOrderService.queryScrollPageByUser(pageVo, userInfo.getId(), isWaiting);

            if(CollectionUtils.isEmpty(list))
            {
                apiJsonTemplate.setData(list);
            }
            else
            {
                String ordernoKey = "orderno";
                String amountKey = "amount";
                String feemoneyKey = "feemoney";
                String startTimeKey = "startTime";
                String txStatusKey = "txStatus";
                String currency = "currency";
                String networkType = "networkType";
                List rsList = new ArrayList(list.size());
                for(WithdrawOrder orderInfo : list)
                {
                    Map<String, Object> maps = Maps.newHashMap();

                    String txStatus = orderInfo.getStatus();
                    if(OrderTxStatus.AUDIT.getKey().equalsIgnoreCase(txStatus))
                    {
                        txStatus = OrderTxStatus.WAITING.getKey();
                    }

                    maps.put(ordernoKey, orderInfo.getNo());
                    maps.put(amountKey, orderInfo.getAmount());
                    maps.put(feemoneyKey, orderInfo.getFeemoney());
                    maps.put(startTimeKey, DateUtils.convertString(orderInfo.getCreatetime(), DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS));
                    maps.put(txStatusKey, txStatus);
                    maps.put(currency, orderInfo.getCurrency());

                    RemarkVO remarkVO =orderInfo.getRemarkVO();
                    maps.put(networkType, remarkVO.getString("ifsc"));
                    rsList.add(maps);
                }

                apiJsonTemplate.setData(rsList);
            }
        }


        return apiJsonTemplate.toJSONString();
    }

    /**
     * @api {post} /passport/payment/getWithdrawFeerate
     * @apiDescription  获取用户当前提现手续费
     * @apiName login
     * @apiGroup passport-payment-api
     * @apiVersion 1.0.0
     *
     * @apiParam {String}  accessToken
     * @apiParam {int}  offset
     *
     *
     * @apiSuccess  {String}  code    错误码
     * @apiSuccess  {String}  msg   错误信息
     *
     * @apiSuccessExample {json} Success-Response:
     *       {
     *         "code": 200,
     *         "msg": "success",
     *       }
     */
    @MyLoginRequired
    @RequestMapping("getWithdrawFeerate")
    @ResponseBody
    public String getWithdrawFeerate()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        UserInfo userInfo = mUserService.findByUsername(false, username);

        BigDecimal feerate = mConfigService.getBigDecimal(false, PlatformConfig.ADMIN_APP_PLATFORM_USER_WITHDRAW_FEERATE);
        BigDecimal userWithdrawFeerate=feerate;

        DateTime dateTime = new DateTime();
        // 成功邀请一个人少一个点，最多7个点
        int buyCount = TodayInviteFriendHelper.getTodayRegAndBuyVipCount(dateTime, userInfo.getId());
        String discountFeerateKey = PlarformConfig2.ADMIN_APP_PLATFORM_USER_WITHDRAW_MAX_DISCOUNT_FEE_RATE.getKey();
        BigDecimal discountFeerate = mConfigService.getBigDecimal(false, discountFeerateKey);

        if(SystemRunningMode.isFundsMode())
        {

            // 大于0才作用
            if(discountFeerate.compareTo(BigDecimal.ZERO) > 0)
            {

                if(buyCount >= discountFeerate.intValue())
                {
                    buyCount = discountFeerate.intValue();
                }

                feerate = feerate.subtract(new BigDecimal(buyCount));
                if(feerate.compareTo(BigDecimal.ZERO) <= 0)
                {
                    // 小于0则异常
                    return null;
                }
            }
        }

        feerate = feerate.divide(BigDecimalUtils.DEF_100);

        String userWithdrawFeerateKey="userWithdrawFeerate";
        String FeerateKey="discountFeerate";
        String feerateKey = "feerate";
        String buyVipCountKey = "buyVipCount";

        Map<String, Object> maps = Maps.newHashMap();
        maps.put(feerateKey, feerate);
        maps.put(buyVipCountKey, buyCount);
        maps.put(userWithdrawFeerateKey, userWithdrawFeerate);
        maps.put(FeerateKey, discountFeerate);

        apiJsonTemplate.setData(maps);
        return apiJsonTemplate.toJSONString();

    }


    /**
     * @api {post} /passport/payment/getTransactionList
     * @apiDescription  事务记录
     * @apiName login
     * @apiGroup passport-payment-api
     * @apiVersion 1.0.0
     *
     * @apiParam {String}  accessToken
     * @apiParam {int}  offset
     *
     *
     * @apiSuccess  {String}  code    错误码
     * @apiSuccess  {String}  msg   错误信息
     *
     * @apiSuccessExample {json} Success-Response:
     *       {
     *         "code": 200,
     *         "msg": "success",
     *       }
     */
    @MyLoginRequired
    @RequestMapping("getTransactionList")
    @ResponseBody
    public String getTransactionList()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);
        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), 10);

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        UserInfo userInfo = mUserService.findByUsername(false, username);

        List<MoneyOrder> list = moneyOrderService.queryScrollPageByUser(pageVo, userInfo.getId());

        if(CollectionUtils.isEmpty(list))
        {
            apiJsonTemplate.setData(list);
        }
        else
        {
            String ordernoKey = "orderno";
            String outTradeNoKey = "outTradeNo";
            String amountKey = "amount";
            String feemoneyKey = "feemoney";
            String startTimeKey = "startTime";
            String txStatusKey = "txStatus";
            String typeKey = "type"; // 订单类型
            List rsList = new ArrayList(list.size());
            for(MoneyOrder orderInfo : list)
            {
                Map<String, Object> maps = Maps.newHashMap();

                maps.put(ordernoKey, orderInfo.getNo());
                maps.put(outTradeNoKey, orderInfo.getOutTradeNo());
                maps.put(amountKey, orderInfo.getAmount());
                maps.put(feemoneyKey, orderInfo.getFeemoney());
                maps.put(startTimeKey, DateUtils.convertString(orderInfo.getCreatetime(), DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS));
                maps.put(txStatusKey, orderInfo.getStatus());
                maps.put(typeKey, orderInfo.getType());

                rsList.add(maps);
            }
        }
        return apiJsonTemplate.toJSONString();
    }
}
