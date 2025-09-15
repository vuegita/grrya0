package com.inso.modules.game.lottery_game_impl.turntable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RandomUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.lottery_game_impl.BaseLotterySupport;
import com.inso.modules.game.lottery_game_impl.NewLotteryPeriodStatus;
import com.inso.modules.game.lottery_game_impl.job.MyLotteryBeginJob;
import com.inso.modules.game.lottery_game_impl.turntable.config.TurntableConfig;
import com.inso.modules.game.lottery_game_impl.turntable.helper.TurntableHelper;
import com.inso.modules.game.lottery_game_impl.turntable.model.TurnTableType;
import com.inso.modules.game.lottery_game_impl.turntable.model.TurntableBetItemType;
import com.inso.modules.game.model.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * 开奖管理器
 */
@Component
public class TurntableProcessorImpl extends BaseLotterySupport {


    private static float DEFAULT_PLATFORM_RATE = 0.8f;

    private static TurntableBetItemType[] mBetItemTypeArray = TurntableBetItemType.values();


    private long mOpenResultOccurCount = 0;
    private TurntableBetItemType mLastOpenResult = null;

    private boolean debug = false;

    private List<RealtimeBetItemReport> mZeroBetItemReport;

    @Override
    public GameChildType[] getAllGameTypes() {
        return TurnTableType.mArr;
    }

    @Override
    public void onGameStart(NewLotteryPeriodInfo periodInfo, NewLotteryPeriodStatus periodStatus) {
        if(!periodStatus.isInit())
        {
            String maxMoneyOfIssueValue = mConfigService.getValueByKey(false, TurntableConfig.GAME_TURNTABLE_MAX_MONEY_OF_ISSUE);
            String maxMoneyOfUserValue = mConfigService.getValueByKey(false, TurntableConfig.GAME_TURNTABLE_MAX_MONEY_OF_USER);
            periodStatus.setStartTime(periodInfo.getStarttime());
            periodStatus.setEndTime(periodInfo.getEndtime());
            periodStatus.setLimitMaxMoneyOfIssue(StringUtils.asFloat(maxMoneyOfUserValue));
            periodStatus.setLimitMaxMoneyOfUser(StringUtils.asFloat(maxMoneyOfIssueValue));
            periodStatus.saveCache();
        }

        //
        long openTime = periodInfo.getEndtime().getTime() - 1000;
        MyLotteryBeginJob.bootEndTaskJob(periodInfo.getType(), periodInfo.getIssue(), new Date(openTime));

    }

    @Override
    public List<RealtimeBetItemReport> initRealBetItemReport(boolean fetchZero) {
        if(fetchZero)
        {
            if(mZeroBetItemReport == null)
            {
                this.mZeroBetItemReport = createRealBetItemReport();
            }
            return this.mZeroBetItemReport;
        }
        return createRealBetItemReport();
    }

    @Override
    public void handleStatsBetItem(String username, GameChildType gameChildType, Map<String, RealtimeBetItemReport> betItemReportMap, String betItem, BigDecimal betAmount, BigDecimal feemoney) {
//        TurntableBetItemType betItemType = TurntableBetItemType.getType(betItem);
        RealtimeBetItemReport report = betItemReportMap.get(betItem);
        BigDecimal winAmount = calcWinAmount(betItem, betAmount, betItem);
        report.incre(betAmount, winAmount, feemoney, false);
    }

    public List<RealtimeBetItemReport> createRealBetItemReport() {
        List<RealtimeBetItemReport> rsList = Lists.newArrayList();
        for(TurntableBetItemType tmp : TurntableBetItemType.mBetItemArr)
        {
            rsList.add(new RealtimeBetItemReport(tmp.getKey().toLowerCase()));
        }
        return rsList;
    }

    public boolean isWin(String openResult, String betItem)
    {
        return TurntableHelper.isWin(openResult, betItem);
    }

    @Override
    public BigDecimal calcWinAmount(String openResult, BigDecimal betAmount, String betItem) {
        TurntableBetItemType openResultItem = TurntableBetItemType.getType(openResult);
        TurntableBetItemType betItemType = TurntableBetItemType.getType(betItem);
        return TurntableHelper.calcWinMoney(betAmount, openResultItem, betItemType);
    }


    @Override
    public BigDecimal calcWinAmount(NewLotteryOrderInfo orderInfo, String openResult) {

        int winCount = 0;
        TurntableBetItemType openResultItem = TurntableBetItemType.getType(openResult);
        BigDecimal winAmount = BigDecimal.ZERO;
        long betCount = orderInfo.getTotalBetCount();
        if(betCount == 1)
        {
            TurntableBetItemType betItemType = TurntableBetItemType.getType(orderInfo.getBetItem());
            winAmount = TurntableHelper.calcWinMoney(orderInfo.getTotalBetAmount(), openResultItem, betItemType);

            if(winAmount != null || winAmount.compareTo(BigDecimal.ZERO) > 0)
            {
                winCount = 1;
            }
        }
        else
        {

            String[] betItemArr = StringUtils.split(orderInfo.getBetItem(), ',');
            for(String tmp : betItemArr)
            {
                TurntableBetItemType betItemType = TurntableBetItemType.getType(tmp);
                BigDecimal tmpAmount = TurntableHelper.calcWinMoney(orderInfo.getSingleBetAmount(), openResultItem, betItemType);
                if(tmpAmount == null || tmpAmount.compareTo(BigDecimal.ZERO) <= 0)
                {
                    continue;
                }

                winCount ++;
                winAmount = winAmount.add(tmpAmount);
            }
        }

        orderInfo.setTmpCalcWinCount(winCount);
        return winAmount;
    }

    @Override
    public String getReference(NewLotteryPeriodInfo periodInfo, GameChildType gameChildType) {

        // 设置开奖模式
        String openModeString = mConfigService.getValueByKey(false, TurntableConfig.GAME_TURNTABLE_OPEN_MODE);
        periodInfo.setOpenMode(openModeString);

        String openRate = mConfigService.getValueByKey(false, TurntableConfig.GAME_TURNTABLE_OPEN_RATE);

        // 如果设置为比例
        float platformRate = StringUtils.asFloat(openRate);

        int smartNum=mConfigService.getInt(false, TurntableConfig.GAME_TURNTABLE_OPEN_SMART_NUM);

        TurntableBetItemType openResult = getOpenResult(periodInfo, platformRate, smartNum);

        if(openResult == null)
        {
            return null;
        }
        return openResult.getKey();
    }

    @Override
    public String getOpenResult(String reference) {
        return reference;
    }

    @Override
    public long getOpenIndex(String openResult) {
        TurntableBetItemType betItemType = TurntableBetItemType.getType(openResult);
        return TurntableBetItemType.randomOpenIndex(betItemType);
    }

    @Override
    public GameOpenMode getOpenMode(GameChildType gameChildType) {
        String openModeString = mConfigService.getValueByKey(false, TurntableConfig.GAME_TURNTABLE_OPEN_MODE);
        return GameOpenMode.getType(openModeString);
    }

    public TurntableBetItemType getOpenResult(NewLotteryPeriodInfo model, float platformRate, int smartNum)
    {
        GameOpenMode mode = GameOpenMode.getType(model.getOpenMode());
        if(mode == GameOpenMode.RANDOM)
        {
            updateOpenMode(model, mode, GameOpenMode.RANDOM);
            return getOpenResultByRandom();
        }
        if(mode == GameOpenMode.SMART)
        {
            int randomNum = RandomUtils.nextInt(9) + 1;
            if(randomNum < smartNum)
            {
                updateOpenMode(model, mode, GameOpenMode.RANDOM);
                // 智能开奖，小于只能数就随机开奖
                return getOpenResultByRandom();
            }
        }
        // valid platformrate
        if(platformRate > 1 || platformRate < 0)
        {
            platformRate = DEFAULT_PLATFORM_RATE;
        }

        updateOpenMode(model, mode, GameOpenMode.RATE);
        return getByCalcAllOrder(model, platformRate);
    }

    private void updateOpenMode(NewLotteryPeriodInfo model, GameOpenMode dbMode, GameOpenMode currentOpenMode)
    {
        if(dbMode != currentOpenMode)
        {
            GameChildType gameChildType = GameChildType.getType(model.getType());
            // 随机开奖
            mPeriodService.updateOpenMode(gameChildType, model.getIssue(), currentOpenMode);
        }
    }

    private TurntableBetItemType getOpenResultByRandom()
    {
        TurntableBetItemType openResult = TurntableBetItemType.randomItem2();

        try {
            if(!RandomUtils.nextBoolean())
            {
                // 决定是否判断重复
                return openResult;
            }

            if(mLastOpenResult != null && openResult == mLastOpenResult)
            {
                // 随机是否重新随机
                // 强制随机
                if(mOpenResultOccurCount == 1 && RandomUtils.nextBoolean())
                {
                    openResult = TurntableBetItemType.randomItem2();
                }
            }

            // 第二次相同，重新随机
            if(mLastOpenResult != null && openResult == mLastOpenResult)
            {
                // 强制随机
                if(mOpenResultOccurCount == 2)
                {
                    openResult = TurntableBetItemType.randomItem2();
                }
            }

            // 如果超过3次相同，再次随机
            if(mLastOpenResult != null && openResult == mLastOpenResult)
            {
                if(mOpenResultOccurCount > 2)
                {
                    openResult = TurntableBetItemType.randomItem2();
                }
            }
        } finally {
            if(mLastOpenResult != null && openResult == mLastOpenResult)
            {
                mOpenResultOccurCount ++;
            }
            else
            {
                mOpenResultOccurCount = 0;
            }
        }

        mLastOpenResult = openResult;
        return openResult;
    }

    private TurntableBetItemType getByCalcAllOrder(NewLotteryPeriodInfo model, float platformRate)
    {
        //

        GameChildType gameChildType = GameChildType.getType(model.getType());

        LinkedHashMap<String, Object> maps = getCalcMaps();
        TurntableBetItemType[] betItemTypes = TurntableBetItemType.values();
        for (TurntableBetItemType type : betItemTypes)
        {
            CalcItem item = new CalcItem();
            item.setOpenResult(type);
            maps.put(type.getKey(), item);
        }

        mAOrderService.queryAllByIssue(gameChildType, model.getIssue(), new Callback<NewLotteryOrderInfo>() {
            public void execute(NewLotteryOrderInfo orderInfo) {
                TurntableBetItemType betItemType = TurntableBetItemType.getType(orderInfo.getBetItem());
                doCalcAmount(maps, orderInfo.getTotalBetAmount(), betItemType);
            }
        });

        //
        TurntableBetItemType rsOpenResult = doCalcOpenResult(maps, platformRate);
        return rsOpenResult;
    }

    private TurntableBetItemType doCalcOpenResult(LinkedHashMap<String, Object> maps, float platformRate)
    {
        BigDecimal totalBetAmount = (BigDecimal) maps.get(KEY_TOTALBETAMOUNT);
        maps.remove(KEY_TOTALBETAMOUNT);

        if(totalBetAmount == null)
        {
            return getOpenResultByRandom();
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
            CalcItem firstItem = (CalcItem)list.get(0).getValue();
            CalcItem secondItem = (CalcItem)list.get(1).getValue();
            if(debug)
            {
                System.out.println("total open result len = " + equalLen + ", totalBetAmount = " + totalBetAmount.toString() + ", platform win = " + totalBetAmount.subtract(firstItem.getWinAmount()));
            }
            return getOpenResultByItem(firstItem, secondItem);
        }
        int rsOpenResult = RandomUtils.nextInt(equalLen + 1);
        int rsSecondOpenResult = 0;
        if(rsOpenResult == 0)
        {
            rsSecondOpenResult = 1;
        }
        CalcItem firstItem = (CalcItem)list.get(rsOpenResult).getValue();
        CalcItem secondItem = (CalcItem)list.get(rsSecondOpenResult).getValue();
        return getOpenResultByItem(firstItem, secondItem);
    }

    private TurntableBetItemType getOpenResultByItem(CalcItem firstItem, CalcItem secondItem)
    {
        // 进入这里已经是属于比例开奖，但是如果计算结果是开Tie，则 1/10 杀
        if( firstItem.openResult == TurntableBetItemType.CHEST) {
            int number = RandomUtils.nextInt(20);
            if(number== 8){
                return TurntableBetItemType.CHEST;
            }
            if(secondItem != null)
            {
                return secondItem.getOpenResult();
            }
        }
        return firstItem.getOpenResult();
    }

    private void doCalcAmount(LinkedHashMap<String, Object> maps, BigDecimal betAmount, TurntableBetItemType betItem)
    {
//        System.out.println("betItem " + betItem.getKey() + ", openResult = " + openResult.getKey());
        String key = betItem.getKey();
        Object value = maps.get(key);
        if(value == null)
        {
            value = new CalcItem();
        }
        CalcItem item = (CalcItem) value;
        item.setOpenResult(betItem);
        BigDecimal winmoney = TurntableHelper.calcWinMoney(betAmount, betItem.getKey(), betItem.getKey());
        item.increAmount(winmoney);
        maps.put(key, item);

        String totalBetAmountKey = KEY_TOTALBETAMOUNT;
        BigDecimal totalBetAmount = (BigDecimal) maps.getOrDefault(totalBetAmountKey, BigDecimal.ZERO);
        totalBetAmount = totalBetAmount.add(betAmount);
        maps.put(totalBetAmountKey, totalBetAmount);
    }



    private class CalcItem{
        private BigDecimal winAmount = BigDecimal.ZERO;
        private TurntableBetItemType openResult;
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

        public TurntableBetItemType getOpenResult() {
            return openResult;
        }

        public void setOpenResult(TurntableBetItemType openResult) {
            this.openResult = openResult;
        }

        public float getPlatformProfitRate() {
            return platformProfitRate;
        }

        public float getExpectRate() {
            return expectRate;
        }
    }

    private  LinkedHashMap<String, Object> getCalcMaps()
    {
        LinkedHashMap<String, Object> maps = Maps.newLinkedHashMap();
        for(TurntableBetItemType tmp : mBetItemTypeArray)
        {
            CalcItem item = new CalcItem();
            item.setOpenResult(tmp);
            maps.put(tmp.getKey(), item);
        }
        return maps;
    }

    public static void main(String[] args) {
        TurntableProcessorImpl mgr = new TurntableProcessorImpl();
        LinkedHashMap<String, Object> maps = mgr.getCalcMaps();
        BigDecimal amount = new BigDecimal(100);
        for(int i = 0; i < 500; i ++)
        {
            int openResult = RandomUtils.nextInt(3);
//            int betItem = RandomUtils.nextInt(3);
            long betCount = RandomUtils.nextInt(999) + 1;
            long basicAmountValue = 10;
            if(RandomUtils.nextBoolean())
            {
                basicAmountValue = 100;
            }
            BigDecimal basicAmount = new BigDecimal(basicAmountValue);
            mgr.doCalcAmount(maps, basicAmount, TurntableBetItemType.randomItem2());
        }

        mgr.debug = true;
        TurntableBetItemType openResult = mgr.doCalcOpenResult(maps, 0.85f);
        System.out.println("rs open result = " + openResult.getKey());
    }

}
