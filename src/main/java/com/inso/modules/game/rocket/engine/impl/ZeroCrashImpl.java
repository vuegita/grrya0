package com.inso.modules.game.rocket.engine.impl;

import com.inso.framework.utils.RandomUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.rocket.engine.CrashSupport;
import com.inso.modules.game.rocket.logical.RocketPeriodStatus;

public class ZeroCrashImpl implements CrashSupport {

    private static float BET_AMOUNT_1 = 100000;
    private static float BET_AMOUNT_2 = 80000;
    private static float BET_AMOUNT_3 = 50000;
    private static float BET_AMOUNT_4 = 10000;
    private static float BET_AMOUNT_5 = 5000;

    private float maxCrashValue;

    public float handleAndFetchMaxCrashValueByConfig(String strCrashArr) {
        float maxCrashValue = -1;
        try {
            if(StringUtils.isEmpty(strCrashArr))
            {
                return maxCrashValue;
            }

            String[] arr = StringUtils.split(strCrashArr, '|');
            if(arr == null || arr.length <= 0)
            {
                return maxCrashValue;
            }
            int count = arr.length;
            int index = RandomUtils.nextInt(count);
            String value = arr[index];
            if(StringUtils.isEmpty(value))
            {
                return maxCrashValue;
            }
            value = value.trim();
            if(StringUtils.isEmpty(value))
            {
                return maxCrashValue;
            }

            float crashValue = StringUtils.asFloat(value);
            if(crashValue == 0 || crashValue >= 1)
            {
                return crashValue;
            }
            return maxCrashValue;
        } finally {
            this.maxCrashValue = maxCrashValue;
        }
    }

    @Override
    public boolean support(RocketPeriodStatus status) {
        return true;
    }

    @Override
    public boolean verify(float openResult, RocketPeriodStatus status) {
        if(maxCrashValue == 0)
        {
            return RandomUtils.nextInt(10) <= 7;
        }
        float totalBetAmount = status.getCurrentBetMoneyOfIssue();

        int count = 10;
        if(totalBetAmount >= BET_AMOUNT_1)
        {
            return RandomUtils.nextInt(count) < 1;
        }

        count ++;
        if(totalBetAmount >= BET_AMOUNT_2)
        {
            return RandomUtils.nextInt(count) < 1;
        }

        count ++;
        if(totalBetAmount >= BET_AMOUNT_3)
        {
            return RandomUtils.nextInt(count) < 1;
        }

        count ++;
        if(totalBetAmount >= BET_AMOUNT_4)
        {
            return RandomUtils.nextInt(count) < 1;
        }

        return RandomUtils.nextInt(25) < 1;
    }



}

