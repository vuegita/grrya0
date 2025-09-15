package com.inso.modules.passport.returnwater;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.inso.framework.context.MyEnvironment;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.CollectionUtils;
import com.inso.modules.ad.core.model.AdVipLimitInfo;
import com.inso.modules.ad.core.service.VipLimitService;
import com.inso.modules.common.config.PlarformConfig2;
import com.inso.modules.common.config.SystemConfig;
import com.inso.modules.common.helper.ParamsParseHelper;
import com.inso.modules.common.model.*;
import com.inso.modules.passport.business.model.ReturnWaterType;
import com.inso.modules.passport.money.PayApiManager;
import com.inso.modules.passport.money.model.UserMoney;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.user.logical.RegPresentationManager;
import com.inso.modules.passport.user.model.*;
import com.inso.modules.passport.returnwater.service.ReturnWaterLogDetailService;
import com.inso.modules.passport.returnwater.service.ReturnWaterLogAmountService;
import com.inso.modules.passport.returnwater.service.ReturnWaterOrderService;
import com.inso.modules.passport.user.service.*;
import com.inso.modules.web.SystemRunningMode;
import com.inso.modules.web.model.MyKeyValueInfo;
import com.inso.modules.web.model.VIPType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.config.PlatformConfig;
import com.inso.modules.web.service.ConfigService;

/**
 * 返佣管理器
 */
@Component
public class ReturnWaterManager {

    private static Log LOG = LogFactory.getLog(RegPresentationManager.class);

    private static final long DEF_7_DAY_MILLIOS = 3600 * 24 * 1000 * 7;

    private static final long DEF_15_DAY_MILLIOS = 3600 * 24 * 1000 * 15;

    private static final long DEF_30_DAY_MILLIOS = 3600 * 24 * 1000 * 30;

    private static final long DEF_60_DAY_MILLIOS = 3600 * 24 * 1000 * 60;

    private static final long DEF_90_DAY_MILLIOS = 3600 * 24 * 1000 * 90;

    @Autowired
    private UserVIPService mUserVIPService;

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private UserMoneyService mUserMoneyService;

    @Autowired
    private ReturnWaterOrderService mReturnWaterService;

    @Autowired
    private ReturnWaterLogAmountService mReturnWaterLogService;

    @Autowired
    private ReturnWaterLogDetailService mReturnWaterLogDetailService;

    @Autowired
    private PayApiManager payManager;

    @Autowired
    private ConfigService mConfigService;

    @Autowired
    private BuyVipOrderService mBuyVipOrderService;

    @Autowired
    private VipLimitService mVipLimitService;

    private BusinessType businessType = BusinessType.RETURN_WATER;

    /*** 上次反水比例刷新时间 ***/
    private long mLastRefreshTime = -1;

    /*** 反水比例 ***/
    private BigDecimal mReturnWaterRateLevel1;
    private BigDecimal mReturnWaterRateLevel2;
    /*** 最低充值 ***/
    private BigDecimal mReturnWaterMinRecharge;

    /*** 分级赠送最低金额限制 ***/
    private BigDecimal mReturnWaterLayerConfigLimitMinAmount;
    private List<MyKeyValueInfo> mReturnLayerConfigList = Collections.emptyList();

    /**
     * 异步赠送
     * @param userInfo
     * @param feemoney
     */
    public void doReturnWater(ICurrencyType currencyType, ReturnWaterType type, UserInfo userInfo, String outTradeNo, BigDecimal feemoney)
    {
        doReturnWater(currencyType, type, userInfo, outTradeNo, feemoney, null);
    }

    @Async
    public void doReturnWater(ICurrencyType currencyType, ReturnWaterType type, UserInfo userInfo, String outTradeNo, BigDecimal feemoney, BigDecimal originAmount)
    {
        FundAccountType accountType = FundAccountType.Spot;
        UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());
        if(userType != UserInfo.UserType.MEMBER)
        {
            // 会员才有返佣
            return;
        }

        Status status = Status.getType(userInfo.getStatus());
        if(status != Status.ENABLE)
        {
            // 如果被禁用了，就不能有返佣了
            return;
        }

        boolean refreshRs = refreshRate();
        if(!refreshRs)
        {
            return;
        }

        if(!MyEnvironment.isDev() && mReturnWaterMinRecharge.compareTo(BigDecimal.ZERO) > 0 && (SystemRunningMode.isBCMode() || SystemRunningMode.isFundsMode()))
        {
            // 最低充值可返佣比例
            UserMoney userMoney = mUserMoneyService.findMoney(false, userInfo.getId(), accountType, currencyType);
            if(userMoney == null || userMoney.getTotalRecharge() == null )
            {
                return;
            }

            BigDecimal chkAmount = userMoney.getTotalRecharge().subtract(userMoney.getTotalWithdraw()).add(userMoney.getTotalRefund());
            if(chkAmount.compareTo(mReturnWaterMinRecharge) < 0)
            {
                return;
            }

        }



        try {
            //


            UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());
            UserInfo userInfoLevel1 = mUserService.findByUsername(false, userAttr.getParentname());
            UserInfo userInfoLevel2 = mUserService.findByUsername(false, userAttr.getGrantfathername());

            BigDecimal firstRate1 = mReturnWaterRateLevel1;
            BigDecimal firstRate2 = mReturnWaterRateLevel2;


            // level 1 return water

            if(StringUtils.isEmpty(userAttr.getParentname()))
            {
                return;
            }

            AdVipLimitInfo returnWaterLevel1 = getLevel(type, userInfoLevel1.getId());
            if(returnWaterLevel1 != null)
            {
                firstRate1 = returnWaterLevel1.getLv1RebateBalanceRate().divide(BigDecimalUtils.DEF_100);
                //firstRate2 = returnWaterLevel.getSecondRate();
            }
            BigDecimal presentationAmount = feemoney.multiply(firstRate1);

            //
            String outTradeNoLevel1 = 1 + outTradeNo;
           // UserInfo userInfoLevel1 = mUserService.findByUsername(false, userAttr.getParentname());
            if(userInfoLevel1 == null)
            {
                return;
            }

            RemarkVO remark = RemarkVO.create("Return water Presentation for level 1");
            remark.put("presentUsername", userInfo.getName()); //赠送人用户名
            Date createtime = new Date();

            //判断是否是购买vip反水
            if(type == ReturnWaterType.AD)
            {
                remark.setMesage("Return water Presentation for level 1; buy_VIP_Name:"+userInfo.getName()+"; buy_Vip_amount:" + feemoney+"; buy_Vip_order:" + outTradeNo );
            }

            boolean rs = createOrder(type, accountType, currencyType, userInfo, userAttr, 1, userInfoLevel1, outTradeNoLevel1, presentationAmount, createtime, remark, originAmount);
            if(!rs)
            {
                // 上面一个如果没有成功，下面就不执行
                return;
            }

            // leve2 return water
            if(StringUtils.isEmpty(userAttr.getGrantfathername()))
            {
                return;
            }
            AdVipLimitInfo returnWaterLevel2 = getLevel(type, userInfoLevel2.getId());
            if(returnWaterLevel2 != null)
            {
                firstRate2 = returnWaterLevel2.getLv2RebateBalanceRate().divide(BigDecimalUtils.DEF_100);
            }

            // 反水比例
            if(firstRate2 == null || firstRate2.floatValue() <= 0)
            {
                return;
            }
            BigDecimal presentationAmount2 = feemoney.multiply(firstRate2);
            String outTradeNoLevel2 = 2 + outTradeNo;
           // UserInfo userInfoLevel2 = mUserService.findByUsername(false, userAttr.getGrantfathername());
            if(userInfoLevel2 == null)
            {
                return;
            }
            remark.setMesage("Return water Presentation for level 2");

            //判断是否是购买vip反水
            if(type == ReturnWaterType.AD)
            {
                remark.setMesage("Return water Presentation for level 2; buy_VIP_Name:"+userInfo.getName()+"; buy_Vip_amount:" + feemoney+"; buy_Vip_order:" + outTradeNo );

            }
            createOrder(type, accountType, currencyType, userInfo, userAttr, 2, userInfoLevel2, outTradeNoLevel2, presentationAmount2, createtime, remark, originAmount);

        } catch (Exception e) {
            LOG.error("handle return water presentation error:", e);
        }
    }

    /**
     *
     * @param level
     * @param beneficiaryUserInfo 受益人用户信息
     * @param presentationUserInfo 赠送人用户信息-也就是投注用户反水给受益人
     * @param outTradeNo
     * @param presentationAmount
     * @param createtime
     * @param remark
     */
    private boolean createOrder(ReturnWaterType type, FundAccountType accountType, ICurrencyType currencyType, UserInfo presentationUserInfo, UserAttr fromUserAttr,
                                int level, UserInfo beneficiaryUserInfo, String outTradeNo, BigDecimal presentationAmount, Date createtime, RemarkVO remark, BigDecimal originAmount)
    {
        try {

            if(presentationAmount == null || presentationAmount.compareTo(BigDecimal.ZERO) <= 0)
            {
                return false;
            }

            Status returnLevelstatus = Status.getType(fromUserAttr.getReturnLevelStatus());
            if(returnLevelstatus == Status.ENABLE)
            {
                // 自定义返佣给上级
                if(level == 1)
                {
                    if(fromUserAttr.getReturnLv1Rate().compareTo(BigDecimal.ZERO) <= 0)
                    {
                        return false;
                    }
                    else if(fromUserAttr.getReturnLv1Rate().compareTo(BigDecimal.ZERO) > 0 && fromUserAttr.getReturnLv1Rate().compareTo(BigDecimal.ONE) < 0)
                    {
                        presentationAmount = presentationAmount.multiply(fromUserAttr.getReturnLv1Rate());
                    }
                }
                else
                {
                    if(fromUserAttr.getReturnLv2Rate().compareTo(BigDecimal.ZERO) <= 0)
                    {
                        return false;
                    }
                    else if(fromUserAttr.getReturnLv2Rate().compareTo(BigDecimal.ZERO) > 0 && fromUserAttr.getReturnLv2Rate().compareTo(BigDecimal.ONE) < 0)
                    {
                        presentationAmount = presentationAmount.multiply(fromUserAttr.getReturnLv2Rate());
                    }
                }
            }
            else
            {
                presentationAmount = handlePresentByTimeLayer(type, presentationAmount, presentationUserInfo);
            }
//            presentationAmount = handlePresentByTimeLayer(type, presentationAmount, presentationUserInfo);

            // 并发锁-限制针对同一个用户返佣
            synchronized (beneficiaryUserInfo.getName())
            {
                UserInfo.UserType userType = UserInfo.UserType.getType(beneficiaryUserInfo.getType());
                if(userType != UserInfo.UserType.MEMBER)
                {
                    // 非会员不能来自返佣的收益
                    return false;
                }

                // 受益人
                UserAttr toUserAttr = mUserAttrService.find(false, beneficiaryUserInfo.getId());

                Status toReturnstatus = Status.getType(toUserAttr.getReturnLevelStatus());
                if(toReturnstatus == Status.ENABLE)
                {
                    // 自定义接受
                    if(level == 1)
                    {
                        if(toUserAttr.getReceivLv1Rate().compareTo(BigDecimal.ZERO) <= 0)
                        {
                            return false;
                        }
                        else if(toUserAttr.getReceivLv1Rate().compareTo(BigDecimal.ZERO) > 0 && toUserAttr.getReceivLv1Rate().compareTo(BigDecimal.ONE) < 0)
                        {
                            presentationAmount = presentationAmount.multiply(fromUserAttr.getReceivLv1Rate());
                        }
                    }
                    else
                    {
                        if(toUserAttr.getReceivLv2Rate().compareTo(BigDecimal.ZERO) <= 0)
                        {
                            return false;
                        }
                        else if(toUserAttr.getReceivLv2Rate().compareTo(BigDecimal.ZERO) > 0 && toUserAttr.getReceivLv2Rate().compareTo(BigDecimal.ONE) < 0)
                        {
                            presentationAmount = presentationAmount.multiply(fromUserAttr.getReceivLv2Rate());
                        }
                    }
                }

                //
                if(type == ReturnWaterType.GAME)
                {
                    ReturnRecordManager.sendMessage(ReturnRecordManager.MQ_EVENT_TYPE_RETURN_AMOUNT, presentationAmount, beneficiaryUserInfo.getName(), level, originAmount);
                }

                //
                String orderno = mReturnWaterService.createOrder(level, outTradeNo, beneficiaryUserInfo, toUserAttr.getAgentid(), accountType, currencyType, presentationAmount, createtime, remark);
                // 走返佣通道
                ErrorResult result = payManager.doReturnWater(accountType, currencyType, businessType, orderno, beneficiaryUserInfo, presentationAmount, remark);
                if(result == SystemErrorResult.SUCCESS)
                {
                    mReturnWaterService.updateTxStatus(level, orderno, OrderTxStatus.REALIZED, null, remark);

                    // 添加日志
                    mReturnWaterLogDetailService.updateAmount(level, beneficiaryUserInfo, presentationUserInfo, accountType, currencyType, presentationAmount);

                    return true;
                }
            }
        } catch (Exception e) {
            LOG.error("handle return water presentation error:", e);
        }
        return false;
    }

    /**
     * 刷新反水比例，不用每次都都从内存获取
     * @return
     */
    private boolean refreshRate()
    {
        long current = System.currentTimeMillis();
        if(mLastRefreshTime != -1 && current - mLastRefreshTime <= 60_000)
        {
            return true;
        }
        this.mLastRefreshTime = current;

        this.mReturnWaterRateLevel1 = mConfigService.getBigDecimal(false, PlatformConfig.ADMIN_APP_PLATFORM_USER_RETURN_WATER_1LAYER_RATE);
        this.mReturnWaterRateLevel2 = mConfigService.getBigDecimal(false, PlatformConfig.ADMIN_APP_PLATFORM_USER_RETURN_WATER_2LAYER_RATE);
        this.mReturnWaterMinRecharge = mConfigService.getBigDecimal(false, PlarformConfig2.ADMIN_APP_PLATFORM_USER_RETURN_WATER_MIN_RECHARGE.getKey());
        if(this.mReturnWaterRateLevel1 == null || this.mReturnWaterRateLevel1.floatValue() <= 0)
        {
            return false;
        }


        // 加载分级返佣配置
        loadConfigForReturnLayer();
        return true;
    }

    private void loadConfigForReturnLayer()
    {
        this.mReturnWaterLayerConfigLimitMinAmount = mConfigService.getBigDecimal(false, SystemConfig.RETURN_WATER_LAYER_LEVEL_LIMIT_MIN_PRESENT_AMOUNT.getKey());
        if(this.mReturnWaterLayerConfigLimitMinAmount == null || this.mReturnWaterLayerConfigLimitMinAmount.compareTo(BigDecimal.ZERO) <= 0)
        {
            return;
        }

        String valueStr = mConfigService.getValueByKey(false, SystemConfig.RETURN_WATER_LAYER_LEVEL_BY_TIME.getKey());
        if(StringUtils.isEmpty(valueStr))
        {
            return;
        }
        this.mReturnLayerConfigList = ParamsParseHelper.loadStringByParseInt_2_Rate(valueStr);
        if(CollectionUtils.isEmpty(this.mReturnLayerConfigList))
        {
            return;
        }

        long base = 1000;
        for(MyKeyValueInfo entity : this.mReturnLayerConfigList)
        {
            entity.setLongKey(entity.getIntKey() * base);
        }
        Collections.sort(this.mReturnLayerConfigList);
    }

    private AdVipLimitInfo getLevel(ReturnWaterType type, long userid)
    {
        if(type == ReturnWaterType.AD)
        {
            VIPType vipType = VIPType.AD;
            UserVIPInfo vipInfo = mUserVIPService.findByUserId(false,userid, vipType);
            AdVipLimitInfo vipLimitInfo = mVipLimitService.findByVipId(false, vipInfo.getVipid());
            if(vipLimitInfo == null)
            {
                vipLimitInfo = new AdVipLimitInfo();
            }
            return vipLimitInfo;
        }
        return null;
    }


    private BigDecimal handlePresentByTimeLayer(ReturnWaterType type, BigDecimal presentationAmount, UserInfo presentUserInfo)
    {
        if(type != ReturnWaterType.GAME)
        {
            return presentationAmount;
        }
        if(this.mReturnWaterLayerConfigLimitMinAmount == null  || this.mReturnWaterLayerConfigLimitMinAmount.compareTo(BigDecimal.ZERO) <= 0 || presentationAmount.compareTo(this.mReturnWaterLayerConfigLimitMinAmount) < 0)
        {
            return presentationAmount;
        }
        long rs = (System.currentTimeMillis() - presentUserInfo.getCreatetime().getTime()) / 1000;

        List<MyKeyValueInfo> rsList = this.mReturnLayerConfigList;
        int size = rsList.size();
        for(int i = size - 1; i >= 0; i --)
        {
            MyKeyValueInfo entity = rsList.get(i);
            if(rs >= entity.getIntKey())
            {
                presentationAmount = presentationAmount.multiply(entity.getRateValue());
                break;
            }
        }

//            if(rs > DEF_90_DAY_MILLIOS)
//            {
//                presentationAmount = presentationAmount.multiply(BigDecimalUtils.DEF_DECIMAL_01);
//            }
//            else if(rs > DEF_60_DAY_MILLIOS)
//            {
//                presentationAmount = presentationAmount.multiply(BigDecimalUtils.DEF_DECIMAL_03);
//            }
//            else if(rs > DEF_30_DAY_MILLIOS)
//            {
//                presentationAmount = presentationAmount.multiply(BigDecimalUtils.DEF_DECIMAL_05);
//            }
//            else if(rs > DEF_15_DAY_MILLIOS)
//            {
//                presentationAmount = presentationAmount.multiply(BigDecimalUtils.DEF_DECIMAL_06);
//            }
//            else if(rs > DEF_7_DAY_MILLIOS)
//            {
//                presentationAmount = presentationAmount.multiply(BigDecimalUtils.DEF_DECIMAL_08);
//            }

        return presentationAmount;
    }

    public BigDecimal getReturnRate(boolean isLv1)
    {
        refreshRate();
        if(isLv1)
        {
            return this.mReturnWaterRateLevel1;
        }
        return this.mReturnWaterRateLevel2;
    }

    public void test()
    {
        String usernmae = "up9199999999992";
        UserInfo userInfo = mUserService.findByUsername(false, usernmae);

        BigDecimal feemoney = new BigDecimal(30);

        doReturnWater(CryptoCurrency.USDT, ReturnWaterType.GAME, userInfo, "" + System.currentTimeMillis(), feemoney);
    }

//    private ReturnWaterLevel getLevel(ReturnWaterType type, long userid)
//    {
//        if(type == ReturnWaterType.AD)
//        {
//            VIPType vipType = VIPType.getType("ad");
//            UserVIPInfo vipInfo = mUserVIPService.findByUserId(false,userid, vipType);
//            long vipLevel=0;
//            if(vipInfo==null){
//                vipLevel=0;
//            }else{
//                vipLevel= vipInfo.getVipLevel();
//            }
//
//            return ReturnWaterLevel.getTypeByUserInviteStatus(vipLevel);
//        }
//        return null;
//    }

    public static void testRun()
    {
        ReturnWaterManager mgr = SpringContextUtils.getBean(ReturnWaterManager.class);
        mgr.test();
    }

}
