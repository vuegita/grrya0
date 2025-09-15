package com.inso.modules.game.fm.logical;

import java.math.BigDecimal;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.inso.modules.common.model.*;
import com.inso.modules.game.fm.model.FMOrderInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.fm.helper.FMOrderIdHelper;
import com.inso.modules.game.fm.model.FMProductInfo;
import com.inso.modules.game.fm.model.FMType;
import com.inso.modules.game.fm.service.FMOrderService;
import com.inso.modules.game.fm.service.FMProductService;
import com.inso.modules.passport.money.PayApiManager;
import com.inso.modules.passport.business.RefundManager;
import com.inso.modules.passport.returnwater.ReturnWaterManager;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;

/**
 * 投注任务管理器
 */
@Component
public class FMBuyTaskManager {

    private static Log LOG = LogFactory.getLog(FMBuyTaskManager.class);

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private FMProductService mProductService;

    @Autowired
    private FMOrderService mOrderService;

    @Autowired
    private PayApiManager mPayApiMgr;

    @Autowired
    private RefundManager mRefundMgr;

    @Autowired
    private ReturnWaterManager mReturnWaterManager;

    private static final int DEFAULT_CAPACITY = 100;
    private AtomicInteger count = new AtomicInteger();
    private BlockingQueue<BetItemTask> queue = new ArrayBlockingQueue(DEFAULT_CAPACITY);

    private ExecutorService mThreadPool = Executors.newFixedThreadPool(30);

    private boolean stop = false;

    public FMBuyTaskManager()
    {
        new Thread(new Runnable() {
            public void run() {

                while (!stop)
                {
                    try {
                        BetItemTask itemInfo = queue.take();
                        mThreadPool.execute(itemInfo);
                    } catch (Exception e) {
                    }
                }

            }
        }).start();
    }

    public boolean isFull()
    {
        return count.get() >= DEFAULT_CAPACITY;
    }

    public boolean addItemToQueue(FMType type, FMProductInfo productInfo, UserInfo userInfo, BigDecimal buyAmount)
    {
        if(isFull())
        {
            return false;
        }
        type = FMType.getType(productInfo.getType());
        BetItemTask betItemTask = new BetItemTask();
        betItemTask.setProductInfo(productInfo);
        betItemTask.setUserInfo(userInfo);
        betItemTask.setType(type);
        betItemTask.setBuyAmount(buyAmount);

        if(queue.add(betItemTask))
        {
            count.incrementAndGet();
            return true;
        }
        return false;
    }

    public ErrorResult doCreateOrCancelOrder(FMType type, FMProductInfo productInfo, UserInfo userInfo, BigDecimal buyAmount)
    {
        try {
            long issue = productInfo.getId();
            FMPeriodStatus runningStatus = FMPeriodStatus.loadCache(false, type, issue);

            // 线程锁
            synchronized (issue + StringUtils.getEmpty())
            {
                ErrorResult verifyResult = runningStatus.verify(userInfo, buyAmount.floatValue(),issue);

                if(verifyResult != SystemErrorResult.SUCCESS)
                {
                    return verifyResult;
                }

                String orderno = FMOrderIdHelper.nextOrderId(type);
                UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());

                // create order
                BigDecimal feemoney = BigDecimal.ZERO;
                mOrderService.addOrder(orderno, issue, userInfo, userAttr, buyAmount, BigDecimal.ZERO, productInfo.getReturnRealRate(),runningStatus.getTimeHorizon());

                // pay
                FundAccountType accountType = FundAccountType.Spot;
                ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
                ErrorResult result = mPayApiMgr.doFinanceDeduct(accountType, currencyType, BusinessType.GAME_FINANCIAL_MANAGE, orderno, userInfo, buyAmount, feemoney, null);
                if(result == SystemErrorResult.SUCCESS)
                {
                    // 更新订单状态为等待
                    mOrderService.updateTxStatus(orderno, OrderTxStatus.WAITING, null, feemoney, null);
                    mOrderService.clearUserCache(userInfo.getId(),null);

                    //
                    mProductService.updateSaleActual(productInfo.getId(), buyAmount.longValue(), true);

                    // lrc 缓存起来
                    runningStatus.incre(userInfo, buyAmount,productInfo.getId());
                    runningStatus.saveCache();
                }
                return result;
            }

        } catch (Exception e) {
            LOG.error("doCreateOrder error: ", e);
        }
        return SystemErrorResult.ERR_SYS_OPT_FAILURE;
    }

    //提前赎回
    public boolean doRefundOrder(FMOrderInfo MOrderInfo, UserInfo userInfo,boolean isWaiting)
    {

        OrderTxStatus status = OrderTxStatus.getType(MOrderInfo.getStatus());
        if((status != OrderTxStatus.WAITING) && isWaiting)
        {
            return false;
        }
        BigDecimal return_real_amount = MOrderInfo.getBuyAmount().add(BigDecimal.ZERO);

        // 先变更为退款中状态
        if(status != OrderTxStatus.REFUNDING)
        {
            mOrderService.updateTxStatus(MOrderInfo.getNo(), OrderTxStatus.REFUNDING, return_real_amount, null, null);
            mOrderService.clearUserCache(userInfo.getId(),null);

        }

        try {
            FundAccountType accountType = FundAccountType.Spot;
            ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
            ErrorResult errorResult = mPayApiMgr.doFinanceRecharge(accountType, currencyType, BusinessType.GAME_FINANCIAL_MANAGE, MOrderInfo.getNo(), userInfo, return_real_amount, null);
            if(errorResult == SystemErrorResult.SUCCESS)
            {
                mOrderService.updateTxStatus(MOrderInfo.getNo(), OrderTxStatus.FAILED, return_real_amount, null, null);
                mOrderService.clearUserCache(userInfo.getId(),null);
                return true;
            }

        } finally {
            // 清除缓存
            mOrderService.clearUserCache(userInfo.getId(),null);
        }


        return true;

    }

    private class BetItemTask implements Runnable{
        private FMType type;
        private FMProductInfo productInfo;
        private UserInfo userInfo;
        private BigDecimal buyAmount;

        public FMType getType() {
            return type;
        }

        public void setType(FMType type) {
            this.type = type;
        }


        @Override
        public void run() {
            try {
                doCreateOrCancelOrder(type, productInfo, userInfo, buyAmount);
            } finally {
                count.decrementAndGet();
            }
        }

        public UserInfo getUserInfo() {
            return userInfo;
        }

        public void setUserInfo(UserInfo userInfo) {
            this.userInfo = userInfo;
        }

        public BigDecimal getBuyAmount() {
            return buyAmount;
        }

        public void setBuyAmount(BigDecimal buyAmount) {
            this.buyAmount = buyAmount;
        }

        public FMProductInfo getProductInfo() {
            return productInfo;
        }

        public void setProductInfo(FMProductInfo productInfo) {
            this.productInfo = productInfo;
        }

    }



}
