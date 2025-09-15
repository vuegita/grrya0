package com.inso.modules.game.helper;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.modules.admin.config.PlatformConfig;
import com.inso.modules.game.lottery_game_impl.btc_kline.helper.BTCKlineHelper;
import com.inso.modules.web.service.ConfigService;


public class BetFeemoneyHelper {

    private static final BigDecimal FEEMONEY_1 = new BigDecimal(1); // 10块

    private static BigDecimal DEFAULT_FEEMONEY_RATE = new BigDecimal(0.03);
    private static BigDecimal DEF_WIN_AMOUNT_2_RATE = BigDecimalUtils.DEF_2.subtract(DEFAULT_FEEMONEY_RATE);
    private static BigDecimal DEF_WIN_AMOUNT_10_RATE = BigDecimalUtils.DEF_10.subtract(DEFAULT_FEEMONEY_RATE);// new BigDecimal(9.7);
    private static BigDecimal DEF_WIN_AMOUNT_3_RATE = BigDecimalUtils.DEF_3.subtract(DEFAULT_FEEMONEY_RATE);

    static
    {
        refresh();
    }

    private static long mLastRefreshTime = -1;

    private static void refresh()
    {
        try {
            long currentTime = System.currentTimeMillis();
            if(mLastRefreshTime != -1 && currentTime - mLastRefreshTime <= 60_000)
            {
                return;
            }

            synchronized (BetFeemoneyHelper.class)
            {
                if(mLastRefreshTime != -1 && currentTime - mLastRefreshTime <= 60_000)
                {
                    return;
                }

                ConfigService configService = SpringContextUtils.getBean(ConfigService.class);
                BigDecimal value = configService.getBigDecimal(false, PlatformConfig.ADMIN_PLATFORM_CONFIG_GAME_BET_RATE);
                if(value == null || value.compareTo(BigDecimal.ZERO) <= 0)
                {
                    return;
                }
                DEFAULT_FEEMONEY_RATE = value;

                // 2
                DEF_WIN_AMOUNT_2_RATE = BigDecimalUtils.DEF_2.subtract(DEFAULT_FEEMONEY_RATE).setScale(2, RoundingMode.HALF_DOWN);

                // 10
                BigDecimal numberFee = BigDecimalUtils.DEF_10.multiply(DEFAULT_FEEMONEY_RATE);
                DEF_WIN_AMOUNT_10_RATE = BigDecimalUtils.DEF_10.subtract(numberFee).setScale(2, RoundingMode.HALF_DOWN);



                DEF_WIN_AMOUNT_3_RATE = BigDecimalUtils.DEF_3.subtract(DEFAULT_FEEMONEY_RATE).setScale(2, RoundingMode.HALF_DOWN);

                mLastRefreshTime = currentTime;
            }
        } catch (Exception e) {
            // 单元测试没有数据
        }
    }


    public static BigDecimal getWinAmountMultiple_2(boolean deductFee)
    {
        if(!deductFee)
        {
            return BigDecimalUtils.DEF_2;
        }
        refresh();
        return DEF_WIN_AMOUNT_2_RATE;
    }

    public static BigDecimal getWinAmountMultiple_3(boolean deductFee)
    {
        if(!deductFee)
        {
            return BigDecimalUtils.DEF_3;
        }
        refresh();
        return DEF_WIN_AMOUNT_3_RATE;
    }



    public static BigDecimal getWinAmountMultiple_10(boolean deductFee)
    {
        if(!deductFee)
        {
            return BigDecimalUtils.DEF_10;
        }
        refresh();
        return DEF_WIN_AMOUNT_10_RATE;
    }

    public static BigDecimal getSingleFeemoney(BigDecimal basicAmount)
    {
        refresh();
        return basicAmount.multiply(DEFAULT_FEEMONEY_RATE);
    }

    public static BigDecimal getFeeRate()
    {
        refresh();
        return DEFAULT_FEEMONEY_RATE;
    }

    public static boolean checkBasicAmount(long basicAmountValue)
    {
        if(basicAmountValue == 1 || basicAmountValue == 10 || basicAmountValue == 100 || basicAmountValue == 1000 || basicAmountValue == 10000)
        {
            return true;
        }
        return false;
    }

    public static BigDecimal getTotalFeemoney(BigDecimal basicAmount, long betCount)
    {
        BigDecimal singleFeemoney = getSingleFeemoney(basicAmount);
        return singleFeemoney.multiply(new BigDecimal(betCount));
    }

    public static BigDecimal getTotalFeemoney(BigDecimal betAmount)
    {
        BigDecimal singleFeemoney = getSingleFeemoney(betAmount);
        return singleFeemoney;
    }

    public static void main(String[] args) {
        System.out.println(DEFAULT_FEEMONEY_RATE);
    }

}
