package com.inso.modules.game.rocket.engine.impl;

import com.inso.framework.utils.RandomUtils;
import com.inso.modules.game.rocket.engine.CrashSupport;
import com.inso.modules.game.rocket.logical.RocketPeriodStatus;

public class SafeCrashImpl implements CrashSupport {



    private float mLastOpenResult = -1;
    private CrashSupport mImpl;


    private float OPEN_RESULT_1 = 1.3f;
    private float OPEN_RESULT_2 = 1.5f;
    private float OPEN_RESULT_3 = 1.6f;
    private float OPEN_RESULT_4 = 1.8f;
    private float OPEN_RESULT_5 = 2.0f;

    private int mSeedValue = 5;


    public void setmSeedValue(int value)
    {
        this.mSeedValue = value;
    }


    public SafeCrashImpl(CrashSupport impl)
    {
        this.mImpl = impl;
    }

    @Override
    public boolean support(RocketPeriodStatus status) {
        return mImpl.support(status);
    }

    @Override
    public boolean verify(float openResult, RocketPeriodStatus status) {

//        if(openResult >= 80)
//        {
//            return true;
//        }
//        boolean aa = true;
//        if(aa)
//        {
//            return false;
//        }

        boolean rs = mImpl.verify(openResult, status);

        if(!rs)
        {
            return rs;
        }

        if(checkSame(openResult))
        {
            rs = RandomUtils.nextInt(mSeedValue) < 1;
            if(rs)
            {
                this.mLastOpenResult = openResult;
            }
            else
            {
                return false;
            }
        }
        else if(mLastOpenResult == -1)
        {
            this.mLastOpenResult = openResult;
        }
        return rs;
    }

    private boolean checkSame(float openReuslt)
    {
        if(openReuslt <= OPEN_RESULT_1 && mLastOpenResult <= OPEN_RESULT_1)
        {
            return true;
        }
        if(openReuslt > OPEN_RESULT_1 && openReuslt <= OPEN_RESULT_2 && mLastOpenResult > OPEN_RESULT_1 && mLastOpenResult <= OPEN_RESULT_2)
        {
            return true;
        }
        if(openReuslt > OPEN_RESULT_2 && openReuslt <= OPEN_RESULT_3 && mLastOpenResult > OPEN_RESULT_2 && mLastOpenResult <= OPEN_RESULT_3)
        {
            return true;
        }
        if(openReuslt > OPEN_RESULT_3 && openReuslt <= OPEN_RESULT_4 && mLastOpenResult > OPEN_RESULT_3 && mLastOpenResult <= OPEN_RESULT_4)
        {
            return true;
        }
        if(openReuslt > OPEN_RESULT_4 && openReuslt <= OPEN_RESULT_5 && mLastOpenResult > OPEN_RESULT_4 && mLastOpenResult <= OPEN_RESULT_5)
        {
            return true;
        }
        return false;
    }


}
