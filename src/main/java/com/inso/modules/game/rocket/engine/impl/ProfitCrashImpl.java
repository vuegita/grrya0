package com.inso.modules.game.rocket.engine.impl;

import com.google.common.collect.Lists;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.RandomUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.rocket.engine.CrashSupport;
import com.inso.modules.game.rocket.logical.RocketPeriodStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ProfitCrashImpl implements CrashSupport {

    private static Log LOG = LogFactory.getLog(ProfitCrashImpl.class);

    private int MAX_PROFIT_RANDOM_SEED = 23;
    private int MAX_OPEN_RESULT_RANDOM_SEED = 25;

    private float mPlatformProfitRate;
    private float maxCrashValue = -1;

    public void setmPlatformProfitRate(float mPlatformProfitRate) {
        this.mPlatformProfitRate = mPlatformProfitRate;
    }

    private List<SpecialLimitItem> mSpecialItemList = Lists.newArrayList();


    private int mSepecialCapacity = 10;

    public ProfitCrashImpl()
    {
        for(int i = 0; i < mSepecialCapacity; i ++)
        {
            mSpecialItemList.add(new SpecialLimitItem());
        }
    }

    public void setMaxCrashValue(float maxCrashValue) {
        this.maxCrashValue = maxCrashValue;
    }

    @Override
    public boolean support(RocketPeriodStatus status) {
        if(status.getmTotalBetCount() <= 0)
        {
            return false;
        }

        if(status.getmCashoutCount().get() >= status.getmTotalBetCount())
        {
            return false;
        }
        return true;
    }

    @Override
    public boolean verify(float openResult, RocketPeriodStatus periodStatus) {
//        StringBuilder log1 = new StringBuilder();
//        log1.append("betCount = ").append(periodStatus.getmTotalBetCount());
//        log1.append(", cashoutCount = ").append(periodStatus.getmCashoutCount());
//        log1.append(", robotBetCount = ").append(periodStatus.getmRobotBetCount());
//        log1.append(", robotCashout = ").append(periodStatus.getmRobotCashoutCount());
//        log1.append(", maxCrashValue = ").append(maxCrashValue);
//        log1.append(", openResult = ").append(openResult);
//        LOG.info(log1.toString());

        boolean isCrash = false;
        if(maxCrashValue > 0 && openResult >= maxCrashValue)
        {
            int count = RandomUtils.nextInt(10);
            isCrash = count <= 6;
            LOG.info("isCrash =  " + isCrash + ", count = " + count);
        }

        if(isCrash)
        {
            return true;
        }

        double totalCashoutWinAmount = periodStatus.getCashoutWinAmountOfIssue().doubleValue() + periodStatus.getBetCashoutWinAmount().doubleValue() - periodStatus.getDecreCashoutWinAmount().doubleValue();
        double platfromPrifitAmount = 0;

        boolean checkProfit = false;
        double rsProfit = -1;
        if(totalCashoutWinAmount > 0)
        {
            platfromPrifitAmount = periodStatus.getCurrentBetMoneyOfIssue() - totalCashoutWinAmount;
            rsProfit = platfromPrifitAmount / periodStatus.getCurrentBetMoneyOfIssue();
            checkProfit = true;
        }

        LOG.info("rsProfit = " + rsProfit + ", totalCashoutWinAmount = " + totalCashoutWinAmount);
        if(checkProfit && rsProfit <= 0)
        {
            return true;
        }

        if(mPlatformProfitRate <= rsProfit)
        {
            return true;
        }

        if(checkProfit)
        {
            return verifyProfit(rsProfit, MAX_PROFIT_RANDOM_SEED);
        }
        else
        {
            return verifyOpenResult(openResult, MAX_OPEN_RESULT_RANDOM_SEED, 5);
        }
    }

    private boolean verifyProfit( double profitRate, int maxValue)
    {
        float stepProfit = 0.08f;
        float currentProfit = 0.9f;
        while (profitRate > currentProfit && currentProfit > stepProfit)
        {
            currentProfit -= stepProfit;
            maxValue = maxValue - 2;

            if(currentProfit <= 0.15)
            {
                maxValue = 2;
                break;
            }
        }

        int rsReslult = RandomUtils.nextInt(maxValue);
        return rsReslult < 1;
    }

    private boolean verifyOpenResult(double openResult, int maxValue, int minValue)
    {
        float stepResult = 0.1f;
        float currentResult = 1.1f;
        while (openResult > currentResult && currentResult > stepResult)
        {
            currentResult += stepResult;
            maxValue = maxValue - 2;

            if(maxValue <= minValue)
            {
                maxValue = minValue;
                break;
            }
        }
        int rsReslult = RandomUtils.nextInt(maxValue);
        return rsReslult < 1;
    }

    private class SpecialLimitItem implements Comparable<SpecialLimitItem>
    {
        private long amount;
        private int limitCount;

        public long getAmount() {
            return amount;
        }

        public void setAmount(long amount) {
            this.amount = amount;
        }

        public int getLimitCount() {
            return limitCount;
        }

        public void setLimitCount(int limitCount) {
            this.limitCount = limitCount;
        }

        public void clear()
        {
            this.amount = 0;
             this.limitCount = 0;
        }

        @Override
        public int compareTo(@NotNull SpecialLimitItem item) {
            if(getAmount() > item.getAmount())
            {
                return 1;
            }
            else if(getAmount() < item.getAmount())
            {
                return  -1;
            }
            return 0;
        }
    }

    public static void main(String[] args) {

        ProfitCrashImpl im = new ProfitCrashImpl();

        int count = 0;
        for(int i = 0; i < 100; i ++)
        {
            boolean rs = im.verifyOpenResult(2, 18, 5);
            if(rs)
            {
                count ++;
            }
        }

        System.out.println(count);


    }


}
