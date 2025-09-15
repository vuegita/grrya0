package com.inso.modules.game.lottery_game_impl.btc_kline;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.utils.StringUtils;
import com.inso.framework.utils.ThreadUtils;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.lottery_game_impl.BaseLotterySupport;
import com.inso.modules.game.lottery_game_impl.NewLotteryPeriodStatus;
import com.inso.modules.game.lottery_game_impl.btc_kline.config.BTCKlineConfig;
import com.inso.modules.game.lottery_game_impl.btc_kline.helper.BTCKlineHelper;
import com.inso.modules.game.lottery_game_impl.btc_kline.helper.BTCResultHelper;
import com.inso.modules.game.lottery_game_impl.btc_kline.model.BTCKlineBetItemType;
import com.inso.modules.game.lottery_game_impl.btc_kline.model.BTCKlineType;
import com.inso.modules.game.lottery_game_impl.job.MyLotteryBeginJob;
import com.inso.modules.game.model.GameOpenMode;
import com.inso.modules.game.model.NewLotteryOrderInfo;
import com.inso.modules.game.model.NewLotteryPeriodInfo;
import com.inso.modules.game.model.RealtimeBetItemReport;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 开奖管理器
 */
@Component
public class BTCKlineProcessorImpl extends BaseLotterySupport {

    public static final String DATE_TYPE_YYYYMMDDHHMM = "yyyyMMddHHmm";


    private List<RealtimeBetItemReport> mZeroBetItemReport;

    @Override
    public GameChildType[] getAllGameTypes() {
        return BTCKlineType.mArr;
    }


    @Override
    public boolean isWin(String openResultValue, String betItem) {

        int rsOpenResult = StringUtils.asInt(openResultValue);
        BTCKlineBetItemType betItemType = BTCKlineBetItemType.getType(betItem);
        if(BTCKlineBetItemType.Big == betItemType)
        {
            if(rsOpenResult >= 5)
            {
                return true;
            }
        }
        else if(BTCKlineBetItemType.Small == betItemType)
        {
            if(rsOpenResult >= 5)
            {
                return true;
            }
        }
        else if(BTCKlineBetItemType.Odd == betItemType)
        {
            // (0 5 ) * 4.5
            if(rsOpenResult % 2 != 0)
            {
                return true;
            }
        }
        else if(BTCKlineBetItemType.Even == betItemType)
        {
            // (0 5 ) * 4.5
            if(rsOpenResult % 2 == 0)
            {
                return true;
            }
        }
        else
        {
            // amount * 9
            int betNumber = StringUtils.asInt(betItem);
            if(betNumber == rsOpenResult)
            {
                return true;
            }
        }
        return false;

    }

    @Override
    public BigDecimal calcWinAmount(String openResult, BigDecimal betAmount, String betItem) {
        return BTCKlineHelper.calcWinMoney(betAmount, openResult, betItem, true);
    }

    @Override
    public void onGameStart(NewLotteryPeriodInfo periodInfo, NewLotteryPeriodStatus periodStatus) {
        if(!periodStatus.isInit())
        {
            String maxMoneyOfIssueValue = mConfigService.getValueByKey(false, BTCKlineConfig.GAME__MAX_MONEY_OF_ISSUE);
            String maxMoneyOfUserValue = mConfigService.getValueByKey(false, BTCKlineConfig.GAME_MAX_MONEY_OF_USER);
            periodStatus.setStartTime(periodInfo.getStarttime());
            periodStatus.setEndTime(periodInfo.getEndtime());
            periodStatus.setLimitMaxMoneyOfIssue(StringUtils.asFloat(maxMoneyOfUserValue));
            periodStatus.setLimitMaxMoneyOfUser(StringUtils.asFloat(maxMoneyOfIssueValue));
            periodStatus.saveCache();
        }

        long openTime = periodInfo.getEndtime().getTime();
        if(isDEV)
        {
            openTime += 5_000;
        }
        else
        {
            openTime += 1_800;
        }
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
        BTCKlineBetItemType betItemType = BTCKlineBetItemType.getType(betItem);

        // 如果中奖能中多少
        BigDecimal winAmountIfRealized = null;

        if(betItemType == BTCKlineBetItemType.Big)
        {
            winAmountIfRealized = calcWinAmount("9", betAmount, betItem);

            updateBetItem(betAmount, betItemReportMap, winAmountIfRealized, feemoney, "5", true);
            updateBetItem(betAmount, betItemReportMap, winAmountIfRealized, feemoney, "6", true);
            updateBetItem(betAmount, betItemReportMap, winAmountIfRealized, feemoney, "7", true);
            updateBetItem(betAmount, betItemReportMap, winAmountIfRealized, feemoney, "8", true);
            updateBetItem(betAmount, betItemReportMap, winAmountIfRealized, feemoney, "9", true);
        }
        else if(betItemType == BTCKlineBetItemType.Small)
        {
            winAmountIfRealized = calcWinAmount("1", betAmount, betItem);
            updateBetItem(betAmount, betItemReportMap, winAmountIfRealized, feemoney, "0", true);
            updateBetItem(betAmount, betItemReportMap, winAmountIfRealized, feemoney, "1", true);
            updateBetItem(betAmount, betItemReportMap, winAmountIfRealized, feemoney, "2", true);
            updateBetItem(betAmount, betItemReportMap, winAmountIfRealized, feemoney, "3", true);
            updateBetItem(betAmount, betItemReportMap, winAmountIfRealized, feemoney, "4", true);
        }
        else if(betItemType == BTCKlineBetItemType.Odd)
        {
            winAmountIfRealized = calcWinAmount("1", betAmount, betItem);

            updateBetItem(betAmount, betItemReportMap, winAmountIfRealized, feemoney, "1", true);
            updateBetItem(betAmount, betItemReportMap, winAmountIfRealized, feemoney, "3", true);
            updateBetItem(betAmount, betItemReportMap, winAmountIfRealized, feemoney, "5", true);
            updateBetItem(betAmount, betItemReportMap, winAmountIfRealized, feemoney, "7", true);
            updateBetItem(betAmount, betItemReportMap, winAmountIfRealized, feemoney, "9", true);
        }
        else if(betItemType == BTCKlineBetItemType.Even)
        {
            winAmountIfRealized = calcWinAmount("2", betAmount, betItem);

            updateBetItem(betAmount, betItemReportMap, winAmountIfRealized, feemoney, "0", true);
            updateBetItem(betAmount, betItemReportMap, winAmountIfRealized, feemoney, "2", true);
            updateBetItem(betAmount, betItemReportMap, winAmountIfRealized, feemoney, "4", true);
            updateBetItem(betAmount, betItemReportMap, winAmountIfRealized, feemoney, "6", true);
            updateBetItem(betAmount, betItemReportMap, winAmountIfRealized, feemoney, "8", true);
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
    }

    private List<RealtimeBetItemReport> createRealBetItemReport() {
        List<RealtimeBetItemReport> rsList = Lists.newArrayList();

        for(int i = 0; i < 10; i ++)
        {
            rsList.add(new RealtimeBetItemReport(i + StringUtils.getEmpty()));
        }
        for(BTCKlineBetItemType tmp : BTCKlineBetItemType.mArr)
        {
            String key = tmp.getKey().toLowerCase();
            rsList.add(new RealtimeBetItemReport(key));
        }

        return rsList;
    }

    @Override
    public BigDecimal calcWinAmount(NewLotteryOrderInfo orderInfo, String openResult) {

        int winCount = 0;
        BigDecimal winAmount = BigDecimal.ZERO;
        BigDecimal winAmount2 = BigDecimal.ZERO;
        long betCount = orderInfo.getTotalBetCount();
        if(betCount == 1)
        {
            winAmount = BTCKlineHelper.calcWinMoney(orderInfo.getTotalBetAmount(), openResult, orderInfo.getBetItem(), true);
            if(winAmount != null || winAmount.compareTo(BigDecimal.ZERO) > 0)
            {
                winAmount2 = BTCKlineHelper.calcWinMoney(orderInfo.getTotalBetAmount(), openResult, orderInfo.getBetItem(), false);
                winCount = 1;
            }
        }
        else
        {

            String[] betItemArr = StringUtils.split(orderInfo.getBetItem(), ',');
            for(String tmp : betItemArr)
            {
                BigDecimal tmpAmount = BTCKlineHelper.calcWinMoney(orderInfo.getSingleBetAmount(), openResult, tmp, true);
                if(tmpAmount == null || tmpAmount.compareTo(BigDecimal.ZERO) <= 0)
                {
                    continue;
                }


                winCount ++;
                winAmount = winAmount.add(tmpAmount);

                BigDecimal tmpAmount2 = BTCKlineHelper.calcWinMoney(orderInfo.getSingleBetAmount(), openResult, tmp, false);
                winAmount2 = winAmount2.add(tmpAmount2);
            }
        }

        orderInfo.setTmpCalcWinCount(winCount);
        orderInfo.setTmpWinAmount2(winAmount2);
        return winAmount;
    }

    public String createIssue(GameChildType gameChildType, DateTime dateTime, boolean generateKeyIssue)
    {
        // yyyyMMddHHmm
        String timeStr = dateTime.toString(DATE_TYPE_YYYYMMDDHHMM);
        if(generateKeyIssue)
        {
            String timeString =  timeStr.substring(0, 8) + gameChildType.getCode() + timeStr.substring(8, 12);
            return timeString;
        }
        else
        {
            String timeString =  timeStr.substring(0, 8) + timeStr.substring(8, 12);
            return timeString;
        }
    }

    @Override
    public String getReference(NewLotteryPeriodInfo periodInfo, GameChildType gameChildType) {
        BTCKlineType gameType = (BTCKlineType) gameChildType;
        String openResult = null;
        for(int i = 0; i < 5; i ++)
        {
            openResult = BTCResultHelper.getInstance().getKlineClosePrice(periodInfo.getIssue(), periodInfo.getStarttime().getTime(), gameType.getBinanceInternal());
            if(!StringUtils.isEmpty(openResult))
            {
                break;
            }
            if(i > 0)
            {
                LOG.warn("retry load kline data for issue = " + periodInfo.getIssue() + ", internal = " + gameType.getBinanceInternal());
            }
            ThreadUtils.sleep(500);
        }

        if(!StringUtils.isEmpty(periodInfo.getReferenceExternal()) && !StringUtils.isEmpty(openResult))
        {
            if(gameChildType == BTCKlineType.BTC_KLINE_1MIN )
            {
                String tmpOpenResult = openResult;
                openResult = updateOpenResultByManual(openResult, periodInfo.getReferenceExternal());
                periodInfo.setTmpRealReference(tmpOpenResult);
            }
            else if(gameChildType == BTCKlineType.BTC_KLINE_5MIN)
            {
                String tmpOpenResult = openResult;
                openResult = updateOpenResultByManual(openResult, periodInfo.getReferenceExternal());
                periodInfo.setTmpRealReference(tmpOpenResult);
            }
        }


        return openResult;
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
//        String openModeString = mConfigService.getValueByKey(false, BTCKlineConfig.GAME_TURNTABLE_OPEN_MODE);
        return GameOpenMode.SMART;
    }

    private void updateBetItem(BigDecimal betAmount, Map<String, RealtimeBetItemReport> betItemReportMap, BigDecimal winAmount, BigDecimal feemoney, String betItem, boolean onlyUpdateWinAmount)
    {
        RealtimeBetItemReport rgRealtimeBetItem = (RealtimeBetItemReport)betItemReportMap.get(betItem);
        rgRealtimeBetItem.incre(betAmount, winAmount, feemoney, onlyUpdateWinAmount);
    }

    private void test1()
    {
        String username = "u1";
        BigDecimal betAmount = new BigDecimal(10);
        BigDecimal feemoney = new BigDecimal(1);
        BTCKlineBetItemType betItemType = BTCKlineBetItemType.Even;

        List<RealtimeBetItemReport> rsList = initRealBetItemReport(false);
        Map<String, RealtimeBetItemReport> maps = Maps.newHashMap();
        for(RealtimeBetItemReport report : rsList)
        {
            maps.put(report.getOpenResult(), report);
        }

        GameChildType gameChildType = BTCKlineType.BTC_KLINE_1MIN;
        handleStatsBetItem(username, gameChildType, maps, "Even", betAmount, feemoney);
        handleStatsBetItem(username, gameChildType, maps, "Even", betAmount, feemoney);
    }

    private String updateOpenResultByManual(String openResult, String reference)
    {
        BTCKlineBetItemType betItemType = BTCKlineBetItemType.getType(reference);
        int len = openResult.length();
        int rsValue = StringUtils.asInt(openResult.substring(len - 1, len));
        if(rsValue % 2 == 0)
        {
            if(betItemType == BTCKlineBetItemType.Even)
            {
                // 都是双
                return openResult;
            }

            // 0-2-4-6-8
            rsValue += 1;
            openResult = openResult.substring(0, len - 1) + rsValue;
        }
        else
        {
            if(betItemType == BTCKlineBetItemType.Odd)
            {
                // 都是单
                return openResult;
            }

            // 1-3-5-7-9
            if(rsValue >= 9)
            {
                rsValue = rsValue - 1;
            }
            else
            {
                rsValue += 1;
            }
            openResult = openResult.substring(0, len - 1) + rsValue;
        }

        if(betItemType==null){
            openResult = openResult.substring(0, len - 1) + reference;
        }

        return openResult;
    }

    public static void main(String[] args) {

        BTCKlineProcessorImpl mgr = new BTCKlineProcessorImpl();
//        mgr.test1();
//        String issue = mgr.createIssue(BTCKlineType.BTC_KLINE_1MIN, DateTime.now(), true);
//        System.out.println(issue);


        String openResult = "2323.29";
        String refence = "9";

        String rs = mgr.updateOpenResultByManual(openResult, refence);
        System.out.println(rs);

    }

}
