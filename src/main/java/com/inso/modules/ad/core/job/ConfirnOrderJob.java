package com.inso.modules.ad.core.job;

import com.inso.framework.context.MyEnvironment;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.modules.ad.core.logical.AdOrderManager;
import com.inso.modules.ad.core.model.AdEventOrderInfo;
import com.inso.modules.ad.core.model.AdEventType;
import com.inso.modules.ad.core.service.EventOrderService;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.web.SystemRunningMode;
import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;

/**
 * 确认订单状态
 */
public class ConfirnOrderJob implements Job {

    private static Log LOG = LogFactory.getLog(ConfirnOrderJob.class);

    private EventOrderService mEventOrderService;

    private AdOrderManager mAdOrderManager;

    private static boolean isRunning = false;

    public ConfirnOrderJob()
    {
        this.mEventOrderService = SpringContextUtils.getBean(EventOrderService.class);
        this.mAdOrderManager = SpringContextUtils.getBean(AdOrderManager.class);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        start(context.getFireTime());
    }

    public void test()
    {
        start(new Date());
    }

    private void start(Date date)
    {
        if(isRunning)
        {
            return;
        }

        isRunning = true;

        try {
            SystemRunningMode mode = SystemRunningMode.getSystemConfig();
            if(!(MyEnvironment.isDev() || mode == SystemRunningMode.FUNDS))
            {
                return;
            }

            DateTime toTime = new DateTime(date);
            toTime = toTime.minusMinutes(1);
            DateTime fromTime = toTime.minusMinutes(5);

            mEventOrderService.queryAll(fromTime, toTime, null, new Callback<AdEventOrderInfo>() {
                @Override
                public void execute(AdEventOrderInfo orderInfo) {
                    OrderTxStatus txStatus = OrderTxStatus.getType(orderInfo.getStatus());
                    if(txStatus != OrderTxStatus.NEW)
                    {
                        return;
                    }

                    AdEventType eventType = AdEventType.getType(orderInfo.getEventType());
                    if(eventType == AdEventType.SHOP)
                    {

                    }
                    else
                    {
                        mAdOrderManager.passOrder(orderInfo);
                    }
                }
            });
        } catch (Exception e) {
            LOG.error("handle error:", e);
        }
        finally {
            isRunning = false;
        }
    }



}
