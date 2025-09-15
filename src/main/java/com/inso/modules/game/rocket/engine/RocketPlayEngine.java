package com.inso.modules.game.rocket.engine;

import com.google.common.collect.Lists;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.quartz.QuartzManager;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.RandomUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.lottery_game_impl.GameResultManager;
import com.inso.modules.game.rocket.engine.impl.*;
import com.inso.modules.game.rocket.helper.RocketOpenResultHelp;
import com.inso.modules.game.rocket.job.RocketBeginJob;
import com.inso.modules.game.rocket.logical.RocketOpenTaskManager;
import com.inso.modules.game.rocket.logical.RocketPeriodStatus;
import com.inso.modules.game.rocket.logical.RocketRobotManager;
import com.inso.modules.game.rocket.model.RocketGameStatusInfo;
import com.inso.modules.game.rocket.model.RocketType;
import com.inso.modules.web.logical.SystemStatusManager;
import com.inso.modules.websocket.WssDispatchManager;
import com.inso.modules.websocket.impl.RocketMessageImpl;
import com.inso.modules.websocket.model.MyGroupType;
import org.joda.time.DateTime;
import org.quartz.JobDataMap;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class RocketPlayEngine implements Runnable{

    private static Log LOG = LogFactory.getLog(RocketPlayEngine.class);

    private static final JobDataMap mJobDataMap = new JobDataMap();

    private static ScheduledExecutorService mPool = Executors.newScheduledThreadPool(5);
    private ScheduledFuture mOpenResultScheduleFuture;


    private long openPeriodOfSeconds = 2_000;

    private RocketType mRocketType = RocketType.CRASH;
    private String mIssue;

    private float mOpenResult;

    private RocketOpenTaskManager mRocketOpenTaskManager;

    private long mLatestExecTime = -1;
    private boolean debug = true;
    private boolean ignoreNextIssue = false;

    private boolean isFirstCheckOpen = false;
    private long mFirstOpenTime;

    private long mLastBetNotifyTs = -1;

    private RocketGameStatusInfo mRocketGameStatusInfo = new RocketGameStatusInfo();


    private ScheduledFuture mRobotScheduledFuture;
    private int mRobotBetCountdown;
    private Runnable mRobotBetTask;

    private List<CrashSupport> mCrashProessorList = Lists.newArrayList();
    private CountCrashImpl mCountCrashImpl = new CountCrashImpl();
    private ProfitCrashImpl mBetCrashImpl;
    private CrashSupport mZeroCrashImpl;
    private MaxWinCrashImpl maxWinCrash = new MaxWinCrashImpl();

    private boolean isStopTask = false;
    private boolean isPresetStop = false;

    private CrashRunningStatus mCrashRunningStatus = CrashRunningStatus.Bet;

    private interface MyInternal {
        public RocketPlayEngine mgr = new RocketPlayEngine();
    }

    private RocketPlayEngine()
    {
        this.mRocketOpenTaskManager = SpringContextUtils.getBean(RocketOpenTaskManager.class);

        this.mZeroCrashImpl = new ZeroCrashImpl();
        this.mBetCrashImpl = new ProfitCrashImpl();
        this.mCrashProessorList.add(mBetCrashImpl);
        this.mCrashProessorList.add(maxWinCrash);

        this.mCrashProessorList.add(new SafeCrashImpl(new RandomCrashImpl()));

        if(!MyEnvironment.isDev())
        {
            this.debug = false;
        }

        this.mRobotBetTask = new Runnable() {
            @Override
            public void run() {

                onBetProcessor();

            }
        };
    }

    public static RocketPlayEngine getInstance()
    {
        return MyInternal.mgr;
    }

    public void verifyTask()
    {
        long maxExpires = 120_000;

        long delay = 120;
        mPool.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                long ts = System.currentTimeMillis();
                if(mLatestExecTime == -1 || ts - mLatestExecTime > maxExpires)
                {
                    LOG.debug("Verify rocket program not running and will restart ...");
                    beginNextIssue();
                }
            }
        }, delay, delay, TimeUnit.SECONDS);
    }

    public void setIssue(String issue)
    {
        this.mIssue = issue;
    }

    @Override
    public void run() {
        onOpenProcessor();
    }

    public void initParameter(float platformProfitRate, String maxCrashResult, float maxWinAmountMuitiple)
    {
        ZeroCrashImpl zeroCrash = (ZeroCrashImpl)mZeroCrashImpl;
        float maxCrashValue = zeroCrash.handleAndFetchMaxCrashValueByConfig(maxCrashResult);
        this.mBetCrashImpl.setmPlatformProfitRate(platformProfitRate);
        this.mBetCrashImpl.setMaxCrashValue(maxCrashValue);
    }

    public void start()
    {
        if(!SystemStatusManager.getInstance().isRunning())
        {
            stop();
            return;
        }

        this.isStopTask = false;
        this.isPresetStop = false;
        this.mLatestExecTime = System.currentTimeMillis();

        //
        this.mRocketGameStatusInfo.clear();
        RocketRobotManager.getInstance().clear();

        int betInitDelay = 3;
        this.mRobotBetCountdown = mRocketType.getStepOfSeconds() - mRocketType.getDisableSecond() - 3 - betInitDelay;
        this.mRobotScheduledFuture = mPool.scheduleAtFixedRate(mRobotBetTask, betInitDelay, 1, TimeUnit.SECONDS);

        if(debug)
        {
            LOG.info("Start: issue = " + mIssue + ", and platformProfitRate = ");
        }

        // 初始化
        RocketPeriodStatus status = RocketPeriodStatus.tryLoadCache(false, mRocketType, mIssue);
        if(this.mZeroCrashImpl.verify(0, status))
        {
            this.mOpenResult = 0;
        }
        else
        {
            this.mOpenResult = 0.95f;
        }

        long initDelay = mRocketType.getStepOfSeconds() * 1000 - 250;
        if(ignoreNextIssue)
        {
            initDelay = 3;
        }
        this.isFirstCheckOpen = true;
        LOG.info("begin time = " + DateTime.now().toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS));
        this.mFirstOpenTime = System.currentTimeMillis() + initDelay - 500;
        this.mOpenResultScheduleFuture = mPool.scheduleAtFixedRate(this, initDelay, openPeriodOfSeconds, TimeUnit.MILLISECONDS);
    }

    public boolean safeStop()
    {
        if(this.mCrashRunningStatus == CrashRunningStatus.Bet || this.mCrashRunningStatus == CrashRunningStatus.Cashout)
        {
            this.isPresetStop = true;
//            LOG.info("set isPresetStop = true");
            return true;
        }
        return false;
    }

    public void stop()
    {
        this.mCrashRunningStatus = CrashRunningStatus.End;
        this.isStopTask = true;
        if(this.mOpenResultScheduleFuture != null)
        {
            this.mOpenResultScheduleFuture.cancel(false);
        }

        if(mOpenResult > 0 && mOpenResult < 1)
        {
            mOpenResult = 1.0f;
        }

        String stringOpenResult = mOpenResult + StringUtils.getEmpty();
        RocketOpenResultHelp.saveOpenResult(this.mIssue, stringOpenResult);

        //
        RocketPeriodStatus status = RocketPeriodStatus.tryLoadCache(false, mRocketType, mIssue);
        status.setOpenResult(stringOpenResult);
        status.saveCache();
        updateGameStatusInfo("stop game", status, true, true, true);
        GameResultManager.getInstance().saveResult(mIssue, stringOpenResult, mRocketType, null, null);

        // update result
        if(this.mRocketOpenTaskManager != null)
        {
            this.mRocketOpenTaskManager.updateDBOpenResult(mIssue, stringOpenResult);
        }

        // next issue
        beginNextIssue();

        // settle order
        if(this.mRocketOpenTaskManager != null)
        {
            this.mRocketOpenTaskManager.handleOpenResultForAllOrder(mIssue, stringOpenResult);
        }

        //LOG.info("End: issue = " + mIssue + ", and open result = " + mOpenResult + ", totalBetCount = " + status.getmRobotBetCount());
    }

    private void onBetProcessor()
    {
        //LOG.info("bet time = " + DateTime.now().toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS));
        if(isStopTask)
        {
            return;
        }

        RocketPeriodStatus periodStatus = RocketPeriodStatus.tryLoadCache(false, mRocketType, mIssue);
        if(periodStatus == null)
        {
            return;
        }

        mRobotBetCountdown --;
        if(mRobotBetCountdown <= 0)
        {
            mCountCrashImpl.initGenerateResultCount(periodStatus);
            mRobotScheduledFuture.cancel(false);
            return;
        }

        this.mCrashRunningStatus = CrashRunningStatus.Bet;

        boolean needNotifyStatus = false;
        long ts = System.currentTimeMillis();
        if(mLastBetNotifyTs == -1 || ts - mLastBetNotifyTs > 3_000)
        {
            this.mLastBetNotifyTs = ts;
            needNotifyStatus = true;
        }

        try {

            int betCount = RandomUtils.nextInt(100) + 1;
            for(int i = 0; i < betCount; i ++)
            {
                RocketRobotManager.getInstance().doBet(mIssue, periodStatus);
            }

        } finally {
            updateGameStatusInfo("bet ", periodStatus, false, false, needNotifyStatus);
        }
    }

    private void onOpenProcessor()
    {
        if(isFirstCheckOpen)
        {
            if(System.currentTimeMillis() - mFirstOpenTime < 0)
            {
                return;
            }
//            FastJsonHelper.prettyJson(RocketBetRecordCache.getInstance().getAllRecordListFromCache(false, mRocketType, mIssue));
//            LOG.info("open time = " + DateTime.now().toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS));
        }

        if(!SystemStatusManager.getInstance().isRunning())
        {
            stop();
            return;
        }

        if(this.isStopTask)
        {
            return;
        }

        RocketPeriodStatus periodStatus = RocketPeriodStatus.tryLoadCache(false, mRocketType, mIssue);
        if(periodStatus == null)
        {
            return;
        }

        // 预设结束
        if(this.isPresetStop || mOpenResult <= 0)
        {
            stop();
            return;
        }

        this.mCrashRunningStatus = CrashRunningStatus.Cashout;

        //
        float nextMaxResult = RocketOpenResultHelp.getMaxFloat(this.mOpenResult);
        float nextOpenResult = RocketOpenResultHelp.getRandomOpenResult(this.mOpenResult, nextMaxResult, isFirstCheckOpen);

        if(nextOpenResult > 0 && nextOpenResult < 1)
        {
            nextOpenResult = 1.0f;
        }

        if(!isFirstCheckOpen)
        {
            if(mCountCrashImpl.verify(nextOpenResult, periodStatus))
            {
                stop();
                return;
            }
            for(CrashSupport processor : mCrashProessorList)
            {
                if(!processor.support(periodStatus))
                {
                    continue;
                }

                if(processor.verify(nextOpenResult, periodStatus))
                {
//                    LOG.info("========== crash by " + processor.getClass().getSimpleName() + ", generateResultCount = ");
                    this.stop();
                    return;
                }
                break;
            }
        }

        this.mOpenResult = nextOpenResult;
        String currentStringResult = mOpenResult + StringUtils.getEmpty();
        periodStatus.setCurrentResult(currentStringResult);
        RocketOpenResultHelp.saveOpenResult(this.mIssue, currentStringResult);

        if(isFirstCheckOpen)
        {
            isFirstCheckOpen = false;
        }
        else
        {
            RocketRobotManager.getInstance().cashout(mIssue, mOpenResult, periodStatus);
        }

        updateGameStatusInfo("open", periodStatus, true, false, true);
//        if(debug)
//        {
//
//        }
    }

    public void endWithOpenResult(String openResult)
    {
        this.mLatestExecTime = System.currentTimeMillis();

        float value = StringUtils.asFloat(openResult);
        this.mOpenResult = value;
        stop();
    }

    private void beginNextIssue()
    {
        if(SystemStatusManager.getInstance().isRunning() && !ignoreNextIssue)
        {
            QuartzManager quartz = QuartzManager.getInstance();
            quartz.submitDateJob(RocketBeginJob.class, mJobDataMap, new Date(), null, null);

            if(debug)
            {
                LOG.debug("will next issue ...");
            }

        }
    }

    private void updateGameStatusInfo(String from, RocketPeriodStatus periodStatus, boolean updateCurrentResult, boolean updateOpenResult, boolean needNotifyStatus)
    {
        int totalCount = periodStatus.getmTotalBetCount() + periodStatus.getmRobotBetCount();
        int cashoutCount = periodStatus.getmCashoutCount().get();
        if(cashoutCount >= periodStatus.getmTotalBetCount())
        {
            cashoutCount = periodStatus.getmTotalBetCount();
        }
        cashoutCount += periodStatus.getmRobotCashoutCount();

        mRocketGameStatusInfo.setTotalBetCount(totalCount);
        mRocketGameStatusInfo.setCashoutCount(cashoutCount);

        String openResult = mOpenResult + StringUtils.getEmpty();
        if(updateCurrentResult)
        {
            mRocketGameStatusInfo.setCurrentResult(openResult);
        }

        if(updateOpenResult)
        {
            mRocketGameStatusInfo.setOpenResult(openResult);
        }
        mRocketGameStatusInfo.save(mIssue);

        MyGroupType groupType = MyGroupType.GAME_ROCKET;

        if(needNotifyStatus)
        {
            RocketMessageImpl support = (RocketMessageImpl) WssDispatchManager.getInstance().getSupport(groupType);
            support.notifyLotteryStatusToAll();
        }

        if(updateOpenResult)
        {
            StringBuilder log1 = new StringBuilder();
            log1.append("from bet = ").append(from);
            log1.append(", issue = ").append(periodStatus.getIssue());
            log1.append(", betCount = ").append(periodStatus.getmTotalBetCount());
            log1.append(", cashoutCount = ").append(periodStatus.getmCashoutCount());
            log1.append(", robotBetCount = ").append(periodStatus.getmRobotBetCount());
            log1.append(", robotCashout = ").append(periodStatus.getmRobotCashoutCount());
            log1.append(", openResult = ").append(openResult);

            LOG.info("updateGameStatusInfo: " + log1.toString());
        }
    }

    private void test1()
    {
        RocketPeriodStatus periodStatus = RocketPeriodStatus.loadCache(true, mRocketType, mIssue);
        periodStatus.saveCache();
    }

    public static enum CrashRunningStatus {
        Bet,
        Cashout,
        End,
    }

    public static void main(String[] args) throws IOException {
        String issue = "123";
        RocketPlayEngine mgr = new RocketPlayEngine();
        mgr.ignoreNextIssue = true;

        mgr.debug = true;
        mgr.mIssue = issue;

        mgr.setIssue( issue);
        mgr.test1();

        mgr.start();

//        mgr.verifyTask();
        System.in.read();
    }


}
