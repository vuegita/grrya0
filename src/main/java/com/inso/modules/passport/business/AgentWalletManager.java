package com.inso.modules.passport.business;

import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.modules.common.config.PlarformConfig2;
import com.inso.modules.common.model.*;
import com.inso.modules.passport.business.model.AgentWalletOrderInfo;
import com.inso.modules.passport.business.model.RechargeOrder;
import com.inso.modules.passport.business.model.WithdrawOrder;
import com.inso.modules.passport.business.service.AgentWalletOrderService;
import com.inso.modules.passport.money.PayApiManager;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.paychannel.model.ChannelInfo;
import com.inso.modules.paychannel.service.ChannelService;
import com.inso.modules.web.SystemRunningMode;
import com.inso.modules.web.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AgentWalletManager {

    private static Log LOG = LogFactory.getLog(AgentWalletManager.class);

    @Autowired
    private AgentWalletOrderService mAgentWalletOrderService;

    @Autowired
    private ChannelService mChannelService;

    @Autowired
    private PayApiManager mPayApiManager;

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private ConfigService mConfigService;

    private long mLastRefreshTime = -1;

    /*** 开启代理提现权限-1分钟刷新 ***/
    private boolean enableAgentWithdrawAll = false;

    private void reload()
    {
        long ts = System.currentTimeMillis();
        if(mLastRefreshTime > 0 && ts - mLastRefreshTime <= 60_000)
        {
            return;
        }
        this.mLastRefreshTime = ts;
        String agentWithdrawPermission = mConfigService.getValueByKey(false, PlarformConfig2.ADMIN_PLATFORM_USER_WITHDRAW_CHECK_AGENT_SWITCH.getKey());
        this.enableAgentWithdrawAll = "enableAll".equalsIgnoreCase(agentWithdrawPermission);
    }

    public ErrorResult recharge(UserInfo userInfo, UserAttr userAttr, BigDecimal merchantMoney, RechargeOrder rechargeOrder, BigDecimal channelFeemoney)
    {
        try {
            BusinessType businessType = BusinessType.USER_RECHARGE;
            AgentWalletOrderInfo orderInfo = mAgentWalletOrderService.findByOutTradeNo(false, rechargeOrder.getNo(), businessType);

            UserInfo agentInfo = mUserService.findByUsername(false, userAttr.getAgentname());

            ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
            FundAccountType accountType = FundAccountType.Spot;

            BigDecimal realmoney = null;
            String orderno = null;
            String outTradeNo = rechargeOrder.getNo();
            if(orderInfo != null)
            {
                OrderTxStatus txStatus = OrderTxStatus.getType(orderInfo.getStatus());
                if(txStatus == OrderTxStatus.REALIZED)
                {
                    return SystemErrorResult.SUCCESS;
                }
                orderno = orderInfo.getNo();
                realmoney = orderInfo.getRealmoney();
            }
            else
            {
                ChannelInfo channelInfo = mChannelService.findById(false, rechargeOrder.getChannelid());

                BigDecimal orderAmount = rechargeOrder.getAmount();
                BigDecimal feemoney = BigDecimal.ZERO;

                if(channelInfo.getFeerate() != null && channelInfo.getFeerate().compareTo(BigDecimal.ZERO) > 0)
                {
                    // 系统
                    feemoney = orderAmount.multiply(channelInfo.getFeerate());
                    if(channelInfo.getExtraFeemoney() != null && channelInfo.getExtraFeemoney().compareTo(BigDecimal.ZERO) > 0)
                    {
                        feemoney = feemoney.add(channelInfo.getExtraFeemoney());
                    }

                    feemoney = BigDecimalUtils.getNotNull(feemoney);
                    realmoney = orderAmount.subtract(feemoney);
                }
                else
                {
                    // 外部系统
                    if(merchantMoney == null || merchantMoney.compareTo(BigDecimal.ZERO) <= 0)
                    {
                        return SystemErrorResult.ERR_SYS_OPT_FORBID;
                    }

                    feemoney = BigDecimalUtils.getNotNull(channelFeemoney);
                    realmoney = merchantMoney;
                }


                orderno = mAgentWalletOrderService.addOrder(businessType, outTradeNo, agentInfo, channelInfo, orderAmount, feemoney, currencyType, realmoney);
            }

            RemarkVO remarkVO = RemarkVO.create("Add agent balance by user recharge for " + rechargeOrder.getUsername());

            ErrorResult result = mPayApiManager.doUserRecharge(accountType, currencyType, businessType, orderno, agentInfo, realmoney, remarkVO);
            if(result == SystemErrorResult.SUCCESS)
            {
                mAgentWalletOrderService.updateTxStatus(orderno, OrderTxStatus.REALIZED, null, null, outTradeNo, businessType);
                return SystemErrorResult.SUCCESS;
            }
        } catch (Exception e) {
            LOG.error("handle recharge error:", e);
        }
        return SystemErrorResult.ERR_SYS_OPT_FAILURE;
    }

    public ErrorResult withdraw(UserInfo userInfo, WithdrawOrder withdrawOrder)
    {
        try {

            BusinessType businessType = BusinessType.USER_WITHDRAW;
            AgentWalletOrderInfo orderInfo = mAgentWalletOrderService.findByOutTradeNo(false, withdrawOrder.getNo(), businessType);

            UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());
            UserInfo agentInfo = new UserInfo();
            agentInfo.setId(userAttr.getAgentid());
            agentInfo.setName(userAttr.getAgentname());

            ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
            FundAccountType accountType = FundAccountType.Spot;

            BigDecimal realmoney = null;
            BigDecimal feemoney = BigDecimal.ZERO;
            String orderno = null;
            String outTradeNo = withdrawOrder.getNo();
            if(orderInfo != null)
            {
                OrderTxStatus txStatus = OrderTxStatus.getType(orderInfo.getStatus());
                if(txStatus == OrderTxStatus.REALIZED)
                {
                    return SystemErrorResult.SUCCESS;
                }
                orderno = orderInfo.getNo();
                realmoney = orderInfo.getRealmoney();
                feemoney = orderInfo.getFeemoney();
            }
            else
            {
                ChannelInfo channelInfo = mChannelService.findById(false, withdrawOrder.getChannelid());

                BigDecimal orderAmount = withdrawOrder.getAmount().subtract(withdrawOrder.getFeemoney());
                if(channelInfo.getFeerate() != null && channelInfo.getFeerate().compareTo(BigDecimal.ZERO) > 0)
                {
                    feemoney = orderAmount.multiply(channelInfo.getFeerate());
                }
                if(channelInfo.getExtraFeemoney() != null && channelInfo.getExtraFeemoney().compareTo(BigDecimal.ZERO) > 0)
                {
                    feemoney = feemoney.add(channelInfo.getExtraFeemoney());
                }
                realmoney = orderAmount.add(feemoney);

                orderno = mAgentWalletOrderService.addOrder(businessType, withdrawOrder.getNo(), agentInfo, channelInfo, orderAmount, feemoney, currencyType, realmoney);
            }

            RemarkVO remarkVO = RemarkVO.create("Deduct agent balance by user withdraw for " + withdrawOrder.getUsername());

            ErrorResult result = mPayApiManager.doUserWithdraw(accountType, currencyType, businessType, orderno, agentInfo, realmoney, feemoney, remarkVO);
            if(result == SystemErrorResult.SUCCESS)
            {
                mAgentWalletOrderService.updateTxStatus(orderno, OrderTxStatus.REALIZED, null, null, outTradeNo, businessType);
                return SystemErrorResult.SUCCESS;
            }
            return result;
        } catch (Exception e) {
            LOG.error("handle withdraw error:", e);
        }
        return SystemErrorResult.ERR_SYS_OPT_FAILURE;
    }


    public ErrorResult refund(WithdrawOrder withdrawOrder)
    {
        try {
            AgentWalletOrderInfo withdrawOrderInfo = mAgentWalletOrderService.findByOutTradeNo(false, withdrawOrder.getNo(), BusinessType.USER_WITHDRAW);
            if(withdrawOrderInfo == null || !OrderTxStatus.REALIZED.getKey().equalsIgnoreCase(withdrawOrderInfo.getStatus()))
            {
                //
                return SystemErrorResult.ERR_SYS_OPT_FORBID;
            }

            BusinessType businessType = BusinessType.REFUND;
            AgentWalletOrderInfo orderInfo = mAgentWalletOrderService.findByOutTradeNo(false, withdrawOrder.getNo(), businessType);

            ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
            FundAccountType accountType = FundAccountType.Spot;

            UserAttr userAttr = mUserAttrService.find(false, withdrawOrder.getUserid());
            UserInfo agentInfo = new UserInfo();
            agentInfo.setId(userAttr.getAgentid());
            agentInfo.setName(userAttr.getAgentname());

            String orderno = null;
            String outTradeNo = withdrawOrderInfo.getOutTradeNo();
            BigDecimal realmoney = withdrawOrderInfo.getRealmoney();
            BigDecimal feemoney = withdrawOrderInfo.getFeemoney();

            if(orderInfo != null)
            {
                OrderTxStatus txStatus = OrderTxStatus.getType(orderInfo.getStatus());
                if(txStatus == OrderTxStatus.REALIZED)
                {
                    return SystemErrorResult.SUCCESS;
                }
                orderno = orderInfo.getNo();
            }
            else
            {
                orderno = mAgentWalletOrderService.addOrder(businessType, withdrawOrder.getNo(), agentInfo, null, realmoney, feemoney, currencyType, realmoney);
            }

            RemarkVO remarkVO = RemarkVO.create("Refund to agent balance by " + withdrawOrder.getUsername());
            ErrorResult result = mPayApiManager.doRefund(accountType, currencyType, businessType, orderno, agentInfo, realmoney, feemoney, remarkVO);
            if(result == SystemErrorResult.SUCCESS)
            {
                mAgentWalletOrderService.updateTxStatus(orderno, OrderTxStatus.REALIZED, null, null, outTradeNo, businessType);
                return SystemErrorResult.SUCCESS;
            }
        } catch (Exception e) {
            LOG.error("handle withdraw error:", e);
        }
        return SystemErrorResult.ERR_SYS_OPT_FAILURE;
    }

    public boolean checkValid(UserInfo userInfo)
    {
//        if(true || SystemRunningMode.getSystemConfig() == SystemRunningMode.CRYPTO)
//        {
//            return false;
//        }

        reload();

        if(!enableAgentWithdrawAll)
        {
            return false;
        }

        if(UserInfo.DEFAULT_SYSTEM_ACCOUNT.equalsIgnoreCase(userInfo.getName()))
        {
            return false;
        }

        UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());
        if(userType != UserInfo.UserType.MEMBER)
        {
            return false;
        }
        return true;
    }

}
