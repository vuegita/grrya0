package com.inso.modules.game.fruit.job;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.inso.framework.context.MyEnvironment;
import com.inso.modules.web.SystemRunningMode;
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
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.fruit.config.FruitConfig;
import com.inso.modules.game.fruit.helper.FruitHelper;
import com.inso.modules.game.fruit.logical.FruitLatestPeriodCache;
import com.inso.modules.game.fruit.logical.FruitOpenTaskManager;
import com.inso.modules.game.fruit.logical.FruitPeriodStatus;
import com.inso.modules.game.fruit.model.FruitBetItemType;
import com.inso.modules.game.fruit.model.FruitPeriodInfo;
import com.inso.modules.game.fruit.model.FruitType;
import com.inso.modules.game.fruit.service.FruitPeriodService;
import com.inso.modules.game.model.GamePeriodStatus;
import com.inso.modules.game.service.GameService;
import com.inso.modules.web.service.ConfigService;

/**
 * 开奖任务
 */
public class FruitOpenJob implements Job {

    private static Log LOG = LogFactory.getLog(FruitOpenJob.class);

    private static String QUEUE_NAME = "inso_game_fruit_open_result";

    private static final String KEY_ISSUE = "issue";

    private ConfigService mConfigService;
    private GameService mGameService;
    private FruitPeriodService mPeriodService;

    private FruitOpenTaskManager mOpenTaskManager;

    private static MQSupport mq = MQManager.getInstance().getMQ(MQManager.MQType.REDIS);

    /*** 线程池 ***/
    private static ExecutorService mThreadPool = Executors.newFixedThreadPool(10);

    public FruitOpenJob()
    {
        this.mConfigService = SpringContextUtils.getBean(ConfigService.class);
        this.mGameService = SpringContextUtils.getBean(GameService.class);
        this.mPeriodService = SpringContextUtils.getBean(FruitPeriodService.class);
        this.mOpenTaskManager = SpringContextUtils.getBean(FruitOpenTaskManager.class);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        if(!SystemRunningMode.isBCMode())
        {
            return;
        }

//        if(!MyEnvironment.isDev())
//        {
//            return;
//        }

        Date fireTime = context.getFireTime();

        String typeString = context.getJobDetail().getJobDataMap().getString("type");
        FruitType type = FruitType.getType(typeString);

        // 定时任务
        if(type != null)
        {
            handleTask(type, fireTime);
            return;
        }

        // 首次执行才走下面
        FruitType[] allType = FruitType.values();
        for(FruitType tmp : allType)
        {
            FruitPeriodInfo periodInfo = mPeriodService.findCurrentRunning(tmp);
            if(periodInfo == null)
            {
                continue;
            }

            handleTask(tmp, periodInfo.getStarttime());

            // 检查上上期是否开奖
            DateTime fireDateTime = new DateTime(periodInfo.getStarttime());
            finishGameByTime(tmp, fireDateTime.minusMinutes(tmp.getStepOfMinutes()));
        }

        // boot mq
        startMQ();
    }

    private void handleTask(FruitType type, Date fireTime)
    {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (type)
                {
                    DateTime fireDateTime = new DateTime(fireTime);

                    // 3. 上期开奖
                    finishGameByTime(type, fireDateTime);

                    // 4. 更新最新期号缓存
                    DateTime endTime = fireDateTime.minusSeconds(10);
                    DateTime starTime = endTime.minusMinutes(3600);

                    String startTimeString = starTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
                    String endTimeString = endTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
                    List<FruitPeriodInfo> rsList = mPeriodService.queryByTime(type, startTimeString, endTimeString, 200);
                    FruitLatestPeriodCache.updateCache(type, rsList);
                }
            }
        });
    }

    public void finishGameByTime(FruitType type, DateTime dateTime)
    {

        String issue = FruitHelper.generateIssue(type, dateTime);
        finishGameByIssue(type, issue);
    }

    public void finishGameByIssue(FruitType type, String issue)
    {
        // 防止并发
        synchronized (type.getKey())
        {
            FruitPeriodInfo model = mPeriodService.findByIssue(true, issue);
            if(model == null)
            {
                return;
            }

            // 1. 获取开奖结果
            FruitBetItemType openResult = model.getOpenResultType();
            if(openResult == null)
            {
                // 获取开奖模式
                String openModeString = mConfigService.getValueByKey(false, FruitConfig.GAME_FRUIT_OPEN_MODE);
//                LotteryOpenMode openMode = LotteryOpenMode.getType(openModeString);
                model.setOpenMode(openModeString);

                String openRate = mConfigService.getValueByKey(false, FruitConfig.GAME_FRUIT_OPEN_RATE);

                int smartNum = mConfigService.getInt(false, FruitConfig.GAME_FRUIT_OPEN_SMART_NUM);

                // 如果设置为比例
                float platformRate = StringUtils.asFloat(openRate);

                openResult = mOpenTaskManager.getOpenResult(model, platformRate, smartNum);
            }

            if(openResult == null)
            {
                return;
            }

            // 2. 更新数据库开奖表
            GamePeriodStatus periodStatus = GamePeriodStatus.getType(model.getStatus());
            if(model.getOpenResultType() == null || periodStatus != GamePeriodStatus.FINISH)
            {
                mPeriodService.updateStatusToFinish(model, openResult);
            }

            // 3. 更新缓存
            FruitPeriodStatus status = FruitPeriodStatus.loadCache(true, type, issue);
            status.setOpenResult(openResult);
//            status.updateCardList();
            status.saveCache();

            // 4. 更新订单信息
            mOpenTaskManager.handleOpenResultForAllOrder(model.getIssue(), openResult);
        }
    }

    private void startMQ()
    {
        mq.subscribe(QUEUE_NAME, null, new Callback<String>() {
            @Override
            public void execute(String msg) {

//                LOG.info("receiv issue = " + msg);

                String issue = msg;

                FruitPeriodInfo info = mPeriodService.findByIssue(false, issue);
                if(info == null)
                {
                    return;
                }

                FruitType type = FruitType.getType(info.getType());

                // 是2期之前
                long time = System.currentTimeMillis() - type.getStepOfMinutes() * 2 * 60 * 1000;
                if(time - info.getStarttime().getTime() < 0)
                {
                    return;
                }

                finishGameByIssue(type, issue);

            }
        });
    }


    public void test2()
    {
        FruitType type = FruitType.PRIMARY;
        String time = "2021-05-17 13:17:00";

        Date fireTime = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, time);

        handleTask(FruitType.PRIMARY, fireTime);
    }


    public void test()
    {
        FruitType type = FruitType.PRIMARY;
        String time = "2021-03-27 10:50:00";

        Date fireTime = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, time);
//        if(!doCheckGenerate(LotteryType.RG3, fireTime))
//        {
//            doGeneratePeriod(fireTime, LotteryType.RG3);
//        }
//        FruitPeriodInfo periodInfo = mPeriodService.findCurrentRunning(LotteryType.BECONE);
//        handleTask(LotteryType.BECONE, periodInfo.getStarttime());


        DateTime endTime = new DateTime(fireTime).minusSeconds(10);
        DateTime starTime = endTime.minusMinutes(120);

        String startTimeString = starTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String endTimeString = endTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        List<FruitPeriodInfo> rsList = mPeriodService.queryByTime(type, startTimeString, endTimeString, 10);
        System.out.println(FastJsonHelper.jsonEncode(rsList));
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
        FruitOpenJob job = new FruitOpenJob();
        job.test();
    }

}
