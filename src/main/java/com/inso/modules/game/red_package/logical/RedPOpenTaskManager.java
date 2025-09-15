package com.inso.modules.game.red_package.logical;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.inso.framework.cache.CacheManager;
import com.inso.modules.common.model.*;
import com.inso.modules.game.rg.cache.LotteryCacheHelper;
import com.inso.modules.game.rg.model.LotteryRGType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RandomUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.model.GameOpenMode;
import com.inso.modules.game.red_package.model.RedPBetOrderInfo;
import com.inso.modules.game.red_package.model.RedPPeriodInfo;
import com.inso.modules.game.red_package.service.RedPBetOrderService;
import com.inso.modules.game.red_package.service.RedPPeriodService;
import com.inso.modules.game.rg.helper.LotteryHelper;
import com.inso.modules.game.lottery_game_impl.rg2.model.LotteryRgBetItemType;
import com.inso.modules.game.rg.model.LotteryOrderInfo;
import com.inso.modules.passport.money.PayApiManager;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.logical.SystemStatusManager;

/**
 * 开奖管理器
 */
@Component
public class RedPOpenTaskManager {

    private static Log LOG = LogFactory.getLog(RedPOpenTaskManager.class);

    private static float DEFAULT_PLATFORM_RATE = 0.8f;

    @Autowired
    private RedPPeriodService mPeriodService;

    @Autowired
    private RedPBetOrderService mLotteryOrderService;

    @Autowired
    private PayApiManager mPayApiMgr;

    @Autowired
    private UserService mUserService;

    private boolean debug = false;

    public void handleOpenResultForAllOrder(long issue, long openResult)
    {
        // 系统维护不执行开奖订单信息
        if(!SystemStatusManager.getInstance().isRunning())
        {
        }

        AtomicInteger totalBetCount = new AtomicInteger();
        AtomicInteger totalWinCount = new AtomicInteger();

        String totalBetAmountKey = "totalBetAmount";
        String totalWinAmountKey = "totalWinAmount";
        String totalFeeAmountKey = "totalFeeAmount";
        Map<String, BigDecimal> maps = Maps.newHashMap();

        BusinessType businessType = BusinessType.GAME_LOTTERY;
        FundAccountType accountType = FundAccountType.Spot;
        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();

        mLotteryOrderService.queryAllByIssue(issue, new Callback<RedPBetOrderInfo>() {
            @Override
            public void execute(RedPBetOrderInfo orderInfo) {

                //清除下注前100条缓存记录
                String cachekey = LotteryCacheHelper.queryLatestPage_100(orderInfo.getUserid(), LotteryRGType.getType(orderInfo.getRpType()));
                CacheManager.getInstance().delete(cachekey);

                try {
                    //
                    BigDecimal totalBetAmount = maps.getOrDefault(totalBetAmountKey, BigDecimal.ZERO);
                    BigDecimal totalWinAmount = maps.getOrDefault(totalWinAmountKey, BigDecimal.ZERO);
                    BigDecimal totalFeeAmount = maps.getOrDefault(totalFeeAmountKey, BigDecimal.ZERO);

                    // stats
                    totalBetCount.incrementAndGet();
                    maps.put(totalBetAmountKey, totalBetAmount.add(orderInfo.getBetAmount()));
                    maps.put(totalFeeAmountKey, totalFeeAmount.add(orderInfo.getFeemoney()));


                    OrderTxStatus status = OrderTxStatus.getType(orderInfo.getStatus());
                    if(status == OrderTxStatus.NEW)
                    {
                        // 异常订单, 重新扣款
                        UserInfo userInfo = mUserService.findByUsername(false, orderInfo.getUsername());
                        ErrorResult result = mPayApiMgr.doBusinessDeduct(accountType, currencyType, businessType, orderInfo.getNo(), userInfo, orderInfo.getBetAmount(), null, null);
                        if(result == SystemErrorResult.SUCCESS)
                        {
                            status = OrderTxStatus.WAITING;
                        }
                    }

                    if(status == OrderTxStatus.REALIZED)
                    {
                        // stats
                        totalWinCount.incrementAndGet();
                        maps.put(totalWinAmountKey, totalWinAmount.add(orderInfo.getWinAmount()));
                        return;
                    }

                    if(status != OrderTxStatus.WAITING)
                    {
                        return;
                    }

                    BigDecimal winMoney = LotteryHelper.calcWinMoney(orderInfo.getBasicAmount(), orderInfo.getBetCount(), openResult, orderInfo.getBetItem());
                    // 中奖充值
                    if(winMoney.compareTo(BigDecimal.ZERO) > 0)
                    {
                        //
                        UserInfo userInfo = mUserService.findByUsername(false, orderInfo.getUsername());
                        ErrorResult result = mPayApiMgr.doBusinessRecharge(accountType, currencyType, businessType, orderInfo.getNo(), userInfo, winMoney,null);
                        if(result == SystemErrorResult.SUCCESS)
                        {
                            mLotteryOrderService.updateTxStatusToRealized(orderInfo.getNo(), openResult, winMoney);

                            // stats
                            totalWinCount.incrementAndGet();
                            maps.put(totalWinAmountKey, totalWinAmount.add(winMoney));
                        }
                        return;
                    }

                    // 未中奖，直接修改状态为失败
                    mLotteryOrderService.updateTxStatusToFailed(orderInfo.getNo(), openResult);
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
        BigDecimal totalfeeAmount = maps.getOrDefault(totalFeeAmountKey, BigDecimal.ZERO);
        mPeriodService.updateAmount(issue, totalBetAmount, totalWinAmount, totalfeeAmount, totalBetCount.get(), totalWinCount.get());
    }

    public long getOpenResult(RedPPeriodInfo model, float platformRate)
    {
        GameOpenMode mode = GameOpenMode.getType(model.getOpenMode());
        if(mode == GameOpenMode.RANDOM)
        {
            int rs = RandomUtils.nextInt(10);
            return rs;
        }
        else if(mode == GameOpenMode.MANUAL)
        {
            return model.getOpenResult();
        }
        // valid platformrate
        if(platformRate > 1 || platformRate < 0)
        {
            platformRate = DEFAULT_PLATFORM_RATE;
        }
        return getByCalcAllOrder(model, platformRate);
    }

    private long getByCalcAllOrder(RedPPeriodInfo model, float platformRate)
    {
        //
        LinkedHashMap<String, Object> maps = getCalcMaps();
        mLotteryOrderService.queryAllByIssue(model.getId(), new Callback<RedPBetOrderInfo>() {
            public void execute(RedPBetOrderInfo orderInfo) {

                LotteryRgBetItemType betItemType = LotteryRgBetItemType.getType(orderInfo.getBetItem());
                if(betItemType == LotteryRgBetItemType.RED)
                {
                    // 2 | 4 | 6 | 8
//                    doCalcAmount(maps, orderInfo.getAmount(),0, orderInfo.getBetItem());
                    doCalcAmount(maps, orderInfo.getBasicAmount(), orderInfo.getBetCount(),2, orderInfo.getBetItem());
                    doCalcAmount(maps, orderInfo.getBasicAmount(), orderInfo.getBetCount(),4, orderInfo.getBetItem());
                    doCalcAmount(maps, orderInfo.getBasicAmount(), orderInfo.getBetCount(),6, orderInfo.getBetItem());
                    doCalcAmount(maps, orderInfo.getBasicAmount(), orderInfo.getBetCount(),8, orderInfo.getBetItem());
                }
                else if(betItemType == LotteryRgBetItemType.GREEN)
                {
                    // 1 | 3 | 7 | 9
                    doCalcAmount(maps, orderInfo.getBasicAmount(), orderInfo.getBetCount(),1, orderInfo.getBetItem());
                    doCalcAmount(maps, orderInfo.getBasicAmount(), orderInfo.getBetCount(),3, orderInfo.getBetItem());
                    doCalcAmount(maps, orderInfo.getBasicAmount(), orderInfo.getBetCount(),7, orderInfo.getBetItem());
                    doCalcAmount(maps, orderInfo.getBasicAmount(), orderInfo.getBetCount(),9, orderInfo.getBetItem());

                    // 1.5倍
//                    doCalcAmount(maps, orderInfo.getAmount(),5, orderInfo.getBetItem());
                }
                else if(betItemType == LotteryRgBetItemType.VIOLET)
                {
                    // 0 | 5
                    doCalcAmount(maps, orderInfo.getBasicAmount(), orderInfo.getBetCount(),0, orderInfo.getBetItem());
                    doCalcAmount(maps, orderInfo.getBasicAmount(), orderInfo.getBetCount(),5, orderInfo.getBetItem());
                }
                else
                {
                    // number 0 ~ 9
                    long number = StringUtils.asLong(orderInfo.getBetItem());
                    doCalcAmount(maps, orderInfo.getBasicAmount(), orderInfo.getBetCount(), number, orderInfo.getBetItem());
                }
            }
        });

        //
        long rsOpenResult = doCalcOpenResult(maps, platformRate);
        return rsOpenResult;
    }

    private long doCalcOpenResult(LinkedHashMap<String, Object> maps, float platformRate)
    {
        BigDecimal totalBetAmount = (BigDecimal) maps.get("totalBetAmount");
        maps.remove("totalBetAmount");

        if(totalBetAmount == null)
        {
            int rs = RandomUtils.nextInt(10);
            return rs;
        }

        Set<Map.Entry<String, Object>> entrySet = maps.entrySet();
        for(Map.Entry<String, Object> tmp : entrySet)
        {
            CalcItem item = (CalcItem) tmp.getValue();
            item.updateRate(totalBetAmount, platformRate);
        }

        List<Map.Entry<String, Object> > list =  new LinkedList<Map.Entry<String, Object> >(entrySet);

        Collections.sort(list, new Comparator<Map.Entry<String, Object>>() {
            @Override
            public int compare(Map.Entry<String, Object> value1, Map.Entry<String, Object> value2) {
                CalcItem calcItem1 = (CalcItem) value1.getValue();
                CalcItem calcItem2 = (CalcItem) value2.getValue();

                float expectRate1 = Math.abs(calcItem1.getExpectRate());
                float expectRate2 = Math.abs(calcItem2.getExpectRate());

                if(expectRate1 > expectRate2 && calcItem2.getPlatformProfitRate() > 0)
                {
                    return 1;
                }
                else if(expectRate1 < expectRate2 && calcItem1.getPlatformProfitRate() > 0)
                {
                    return -1;
                }
                else if(calcItem1.getPlatformProfitRate() > calcItem2.getPlatformProfitRate())
                {
                    return 1;
                }
                else if(calcItem1.getPlatformProfitRate() < calcItem2.getPlatformProfitRate())
                {
                    return -1;
                }
                return 0;
            }
        });

        if(debug)
        {
            for(Map.Entry<String, Object> tmp : list)
            {
                System.out.println(FastJsonHelper.jsonEncode(tmp.getValue()));
            }
        }

        int equalLen = 0;
        int len = list.size() ;
        for(int i = 0; i < len - 1; i ++)
        {
            CalcItem item1 = (CalcItem) list.get(i).getValue();
            CalcItem item2 = (CalcItem) list.get(i + 1).getValue();
            if(item1.getExpectRate() != item2.getExpectRate())
            {
                break;
            }
            equalLen = i + 1;
        }

        if(equalLen == 0)
        {
            if(debug)
            {
                System.out.println("total open result len = " + equalLen + ", totalBetAmount = " + totalBetAmount.toString());
            }
            CalcItem item = (CalcItem)list.get(0).getValue();
            return item.getOpenResult();
        }

        if(debug)
        {
            System.out.println("total open result len = " + equalLen + ", totalBetAmount = " + totalBetAmount.toString());
            for(int i = 0; i < 10; i ++)
            {
                int rsOpenResult = RandomUtils.nextInt(equalLen + 1);
                CalcItem item = (CalcItem)list.get(rsOpenResult).getValue();
                System.out.println("debug open result = " + item.getOpenResult());
            }
        }

        int rsOpenResult = RandomUtils.nextInt(equalLen + 1);
        CalcItem item = (CalcItem)list.get(rsOpenResult).getValue();
        return item.getOpenResult();
    }

    private void doCalcAmount(LinkedHashMap<String, Object> maps, BigDecimal basicAmount, long betCount, long openResult, String betItem)
    {
        String key = openResult + StringUtils.getEmpty();
        Object value = maps.get(openResult + StringUtils.getEmpty());
        if(value == null)
        {
            value = new CalcItem();
        }
        CalcItem item = (CalcItem) value;
        item.setOpenResult(openResult);
        BigDecimal winmoney = LotteryHelper.calcWinMoney(basicAmount, betCount, openResult, betItem);
        item.increAmount(winmoney);
        maps.put(key, item);

        String totalBetAmountKey = "totalBetAmount";
        BigDecimal currentBetAmount = new BigDecimal(betCount).multiply(basicAmount);
        BigDecimal totalBetAmount = (BigDecimal) maps.getOrDefault(totalBetAmountKey, BigDecimal.ZERO);
        totalBetAmount = totalBetAmount.add(currentBetAmount);
        maps.put(totalBetAmountKey, totalBetAmount);
    }

    private class CalcItem{
        private BigDecimal winAmount = BigDecimal.ZERO;
        private long openResult;
        /*** 平台盈利比例 ***/
        private float platformProfitRate = 1;
        /*** 预期比例 (platformRate - platformProfitRate) ***/
        private float expectRate = 1;

        public BigDecimal getWinAmount() {
            return winAmount;
        }

        public void updateRate(BigDecimal totalAmount, float platformRate)
        {
            BigDecimal rs1 = totalAmount.subtract(winAmount);
            BigDecimal rs2 = rs1.divide(totalAmount, 2, BigDecimal.ROUND_HALF_UP);
            this.platformProfitRate = rs2.floatValue();
            this.expectRate = new BigDecimal(platformRate).subtract(rs2).floatValue();
        }

        public void increAmount(BigDecimal amount) {
            if(winAmount == null)
            {
                this.winAmount = amount;
            }
            else
            {
                this.winAmount = this.winAmount.add(amount);
            }
        }

        public long getOpenResult() {
            return openResult;
        }

        public void setOpenResult(long openResult) {
            this.openResult = openResult;
        }

        public float getPlatformProfitRate() {
            return platformProfitRate;
        }

        public float getExpectRate() {
            return expectRate;
        }
    }

    private class CalcTask {
        private BigDecimal totalAmount;
        private BigDecimal totalFeemoney;
        private long totalBetCount;
        private long totalWinCount;

        public void incre(LotteryOrderInfo orderInfo)
        {
            totalBetCount ++;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public BigDecimal getTotalFeemoney() {
            return totalFeemoney;
        }

        public long getTotalBetCount() {
            return totalBetCount;
        }

        public long getTotalWinCount() {
            return totalWinCount;
        }
    }

    private  LinkedHashMap<String, Object> getCalcMaps()
    {
        LinkedHashMap<String, Object> maps = Maps.newLinkedHashMap();
        for(int i = 0; i < 10; i ++)
        {
            CalcItem item = new CalcItem();
            item.setOpenResult(i);
            maps.put(i + StringUtils.getEmpty(), item);
        }
        return maps;
    }

    public static void main(String[] args) {
        RedPOpenTaskManager mgr = new RedPOpenTaskManager();
        LinkedHashMap<String, Object> maps = mgr.getCalcMaps();
        BigDecimal amount = new BigDecimal(100);
        for(int i = 0; i < 500; i ++)
        {
            int openResult = RandomUtils.nextInt(10);
            int betItem = RandomUtils.nextInt(10);
            long betCount = RandomUtils.nextInt(999) + 1;
            long basicAmountValue = 10;
            if(RandomUtils.nextBoolean())
            {
                basicAmountValue = 100;
            }
            BigDecimal basicAmount = new BigDecimal(basicAmountValue);
            mgr.doCalcAmount(maps, basicAmount, betCount, openResult, "" + betItem);
        }
//        mgr.doCalcAmount(maps, amount, 2, "red");
//        mgr.doCalcAmount(maps, amount, 3, "green");
//        mgr.doCalcAmount(maps, amount, 5, "purple");
//
//        mgr.doCalcAmount(maps, amount, 1, "1");
//        mgr.doCalcAmount(maps, amount, 2, "2");
//        mgr.doCalcAmount(maps, amount, 3, "3");

        mgr.debug = true;
        long openResult = mgr.doCalcOpenResult(maps, 0.85f);
        System.out.println("rs open result = " + openResult);
    }

}
