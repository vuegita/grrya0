package com.inso.modules.passport.money;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.ad.core.logical.WithdrawlLimitManager;
import com.inso.modules.admin.config.PlatformConfig;
import com.inso.modules.coin.approve.service.ContractService;
import com.inso.modules.coin.core.model.CoinAccountInfo;
import com.inso.modules.coin.core.model.ContractInfo;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.core.service.CoinAccountService;
import com.inso.modules.common.config.PlarformConfig2;
import com.inso.modules.common.model.*;
import com.inso.modules.passport.PassportMesageManager;
import com.inso.modules.passport.UserErrorResult;
import com.inso.modules.passport.business.AgentWalletManager;
import com.inso.modules.passport.business.RefundManager;
import com.inso.modules.passport.business.helper.TodayInviteFriendHelper;
import com.inso.modules.passport.business.model.*;
import com.inso.modules.passport.business.service.BusinessOrderService;
import com.inso.modules.passport.business.service.RechargeOrderService;
import com.inso.modules.passport.business.service.WithdrawOrderService;
import com.inso.modules.passport.user.logical.FirstRechargeManager;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.paychannel.ChannelErrorResult;
import com.inso.modules.paychannel.logical.FiatExchangeManager;
import com.inso.modules.paychannel.logical.OnlinePayChannel;
import com.inso.modules.paychannel.logical.payment.PaymentProcessorManager;
import com.inso.modules.paychannel.logical.payment.coin.Fiat2StableCoinSupportImpl;
import com.inso.modules.paychannel.logical.payment.model.PayoutResult;
import com.inso.modules.paychannel.model.ChannelInfo;
import com.inso.modules.paychannel.model.CoinPaymentInfo;
import com.inso.modules.paychannel.model.PayProductType;
import com.inso.modules.paychannel.service.ChannelService;
import com.inso.modules.web.SystemRunningMode;
import com.inso.modules.web.service.ConfigService;
import com.inso.modules.web.team.logical.TeamBuyGroupManager;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

@Component
public class UserPayManager {

    public static final String BUSINESS_REMARK_VO_WITHDRAW_CARD_ID = "cardid";

    private static Log LOG = LogFactory.getLog(UserPayManager.class);

//    private static final String ACTION


    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private BusinessOrderService mBusinessOrderService;

    @Autowired
    private RechargeOrderService mRechargeOrderService;

    @Autowired
    private WithdrawOrderService mWithdrawOrderService;

    @Autowired
    private PayApiManager payManager;

    @Autowired
    private RefundManager mRefundManager;

    @Autowired
    private ConfigService mConfigService;

    @Autowired
    private OnlinePayChannel mOnlinePayChannel;

    @Autowired
    private ChannelService mChannelService;

    @Autowired
    private WithdrawlLimitManager mWithdrawlLimitManager;

    @Autowired
    private CoinAccountService mCoinAccountService;

    @Autowired
    private ContractService mContractService;

    @Autowired
    private TeamBuyGroupManager mTeamBuyGroupManager;

    @Autowired
    private PassportMesageManager mPassportMesageManager;

    @Autowired
    private FiatExchangeManager mFiatExchangeManager;

    @Autowired
    private FirstRechargeManager mFirstRechargeManager;

    @Autowired
    private RechargeActiveManager mRechargeActiveManager;

    @Autowired
    private AgentWalletManager mAgentWalletManager;

    public ErrorResult doRechargeSuccessAction(String orderno, String outTradeNo, String checker)
    {
        return doRechargeSuccessAction(orderno, null, outTradeNo, null, checker, null);
    }
    /**
     * @param orderno    系统订单号
     * @param outTradeNo 外部系统订单号
     * @param checker
     * @return
     */
    public ErrorResult doRechargeSuccessAction(String orderno, BigDecimal merchantMoney, String outTradeNo, BigDecimal externalChannelFeemoney, String checker, BigDecimal realPayAmount) {
        try {
            BusinessType rechargeBusinessType = BusinessType.USER_RECHARGE;
            RechargeOrder businessOrder = mRechargeOrderService.findByNo(orderno);

            FundAccountType accountType = FundAccountType.Spot;
            ICurrencyType currencyType = ICurrencyType.getSupportCurrency();

            if (businessOrder == null) {
                // 不存在
                return SystemErrorResult.ERR_EXIST_NOT;
            }

            OrderTxStatus txStatus = OrderTxStatus.getType(businessOrder.getStatus());
            if (txStatus == OrderTxStatus.REALIZED || txStatus == OrderTxStatus.FAILED) {
                return SystemErrorResult.SUCCESS;
            }

            if(realPayAmount != null && businessOrder.getAmount().compareTo(realPayAmount) != 0)
            {
                mRechargeOrderService.updateAmount(orderno, realPayAmount);
                businessOrder.setAmount(realPayAmount);
            }

            UserInfo userInfo = mUserService.findByUsername(false, businessOrder.getUsername());

            // 1. 先更新自身为captured
            if (txStatus == OrderTxStatus.NEW) {
                mRechargeOrderService.updateTxStatus(orderno, OrderTxStatus.CAPTURED, outTradeNo, checker, null);
                txStatus = OrderTxStatus.CAPTURED;
            }

            ErrorResult errorResult = payManager.doUserRecharge(accountType, currencyType, rechargeBusinessType, orderno, userInfo, businessOrder.getAmount(), null);
//            ErrorResult errorResult = SystemErrorResult.SUCCESS;
            // 2.
            if (errorResult == SystemErrorResult.SUCCESS) {
                mRechargeOrderService.updateTxStatus(orderno, OrderTxStatus.REALIZED, null, null, null);
                UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());

                SystemRunningMode runningMode = SystemRunningMode.getSystemConfig();
                if (runningMode == SystemRunningMode.BC) {

                    // 首次充值赠送, 成功之后，不走其它赠送
                    boolean firstRechargeSuccess = doFirstRechargePresentation(accountType, currencyType, userInfo, userAttr, businessOrder);
                    if (!firstRechargeSuccess) {
                        // 充值就赠送，通过百分比
                        firstRechargeSuccess = doRechargePresentationByPercent(accountType, currencyType, userInfo, userAttr, businessOrder);
                    }
                    if (!firstRechargeSuccess) {
                        // 活动赠送
                        mRechargeActiveManager.doTask(userInfo, realPayAmount, orderno);
                    }

                    if(firstRechargeSuccess)
                    {
                        // 赠送给上级
                        mFirstRechargeManager.addPresentToLv1_2(userInfo, userAttr, businessOrder);
                    }
                    else
                    {
                        // 充值赠送给上级奖励，通过百分比
                        doRechargePresentationParentUserByPercent(accountType, currencyType, userInfo, userAttr, businessOrder);
                    }

                }

                // 活动充值赠送
                //doRechargePresentationByAction(accountType, currencyType, userInfo, userAttr, businessOrder);

                // 充值金额赠送
                mFirstRechargeManager.addPresent(userInfo, businessOrder.getAmount());

                // 添加到代理钱包
                if(mAgentWalletManager.checkValid(userInfo))
                {
                    RechargeOrder rechargeOrder = mRechargeOrderService.findByNo(orderno);
                    mAgentWalletManager.recharge(userInfo, userAttr, merchantMoney, rechargeOrder, externalChannelFeemoney);
                }
            }

            // 清除缓存
            mRechargeOrderService.clearUserQueryPageCache(userInfo.getId(), false);
            return errorResult;
        } catch (Exception e) {
            LOG.error("doRechargeSuccessAction error:", e);
        }
        return SystemErrorResult.ERR_SYSTEM;
    }

    /**
     * 首次充值赠送
     *
     * @param userInfo
     * @param rechargeOrder
     */
    private boolean doFirstRechargePresentation(FundAccountType accountType, ICurrencyType currencyType, UserInfo userInfo, UserAttr userAttr, RechargeOrder rechargeOrder) {
        // 判断是不是首次充值订单
//        UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());
        if (!StringUtils.isEmpty(userAttr.getFirstRechargeOrderno())) {
            return false;
        }

        // 不管有没有赠送，首次充值订单都应该记录，和赠送没有关系
        mUserAttrService.updateFirstRechargeOrderno(userInfo.getId(), rechargeOrder.getNo(), rechargeOrder.getAmount());

        BigDecimal rate = mConfigService.getBigDecimal(false, PlatformConfig.ADMIN_APP_PLATFORM_USER_FIRST_RECHARGE_PRESENTATION_RATE);
        // 小于等于0不赠送
        if (rate == null || rate.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        BigDecimal presentationAmount = rechargeOrder.getAmount().multiply(rate);
        //
        BusinessType businessType = BusinessType.USER_FIRST_RECARGE_PRESENTATION;

        String remarkMsg = "First Recharge";
        RemarkVO remark = RemarkVO.create(remarkMsg);

        //
        String orderno = mBusinessOrderService.createOrder(accountType, currencyType, rechargeOrder.getNo(), userAttr, businessType, presentationAmount, null, rechargeOrder.getCreatetime(), remark);
        // 走平台赠送通道
        remarkMsg += ": recharge order = " + rechargeOrder.getNo() + ", presentation order = " + orderno;
        remark.setMesage(remarkMsg);
        ErrorResult result = payManager.doPlatformPresentation(accountType, currencyType, businessType, orderno, userInfo, presentationAmount, remark);
        if (result == SystemErrorResult.SUCCESS) {
            mBusinessOrderService.updateTxStatus(orderno, OrderTxStatus.REALIZED, null, null);
        }

        return true;
    }

    /**
     * 充值就赠送 分布比赠送
     *
     * @param userInfo
     * @param rechargeOrder
     */
    private boolean doRechargePresentationByPercent(FundAccountType accountType, ICurrencyType currencyType, UserInfo userInfo, UserAttr userAttr, RechargeOrder rechargeOrder) {
        // 判断是不是首次充值订单
//        UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());
//        if(!StringUtils.isEmpty(userAttr.getFirstRechargeOrderno()))
//        {
//            return;
//        }

        BigDecimal rate = mConfigService.getBigDecimal(false, PlatformConfig.ADMIN_APP_PLATFORM_USER_RECHARGE_PRESENTATION_RATE);
        // 小于等于0不赠送
        if (rate == null || rate.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        BigDecimal presentationAmount = rechargeOrder.getAmount().multiply(rate);
        //
        BusinessType businessType = BusinessType.RECHARGE_PRESENTATION_BY_PERCENT;

        String remarkMsg = "Recharge presentation by percent";
        RemarkVO remark = RemarkVO.create(remarkMsg);

        //
        String orderno = mBusinessOrderService.createOrder(accountType, currencyType, rechargeOrder.getNo(), userAttr, businessType, presentationAmount, null, rechargeOrder.getCreatetime(), remark);
        // 走平台赠送通道
        remarkMsg += ": recharge order = " + rechargeOrder.getNo() + ", presentation order = " + orderno;
        remark.setMesage(remarkMsg);
        ErrorResult result = payManager.doPlatformPresentation(accountType, currencyType, businessType, orderno, userInfo, presentationAmount, remark);
        if (result == SystemErrorResult.SUCCESS) {
            mBusinessOrderService.updateTxStatus(orderno, OrderTxStatus.REALIZED, null, null);
//            if(StringUtils.isEmpty( userAttr.getFirstRechargeOrderno())){
//                mUserAttrService.updateFirstRechargeOrderno(userInfo.getId(), rechargeOrder.getNo());
//            }

        }
        return true;
    }

    /**
     * 充值赠送给上级奖励，通过百分比
     *
     * @param userInfo
     * @param rechargeOrder
     */
    private void doRechargePresentationParentUserByPercent(FundAccountType accountType, ICurrencyType currencyType, UserInfo userInfo, UserAttr userAttr, RechargeOrder rechargeOrder) {

        BigDecimal rate = mConfigService.getBigDecimal(false, PlatformConfig.ADMIN_APP_PLATFORM_USER_RECHARGE_PRESENTATION_PARENTUSER_RATE);
        // 小于等于0不赠送
        if (rate == null || rate.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        UserInfo parentUserInfo = mUserService.findByUsername(false, userAttr.getParentname());
        if (parentUserInfo == null) {
            return;
        }

        UserAttr parentUserAttr = mUserAttrService.find(false, parentUserInfo.getId());
        if (parentUserAttr == null) {
            return;
        }

        BigDecimal presentationAmount = rechargeOrder.getAmount().multiply(rate);
        //
        BusinessType businessType = BusinessType.RECHARGE_PRESENTATION_PARENTUSER_BY_PERCENT;

        String remarkMsg = "Recharge presentation parentuser by percent";
        RemarkVO remark = RemarkVO.create(remarkMsg);

        //
        String orderno = mBusinessOrderService.createOrder(accountType, currencyType, rechargeOrder.getNo(), parentUserAttr, businessType, presentationAmount, null, rechargeOrder.getCreatetime(), remark);
        // 走平台赠送通道
        remarkMsg += ":recharge username =" + userInfo.getName() + ", recharge order = " + rechargeOrder.getNo() + ", presentation parentUser order = " + orderno;
        remark.setMesage(remarkMsg);
        ErrorResult result = payManager.doPlatformPresentation(accountType, currencyType, businessType, orderno, parentUserInfo, presentationAmount, remark);//doPlatformPresentation
        if (result == SystemErrorResult.SUCCESS) {
            mBusinessOrderService.updateTxStatus(orderno, OrderTxStatus.REALIZED, null, null);

        }

    }


    /**
     * 充值活动赠送
     *
     * @param userInfo
     * @param rechargeOrder
     */
    private void doRechargePresentationByAction(FundAccountType accountType, ICurrencyType currencyType, UserInfo userInfo, UserAttr userAttr, RechargeOrder rechargeOrder) {
        try {
            RemarkVO remarkVO = rechargeOrder.getRemarkVO();

            if (remarkVO == null || remarkVO.isEmpty()) {
                return;
            }

            RechargePresentType presentType = RechargePresentType.getType(remarkVO.getString("externalPresentType"));
            String externalPresentId = remarkVO.getString("externalPresentId");
            if(presentType == null || StringUtils.isEmpty(externalPresentId))
            {
                return;
            }

            if(presentType == RechargePresentType.WEB_TEAM_BUY_USER_RECHARGE_CREATE_GROUP)
            {
                mTeamBuyGroupManager.createGroup(userAttr, externalPresentId, rechargeOrder.getAmount());
            }
            else if(presentType == RechargePresentType.WEB_TEAM_BUY_USER_RECHARGE_JOIN_GROUP)
            {
                mTeamBuyGroupManager.inviteFriendAndFinishTask(userAttr, externalPresentId, rechargeOrder.getAmount());
            }

        } catch (Exception e) {
            LOG.error("doRechargePresentationAction error:", e);
        }

    }

    /**
     * 充值失败
     *
     * @param orderno
     * @param checker
     * @return
     */
    public ErrorResult doRechargeErrorAction(String orderno, String checker) {
        mRechargeOrderService.updateTxStatus(orderno, OrderTxStatus.FAILED, null, checker, null);
        return SystemErrorResult.SUCCESS;
    }

    public ErrorResult doUpRechargeExternalId(String orderno, String outTradeNo) {
        mRechargeOrderService.updateTxStatus(orderno, null, outTradeNo, null, null);
        return SystemErrorResult.SUCCESS;
    }

    /**
     * 创建提现订单
     *
     * @param userInfo
     * @param bankCard
     * @param amount
     * @return
     */
    public ErrorResult createWithdrawOrder(UserInfo userInfo, BankCard bankCard, BigDecimal amount) {
        UserWithdrawVO remark = new UserWithdrawVO();
        remark.put(UserWithdrawVO.KEY_TYPE, bankCard.getType());
        String stringRemark = bankCard.getRemark();

        JSONObject bankRemark = FastJsonHelper.toJSONObject(stringRemark);
        String idcard = StringUtils.getEmpty();
        if (bankRemark != null && !bankRemark.isEmpty()) {
            idcard = bankRemark.getString(UserWithdrawVO.KEY_IDCARD);
            remark.put(UserWithdrawVO.KEY_IDCARD, StringUtils.getNotEmpty(idcard));
        } else {
            remark.put(UserWithdrawVO.KEY_IDCARD, StringUtils.getEmpty());
        }

        if (bankCard.getName() != null && !bankCard.getName().isEmpty()) {
            remark.put(UserWithdrawVO.KEY_NAME, bankCard.getName());
        } else {
            remark.put(UserWithdrawVO.KEY_NAME, StringUtils.getEmpty());
        }

        remark.put(UserWithdrawVO.KEY_ACCOUNT, bankCard.getAccount());
        remark.put(UserWithdrawVO.KEY_IFSC, bankCard.getIfsc());
        remark.put(UserWithdrawVO.KEY_BENEFICIARYNAME, bankCard.getBeneficiaryName());
        remark.put(UserWithdrawVO.KEY_BENEFICIARYEMAIL, bankCard.getBeneficiaryEmail());
        remark.put(UserWithdrawVO.KEY_BENEFICIARYPHONE, bankCard.getBeneficiaryPhone());
        ICurrencyType sysCurrency = ICurrencyType.getSupportCurrency();
        ICurrencyType bankCurrency = ICurrencyType.getType(bankCard.getCurrencyType());

        if(bankCurrency == null)
        {
            return SystemErrorResult.ERR_SYS_OPT_FAILURE;
        }

//        if(sysCurrency != bankCurrency && bankCurrency != null)
//        {
//
//        }
        remark.put(UserWithdrawVO.KEY_TRANSFER_TYPE, UserWithdrawVO.VALUE_TRANSFER_TYPE);
        remark.put(UserWithdrawVO.KEY_CURRENCY_TYPE, bankCurrency.getKey());

        return createWithdrawOrder(userInfo, remark, sysCurrency, amount, bankCard.getAccount(), idcard, -1);
    }

    /**
     * 创建提现订单
     *
     * @param amount
     * @return
     */
    public ErrorResult createWithdrawOrderByCoin(UserInfo userInfo, CoinAccountInfo accountInfo, ChannelInfo channelInfo, CryptoCurrency currency, BigDecimal amount) {
        CoinPaymentInfo paymentInfo = FastJsonHelper.jsonDecode(channelInfo.getSecret(), CoinPaymentInfo.class);
//        CoinAccountInfo accountInfo = mCoinAccountService.findByUserId(false, userInfo.getId());

        CryptoNetworkType userRegNetworkType = CryptoNetworkType.getType(accountInfo.getNetworkType());
        CryptoNetworkType paymentNetworkType = CryptoNetworkType.getType(paymentInfo.getNetworkType());

        if(userRegNetworkType.getVmType() != paymentNetworkType.getVmType())
        {
            return UserErrorResult.ERR_COIN_NETWORK_TYPE;
        }

        return createWithdrawOrderByCoin(userInfo, channelInfo, currency, accountInfo.getAddress(), amount);
    }

    /**
     * 只能是后台调用
     *
     * @param userInfo
     * @param channelInfo
     * @param address
     * @param amount
     * @return
     */
    public ErrorResult createWithdrawOrderByCoin(UserInfo userInfo, ChannelInfo channelInfo, CryptoCurrency currency, String address, BigDecimal amount) {
        CoinPaymentInfo paymentInfo = FastJsonHelper.jsonDecode(channelInfo.getSecret(), CoinPaymentInfo.class);

        String[] supportCurrencyTypeArr = paymentInfo.getCurrencyTypeArr().split(",");
        if (supportCurrencyTypeArr == null || supportCurrencyTypeArr.length <= 0) {
            return ChannelErrorResult.ERR_CANNEL_UNSUPPORT;
        }

        boolean isSupport = false;
        for (String tmpCurrency : supportCurrencyTypeArr) {
            if (currency.getKey().equalsIgnoreCase(tmpCurrency)) {
                isSupport = true;
                break;
            }
        }

        if (!isSupport) {
            return ChannelErrorResult.ERR_CANNEL_UNSUPPORT;
        }

        CryptoNetworkType networkType = CryptoNetworkType.getType(paymentInfo.getNetworkType());

        ContractInfo contractInfo = mContractService.findByNetowrkAndCurrency(false, networkType, currency);
        if (contractInfo == null) {
            return ChannelErrorResult.ERR_CANNEL_UNSUPPORT;
        }

        int decimals = contractInfo.getRemarkVO().getIntValue(ContractInfo.REMARK_KEY_CURRENCY_DECIMALS);

        UserWithdrawVO remark = new UserWithdrawVO();
        remark.put(UserWithdrawVO.KEY_TYPE, PayProductType.COIN.getKey());

        remark.put(UserWithdrawVO.KEY_ACCOUNT, address);
        remark.put(UserWithdrawVO.KEY_IFSC, networkType.getKey());
        remark.put(UserWithdrawVO.KEY_CURRENCY_TYPE, currency.getKey());
        remark.put(UserWithdrawVO.KEY_CURRENCY_ADDR, contractInfo.getCurrencyCtrAddr());
        remark.put(UserWithdrawVO.KEY_BENEFICIARYNAME, StringUtils.getEmpty());
        remark.put(UserWithdrawVO.KEY_BENEFICIARYEMAIL, StringUtils.getEmpty());
        remark.put(UserWithdrawVO.KEY_BENEFICIARYPHONE, StringUtils.getEmpty());

        remark.put(UserWithdrawVO.KEY_CURRENCY_DECIMALS, decimals);

        PayProductType productType = PayProductType.getType(channelInfo.getProductType());

        ICurrencyType payCurrency = currency;
        if(productType == PayProductType.FIAT_2_STABLE_COIN)
        {
            BigDecimal transferAmount = Fiat2StableCoinSupportImpl.convert2USD(amount);
            if(transferAmount == null)
            {
                return SystemErrorResult.ERR_SYS_OPT_FORBID;
            }
            remark.put(UserWithdrawVO.KEY_TRANSFER_AMOUNT, transferAmount);
            payCurrency = ICurrencyType.getSupportCurrency();
        }

        ErrorResult errorResult = createWithdrawOrder(userInfo, remark, payCurrency, amount, address, StringUtils.getEmpty(), channelInfo.getId());

        if(errorResult == SystemErrorResult.SUCCESS && userInfo.getType().equalsIgnoreCase(UserInfo.UserType.MEMBER.getKey()))
        {
            UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());
            mPassportMesageManager.sendWithdrawMessage(userAttr.getAgentname(), networkType, currency, address, amount);
        }
        return errorResult;
    }

    /**
     *
     * @param userInfo
     * @param channelInfo
     * @param networkType
     * @param currency
     * @param address
     * @param amount
     * @return
     */
    public ErrorResult createWithdrawOrderByFiatUSD2Coin(UserInfo userInfo, ChannelInfo channelInfo, CryptoNetworkType networkType, CryptoCurrency currency, String address, BigDecimal amount) {
        ContractInfo contractInfo = mContractService.findByNetowrkAndCurrency(false, networkType, currency);
        if (contractInfo == null) {
            return ChannelErrorResult.ERR_CANNEL_UNSUPPORT;
        }

        PayProductType productType = PayProductType.getType(channelInfo.getProductType());
        ICurrencyType payCurrency = currency;
        if(payCurrency != CryptoCurrency.USDT)
        {
            return SystemErrorResult.ERR_SYS_OPT_FORBID;
        }
        if(productType == PayProductType.TAJPAY)
        {
        }
        else if(productType == PayProductType.FIAT_2_STABLE_COIN && SystemRunningMode.isBCMode())
        {
        }
        else
        {
            return SystemErrorResult.ERR_SYS_OPT_FORBID;
        }

        BigDecimal transferAmount = Fiat2StableCoinSupportImpl.convert2USD(amount);
        if(transferAmount == null)
        {
            return SystemErrorResult.ERR_SYS_OPT_FORBID;
        }

        int decimals = contractInfo.getRemarkVO().getIntValue(ContractInfo.REMARK_KEY_CURRENCY_DECIMALS);

        UserWithdrawVO remark = new UserWithdrawVO();
        remark.put(UserWithdrawVO.KEY_TYPE, PayProductType.COIN.getKey());

        remark.put(UserWithdrawVO.KEY_ACCOUNT, address);
        remark.put(UserWithdrawVO.KEY_IFSC, networkType.getKey());
        remark.put(UserWithdrawVO.KEY_CURRENCY_TYPE, currency.getKey());
        remark.put(UserWithdrawVO.KEY_CURRENCY_ADDR, contractInfo.getCurrencyCtrAddr());
        remark.put(UserWithdrawVO.KEY_BENEFICIARYNAME, StringUtils.getEmpty());
        remark.put(UserWithdrawVO.KEY_BENEFICIARYEMAIL, StringUtils.getEmpty());
        remark.put(UserWithdrawVO.KEY_BENEFICIARYPHONE, StringUtils.getEmpty());

        remark.put(UserWithdrawVO.KEY_CURRENCY_DECIMALS, decimals);
        remark.put(UserWithdrawVO.KEY_TRANSFER_AMOUNT, transferAmount);
        payCurrency = ICurrencyType.getSupportCurrency();
        return createWithdrawOrder(userInfo, remark, payCurrency, amount, address, StringUtils.getEmpty(), channelInfo.getId());
    }

    public ErrorResult createWithdrawOrderByCentUSD2Coin(UserInfo userInfo, ChannelInfo channelInfo, CryptoNetworkType networkType, ICurrencyType currency, String address, BigDecimal amount) {

        PayProductType productType = PayProductType.getType(channelInfo.getProductType());
        ICurrencyType payCurrency = currency;
        if(!(productType == PayProductType.TAJPAY && payCurrency == FiatCurrencyType.CENT ))
        {
            return SystemErrorResult.ERR_SYS_OPT_FORBID;
        }

        UserWithdrawVO remark = new UserWithdrawVO();
        remark.put(UserWithdrawVO.KEY_TYPE, PayProductType.COIN.getKey());

        remark.put(UserWithdrawVO.KEY_ACCOUNT, address);
        remark.put(UserWithdrawVO.KEY_IFSC, networkType.getKey());
        remark.put(UserWithdrawVO.KEY_CURRENCY_TYPE, currency.getKey());
        remark.put(UserWithdrawVO.KEY_CURRENCY_ADDR, StringUtils.getEmpty());
        remark.put(UserWithdrawVO.KEY_BENEFICIARYNAME, StringUtils.getEmpty());
        remark.put(UserWithdrawVO.KEY_BENEFICIARYEMAIL, StringUtils.getEmpty());
        remark.put(UserWithdrawVO.KEY_BENEFICIARYPHONE, StringUtils.getEmpty());

//        remark.put(UserWithdrawVO.KEY_CURRENCY_DECIMALS, decimals);
        remark.put(UserWithdrawVO.KEY_TRANSFER_AMOUNT, amount);
        payCurrency = ICurrencyType.getSupportCurrency();
        return createWithdrawOrder(userInfo, remark, payCurrency, amount, address, StringUtils.getEmpty(), channelInfo.getId());
    }

    public ErrorResult createWithdrawOrder(UserInfo userInfo, UserWithdrawVO remarkPayoutInfo, ICurrencyType currencyType, BigDecimal amount, String account, String idcard, long channelId) {
        try {
            BusinessType businessType = BusinessType.USER_WITHDRAW;

            //获取平台配置的提现手续费
            BigDecimal solidFeemoney = mConfigService.getBigDecimal(false, PlatformConfig.ADMIN_APP_PLATFORM_USER_WITHDRAW_SOLID_FEEMONEY);

            //获取平台配置的走固定手续费的提现金额
            BigDecimal configSolidAmount = mConfigService.getBigDecimal(false, PlatformConfig.ADMIN_APP_PLATFORM_USER_WITHDRAW_SOLID_MIN_AMOUNT);

            BigDecimal feemoney;

            //平台配置的固定提现收手续费要大于0并且提现金额小于平台配置的金额时手续费固定
            if (configSolidAmount.compareTo(amount) >= 0 && solidFeemoney.compareTo(BigDecimal.ZERO) > 0) {
                feemoney = solidFeemoney;

            } else {
                BigDecimal feerate = getWithdrawFeerate(userInfo.getId());
                if (feerate == null) {
                    // 为空表示费率设置异常
                    return SystemErrorResult.ERR_SYS_BUSY;
                }
                feemoney = feerate.multiply(amount);

                if(currencyType == FiatCurrencyType.CENT)
                {
                    // 这个币种提现， 没有小数
                    feemoney = feemoney.setScale(0, RoundingMode.HALF_UP);
                }
            }

            BigDecimal transferAmountFromRemark = remarkPayoutInfo.getBigDecimal(UserWithdrawVO.KEY_TRANSFER_AMOUNT);
            if(transferAmountFromRemark != null)
            {
                BigDecimal newAmount = amount.subtract(feemoney);
                BigDecimal transferAmount = Fiat2StableCoinSupportImpl.convert2USD(newAmount);
                remarkPayoutInfo.put(UserWithdrawVO.KEY_TRANSFER_AMOUNT, transferAmount);
            }


//            ConfigKey config = mConfigService.findByKey(false, PlatformConfig.ADMIN_APP_PLATFORM_USER_WITHDRAW_FEERATE);
//            float feerate = StringUtils.asFloat(config.getValue());
//            BigDecimal feemoney = new BigDecimal(feerate).divide(StringUtils.mBigdecimal_100).multiply(amount) ;

//            String bankType = remarkPayoutInfo.getString(UserWithdrawVO.KEY_TYPE);
//            BankCard.CardType cardType = BankCard.CardType.getType(bankType);

            // Tajpay 可以支持 bank代付
//            ChannelInfo channelInfo = mOnlinePayChannel.getPayoutChannel(cardType.getPayProductType());
//            if(channelInfo == null)
//            {
//                channelInfo = mOnlinePayChannel.getPayoutChannel(PayProductType.TAJPAY);
//            }
            ChannelInfo channelInfo = null;
            if (channelId > 0) {
                channelInfo = mChannelService.findById(false, channelId);

            } else if (channelId == -1) {

                ICurrencyType targetPayCurrency = ICurrencyType.getType(remarkPayoutInfo.getString(UserWithdrawVO.KEY_CURRENCY_TYPE));
                if(StringUtils.isEmpty(remarkPayoutInfo.getString(UserWithdrawVO.KEY_TRANSFER_TYPE)))
                {
                    targetPayCurrency = currencyType;
                }
                else
                {
                    BigDecimal payoutAmount = amount.subtract(feemoney);
                    BigDecimal transferAmount = mFiatExchangeManager.doConvert(ICurrencyType.getSupportCurrency(), targetPayCurrency, payoutAmount);
                    if(transferAmount == null)
                    {
                        return SystemErrorResult.ERR_SYS_OPT_FORBID;
                    }
                    remarkPayoutInfo.put(UserWithdrawVO.KEY_TRANSFER_AMOUNT, transferAmount);
                }

                if(targetPayCurrency == null)
                {
                    return SystemErrorResult.ERR_PARAMS;
                }
                channelInfo = mOnlinePayChannel.getPayoutChannel(PayProductType.TAJPAY, targetPayCurrency);

            }
            if (channelInfo == null) {
                return ChannelErrorResult.ERR_CANNEL;
            }
            remarkPayoutInfo.put(RemarkVO.KEY_CHANNEL_ID, channelInfo.getId());
            remarkPayoutInfo.put(RemarkVO.KEY_CHANNEL_NAME, channelInfo.getName());

            // 银行卡信息
            remarkPayoutInfo.put(RemarkVO.KEY_BANK_NAME, channelInfo.getSecretInfo().getString(RemarkVO.KEY_BANK_NAME));
            remarkPayoutInfo.put(RemarkVO.KEY_BANK_CODE, channelInfo.getSecretInfo().getString(RemarkVO.KEY_BANK_CODE));
            remarkPayoutInfo.put(RemarkVO.KEY_BANK_ACCOUNT, channelInfo.getSecretInfo().getString(RemarkVO.KEY_BANK_ACCOUNT));

            UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());
            remarkPayoutInfo.put("userType", userType.getKey());

            UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());
            Date createtime = new Date();

            FundAccountType accountType = FundAccountType.Spot;
            if (currencyType == null) {
                currencyType = ICurrencyType.getSupportCurrency();
            }

            String orderno = mWithdrawOrderService.createOrder(accountType, currencyType, userInfo, userAttr, channelInfo.getProduct(), amount, feemoney, createtime, remarkPayoutInfo, account, idcard, channelInfo);

            // 扣款
            ErrorResult payErrorResult = payManager.doUserWithdraw(accountType, currencyType, businessType, orderno, userInfo, amount, feemoney, null);
            if (payErrorResult == SystemErrorResult.SUCCESS) {

                OrderTxStatus txStatus = OrderTxStatus.AUDIT;
                if(userType == UserInfo.UserType.TEST)
                {
                    // 测试号提现直接成功
                    txStatus = OrderTxStatus.REALIZED;
                }
                // 变成审核中状态
                mWithdrawOrderService.updateTxStatus(orderno, txStatus, null, null, null);
            } else {
                // 错误
                remarkPayoutInfo.put(UserWithdrawVO.KEY_MSG, "Current : " + payErrorResult.getError());
                mWithdrawOrderService.updateTxStatus(orderno, OrderTxStatus.FAILED, null, null, remarkPayoutInfo);
            }

            // 清除缓存
            mWithdrawOrderService.clearUserQueryPageCache(userInfo.getId(), true);

            return payErrorResult;
        } catch (Exception e) {
            LOG.error("createWithdrawOrder error:", e);
        }
        return SystemErrorResult.ERR_SYS_OPT_FAILURE;
    }

    public ErrorResult passWithdrawOrderToWaiting(String orderno, String checker) {
        //总后台和员工后台同时点通过
        synchronized (orderno) {


            // 通道维护
//        ChannelInfo channelInfo = mOnlinePayChannel.getPayoutChannel();
//        if(channelInfo == null)
//        {
//            return ChannelErrorResult.ERR_CANNEL;
//        }

            WithdrawOrder orderInfo = mWithdrawOrderService.findByNo(orderno);
            if (orderInfo == null) {
                return SystemErrorResult.ERR_EXIST_NOT;
            }
            PayProductType productType = PayProductType.getType(orderInfo.getPayProductType());
            FundAccountType accountType = FundAccountType.getType(orderInfo.getFundKey());
            ICurrencyType currencyType = ICurrencyType.getType(orderInfo.getCurrency());

            OrderTxStatus txStatus = OrderTxStatus.getType(orderInfo.getStatus());
            if (!(txStatus == OrderTxStatus.AUDIT || txStatus == OrderTxStatus.PENDING)) {
                return SystemErrorResult.ERR_SYS_OPT_FINISHED;
            }

            UserInfo userInfo = mUserService.findByUsername(false, orderInfo.getUsername());
            if (userInfo == null) {
                return SystemErrorResult.ERR_EXIST_NOT;
            }

            UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());
            if(userType != UserInfo.UserType.MEMBER)
            {
                mWithdrawOrderService.updateTxStatus(orderInfo.getNo(), OrderTxStatus.REALIZED, null, null, null);
                return SystemErrorResult.SUCCESS;
            }

            RemarkVO remarkVO = orderInfo.getRemarkVO();
            long channelid = remarkVO.getIntValue(RemarkVO.KEY_CHANNEL_ID);
            ChannelInfo channelInfo = null;
            if(productType == PayProductType.TAJPAY)
            {
                RemarkVO remarkPayoutInfo = orderInfo.getRemarkVO();
                ICurrencyType targetPayCurrency = ICurrencyType.getType(remarkPayoutInfo.getString(UserWithdrawVO.KEY_CURRENCY_TYPE));
                // USDT: bank-currency != sysCurrency 有这个值， 其它没有
                // BRL:

                if(UserWithdrawVO.VALUE_TRANSFER_TYPE.equalsIgnoreCase(remarkPayoutInfo.getString(UserWithdrawVO.KEY_TRANSFER_TYPE)))
                {
                }
                else  if(targetPayCurrency != null)
                {
                }
                else
                {
                    targetPayCurrency = ICurrencyType.getSupportCurrency();
                }

                channelInfo = mOnlinePayChannel.getPayoutChannel(PayProductType.TAJPAY, targetPayCurrency);
                if(channelInfo == null)
                {
                    return ChannelErrorResult.ERR_CANNEL;
                }
                remarkVO.put(RemarkVO.KEY_CHANNEL_ID, channelInfo.getId());
            }
            else
            {
                channelInfo = mChannelService.findById(false, channelid);
            }

//        if(channelid <= 0)
//        {
//            ChannelInfo channelInfo = mOnlinePayChannel.getPayoutChannel();
//            if(channelInfo == null)
//            {
//                return ChannelErrorResult.ERR_CANNEL;
//            }
//            remarkVO.put(RemarkVO.KEY_CHANNEL_ID, channelInfo.getId());
//            remarkVO.put(RemarkVO.KEY_CHANNEL_NAME, channelInfo.getName());
//
//            // 银行卡信息
//            remarkVO.put(RemarkVO.KEY_BANK_NAME, channelInfo.getSecretInfo().getString(RemarkVO.KEY_BANK_NAME));
//            remarkVO.put(RemarkVO.KEY_BANK_CODE, channelInfo.getSecretInfo().getString(RemarkVO.KEY_BANK_CODE));
//            remarkVO.put(RemarkVO.KEY_BANK_ACCOUNT, channelInfo.getSecretInfo().getString(RemarkVO.KEY_BANK_ACCOUNT));
//        }

            if(channelInfo == null)
            {
                return ChannelErrorResult.ERR_CANNEL;
            }

            boolean refundToAgentWallet = false;
            if(mAgentWalletManager.checkValid(userInfo))
            {
                ErrorResult deductAgentResult = mAgentWalletManager.withdraw(userInfo, orderInfo);
                if(deductAgentResult != SystemErrorResult.SUCCESS)
                {
                    return deductAgentResult;
                }
                refundToAgentWallet = true;
            }

            // 更新状态为等待提交
            if (txStatus == OrderTxStatus.AUDIT) {
                mWithdrawOrderService.updateTxStatus(orderInfo.getNo(), OrderTxStatus.PENDING, null, checker, remarkVO);
            }

            try {
//            RemarkVO remarkVO = orderInfo.getRemarkVO();
//            long channelid = remarkVO.getIntValue(RemarkVO.KEY_CHANNEL_ID);

                // 打款金额
//            BigDecimal settleAmount = orderInfo.getAmount().subtract(orderInfo.getFeemoney());
                PayoutResult payoutResult = PaymentProcessorManager.getIntance().createPayout(channelInfo, orderInfo);

//                payoutResult = PayoutResult.WAITING_RESULT;

                if (payoutResult == null) {
                    refundToAgentWallet = false;
                    return SystemErrorResult.ERR_SYS_MAINTAINED;
                }

                if (payoutResult.getmTxStatus() == OrderTxStatus.WAITING) {
                    refundToAgentWallet = false;
                    // 然后等待上游给我们回调
                    mWithdrawOrderService.updateTxStatus(orderInfo.getNo(), OrderTxStatus.WAITING, payoutResult.getmPayoutId(), checker, null);
                    return SystemErrorResult.SUCCESS;
                }
                else if (payoutResult.getmTxStatus() == OrderTxStatus.FAILED){
//                RemarkVO remarkVO = orderInfo.getRemarkVO();
                    remarkVO.setMesage("通道错误(提交): " + payoutResult.getErrorMsg());

                    boolean refundResult = mRefundManager.doWithdrawRefund(accountType, currencyType, userInfo, orderInfo.getNo(), orderInfo.getAmount(), orderInfo.getFeemoney(), checker, payoutResult.getErrorMsg());
                    if (refundResult) {
                        // 更新提现订单为失败
                        RemarkVO errorRemark = orderInfo.getRemarkVO();
                        if (!StringUtils.isEmpty(payoutResult.getErrorMsg())) {
                            errorRemark.setMesage("通道错误(提交): " + payoutResult.getErrorMsg());
                        }
                        mWithdrawOrderService.updateTxStatus(orderInfo.getNo(), OrderTxStatus.FAILED, payoutResult.getmPayoutId(), checker, errorRemark);
                    }

                    refundToAgentWallet = true;
                }
                 else
                {
                    remarkVO.setMesage("通道错误(提交): " + payoutResult.getErrorMsg());
                    // 更新提现订单为失败
                    RemarkVO errorRemark = orderInfo.getRemarkVO();
                    if (!StringUtils.isEmpty(payoutResult.getErrorMsg())) {
                        errorRemark.setMesage(payoutResult.getErrorMsg());
                    }
                    mWithdrawOrderService.updateTxStatus(orderInfo.getNo(), null, payoutResult.getmPayoutId(), checker, errorRemark);
                    refundToAgentWallet = true;
                }

                if(SystemRunningMode.isCryptoMode())
                {
                    return SystemErrorResult.ERR_NATIVE_OR_TOKEN20;
                }
                else
                {
                    return SystemErrorResult.THIRD_OR_BANK_PAYOUT_SYSTEM_ERROR;
                }

            } finally {
                // 清除缓存
                mWithdrawOrderService.clearUserQueryPageCache(userInfo.getId(), true);

                if(refundToAgentWallet && mAgentWalletManager.checkValid(userInfo))
                {
                    // refund to agent
                    mAgentWalletManager.refund(orderInfo);
                }
            }

        }
    }

    public ErrorResult doWithdrawSuccess(String orderno, String outTradeNo, String checker)
    {
        return doWithdrawSuccess(orderno, outTradeNo, checker, false);
    }

    public ErrorResult doWithdrawSuccess(String orderno, String outTradeNo, String checker, boolean forceRealized) {
        WithdrawOrder orderInfo = mWithdrawOrderService.findByNo(orderno);
        if (orderInfo == null) {
            return SystemErrorResult.ERR_EXIST_NOT;
        }

        OrderTxStatus txStatus = OrderTxStatus.getType(orderInfo.getStatus());
        if(!forceRealized && txStatus != OrderTxStatus.WAITING) {
            return SystemErrorResult.ERR_SYS_OPT_FINISHED;
        }

        UserInfo userInfo = mUserService.findByUsername(false, orderInfo.getUsername());

        try {
            mWithdrawOrderService.updateTxStatus(orderInfo.getNo(), OrderTxStatus.REALIZED, outTradeNo, checker, null);

            // 扣除提现额度
            mWithdrawlLimitManager.decreAmount(userInfo, orderInfo.getAmount());
            return SystemErrorResult.SUCCESS;
        } catch (Exception e) {
            return SystemErrorResult.ERR_SYS_OPT_FAILURE;
        } finally {
            // 清除缓存
            mWithdrawOrderService.clearUserQueryPageCache(userInfo.getId(), false);
        }
    }

    public ErrorResult refuseWithdrawOrder(String orderno, String errmsg, String checker)
    {
        return refuseWithdrawOrder(false, orderno, errmsg, checker);
    }

    public ErrorResult refuseWithdrawOrder(boolean isFromTajpayCallback, String orderno, String errmsg, String checker) {
        WithdrawOrder orderInfo = mWithdrawOrderService.findByNo(orderno);
        if (orderInfo == null) {
            return SystemErrorResult.ERR_EXIST_NOT;
        }
        FundAccountType accountType = FundAccountType.getType(orderInfo.getFundKey());
        ICurrencyType currencyType = ICurrencyType.getType(orderInfo.getCurrency());

        OrderTxStatus txStatus = OrderTxStatus.getType(orderInfo.getStatus());
        if (!(txStatus == OrderTxStatus.AUDIT || txStatus == OrderTxStatus.PENDING || txStatus == OrderTxStatus.WAITING || txStatus == OrderTxStatus.REFUNDING)) {
            return SystemErrorResult.ERR_SYS_OPT_FINISHED;
        }

        if (txStatus == OrderTxStatus.FAILED || txStatus == OrderTxStatus.REALIZED) {
            return SystemErrorResult.ERR_SYS_OPT_FORBID;
        }

        // 更新错误信息
        RemarkVO remarkVO = orderInfo.getRemarkVO();
        if (!StringUtils.isEmpty(errmsg)) {
            remarkVO.setMesage(errmsg);
        }
        errmsg = remarkVO.getString(RemarkVO.KEY_MSG);

        if(isFromTajpayCallback)
        {
            mWithdrawOrderService.changeTxStatusToAudit(orderInfo, remarkVO);
            return SystemErrorResult.SUCCESS;
        }

        // 更新提现订单为失败
        if (StringUtils.isEmpty(errmsg)) {

            if(isFromTajpayCallback)
            {
                remarkVO.setMesage("通道错误(回调)!");
            }
            else
            {
                remarkVO.setMesage("Audit refuse!");
            }

        }
        remarkVO.put("isFromTajpayCallback", isFromTajpayCallback);

        // 检查人
        if (StringUtils.isEmpty(checker) && !StringUtils.isEmpty(orderInfo.getChecker())) {
            checker = orderInfo.getChecker();
        }

        // 先变更为退款中状态
        if (txStatus != OrderTxStatus.REFUNDING) {
            mWithdrawOrderService.updateTxStatus(orderInfo.getNo(), OrderTxStatus.REFUNDING, null, checker, remarkVO);
        }

        UserInfo userInfo = mUserService.findByUsername(false, orderInfo.getUsername());
        try {
            boolean refundResult = mRefundManager.doWithdrawRefund(accountType, currencyType, userInfo, orderInfo.getNo(), orderInfo.getAmount(), orderInfo.getFeemoney(), checker, errmsg);
            if (refundResult) {
                mWithdrawOrderService.updateTxStatus(orderInfo.getNo(),OrderTxStatus.FAILED , null, checker, remarkVO);//OrderTxStatus.FAILED
                // 必须是已经提交的
                if(mAgentWalletManager.checkValid(userInfo))
                {
                    mAgentWalletManager.refund(orderInfo);
                }
                return SystemErrorResult.SUCCESS;
            }

        } finally {
            // 清除缓存
            mWithdrawOrderService.clearUserQueryPageCache(userInfo.getId(), true);
        }

        return SystemErrorResult.ERR_SYS_OPT_FAILURE;
    }


    /**
     * 获取提现费率
     *
     * @param userid
     * @return
     */
    private BigDecimal getWithdrawFeerate(long userid) {
        BigDecimal feerate = mConfigService.getBigDecimal(false, PlatformConfig.ADMIN_APP_PLATFORM_USER_WITHDRAW_FEERATE);
        if (SystemRunningMode.isFundsMode()) {
            String discountFeerateKey = PlarformConfig2.ADMIN_APP_PLATFORM_USER_WITHDRAW_MAX_DISCOUNT_FEE_RATE.getKey();
            BigDecimal discountFeerate = mConfigService.getBigDecimal(false, discountFeerateKey);

            // 大于0才作用
            if (discountFeerate.compareTo(BigDecimal.ZERO) > 0) {
                DateTime dateTime = new DateTime();
                // 成功邀请一个人少一个点，最多7个点
                int buyCount = TodayInviteFriendHelper.getTodayRegAndBuyVipCount(dateTime, userid);
                if (buyCount >= discountFeerate.intValue()) {
                    buyCount = discountFeerate.intValue();
                }

                feerate = feerate.subtract(new BigDecimal(buyCount));
                if (feerate.compareTo(BigDecimal.ZERO) <= 0) {
                    // 小于0则异常
                    return null;
                }
            }
        }

        feerate = feerate.divide(BigDecimalUtils.DEF_100);
        return feerate;
    }


    public void test1()
    {
        FundAccountType accountType = FundAccountType.Spot;
        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
        BusinessType rechargeBusinessType = BusinessType.USER_RECHARGE;
        String orderno = System.currentTimeMillis() + "";

        String username = "eptes_gmail";
        UserInfo userInfo = mUserService.findByUsername(false, username);

        BigDecimal amount = new BigDecimal(100);

        ErrorResult errorResult = payManager.doUserRecharge(accountType, currencyType, rechargeBusinessType, orderno, userInfo, amount, null);

    }

    public static void testRun()
    {
        UserPayManager mgr = SpringContextUtils.getBean(UserPayManager.class);
        mgr.test1();
    }

}