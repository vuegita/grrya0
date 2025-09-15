package com.inso.modules.game.red_package.logical;

import java.math.BigDecimal;

import com.inso.modules.admin.helper.AgentAccountHelper;
import com.inso.modules.common.config.PlarformConfig2;
import com.inso.modules.common.model.*;
import com.inso.modules.passport.UserErrorResult;
import com.inso.modules.passport.money.model.UserMoney;
import com.inso.modules.passport.money.service.UserMoneyService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.utils.RedPackageUtils;
import com.inso.modules.game.red_package.helper.RedPOrderIdHelper;
import com.inso.modules.game.red_package.model.RedPCreatorType;
import com.inso.modules.game.red_package.model.RedPStaffLimit;
import com.inso.modules.game.red_package.model.RedPStaffStatus;
import com.inso.modules.game.red_package.model.RedPType;
import com.inso.modules.game.red_package.service.RedPPeriodService;
import com.inso.modules.game.red_package.service.RedPStaffLimitService;
import com.inso.modules.passport.money.PayApiManager;
import com.inso.modules.passport.business.RefundManager;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.service.ConfigService;

@Component
public class RedPGameManager {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private RedPPeriodService mRedEnveloperService;

    @Autowired
    private ConfigService mConfigService;

    @Autowired
    private PayApiManager mPayApiManager;

    @Autowired
    private RefundManager mRefundManager;

    @Autowired
    private RedPStaffLimitService mRedPStaffLimitService;

    @Autowired
    private UserMoneyService mUserMoneyService;

    /**
     * BigDecimal totalAmount = WebRequest.getBigDecimal("totalAmount");
     *         String typeString = WebRequest.getString("type");
     *         long totalCount = WebRequest.getLong("totalCount");
     *         int expires = WebRequest.getInt("expires");
     *
     *         RedpType type = RedpType.getType(typeString);
     */

    public void createBySystem(String remark, BigDecimal totalAmount, long totalCount, BigDecimal maxAmount, RedPType type, int expires, BigDecimal externalLimitMinxAmount ,String specifyUserName)
    {

        RemarkVO remarkVO = RemarkVO.create(remark);
        DateTime startTime = new DateTime();
        DateTime endTime = startTime.plusMinutes(expires);

        // 1. 创建红包
        long issue = mRedEnveloperService.addBySystem(type, totalAmount, totalCount, maxAmount, startTime, endTime, remarkVO);

        // 2. 写入缓存
        RedPGrabStatus periodStatus = RedPGrabStatus.loadCache(false, issue);
        periodStatus.setType(type);
        periodStatus.setStartTime(startTime.toDate());
        periodStatus.setEndTime(endTime.toDate());
        periodStatus.setMaxAmount(maxAmount);
        periodStatus.setTotalRPCount(totalCount);
        periodStatus.setTotalRPAmount(totalAmount);
        periodStatus.setCreatorType(RedPCreatorType.SYS);
        periodStatus.setExternalLimitMinAmount(externalLimitMinxAmount);
        periodStatus.setSpecifyUserName(specifyUserName);
        periodStatus.saveCache();
    }

    public ErrorResult createByAgent(UserInfo agentOrStaffInfo, BigDecimal totalAmount, long totalCount, BigDecimal maxAmount, RedPType type, int expires,String specifyUserName)
    {
        FundAccountType accountType = FundAccountType.Spot;
        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();

        String agentWithdrawPermission = mConfigService.getValueByKey(false, PlarformConfig2.ADMIN_PLATFORM_USER_WITHDRAW_CHECK_AGENT_SWITCH.getKey());

        boolean enableAll = "enableAll".equalsIgnoreCase(agentWithdrawPermission);
        if(!enableAll)
        {
            UserMoney userMoney = mUserMoneyService.findMoney(false, AgentAccountHelper.getAdminAgentid(), accountType, currencyType);
            if(userMoney.getBalance().compareTo(totalAmount) < 0)
            {
                // 余额不足
                return UserErrorResult.ERR_PAY_NOT_ENOUGH_BALANCE;
            }
        }


        // 代理账户
        UserInfo walletUserInfo = agentOrStaffInfo;
        UserInfo createUserInfo = agentOrStaffInfo;

        DateTime startTime = new DateTime();

        UserInfo.UserType userType = UserInfo.UserType.getType(agentOrStaffInfo.getType());

        // 员工发送红包有限制
        boolean isAgent = true;
        RedPStaffStatus staffStatus = null;
        if(userType == UserInfo.UserType.STAFF)
        {
            isAgent = false;
            RedPStaffLimit limitInfo = mRedPStaffLimitService.findByStaffId(false, agentOrStaffInfo.getId());

            if(!Status.ENABLE.getKey().equalsIgnoreCase(limitInfo.getStatus()))
            {
                // 被禁用发送红包
                return SystemErrorResult.ERR_DISABLE;
            }

            // 员工发送红包有限制
            staffStatus = RedPStaffStatus.loadCache(agentOrStaffInfo.getId());
            staffStatus.setMaxMoneyOfSingle(limitInfo.getMaxMoneyOfSingle());
            staffStatus.setMaxMoneyOfDay(limitInfo.getMaxMoneyOfDay());
            staffStatus.setMaxCountOfDay(limitInfo.getMaxCountOfDay());

            ErrorResult  result = staffStatus.verify(totalAmount);
            if(result != SystemErrorResult.SUCCESS)
            {
                return result;
            }


            long agentid = agentOrStaffInfo.getAgentid();
            UserAttr staffAttr = mUserAttrService.find(false, agentid);
            walletUserInfo = mUserService.findByUsername(false, staffAttr.getUsername());
        }

        DateTime endTime = startTime.plusMinutes(expires);
        String orderno = RedPOrderIdHelper.nextPeriodOrderId();

        RemarkVO remark = null;
        RedPCreatorType creatorType = RedPCreatorType.AGENT;
        if(isAgent)
        {
            remark = RemarkVO.create( "代理 【" +  walletUserInfo.getName() + "】 发红包!");
        }
        else
        {
            creatorType = RedPCreatorType.STAFF;
            remark = RemarkVO.create("员工 【" + agentOrStaffInfo.getName() + "】 发红包!");
        }

        if(!enableAll)
        {
            // 如果没有走代理自己的钱包，则需要验证
            ErrorResult errorResult = mPayApiManager.doBusinessDeduct(accountType, currencyType, BusinessType.GAME_RED_PACKAGE, orderno, walletUserInfo, totalAmount, BigDecimal.ZERO, remark);
            if(errorResult != SystemErrorResult.SUCCESS)
            {
                return errorResult;
            }
        }


        BigDecimal minAmount = RedPackageUtils.DEFAULT_MIN_AMOUNT;
        // 1. 创建红包
        long issue = mRedEnveloperService.addByMember(creatorType, type, orderno, createUserInfo, walletUserInfo.getId(), totalAmount, totalCount, minAmount, maxAmount, startTime, endTime, remark);

        // 2. 写入缓存
        RedPGrabStatus periodStatus = RedPGrabStatus.loadCache(false, issue);
        periodStatus.setType(type);
        periodStatus.setStartTime(startTime.toDate());
        periodStatus.setEndTime(endTime.toDate());
        periodStatus.setMaxAmount(maxAmount);
        periodStatus.setTotalRPCount(totalCount);
        periodStatus.setTotalRPAmount(totalAmount);
        periodStatus.setCreatorType(creatorType);
        periodStatus.setCreatorUserid(createUserInfo.getId());
        periodStatus.setExternalLimitMinAmount(BigDecimal.ZERO);
        periodStatus.setSpecifyUserName(specifyUserName);
        periodStatus.saveCache();

        // 如果不为空表示是员工发送红包
        if(staffStatus != null)
        {
            staffStatus.incre(totalAmount);
            staffStatus.saveCache();
        }

        return SystemErrorResult.SUCCESS;
    }

    public static void main(String[] args) {

        DateTime startTime = new DateTime();
        DateTime endTime = startTime.plusMinutes(100);

        BigDecimal totalAmount = new BigDecimal(100);
        BigDecimal maxAmount = new BigDecimal(10);

        long issue = 1;

        RedPType type = RedPType.SIMPLE;
        RedPGrabStatus periodStatus = RedPGrabStatus.loadCache(false, issue);
        periodStatus.setType(type);
        periodStatus.setStartTime(startTime.toDate());
        periodStatus.setEndTime(endTime.toDate());
        periodStatus.setMaxAmount(maxAmount);
        periodStatus.setTotalRPCount(4);
        periodStatus.setTotalRPAmount(totalAmount);
        periodStatus.setCreatorType(RedPCreatorType.AGENT);
        periodStatus.setCreatorUserid(1);

        periodStatus.decreRPAmount("u1");
        periodStatus.decreRPAmount("u2");
        periodStatus.decreRPAmount("u3");
        periodStatus.decreRPAmount("u4");

        ErrorResult re = periodStatus.verify("u5");
        periodStatus.decreRPAmount("u5");

    }

}
