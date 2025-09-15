package com.inso.modules.game.lottery_game_impl.turntable.helper;

import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.ThreadUtils;
import com.inso.modules.game.helper.BetFeemoneyHelper;
import com.inso.modules.game.lottery_game_impl.turntable.model.TurnTableType;
import com.inso.modules.game.lottery_game_impl.turntable.model.TurntableBetItemType;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.Date;

public class TurntableHelper {

    private static final String PERIOD_FORMAT = "yyyyMMdd";  //yyMMddHHmm

    private static final BigDecimal WIN_MONEY_MULTIPLE_2 = new BigDecimal(2);
    private static final BigDecimal WIN_MONEY_MULTIPLE_7 = new BigDecimal(7);


    public static BigDecimal calcWinMoney(BigDecimal betAmount, String openResult, String betItem)
    {
        TurntableBetItemType openItemType = TurntableBetItemType.getType(openResult);
        TurntableBetItemType betItemType = TurntableBetItemType.getType(betItem);
        return calcWinMoney(betAmount, openItemType, betItemType);
    }

    public static BigDecimal calcWinMoney(BigDecimal betAmount, TurntableBetItemType openItemType, TurntableBetItemType betItemType)
    {
        BigDecimal singleFeemoney = BetFeemoneyHelper.getSingleFeemoney(betAmount);
        BigDecimal rsAmount = betAmount.subtract(singleFeemoney);
        if(TurntableBetItemType.RED_RUBY == betItemType || TurntableBetItemType.GREEN_EMERALD == betItemType)
        {
            // big
            if(openItemType == betItemType)
            {
                return rsAmount.multiply(WIN_MONEY_MULTIPLE_2);
            }
        }
        else if(TurntableBetItemType.CHEST == betItemType)
        {
            // big
            if(openItemType == betItemType)
            {
                return rsAmount.multiply(WIN_MONEY_MULTIPLE_7);
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * 判断是否中奖
     * @param openResult
     * @param betItem
     * @return
     */
    public static boolean isWin(String openResult, String betItem)
    {
        TurntableBetItemType openItemType = TurntableBetItemType.getType(openResult);
        TurntableBetItemType betItemType = TurntableBetItemType.getType(betItem);
        return openItemType == betItemType;
    }

//    public static long getOpenResult(long openResult)
//    {
//        if(openResult >= 0 && openResult <= TurntableBetItemType.BET_ITEM_MAX_NUMBER)
//        {
//            return openResult;
//        }
//        return -1;
//    }

    public static void main(String[] args) {

        String time = "2023-02-16 00:00:53";
        Date fireTime = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, time);

        DateTime dateTime = new DateTime(fireTime);
        TurnTableType type = TurnTableType.ROULETTE;
        for(int i = 0; i < 10000; i ++)
        {
            dateTime = dateTime.plusSeconds(type.getStepOfSeconds());

            ThreadUtils.sleep(100);
        }
//        System.out.println(issue);
    }

}

