package com.inso.modules.game.lottery_game_impl.mines.helper;

import com.inso.modules.game.helper.BetFeemoneyHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MinesHelper {

    // 原来
    public static BigDecimal calcWinAmount(BigDecimal betAmount, float betIndex, float mineNum)
    {
        return calcWinAmount_V2(betAmount, betIndex, mineNum);
    }

    private static BigDecimal calcWinAmount_V1(BigDecimal betAmount, float betIndex, float mineNum)
    {
        float feereate = BetFeemoneyHelper.getFeeRate().floatValue();
//        feereate = 0.06f;
//        (Math.pow(25/(25-mineNum), betIndex) * (1-(betIndex)*0.03));
        double rs1 = Math.pow(25 / (25 - mineNum),  betIndex);
        double rs2 = 1 -  feereate * Math.min(betIndex, 3);
        BigDecimal rsFee = new BigDecimal(rs1 * rs2).setScale(3, RoundingMode.HALF_UP);
//        System.out.println("Fee = " + rsFee);
        BigDecimal rs = betAmount.multiply(rsFee);
        return rs.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    // 现在
    private static BigDecimal calcWinAmount_V2(BigDecimal betAmount, float betIndex, float mineNum)
    {
        float feereate = BetFeemoneyHelper.getFeeRate().floatValue();
//        feereate = 0.06f;

        // 赔率计算
        double rs1 = Math.pow(25.0f / (25.0f - mineNum),  betIndex);
        BigDecimal rsFee = new BigDecimal(rs1 ).setScale(3, RoundingMode.HALF_UP);

        // (下注金额 - 手续费) * 赔率， 双向收手续费
        BigDecimal rs = betAmount.multiply(new BigDecimal(1.0f - feereate)).multiply(rsFee);

        // 单向收手续费
//        BigDecimal feeAmount = betAmount.multiply(new BigDecimal(feereate));
//        BigDecimal rs2 = betAmount.multiply(rsFee).subtract(feeAmount);
        return rs.setScale(2, BigDecimal.ROUND_HALF_UP);
    }


    /**
     * 计算概率
     * @param mineNum
     * @param clickCount
     * @return
     */
    public static double calcProbability(int mineNum, float clickCount)
    {
        double rs = 1;
        for(int i = 0; i < clickCount; i ++)
        {
            rs = rs * calcBasicProbability(mineNum, i);
        }
        return rs;
    }

    public static double calcBasicProbability(int mineNum, float clickCount)
    {
        return (25.0 - mineNum - clickCount) / (25.0 - clickCount);
    }

    public static void test2()
    {
        int mineNum = 2;
        int clickCount = 0;
        for (int i = 0; i < 10; i ++)
        {
            clickCount ++;
            double rs = calcProbability(mineNum, clickCount);
            System.out.println("betIndex = " + clickCount + ", rs = " + rs);
        }

    }

    private static void test1()
    {
        float betIndex = 0;
        float mineNum = 2;

        BigDecimal betAmount = new BigDecimal(1);
        for(int i = 0; i < 24; i ++)
        {
            betIndex ++;
            BigDecimal rs = calcWinAmount(betAmount, betIndex, mineNum);
            System.out.println("betIndex = " + betIndex + ", rs = " + rs);
        }
    }


    public static void main(String[] args) {


//        test1();
        test2();



    }

}
