package com.inso.modules.game.rocket.engine.impl;

import com.inso.framework.utils.RandomUtils;
import com.inso.modules.game.rocket.engine.CrashSupport;
import com.inso.modules.game.rocket.logical.RocketPeriodStatus;

public class CountCrashImpl implements CrashSupport {

    private int generateResultCount = 0;

    public void initGenerateResultCount(RocketPeriodStatus periodStatus)
    {
        if(periodStatus.getmTotalBetCount() > 0)
        {
            this.generateResultCount = RandomUtils.nextInt(10) + 5;
        }
        else
        {
            this.generateResultCount = RandomUtils.nextInt(20) + 5;
        }
    }

    @Override
    public boolean support(RocketPeriodStatus status) {
        return false;
    }

    @Override
    public boolean verify(float openResult, RocketPeriodStatus periodStatus) {
        this.generateResultCount --;
//        boolean checkHasBet = periodStatus.getmTotalBetCount() > 0 && periodStatus.getmTotalBetCount() > periodStatus.getmCashoutCount().get();
//        if(checkHasBet && this.generateResultCount <= 0)
        if(this.generateResultCount <= 0)
        {
            return true;
        }
        return false;
    }
}
