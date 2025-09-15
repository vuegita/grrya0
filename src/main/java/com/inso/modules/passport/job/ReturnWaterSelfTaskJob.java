package com.inso.modules.passport.job;

import com.inso.framework.spring.SpringContextUtils;
import com.inso.modules.passport.returnwater.ReturnWaterSelfManager;
import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class ReturnWaterSelfTaskJob implements Job {


    private ReturnWaterSelfManager mReturnWaterSelfManager;

    public ReturnWaterSelfTaskJob()
    {
        this.mReturnWaterSelfManager = SpringContextUtils.getBean(ReturnWaterSelfManager.class);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        DateTime dateTime = new DateTime().minusDays(1);
//        mReturnWaterSelfManager.doTask(dateTime, true);



    }

    public void test()
    {
        DateTime dateTime = new DateTime().minusDays(8);
//        mReturnWaterSelfManager.doTask(dateTime, false);
    }

}
