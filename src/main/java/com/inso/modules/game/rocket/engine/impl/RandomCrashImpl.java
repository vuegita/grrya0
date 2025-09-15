package com.inso.modules.game.rocket.engine.impl;

import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.RandomUtils;
import com.inso.modules.game.rocket.engine.CrashSupport;
import com.inso.modules.game.rocket.logical.RocketPeriodStatus;

public class RandomCrashImpl implements CrashSupport {

    private static Log LOG = LogFactory.getLog(RandomCrashImpl.class);

    private int maxRandCount = 32;
    private static boolean debug = false;


    protected float mLastCrashTime = -1;

    public RandomCrashImpl(int randCount)
    {
        this.maxRandCount = randCount;
    }

    public RandomCrashImpl()
    {
    }
    @Override
    public boolean support(RocketPeriodStatus status) {
        return true;
    }

    @Override
    public boolean verify(float openResult, RocketPeriodStatus status) {
        if(openResult >= 90)
        {
            return true;
        }

        if(openResult < 2.6)
        {
            if(verifyCrash(openResult, maxRandCount, 3))
            {
                if(debug)
                {
                    LOG.info("crash from 1");
                }
                return true;
            }
        }

        // 大于2.6
        boolean rs;
        long ts = System.currentTimeMillis();
        if(mLastCrashTime > 0 && ts - mLastCrashTime > 280_000)
        {
            rs = RandomUtils.nextInt(25) < 1;
        }
        else
        {
            rs = RandomUtils.nextInt(20) < 1;
        }

        if(rs)
        {
            mLastCrashTime = ts;
        }

        if(rs && debug)
        {
            LOG.info("crash from 2");
        }
        return rs;
    }

    private boolean verifyCrash(float openResult, int maxValue, int minvValue)
    {
        float stepResult = 0.1f;
        float currentResult = 1.0f;
        while (openResult > currentResult && maxValue >= minvValue)
        {
            currentResult += stepResult;
            maxValue = maxValue - 2;
        }

        int rsReslult = RandomUtils.nextInt(maxValue);

        if(debug)
        {
            LOG.debug("check rValue = " + maxValue + ", currentResult = " + currentResult);
        }
//        LOG.debug("check rValue = " + maxValue + ", currentResult = " + currentResult + ", rsReslult = " + rsReslult);
        return rsReslult < 1;
    }

    public static void main(String[] args) {
        debug = true;
        RandomCrashImpl mgr = new RandomCrashImpl();

        int rsTrue = 0;
        for(int i = 0; i < 100; i ++)
        {
            float openResult = 1.1f;
            boolean rs = mgr.verify(openResult, null);
            if(rs)
            {
                rsTrue ++;
            }
        }

        System.out.println("=====================================" + rsTrue);

    }
}
