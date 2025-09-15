package com.inso.modules.passport.money.service;

import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.BusinessType;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.passport.money.model.MoneyOrderType;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.money.model.UserMoney;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class TransferServiceImpl implements TransferService{


    @Autowired
    private UserMoneyService mUserMoneyService;

    @Autowired
    private MoneyOrderService moneyOrderService;


    @Transactional
    public void doTransferInSelf(String orderno, FundAccountType fromAccountType, FundAccountType toAccountType, ICurrencyType currencyType,
                                 UserMoney fromUserMoney, BigDecimal fromNewBalance,
                                 String outTradeNo, UserInfo userInfo, UserAttr userAttr, BigDecimal amount, Date createtime)
    {
        BusinessType businessType = BusinessType.USER_TRANSFER_TO_SELF;

        MoneyOrderType deductOrderType = MoneyOrderType.BUSINESS_DEDUCT;
        MoneyOrderType rechargeOrderType = MoneyOrderType.BUSINESS_RECHARGE;

        if(!StringUtils.isEmpty(orderno))
        {
            // deduct
            moneyOrderService.createOrder(fromAccountType, currencyType, orderno, outTradeNo, userInfo, userAttr, businessType, deductOrderType, amount, null, createtime, null);

            // recharge
            moneyOrderService.createOrder(toAccountType, currencyType, orderno, outTradeNo, userInfo, userAttr, businessType, rechargeOrderType, amount, null, createtime, null);
        }

        // deduct: update txstatus
        moneyOrderService.updateToRealized(fromAccountType, currencyType, businessType, deductOrderType, outTradeNo, userInfo, amount, null, fromNewBalance, false, createtime, fromUserMoney, null);

        // recharge: update txstatus
        UserMoney toUserMoney = mUserMoneyService.findMoney(false, userInfo.getId(), toAccountType, currencyType);
        BigDecimal toNewBalance = toUserMoney.getBalance().add(amount);
        moneyOrderService.updateToRealized(toAccountType, currencyType, businessType, rechargeOrderType, outTradeNo, userInfo, amount, null, toNewBalance, false, createtime, toUserMoney, null);
    }


}
