package com.inso.modules.game.helper;

import com.inso.framework.conf.MyConfiguration;
import com.inso.framework.utils.RandomUtils;
import com.inso.modules.common.model.CryptoCurrency;

import java.math.BigDecimal;

public class BetAmountHelper {

    private static String DEFAULT_CURRENCY_INR = "INR";
    private static String DEFAULT_CURRENCY_COP = "COP";

    public static final long[] mBasicAmountArray = {1, 10, 100, 1000, 10000};

    private static int mLength = mBasicAmountArray.length;

    private static boolean isUSD = false;


    private static int mGameBetAmountBasicMuitiple = 1;
    private static int mGameMinBetAmount = 1;

    static {
        MyConfiguration conf = MyConfiguration.getInstance();
        String currency = conf.getString("system.support.currency");

        int multyCount = 1;
        if(DEFAULT_CURRENCY_INR.equalsIgnoreCase(currency))
        {
            multyCount = 1;
        }
        else if(DEFAULT_CURRENCY_COP.equalsIgnoreCase(currency))
        {
            multyCount = 100;
        }

        if(multyCount > 1)
        {
            for(int i = 0; i < mLength; i ++)
            {
                mBasicAmountArray[i] = mBasicAmountArray[i] * multyCount;
            }
        }


        isUSD = CryptoCurrency.USDT.getKey().equalsIgnoreCase(currency);
        mGameBetAmountBasicMuitiple = conf.getInt("game.basic.bet.amount.muitiple", 1);
        mGameMinBetAmount = conf.getInt("game.basic.bet.amount.min", 1);
    }

    public static boolean verifyBasicAmount(long basicAmount)
    {
        if(isUSD && basicAmount == 1)
        {
            return true;
        }

        for(int i = 0; i < mLength; i++)
        {
            long value = mBasicAmountArray[i];
            if(value == basicAmount)
            {
                return true;
            }
        }
        return false;
    }

    public static boolean checkMinBetAmount(long betAmount)
    {
        return betAmount > 0 && betAmount >= mGameMinBetAmount;
    }

    public static long randomBetAmount()
    {
        long rs = 0;
        long count = RandomUtils.nextInt(10);
        if(count < 1)
        {
            rs = 1;
        }

        else if(count == 1)
        {
            rs =  RandomUtils.nextInt(10) + 1;
        }

        else if(count == 3)
        {
            rs =  10;
        }

        else if(count <= 5)
        {
            rs =  RandomUtils.nextInt(50) + 1;
        }

        else if(count <= 7)
        {
            rs =  RandomUtils.nextInt(100) + 1;
        }

        else if(count <= 8)
        {
            rs =  RandomUtils.nextInt(200) + 1;
        }
        else
        {
            rs = RandomUtils.nextInt(300) + 1;
        }
        if(mGameBetAmountBasicMuitiple <= 1)
        {
            return rs;
        }
        return rs * mGameBetAmountBasicMuitiple;
    }

    public static void main(String[] args) {
        for(int i = 0; i < 25; i ++)
        {
            System.out.println(randomBetAmount());
        }
    }

}
