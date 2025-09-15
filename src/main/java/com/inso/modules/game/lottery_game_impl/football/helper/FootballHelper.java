package com.inso.modules.game.lottery_game_impl.football.helper;

import com.inso.modules.game.helper.BetFeemoneyHelper;

import java.math.BigDecimal;

public class FootballHelper {

    private static BigDecimal[] multipleArr = {
            new BigDecimal(1.8f), // 0.55
            new BigDecimal(2.6f), // 0.38
            new BigDecimal(6.2f), // 0.16
            new BigDecimal(12.4f), // 0.08
            new BigDecimal(32.6f)  // 0.03
    };

    public static float[] mProfitArr = {0.46f, 0.21f, 0.10f, 0.04f, 0.02f};

    public static float getProfitValue(int index)
    {
        return mProfitArr[index];
    }


    public static BigDecimal calcWinAmount(BigDecimal betAmount, int betIndex)
    {
        betIndex --;
        if(betIndex < 0 || betIndex >= 5)
        {
            return BigDecimal.ZERO;
        }
        BigDecimal value = multipleArr[betIndex];
        BigDecimal feemoney = BetFeemoneyHelper.getSingleFeemoney(betAmount);
        BigDecimal rs = betAmount.subtract(feemoney);
        return rs.multiply(value).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

}
