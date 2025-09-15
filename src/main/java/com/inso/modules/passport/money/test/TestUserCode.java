package com.inso.modules.passport.money.test;

import com.inso.framework.bean.ErrorResult;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.modules.common.model.BusinessType;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.passport.money.PayApiManager;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

//@Component
public class TestUserCode {

    @Autowired
    private UserService mUserService;

    @Autowired
    private PayApiManager mApiApiManager;


    public void test1()
    {
        String username = "up9199999999992";

        UserInfo userInfo = mUserService.findByUsername(false, username);

        FundAccountType accountType = FundAccountType.Spot;
        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();

        BusinessType businessType = BusinessType.RECHARGE_PRESENTATION_BY_PERCENT;
        String outTradeNo = System.currentTimeMillis() + "";

        BigDecimal amount = new BigDecimal(10);


        // public ErrorResult doBusinessRecharge(FundAccountType accountType, ICurrencyType currencyType, BusinessType businessType, String outTradeNo, UserInfo userInfo, BigDecimal amount, JSONObject remark)
        ErrorResult errorResult = mApiApiManager.doBusinessRecharge(accountType, currencyType, businessType, outTradeNo, userInfo, amount, null);

        System.out.println(errorResult);
    }

    public void test2()
    {
        String username = "up9199999999992";

        UserInfo userInfo = mUserService.findByUsername(false, username);

        FundAccountType accountType = FundAccountType.Spot;
        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();

        BusinessType businessType = BusinessType.GAME_NEW_LOTTERY;
        String outTradeNo = System.currentTimeMillis() + "";

        BigDecimal amount = new BigDecimal(10);
        BigDecimal feemoney = new BigDecimal(1);


        //public ErrorResult doBusinessDeduct(FundAccountType accountType, ICurrencyType currencyType, BusinessType businessType, String outTradeNo, UserInfo userInfo, BigDecimal amount, BigDecimal feemoney, JSONObject remark)
        ErrorResult errorResult = mApiApiManager.doBusinessDeduct(accountType, currencyType, businessType, outTradeNo, userInfo, amount, null, null);

        System.out.println(errorResult);
    }

    public static void testRun()
    {
        TestUserCode testUserCode = SpringContextUtils.getBean(TestUserCode.class);
        testUserCode.test2();
    }

}
