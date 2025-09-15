package com.inso.modules.game.rocket.logical;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.common.collect.Maps;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RandomUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.BusinessType;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.game.model.GameOpenMode;
import com.inso.modules.game.model.GamePeriodStatus;
import com.inso.modules.game.model.NewLotteryOrderInfo;
import com.inso.modules.game.model.NewLotteryPeriodInfo;
import com.inso.modules.game.rocket.helper.RocketHelper;
import com.inso.modules.game.rocket.model.RocketBetItemType;
import com.inso.modules.game.rocket.model.RocketType;
import com.inso.modules.game.service.NewLotteryOrderService;
import com.inso.modules.game.service.NewLotteryPeriodService;
import com.inso.modules.passport.money.PayApiManager;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.logical.SystemStatusManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 开奖管理器
 */
@Component
public class RocketOpenTaskManager {

    private static Log LOG = LogFactory.getLog(RocketOpenTaskManager.class);

    private static float DEFAULT_PLATFORM_RATE = 0.8f;

    @Autowired
    private NewLotteryPeriodService mPeriodService;

    @Autowired
    private NewLotteryOrderService mLotteryOrderService;

    @Autowired
    private PayApiManager mPayApiMgr;

    @Autowired
    private UserService mUserService;

    private boolean debug = false;

    private RocketType mRocketType = RocketType.CRASH;

    public void updateDBOpenResult(String issue, String openResult)
    {
        NewLotteryPeriodInfo model = mPeriodService.findByIssue(true, mRocketType, issue);
        GamePeriodStatus periodStatus = GamePeriodStatus.getType(model.getStatus());
        if(periodStatus != GamePeriodStatus.FINISH)
        {
            mPeriodService.updateStatusToFinish(model, openResult, null);
        }
    }

    public void handleOpenResultForAllOrder(String issue, String openResult)
    {
        // 系统维护不执行开奖订单信息
        if(!SystemStatusManager.getInstance().isRunning())
        {
            return;
        }

        AtomicInteger totalBetCount = new AtomicInteger();
        AtomicInteger totalWinCount = new AtomicInteger();

        String totalBetAmountKey = "totalBetAmount";
        String totalWinAmountKey = "totalWinAmount";
        String totalFeeAmountKey = "totalFeeAmount";
        Map<String, BigDecimal> maps = Maps.newHashMap();

        BusinessType businessType = BusinessType.GAME_NEW_LOTTERY;
        FundAccountType accountType = FundAccountType.Spot;
        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();

        float openResultValue = StringUtils.asFloat(openResult);

        // 重新支付
        mLotteryOrderService.queryAllByIssue(mRocketType, issue, new Callback<NewLotteryOrderInfo>() {
            @Override
            public void execute(NewLotteryOrderInfo orderInfo) {

                try {
                    OrderTxStatus status = OrderTxStatus.getType(orderInfo.getStatus());
                    if(status == OrderTxStatus.NEW)
                    {
                        UserInfo userInfo = mUserService.findByUsername(false, orderInfo.getUsername());
                        // 异常订单, 重新扣款
                        ErrorResult result = mPayApiMgr.doBusinessDeduct(accountType, currencyType, businessType, orderInfo.getNo(), userInfo, orderInfo.getTotalBetAmount(), orderInfo.getFeemoney(), null);
                        if(result == SystemErrorResult.SUCCESS)
                        {
                            long userid = orderInfo.getUserid();
                            mLotteryOrderService.updateTxStatus(userid, mRocketType, orderInfo.getNo(), OrderTxStatus.WAITING, null);
                        }
                    }
                } catch (Exception e) {
                    LOG.error("do handle lottery error:", e);
                }
            }
        });

        // 结算
        mLotteryOrderService.queryAllByIssue(mRocketType, issue, new Callback<NewLotteryOrderInfo>() {
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
                    BigDecimal totalFeeAmount = maps.getOrDefault(totalFeeAmountKey, BigDecimal.ZERO);

                    // stats
                    if(userType == UserInfo.UserType.MEMBER)
                    {
                        totalBetCount.incrementAndGet();
                        maps.put(totalBetAmountKey, totalBetAmount.add(orderInfo.getTotalBetAmount()));
                        maps.put(totalFeeAmountKey, totalFeeAmount.add(orderInfo.getFeemoney()));
                    }


                    if(status == OrderTxStatus.REALIZED)
                    {
                        // stats
                        if(userType == UserInfo.UserType.MEMBER)
                        {
                            totalWinCount.incrementAndGet();
                            maps.put(totalWinAmountKey, totalWinAmount.add(orderInfo.getWinAmount()));
                        }

                        return;
                    }

                    if(status != OrderTxStatus.WAITING)
                    {
                        return;
                    }

                    float betItemValue = StringUtils.asFloat(orderInfo.getBetItem());

                    BigDecimal winMoney = RocketHelper.calcWinMoney(betItemValue, openResultValue, orderInfo.getTotalBetAmount());
                    // 中奖充值
                    if(winMoney.compareTo(BigDecimal.ZERO) > 0)
                    {
                        //
                        ErrorResult result = mPayApiMgr.doBusinessRecharge(accountType, currencyType, businessType, orderInfo.getNo(), userInfo, winMoney,null);
                        if(result == SystemErrorResult.SUCCESS)
                        {
                            mLotteryOrderService.updateTxStatusToRealized(userInfo.getId(), mRocketType, orderInfo.getNo(), openResult, winMoney, null, null);

                            // stats
                            if(userType == UserInfo.UserType.MEMBER)
                            {
                                totalWinCount.incrementAndGet();
                                maps.put(totalWinAmountKey, totalWinAmount.add(winMoney));
                            }
                        }
                        return;
                    }

                    // 未中奖，直接修改状态为失败
                    mLotteryOrderService.updateTxStatusToFailed(userInfo.getId(), mRocketType, orderInfo.getNo(), openResult, null);
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
        mPeriodService.updateAmount(mRocketType, issue, totalBetAmount, totalWinAmount, totalfeeAmount, totalBetCount.get(), totalWinCount.get(), null);
    }

    public String getOpenResult(NewLotteryPeriodInfo model, float platformRate, int smartNum)
    {
        GameOpenMode mode = GameOpenMode.getType(model.getOpenMode());
        if(mode == GameOpenMode.RANDOM)
        {
            updateOpenMode(model, mode, GameOpenMode.RANDOM);
            platformRate = getRandomProfitRate();
//            return RocketBetItemType.randomItem();
        }
        else if(mode == GameOpenMode.MANUAL)
        {
            return model.getOpenResult();
        }
        else if(mode == GameOpenMode.SMART)
        {
            int randomNum = RandomUtils.nextInt(9) + 1;
            if(randomNum < smartNum)
            {
                updateOpenMode(model, mode, GameOpenMode.RANDOM);
                platformRate = getRandomProfitRate();
                // 智能开奖，小于只能数就随机开奖
//                return RocketBetItemType.randomItem();
            }
        }
        // valid platformrate
        if(platformRate > 1 || platformRate < 0)
        {
            platformRate = getRandomProfitRate();
        }

        updateOpenMode(model, mode, GameOpenMode.RATE);
        return getByCalcAllOrder(model, platformRate);
    }

    private float getRandomProfitRate()
    {
        int randomvalue = RandomUtils.nextInt(15) + 10;
        float rs = (float) randomvalue / 100f;
        return rs;
    }

    private void updateOpenMode(NewLotteryPeriodInfo model, GameOpenMode dbMode, GameOpenMode currentOpenMode)
    {
        if(dbMode != currentOpenMode)
        {
            // 随机开奖
            mPeriodService.updateOpenMode(mRocketType, model.getIssue(), currentOpenMode);
        }
    }

    private String getByCalcAllOrder(NewLotteryPeriodInfo model, float platformRate)
    {
        //
        LinkedHashMap<String, Object> maps = getCalcMaps();
        mLotteryOrderService.queryAllByIssue(mRocketType, model.getIssue(), new Callback<NewLotteryOrderInfo>() {
            public void execute(NewLotteryOrderInfo orderInfo) {
//                TurntableBetItemType betItemType = TurntableBetItemType.getType(orderInfo.getBetItem());
                /**同个单号同种下注类型只有第一个设置为YES_ADD添加到下注总金额,其他的设置为No_ADD**/
                doCalcAmount(maps, orderInfo.getTotalBetAmount(), orderInfo.getBetItem(), orderInfo.getBetItem(),true);
            }
        });

        //
        String rsOpenResult = doCalcOpenResult(maps, platformRate);
        return rsOpenResult;
    }

    private String doCalcOpenResult(LinkedHashMap<String, Object> maps, float platformRate)
    {
        BigDecimal totalBetAmount = (BigDecimal) maps.get("totalBetAmount");
        maps.remove("totalBetAmount");

        if(totalBetAmount == null)
        {
            return RocketBetItemType.randomItem();
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

                float expectRate1 = calcItem1.getExpectRate();
                float expectRate2 = calcItem2.getExpectRate();

                if(expectRate1 > expectRate2 )
                {
                    return 1;
                }
                else if(expectRate1 < expectRate2 )
                {
                    return -1;
                }

//                else if(calcItem1.getPlatformProfitRate() > calcItem2.getPlatformProfitRate())
//                {
//                    return 1;
//                }
//                else if(calcItem1.getPlatformProfitRate() < calcItem2.getPlatformProfitRate())
//                {
//                    return -1;
//                }
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
            CalcItem item = (CalcItem)list.get(0).getValue();
            String rs = item.getStringOpenResult();
            if(debug)
            {
                System.out.println("total open result len = " + equalLen + ", totalBetAmount = " + totalBetAmount.toString() + ", openResult = " + rs);
            }
            return rs;
        }

        int rsOpenResultValue = RandomUtils.nextInt(equalLen + 1);
        CalcItem rsItem = (CalcItem)list.get(rsOpenResultValue).getValue();

        if(debug)
        {
            System.out.println("total open result len = " + equalLen + ", totalBetAmount = " + totalBetAmount.toString() + ", " + rsItem.getStringOpenResult());
            for(int i = 0; i < 5; i ++)
            {
                int rsOpenResult = RandomUtils.nextInt(equalLen + 1);
                CalcItem item = (CalcItem)list.get(rsOpenResult).getValue();
                System.out.println("debug open result = " + item.getStringOpenResult());
            }
        }


//        int rsOpenResult = 1;

        return rsItem.getStringOpenResult();
    }

    private void doCalcAmount(LinkedHashMap<String, Object> maps, BigDecimal betAmount, String betItemType, String openResult, boolean isIncreTotalBetAmount)
    {
        float openResultValue = StringUtils.asFloat(openResult);
        String maxOpenResult = RocketHelper.getMaxBetItemValue(false, openResultValue);

        String key = maxOpenResult;
        openResultValue = StringUtils.asFloat(maxOpenResult);

        Object value = maps.get(key);
        if(value == null)
        {
            CalcItem tmpValue = new CalcItem();
            value = tmpValue;
        }
        CalcItem item = (CalcItem) value;
        item.setOpenResult(openResultValue);

        item.increBetAmount(betAmount);
        maps.put(key, item);

        String totalBetAmountKey = "totalBetAmount";
        BigDecimal totalBetAmount = (BigDecimal) maps.getOrDefault(totalBetAmountKey, BigDecimal.ZERO);
        /**判断是否加入下注总金额*/
        if(isIncreTotalBetAmount){
            totalBetAmount = totalBetAmount.add(betAmount);
            maps.put(totalBetAmountKey, totalBetAmount);
        }
    }

    private void doCalcProfit(LinkedHashMap<String, Object> maps)
    {
        String totalBetAmountKey = "totalBetAmount";
        Set<String> keys = maps.keySet();
        for(String key : keys)
        {
            if(key.equalsIgnoreCase(totalBetAmountKey))
            {
                continue;
            }

            CalcItem value = (CalcItem)maps.get(key);
            BigDecimal totalBetAmount = BigDecimal.ZERO;
            if(value.getFloatOpenResult() > 0)
            {
                totalBetAmount = BigDecimalUtils.getNotNull(value.getBetAmount());
            }

            for(String key2 : keys)
            {
                if(key2.equalsIgnoreCase(totalBetAmountKey))
                {
                    continue;
                }

                if(key.equalsIgnoreCase(key2))
                {
                    continue;
                }

                CalcItem tmpItem = (CalcItem)maps.get(key2);
                if(tmpItem.getFloatOpenResult() < value.getFloatOpenResult() && value.getFloatOpenResult() > 0)
                {
                    totalBetAmount = totalBetAmount.add(BigDecimalUtils.getNotNull(tmpItem.getBetAmount()));
                }
            }

            BigDecimal winAmount = totalBetAmount.multiply(BigDecimal.valueOf(value.getFloatOpenResult()));
            value.setWinAmount(winAmount);
        }
    }

    private class CalcItem{
        private BigDecimal winAmount = BigDecimal.ZERO;
        private BigDecimal betAmount = BigDecimal.ZERO;
        private float openResult;

        private float minOpenValue;
        private float maxOpenValue;
        /*** 平台盈利比例 ***/
        private float platformProfitRate = 1;
        /*** 预期比例 (platformRate - platformProfitRate) ***/
        private float expectRate = 1;

        public void updateRate(BigDecimal totalAmount, float platformRate)
        {
            BigDecimal rs1 = totalAmount.subtract(winAmount);
            BigDecimal rs2 = rs1.divide(totalAmount, 2, BigDecimal.ROUND_HALF_UP);
            this.platformProfitRate = rs2.floatValue();
            this.expectRate = new BigDecimal(platformRate).subtract(rs2).floatValue();
        }

        public void increBetAmount(BigDecimal amount) {
            if(betAmount == null)
            {
                this.betAmount = amount;
            }
            else
            {
                this.betAmount = this.betAmount.add(amount);
            }
        }

        public BigDecimal getWinAmount() {
            return winAmount;
        }

        public void setWinAmount(BigDecimal amount)
        {
            this.winAmount = amount;
        }


        public BigDecimal getBetAmount() {
            return betAmount;
        }

        public float getFloatOpenResult()
        {
            return openResult;
        }

        @JSONField(serialize = false, deserialize = false)
        public String getStringOpenResult() {
            if(minOpenValue == 0 || maxOpenValue == 0)
            {
                return "0";
            }

            int minValue = (int)(minOpenValue * 100);
            int maxValue = (int)(maxOpenValue * 100);
            int result = minValue + RandomUtils.nextInt(maxValue - minValue - 1);
            BigDecimal rsValue = new BigDecimal(result).divide(BigDecimalUtils.DEF_100, 2, RoundingMode.DOWN);
            return rsValue.toString();
        }

        public void setOpenResult(float openResult) {
            this.openResult = openResult;
        }

        public float getPlatformProfitRate() {
            return platformProfitRate;
        }

        public float getExpectRate() {
            return expectRate;
        }

        public float getMinOpenValue() {
            return minOpenValue;
        }

        public void setMinOpenValue(float minOpenValue) {
            this.minOpenValue = minOpenValue;
        }

        public float getMaxOpenValue() {
            return maxOpenValue;
        }

        public void setMaxOpenValue(float maxOpenValue) {
            this.maxOpenValue = maxOpenValue;
        }
    }

    private  LinkedHashMap<String, Object> getCalcMaps()
    {
        LinkedHashMap<String, Object> maps = Maps.newLinkedHashMap();

        float zero = 0;
        String zeroMaxOopenResult = RocketHelper.getMaxBetItemValue(false, zero);
        CalcItem zeroItem = new CalcItem();
        zeroItem.setOpenResult(zero);
        zeroItem.setMinOpenValue(0);
        zeroItem.setMaxOpenValue(0);
        maps.put(zeroMaxOopenResult, zeroItem);

        int len = RocketHelper.mBetItemSequenceArr.length;
        for(int i = 0; i < len; i ++)
        {
            float value = RocketHelper.mBetItemSequenceArr[i];
            String key = value + StringUtils.getEmpty();

            CalcItem item = new CalcItem();
            item.setMinOpenValue(value);

            float maxOpenValue = 0;
            if(i >= len - 1)
            {
                maxOpenValue = RocketHelper.mBetItem_MaxValue;
            }
            else
            {
                maxOpenValue = RocketHelper.mBetItemSequenceArr[i + 1];
            }
            item.setMaxOpenValue(maxOpenValue);
            item.setOpenResult(maxOpenValue);

            maps.put(key, item);
        }
        return maps;
    }

    public static void main(String[] args) {
        RocketOpenTaskManager mgr = new RocketOpenTaskManager();
        LinkedHashMap<String, Object> maps = mgr.getCalcMaps();
        for(int i = 0; i < 100; i ++)
        {
            String betItem = RocketBetItemType.randomItem();
            betItem = StringUtils.getEmpty();
            long basicAmountValue = 10;
            if(RandomUtils.nextBoolean())
            {
                basicAmountValue = 100;
            }
            BigDecimal betAmount = new BigDecimal(basicAmountValue);
//            mgr.doCalcAmount(maps, betAmount, betItem, betAmount, 1,  betItem,true);
        }

        mgr.doCalcProfit(maps);

//        BigDecimal betAmount = new BigDecimal(100);
//        mgr.doCalcAmount(maps, betAmount, "2", betAmount, 1, "0", true);
//        mgr.doCalcAmount(maps, betAmount, TurntableBetItemType.Small.getKey(), betAmount, 1, "0", true);

        mgr.debug = true;
        String openResult = mgr.doCalcOpenResult(maps, 0.1f);
        System.out.println("rs open result = " + openResult);

    }

}
