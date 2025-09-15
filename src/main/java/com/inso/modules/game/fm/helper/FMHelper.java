package com.inso.modules.game.fm.helper;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.RandomUtils;

public class FMHelper {


    public static BigDecimal calcReturnRealRate(BigDecimal minRate, BigDecimal maxRate)
    {
        int minValue = minRate.multiply(BigDecimalUtils.DEF_100).intValue();
        int maxValue = maxRate.multiply(BigDecimalUtils.DEF_100).intValue();

        int range = maxValue - minValue;
        int value = RandomUtils.nextInt(range);
        BigDecimal bigValue = new BigDecimal(value).divide(BigDecimalUtils.DEF_100, 2, RoundingMode.DOWN);
        return bigValue.add(minRate);
    }

    public static void main(String[] args) {
        BigDecimal minRate = new BigDecimal("0.01");
        BigDecimal maxRate = new BigDecimal("0.01");

        System.out.println(calcReturnRealRate(minRate, maxRate));
    }

}
