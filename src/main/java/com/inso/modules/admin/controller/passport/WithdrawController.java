package com.inso.modules.admin.controller.passport;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.*;
import com.inso.modules.ad.core.logical.WithdrawlLimitManager;
import com.inso.modules.ad.core.model.WithdrawlLimitInfo;
import com.inso.modules.admin.config.PlatformConfig;
import com.inso.modules.admin.core.model.AdminSecret;
import com.inso.modules.admin.core.service.AdminService;
import com.inso.modules.admin.helper.AdminAccountHelper;
import com.inso.modules.coin.core.model.CoinAccountInfo;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.core.service.CoinAccountService;
import com.inso.modules.common.WhiteIPManager;
import com.inso.modules.common.config.SystemConfig;
import com.inso.modules.common.model.*;
import com.inso.modules.passport.UserErrorResult;
import com.inso.modules.passport.business.WithdrawAuth;
import com.inso.modules.passport.business.model.BankCard;
import com.inso.modules.passport.business.model.UserWithdrawVO;
import com.inso.modules.passport.business.service.CardService;
import com.inso.modules.passport.money.cache.UserMoneyCacheHelper;
import com.inso.modules.passport.money.model.UserMoney;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.user.model.MemberSubType;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserVIPInfo;
import com.inso.modules.passport.user.service.UserVIPService;
import com.inso.modules.paychannel.model.ChannelInfo;
import com.inso.modules.paychannel.model.ChannelType;
import com.inso.modules.paychannel.model.PayProductType;
import com.inso.modules.paychannel.service.ChannelService;
import com.inso.modules.risk.RiskManager;
import com.inso.modules.risk.support.WithdrawInterceptorImpl;
import com.inso.modules.web.SystemRunningMode;
import com.inso.modules.web.logical.SystemStatusManager;
import com.inso.modules.web.model.ConfigKey;
import com.inso.modules.web.model.VIPType;
import com.inso.modules.web.service.ConfigService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.joda.time.DateTime;
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
import com.inso.modules.admin.core.helper.CoreAdminHelper;
import com.inso.modules.passport.business.helper.BusinessOrderVerify;
import com.inso.modules.passport.business.PlatformPayManager;
import com.inso.modules.passport.money.UserPayManager;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.business.model.WithdrawOrder;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.passport.business.service.WithdrawOrderService;

/**
 * 用户充值管理
 */

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class WithdrawController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private PlatformPayManager mPlatformPayManager;

//    @Autowired
//    private BusinessOrderService mBusinessOrderService;

    @Autowired
    private WithdrawOrderService mWithdrawOrderService;

    @Autowired
    private UserPayManager mUserPayMgr;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private ConfigService mConfigService;

    @Autowired
    private ChannelService mChannelService;

    @Autowired
    private CoinAccountService mAccountService;

    @Autowired
    private UserMoneyService mUserMoneyService;

    @Autowired
    private WithdrawlLimitManager mWithdrawlLimitManager;

    @Autowired
    private UserVIPService mUserVIPService;

    @Autowired
    private CardService mCardService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private WithdrawAuth mWithdrawAuth;

    private FiatCurrencyType mFiatCurrencyType;

    @RequiresPermissions("root_passport_user_withdraw_order_audit_list")
    @RequestMapping("root_passport_user_withdraw_order_audit")
    public String toAuditUserWithdraw(Model model)
    {
        return "admin/passport/user_withdraw_order_audit_list";
    }

    @RequiresPermissions("root_passport_user_withdraw_order_audit_list")
    @RequestMapping("getAuditUserWithdrawList")
    @ResponseBody
    public String getAuditUserWithdrawList()
    {
        String time = WebRequest.getString("time");
        String username = WebRequest.getString("username");
        String agentname = WebRequest.getString("agentname");
        String staffname = WebRequest.getString("staffname");

        String systemOrderno = WebRequest.getString("systemOrderno");
        String outTradeNo = WebRequest.getString("outTradeNo");

//        String txStatusString = WebRequest.getString("txStatus");


        OrderTxStatus txStatus = OrderTxStatus.AUDIT;

        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        if(!pageVo.parseTime(time))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        // 订单号检验，如果不是本业务订单号，则直接返回
        if(!StringUtils.isEmpty(systemOrderno) && !BusinessOrderVerify.verify(systemOrderno, BusinessType.USER_WITHDRAW))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        long userid = mUserQueryManager.findUserid(username);
        long agentid = mUserQueryManager.findUserid(agentname);
        long staffid = mUserQueryManager.findUserid(staffname);

        RowPager<WithdrawOrder> rowPager = mWithdrawOrderService.queryScrollPage(pageVo, userid, agentid,staffid, systemOrderno, outTradeNo, txStatus , null,null,null);

//        List<WithdrawOrder> list =rowPager.getList();
//
//        for(int i=0;i<list.size();i++){
//            UserAttr userAttr = mUserAttrService.find(false, list.get(i).getUserid() );
//            list.get(i).setAgentname(userAttr.getAgentname());
//            list.get(i).setStaffname(userAttr.getDirectStaffname());
//        }
//        rowPager.setList(list);

        template.setData(rowPager);
        return template.toJSONString();
    }

    @RequiresPermissions("root_passport_user_withdraw_order_audit_list")
    @RequestMapping("getAuditUserWithdrawNumber")
    @ResponseBody
    public String getAuditUserWithdrawNumber()
    {
        OrderTxStatus txStatus = OrderTxStatus.AUDIT;

        ApiJsonTemplate template = new ApiJsonTemplate();

        DateTime date=new DateTime();
        DateTime startdate=date.plusWeeks(-1);

        String fromTime = DateUtils.convertString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, startdate );
        String toTime = DateUtils.convertString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, date );
        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        pageVo.setFromTime(fromTime);
        pageVo.setToTime(toTime);

        RowPager<WithdrawOrder> rowPager = mWithdrawOrderService.queryScrollPage(pageVo, -1, -1,-1, null, null, txStatus , null,null,null);
        Map<String, Object> map = new HashMap<>();
        map.put("AuditUserWithdrawNumber", rowPager.getTotal());
        template.setData(map);

        return template.toJSONString();
    }


    @RequiresPermissions("root_passport_user_withdraw_order_audit_edit")
    @RequestMapping("doAuditUserWithdrawOrder")
    @ResponseBody
    public String doAuditOrder()
    {
        String orderno = WebRequest.getString("orderno");
        String action = WebRequest.getString("action");
        String remoteip = WebRequest.getRemoteIP();
        String remarkInfo = WebRequest.getString("remarkInfo");

        ApiJsonTemplate template = new ApiJsonTemplate();

        if(StringUtils.isEmpty(orderno) || !RegexUtils.isDigit(orderno))
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

//        if(SystemRunningMode.isCryptoMode())
//        {
//            // 超级管理员&&白名单不限制
//            if(!(AdminAccountHelper.isNy4timeAdminOrDEV() || WhiteIPManager.getInstance().verify(remoteip)))
//            {
//                WithdrawOrder orderInfo = mWithdrawOrderService.findByNo(orderno);
//                if(orderInfo == null)
//                {
//                    template.setJsonResult(SystemErrorResult.ERR_PARAMS);
//                    return template.toJSONString();
//                }
//
//                if(!mWithdrawAuth.verify(true))
//                {
//                    template.setError(-1, "未认证或认证过期!");
//                    return template.toJSONString();
//                }
//
//                else if(!UserInfo.DEFAULT_GAME_SYSTEM_AGENT.equalsIgnoreCase(orderInfo.getAgentname()))
//                {
//                    template.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
//                    return template.toJSONString();
//                }
//            }
//
//        }

        boolean enableAutoWithdraw = mConfigService.getBoolean(false, SystemConfig.USER_AUTO_WITHDRAW_MAX_MONEY.getKey());

        String checker = CoreAdminHelper.getAdminName();
        ErrorResult result = null;
        if("pass".equalsIgnoreCase(action))
        {
            if(enableAutoWithdraw)
            {
                template.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "系统已变更为自动审核通过提现模式!");
                return template.toJSONString();
            }

            result = mUserPayMgr.passWithdrawOrderToWaiting(orderno, checker);
        }
        else if("passToRealized".equalsIgnoreCase(action))
        {
            String outTradeNo = WebRequest.getString("outTradeNo");
            if(StringUtils.isEmpty(outTradeNo) || !RegexUtils.isLetterDigit(orderno))
            {
                template.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return template.toJSONString();
            }

            boolean forceRearlized = true;
            result = mUserPayMgr.doWithdrawSuccess(orderno, outTradeNo, "passToRealized,  " + checker, forceRearlized);
        }
        else if("refuse".equalsIgnoreCase(action))
        {
            if(enableAutoWithdraw)
            {
                template.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "系统已变更为自动审核通过提现模式!");
                return template.toJSONString();
            }

            result = mUserPayMgr.refuseWithdrawOrder(orderno, remarkInfo, checker);
        }
        else
        {
            result = SystemErrorResult.ERR_PARAMS;
        }
        template.setJsonResult(result);
        return template.toJSONString();
    }


    @RequiresPermissions("root_passport_user_withdraw_order_record_list")
    @RequestMapping("doPassportWithdrawAuth")
    @ResponseBody
    public String doPassportWithdrawAuth(Model model)
    {
        String googleCode = WebRequest.getString("withdrawGoogleCode");
        int expires = WebRequest.getInt("withdrawExpires");
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(!(expires == 60 || expires == 300 || expires == 600))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        String admin = AdminAccountHelper.getAdmin().getAccount();
        AdminSecret adminSecret = adminService.findAdminSecretByID(admin);
        if(!adminSecret.checkGoogleCode(googleCode))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_GOOGLE);
            return apiJsonTemplate.toJSONString();
        }

        mWithdrawAuth.addAuth(true, expires);
        apiJsonTemplate.setJsonResult(SystemErrorResult.SUCCESS);
        return apiJsonTemplate.toJSONString();

    }

    @RequiresPermissions("root_passport_user_withdraw_order_record_list")
    @RequestMapping("root_passport_user_withdraw_order_record")
    public String tUserWithdrawRecordPage(Model model)
    {
        boolean iscrypto = (SystemRunningMode.getSystemConfig() == SystemRunningMode.CRYPTO);
        model.addAttribute("iscrypto", iscrypto);

        CryptoCurrency[] arr = CryptoCurrency.values();
        model.addAttribute("cryptoCurrencyArr", arr);
        return "admin/passport/user_withdraw_order_record_list";
    }

   // @RequiresPermissions("root_passport_user_withdraw_order_record_list")
    @RequestMapping("reWithdrawOrder")
    @ResponseBody
    public String reWithdrawOrder()
    {
        String orderno = WebRequest.getString("no");
        ApiJsonTemplate template = new ApiJsonTemplate();
        if(StringUtils.isEmpty(orderno) || !RegexUtils.isDigit(orderno))
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }
        WithdrawOrder orderInfo = mWithdrawOrderService.findByNo(orderno);
        UserInfo userInfo = mUserService.findByUsername(false, orderInfo.getUsername());


        RemarkVO remarkVO =orderInfo.getRemarkVO();
        CryptoNetworkType networkType = CryptoNetworkType.getType( remarkVO.getString("ifsc"));
        UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());
        if(userAttr.getAgentid() <= 0)
        {
            // 没有代理，说明当前用户非法，
            template.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "Current User is not support!");
            return template.toJSONString();
        }

        String  str =remarkVO.getString("channelid");
        long channelid=Long.valueOf(str);
        ChannelInfo channelInfo = mChannelService.findById(false, channelid);
        if(channelInfo == null || !ChannelType.PAYOUT.getKey().equalsIgnoreCase(channelInfo.getType()))
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }
        if(!channelInfo.getProductType().equalsIgnoreCase(PayProductType.COIN.getKey()))
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return  template.toJSONString();
        }


        String address = remarkVO.getString(UserWithdrawVO.KEY_ACCOUNT);

        CoinAccountInfo accountInfo = mAccountService.findByAddress(false, address);

        CryptoCurrency currencyType = CryptoCurrency.getType(orderInfo.getCurrency());
        BigDecimal amount = orderInfo.getAmount();
        ErrorResult result = mUserPayMgr.createWithdrawOrderByCoin(userInfo, accountInfo, channelInfo, currencyType, amount);
        template.setData(result);

        return template.toJSONString();
    }

    @RequiresPermissions("root_passport_user_withdraw_order_record_list")
    @RequestMapping("getUserWithdrawRecordList")
    @ResponseBody
    public String getUserWithdrawRecordList()
    {
        String beneficiaryAccount = WebRequest.getString("beneficiaryAccount");
        String beneficiaryIdcard = WebRequest.getString("beneficiaryIdcard");

        String time = WebRequest.getString("time");
        String username = WebRequest.getString("username");
        if(username == null){
            username = "c_"+WebRequest.getString("address");
        }

        String agentname = WebRequest.getString("agentname");

        String staffname = WebRequest.getString("staffname");



        String systemOrderno = WebRequest.getString("systemOrderno");
        String outTradeNo = WebRequest.getString("outTradeNo");

        String txStatusString = WebRequest.getString("txStatus");
        OrderTxStatus txStatus = OrderTxStatus.getType(txStatusString);

        String currencyType = WebRequest.getString("currencyType");
        //ICurrencyType currencyType = ICurrencyType.getType(WebRequest.getString("currencyType"));

        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        if(!pageVo.parseTime(time))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        // 订单号检验，如果不是本业务订单号，则直接返回
        if(!StringUtils.isEmpty(systemOrderno) && !BusinessOrderVerify.verify(systemOrderno, BusinessType.USER_WITHDRAW))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        UserInfo userInfo = mUserService.findByUsername(false, username);

        long userid = 0;
        if(userInfo != null)
        {
            userid = userInfo.getId();
        }

        long staffid = mUserQueryManager.findUserid(staffname);
        long agentid = mUserQueryManager.findUserid(agentname);

        RowPager<WithdrawOrder> rowPager = mWithdrawOrderService.queryScrollPage(pageVo, userid, agentid,staffid, systemOrderno, outTradeNo, txStatus, OrderTxStatus.AUDIT,beneficiaryAccount,beneficiaryIdcard);
        if(currencyType==null){
            template.setData(rowPager);
        }else{
            List<WithdrawOrder> list = rowPager.getList();
            List<WithdrawOrder> relist = new ArrayList<>();
            for(int i=0;i< list.size();i++){
                if(!StringUtils.isEmpty(list.get(i).getRemark())) {
                    String currencyTypeRemark=JSONObject.parseObject(list.get(i).getRemark()).getString("currencyType");
                    if(currencyTypeRemark.equals(currencyType)){
                        relist.add(list.get(i));
                    }

                }

            }
            rowPager.setList(relist);
            template.setData(rowPager);
        }


        return template.toJSONString();
    }


    /**
     * 审核 waiting状态下 的订单
     * @param model
     * @return
     */
    @RequiresPermissions("root_passport_user_withdraw_order_record_edit")
    @RequestMapping("root_passport_user_withdraw_order_audit_waiting_result_page")
    public String tUserWithdrawAuditWaitingToResultPage(Model model)
    {
        String orderno = WebRequest.getString("orderno");
        WithdrawOrder orderInfo = mWithdrawOrderService.findByNo(orderno);
        model.addAttribute("order", orderInfo);
        model.addAttribute("remarkInfo", orderInfo.getRemarkVO());
        return "admin/passport/user_withdraw_order_audit_page_result";
    }



    @RequiresPermissions("root_passport_user_withdraw_order_record_list")
    @RequestMapping("backgroundRewithdraw")
    @ResponseBody
    public String backgroundRewithdraw()
    {

        String orderno = WebRequest.getString("no");
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        if(StringUtils.isEmpty(orderno) || !RegexUtils.isDigit(orderno))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }
        WithdrawOrder orderInfo = mWithdrawOrderService.findByNo(orderno);
        UserInfo userInfo = mUserService.findByUsername(false, orderInfo.getUsername());
        BigDecimal amount = orderInfo.getAmount(); //WebRequest.getBigDecimal("amount");
        List<BankCard> BankCardList= mCardService.queryListByUserid(true,userInfo.getId());
        if(BankCardList==null){
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }


        BankCard bankCard = null;
        if(BankCardList.size()>0){
             bankCard = BankCardList.get(0);
        }

        if(!SystemStatusManager.getInstance().isRunning())
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_MAINTAINED);
            return apiJsonTemplate.toJSONString();
        }

        // 1. basic params check

        if(bankCard == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
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
//        boolean rsPlatformCheckLimit = checkPlatformWithdrawLimit(apiJsonTemplate, platformWithdrawConfigMaps, amount, nowTime, null,null);
//        if(!rsPlatformCheckLimit)
//        {
//            return apiJsonTemplate.toJSONString();
//        }

        //todo 每日提现次数
        long timesOfDay = StringUtils.asLong(platformWithdrawConfigMaps.get(PlatformConfig.ADMIN_APP_PLATFORM_USER_WITHDRAW_TIMES_OF_DAY));
        String userTimesOfDayCacheKey = UserMoneyCacheHelper.createUserWithdrawTimesOfDayLimit(userInfo.getName(), nowTime.getDayOfYear());

        long userTimesOfDayValue = StringUtils.asLong(CacheManager.getInstance().getString(userTimesOfDayCacheKey));
        if (timesOfDay <= userTimesOfDayValue){
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_MAX_WITHDRAW_TIME);
            return apiJsonTemplate.toJSONString();
        }

        //todo 每日提现最大金额
        float totalAmountLimitOfDay = StringUtils.asLong(platformWithdrawConfigMaps.get(PlatformConfig.ADMIN_APP_PLATFORM_USER_WITHDRAW_MAX_MONEY_OF_DAY));
        String userAmountLimitOfDayCacheKey = UserMoneyCacheHelper.createUserWithdrawTotalAmountOfDay(userInfo.getName(), nowTime.getDayOfYear());
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
        BigDecimal rsCodeAmount = userMoney.getBalance().subtract(userMoney.getCodeAmount()).subtract(amount);
        if(rsCodeAmount.compareTo(BigDecimal.ZERO) < 0)
        {
            //apiJsonTemplate.setError(-8810, "Not enough code amount, also need code amount " + Math.abs(rsCodeAmount.floatValue()));
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

}
