package com.inso.modules.game.fruit.job;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.inso.modules.web.SystemRunningMode;
import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.Status;
import com.inso.modules.game.fruit.config.FruitConfig;
import com.inso.modules.game.fruit.helper.FruitHelper;
import com.inso.modules.game.fruit.logical.FruitOpenTaskManager;
import com.inso.modules.game.fruit.logical.FruitPeriodStatus;
import com.inso.modules.game.fruit.logical.FruitRunningStatus;
import com.inso.modules.game.fruit.model.FruitPeriodInfo;
import com.inso.modules.game.fruit.model.FruitType;
import com.inso.modules.game.fruit.service.FruitPeriodService;
import com.inso.modules.game.model.GameInfo;
import com.inso.modules.game.model.GameOpenMode;
import com.inso.modules.game.model.GamePeriodStatus;
import com.inso.modules.game.service.GameService;
import com.inso.modules.web.service.ConfigService;

/**
 * 开盘任务
 */
public class FruitBeginJob implements Job {

    private static Log LOG = LogFactory.getLog(FruitBeginJob.class);

    private static final String KEY_ISSUE = "issue";

    private ConfigService mConfigService;
    private GameService mGameService;
    private FruitPeriodService mPeriodService;

    private FruitOpenTaskManager mOpenTaskManager;

    /*** 线程池 ***/
    private static ExecutorService mThreadPool = Executors.newFixedThreadPool(5);

    public FruitBeginJob()
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

        Date fireTime = context.getFireTime();

        String typeString = context.getJobDetail().getJobDataMap().getString("type");
        FruitType type = FruitType.getType(typeString);
        handleTask(type, fireTime);
    }

    private void handleTask(FruitType type, Date fireTime)
    {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (type)
                {
                    DateTime fireDateTime = new DateTime(fireTime);

                    // 1. check period and generate
                    if(!doCheckGenerate(type, fireTime))
                    {
                        doGeneratePeriod(fireTime, type);
                    }

                    // 2. 开盘
                    beginGame(type, fireDateTime);
                }
            }
        });

    }

    public void beginGame(FruitType type, DateTime fireDateTime)
    {
        // 当前期号
        String issue = FruitHelper.generateIssue(type, fireDateTime);
        FruitPeriodInfo model = mPeriodService.findByIssue(true, issue);

        GamePeriodStatus status = GamePeriodStatus.getType(model.getStatus());
        if(status == GamePeriodStatus.PENDING)
        {
            mPeriodService.updateStatusToWaiting(issue);
        }
        else  if(status == GamePeriodStatus.FINISH)
        {
            return;
        }

        // 初始化状态
        FruitPeriodStatus periodStatus = FruitPeriodStatus.loadCache(true, type, issue);
        if(!periodStatus.isInit())
        {
            String maxMoneyOfIssueValue = mConfigService.getValueByKey(false, FruitConfig.GAME_FRUIT_MAX_MONEY_OF_ISSUE);
            String maxMoneyOfUserValue = mConfigService.getValueByKey(false, FruitConfig.GAME_FRUIT_MAX_MONEY_OF_USER);
            periodStatus.setStartTime(model.getStarttime());
            periodStatus.setEndTime(model.getEndtime());
            periodStatus.setMaxMoneyOfUser(StringUtils.asFloat(maxMoneyOfUserValue));
            periodStatus.setMaxMoneyOfIssue(StringUtils.asFloat(maxMoneyOfIssueValue));
//            periodStatus.setStartIndex((long)model.getOpenCardNum());
            periodStatus.saveCache();
        }

        // 更新当前运行状态
        GameInfo game = mGameService.findByKey(false, type.getKey());
        FruitRunningStatus runningStatus = FruitRunningStatus.loadCache(type);
        runningStatus.setCurrentIssue(model.getIssue());
        runningStatus.setStatus(Status.getType(game.getStatus()));
        runningStatus.saveCache();

//        LOG.info("start open period = " + issue + ", firetime = " + fireDateTime);
    }


    public void doGeneratePeriod(Date fireTime, FruitType type)
    {
        GameInfo game = mGameService.findByKey(false, type.getKey());
        DateTime nowTime = new DateTime(fireTime);
        int todayOfWeek = nowTime.getDayOfWeek();

        String openModeString = mConfigService.getValueByKey(false, FruitConfig.GAME_FRUIT_OPEN_MODE);
        GameOpenMode openMode = GameOpenMode.getType(openModeString);

        int currentIndex = 0;
        int maxCount = 100;

        boolean stop = false;
        // 从下一期开始
        int i = 0;
        while(!stop)
        {
            DateTime startTime = nowTime.plusSeconds(60 * i);   //加多少秒
            DateTime endTime = nowTime.plusSeconds(60 * (i + type.getStepOfMinutes())).minusSeconds(1);


            if(endTime.getDayOfWeek() != todayOfWeek)
            {
                stop = true;
                break;
            }

            if(currentIndex >= maxCount)
            {
                stop = true;
                break;
            }

//            String timeString = "time = " + DateUtils.convertString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, startTime);
//            timeString += " - " + DateUtils.convertString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, endTime);
//            System.out.println(timeString);
            String issue = FruitHelper.generateIssue(type, startTime);
            mPeriodService.add(type, issue, game.getId(), openMode, startTime.toDate(), endTime.toDate());

            //
            i += type.getStepOfMinutes();

            currentIndex++;
        }
    }

    private boolean doCheckGenerate(FruitType type, Date fireTime)
    {
        String startTimeString = DateUtils.convertString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, fireTime);

        String dayString = DateUtils.convertString(DateUtils.TYPE_YYYY_MM_DD, fireTime);
        String endTimeString = DateUtils.getEndTimeOfDay(dayString);

//        Date endTime = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, endTimeString);

        long count = mPeriodService.count(type, startTimeString, endTimeString);
        return count > 0;
    }


    public void test()
    {
        FruitType type = FruitType.PRIMARY;
        String time = "2021-05-04 10:50:00";

        Date fireTime = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, time);
        if(!doCheckGenerate(FruitType.PRIMARY, fireTime))
        {
            doGeneratePeriod(fireTime, FruitType.PRIMARY);
        }
//        FruitPeriodInfo periodInfo = mPeriodService.findCurrentRunning(LotteryType.BECONE);
//        handleTask(LotteryType.BECONE, periodInfo.getStarttime());


//        DateTime endTime = new DateTime(fireTime).minusSeconds(10);
//        DateTime starTime = endTime.minusMinutes(120);
//
//        String startTimeString = starTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
//        String endTimeString = endTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
//        List<FruitPeriodInfo> rsList = mPeriodService.queryByTime(type, startTimeString, endTimeString, 10);
//        System.out.println(FastJsonHelper.jsonEncode(rsList));
    }

    public static void sendMessage(String issue)
    {
        if(StringUtils.isEmpty(issue))
        {
            return;
        }
//        mq.sendMessage(QUEUE_NAME, issue);
    }

    public static void main(String[] args) {
        FruitBeginJob job = new FruitBeginJob();
        job.test();
    }

}
