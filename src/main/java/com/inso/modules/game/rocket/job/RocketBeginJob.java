package com.inso.modules.game.rocket.job;

import com.inso.framework.cache.CacheManager;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.mq.MQManager;
import com.inso.framework.mq.MQSupport;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.Status;
import com.inso.modules.game.lottery_game_impl.GameResultManager;
import com.inso.modules.game.lottery_game_impl.NewLotteryRunningStatus;
import com.inso.modules.game.lottery_game_impl.helper.NewLotteryLatestPeriod;
import com.inso.modules.game.lottery_game_impl.turntable.config.TurntableConfig;
import com.inso.modules.game.lottery_game_impl.turntable.model.TurnTableType;
import com.inso.modules.game.model.GameInfo;
import com.inso.modules.game.model.GameOpenMode;
import com.inso.modules.game.model.GamePeriodStatus;
import com.inso.modules.game.model.NewLotteryPeriodInfo;
import com.inso.modules.game.rocket.cache.RocketCacheHelper;
import com.inso.modules.game.rocket.config.RocketConfig;
import com.inso.modules.game.rocket.engine.RocketPlayEngine;
import com.inso.modules.game.rocket.helper.RocketHelper;
import com.inso.modules.game.rocket.helper.RocketOpenResultHelp;
import com.inso.modules.game.rocket.logical.RocketOpenTaskManager;
import com.inso.modules.game.rocket.logical.RocketPeriodStatus;
import com.inso.modules.game.rocket.model.RocketType;
import com.inso.modules.game.service.GameService;
import com.inso.modules.game.service.NewLotteryPeriodService;
import com.inso.modules.web.SystemRunningMode;
import com.inso.modules.web.service.ConfigService;
import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;
import java.util.List;

public class RocketBeginJob implements Job {

    private static Log LOG = LogFactory.getLog(RocketBeginJob.class);

    private static final String KEY_ISSUE = "issue";

    private static final String QUEUE_NAME = RocketBeginJob.class.getName();

    private ConfigService mConfigService;
    private GameService mGameService;
    private NewLotteryPeriodService mPeriodService;
    private RocketOpenTaskManager mLotteryOpenMgr;

    private static MQSupport mq = MQManager.getInstance().getMQ(MQManager.MQType.REDIS);

    public RocketBeginJob()
    {
        this.mConfigService = SpringContextUtils.getBean(ConfigService.class);
        this.mGameService = SpringContextUtils.getBean(GameService.class);
        this.mPeriodService = SpringContextUtils.getBean(NewLotteryPeriodService.class);
        this.mLotteryOpenMgr = SpringContextUtils.getBean(RocketOpenTaskManager.class);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        if(!SystemRunningMode.isBCMode())
        {
            return;
        }

        Date fireTime = context.getFireTime();
        DateTime fireDateTime = new DateTime(fireTime);

        RocketType type = RocketType.CRASH;
        String from = context.getJobDetail().getJobDataMap().getString("from");

        // 定时任务
        handleTask(type, fireDateTime, true);

        // 首次
        if(!StringUtils.isEmpty(from))
        {
            DateTime startDateTime = fireDateTime.minusMinutes(20);
            DateTime endDateTime = fireDateTime.minusSeconds(10);
            settleLatestPeriod(false, startDateTime, endDateTime, 10, true);
            startMQ();

            RocketPlayEngine.getInstance().verifyTask();
        }
    }

    private void handleTask(RocketType type, DateTime fireDateTime, boolean upLatestAssue)
    {
        try {
            GameInfo game = mGameService.findByKey(false, type.getKey());
            if(game == null)
            {
                return;
            }

            if(Status.DISABLE.getKey().equalsIgnoreCase(game.getStatus()))
            {
                return;
            }


            String issueIndexCacheKey = RocketCacheHelper.createIssueIndex(type, fireDateTime);
            int periodIssueOfToday = StringUtils.asInt(CacheManager.getInstance().getString(issueIndexCacheKey));
            if(periodIssueOfToday <= 0)
            {
                periodIssueOfToday = 1;
            }
            else
            {
                periodIssueOfToday += 1;
            }
            CacheManager.getInstance().setString(issueIndexCacheKey, periodIssueOfToday + StringUtils.getEmpty());

            // 1. 开盘
            String currentIssue = RocketHelper.generateIssue(type, fireDateTime, periodIssueOfToday);
            beginGame(type, game, currentIssue, fireDateTime);

            // 2. 更新最新期号缓存
            if(upLatestAssue)
            {
                DateTime endTime = fireDateTime.minusSeconds(10);
                DateTime starTime = endTime.minusHours(3);

                String startTimeString = starTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
                String endTimeString = endTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
                List<NewLotteryPeriodInfo> rsList = mPeriodService.queryByTime(type, startTimeString, endTimeString, 100);
                NewLotteryLatestPeriod.updateCache(type, rsList);
//                RocketLatestPeriodCache.updateCache(type, rsList);
            }

            // 3. 结算 最新未结果的订单
            DateTime startDateTime = fireDateTime.minusMinutes(2);
            DateTime endDateTime = fireDateTime.minusSeconds(type.getStepOfSeconds() + 10);
            settleLatestPeriod(false, startDateTime, endDateTime, 5, true);

        } catch (Exception exception) {
            LOG.error("handleTask error", exception);
        }

    }

    private void startMQ()
    {
        mq.subscribe(QUEUE_NAME, null, new Callback<String>() {
            @Override
            public void execute(String msg) {

//                LOG.info("receiv issue = " + msg);

                String issue = msg;

                NewLotteryPeriodInfo info = mPeriodService.findByIssue(false, RocketType.CRASH, issue);
                if(info == null)
                {
                    return;
                }

                RocketType type = RocketType.getType(info.getType());

                // 是2期之前
                long rsTime = System.currentTimeMillis() - type.getStepOfSeconds() * 2_000;
                if(rsTime - info.getStarttime().getTime() < 0)
                {
                    return;
                }

                settleOrderByIssue(true, type, issue, true);
            }
        });
    }



    public void beginGame(RocketType type,  GameInfo game, String currentIssue, DateTime fireDateTime)
    {
        // 当前期号
        NewLotteryPeriodInfo model = mPeriodService.findByIssue(true, RocketType.CRASH, currentIssue);
        if(model == null)
        {
            // 期号生成异常重新生成
            doGeneratePeriod(fireDateTime, currentIssue, type);
            model = mPeriodService.findByIssue(true, RocketType.CRASH, currentIssue);
        }
        else
        {
            // 已经存在
            return;
        }

        if(model == null)
        {
            LOG.error("generate issue error ...");
            return;
        }

        GamePeriodStatus status = GamePeriodStatus.getType(model.getStatus());
        if(status == GamePeriodStatus.PENDING)
        {
            mPeriodService.updateStatusToWaiting(model, currentIssue);
        }

        // 初始化状态
        RocketPeriodStatus periodStatus = RocketPeriodStatus.loadCache(true, type, currentIssue);
        if(!periodStatus.isInit())
        {
            String maxMoneyOfIssueValue = mConfigService.getValueByKey(false, RocketConfig.GAME_ROCKET_MAX_MONEY_OF_ISSUE);
            String maxMoneyOfUserValue = mConfigService.getValueByKey(false, RocketConfig.GAME_ROCKET_MAX_MONEY_OF_USER);
            periodStatus.setStartTime(model.getStarttime());
            periodStatus.setEndTime(model.getEndtime());
            periodStatus.setMaxMoneyOfUser(StringUtils.asFloat(maxMoneyOfUserValue));
            periodStatus.setMaxMoneyOfIssue(StringUtils.asFloat(maxMoneyOfIssueValue));

            RocketPlayEngine.getInstance().setIssue(currentIssue);
            // begin engine
            if(StringUtils.isEmpty(model.getOpenResult()))
            {
                // 如果设置为比例
                float platformRate = mConfigService.getFloat(false, RocketConfig.GAME_ROCKET_OPEN_RATE);
                float maxWinAmountMuitiple = mConfigService.getFloat(false, RocketConfig.GAME_ROCKET_MAX_WIN_AMOUNT_MUITIPLE_2_BET_AMOUNT);
                String maxCrashResult = mConfigService.getValueByKey(false, RocketConfig.GAME_ROCKET_MAX_CRASH_VALUE);
                RocketPlayEngine.getInstance().initParameter(platformRate, maxCrashResult, maxWinAmountMuitiple);
                RocketPlayEngine.getInstance().start();
            }
            else
            {
                RocketPlayEngine.getInstance().endWithOpenResult(model.getOpenResult());
            }

            periodStatus.saveCache();
        }

        // 更新当前运行状态
        NewLotteryRunningStatus runningStatus = NewLotteryRunningStatus.loadCache(type);
        runningStatus.setCurrentIssue(model.getIssue());
        runningStatus.setStatus(Status.getType(game.getStatus()));
        runningStatus.saveCache();

//        LOG.info("start open period = " + issue + ", firetime = " + fireDateTime);
    }

    public void doGeneratePeriod(DateTime fireTime, String issue, RocketType type)
    {
        try {
            GameInfo game = mGameService.findByKey(false, type.getKey());
            DateTime nowTime = fireTime;

            String openModeString = mConfigService.getValueByKey(false, TurntableConfig.GAME_TURNTABLE_OPEN_MODE);
            GameOpenMode openMode = GameOpenMode.getType(openModeString);
            DateTime startTime = nowTime;
            // 整期
            DateTime endTime = nowTime.plusSeconds(type.getStepOfSeconds() - 1);
            mPeriodService.add(null, type, issue, game.getId(), openMode, startTime.toDate(), endTime.toDate());
        }
        catch (org.springframework.dao.DuplicateKeyException e)
        {
            LOG.error("doGeneratePeriod error:", e);
        }
        catch (Exception e) {
            // 期号可以创建错误- 重复id
            LOG.error("doGeneratePeriod error:", e);
        }
    }


//    public void settleOrderByTime(RocketType type, DateTime dateTime)
//    {
//        String issue = RocketHelper.generateIssue(type, dateTime);
//        settleOrderByIssue(type, issue);
//    }

    public void settleLatestPeriod(boolean forceSettleOrderByMQ, DateTime startDataTime, DateTime endDateTime, int limit, boolean settleOrder)
    {
        String startTimeStr = startDataTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String endTimeStr = endDateTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        List<NewLotteryPeriodInfo> rsList = mPeriodService.queryByTime(RocketType.CRASH, startTimeStr, endTimeStr, limit);

        if(CollectionUtils.isEmpty(rsList))
        {
            return;
        }

        for(NewLotteryPeriodInfo periodInfo: rsList)
        {
            settleOrderByIssue(forceSettleOrderByMQ, RocketType.CRASH, periodInfo.getIssue(), settleOrder);
        }
    }

    public void settleOrderByIssue(boolean forceSettleOrderByMQ, RocketType type, String issue, boolean settleOrder)
    {
        NewLotteryPeriodInfo model = null;
        String openResult = null;
        GamePeriodStatus gameStatus = null;
        // 防止并发
        synchronized (type.getKey())
        {
            model = mPeriodService.findByIssue(true, type, issue);
            if(model == null)
            {
                return;
            }

            gameStatus = GamePeriodStatus.getType(model.getStatus());

            // 1. 获取开奖结果
            openResult = model.getOpenResult();
            if(StringUtils.isEmpty(openResult))
            {
                openResult = RocketOpenResultHelp.getOpenResult(model.getIssue());
                if(!StringUtils.isEmpty(openResult))
                {
                    mPeriodService.updateStatusToFinish(model, openResult, null);
                }
            }

            if(StringUtils.isEmpty(openResult))
            {
                float value = RocketOpenResultHelp.getRandomOpenResult(1.0f, 1.2f);
                openResult = value + StringUtils.getEmpty();
                mPeriodService.updateStatusToFinish(model, openResult, null);
            }

        }

        if(StringUtils.isEmpty(openResult))
        {
            return;
        }

        GameResultManager.getInstance().saveResult(issue, openResult, type, null, null);
        if(!settleOrder)
        {
            return;
        }

        if(forceSettleOrderByMQ || gameStatus == GamePeriodStatus.WAITING)
        {
            // 4. 更新订单信息
            mLotteryOpenMgr.handleOpenResultForAllOrder(model.getIssue(), openResult);
        }
    }

    public static void sendMessage(String issue)
    {
        mq.sendMessage(QUEUE_NAME, issue);
    }


    public void test()
    {
        TurnTableType type = TurnTableType.ROULETTE;
        String time = "2022-10-16 10:50:00";

        Date fireTime = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, time);
//        if(!doCheckGenerate(TurnTableType.P_2, fireTime, true))
//        {
//            doGeneratePeriod(fireTime, TurnTableType.P_2, true);
//        }
//        LotteryPeriodInfo periodInfo = mPeriodService.findCurrentRunning(LotteryType.BECONE);
        handleTask(RocketType.CRASH, null, true);


//        DateTime endTime = new DateTime(fireTime).minusSeconds(10);
//        DateTime starTime = endTime.minusMinutes(120);
//
//        String startTimeString = starTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
//        String endTimeString = endTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
//        List<TurntablePeriodInfo> rsList = mPeriodService.queryByTime(type, startTimeString, endTimeString, 10);
//        System.out.println(FastJsonHelper.jsonEncode(rsList));
    }

    public static void main(String[] args) {
        RocketBeginJob job = new RocketBeginJob();
        job.test();
    }

}
