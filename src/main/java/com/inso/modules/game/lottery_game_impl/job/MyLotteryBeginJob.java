package com.inso.modules.game.lottery_game_impl.job;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.mq.MQManager;
import com.inso.framework.mq.MQSupport;
import com.inso.framework.quartz.QuartzManager;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.Status;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.lottery_game_impl.GameResultManager;
import com.inso.modules.game.MyLotteryBetRecordCache;
import com.inso.modules.game.lottery_game_impl.*;
import com.inso.modules.game.lottery_game_impl.btc_kline.model.BTCKlineType;
import com.inso.modules.game.lottery_game_impl.football.model.FootballType;
import com.inso.modules.game.lottery_game_impl.helper.NewLotteryLatestPeriod;
import com.inso.modules.game.lottery_game_impl.mines.model.MineType;
import com.inso.modules.game.model.GameInfo;
import com.inso.modules.game.model.GameOpenMode;
import com.inso.modules.game.model.GamePeriodStatus;
import com.inso.modules.game.model.NewLotteryPeriodInfo;
import com.inso.modules.game.service.GameService;
import com.inso.modules.game.service.NewLotteryPeriodService;
import com.inso.modules.web.SystemRunningMode;
import com.inso.modules.web.logical.SystemStatusManager;
import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;
import java.util.List;

public class MyLotteryBeginJob implements Job {

    private static Log LOG = LogFactory.getLog(MyLotteryBeginJob.class);


    private static final String QUEUE_NAME = MyLotteryBeginJob.class.getName();

    public static final String OPT_TYPE_KEY = "optType";
    public static final String OPT_TYPE_VALUE_BEGIN = "begin";

    public static final String OPT_TYPE_VALUE_END = "end";
    public static final String OPT_TYPE_VALUE_END_CUSTOM = "endCustom";
    public static final String OPT_TYPE_VALUE_VERIFY = "verify";

    private GameService mGameService;
    private NewLotteryPeriodService mPeriodService;

    private static boolean isRunning = false;

    private static MQSupport mq = MQManager.getInstance().getMQ(MQManager.MQType.REDIS);

    public MyLotteryBeginJob()
    {
        this.mGameService = SpringContextUtils.getBean(GameService.class);
        this.mPeriodService = SpringContextUtils.getBean(NewLotteryPeriodService.class);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        if(!SystemRunningMode.isBCMode())
        {
            if(!SystemRunningMode.isCryptoMode())
            {
                return;
            }

        }



        if(!SystemStatusManager.getInstance().isRunning())
        {

            LOG.warn("System will join maintanence ...");
            return;
        }


        DateTime fireTime = new DateTime(context.getFireTime());

        String optType = context.getJobDetail().getJobDataMap().getString(OPT_TYPE_KEY);
        GameChildType type = GameChildType.getType(context.getJobDetail().getJobDataMap().getString(MyLotteryBetRecordCache.KEY_LOTTERY_TYPE));

        try {
            if(OPT_TYPE_VALUE_BEGIN.equalsIgnoreCase(optType))
            {
                if(type != null && !type.autoBoot())
                {

                    return;
                }
                // 定时任务
                if(type != null)
                {
                    if(type instanceof BTCKlineType)
                    {
                        // 提前开盘
                        fireTime = fireTime.plusSeconds(1);
                    }
                    handleBeginTask(type, fireTime, false);
                }
                // 定时任务

                return;
            }
            else if(OPT_TYPE_VALUE_END.equalsIgnoreCase(optType))
            {
                String issue = context.getJobDetail().getJobDataMap().getString(MyLotteryBetRecordCache.KEY_ISSUE);
                handleEndTask(type, issue, true);
                return;
            }
            else if(OPT_TYPE_VALUE_END_CUSTOM.equalsIgnoreCase(optType))
            {
                MyLotteryManager.getInstance().getOpenProcessor(FootballType.Football).onBeginGameByCustom_V2(fireTime);
                MyLotteryManager.getInstance().getOpenProcessor(MineType.Mines).onBeginGameByCustom_V2(fireTime);
                return;
            }
        } catch (Exception e) {
            LOG.error("handle error:", e);
        }


        if(isRunning)
        {
            return;
        }
        isRunning = true;
        // 首次执行才走下面
        BaseLotterySupport processor = MyLotteryManager.getInstance().getOpenProcessor(type);
        GameChildType[] allType = processor.getAllGameTypes();

        // begin game
        for(GameChildType tmp : allType)
        {
            if(!tmp.autoBoot())
            {
                continue;
            }
            NewLotteryPeriodInfo periodInfo = mPeriodService.findCurrentRunning(tmp);
            if(periodInfo == null)
            {
                continue;
            }

            DateTime dateTime = new DateTime(periodInfo.getStarttime());

            long ts = System.currentTimeMillis();
            if(periodInfo.getEndtime().getTime() - ts >= 10_000)
            {
                handleBeginTask(tmp, dateTime, true);
            }
        }

        for(GameChildType tmp : allType)
        {
            NewLotteryPeriodInfo periodInfo = mPeriodService.findCurrentRunning(tmp);
            if(periodInfo == null)
            {
                continue;
            }
            DateTime fireDateTime = new DateTime(periodInfo.getStarttime());
            for(int i = 2; i < 10; i ++)
            {
                finishGameByTime(tmp, fireDateTime.minusSeconds(tmp.getTotalSeconds() * i), true);
            }
        }

        // boot mq
        startMQ();


    }

    private void handleBeginTask(GameChildType type, DateTime fireTime, boolean verifyTask)
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

            // 1. check period and generate
//            if(!verifyTask && !doCheckGenerate(type, fireTime, true))
//            {
//                doGeneratePeriod(fireTime, type, true);
//            }

            // 2. 开盘
            beginGame(type, game, fireTime);

            // 3.
            if(!verifyTask && type.autoCreateIssue())
            {
                // 5. tomorrow time
                DateTime tomorrowDateTime = fireTime.plusDays(1);
                tomorrowDateTime = tomorrowDateTime.withSecondOfMinute(0);
                tomorrowDateTime = tomorrowDateTime.withMinuteOfHour(0);
                tomorrowDateTime = tomorrowDateTime.withHourOfDay(0);

                if(!doCheckGenerate(type, tomorrowDateTime, false))
                {
                    doGeneratePeriod(tomorrowDateTime, type, false);
                }
            }
        } catch (Exception exception) {
            LOG.error("handleTask error", exception);
        }

    }

    private void handleEndTask(GameChildType type, String issue, boolean updateLatestIssue)
    {
        try {
            LOG.info("handleEndTask: " + issue);

            // 3. 上期开奖
            NewLotteryPeriodInfo periodInfo = finishGameByIssue(type, issue, false);
            if(periodInfo == null)
            {
                LOG.error("handleEndTask error: " + issue);
                return;
            }

            if(updateLatestIssue)
            {
                // 4. 更新最新期号缓存
                DateTime endTime = new DateTime(periodInfo.getStarttime().getTime() + 1000);
                DateTime starTime = endTime.minusSeconds(101 * type.getTotalSeconds());
                String startTimeString = starTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
                String endTimeString = endTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
                List<NewLotteryPeriodInfo> rsList = mPeriodService.queryByTime(type, startTimeString, endTimeString, 100);
                NewLotteryLatestPeriod.updateCache(type, rsList);


                boolean update = false;
                int count = 0;
                for(NewLotteryPeriodInfo model : rsList)
                {
                    if(count >= 20)
                    {
                        return;
                    }
                    if(!StringUtils.isEmpty(model.getOpenResult()))
                    {
                        continue;
                    }
                    finishGameByIssue(type, model.getIssue(), true);
                    count ++;
                    update = true;
                }

                if(update)
                {
                    rsList = mPeriodService.queryByTime(type, startTimeString, endTimeString, 100);
                    NewLotteryLatestPeriod.updateCache(type, rsList);
                }

            }
        } catch (Exception exception) {
            LOG.error("handleTask error", exception);
        }

    }


    public void beginGame(GameChildType type, GameInfo game, DateTime fireDateTime)
    {
//        LOG.info("date time = " + fireDateTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS) + ", game type " + type.getKey());
        BaseLotterySupport processor = MyLotteryManager.getInstance().getOpenProcessor(type);

        // 当前期号
        String issue = processor.createIssue(type, fireDateTime, true);
        NewLotteryPeriodInfo model = mPeriodService.findByIssue(true, type, issue);
        if(model == null)
        {
            // 期号生成异常重新生成
            doGeneratePeriod(fireDateTime, type, true);
            model = mPeriodService.findByIssue(true, type, issue);
        }

        if(model == null)
        {
            LOG.error("create period error:");
            return;
        }

        GamePeriodStatus status = GamePeriodStatus.getType(model.getStatus());
        if(status == GamePeriodStatus.PENDING)
        {
            mPeriodService.updateStatusToWaiting(model, issue);
        }

        Status gameStatus = Status.getType(game.getStatus());
        // 初始化状态
        NewLotteryPeriodStatus periodStatus = NewLotteryPeriodStatus.loadCache(true, type, issue);
        if(!periodStatus.isInit())
        {
            if(gameStatus == Status.ENABLE)
            {
                long endTs = model.getEndtime().getTime() - type.getDisableMilliSeconds() - 3000;
                MyLotteryRobotManager.sendMessage(type, issue, endTs);
            }
        }
        processor.onGameStart(model, periodStatus);

        // 更新当前运行状态
        NewLotteryRunningStatus runningStatus = NewLotteryRunningStatus.loadCache(type);
        runningStatus.setCurrentIssue(model.getIssue());
        runningStatus.setStatus(gameStatus);
        runningStatus.saveCache();

//        LOG.info("start open period = " + issue + ", firetime = " + fireDateTime);
    }



    public void doGeneratePeriod(DateTime fireTime, GameChildType type, boolean isOnly100)
    {

        try {
            //LOG.info("doGeneratePeriod = " + fireTime + " - type " + type.getKey() + " - " + fireTime + " isOnly100 = " + isOnly100);
            GameInfo game = mGameService.findByKey(false, type.getKey());
            DateTime nowTime = fireTime.withSecondOfMinute(0);
            int todayOfWeek = nowTime.getDayOfWeek();
//
            BaseLotterySupport processor = MyLotteryManager.getInstance().getOpenProcessor(type);
            GameOpenMode openMode = processor.getOpenMode(type);

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

                DateTime startTime = nowTime.plusSeconds(type.getTotalSeconds() * i);
                // 整期
                DateTime endTime = nowTime.plusSeconds(type.getTotalSeconds() * (i + 1)).minusSeconds(1);

                if(endTime.getDayOfWeek() != todayOfWeek)
                {
                    stop = true;
                    return;
                }

    //            String timeString = "time = " + DateUtils.convertString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, startTime);
    //            timeString += " - " + DateUtils.convertString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, endTime);
    //            System.out.println(timeString);

                String issue = processor.createIssue(type, startTime, true);
                String showIssue = processor.createIssue(type, startTime, false);
//                System.out.println(issue);
                if(mPeriodService != null)
                {
                    mPeriodService.add(showIssue, type, issue, game.getId(), openMode, startTime.toDate(), endTime.toDate());
                }



                //
                i += 1;

                index ++;
            }
        } catch (Exception e) {
            // 期号可以创建错误- 重复id
            LOG.error("doGeneratePeriod error:", e);
        }
    }

    private boolean doCheckGenerate(GameChildType type, DateTime fireTime, boolean isOnly100)
    {
        if(mPeriodService == null)
        {
            return false;
        }
        String startTimeString = DateUtils.convertString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, fireTime);

        String dayString = DateUtils.convertString(DateUtils.TYPE_YYYY_MM_DD, fireTime);
        String endTimeString = DateUtils.getEndTimeOfDay(dayString);

//        Date endTime = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, endTimeString);

        long count = mPeriodService.count(type, startTimeString, endTimeString);
//        LOG.info("doCheckGenerate = " + type.getKey() + " - " + startTimeString + ", endString = " + endTimeString + " - count = " + count + " - isOnly100 + " + isOnly100);
        return count > 0;
    }


    private void startMQ()
    {

        mq.subscribe(QUEUE_NAME, null, new Callback<String>() {
            @Override
            public void execute(String jsonString) {

//                LOG.info("receiv issue = " + msg);


                JSONObject jsonObject = FastJsonHelper.toJSONObject(jsonString);
                if(jsonObject == null || jsonObject.isEmpty())
                {
                    return;
                }

                String issue = jsonObject.getString(MyLotteryBetRecordCache.KEY_ISSUE);
                GameChildType childType = GameChildType.getType(jsonObject.getString(MyLotteryBetRecordCache.KEY_LOTTERY_TYPE));

                if(childType == null || StringUtils.isEmpty(issue))
                {
                    return;
                }


                NewLotteryPeriodInfo info = mPeriodService.findByIssue(false, childType, issue);
                if(info == null)
                {
                    return;
                }

                GameChildType type = GameChildType.getType(info.getType());

                // 是2期之前
                long time = System.currentTimeMillis() - type.getTotalSeconds() * 2 * 3 * 1000;
                if(time - info.getStarttime().getTime() < 0)
                {
                    return;
                }
                finishGameByIssue(type, issue, false);

            }
        });
    }

    public void finishGameByTime(GameChildType type, DateTime dateTime, boolean stopIfFinishStatus)
    {

        BaseLotterySupport openProcessor = MyLotteryManager.getInstance().getOpenProcessor(type);
        String issue = openProcessor.createIssue(type, dateTime, true);
        finishGameByIssue(type, issue, stopIfFinishStatus);
    }

    public NewLotteryPeriodInfo finishGameByIssue(GameChildType type, String issue, boolean stopIfFinishStatus)
    {


        // 防止并发
        synchronized (type.getKey())
        {
            NewLotteryPeriodInfo model = mPeriodService.findByIssue(true, type, issue);
            if(model == null)
            {
                LOG.error("findByIssue not exist, error: " + issue);
                return null;
            }

            BaseLotterySupport openProcessor = MyLotteryManager.getInstance().getOpenProcessor(type);
            // 1. 获取开奖结果
            String reference = model.getReferenceExternal();
            String openResult = model.getOpenResult();
            long openIndex = model.getOpenIndex();
//            TurntableBetItemType betItemType = TurntableBetItemType.getType(openResult);
            if(StringUtils.isEmpty(openResult))
            {
                reference = openProcessor.getReference(model, type);
                if(!StringUtils.isEmpty(reference))
                {
                    openResult = openProcessor.getOpenResult(reference);
                }
                if(!StringUtils.isEmpty(model.getTmpRealReference()))
                {
                    JSONObject remarkJson = model.getRemarkJson();
                    remarkJson.put("realReference", model.getTmpRealReference());
                    mPeriodService.updateReference(model, null, remarkJson);
                }
            }

            if(StringUtils.isEmpty(openResult))
            {
                LOG.error("fetch open result error: " + issue + ", reference = " + reference);
                return model;
            }
            // 2. 更新数据库开奖表
            GamePeriodStatus periodStatus = GamePeriodStatus.getType(model.getStatus());
            if( periodStatus != GamePeriodStatus.FINISH)
            {
                mPeriodService.updateStatusToFinish(model, openResult, reference);
//                MyLotteryOpenResultHelper.saveOpenResult(type, issue, openResult);
                GameResultManager.getInstance().saveResult(issue, openResult, type, reference, null);

                if(!StringUtils.isEmpty(openResult))
                {
                    openIndex = openProcessor.getOpenIndex(openResult);
                }
            }
            else
            {
                if(stopIfFinishStatus)
                {
                    return model;
                }
            }

            LOG.info("doFinish game success, issue = " + issue  + ", openResult = " + openResult + ", reference = " + reference);
            LOG.info("get open result from cache, issue = " + issue + ", openResult = " + GameResultManager.getInstance().getStringResult(type, issue));

            // 3. 更新缓存
            NewLotteryPeriodStatus status = NewLotteryPeriodStatus.tryLoadCache(true, type, issue);
            if(status != null)
            {
                //
                status.setOpenResult(openResult);
                if(openIndex >= 0)
                {
                    status.setOpenIndex((int)openIndex);
                }
                status.saveCache();
            }

            // 4. 更新订单信息
            openProcessor.handleOpenResultForAllOrder(type, model.getIssue(), openResult);
            return model;
        }
    }

    public static void sendEndTaskMessage(GameChildType gameChildType, String issue)
    {
        if(StringUtils.isEmpty(issue))
        {
            return;
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(MyLotteryBetRecordCache.KEY_LOTTERY_TYPE, gameChildType.getKey());
        jsonObject.put(MyLotteryBetRecordCache.KEY_ISSUE, issue);
        mq.sendMessage(QUEUE_NAME, jsonObject.toJSONString());
    }

    public static void bootBeginJob(GameChildType type, String cron)
    {

        QuartzManager quartz = QuartzManager.getInstance();
        JobDataMap beginDataMap = new JobDataMap();
        beginDataMap.put(MyLotteryBetRecordCache.KEY_LOTTERY_TYPE, type.getKey());
        beginDataMap.put(MyLotteryBeginJob.OPT_TYPE_KEY, MyLotteryBeginJob.OPT_TYPE_VALUE_BEGIN);
        quartz.submitCronJob(MyLotteryBeginJob.class, beginDataMap, cron, null, null);
    }


    public static void bootEndTaskJob(GameChildType gameChildType, String issue, Date date)
    {
        bootEndTaskJob(gameChildType.getKey(), issue, date);
    }
    public static void bootEndTaskJob(String lotteryType, String issue, Date date)
    {
        if(StringUtils.isEmpty(issue))
        {
            return;
        }

        JobDataMap endDataMap = new JobDataMap();
        endDataMap.put(MyLotteryBetRecordCache.KEY_LOTTERY_TYPE, lotteryType);
        endDataMap.put(MyLotteryBeginJob.OPT_TYPE_KEY, MyLotteryBeginJob.OPT_TYPE_VALUE_END);
        endDataMap.put(MyLotteryBetRecordCache.KEY_ISSUE, issue);
        QuartzManager.getInstance().submitDateJob(MyLotteryBeginJob.class, endDataMap, date, null, null);

        LOG.info("boot end task job: " + date + ", type = " + lotteryType);
    }

    public void test()
    {
        GameChildType type = BTCKlineType.BTC_KLINE_1MIN;
        String time = "2023-04-05 21:58:00";


        DateTime fireTime = new DateTime(DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, time));

        BaseLotterySupport processor = MyLotteryManager.getInstance().getOpenProcessor(type);



//        doGeneratePeriod(fireTime, type, true);

//        String issue = processor.createIssue(type, fireTime);

        handleBeginTask(type, fireTime, false);

//        handleEndTask(type, issue, true);
//        if(!doCheckGenerate(GameChildType.T_1, fireTime, true))
//        {
//            doGeneratePeriod(fireTime, GameChildType.T_1, true);
//        }
//        doGeneratePeriod(fireTime, GameChildType.ROULETTE, true);
//        LotteryPeriodInfo periodInfo = mPeriodService.findCurrentRunning(LotteryType.BECONE);
//        handleTask(GameChildType.T_1, fireTime);


//        DateTime endTime = new DateTime(fireTime).minusSeconds(10);
//        DateTime starTime = endTime.minusMinutes(120);
//
//        String startTimeString = starTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
//        String endTimeString = endTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
//        List<TurntablePeriodInfo> rsList = mPeriodService.queryByTime(type, startTimeString, endTimeString, 10);
//        System.out.println(FastJsonHelper.jsonEncode(rsList));
    }

    public static void main(String[] args) {
        MyLotteryBeginJob job = new MyLotteryBeginJob();
        job.test();
    }

}
