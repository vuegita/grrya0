package com.inso.modules.game.rg.job;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.inso.modules.game.lottery_game_impl.MyLotteryRobotManager;
import com.inso.modules.game.rg.helper.RGTotalAmountHelper;
import com.inso.modules.game.rg.logical.*;
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
import com.inso.modules.common.model.Status;
import com.inso.modules.game.model.GameInfo;
import com.inso.modules.game.model.GameOpenMode;
import com.inso.modules.game.model.GamePeriodStatus;
import com.inso.modules.game.rg.config.LotteryConfig;
import com.inso.modules.game.rg.helper.LotteryHelper;
import com.inso.modules.game.rg.model.LotteryPeriodInfo;
import com.inso.modules.game.rg.model.LotteryRGType;
import com.inso.modules.game.rg.service.LotteryPeriodService;
import com.inso.modules.game.service.GameService;
import com.inso.modules.web.service.ConfigService;

public class LotteryRgJob implements Job {

    private static Log LOG = LogFactory.getLog(LotteryRgJob.class);

    private static final String QUEUE_NAME = "inso_game_lottery_open_result";

    private static final String KEY_ISSUE = "issue";

    private ConfigService mConfigService;
    private GameService mGameService;
    private LotteryPeriodService mPeriodService;
    private RGOpenTaskManager mLotteryOpenMgr;

    private static MQSupport mq = MQManager.getInstance().getMQ(MQManager.MQType.REDIS);

    /*** 线程池 ***/
    private static ExecutorService mThreadPool = Executors.newFixedThreadPool(10);

    public LotteryRgJob()
    {
        this.mConfigService = SpringContextUtils.getBean(ConfigService.class);
        this.mGameService = SpringContextUtils.getBean(GameService.class);
        this.mPeriodService = SpringContextUtils.getBean(LotteryPeriodService.class);
        this.mLotteryOpenMgr = SpringContextUtils.getBean(RGOpenTaskManager.class);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        if(!SystemRunningMode.isBCMode())
        {
            return;
        }

        //LOG.info("start LotteryRgJob ========== ");
        Date fireTime = context.getFireTime();

        String typeString = context.getJobDetail().getJobDataMap().getString("type");
        LotteryRGType type = LotteryRGType.getType(typeString);

        // 定时任务
        if(type != null)
        {
            handleTask(type, fireTime);
            return;
        }

        // 首次执行才走下面
        LotteryRGType[] allType = LotteryRGType.values();
        for(LotteryRGType tmp : allType)
        {
            LotteryPeriodInfo periodInfo = mPeriodService.findCurrentRunning(tmp);
            if(periodInfo == null)
            {
                continue;
            }

            handleTask(tmp, periodInfo.getStarttime());

            DateTime fireDateTime = new DateTime(periodInfo.getStarttime());
            finishGameByTime(tmp, fireDateTime.minusMinutes(tmp.getStepOfMinutes() * 2));

            // 上上期
            finishGameByTime(tmp, fireDateTime.minusMinutes(tmp.getStepOfMinutes() * 3));
        }

        // boot mq
        startMQ();
    }

    private void handleTask(LotteryRGType type, Date fireTime)
    {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (type)
                    {
                        GameInfo game = mGameService.findByKey(false, type.getKey());
                        if(game == null)
                        {
                            return;
                        }

                        if(Status.DISABLE.getKey().equalsIgnoreCase(game.getStatus()))
                        {
                            return;
                        }


                        DateTime fireDateTime = new DateTime(fireTime);

                        // 1. check period and generate
                        if(!doCheckGenerate(type, fireTime, true))
                        {
                            doGeneratePeriod(fireTime, type, true);
                        }

                        // 2. 开盘
                        beginGame(type, game, fireDateTime);

                        // 3. 上期开奖
                        finishGameByTime(type, fireDateTime.minusMinutes(type.getStepOfMinutes()));
                        RGTotalAmountHelper.getInstance().update();

                        // 4. 更新最新期号缓存
                        DateTime endTime = fireDateTime.minusSeconds(10);
                        DateTime starTime = endTime.minusMinutes(3600);

                        String startTimeString = starTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
                        String endTimeString = endTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
                        List<LotteryPeriodInfo> rsList = mPeriodService.queryByTime(type, startTimeString, endTimeString, 200);
                        RGLatestPeriodCache.updateCache(type, rsList);

                        // 5. tomorrow time
                        DateTime tomorrowDateTime = fireDateTime.plusDays(1);
                        tomorrowDateTime = tomorrowDateTime.withSecondOfMinute(0);
                        tomorrowDateTime = tomorrowDateTime.withMinuteOfHour(0);
                        tomorrowDateTime = tomorrowDateTime.withHourOfDay(0);

                        Date tomorrowTime =  tomorrowDateTime.toDate();
                        if(!doCheckGenerate(type, tomorrowTime, false))
                        {
                            doGeneratePeriod(tomorrowTime, type, false);
                        }
                    }
                } catch (Exception exception) {
                    LOG.error("handleTask error", exception);
                }
            }
        });

    }

    private void startMQ()
    {
        mq.subscribe(QUEUE_NAME, null, new Callback<String>() {
            @Override
            public void execute(String msg) {

//                LOG.info("receiv issue = " + msg);

                String issue = msg;

                LotteryPeriodInfo info = mPeriodService.findByIssue(false, issue);
                if(info == null)
                {
                    return;
                }

                LotteryRGType type = LotteryRGType.getType(info.getType());

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

    public void beginGame(LotteryRGType type, GameInfo game, DateTime fireDateTime)
    {
        // 当前期号
        String issue = LotteryHelper.generateIssue(type, fireDateTime);
        LotteryPeriodInfo model = mPeriodService.findByIssue(true, issue);
        if(model == null)
        {
            // 期号生成异常重新生成
            doGeneratePeriod(fireDateTime.toDate(), type, true);
            model = mPeriodService.findByIssue(true, issue);
        }



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
        RGPeriodStatus periodStatus = RGPeriodStatus.loadCache(true, type, issue);
        if(periodStatus != null && !periodStatus.isInit())
        {
            String maxMoneyOfIssueValue = mConfigService.getValueByKey(false, LotteryConfig.GAME_LOTTERY_RG_MAX_MONEY_OF_ISSUE);
            String maxMoneyOfUserValue = mConfigService.getValueByKey(false, LotteryConfig.GAME_LOTTERY_RG_MAX_MONEY_OF_USER);
            periodStatus.setStartTime(model.getStarttime());
            periodStatus.setEndTime(model.getEndtime());
            periodStatus.setMaxMoneyOfUser(StringUtils.asFloat(maxMoneyOfUserValue));
            periodStatus.setMaxMoneyOfIssue(StringUtils.asFloat(maxMoneyOfIssueValue));
            periodStatus.saveCache();

            if(Status.ENABLE.getKey().equalsIgnoreCase(game.getStatus()))
            {
                long endTs = model.getEndtime().getTime() - type.getDisableMillis() - 10_000;
                MyLotteryRobotManager.sendMessage(type, issue, endTs);
            }

        }

        // 更新当前运行状态
        RGRunningStatus runningStatus = RGRunningStatus.loadCache(type);
        if(runningStatus != null)
        {
            runningStatus.setCurrentIssue(model.getIssue());
            runningStatus.setStatus(Status.getType(game.getStatus()));
            runningStatus.saveCache();
        }


//        LOG.info("start open period = " + issue + ", firetime = " + fireDateTime);
    }

    public void finishGameByTime(LotteryRGType type, DateTime dateTime)
    {
        String issue = LotteryHelper.generateIssue(type, dateTime);
        finishGameByIssue(type, issue);
    }

    public void finishGameByIssue(LotteryRGType type, String issue)
    {
        // 防止并发
        synchronized (type.getKey())
        {
            try {
                LotteryPeriodInfo model = mPeriodService.findByIssue(true, issue);
                if(model == null)
                {
                    return;
                }

                // 1. 获取开奖结果
                long openResult = model.getOpenResult();
                if(openResult < 0 || openResult > 9)
                {
                    // 设置开奖模式
                    String openModeString = mConfigService.getValueByKey(false, LotteryConfig.GAME_LOTTERY_RG_OPEN_MODE);
    //                LotteryOpenMode openMode = LotteryOpenMode.getType(openModeString);
                    model.setOpenMode(openModeString);

                    String openRate = mConfigService.getValueByKey(false, LotteryConfig.GAME_LOTTERY_RG_OPEN_RATE);

                    // 如果设置为比例
                    float platformRate = StringUtils.asFloat(openRate);

                    int smartNum=mConfigService.getInt(false, LotteryConfig.GAME_LOTTERY_RG_OPEN_SMART_NUM);

                    openResult = mLotteryOpenMgr.getOpenResult(model, platformRate ,smartNum );
                }

                if(openResult < 0 || openResult > 9)
                {
                    return;
                }

                // 2. 更新数据库开奖表
                GamePeriodStatus periodStatus = GamePeriodStatus.getType(model.getStatus());
                if(model.getOpenResult() == -1 || periodStatus != GamePeriodStatus.FINISH)
                {
                    mPeriodService.updateStatusToFinish(model, openResult);
                }

                // 3. 更新缓存
                RGPeriodStatus status = RGPeriodStatus.tryLoadCache(true, type, issue);
                if(status != null)
                {
                    status.setOpenResult(openResult);
                    status.saveCache();
                }

                // 4. 更新订单信息
                mLotteryOpenMgr.handleOpenResultForAllOrder(model.getIssue(), openResult);
            } catch (Exception e) {
                LOG.error("handle error:  issue = " + issue , e);
            }
        }
    }

    public void doGeneratePeriod(Date fireTime, LotteryRGType type, boolean isOnly100)
    {
        try {
            //LOG.info("doGeneratePeriod = " + fireTime + " - type " + type.getKey() + " - " + fireTime + " isOnly100 = " + isOnly100);
            GameInfo game = mGameService.findByKey(false, type.getKey());
            DateTime nowTime = new DateTime(fireTime);
            int todayOfWeek = nowTime.getDayOfWeek();

            String openModeString = mConfigService.getValueByKey(false, LotteryConfig.GAME_LOTTERY_RG_OPEN_MODE);
            GameOpenMode openMode = GameOpenMode.getType(openModeString);

            boolean stop = false;
            // 从下一期开始
            int i = 0;
            int index = 0;
            while(!stop)
            {
                if(isOnly100)
                {
                    // 每次最多生成100个期号
                    if(index >= 100)
                    {
                        break;
                    }
                }

                DateTime startTime = nowTime.plusSeconds(60 * i);
                // 整期
                DateTime endTime = nowTime.plusSeconds(60 * (i + type.getStepOfMinutes())).minusSeconds(1);

                if(endTime.getDayOfWeek() != todayOfWeek)
                {
                    stop = true;
                    return;
                }

    //            String timeString = "time = " + DateUtils.convertString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, startTime);
    //            timeString += " - " + DateUtils.convertString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, endTime);
    //            System.out.println(timeString);

                String issue = LotteryHelper.generateIssue(type, startTime);
                mPeriodService.add(type, issue, game.getId(), openMode, startTime.toDate(), endTime.toDate());

                //
                i += type.getStepOfMinutes();

                index ++;
            }
        } catch (Exception e) {
            // 期号可以创建错误- 重复id
            LOG.error("doGeneratePeriod error:", e);
        }
    }

    private boolean doCheckGenerate(LotteryRGType type, Date fireTime, boolean isOnly100)
    {
        String startTimeString = DateUtils.convertString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, fireTime);

        String dayString = DateUtils.convertString(DateUtils.TYPE_YYYY_MM_DD, fireTime);
        String endTimeString = DateUtils.getEndTimeOfDay(dayString);

//        Date endTime = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, endTimeString);

        long count = mPeriodService.count(type, startTimeString, endTimeString);
//        LOG.info("doCheckGenerate = " + type.getKey() + " - " + startTimeString + ", endString = " + endTimeString + " - count = " + count + " - isOnly100 + " + isOnly100);
        return count > 0;
    }


    public void test()
    {
        LotteryRGType type = LotteryRGType.PARITY;
        String time = "2021-03-27 10:50:00";

        Date fireTime = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, time);
//        if(!doCheckGenerate(LotteryType.RG3, fireTime))
//        {
//            doGeneratePeriod(fireTime, LotteryType.RG3);
//        }
//        LotteryPeriodInfo periodInfo = mPeriodService.findCurrentRunning(LotteryType.BECONE);
//        handleTask(LotteryType.BECONE, periodInfo.getStarttime());


        DateTime endTime = new DateTime(fireTime).minusSeconds(10);
        DateTime starTime = endTime.minusMinutes(120);

        String startTimeString = starTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String endTimeString = endTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        List<LotteryPeriodInfo> rsList = mPeriodService.queryByTime(type, startTimeString, endTimeString, 10);
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
        LotteryRgJob job = new LotteryRgJob();
        job.test();
    }

}
