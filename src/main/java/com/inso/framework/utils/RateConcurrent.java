package com.inso.framework.utils;

import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.RateLimiter;

public class RateConcurrent {

    private RateLimiter mRateLimiter;

    public RateConcurrent(double permitsPerSecond)
    {
        this.mRateLimiter = RateLimiter.create(permitsPerSecond);
    }

    public boolean tryAcquire(long waitSeconds)
    {
        return mRateLimiter.tryAcquire(waitSeconds, TimeUnit.SECONDS);
    }

}
