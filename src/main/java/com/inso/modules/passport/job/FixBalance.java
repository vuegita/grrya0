package com.inso.modules.passport.job;

import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.FiatCurrencyType;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.passport.business.PlatformPayManager;
import com.inso.modules.passport.money.model.UserMoney;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class FixBalance {

    private static Log LOG = LogFactory.getLog(FixBalance.class);

    @Autowired
    private UserMoneyService mUserMoneyService;

    @Autowired
    private PlatformPayManager mPlatformPayManager;

    @Autowired
    private UserService mUserService;


    public void doFix()
    {

        LOG.info("start ============= ");
        FundAccountType accountType = FundAccountType.Spot;
        FiatCurrencyType srcCurrency = FiatCurrencyType.INR;
        ICurrencyType toCurrency = CryptoCurrency.USDT;
        String checker = "后台任务";
        String deductRemark = "Transfer INR to USDT for deduct";
        String addRemark = "Transfer INR to USDT for add";

        BigDecimal feerate = new BigDecimal(83);

        mUserService.queryAll(null, null, new Callback<UserInfo>() {
            @Override
            public void execute(UserInfo userInfo) {


                try {
                    UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());
                    if(userType != UserInfo.UserType.MEMBER)
                    {
                        return;
                    }

                    UserMoney userMoney = mUserMoneyService.findMoney(false, userInfo.getId(), accountType, srcCurrency);
                    if(userMoney == null || userMoney.getBalance().compareTo(BigDecimal.ZERO) <= 0)
                    {
                        return;
                    }

                    ErrorResult result = mPlatformPayManager.addDeduct(accountType, srcCurrency, userInfo, userMoney.getBalance(), checker, deductRemark);
                    if(result == SystemErrorResult.SUCCESS)
                    {
                        BigDecimal newBalance = userMoney.getBalance().divide(feerate, 2, RoundingMode.DOWN);
                        result = mPlatformPayManager.addPresentation(accountType, toCurrency, userInfo, newBalance, checker, addRemark);

                        if(result != SystemErrorResult.SUCCESS)
                        {
                            LOG.warn("addPresentation error for " + userInfo.getName() + ", money = " + newBalance);
                        }

                        return;
                    }
                    LOG.warn("addDeduct error for " + userInfo.getName() + ", money = " + userMoney.getBalance());
                } catch (Exception e) {

                    LOG.error("handle error:", e);
                }


            }
        });


        LOG.info("End ============= ");
    }


    public static void test()
    {
        FixBalance mgr = SpringContextUtils.getBean(FixBalance.class);
        mgr.doFix();
    }

}
