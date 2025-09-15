package com.inso.modules.game.rocket.engine.impl;

import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.RandomUtils;
import com.inso.modules.game.rocket.engine.CrashSupport;
import com.inso.modules.game.rocket.logical.RocketPeriodStatus;
import com.inso.modules.game.rocket.model.RocketType;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public class MaxWinCrashImpl implements CrashSupport {

    private static Log LOG = LogFactory.getLog(MaxWinCrashImpl.class);

    private float muitiple;
    public MaxWinCrashImpl()
    {
    }

    public void setMuitiple(float muitiple) {
        this.muitiple = muitiple;
    }

    @Override
    public boolean support(RocketPeriodStatus status) {
        if(muitiple <= 0)
        {
            return false;
        }
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
        float cashoutAmount = periodStatus.getDecreCashoutAmount().floatValue();
        float totalBetAmount = periodStatus.getCurrentBetMoneyOfIssue();
        float maxCrashAmount = totalBetAmount * muitiple;
        float currentCrashAmount = (totalBetAmount - cashoutAmount) * openResult + periodStatus.getCashoutWinAmountOfIssue().floatValue();
        return currentCrashAmount >= maxCrashAmount;
    }

    public static void main(String[] args) {
        MaxWinCrashImpl maxWinCrash = new MaxWinCrashImpl();
        maxWinCrash.setMuitiple(5);

        RocketPeriodStatus periodStatus = RocketPeriodStatus.loadCache(false, RocketType.CRASH, "1");
        periodStatus.incre("u1", "0", new BigDecimal(1), BigDecimal.ZERO);
        periodStatus.incre("u1", "0", new BigDecimal(10), BigDecimal.ZERO);
        periodStatus.incre("u1", "0", new BigDecimal(1), BigDecimal.ZERO);

        periodStatus.decryByCashout("u1", new BigDecimal(10), new BigDecimal(2.0f), null);


        boolean rs2 = maxWinCrash.support(periodStatus);

        boolean rs = maxWinCrash.verify(1.0f, periodStatus);
        System.out.println("rs = " + rs);

    }



}
