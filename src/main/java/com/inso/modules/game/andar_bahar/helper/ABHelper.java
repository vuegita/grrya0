package com.inso.modules.game.andar_bahar.helper;

import java.math.BigDecimal;

import org.joda.time.DateTime;

import com.inso.framework.utils.DateUtils;
import com.inso.modules.common.helper.IdGenerator;
import com.inso.modules.game.andar_bahar.model.ABBetItemType;
import com.inso.modules.game.andar_bahar.model.ABType;
import com.inso.modules.game.helper.BetFeemoneyHelper;

public class ABHelper {

    private static final String PERIOD_FORMAT = "yyyyMMdd";//yyMMddHHmm

    private static IdGenerator mIdGenerator = IdGenerator.newSingleWorder();

    /*** 中奖赔率 ***/
    private static final BigDecimal ANDAR_WIN_RATE = new BigDecimal(1.9); // 1:1.9
    private static final BigDecimal BAHAR_WIN_RATE = new BigDecimal(2); // 1:2
    private static final BigDecimal TIE_WIN_RATE = new BigDecimal(15); // 1:14


    /**
     * 生成订单号
     * @param abType
     * @return
     */
    public static String nextOrderId(ABType abType)
    {
        return mIdGenerator.nextId(abType.getCode());
    }

    /**
     * 生成期号
     * @param type
     * @param time
     * @return
     */
    public static String generateIssue(ABType type, DateTime time)
    {
        int num=time.getMinuteOfDay()/type.getStepOfMinutes()+1;
        String periods=""+num;
        if(num<10){
            periods="00"+num;
        }else if(10<=num &&num<100){
            periods="0"+num;
        }
        String timeString = type.getCode() + DateUtils.convertString(PERIOD_FORMAT, time)+periods;
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

    public static BigDecimal calcWinMoney(BigDecimal basicAmount, long betCountValue, ABBetItemType openResult, ABBetItemType betItem)
    {
        BigDecimal singleFeemoney = getSingleFeemoney(basicAmount);

        BigDecimal rsAmount = basicAmount.subtract(singleFeemoney);
        BigDecimal betCount = new BigDecimal(betCountValue);
        if(openResult == ABBetItemType.ANDAR)
        {
            if(betItem == openResult)
            {
                return rsAmount.multiply(ANDAR_WIN_RATE).multiply(betCount);
            }
        }
        else if(openResult == ABBetItemType.BAHAR)
        {
            if(betItem == openResult)
            {
                return rsAmount.multiply(BAHAR_WIN_RATE).multiply(betCount);
            }
        }
        else if(openResult == ABBetItemType.TIE)
        {
            if(betItem == openResult)
            {
                return rsAmount.multiply(TIE_WIN_RATE).multiply(betCount);
            }
        }
        return BigDecimal.ZERO;
    }

}
