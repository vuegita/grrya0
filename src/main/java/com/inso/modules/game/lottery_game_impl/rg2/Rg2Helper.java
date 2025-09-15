package com.inso.modules.game.lottery_game_impl.rg2;

import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.helper.IdGenerator;
import com.inso.modules.game.helper.BetFeemoneyHelper;
import com.inso.modules.game.lottery_game_impl.rg2.model.LotteryRgBetItemType;

import java.math.BigDecimal;

public class Rg2Helper {

    private static final String PERIOD_FORMAT = "yyyyMMdd";  //yyMMddHHmm

    private static IdGenerator mIdGenerator = IdGenerator.newSingleWorder();

    private static final BigDecimal WIN_MONEY_MULTIPLE_1_5 = new BigDecimal(1.5);
    private static final BigDecimal WIN_MONEY_MULTIPLE_2 = new BigDecimal(2);
    private static final BigDecimal WIN_MONEY_MULTIPLE_4_5 = new BigDecimal(4.5);
    private static final BigDecimal WIN_MONEY_MULTIPLE_9 = new BigDecimal(9);


    public static BigDecimal calcWinMoney(BigDecimal betAmount, long openResult, String betItem)
    {
        LotteryRgBetItemType betItemType = LotteryRgBetItemType.getType(betItem);
        BigDecimal totalFeemoney = BetFeemoneyHelper.getTotalFeemoney(betAmount);

        BigDecimal rsAmount = betAmount.subtract(totalFeemoney);
        if(LotteryRgBetItemType.RED == betItemType)
        {
            // (2 4 6 8) * 2 | 0 * 1.5
           if(openResult == 2 || openResult == 4 || openResult == 6 || openResult == 8)
            {
                return rsAmount.multiply(WIN_MONEY_MULTIPLE_2);
            }
            if(openResult == 0)
            {
                return rsAmount.multiply(WIN_MONEY_MULTIPLE_1_5);
            }
        }
        else if(LotteryRgBetItemType.GREEN == betItemType)
        {
            // 1 3 7 9 | 5 * 1.5
            if(openResult == 1 || openResult == 3 || openResult == 7 || openResult == 9)
            {
                return rsAmount.multiply(WIN_MONEY_MULTIPLE_2);
            }
            if(openResult == 5)
            {
                return rsAmount.multiply(WIN_MONEY_MULTIPLE_1_5);
            }
        }
        else if(LotteryRgBetItemType.VIOLET == betItemType)
        {
            // (0 5 ) * 4.5
            if(openResult == 0 || openResult == 5)
            {
                return rsAmount.multiply(WIN_MONEY_MULTIPLE_4_5);
            }
        }
        else
        {
            // amount * 9
            long betNumber = StringUtils.asLong(betItem);
            if(betNumber == openResult)
            {
                return rsAmount.multiply(WIN_MONEY_MULTIPLE_9);
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
    public static boolean isWin(long openResult, String betItem)
    {
        LotteryRgBetItemType betItemType = LotteryRgBetItemType.getType(betItem);
        if(LotteryRgBetItemType.RED == betItemType)
        {
            // (2 4 6 8) * 2 | 0 * 1.5
            if(openResult == 2 || openResult == 4 || openResult == 6 || openResult == 8)
            {
                return true;
            }
            if(openResult == 0)
            {
                return true;
            }
        }
        else if(LotteryRgBetItemType.GREEN == betItemType)
        {
            // 1 3 7 9 | 5 * 1.5
            if(openResult == 1 || openResult == 3 || openResult == 7 || openResult == 9)
            {
                return true;
            }
            if(openResult == 5)
            {
                return true;
            }
        }
        else if(LotteryRgBetItemType.VIOLET == betItemType)
        {
            // (0 5 ) * 4.5
            if(openResult == 0 || openResult == 5)
            {
                return true;
            }
        }
        else
        {
            // amount * 9
            long betNumber = StringUtils.asLong(betItem);
            if(betNumber == openResult)
            {
                return true;
            }
        }
        return false;
    }

    public static long getOpenResult(long openResult)
    {
        if(openResult >= 0 && openResult <= 9)
        {
            return openResult;
        }
        return -1;
    }

    public static void main(String[] args) {
        BigDecimal rsAmount = BigDecimal.valueOf(100);

        BigDecimal winAmount = calcWinMoney( rsAmount, 2, "red");

        System.out.println(winAmount);
    }

}
