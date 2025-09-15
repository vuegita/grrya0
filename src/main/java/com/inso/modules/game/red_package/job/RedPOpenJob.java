package com.inso.modules.game.red_package.job;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.mq.MQManager;
import com.inso.framework.mq.MQSupport;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.red_package.logical.RedPOpenTaskManager;
import com.inso.modules.game.red_package.model.RedPPeriodInfo;
import com.inso.modules.game.red_package.model.RedPType;
import com.inso.modules.game.red_package.service.RedPPeriodService;
import com.inso.modules.game.service.GameService;
import com.inso.modules.web.service.ConfigService;

/**
 * 开奖任务
 */
public class RedPOpenJob implements Job {

    private static Log LOG = LogFactory.getLog(RedPOpenJob.class);

    private static String QUEUE_NAME = "inso_game_red_package_open_result";

    private static final String KEY_ISSUE = "issue";

    private ConfigService mConfigService;
    private GameService mGameService;
    private RedPPeriodService mPeriodService;

    private RedPOpenTaskManager mOpenTaskManager;

    private static MQSupport mq = MQManager.getInstance().getMQ(MQManager.MQType.REDIS);

    /*** 线程池 ***/
    private static ExecutorService mThreadPool = Executors.newFixedThreadPool(10);

    public RedPOpenJob()
    {
        this.mConfigService = SpringContextUtils.getBean(ConfigService.class);
        this.mGameService = SpringContextUtils.getBean(GameService.class);
        this.mPeriodService = SpringContextUtils.getBean(RedPPeriodService.class);
        this.mOpenTaskManager = SpringContextUtils.getBean(RedPOpenTaskManager.class);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Date fireTime = context.getFireTime();

        String typeString = context.getJobDetail().getJobDataMap().getString("type");
        RedPType type = RedPType.getType(typeString);

        // 定时任务
        if(type != null)
        {
            handleTask(type, fireTime);
            return;
        }

//        // 首次执行才走下面
//        RedPType[] allType = RedPType.values();
//        for(RedPType tmp : allType)
//        {
//            RedPPeriodInfo periodInfo = mPeriodService.findCurrentRunning(tmp);
//            if(periodInfo == null)
//            {
//                continue;
//            }
//
//            handleTask(tmp, periodInfo.getStarttime());
//
//            // 检查上上期是否开奖
//            DateTime fireDateTime = new DateTime(periodInfo.getStarttime());
//            finishGameByTime(tmp, fireDateTime.minusMinutes(tmp.getStepOfMinutes()));
//        }

        // boot mq
        startMQ();
    }

    private void handleTask(RedPType type, Date fireTime)
    {
        // 1分钟
        DateTime endTime = new DateTime(fireTime).minusMinutes(1);
        DateTime startTime =  endTime.minusMinutes(2);
        String startTimeString = startTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String endTimeString = endTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        // 取前1分钟的数据开奖
        mPeriodService.queryAllByUpdatetime(RedPType.NUMBER, startTimeString, endTimeString, new Callback<RedPPeriodInfo>() {
            public void execute(RedPPeriodInfo o) {
                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        RedPType myType = RedPType.getType(o.getRpType());
                        finishGameByIssue(myType, o.getId());
                    }
                });
            }
        });

    }


    public void finishGameByIssue(RedPType type, long issue)
    {
        // 防止并发
        synchronized (type.getKey())
        {
            RedPPeriodInfo model = mPeriodService.findByIssue(true, issue);
            if(model == null)
            {
                return;
            }

            // 1. 获取开奖结果
//            long openResult = model.getOpenResult();
//            if(openResult == null)
//            {
//                // 获取开奖模式
//                String openModeString = mConfigService.getValueByKey(false, RedPConfig.GAME_RED_PACKAGE_PLATFORM_CONFIG_OPEN_MODE);
//                String openRate = mConfigService.getValueByKey(false, RedPConfig.GAME_RED_PACKAGE_PLATFORM_CONFIG_OPEN_RATE);
//
//                // 会员获取会员开奖配置
//                RedPCreatorType creatorType = model.getRedPCreatorType();
//                if(creatorType == RedPCreatorType.MEMBER)
//                {
//                    openModeString = mConfigService.getValueByKey(false, RedPConfig.GAME_RED_PACKAGE_MEMBER_CONFIG_OPEN_MODE);
//                    openRate = mConfigService.getValueByKey(false, RedPConfig.GAME_RED_PACKAGE_MEMBER_CONFIG_OPEN_RATE);
//                }
//                // 获取开奖模式
//                model.setOpenMode(openModeString);
//
//                // 如果设置为比例
//                float platformRate = StringUtils.asFloat(openRate);
//                openResult = mOpenTaskManager.getOpenResult(model, platformRate);
//            }
//
//            if(openResult == null)
//            {
//                return;
//            }

//            // 2. 更新数据库开奖表
//            GamePeriodStatus periodStatus = GamePeriodStatus.getType(model.getStatus());
//            if(model.getOpenResultType() == null || periodStatus != GamePeriodStatus.FINISH)
//            {
//                mPeriodService.updateStatusToFinish(model, openResult);
//            }
//
//            // 3. 更新缓存
//            RedPPeriodStatus status = RedPPeriodStatus.loadCache(true, type, issue);
//            status.setOpenResult(openResult);
//            status.saveCache();
//
//            // 4. 更新订单信息
//            mOpenTaskManager.handleOpenResultForAllOrder(model.getId(), openResult);
        }
    }

    private void startMQ()
    {
        mq.subscribe(QUEUE_NAME, null, new Callback<String>() {
            @Override
            public void execute(String msg) {

//                LOG.info("receiv issue = " + msg);

                String issue = msg;

                long id = StringUtils.asLong(issue);

                RedPPeriodInfo info = mPeriodService.findByIssue(false, id);
                if(info == null)
                {
                    return;
                }

                RedPType type = RedPType.getType(info.getRpType());

                // 是2期之前
//                long time = System.currentTimeMillis() - type.getStepOfMinutes() * 2 * 60 * 1000;
//                if(time - info.getStarttime().getTime() < 0)
//                {
//                    return;
//                }
//
//                finishGameByIssue(type, issue);
            }
        });
    }


    public void test2()
    {
//        RedPType type = RedPType.PRIMARY;
//        String time = "2021-05-17 13:17:00";
//
//        Date fireTime = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, time);
//
//        handleTask(RedPType.PRIMARY, fireTime);
    }


    public void test()
    {
//        RedPType type = RedPType.PRIMARY;
        String time = "2021-03-27 10:50:00";

        Date fireTime = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, time);
//        if(!doCheckGenerate(LotteryType.RG3, fireTime))
//        {
//            doGeneratePeriod(fireTime, LotteryType.RG3);
//        }
//        RedPPeriodInfo periodInfo = mPeriodService.findCurrentRunning(LotteryType.BECONE);
//        handleTask(LotteryType.BECONE, periodInfo.getStarttime());


        DateTime endTime = new DateTime(fireTime).minusSeconds(10);
        DateTime starTime = endTime.minusMinutes(120);

        String startTimeString = starTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String endTimeString = endTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
//        List<RedPPeriodInfo> rsList = mPeriodService.queryByTime(type, startTimeString, endTimeString, 10);
//        System.out.println(FastJsonHelper.jsonEncode(rsList));
    }

    public static void sendMessage(String issue)
    {
        if(StringUtils.isEmpty(issue))
        {
            return;
        }
        mq.sendMessage(QUEUE_NAME, issue);
    }

    public static void main(String[] args) {
        RedPOpenJob job = new RedPOpenJob();
        job.test();
    }

}
