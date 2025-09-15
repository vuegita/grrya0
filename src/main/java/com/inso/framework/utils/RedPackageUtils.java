package com.inso.framework.utils;

import java.math.BigDecimal;

public class RedPackageUtils {

    public static final BigDecimal DEFAULT_MIN_AMOUNT = new BigDecimal(0.01);

    private static final BigDecimal mBasicAmount = new BigDecimal(100);
    private static final BigDecimal mDoubleAmount = new BigDecimal(100);


    public static int getMaxCount(BigDecimal totalAmount)
    {
        return totalAmount.divide(DEFAULT_MIN_AMOUNT, 0).intValue();
    }

    public static BigDecimal getRandomAmount(BigDecimal totalAmount, long splitCount, BigDecimal maxAmount)
    {
        if(splitCount <= 1)
        {
            return totalAmount;
        }
        BigDecimal tmpMinAmount = DEFAULT_MIN_AMOUNT.multiply(mBasicAmount);
        BigDecimal tmpMaxAmount = maxAmount.multiply(mBasicAmount);

        BigDecimal splitCountValue = new BigDecimal(splitCount);
        BigDecimal tmpAvgAmount = totalAmount.divide(splitCountValue, 2).multiply(mDoubleAmount);

        // 平均金额不能大于限制最大金额
        if(tmpAvgAmount.compareTo(tmpMaxAmount) > 0)
        {
            tmpMaxAmount = tmpAvgAmount;
        }

        int tmpMinAmountValue = tmpMinAmount.intValue();
        int amountValue = RandomUtils.nextInt(tmpMaxAmount.intValue());
        // 如果小于最小金额
        if(amountValue < tmpMinAmountValue)
        {
            amountValue = tmpMinAmountValue;
        }
        return new BigDecimal(amountValue).divide(mBasicAmount);
    }

    /**
     * 验证金额是否有效
     * @param totalAmount
     * @param splitCount
     * @return
     */
    public static boolean verify(BigDecimal totalAmount, long splitCount)
    {
        BigDecimal tmpAmount = DEFAULT_MIN_AMOUNT.multiply(new BigDecimal(splitCount));
        return totalAmount.compareTo(tmpAmount) > 0;
    }

    public static void main(String[] args) {

        int splitCount = 3;
        BigDecimal totalAmount = new BigDecimal(10);
        BigDecimal minAmount = new BigDecimal(1);
        BigDecimal maxAmount = new BigDecimal(2);

//        BigDecimal tmpAmount = getRandomAmount(totalAmount, splitCount --, minAmount, maxAmount);
//        totalAmount = totalAmount.subtract(tmpAmount);
//        System.out.println(" index = " + splitCount + ", amount = " + tmpAmount);
//
//        tmpAmount = getRandomAmount(totalAmount, splitCount --, minAmount, maxAmount);
//        totalAmount = totalAmount.subtract(tmpAmount);
//        System.out.println(" index = " + splitCount + ", amount = " + tmpAmount);
//
//        tmpAmount = getRandomAmount(totalAmount, splitCount --, minAmount, maxAmount);
//        totalAmount = totalAmount.subtract(tmpAmount);
//        System.out.println(" index = " + splitCount + ", amount = " + tmpAmount);

    }
}
