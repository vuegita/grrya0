package com.inso.modules.game.lottery_game_impl.rg2;

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
import com.inso.modules.game.lottery_game_impl.rg2.model.LotteryRgBetItemType;
import com.inso.modules.game.lottery_game_impl.rg2.model.RedGreen2Type;
import com.inso.modules.game.model.GameOpenMode;
import com.inso.modules.game.model.NewLotteryOrderInfo;
import com.inso.modules.game.model.NewLotteryPeriodInfo;
import com.inso.modules.game.model.RealtimeBetItemReport;
import com.inso.modules.game.rg.config.LotteryConfig;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * 开奖管理器
 */
@Component
public class RedGreen2ProcessorImpl extends BaseLotterySupport {

    private static float DEFAULT_PLATFORM_RATE = 0.8f;
    private boolean debug = false;

    /**
     * 添加到总下注金额
     * */
    private static Boolean isIncreseTotalBetAmount = true;
    /**
     * 不添加到总下注金额
     * */
    private static Boolean isIncreTotalBetAmount = false;

    private List<RealtimeBetItemReport> mZeroBetItemReport;

    @Override
    public GameChildType[] getAllGameTypes() {
        return RedGreen2Type.mArr;
    }

    @Override
    public void onGameStart(NewLotteryPeriodInfo periodInfo, NewLotteryPeriodStatus periodStatus) {
        if(!periodStatus.isInit())
        {
            String maxMoneyOfIssueValue = mConfigService.getValueByKey(false, LotteryConfig.GAME_LOTTERY_RG_MAX_MONEY_OF_ISSUE);
            String maxMoneyOfUserValue = mConfigService.getValueByKey(false, LotteryConfig.GAME_LOTTERY_RG_MAX_MONEY_OF_USER);
            periodStatus.setStartTime(periodInfo.getStarttime());
            periodStatus.setEndTime(periodInfo.getEndtime());
            periodStatus.setLimitMaxMoneyOfIssue(StringUtils.asFloat(maxMoneyOfUserValue));
            periodStatus.setLimitMaxMoneyOfUser(StringUtils.asFloat(maxMoneyOfIssueValue));
            periodStatus.saveCache();
        }

        //
        long openTime = periodInfo.getEndtime().getTime() - 2000;
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
        LotteryRgBetItemType betItemType = LotteryRgBetItemType.getType(betItem);

        // 如果中奖能中多少
        BigDecimal winAmountIfRealized = null;
        if(betItemType == LotteryRgBetItemType.RED || betItemType == LotteryRgBetItemType.GREEN)
        {
            // 1 - 9 都可以
            if(betItemType == LotteryRgBetItemType.RED)
            {
                String openResult = "2";
                winAmountIfRealized = calcWinAmount(openResult, betAmount, betItem);
                updateBetItem(betAmount, betItemReportMap, winAmountIfRealized, feemoney, "2", true);
                updateBetItem(betAmount, betItemReportMap, winAmountIfRealized, feemoney, "4", true);
                updateBetItem(betAmount, betItemReportMap, winAmountIfRealized, feemoney, "6", true);
                updateBetItem(betAmount, betItemReportMap, winAmountIfRealized, feemoney, "8", true);

                BigDecimal vioLetAmount = calcWinAmount("0", betAmount, betItem);
                updateBetItem(betAmount, betItemReportMap, vioLetAmount, feemoney, "0", true);
            }
            else
            {
                String openResult = "1";
                winAmountIfRealized = calcWinAmount(openResult, betAmount, betItem);
                updateBetItem(betAmount, betItemReportMap, winAmountIfRealized, feemoney, "1", true);
                updateBetItem(betAmount, betItemReportMap, winAmountIfRealized, feemoney, "3", true);
                updateBetItem(betAmount, betItemReportMap, winAmountIfRealized, feemoney, "7", true);
                updateBetItem(betAmount, betItemReportMap, winAmountIfRealized, feemoney, "9", true);

                BigDecimal vioLetAmount = calcWinAmount("5", betAmount, betItem);
                updateBetItem(betAmount, betItemReportMap, vioLetAmount, feemoney, "5", true);
            }

        }
        else if(betItemType == LotteryRgBetItemType.VIOLET)
        {
            // 0 - 5
            winAmountIfRealized = calcWinAmount("0", betAmount, betItem);// LotteryHelper.calcWinMoney(basicAmount, betCountValue, 0, betItem);

            //
            updateBetItem(betAmount, betItemReportMap, winAmountIfRealized, feemoney, "0", true);
            updateBetItem(betAmount, betItemReportMap, winAmountIfRealized, feemoney, "5", true);
        }
        else
        {
            // 数字
            long openResult = StringUtils.asLong(betItem);
            winAmountIfRealized = calcWinAmount(openResult + StringUtils.getEmpty(), betAmount, betItem);
//            winAmountIfRealized = LotteryHelper.calcWinMoney(basicAmount, betCountValue, openResult, betItem);
        }

         //投注金额累计
         //当前投注
        updateBetItem(betAmount, betItemReportMap, winAmountIfRealized, feemoney, betItem, false);
//        RealtimeBetItemReport rgRealtimeBetItem = (RealtimeBetItemReport)betItemReportMap.get(betItem);
//        rgRealtimeBetItem.incre(betAmount, winAmountIfRealized, feemoney);
    }

    public List<RealtimeBetItemReport> createRealBetItemReport() {
        List<RealtimeBetItemReport> rsList = Lists.newArrayList();

        for(int i = 0; i < 10; i ++)
        {
            rsList.add(new RealtimeBetItemReport(i + StringUtils.getEmpty()));
        }
        for(LotteryRgBetItemType tmp : LotteryRgBetItemType.mArr)
        {
            RealtimeBetItemReport realtimeBetItemReport = new RealtimeBetItemReport(tmp.getKey().toLowerCase());
            realtimeBetItemReport.setOpenThisResult(false);
            rsList.add(realtimeBetItemReport);
        }

        return rsList;
    }

    public boolean isWin(String openResult, String betItem)
    {
        long rsResult = StringUtils.asLong(openResult);
        return Rg2Helper.isWin(rsResult, betItem);
    }

    @Override
    public BigDecimal calcWinAmount(String openResult, BigDecimal betAmount, String betItem) {
        long rsResult = StringUtils.asLong(openResult);
        BigDecimal winAmount = Rg2Helper.calcWinMoney(betAmount, rsResult, betItem);
        return winAmount;
    }


    @Override
    public BigDecimal calcWinAmount(NewLotteryOrderInfo orderInfo, String openResult) {

        int winCount = 0;
        BigDecimal winAmount = BigDecimal.ZERO;
        long betCount = orderInfo.getTotalBetCount();
        if(betCount == 1)
        {
            winAmount = calcWinAmount(openResult, orderInfo.getTotalBetAmount(), orderInfo.getBetItem());
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
                BigDecimal tmpAmount = calcWinAmount(openResult, orderInfo.getSingleBetAmount(), tmp);
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
    public String getReference(NewLotteryPeriodInfo model, GameChildType gameChildType) {
        RedGreen2Type gameType = (RedGreen2Type) gameChildType;

        GameOpenMode mode = GameOpenMode.getType(model.getOpenMode());
        if(mode == GameOpenMode.RANDOM)
        {
            updateOpenMode(model, mode, GameOpenMode.RANDOM, gameChildType);
            int rs = RandomUtils.nextInt(10);
            return gameType.getReferencePrice( rs + StringUtils.getEmpty());
        }
        else if(mode == GameOpenMode.MANUAL)
        {
            return gameType.getReferencePrice(model.getOpenResult());
        }
        else if(mode == GameOpenMode.SMART)
        {
            int smartNum = mConfigService.getInt(false, LotteryConfig.GAME_LOTTERY_RG_OPEN_SMART_NUM);
            int randomNum = RandomUtils.nextInt(9) + 1;
            if(randomNum < smartNum)
            {
                updateOpenMode(model, mode, GameOpenMode.RANDOM, gameChildType);
                // 智能开奖，小于只能数就随机开奖
                int rs = RandomUtils.nextInt(10);
                return gameType.getReferencePrice(rs + StringUtils.getEmpty());
            }
        }

        float platformRate = mConfigService.getFloat(false, LotteryConfig.GAME_LOTTERY_RG_OPEN_RATE);
        // valid platformrate
        if(platformRate > 1 || platformRate < 0)
        {
            platformRate = DEFAULT_PLATFORM_RATE;
        }

        updateOpenMode(model, mode, GameOpenMode.RATE, gameChildType);
        String rs = getByCalcAllOrder(model, platformRate, gameChildType);
        return gameType.getReferencePrice(rs);

    }

    @Override
    public String getOpenResult(String reference) {
        int closeLen = reference.length();
        int endCloseValue = StringUtils.asInt(reference.substring(closeLen - 1, closeLen));
        return endCloseValue + StringUtils.getEmpty();
    }

    @Override
    public long getOpenIndex(String openResult) {
        return -1;
    }

    @Override
    public GameOpenMode getOpenMode(GameChildType gameChildType) {
        String openModeString = mConfigService.getValueByKey(false, LotteryConfig.GAME_LOTTERY_RG_OPEN_MODE);
        return GameOpenMode.getType(openModeString);
    }

    private void updateOpenMode(NewLotteryPeriodInfo model, GameOpenMode dbMode, GameOpenMode currentOpenMode, GameChildType gameChildType)
    {
        if(dbMode != currentOpenMode)
        {
            // 随机开奖
            mPeriodService.updateOpenMode(gameChildType, model.getIssue(), currentOpenMode);
        }
    }

    private String getByCalcAllOrder(NewLotteryPeriodInfo model, float platformRate, GameChildType gameChildType)
    {
        //
        char split = ',';
        LinkedHashMap<String, Object> maps = getCalcMaps();
        mAOrderService.queryAllByIssue(gameChildType, model.getIssue(), new Callback<NewLotteryOrderInfo>() {
            public void execute(NewLotteryOrderInfo orderInfo) {

                String[] betItemArr = StringUtils.split(orderInfo.getBetItem(), split);
                for(String tmp : betItemArr)
                {

                    LotteryRgBetItemType betItemType = LotteryRgBetItemType.getType(orderInfo.getBetItem());
                    /**同个单号同种下注类型只有第一个设置为YES_ADD添加到下注总金额,其他的设置为No_ADD**/
                    if(betItemType == LotteryRgBetItemType.RED)
                    {
                        // 2 | 4 | 6 | 8
                        doCalcAmount(maps, orderInfo.getSingleBetAmount(),"2", tmp, isIncreseTotalBetAmount);
                        doCalcAmount(maps, orderInfo.getSingleBetAmount(),"4", tmp, isIncreTotalBetAmount);
                        doCalcAmount(maps, orderInfo.getSingleBetAmount(),"6", tmp, isIncreTotalBetAmount);
                        doCalcAmount(maps, orderInfo.getSingleBetAmount(),"8", tmp, isIncreTotalBetAmount);

                        // 1.5倍
                        doCalcAmount(maps, orderInfo.getSingleBetAmount(),"0", tmp, isIncreTotalBetAmount);
                    }
                    else if(betItemType == LotteryRgBetItemType.GREEN)
                    {
                        // 2 | 4 | 6 | 8
                        doCalcAmount(maps, orderInfo.getSingleBetAmount(),"1", tmp, isIncreseTotalBetAmount);
                        doCalcAmount(maps, orderInfo.getSingleBetAmount(),"3", tmp, isIncreTotalBetAmount);
                        doCalcAmount(maps, orderInfo.getSingleBetAmount(),"7", tmp, isIncreTotalBetAmount);
                        doCalcAmount(maps, orderInfo.getSingleBetAmount(),"9", tmp, isIncreTotalBetAmount);

                        // 1.5倍
                        doCalcAmount(maps, orderInfo.getSingleBetAmount(),"5", tmp, isIncreTotalBetAmount);
                    }
                    else if(betItemType == LotteryRgBetItemType.VIOLET)
                    {
                        // 0 | 5
                        doCalcAmount(maps, orderInfo.getSingleBetAmount(),"0", tmp, isIncreseTotalBetAmount);
                        doCalcAmount(maps, orderInfo.getSingleBetAmount(),"5", tmp, isIncreTotalBetAmount);
                    }
                    else
                    {
                        // number 0 ~ 9
                        String tmpOpenResult = orderInfo.getBetItem();
                        doCalcAmount(maps, orderInfo.getSingleBetAmount(),tmpOpenResult, tmp, isIncreseTotalBetAmount);
                    }

                }

            }
        });

        //
        return doCalcOpenResult(maps, platformRate);
    }

    private String doCalcOpenResult(LinkedHashMap<String, Object> maps, float platformRate)
    {
        BigDecimal totalBetAmount = (BigDecimal) maps.get("totalBetAmount");
        maps.remove("totalBetAmount");

        if(totalBetAmount == null)
        {
            int rs = RandomUtils.nextInt(10);
            return rs + StringUtils.getEmpty();
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

    private void doCalcAmount(LinkedHashMap<String, Object> maps, BigDecimal betAmount, String openResult, String betItem, boolean isIncreTotalBetAmount)
    {
        String key = openResult;
        Object value = maps.get(openResult);
        if(value == null)
        {
            value = new CalcItem();
        }
        CalcItem item = (CalcItem) value;
        item.setOpenResult(openResult);
        BigDecimal winmoney = calcWinAmount(openResult, betAmount, betItem);
        item.increAmount(winmoney);
        maps.put(key, item);

        String totalBetAmountKey = "totalBetAmount";
        BigDecimal currentBetAmount = betAmount;
        BigDecimal totalBetAmount = (BigDecimal) maps.getOrDefault(totalBetAmountKey, BigDecimal.ZERO);
        /**判断是否加入下注总金额*/
        if(isIncreTotalBetAmount){
            totalBetAmount = totalBetAmount.add(currentBetAmount);
        }

        maps.put(totalBetAmountKey, totalBetAmount);
    }

    private class CalcItem{
        private BigDecimal winAmount = BigDecimal.ZERO;
        private String openResult;
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

        public String getOpenResult() {
            return openResult;
        }

        public void setOpenResult(String openResult) {
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
        for(int i = 0; i < 10; i ++)
        {
            CalcItem item = new CalcItem();
            item.setOpenResult(i + StringUtils.getEmpty());
            maps.put(i + StringUtils.getEmpty(), item);
        }
        return maps;
    }

    private void updateBetItem(BigDecimal betAmount, Map<String, RealtimeBetItemReport> betItemReportMap, BigDecimal winAmount, BigDecimal feemoney, String betItem, boolean onlyUpdateWinAmount)
    {
        RealtimeBetItemReport rgRealtimeBetItem = (RealtimeBetItemReport)betItemReportMap.get(betItem);
        rgRealtimeBetItem.incre(betAmount, winAmount, feemoney, onlyUpdateWinAmount);
    }

}
