package com.inso.modules.game.fm.logical;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.inso.modules.common.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.modules.game.andar_bahar.model.ABBetItemType;
import com.inso.modules.game.fm.model.FMOrderInfo;
import com.inso.modules.game.fm.model.FMProductInfo;
import com.inso.modules.game.fm.service.FMOrderService;
import com.inso.modules.game.fm.service.FMProductService;
import com.inso.modules.passport.money.PayApiManager;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.logical.SystemStatusManager;

/**
 * 开奖管理器
 */
@Component
public class FMOpenTaskManager {


    private static Log LOG = LogFactory.getLog(FMOpenTaskManager.class);

    private static final String KEY_TOTALBETAMOUNT = "totalBetAmount";

    private static float DEFAULT_PLATFORM_RATE = 0.8f;

    private static ABBetItemType[] mBetItemTypeArray = ABBetItemType.values();

    @Autowired
    private FMProductService mPeriodService;

    @Autowired
    private FMOrderService mOrderService;

    @Autowired
    private PayApiManager mPayApiMgr;

    @Autowired
    private UserService mUserService;

    @Autowired
    private FMBuyTaskManager mFMBuyTaskManager;


    private boolean debug = false;

    public void handleAllOrder(FMProductInfo productInfo)
    {
        // 系统维护不执行开奖订单信息
        if(!SystemStatusManager.getInstance().isRunning())
        {
            return;
        }

        AtomicInteger totalBuyCount = new AtomicInteger();

        String totalBuyAmountKey = "totalBetAmount";
        String totalInterestKey = "totalInterest "; // 利息支出
        Map<String, BigDecimal> maps = Maps.newHashMap();

        BusinessType businessType = BusinessType.GAME_FINANCIAL_MANAGE;
        FundAccountType accountType = FundAccountType.Spot;
        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
        mOrderService.queryAllByIssue(productInfo.getId(), new Callback<FMOrderInfo>() {
            @Override
            public void execute(FMOrderInfo orderInfo) {

                try {
                    //
                    BigDecimal totalBetAmount = maps.getOrDefault(totalBuyAmountKey, BigDecimal.ZERO);
                    BigDecimal totalInterestAmount = maps.getOrDefault(totalInterestKey, BigDecimal.ZERO); // 利息


                    UserInfo userInfo = mUserService.findByUsername(false, orderInfo.getUsername());

                    UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());

                    OrderTxStatus status = OrderTxStatus.getType(orderInfo.getStatus());
                    if(status == OrderTxStatus.NEW)
                    {
                        // 异常订单, 重新扣款

                        ErrorResult result = mPayApiMgr.doFinanceDeduct(accountType, currencyType, businessType, orderInfo.getNo(), userInfo, orderInfo.getBuyAmount(), null, null);
                        if(result == SystemErrorResult.SUCCESS)
                        {
                            //status = OrderTxStatus.WAITING;
                            mOrderService.updateTxStatus(orderInfo.getNo(), OrderTxStatus.WAITING, BigDecimal.ZERO, null, null);
                            mOrderService.clearUserCache(userInfo.getId(),null);
                            return;
                        }
                    }

                    if(status == OrderTxStatus.REFUNDING){
                        mFMBuyTaskManager.doRefundOrder( orderInfo, userInfo ,false);
                        return;
                    }

                    BigDecimal currentInterest = orderInfo.getBuyAmount().multiply(orderInfo.getReturnRealRate());

                    if(status == OrderTxStatus.REALIZED)
                    {
                        if(userType == UserInfo.UserType.MEMBER)
                        {
                            // stats
                            totalBuyCount.incrementAndGet();
                            maps.put(totalBuyAmountKey, totalBetAmount.add(orderInfo.getBuyAmount()));
                            maps.put(totalInterestKey, totalInterestAmount.add(currentInterest));
                        }
                        return;
                    }

                    if(status != OrderTxStatus.WAITING)
                    {
                        return;
                    }

                    Date nowTime = new Date();
                    if(orderInfo.getEndtime().compareTo(nowTime)>=0){
                        return ;
                    }

                    BigDecimal return_real_amount = orderInfo.getBuyAmount().add(currentInterest);

                    ErrorResult errorResult = mPayApiMgr.doFinanceRecharge(accountType, currencyType, BusinessType.GAME_FINANCIAL_MANAGE, orderInfo.getNo(), userInfo, return_real_amount, null);
                    if(errorResult == SystemErrorResult.SUCCESS)
                    {
                        mOrderService.updateTxStatus(orderInfo.getNo(), OrderTxStatus.REALIZED, return_real_amount, null, null);
                        mOrderService.clearUserCache(userInfo.getId(),null);
                        if(userType == UserInfo.UserType.MEMBER)
                        {
                            // stats
                            totalBuyCount.incrementAndGet();
                            maps.put(totalBuyAmountKey, totalBetAmount.add(orderInfo.getBuyAmount()));
                            maps.put(totalInterestKey, totalInterestAmount.add(currentInterest));
                        }
                    }

                } catch (Exception e) {
                    LOG.error("do handle error:", e);
                }
            }
        });

        if(totalBuyCount.get() <= 0)
        {
            return;
        }

        BigDecimal totalBuyAmount = maps.getOrDefault(totalBuyAmountKey, BigDecimal.ZERO);
        BigDecimal totalInterestAmount = maps.getOrDefault(totalInterestKey, BigDecimal.ZERO);
        mPeriodService.updateSaleActualAndInterest(productInfo.getId(), totalBuyAmount.longValue(), totalInterestAmount);
    }


    public void handleAllOrderByEndTime(String startTime, String endTime)
    {
        // 系统维护不执行开奖订单信息
        if(!SystemStatusManager.getInstance().isRunning())
        {
            return;
        }

        // 处理到期流程
        BusinessType businessType = BusinessType.GAME_FINANCIAL_MANAGE;
        FundAccountType accountType = FundAccountType.Spot;
        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
        mOrderService.queryAllByEndtime(startTime, endTime, new Callback<FMOrderInfo>() {
            @Override
            public void execute(FMOrderInfo orderInfo) {
                try {
                    //

                    UserInfo userInfo = mUserService.findByUsername(false, orderInfo.getUsername());

                    OrderTxStatus status = OrderTxStatus.getType(orderInfo.getStatus());
                    if(status == OrderTxStatus.NEW)
                    {
                        // 异常订单, 重新扣款

                        ErrorResult result = mPayApiMgr.doFinanceDeduct(accountType, currencyType, businessType, orderInfo.getNo(), userInfo, orderInfo.getBuyAmount(), null, null);
                        if(result == SystemErrorResult.SUCCESS)
                        {
                            status = OrderTxStatus.WAITING;
                        }
                    }

                    BigDecimal currentInterest = orderInfo.getBuyAmount().multiply(orderInfo.getReturnRealRate());

                    if(status == OrderTxStatus.REALIZED)
                    {
                        return;
                    }


                    if(status != OrderTxStatus.WAITING)
                    {
                        return;
                    }

                    BigDecimal return_real_amount = orderInfo.getBuyAmount().add(currentInterest);

                    ErrorResult errorResult = mPayApiMgr.doFinanceRecharge(accountType, currencyType, BusinessType.GAME_FINANCIAL_MANAGE, orderInfo.getNo(), userInfo, return_real_amount, null);
                    if(errorResult == SystemErrorResult.SUCCESS)
                    {
                        mOrderService.updateTxStatus(orderInfo.getNo(), OrderTxStatus.REALIZED, return_real_amount, null, null);
                        mOrderService.clearUserCache(userInfo.getId(),null);
                    }
                } catch (Exception e) {
                    LOG.error("do handle error:", e);
                }
            }
        });


    }



}
