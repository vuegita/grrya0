package com.inso.framework.utils;

import java.math.BigDecimal;

public class BigDecimalUtils {

    public static final BigDecimal DEF_1000 = new BigDecimal(1000);
    public static final BigDecimal DEF_500 = new BigDecimal(500);
    public static final BigDecimal DEF_400 = new BigDecimal(400);
    public static final BigDecimal DEF_350 = new BigDecimal(350);
    public static final BigDecimal DEF_300 = new BigDecimal(300);
    public static final BigDecimal DEF_250 = new BigDecimal(250);
    public static final BigDecimal DEF_205 = new BigDecimal(205);
    public static final BigDecimal DEF_200 = new BigDecimal(200);
    public static final BigDecimal DEF_180 = new BigDecimal(180);
    public static final BigDecimal DEF_160 = new BigDecimal(160);
    public static final BigDecimal DEF_150 = new BigDecimal(150);
    public static final BigDecimal DEF_120 = new BigDecimal(120);
    public static final BigDecimal DEF_100 = new BigDecimal(100);

    public static final BigDecimal DEF_50 = new BigDecimal(50);
    public static final BigDecimal DEF_35 = new BigDecimal(35);
    public static final BigDecimal DEF_30 = new BigDecimal(30);
    public static final BigDecimal DEF_20 = new BigDecimal(20);
    public static final BigDecimal DEF_10 = new BigDecimal(10);
    public static final BigDecimal DEF_9 = new BigDecimal(9);
    public static final BigDecimal DEF_8 = new BigDecimal(8);
    public static final BigDecimal DEF_7 = new BigDecimal(7);
    public static final BigDecimal DEF_6 = new BigDecimal(6);
    public static final BigDecimal DEF_5 = new BigDecimal(5);
    public static final BigDecimal DEF_4 = new BigDecimal(4);
    public static final BigDecimal DEF_3 = new BigDecimal(3);
    public static final BigDecimal DEF_2 = new BigDecimal(2);
    public static final BigDecimal DEF_1 = new BigDecimal(1);

    public static final BigDecimal DEF_DECIMAL_09 = new BigDecimal(0.9);
    public static final BigDecimal DEF_DECIMAL_08 = new BigDecimal(0.8);
    public static final BigDecimal DEF_DECIMAL_06 = new BigDecimal(0.6);
    public static final BigDecimal DEF_DECIMAL_05 = new BigDecimal(0.5);
    public static final BigDecimal DEF_DECIMAL_04 = new BigDecimal(0.4);
    public static final BigDecimal DEF_DECIMAL_03 = new BigDecimal(0.3);
    public static final BigDecimal DEF_DECIMAL_02 = new BigDecimal(0.2);
    public static final BigDecimal DEF_DECIMAL_01 = new BigDecimal(0.1);

    public static BigDecimal getNotNull(BigDecimal value)
    {
        if(value == null)
        {
            return BigDecimal.ZERO;
        }
        return value;
    }
}
