package com.inso.modules.coin.contract.helper;

import com.inso.framework.utils.BigDecimalUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CoinAmountHelper {

    /*** 最多保留余额几位小数 ***/
    private static int DEFAULT_KEEP_MAX_DECIMALS = 6;


    public static BigDecimal toDivideAmount(BigDecimal amount, int decimals)
    {
        if(decimals <= 0)
        {
            return amount;
        }
        BigDecimal baseMuitiple = BigDecimalUtils.DEF_10.pow(decimals);
        if(amount != null)
        {
            // 余额四舍五舍, 因为5入的话可能会导致转换失败
            amount = amount.divide(baseMuitiple, DEFAULT_KEEP_MAX_DECIMALS, RoundingMode.DOWN);
        }
        return amount;
    }

    public static BigDecimal toMultipleAmount(BigDecimal amount, int decimals)
    {
        if(decimals <= 0)
        {
            return null;
        }
        BigDecimal baseMuitiple = BigDecimalUtils.DEF_10.pow(decimals);
        if(amount != null)
        {
            // 余额四舍五舍, 因为5入的话可能会导致转换失败
            amount = amount.multiply(baseMuitiple);
        }
        return amount;
    }
}
