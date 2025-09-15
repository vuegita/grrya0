package com.inso.modules.game.fruit.helper;

import java.math.BigDecimal;

import org.joda.time.DateTime;

import com.inso.framework.utils.DateUtils;
import com.inso.modules.common.helper.IdGenerator;
import com.inso.modules.game.fruit.model.FruitBetItemType;
import com.inso.modules.game.fruit.model.FruitType;
import com.inso.modules.game.helper.BetFeemoneyHelper;

public class FruitHelper {

    private static final String PERIOD_FORMAT = "yyyyMMdd";//yyMMddHHmm

    private static IdGenerator mIdGenerator = IdGenerator.newSingleWorder();

    /*** 中奖赔率 奖励倍数 BARx50丶小BARx25丶双星x20丶双七x20丶西瓜x20丶柠檬x10丶橘子x10丶铃铛x10丶苹果x5(除小BAR外其他小奖品均为x2)***/
    private static final BigDecimal DW_WIN_RATE = new BigDecimal(50); // 1:50
    private static final BigDecimal XDW_WIN_RATE = new BigDecimal(25); // 1:25
    private static final BigDecimal SX_WIN_RATE = new BigDecimal(20); // 1:20
    private static final BigDecimal QQ_WIN_RATE = new BigDecimal(20); // 1:20
    private static final BigDecimal XG_WIN_RATE = new BigDecimal(20); // 1:20
    private static final BigDecimal NM_WIN_RATE = new BigDecimal(10); // 1:10
    private static final BigDecimal JZ_WIN_RATE = new BigDecimal(10); // 1:10
    private static final BigDecimal HZ_WIN_RATE = new BigDecimal(10); // 1:10
    private static final BigDecimal PG_WIN_RATE = new BigDecimal(5); // 1:5

    private static final BigDecimal XSX_WIN_RATE = new BigDecimal(2); // 1:2
    private static final BigDecimal XXG_WIN_RATE = new BigDecimal(2); // 1:2
    private static final BigDecimal XQQ_WIN_RATE = new BigDecimal(2); // 1:2
    private static final BigDecimal XHZ_WIN_RATE = new BigDecimal(2); //1:2
    private static final BigDecimal XPG_WIN_RATE = new BigDecimal(2); // 1:2
    private static final BigDecimal XJZ_WIN_RATE = new BigDecimal(2); // 1:2
    private static final BigDecimal XNM_WIN_RATE = new BigDecimal(2); // 1:2
    private static final BigDecimal TAKEALL_WIN_RATE = new BigDecimal(0); // 1:2


    /**
     * 生成订单号
     * @param FruitType
     * @return
     */
    public static String nextOrderId(FruitType FruitType)
    {
        return mIdGenerator.nextId(FruitType.getCode());
    }

    /**
     * 生成期号
     * @param type
     * @param time
     * @return
     */
    public static String generateIssue(FruitType type, DateTime time)
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

    public static BigDecimal calcWinMoney(BigDecimal basicAmount, long betCountValue, FruitBetItemType openResult, FruitBetItemType betItem)
    {
        BigDecimal singleFeemoney = getSingleFeemoney(basicAmount);

        BigDecimal rsAmount = basicAmount.subtract(singleFeemoney);
        BigDecimal betCount = new BigDecimal(betCountValue);


        if(openResult == FruitBetItemType.DW)
        {
            if(betItem == openResult)
            {
                return rsAmount.multiply(DW_WIN_RATE).multiply(betCount);
            }
        }
        else if(openResult == FruitBetItemType.XDW)
        {
            if(betItem == FruitBetItemType.DW)
            {
                return rsAmount.multiply(XDW_WIN_RATE).multiply(betCount);
            }
        }
        else if(openResult == FruitBetItemType.SX)
        {
            if(betItem == openResult)
            {
                return rsAmount.multiply(SX_WIN_RATE).multiply(betCount);
            }
        }
        else if(openResult == FruitBetItemType.QQ)
        {
            if(betItem == openResult)
            {
                return rsAmount.multiply(QQ_WIN_RATE).multiply(betCount);
            }
        }
        else if(openResult == FruitBetItemType.XG)
        {
            if(betItem == openResult)
            {
                return rsAmount.multiply(XG_WIN_RATE).multiply(betCount);
            }
        }
        else if(openResult == FruitBetItemType.NM)
        {
            if(betItem == openResult)
            {
                return rsAmount.multiply(NM_WIN_RATE).multiply(betCount);
            }
        }
        else if(openResult == FruitBetItemType.JZ)
        {
            if(betItem == openResult)
            {
                return rsAmount.multiply(JZ_WIN_RATE).multiply(betCount);
            }
        }
        else if(openResult == FruitBetItemType.HZ)
        {
            if(betItem == openResult)
            {
                return rsAmount.multiply(HZ_WIN_RATE).multiply(betCount);
            }
        }
        else if(openResult == FruitBetItemType.PG)
        {
            if(betItem == openResult)
            {
                return rsAmount.multiply(PG_WIN_RATE).multiply(betCount);
            }
        }
        else if(openResult == FruitBetItemType.XSX)
        {
            if(betItem == FruitBetItemType.SX)
            {
                return rsAmount.multiply(XSX_WIN_RATE).multiply(betCount);
            }
        }
        else if(openResult == FruitBetItemType.XXG)
        {
            if(betItem == FruitBetItemType.XG)
            {
                return rsAmount.multiply(XXG_WIN_RATE).multiply(betCount);
            }
        }
        else if(openResult == FruitBetItemType.XQQ)
        {
            if(betItem == FruitBetItemType.QQ)
            {
                return rsAmount.multiply(XQQ_WIN_RATE).multiply(betCount);
            }
        }
        else if(openResult == FruitBetItemType.XHZ)
        {
            if(betItem == FruitBetItemType.HZ)
            {
                return rsAmount.multiply(XHZ_WIN_RATE).multiply(betCount);
            }
        }
        else if(openResult == FruitBetItemType.XPG)
        {
            if(betItem == FruitBetItemType.PG)
            {
                return rsAmount.multiply(XPG_WIN_RATE).multiply(betCount);
            }
        }
        else if(openResult == FruitBetItemType.XJZ)
        {
            if(betItem == FruitBetItemType.JZ)
            {
                return rsAmount.multiply(XJZ_WIN_RATE).multiply(betCount);
            }
        }
        else if(openResult == FruitBetItemType.XNM)
        {
            if(betItem == FruitBetItemType.NM)
            {
                return rsAmount.multiply(XNM_WIN_RATE).multiply(betCount);
            }
        }
        else if(openResult == FruitBetItemType.TAKEALL)
        {
            if(betItem == openResult)
            {
                return rsAmount.multiply(TAKEALL_WIN_RATE).multiply(betCount);
            }
        }


        return BigDecimal.ZERO;
    }

}
