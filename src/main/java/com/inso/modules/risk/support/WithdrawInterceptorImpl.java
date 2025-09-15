package com.inso.modules.risk.support;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.alibaba.fastjson.JSONObject;
import com.inso.modules.common.config.SystemConfig;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.money.model.UserMoney;
import com.inso.modules.risk.BaseRiskSupport;

public class WithdrawInterceptorImpl extends BaseRiskSupport {

    /*** 当前提现金额 ***/
    public static String KEY_WITHDRAW_AMOUNT = "withdrawAmount";


    @Override
    public boolean doVerify(UserInfo userInfo, JSONObject data) {

        FundAccountType accountType = FundAccountType.Spot;
        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
        BigDecimal currentWithdrawAmount = data.getBigDecimal(KEY_WITHDRAW_AMOUNT);
        UserMoney userMoney = moneyService.findMoney(false, userInfo.getId(), accountType, currencyType);
        BigDecimal multiple = mConfigService.getBigDecimal(false, SystemConfig.RISK_USER_WITHDRAW_TRIGER_LIMIT_CODEAMOUNT_MULTIPLE_NUMBER.getKey());

        if(multiple.compareTo(BigDecimal.ZERO) <= 0)
        {
            return true;
        }

        if(userMoney.getTotalRecharge().compareTo(BigDecimal.ZERO) <= 0)
        {
            // 充值=0就进入

//            // 充值为0禁止提现
//            boolean enableWithdraw = mConfigService.getBoolean(false, SystemConfig.RISK_USER_WITHDRAW_ENABLE_WHEN_RECHARGE_0.getKey());
//            if(!enableWithdraw)
//            {
//                return false;
//            }

            // 充值为0时倍数
            boolean enableCodeWhenRecharge0 = mConfigService.getBoolean(false, SystemConfig.RISK_USER_WITHDRAW_TRIGER_IMPL_WHEN_RECHARGE_0.getKey());
            if(enableCodeWhenRecharge0)
            {
                BigDecimal codeAmount = userMoney.getBalance().multiply(multiple).add(userMoney.getCodeAmount());
                updateCodeAmount(userInfo, accountType, currencyType, codeAmount);
                return false;
            }
        }
        else
        {
            // 提现/充值总额 限制
            long withdrawDivideRechargeLimit = mConfigService.getLong(false, SystemConfig.RISK_USER_WITHDRAW_TRIGER_IMPL_WITHDRAW_DIVIDE_RECHARGE_MULTIPLE_NUMBER.getKey());
            if(withdrawDivideRechargeLimit > 0)
            {
                BigDecimal totalWithdrawAmount = currentWithdrawAmount.add(userMoney.getTotalWithdraw()).subtract(userMoney.getTotalRefund());
                long rs = totalWithdrawAmount.divide(userMoney.getTotalRecharge(), RoundingMode.HALF_UP).longValue();
                if(rs >= withdrawDivideRechargeLimit)
                {
                    BigDecimal codeAmount = userMoney.getBalance().multiply(multiple).add(userMoney.getCodeAmount());
                    updateCodeAmount(userInfo, accountType, currencyType, codeAmount);
                    return false;
                }
            }

            // 余额 / 充值总额 限制
            long balanceDivideRechargeLimit = mConfigService.getLong(false, SystemConfig.RISK_USER_WITHDRAW_TRIGER_IMPL_BALANCE_DIVIDE_RECHARGE_MULTIPLE_NUMBER.getKey());
            if(balanceDivideRechargeLimit > 0)
            {
                long rs = userMoney.getBalance().divide(userMoney.getTotalRecharge(), RoundingMode.HALF_UP).longValue();
                if(rs >= balanceDivideRechargeLimit)
                {
                    BigDecimal codeAmount = userMoney.getBalance().multiply(multiple).add(userMoney.getCodeAmount());
                    updateCodeAmount(userInfo, accountType, currencyType, codeAmount);
                    return false;
                }
            }
        }

        return true;
    }


}
