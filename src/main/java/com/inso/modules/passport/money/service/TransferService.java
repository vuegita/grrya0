package com.inso.modules.passport.money.service;

import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.money.model.UserMoney;

import java.math.BigDecimal;
import java.util.Date;

public interface TransferService {


    public void doTransferInSelf(String orderno, FundAccountType fromAccountType, FundAccountType toAccountType, ICurrencyType currencyType,
                                 UserMoney fromUserMoney, BigDecimal fromNewBalance,
                                 String outTradeNo, UserInfo userInfo, UserAttr userAttr, BigDecimal amount, Date createtime);


}
