package com.inso.modules.passport.money;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.framework.zookeeper.DistributeLock;
import com.inso.framework.zookeeper.ZKClientManager;
import com.inso.modules.common.config.SystemConfig;
import com.inso.modules.common.helper.IdGenerator;
import com.inso.modules.common.model.BusinessType;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.UserErrorResult;
import com.inso.modules.passport.config.CodeAmountConfig;
import com.inso.modules.passport.money.model.MoneyOrder;
import com.inso.modules.passport.money.model.MoneyOrderType;
import com.inso.modules.passport.money.model.UserMoney;
import com.inso.modules.passport.money.service.MoneyOrderService;
import com.inso.modules.passport.money.service.TransferService;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.user.logical.UserBetReportLogical;
import com.inso.modules.passport.user.logical.UserRecentBetStatus;
import com.inso.modules.passport.user.model.MemberSubType;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.web.logical.ActiveUserManager;
import com.inso.modules.web.service.ConfigService;
import jodd.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/**
 * 支付Api管理器
 * 所有需要操作用户余额的操作都调用这个类
 *
 */
@Component
public class PayApiManager {

    private static final String UP_USER_BALANCE_PRE_PATH = "/inso/passport/paymanager/update_money_";

    private static Log LOG = LogFactory.getLog(PayApiManager.class);

    private static BigDecimal ZERO_AMOUNT = BigDecimal.ZERO;

    /*** 当余额小于这个值时，那么打码量就会重置 ***/
    private static BigDecimal DEAULT_RESET_CODE_AMOUNT_BY_MIN_BALANCE = new BigDecimal(10);

    @Autowired
    private MoneyOrderService moneyOrderService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private UserMoneyService userMoneyService;

    @Autowired
    private ConfigService mConfigService;

    @Autowired
    private TransferService mTransferService;

    private IdGenerator mIdGenerator = IdGenerator.newSingleWorder();

    /**
     * 用户充值
     * @param outTradeNo
     * @param userInfo
     * @param amount
     * @param remark
     */
    public ErrorResult doUserRecharge(FundAccountType accountType, ICurrencyType currencyType, BusinessType businessType, String outTradeNo, UserInfo userInfo, BigDecimal amount, JSONObject remark)
    {
        return doRechargeAction(accountType, currencyType, businessType, MoneyOrderType.USER_RECHARGE, outTradeNo, userInfo, amount, BigDecimal.ZERO, remark);
    }

    /**
     * 用户提现
     * @param outTradeNo
     * @param userInfo
     * @param amount
     * @param feemoney
     * @param remark
     */
    public ErrorResult doUserWithdraw(FundAccountType accountType, ICurrencyType currencyType, BusinessType businessType, String outTradeNo, UserInfo userInfo, BigDecimal amount, BigDecimal feemoney, JSONObject remark)
    {
        return doDeductAction(accountType, currencyType, businessType, MoneyOrderType.USER_WITHDRAW, outTradeNo, userInfo, amount, feemoney, remark);
    }

    /**
     * 退款
     * @param outTradeNo
     * @param userInfo
     * @param amount
     * @param remark
     */
    public ErrorResult doRefund(FundAccountType accountType, ICurrencyType currencyType, BusinessType businessType, String outTradeNo, UserInfo userInfo, BigDecimal amount, BigDecimal deductFeemoney, JSONObject remark)
    {
        return doRechargeAction(accountType, currencyType, businessType, MoneyOrderType.REFUND, outTradeNo, userInfo, amount, deductFeemoney, remark);
    }

    /**
     * 返佣
     * @param outTradeNo
     * @param userInfo
     * @param amount
     * @param remark
     */
    public ErrorResult doReturnWater(FundAccountType accountType, ICurrencyType currencyType, BusinessType businessType, String outTradeNo, UserInfo userInfo, BigDecimal amount, JSONObject remark)
    {
        return doRechargeAction(accountType, currencyType, businessType, MoneyOrderType.RETURN_WATER, outTradeNo, userInfo, amount, BigDecimal.ZERO, remark);
    }


    /**
     * 平台充值
     * @param outTradeNo
     * @param userInfo
     * @param amount
     * @param remark
     */
    public ErrorResult doPlatformRecharge(FundAccountType accountType, ICurrencyType currencyType, BusinessType businessType, String outTradeNo, UserInfo userInfo, BigDecimal amount, JSONObject remark)
    {
        return doRechargeAction(accountType, currencyType, businessType, MoneyOrderType.PLATFORM_RECHARGE, outTradeNo, userInfo, amount, BigDecimal.ZERO, remark);
    }

    /**
     * 平台赠送
     * @param outTradeNo
     * @param userInfo
     * @param amount
     * @param remark
     */
    public ErrorResult doPlatformPresentation(FundAccountType accountType, ICurrencyType currencyType, BusinessType businessType, String outTradeNo, UserInfo userInfo, BigDecimal amount, JSONObject remark)
    {
        return doRechargeAction(accountType, currencyType, businessType, MoneyOrderType.PLATFORM_PRESENTATION, outTradeNo, userInfo, amount, BigDecimal.ZERO, remark);
    }

    /**
     * 平台扣款
     * @param outTradeNo
     * @param userInfo
     * @param amount
     * @param remark
     */
    public ErrorResult doPlatformDeduct(FundAccountType accountType, ICurrencyType currencyType, BusinessType businessType, String outTradeNo, UserInfo userInfo, BigDecimal amount, JSONObject remark)
    {
        return doDeductAction(accountType, currencyType, businessType, MoneyOrderType.PLATFORM_DEDUCT, outTradeNo, userInfo, amount, BigDecimal.ZERO, remark);
    }


    /**
     * 业务充值
     * @param outTradeNo
     * @param userInfo
     * @param amount
     * @param remark
     */
    public ErrorResult doBusinessRecharge(FundAccountType accountType, ICurrencyType currencyType, BusinessType businessType, String outTradeNo, UserInfo userInfo, BigDecimal amount, JSONObject remark)
    {
        return doRechargeAction(accountType, currencyType, businessType, MoneyOrderType.BUSINESS_RECHARGE, outTradeNo, userInfo, amount, BigDecimal.ZERO, remark);
    }

    /**
     * 业务扣款
     * @param outTradeNo
     * @param userInfo
     * @param amount
     * @param feemoney
     * @param remark
     */
    public ErrorResult doBusinessDeduct(FundAccountType accountType, ICurrencyType currencyType, BusinessType businessType, String outTradeNo, UserInfo userInfo, BigDecimal amount, BigDecimal feemoney, JSONObject remark)
    {
        return doDeductAction(accountType, currencyType, businessType, MoneyOrderType.BUSINESS_DEDUCT, outTradeNo, userInfo, amount, feemoney, remark);
    }

    /**
     * 理财充值
     * @param outTradeNo
     * @param userInfo
     * @param amount
     * @param remark
     */
    public ErrorResult doFinanceRecharge(FundAccountType accountType, ICurrencyType currencyType, BusinessType businessType, String outTradeNo, UserInfo userInfo, BigDecimal amount, JSONObject remark)
    {
        return doRechargeAction(accountType, currencyType, businessType, MoneyOrderType.FINANCE_RECHARGE, outTradeNo, userInfo, amount, BigDecimal.ZERO, remark);
    }

    /**
     * 理财扣款
     * @param outTradeNo
     * @param userInfo
     * @param amount
     * @param feemoney
     * @param remark
     */
    public ErrorResult doFinanceDeduct(FundAccountType accountType, ICurrencyType currencyType, BusinessType businessType, String outTradeNo, UserInfo userInfo, BigDecimal amount, BigDecimal feemoney, JSONObject remark)
    {
        return doDeductAction(accountType, currencyType, businessType, MoneyOrderType.FINANCE_DEDUCT, outTradeNo, userInfo, amount, feemoney, remark);
    }

    /**
     * 充值操作
     * @param orderType
     * @param outTradeNo
     * @param userInfo
     * @param amount
     * @param remark
     * @return
     */
    private ErrorResult doRechargeAction(FundAccountType accountType, ICurrencyType currencyType, BusinessType businessType, MoneyOrderType orderType, String outTradeNo, UserInfo userInfo, BigDecimal amount, BigDecimal feemoney, JSONObject remark)
    {
        if(amount.compareTo(ZERO_AMOUNT) <= 0)
        {
            return SystemErrorResult.ERR_PARAMS;
        }

        String lockPath = getLockPath(userInfo.getName());
        DistributeLock lock = ZKClientManager.getInstanced().createLock(lockPath);
        boolean isLock = lock.lockAcquired();

        try {
            if(isLock)
            {
                Date createtime = null;
                MoneyOrder moneyOrder = moneyOrderService.findByTradeNo(outTradeNo, orderType);

                UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());

                if(moneyOrder != null)
                {
                    OrderTxStatus txStatus = OrderTxStatus.getType(moneyOrder.getStatus());
                    if(txStatus == OrderTxStatus.REALIZED)
                    {
                        return SystemErrorResult.SUCCESS;
                    }

                    if(txStatus == OrderTxStatus.FAILED)
                    {
                        return SystemErrorResult.ERR_SYS_OPT_FAILURE;
                    }
                    createtime = moneyOrder.getCreatetime();
                }
                else
                {
                    createtime = new Date();
                    String orderno = mIdGenerator.nextId();

                    moneyOrderService.createOrder(accountType, currencyType, orderno, outTradeNo, userInfo, userAttr, businessType, orderType, amount, feemoney, createtime, remark);
                }

                // 更新订单状态和余额
                UserMoney userMoney = userMoneyService.findMoney(true, userInfo.getId(), accountType, currencyType);
                BigDecimal balance = userMoney.getBalance();
                BigDecimal newBalance = amount.add(balance);

                // 打码量
                boolean upCodeValue = false;
                if(!UserInfo.DEFAULT_SYSTEM_ACCOUNT.equalsIgnoreCase(userInfo.getName()))
                {
                    upCodeValue = getCodeAmount(userInfo, userAttr, businessType, orderType, userMoney, amount);
                }
                moneyOrderService.updateToRealized(accountType, currencyType, businessType, orderType, outTradeNo, userInfo, amount, feemoney, newBalance, upCodeValue, createtime, userMoney, null);

                moneyOrderService.clearUserQueryPageCache(userInfo.getId());

                // 清除缓存
                userMoneyService.clearUserMoneyCache(userInfo.getId(), accountType, currencyType);

                return SystemErrorResult.SUCCESS;
            }
        } catch (Exception e) {
            LOG.error("doDeductAction error:", e);
        } finally {
            if(isLock)
            {
                lock.lockReleased();
            }
        }

        return SystemErrorResult.ERR_SYS_OPT_FAILURE;
    }

    public ErrorResult updateUserFreezeAmount(FundAccountType accountType, ICurrencyType currencyType, UserInfo userInfo, BigDecimal freezeAmount, boolean add)
    {
        if(freezeAmount.compareTo(ZERO_AMOUNT) <= 0)
        {
            return SystemErrorResult.ERR_PARAMS;
        }

        String lockPath = getLockPath(userInfo.getName());
        DistributeLock lock = ZKClientManager.getInstanced().createLock(lockPath);
        boolean isLock = lock.lockAcquired();

        try {
            if(isLock)
            {
                UserMoney userMoney = userMoneyService.findMoney(false, userInfo.getId(), accountType, currencyType);

                BigDecimal newFreezeAmount = null;
                if(add)
                {
                    newFreezeAmount = userMoney.getFreeze().add(freezeAmount);
                }
                else
                {
                    newFreezeAmount = userMoney.getFreeze().subtract(freezeAmount);
                }
                if(newFreezeAmount.compareTo(BigDecimal.ZERO) < 0)
                {
                    newFreezeAmount = BigDecimal.ZERO;
                }

                // 更新订单状态和余额
                userMoneyService.updateFreezeAmount(userInfo.getId(), accountType, currencyType, newFreezeAmount);
                moneyOrderService.clearUserQueryPageCache(userInfo.getId());
                // 清除缓存
                userMoneyService.clearUserMoneyCache(userInfo.getId(), accountType, currencyType);
                return SystemErrorResult.SUCCESS;
            }
        } catch (Exception e) {
            LOG.error("doDeductAction error:", e);
        } finally {
            if(isLock)
            {
                lock.lockReleased();
            }
        }
        return SystemErrorResult.ERR_SYS_OPT_FAILURE;
    }

    private ErrorResult doDeductAction(FundAccountType accountType, ICurrencyType currencyType, BusinessType businessType, MoneyOrderType orderType, String outTradeNo, UserInfo userInfo, BigDecimal amount, BigDecimal feemoney, JSONObject remark)
    {
        if(amount.compareTo(ZERO_AMOUNT) <= 0)
        {
            return SystemErrorResult.ERR_PARAMS;
        }

        String lockPath = getLockPath(userInfo.getName());
        DistributeLock lock = ZKClientManager.getInstanced().createLock(lockPath);
        boolean isLock = lock.lockAcquired();

        try {
            if(isLock)
            {
                MoneyOrder moneyOrder = moneyOrderService.findByTradeNo(outTradeNo, orderType);

                boolean isNew = false;
                if(moneyOrder != null)
                {
                    OrderTxStatus txStatus = OrderTxStatus.getType(moneyOrder.getStatus());
                    if(txStatus == OrderTxStatus.REALIZED)
                    {
                        return SystemErrorResult.SUCCESS;
                    }

                    if(txStatus == OrderTxStatus.FAILED)
                    {
                        return SystemErrorResult.ERR_SYS_OPT_FAILURE;
                    }
                }
                else
                {
                    isNew = true;
                }

                // 更新订单状态和余额
                UserMoney userMoney = userMoneyService.findMoney(true, userInfo.getId(), accountType, currencyType);
                BigDecimal balance = userMoney.getBalance();
                BigDecimal newBalance = balance.subtract(amount);

                // 再扣除冻结金额
                // 余额不足
                if(newBalance.subtract(userMoney.getFreeze()).compareTo(BigDecimal.ZERO) < 0)
                {
                    return UserErrorResult.ERR_PAY_NOT_ENOUGH_BALANCE;
                }

                Date createtime = null;
                if(isNew)
                {
                    createtime = new Date();
                    String orderno = mIdGenerator.nextId();

                    UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());
                    moneyOrderService.createOrder(accountType, currencyType, orderno, outTradeNo, userInfo, userAttr, businessType, orderType, amount, feemoney, createtime, remark);
                }
                else
                {
                    createtime = moneyOrder.getCreatetime();
                }

                // 打码量
                boolean upCodeValue = false;
                if(!UserInfo.DEFAULT_SYSTEM_ACCOUNT.equalsIgnoreCase(userInfo.getName()))
                {
                    upCodeValue = getDeductCodeAmount(businessType, orderType, userMoney, amount);
                }

                BigDecimal totalDeductCodeAmount = userMoney.getTotalDeductCodeAmount();

                moneyOrderService.updateToRealized(accountType, currencyType, businessType, orderType, outTradeNo, userInfo, amount, feemoney, newBalance, upCodeValue, createtime, userMoney, totalDeductCodeAmount);

                moneyOrderService.clearUserQueryPageCache(userInfo.getId());

                userMoneyService.clearUserMoneyCache(userInfo.getId(), accountType, currencyType);

                // 统计
                addStats(businessType, orderType, userInfo, amount);

                return SystemErrorResult.SUCCESS;
            }
        } catch (Exception e) {
            // 没有成功，修改订单状态为error
            //moneyOrderService.updateToError(outTradeNo);
            LOG.error("doDeductAction error:", e);
        } finally {
            if(isLock)
            {
                lock.lockReleased();
            }
        }

        return SystemErrorResult.ERR_SYS_OPT_FAILURE;
    }

    /**
     * 打码量
     * @param type
     * @param userMoney
     * @param currentAmount
     * @return
     */
    private boolean getCodeAmount(UserInfo userInfo, UserAttr userAttr, BusinessType businessType, MoneyOrderType type, UserMoney userMoney, BigDecimal currentAmount)
    {
        // 只有会员类型才有打码量-会员类型有普通会员和推广会员
        MemberSubType subType = MemberSubType.getType(userInfo.getSubType());
        if(subType == null)
        {
            return false;
        }

        BigDecimal rsCodeAmount = null;

        try {
            // 用户充值
            if(type == MoneyOrderType.USER_RECHARGE || type == MoneyOrderType.PLATFORM_RECHARGE)
            {
                CodeAmountConfig codeAmountType = CodeAmountConfig.USER_RECHARGE;
                if(StringUtils.isEmpty(userAttr.getFirstRechargeOrderno()))
                {
                    // 首次充值打码
                    codeAmountType = CodeAmountConfig.FIRST_RECHARGE;
                }
                String codeAmountKey = codeAmountType.getKey(subType);
                if(StringUtil.isEmpty(codeAmountKey))
                {
                    return false;
                }

                BigDecimal value = mConfigService.getBigDecimal(false, codeAmountKey);
                if(value == null)
                {
                    return false;
                }
                if(value.compareTo(BigDecimal.ZERO) <= 0)
                {
                    value = BigDecimal.ZERO;
                }

                if(userMoney.getBalance().compareTo(BigDecimal.ONE) < 0)
                {
                    // 金额 < 1 清除打码量和锁定金额
                    userMoney.setLimitAmount(BigDecimal.ZERO);
                    userMoney.setLimitCode(BigDecimal.ZERO);
                }
                else if(userMoney.getBalance().compareTo(userMoney.getLimitAmount()) < 0)
                {
                    // 在操作打码量之前先检查之前的锁定金额 <= 余额, 防止新充值的金额被锁定
                    userMoney.setLimitAmount(userMoney.getBalance());
                }

                // 打码倍数 > 0 才操作
                BigDecimal newLimitAmount = userMoney.getLimitAmount();
                rsCodeAmount = userMoney.getLimitCode();
                if(value.compareTo(BigDecimal.ZERO) > 0)
                {
                    newLimitAmount = userMoney.getLimitAmount().add(currentAmount);
                    rsCodeAmount = userMoney.getLimitCode().add(currentAmount.multiply(value));
                }

                userMoney.setLimitCode(rsCodeAmount);
                userMoney.setLimitAmount(newLimitAmount);
                return true;

            }
            // 任务赠送
            else if(businessType.isTaskPresentation())
            {
                CodeAmountConfig codeAmountType = CodeAmountConfig.TASK_PRESENTATION;
                String codeAmountKey = codeAmountType.getKey(subType);
                if(StringUtil.isEmpty(codeAmountKey))
                {
                    return false;
                }

                BigDecimal codeAmountMultiple = mConfigService.getBigDecimal(false, codeAmountKey);
                if(codeAmountMultiple == null || codeAmountMultiple.compareTo(BigDecimal.ZERO) <= 0)
                {
                    return false;
                }

                // v1
//                rsCodeAmount =  userMoney.getCodeAmount().add(currentAmount.multiply(codeAmountMultiple));
//                userMoney.setCodeAmount(rsCodeAmount);

                //V2
                BigDecimal newLimitAmount = userMoney.getLimitAmount().add(currentAmount);
                rsCodeAmount = userMoney.getLimitCode().add(currentAmount.multiply(codeAmountMultiple));

                userMoney.setLimitCode(rsCodeAmount);
                userMoney.setLimitAmount(newLimitAmount);
                return true;
            }
            // 红包赠送
            else if(businessType == BusinessType.GAME_RED_PACKAGE_NO_CODE)
            {
                // 特殊红包不打码
                return false;
            }
            else if(businessType == BusinessType.GAME_RED_PACKAGE)
            {
                CodeAmountConfig codeAmountType = CodeAmountConfig.RED_PACKAGE_PRESENTATION;
                String codeAmountKey = codeAmountType.getKey(subType);
                if(StringUtil.isEmpty(codeAmountKey))
                {
                    return false;
                }

                BigDecimal codeAmountMultiple = mConfigService.getBigDecimal(false, codeAmountKey);
                if(codeAmountMultiple == null || codeAmountMultiple.compareTo(BigDecimal.ZERO) <= 0)
                {
                    return false;
                }

                BigDecimal newLimitAmount = userMoney.getLimitAmount().add(currentAmount);
                rsCodeAmount = userMoney.getLimitCode().add(currentAmount.multiply(codeAmountMultiple));

                userMoney.setLimitCode(rsCodeAmount);
                userMoney.setLimitAmount(newLimitAmount);
                return true;
            }
            // 注册赠送
            else if(businessType == BusinessType.REGISTER_PRESENTATION)
            {
                CodeAmountConfig codeAmountType = CodeAmountConfig.REGISTER_PRESENTATION;
                String codeAmountKey = codeAmountType.getKey(subType);
                if(StringUtil.isEmpty(codeAmountKey))
                {
                    return false;
                }

                BigDecimal codeAmountMultiple = mConfigService.getBigDecimal(false, codeAmountKey);
                if(codeAmountMultiple == null || codeAmountMultiple.compareTo(BigDecimal.ZERO) <= 0)
                {
                    return false;
                }

                BigDecimal newLimitAmount = userMoney.getLimitAmount().add(currentAmount);
                rsCodeAmount = userMoney.getLimitCode().add(currentAmount.multiply(codeAmountMultiple));

                userMoney.setLimitCode(rsCodeAmount);
                userMoney.setLimitAmount(newLimitAmount);

                return true;
            }
            else if(businessType == BusinessType.GAME_PG_LOTTERY)
            {
                SystemConfig pgCodeAmountCfg = SystemConfig.GAME_PG_CODE_AMOUNT;
                BigDecimal codeAmountMultiple = mConfigService.getBigDecimal(false, pgCodeAmountCfg.getKey());
                if(codeAmountMultiple == null || codeAmountMultiple.compareTo(BigDecimal.ZERO) <= 0)
                {
                    return false;
                }

                BigDecimal newLimitAmount = userMoney.getLimitAmount().add(currentAmount);
                rsCodeAmount = userMoney.getLimitCode().add(currentAmount.multiply(codeAmountMultiple));

                userMoney.setLimitCode(rsCodeAmount);
                userMoney.setLimitAmount(newLimitAmount);

                return true;
            }
            // 平台赠送
            else if(type == MoneyOrderType.PLATFORM_PRESENTATION || businessType.isPlatformPresent())
            {
                CodeAmountConfig codeAmountType = CodeAmountConfig.PLATFORM_PRESENTATION;
                String codeAmountKey = codeAmountType.getKey(subType);
                if(StringUtil.isEmpty(codeAmountKey))
                {
                    return false;
                }

                BigDecimal codeAmountMultiple = mConfigService.getBigDecimal(false, codeAmountKey);
                if(codeAmountMultiple == null || codeAmountMultiple.compareTo(BigDecimal.ZERO) <= 0)
                {
                    return false;
                }

                BigDecimal newLimitAmount = userMoney.getLimitAmount().add(currentAmount);
                rsCodeAmount = userMoney.getLimitCode().add(currentAmount.multiply(codeAmountMultiple));

                userMoney.setLimitCode(rsCodeAmount);
                userMoney.setLimitAmount(newLimitAmount);
                return true;
            }
            // 返佣
            else if(type == MoneyOrderType.RETURN_WATER)
            {
                CodeAmountConfig codeAmountType = CodeAmountConfig.RETURN_WATER_PRESENTATION;
                String codeAmountKey = codeAmountType.getKey(subType);
                if(StringUtil.isEmpty(codeAmountKey))
                {
                    return false;
                }

                // 订单反佣金额小于1时不打码
    //            if(businessType == BusinessType.RETURN_WATER && currentAmount.floatValue() < 1)
    //            {
    //                return null;
    //            }
                BigDecimal codeAmountMultiple = mConfigService.getBigDecimal(false, codeAmountKey);
                if(codeAmountMultiple == null || codeAmountMultiple.compareTo(BigDecimal.ZERO) <= 0)
                {
                    return false;
                }
//                rsCodeAmount = userMoney.getCodeAmount().add(currentAmount.multiply(codeAmountMultiple));
//                userMoney.setCodeAmount(rsCodeAmount);

                //V2
                BigDecimal newLimitAmount = userMoney.getLimitAmount().add(currentAmount);
                rsCodeAmount = userMoney.getLimitCode().add(currentAmount.multiply(codeAmountMultiple));

                userMoney.setLimitCode(rsCodeAmount);
                userMoney.setLimitAmount(newLimitAmount);

                return true;
            }
            // 业务扣款-下单
//            else if(type == MoneyOrderType.BUSINESS_DEDUCT)
//            {
//                BigDecimal rsAmount = userMoney.getCodeAmount();
//                if(!BusinessType.GAME_FINANCIAL_MANAGE.getKey().equalsIgnoreCase(businessType.getKey())) {
//                     rsAmount = userMoney.getCodeAmount().subtract(currentAmount);
//                }
//                if(rsAmount.compareTo(BigDecimal.ZERO) <= 0)
//                {
//                    return false;
//                }
//
//                rsCodeAmount = rsAmount;
//                return rsCodeAmount;
//            }
            return false;
        } finally {
//            StringBuilder buffer = new StringBuilder();
//            buffer.append("username = ").append(userInfo.getName());
//            buffer.append(", history code amount = ").append(userMoney.getCodeAmount());
//            buffer.append(", new code amount = ").append(rsCodeAmount);
//            buffer.append(", businessType = ").append(businessType.getKey());
//
//            buffer.append(", order amount = ").append(currentAmount);
//            buffer.append(", order type = ").append(type.getKey());
//            buffer.append(", order out trade no = ").append(fromOutTradeno);
//            LOG.info(buffer.toString());
        }
    }

    private boolean getDeductCodeAmount(BusinessType businessType, MoneyOrderType type, UserMoney userMoney, BigDecimal currentAmount)
    {
        if(type != MoneyOrderType.BUSINESS_DEDUCT)
        {
            return false;
        }
        if(!(businessType == BusinessType.GAME_NEW_LOTTERY || businessType == BusinessType.GAME_LOTTERY ||
                businessType == BusinessType.GAME_FRUIT || businessType == BusinessType.GAME_ANDAR_BAHAR ||
                businessType == BusinessType.GAME_PG_LOTTERY))
        {
            return false;
        }

        if(businessType == BusinessType.GAME_PG_LOTTERY)
        {
            BigDecimal newAmoutMuitiple = mConfigService.getBigDecimal(false, SystemConfig.GAME_PG_RUNNING_AMOUNT.getKey());

            // 外部游戏不设置就不流水
            if(newAmoutMuitiple == null || newAmoutMuitiple.compareTo(BigDecimal.ZERO) <= 0 || newAmoutMuitiple.compareTo(BigDecimal.ONE) > 0)
            {
                return false;
            }

            if(newAmoutMuitiple.compareTo(BigDecimal.ONE) < 0)
            {
                // 0 < X < 1 需要转化金额
                currentAmount = newAmoutMuitiple.multiply(currentAmount);
            }
        }


        BigDecimal totalCodeAmount = userMoney.getTotalDeductCodeAmount().add(currentAmount);
        userMoney.setTotalDeductCodeAmount(totalCodeAmount);

        if(userMoney.getLimitAmount().compareTo(BigDecimal.ZERO) > 0)
        {
            // limitAmount/limitCode = ?/limitCode
            // limitAmount * curent / limitCode

            BigDecimal limitCode = userMoney.getLimitCode();
            if(limitCode.compareTo(BigDecimal.ZERO) <= 0)
            {
                limitCode = BigDecimal.ZERO;
            }

            // 动态打码量 >= 配置最低打码倍数
            BigDecimal activeCode2BalanceMultiple = limitCode.divide(userMoney.getLimitAmount(), 2, RoundingMode.UP);
            BigDecimal dbBaseCode2BalanceMultiple = mConfigService.getBigDecimal(false, SystemConfig.PASSPORT_CODE_AMOUNT_LIMIT_TYPE_CODE_2_BALANCE.getKey());;
            if(activeCode2BalanceMultiple != null && dbBaseCode2BalanceMultiple.compareTo(BigDecimal.ZERO) > 0 && dbBaseCode2BalanceMultiple.compareTo(activeCode2BalanceMultiple) > 0)
            {
                // 配置最低打码倍数
                activeCode2BalanceMultiple = dbBaseCode2BalanceMultiple;
            }

            BigDecimal deductLimiAmount = currentAmount.divide(activeCode2BalanceMultiple, 2, RoundingMode.HALF_UP);

            BigDecimal newLimitAmount = userMoney.getLimitAmount().subtract(deductLimiAmount);
            BigDecimal newLimitCode = userMoney.getLimitCode().subtract(currentAmount);

            if(newLimitCode.compareTo(BigDecimal.ZERO) <= 0)
            {
                newLimitCode = BigDecimal.ZERO;
            }

            if(newLimitAmount.compareTo(BigDecimal.ZERO) <= 0)
            {
                newLimitCode = BigDecimal.ZERO;
                newLimitAmount = BigDecimal.ZERO;
            }

            userMoney.setLimitAmount(newLimitAmount);
            userMoney.setLimitCode(newLimitCode);

        }
        else
        {
            BigDecimal rsAmount = userMoney.getCodeAmount().subtract(currentAmount);
            if(rsAmount.compareTo(BigDecimal.ZERO) <= 0)
            {
                rsAmount = BigDecimal.ZERO;
            }

            userMoney.setCodeAmount(rsAmount);
        }

        return true;

    }

    /**
     * 异步
     * @param businessType
     * @param orderType
     * @param userInfo
     */
    @Async
    public void addStats(BusinessType businessType, MoneyOrderType orderType, UserInfo userInfo, BigDecimal amount)
    {
        try {
            if(orderType == MoneyOrderType.BUSINESS_DEDUCT)
            {
                UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());
                ActiveUserManager.increCount(userInfo.getName(),userAttr.getAgentname() ,userAttr.getDirectStaffname() );
            }
            // 实时投注统计
            UserBetReportLogical.addTodayReport(businessType, userInfo.getName(), amount);

            //最近6分钟下单用户
            UserRecentBetStatus.save(userInfo.getName());
        }catch (Exception e){

        }

    }

    private static String getLockPath(String username)
    {
        return UP_USER_BALANCE_PRE_PATH + username;
    }

}
