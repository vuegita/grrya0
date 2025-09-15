package com.inso.modules.passport.controller;

import com.google.common.collect.Maps;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.modules.ad.core.logical.WithdrawlLimitManager;
import com.inso.modules.admin.config.PlatformConfig;
import com.inso.modules.common.helper.RequestTokenHelper;
import com.inso.modules.common.model.*;
import com.inso.modules.passport.UserErrorResult;
import com.inso.modules.passport.business.model.ReturnWaterType;
import com.inso.modules.passport.business.service.BusinessOrderService;
import com.inso.modules.passport.business.service.CardService;
import com.inso.modules.passport.user.logical.TodayInviteFriendManager;
import com.inso.modules.passport.user.logical.RegPresentationManager;
import com.inso.modules.passport.user.logical.RelationManager;
import com.inso.modules.passport.business.helper.TodayInviteFriendHelper;
import com.inso.modules.passport.user.limit.MyLoginRequired;
import com.inso.modules.passport.user.model.*;
import com.inso.modules.passport.money.PayApiManager;
import com.inso.modules.passport.money.model.UserMoney;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.returnwater.ReturnWaterManager;
import com.inso.modules.passport.returnwater.service.ReturnWaterLogAmountService;
import com.inso.modules.passport.user.service.*;
import com.inso.modules.web.logical.SystemStatusManager;
import com.inso.modules.web.model.VIPInfo;
import com.inso.modules.web.model.VIPType;
import com.inso.modules.web.service.ConfigService;
import com.inso.modules.web.service.VIPService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/passport/userVipApi")
public class UserVipApi {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserMoneyService mUserMoneyService;

    @Autowired
    private AuthService mOauth2Service;

    @Autowired
    private RelationManager mRelationMgr;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private UserSecretService mUserSecretService;

    @Autowired
    private CardService mCardService;

    @Autowired
    private TodayInviteFriendManager minviteFriendManager;

    @Autowired
    private RegPresentationManager mRegPresentationMgr;

    @Autowired
    private ReturnWaterLogAmountService mReturnWaterLogService;

    @Autowired
    private UserVIPService mUserVIPService;

    @Autowired
    private VIPService mVIPService;

    @Autowired
    private BuyVipOrderService mBuyVipOrderService;

    @Autowired
    private PayApiManager mPayApiManager;

    @Autowired
    private ConfigService mConfigService;

    @Autowired
    private BusinessOrderService mBusinessOrderService;

    @Autowired
    private PayApiManager payManager;

    @Autowired
    private ReturnWaterManager mReturnWaterManager;

    @Autowired
    private WithdrawlLimitManager mWithdrawlLimitManager;

    @MyLoginRequired
    @RequestMapping("/getUserVipInfo")
    public String getUserVipInfo()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mOauth2Service.getAccountByAccessToken(accessToken);

        String vipTypeStr = WebRequest.getString("vipType");
        VIPType vipType = VIPType.getType(vipTypeStr);

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(vipType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        UserInfo userInfo = mUserService.findByUsername(false, username);

        UserVIPInfo vipInfo = mUserVIPService.findByUserId(false, userInfo.getId(), vipType);
        apiJsonTemplate.setData(vipInfo);
        return apiJsonTemplate.toJSONString();
    }

    @MyLoginRequired
    @RequestMapping("getInviteStatus")
    public String getInviteStatus()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mOauth2Service.getAccountByAccessToken(accessToken);
        UserInfo userInfo = mUserService.findByUsername(false, username);
        DateTime dateTime = new DateTime();
        int regCount = TodayInviteFriendHelper.getTodayRegCount(dateTime, userInfo.getId());
        int buyVipCount = TodayInviteFriendHelper.getTodayRegAndBuyVipCount(dateTime, userInfo.getId());
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        Map<String, Object> model = Maps.newHashMap();
        model.put("regCount", regCount);
        model.put("buyVipCount", buyVipCount);

        apiJsonTemplate.setData(model);
        return apiJsonTemplate.toJSONString();
    }


    @MyLoginRequired
    @RequestMapping("/buyVip")
    public String buyVip()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mOauth2Service.getAccountByAccessToken(accessToken);

        long vipid = WebRequest.getLong("vipid");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(!SystemStatusManager.getInstance().isRunning())
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_MAINTAINED);
            return apiJsonTemplate.toJSONString();
        }

        if(!RequestTokenHelper.verifyGame(username))
        {
            // 并发限制
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_REQUESTS);
            return apiJsonTemplate.toJSONString();
        }

        VIPInfo vipInfo = mVIPService.findById(false, vipid);
        if(vipInfo == null || vipInfo.getId() <= 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }
        if(vipInfo.getPrice().compareTo(BigDecimal.ZERO) <= 0)
        {
            // VIP0不要购买
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_ILEGAL);
            return apiJsonTemplate.toJSONString();
        }
        VIPType vipType = VIPType.getType(vipInfo.getType());

        UserInfo userInfo = mUserService.findByUsername(false, username);

        UserVIPInfo userVIPInfo = mUserVIPService.findByUserId(false, userInfo.getId(), vipType);

        BigDecimal amount = vipInfo.getPrice();

        boolean existVip = false;
        // 买过VIP
        if(userVIPInfo.verifyBuyVIP())
        {
            // 是否开启VIP过期,暂时不开启
            if(userVIPInfo.verifyExpires())
            {
                // 用户当前会员等级大于 现有等级 && 未过期，则会员存在
                // VIP过期, 则要重新购买整个VIP价格
            }
            else
            {
                // 判断是否是升级VIP
                if(userVIPInfo.getVipLevel() >= vipInfo.getLevel() )
                {
                    apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST);
                    return apiJsonTemplate.toJSONString();
                }
                // 会员当前VIP价格,要减去现在会员的VIP价格
                VIPInfo lastVipInfo = mVIPService.findById(false, userVIPInfo.getVipid());
                amount = vipInfo.getPrice().subtract(lastVipInfo.getPrice());
            }

            existVip = true;
        }

        if(amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());

        synchronized (userAttr.getUsername())
        {
            FundAccountType accountType = FundAccountType.Spot;
            ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
            UserMoney userMoney = mUserMoneyService.findMoney(false, userInfo.getId(), accountType, currencyType);
            if(!userMoney.verify(amount))
            {
                // 余额不足
                apiJsonTemplate.setJsonResult(UserErrorResult.ERR_PAY_NOT_ENOUGH_BALANCE);
                return apiJsonTemplate.toJSONString();
            }

            String orderno = mBuyVipOrderService.createOrder(userAttr, vipInfo, amount);
            ErrorResult errorResult = mPayApiManager.doBusinessDeduct(accountType, currencyType, BusinessType.USER_BUY_VIP, orderno, userInfo, amount, null, null);
            if(errorResult == SystemErrorResult.SUCCESS)
            {
                // 已经成功支付
                mBuyVipOrderService.updateInfo(orderno, OrderTxStatus.WAITING);

                // 存在vip，则要修改
                if(existVip)
                {
                    // 有效期只有3个月，不管开不开启失效时间要记录下来
                    DateTime expiresTime = new DateTime();
                    expiresTime = expiresTime.plusMonths(3);
                    mUserVIPService.updateInfo(userVIPInfo, null, vipInfo, expiresTime.toDate());
                }
                else
                {
                    // 不存在则直接成功
                    mUserVIPService.addVip(userAttr, vipInfo, Status.ENABLE);

                    // 成功邀请好友并购买VIP, 历史也算
                    TodayInviteFriendHelper.increRegAndBuy(userAttr.getParentid());
                }

                // 修改订单为成功
                mBuyVipOrderService.updateInfo(orderno, OrderTxStatus.REALIZED);

                // 清除缓存
                mUserVIPService.findByUserId(true, userInfo.getId(), vipType);

//                if(!existVip)
//                {
//                    // 升级会员不赠送, 首次购买会员才会赠送
//                    //用户购买vip金额赠送给上级比例
//                    doBuyVipPresentationParentUserByPercent(userInfo,userAttr,orderno,amount);
//                }
                UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());
                if(userType == UserInfo.UserType.MEMBER)
                {
                    handleFinishBuyVipTask(userInfo, orderno, amount);
                }

            }
            apiJsonTemplate.setJsonResult(errorResult);
        }

        return apiJsonTemplate.toJSONString();
    }

    @Async
    public void handleFinishBuyVipTask(UserInfo userInfo, String orderno, BigDecimal amount)
    {
        try {
            // 用户购买vip金额赠送给一二级的返佣比例
            FundAccountType accountType = FundAccountType.Spot;
            ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
            mReturnWaterManager.doReturnWater(currencyType, ReturnWaterType.AD, userInfo, orderno, amount);

            // 增加提现额度
            mWithdrawlLimitManager.increAmount(false, userInfo, amount);
        } catch (Exception e) {
        }
    }

    /**
     * 用户购买vip金额赠送给上级比例
     * @param userInfo
     * @param
     */
    private void doBuyVipPresentationParentUserByPercent(UserInfo userInfo, UserAttr userAttr, String BuyVipOrder,BigDecimal amount)
    {

        BigDecimal rate = mConfigService.getBigDecimal(false, PlatformConfig.ADMIN_APP_PLATFORM_USER_BUY_VIP_PRESENTATION_PARENTUSER_RATE);
        // 小于等于0不赠送
        if(rate == null || rate.compareTo(BigDecimal.ZERO) <= 0)
        {
            return;
        }

        UserInfo parentUserInfo = mUserService.findByUsername(false, userAttr.getParentname());
        if(parentUserInfo ==null ){
            return;
        }

        UserAttr parentUserAttr = mUserAttrService.find(false, parentUserInfo.getId());
        if(parentUserAttr ==null ){
            return;
        }

        BigDecimal presentationAmount = amount.multiply(rate);
        //
        BusinessType businessType = BusinessType.BUY_VIP_PRESENTATION_PARENTUSER_BY_PERCENT;

        String remarkMsg = "buy vip presentation parentuser by percent";
        RemarkVO remark = RemarkVO.create(remarkMsg);

        BuyVipOrderInfo buyVipOrder = mBuyVipOrderService.findByNo(false, BuyVipOrder);

        //
        FundAccountType accountType = FundAccountType.Spot;
        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
        String orderno = mBusinessOrderService.createOrder(accountType, currencyType, buyVipOrder.getNo(), parentUserAttr, businessType, presentationAmount, null, buyVipOrder.getCreatetime(), remark);
        // 走平台赠送通道
        remarkMsg +=  ":buy Vip username ="+ userInfo.getName()+ ",buy Vip amount = " + amount + ",buy Vip order = " + buyVipOrder.getNo() + ", presentation parentUser order = " + orderno;
        remark.setMesage(remarkMsg);

        ErrorResult result = payManager.doPlatformPresentation(accountType, currencyType, businessType, orderno, parentUserInfo, presentationAmount, remark);//doPlatformPresentation
        if(result == SystemErrorResult.SUCCESS)
        {
            mBusinessOrderService.updateTxStatus(orderno, OrderTxStatus.REALIZED, null, null);

        }

    }

}
