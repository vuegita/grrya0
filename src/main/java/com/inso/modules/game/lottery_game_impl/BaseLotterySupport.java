package com.inso.modules.game.lottery_game_impl;

import com.google.common.collect.Maps;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.*;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.MyLotteryBetRecordCache;
import com.inso.modules.game.cache.GameCacheKeyHelper;
import com.inso.modules.game.model.*;
import com.inso.modules.game.service.NewLotteryOrderService;
import com.inso.modules.game.service.NewLotteryPeriodService;
import com.inso.modules.passport.money.PayApiManager;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.logical.SystemStatusManager;
import com.inso.modules.web.service.ConfigService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 开奖管理器
 */
public abstract class BaseLotterySupport {

    protected Log LOG = LogFactory.getLog(getClass());

    public static final String KEY_TOTALBETAMOUNT = "totalBetAmount";

    @Autowired
    protected NewLotteryPeriodService mPeriodService;

    @Autowired
    protected NewLotteryOrderService mAOrderService;

    @Autowired
    protected PayApiManager mPayApiMgr;

    @Autowired
    protected UserService mUserService;

    @Autowired
    protected ConfigService mConfigService;

    protected boolean isDEV = MyEnvironment.isDev();

    private FundAccountType accountType = FundAccountType.Spot;
    private ICurrencyType currencyType = ICurrencyType.getSupportCurrency();

    public abstract void onGameStart(NewLotteryPeriodInfo periodInfo, NewLotteryPeriodStatus periodStatus);

    public abstract List<RealtimeBetItemReport> initRealBetItemReport(boolean fetchZero);

    public void handleOpenResultForAllOrder(GameChildType type, String issue, String openResult)
    {
        // 系统维护不执行开奖订单信息
        if(!SystemStatusManager.getInstance().isRunning())
        {
            return;
        }

        NewLotteryPeriodInfo periodInfo = mPeriodService.findByIssue(true, type, issue);

        AtomicInteger totalBetCount = new AtomicInteger();
        AtomicInteger totalWinCount = new AtomicInteger();

        String totalBetAmountKey = "totalBetAmount";
        String totalWinAmountKey = "totalWinAmount";
        String totalWinAmountKey2 = "totalWinAmount2";
        String totalFeeAmountKey = "totalFeeAmount";
        Map<String, BigDecimal> maps = Maps.newHashMap();

//        String logOrderno = "20210730154036852114760";

        BusinessType businessType = BusinessType.GAME_ANDAR_BAHAR;
        FundAccountType accountType = FundAccountType.Spot;
        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();

        // 处理未付的订单
        mAOrderService.queryAllByIssue(type, issue, new Callback<NewLotteryOrderInfo>() {
            @Override
            public void execute(NewLotteryOrderInfo orderInfo) {

                //清除下注前100条缓存记录
                String cachekey = GameCacheKeyHelper.queryOrderLatestPage_100(GameCategory.ANDAR_BAHAR, null, orderInfo.getUserid());
                CacheManager.getInstance().delete(cachekey);


                try {
                    OrderTxStatus status = OrderTxStatus.getType(orderInfo.getStatus());
                    if(status == OrderTxStatus.NEW)
                    {
                        UserInfo userInfo = mUserService.findByUsername(false, orderInfo.getUsername());
                        // 异常订单, 重新扣款
                        ErrorResult result = mPayApiMgr.doBusinessDeduct(accountType, currencyType, businessType, orderInfo.getNo(), userInfo, orderInfo.getTotalBetAmount(), orderInfo.getFeemoney(), null);
                        if(result == SystemErrorResult.SUCCESS)
                        {
                            mAOrderService.updateTxStatus(userInfo.getId(),type, orderInfo.getNo(), OrderTxStatus.WAITING, null);
                        }
                    }
                } catch (Exception e) {
                    LOG.error("do handle lottery error:", e);
                }
            }
        });

        // 结算
        mAOrderService.queryAllByIssue(type, issue, new Callback<NewLotteryOrderInfo>() {
            @Override
            public void execute(NewLotteryOrderInfo orderInfo) {

                try {
                    OrderTxStatus status = OrderTxStatus.getType(orderInfo.getStatus());
                    if(status == OrderTxStatus.NEW)
                    {
                        return;
                    }

                    UserInfo userInfo = mUserService.findByUsername(false, orderInfo.getUsername());
                    UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());


                    //
                    BigDecimal totalBetAmount = maps.getOrDefault(totalBetAmountKey, BigDecimal.ZERO);
                    BigDecimal totalWinAmount = maps.getOrDefault(totalWinAmountKey, BigDecimal.ZERO);
                    BigDecimal totalWinAmount2 = maps.getOrDefault(totalWinAmountKey2, BigDecimal.ZERO);
                    BigDecimal totalFeeAmount = maps.getOrDefault(totalFeeAmountKey, BigDecimal.ZERO);

                    // stats
                    if(userType == UserInfo.UserType.MEMBER)
                    {
                        // 会员才计入统计
                        int count = totalBetCount.get() + (int)orderInfo.getTotalBetCount();
                        totalBetCount.set(count);
                        maps.put(totalBetAmountKey, totalBetAmount.add(orderInfo.getTotalBetAmount()));
                        maps.put(totalFeeAmountKey, totalFeeAmount.add(orderInfo.getFeemoney()));
                    }

                    if(status == OrderTxStatus.REALIZED)
                    {
                        if(userType == UserInfo.UserType.MEMBER)
                        {
                            calcWinAmount(orderInfo, openResult);

                            // 会员才计入统计
                            int count = totalWinCount.get() + (int)orderInfo.getTmpCalcWinCount();
                            totalWinCount.set(count);
                            maps.put(totalWinAmountKey, totalWinAmount.add(orderInfo.getWinAmount()));
                            maps.put(totalWinAmountKey2, totalWinAmount2.add(orderInfo.getTmpWinAmount2()));
                        }
                        return;
                    }

                    if(status != OrderTxStatus.WAITING)
                    {
                        return;
                    }

                    // 中奖充值
                    BigDecimal winMoney = calcWinAmount(orderInfo, openResult);
                    if(winMoney != null && winMoney.compareTo(BigDecimal.ZERO) > 0)
                    {
                        RemarkVO remarkVO = RemarkVO.create("Settle order for " + type.getKey());
                        ErrorResult errorResult = mPayApiMgr.doBusinessRecharge(accountType, currencyType, BusinessType.GAME_LOTTERY, orderInfo.getNo(), userInfo, winMoney, remarkVO);
                        if(errorResult == SystemErrorResult.SUCCESS)
                        {
                            mAOrderService.updateTxStatusToRealized(userInfo.getId(), type, orderInfo.getNo(), openResult, winMoney, periodInfo, null);
                            if(userType == UserInfo.UserType.MEMBER)
                            {
                                // 会员才计入统计
                                int count = totalWinCount.get() + (int)orderInfo.getTmpCalcWinCount();
                                totalWinCount.set(count);
                                maps.put(totalWinAmountKey, totalWinAmount.add(winMoney));
                                maps.put(totalWinAmountKey2, totalWinAmount2.add(orderInfo.getTmpWinAmount2()));
                            }
                        }
                        return;
                    }

                    // 未中奖，直接修改状态为失败
                    mAOrderService.updateTxStatusToFailed(userInfo.getId(), type, orderInfo.getNo(), openResult, periodInfo);
                } catch (Exception e) {
                    LOG.error("do handle lottery error:", e);
                }
            }
        });

        if(totalBetCount.get() <= 0)
        {
            return;
        }

        BigDecimal totalBetAmount = maps.getOrDefault(totalBetAmountKey, BigDecimal.ZERO);
        BigDecimal totalWinAmount = maps.getOrDefault(totalWinAmountKey, BigDecimal.ZERO);
        BigDecimal totalWinAmount2 = maps.getOrDefault(totalWinAmountKey2, BigDecimal.ZERO);
        BigDecimal totalfeeAmount = maps.getOrDefault(totalFeeAmountKey, BigDecimal.ZERO);
        mPeriodService.updateAmount(type, issue, totalBetAmount, totalWinAmount, totalfeeAmount, totalBetCount.get(), totalWinCount.get(), totalWinAmount2);
    }


    public void onBeginGameByCustom_V2(DateTime fireTime)
    {

    }


    public void handleOrderToSettle(GameChildType gameChildType, NewLotteryOrderInfo orderInfo, OrderTxStatus txStatus, String openResult, boolean updateUserRecord)
    {
        if(orderInfo == null)
        {
            return;
        }
        String orderno = orderInfo.getNo();
        OrderTxStatus dbStatus = OrderTxStatus.getType(orderInfo.getStatus());
        if(dbStatus == OrderTxStatus.REALIZED || dbStatus == OrderTxStatus.FAILED)
        {
            return;
        }


        try {
            if(dbStatus != OrderTxStatus.WAITING)
            {
                return;
            }

            if(txStatus == OrderTxStatus.FAILED)
            {
                mAOrderService.updateTxStatusToFailed(orderInfo.getUserid(), gameChildType, orderno, openResult, null);
                return;
            }

            if(txStatus != OrderTxStatus.REALIZED)
            {
               return;
            }

            BigDecimal winMoney = calcWinAmount(openResult, orderInfo.getTotalBetAmount(), orderInfo.getBetItem());
            if(winMoney == null || winMoney.compareTo(BigDecimal.ZERO) <= 0)
            {
                return;
            }

            RemarkVO remarkVO = RemarkVO.create("Settle order for " + gameChildType.getKey());
            UserInfo userInfo = mUserService.findByUsername(false, orderInfo.getUsername());
            ErrorResult errorResult = mPayApiMgr.doBusinessRecharge(accountType, currencyType, BusinessType.GAME_NEW_LOTTERY, orderInfo.getNo(), userInfo, winMoney, remarkVO);
            if(errorResult == SystemErrorResult.SUCCESS)
            {
                mAOrderService.updateTxStatusToRealized(orderInfo.getUserid(), gameChildType, orderInfo.getNo(), openResult, winMoney, null, null);
            }
        } finally {
            if(updateUserRecord)
            {
                MyLotteryBetRecordCache.getInstance().updateUserRecord(true, orderno, gameChildType, orderInfo.getUsername(), openResult, txStatus);
            }
        }


    }

    public abstract void handleStatsBetItem(String username, GameChildType gameChildType, Map<String, RealtimeBetItemReport> betItemReportMap, String betItem, BigDecimal betAmount, BigDecimal feemoney);

    public String createIssue(GameChildType gameChildType, DateTime dateTime, boolean generateKeyIssue) {

        int num = dateTime.getSecondOfDay() / gameChildType.getTotalSeconds() + 1;
        String periods = StringUtils.getEmpty() + num;
        if(num < 10 ) {
            periods= "000"+num;
        }
        else if( num < 100) {
            periods= "00"+num;
        }
        else if( num < 1000) {
            periods= "0"+num;
        }
        String timeString = gameChildType.getCode() + dateTime .toString(DateUtils.TYPE_YYYYMMDD) + periods;
        return timeString;
    }

    public abstract GameChildType[] getAllGameTypes();
    public abstract boolean isWin(String openResult, String betItem);
    public abstract BigDecimal calcWinAmount(String openResult, BigDecimal betAmount, String betItem);
    public abstract BigDecimal calcWinAmount(NewLotteryOrderInfo orderInfo, String openResult);

    public abstract String getReference(NewLotteryPeriodInfo periodInfo, GameChildType gameChildType);
    public abstract String getOpenResult(String reference);
    public abstract long getOpenIndex(String openResult);
    public abstract GameOpenMode getOpenMode(GameChildType gameChildType);

}

