package com.inso.modules.game.rocket.helper;

import com.inso.framework.context.MyEnvironment;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.RandomUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.helper.IdGenerator;
import com.inso.modules.game.helper.BetFeemoneyHelper;
import com.inso.modules.game.rocket.model.RocketType;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RocketHelper {

    private static final String PERIOD_FORMAT = "yyyyMMdd";  //yyMMddHHmm




    private static boolean isDEV = MyEnvironment.isDev();

    public static float mBetItem_MaxValue = 100;
    public static float[] mBetItemSequenceArr = {
            1, 1.2f, 1.4f, 1.6f, 1.8f,
            2, 2.2f, 2.4f, 2.6f, 2.8f,
            3, 3.5f, 4.0f, 4.5f, 5,

            6, 7, 8, 9, 10,

            12, 14, 16, 18, 20,

    };

    /**
     * 生成订单号
     * @return
     */
    public static String nextOrderId(String issue, long userid, boolean robot)
    {
        if(robot)
        {
            return issue +"0" + RandomUtils.nextInt(1000);
        }
        return issue + "1" + userid;
    }

    /**
     * 生成期号
     * @param lotteryType
     * @param dateTime
     * @return
     */
    public static String generateIssue(RocketType lotteryType, DateTime dateTime, int periodOfToday)
    {
        String periods = StringUtils.getEmpty();
        if(periodOfToday < 10 ) {
            periods = "000" + periodOfToday;
        }
        else if(  periodOfToday < 100)
        {
            periods = "00" + periodOfToday;
        }
        else if(  periodOfToday < 1000)
        {
            periods = "0" + periodOfToday;
        }
        else {
            periods = StringUtils.getEmpty() + periodOfToday;
        }
        if(isDEV)
        {
            String timeString = lotteryType.getCode() + dateTime.toString(DateUtils.TYPE_YYYYMMDDHHMMSS) + periods;
            return timeString;
        }
        String timeString = lotteryType.getCode() + dateTime.toString(DateUtils.TYPE_YYYYMMDD) + periods;
        return timeString;
    }

    public static BigDecimal getSingleFeemoney(BigDecimal basicAmount)
    {
        return BetFeemoneyHelper.getSingleFeemoney(basicAmount);
    }

    public static BigDecimal getTotalFeemoney(BigDecimal basicAmount, long betCount)
    {
        BigDecimal singleFeemoney = getSingleFeemoney(basicAmount);
        return singleFeemoney.multiply(new BigDecimal(betCount));
    }

    public static BigDecimal calcWinMoney(float betItemType, float openResult, BigDecimal betAmount)
    {
        if(openResult == 0 || betItemType == 0)
        {
            return BigDecimal.ZERO;
        }

        if(betItemType > openResult)
        {
            return BigDecimal.ZERO;
        }

        BigDecimal singleFeemoney = getSingleFeemoney(betAmount);
        BigDecimal rsAmount = betAmount.subtract(singleFeemoney);
        return rsAmount.multiply(new BigDecimal(betItemType)).setScale(4, RoundingMode.HALF_UP);
    }

    public static BigDecimal calcBetAmount(BigDecimal basicAmount, long betCountValue)
    {
        BigDecimal singleFeemoney = getSingleFeemoney(basicAmount);
        BigDecimal rsAmount = basicAmount.subtract(singleFeemoney);
        BigDecimal betCount = new BigDecimal(betCountValue);
        return rsAmount.multiply(betCount);
    }

    /**
     * 判断是否中奖
     * @param openResult
     * @param betItem
     * @return
     */
    public static boolean isWin(String openResult, String betItem)
    {
        float floatOpenResult = StringUtils.asFloat(openResult);
        float betItemResult = StringUtils.asFloat(betItem);
        return floatOpenResult > 0 && betItemResult > 0 && floatOpenResult >= betItemResult;
    }

    public static String getMaxBetItemValue(boolean addPrefix, float betItem)
    {
        if(betItem <= 0)
        {
            return "0";
        }
        int len = mBetItemSequenceArr.length;
        for(int i = 0; i < len; i ++)
        {
            float value = mBetItemSequenceArr[i];
            if(betItem < value)
            {
                if(addPrefix)
                {
                    return " >= " + value;
                }
                else
                {
                    return  value + StringUtils.getEmpty();
                }

            }
        }
        return mBetItem_MaxValue + StringUtils.getEmpty();
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

        String value = getMaxBetItemValue(false, 3.99f);
        System.out.println(value);

//        BigDecimal winAmount = calcWinMoney( rsAmount, 1, "0", "1");
//        System.out.println(winAmount);
    }

}

