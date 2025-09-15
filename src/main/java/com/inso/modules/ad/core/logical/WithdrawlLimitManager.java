package com.inso.modules.ad.core.logical;

import com.inso.framework.utils.BigDecimalUtils;
import com.inso.modules.ad.core.model.AdVipLimitInfo;
import com.inso.modules.ad.core.model.WithdrawlLimitInfo;
import com.inso.modules.ad.core.service.VipLimitService;
import com.inso.modules.ad.core.service.WithdrawlLimitService;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.model.UserVIPInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.passport.user.service.UserVIPService;
import com.inso.modules.web.SystemRunningMode;
import com.inso.modules.web.model.VIPType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class WithdrawlLimitManager {

    /*** 下级返提现额度比例: 如下级购买VIP-Baisc 则获取提现额度 1500 * 0.5 = 750 ***/
    public static final BigDecimal DEFAULT_LV1_REBATE_WITHDRAWL_RATE = new BigDecimal(0.4);
    /*** 自己购买VIP得 1.5倍提现额度 ***/
    public static final BigDecimal DEFAULT_SELF_REBATE_WITHDRAWL_RATE = new BigDecimal(0.88);

    @Autowired
    private WithdrawlLimitService mWithdrawlLimitService;

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private VipLimitService mVipLimitService;

    @Autowired
    private UserVIPService mUserVIPService;


    public WithdrawlLimitInfo findByUserid(boolean purge, UserInfo userInfo)
    {
        WithdrawlLimitInfo model = mWithdrawlLimitService.findByUserId(purge, userInfo.getId());
        if(model == null)
        {
            try {
                // 并发可能会错误
                mWithdrawlLimitService.add(userInfo, BigDecimal.ZERO);
            } catch (Exception e) {
            }
            model = mWithdrawlLimitService.findByUserId(purge, userInfo.getId());
        }
        return model;
    }

    /**
     *
     * @param userInfo
     * @param amount
     */
    public void increAmount(boolean isPresent, UserInfo userInfo, BigDecimal amount)
    {
        // 1. 先增加会员自己的额度|自己的额度全部添加
        BigDecimal selfAmount = amount;
        if(!isPresent)
        {
            selfAmount = amount.multiply(DEFAULT_SELF_REBATE_WITHDRAWL_RATE);
        }
        handleAmount(true, userInfo, selfAmount);

        // 2. 再增加上级的额度
        UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());
        if(userAttr.getParentid() <= 0)
        {
            return;
        }

//        BigDecimal parentAmount = amount.multiply(DEFAULT_LV1_REBATE_WITHDRAWL_RATE);

        UserVIPInfo parentVipInfo = mUserVIPService.findByUserId(false, userAttr.getParentid(), VIPType.AD);
        AdVipLimitInfo parentVipLimitInfo = mVipLimitService.findByVipId(false, parentVipInfo.getVipid());
        if(parentVipLimitInfo.getLv1RebateWithdrawlRate().compareTo(BigDecimal.ZERO) <= 0)
        {
            //
            return;
        }

        BigDecimal parentRebateRate = parentVipLimitInfo.getLv1RebateWithdrawlRate().divide(BigDecimalUtils.DEF_100, 3, RoundingMode.HALF_UP);
        BigDecimal parentAmount = amount.multiply(parentRebateRate);
        UserInfo parentInfo = mUserService.findByUsername(false, userAttr.getParentname());
        handleAmount(true, parentInfo, parentAmount);
    }

    /**
     * 提现成功时扣除
     * @param userInfo
     * @param amount
     */
    public void decreAmount(UserInfo userInfo, BigDecimal amount)
    {
        if(SystemRunningMode.isFundsMode())
        {
            handleAmount(false, userInfo, amount);
        }
    }

    private void handleAmount(boolean isIncre, UserInfo userInfo, BigDecimal amount)
    {
        synchronized (userInfo.getName())
        {
            WithdrawlLimitInfo limitInfo = findByUserid(false, userInfo);
            BigDecimal currentAmount = null;
            if(isIncre)
            {
                currentAmount = limitInfo.getAmount().add(amount);
            }
            else
            {
                currentAmount = limitInfo.getAmount().subtract(amount);
            }
            if(currentAmount.compareTo(BigDecimal.ZERO) <= 0)
            {
                currentAmount = BigDecimal.ZERO;
            }
            mWithdrawlLimitService.updateInfo(userInfo.getId(), currentAmount);
        }
    }

}
