package com.inso.modules.game.lottery_game_impl.football;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.lottery_game_impl.BaseLotterySupport;
import com.inso.modules.game.lottery_game_impl.NewLotteryPeriodStatus;
import com.inso.modules.game.lottery_game_impl.btc_kline.model.BTCKlineBetItemType;
import com.inso.modules.game.lottery_game_impl.btc_kline.model.BTCKlineType;
import com.inso.modules.game.lottery_game_impl.football.helper.FootballHelper;
import com.inso.modules.game.lottery_game_impl.football.model.FootballType;
import com.inso.modules.game.model.GameOpenMode;
import com.inso.modules.game.model.NewLotteryOrderInfo;
import com.inso.modules.game.model.NewLotteryPeriodInfo;
import com.inso.modules.game.model.RealtimeBetItemReport;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 开奖管理器
 */
@Component
public class FootballProcessorImpl extends BaseLotterySupport {

    public static final String DATE_TYPE_YYYYMMDDHHMM = "yyyyMMddHHmm";


    private List<RealtimeBetItemReport> mZeroBetItemReport;

    @Override
    public GameChildType[] getAllGameTypes() {
        return FootballType.mArr;
    }


    @Override
    public boolean isWin(String openResultValue, String betItem) {
        return false;

    }

    @Override
    public BigDecimal calcWinAmount(String openResult, BigDecimal betAmount, String betItem) {
        int betIndex = StringUtils.asInt(openResult);
        return FootballHelper.calcWinAmount(betAmount, betIndex);
    }

    @Override
    public void onGameStart(NewLotteryPeriodInfo periodInfo, NewLotteryPeriodStatus periodStatus) {
    }

    @Override
    public List<RealtimeBetItemReport> initRealBetItemReport(boolean fetchZero) {
        return Collections.emptyList();
    }

    public void onBeginGameByCustom_V2(DateTime fireTime)
    {
        DateTime fromTime = fireTime.minusHours(3);
        DateTime endTime = fireTime.minusHours(2);

        FootballType gameType = FootballType.Football;
        mAOrderService.queryAllPendingByTime(gameType, fromTime, endTime, new Callback<NewLotteryOrderInfo>() {
            @Override
            public void execute(NewLotteryOrderInfo o)
            {
                try {
                    OrderTxStatus txStatus = OrderTxStatus.getType(o.getStatus());
                    if(txStatus == OrderTxStatus.FAILED || txStatus == OrderTxStatus.REALIZED)
                    {
                        return;
                    }

                    JSONObject jsonObject = FootballHandleManager.getOrderStatusByOrderNo(o.getNo());
                    if(jsonObject == null)
                    {
                        return;
                    }
                    OrderTxStatus cacheTxStatus = OrderTxStatus.getType(jsonObject.getString(FootballHandleManager.KEY_TX_STATUS));
                    String betItem = jsonObject.getString(FootballHandleManager.KEY_BET_INDEX);
                    if(cacheTxStatus == OrderTxStatus.WAITING || cacheTxStatus == OrderTxStatus.REALIZED)
                    {
                        handleOrderToSettle(gameType, o, OrderTxStatus.REALIZED, betItem, true);
                    }
                    else
                    {
                        handleOrderToSettle(gameType, o, OrderTxStatus.FAILED, betItem, true);
                    }
                } catch (Exception e) {
                    LOG.error("onBeginGameByCustom_V2 error:", e);
                }

            }
        });
    }

    @Override
    public void handleStatsBetItem(String username, GameChildType gameChildType, Map<String, RealtimeBetItemReport> betItemReportMap, String betItem, BigDecimal betAmount, BigDecimal feemoney) {

    }


    @Override
    public BigDecimal calcWinAmount(NewLotteryOrderInfo orderInfo, String openResult) {
        int winCount = 0;
        BigDecimal winAmount = BigDecimal.ZERO;
        int betIndex = StringUtils.asInt(orderInfo.getBetItem());
        if(betIndex >= 1)
        {
            winAmount = FootballHelper.calcWinAmount(orderInfo.getTotalBetAmount(), betIndex);
        }
        orderInfo.setTmpCalcWinCount(winCount);
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
        return null;
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
        return openResult;
    }

    public static void main(String[] args) {

        FootballProcessorImpl mgr = new FootballProcessorImpl();
//        mgr.test1();
//        String issue = mgr.createIssue(BTCKlineType.BTC_KLINE_1MIN, DateTime.now(), true);
//        System.out.println(issue);


        String openResult = "2323.29";
        String refence = "9";

        String rs = mgr.updateOpenResultByManual(openResult, refence);
        System.out.println(rs);

    }

}
