package com.inso.modules.game.lottery_game_impl;


import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.mq.MQManager;
import com.inso.framework.mq.MQSupport;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RandomStringUtils;
import com.inso.framework.utils.RandomUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.MyLotteryBetRecordCache;
import com.inso.modules.game.helper.BetAmountHelper;
import com.inso.modules.game.helper.BetFeemoneyHelper;
import com.inso.modules.game.rg.model.LotteryRGType;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MyLotteryRobotManager {

    private static Log LOG = LogFactory.getLog(MyLotteryRobotManager.class);

    private static MQSupport mq = MQManager.getInstance().getMQ(MQManager.MQType.REDIS);
    private static final String QUEUE_NAME = MyLotteryRobotManager.class.getName();

    private static final String KEY_END_TS = "endTs";

    private int stepPeriodOfSeconds = 2;

    private ScheduledExecutorService mThread = Executors.newScheduledThreadPool(1);

    private Map<GameChildType, BetTask> mBetTaskMaps = Maps.newConcurrentMap();

    private interface MyInternal {
        public MyLotteryRobotManager mgr = new MyLotteryRobotManager();
    }

    private MyLotteryRobotManager()
    {
    }

    public static MyLotteryRobotManager getInstance()
    {
        return MyInternal.mgr;
    }

    private BetTask getBetTask(GameChildType gameType)
    {
        BetTask betTask = mBetTaskMaps.get(gameType);
        if(betTask == null)
        {
            betTask = new BetTask(gameType);
            mBetTaskMaps.put(gameType, betTask);
        }
        return betTask;
    }

    public void init()
    {
        bgMQTask();
    }
    private void bgMQTask()
    {
        mq.subscribe(QUEUE_NAME, null, new Callback<String>() {
            @Override
            public void execute(String jsonString) {
                try {
                    if(StringUtils.isEmpty(jsonString))
                    {
                        return;
                    }

                    JSONObject jsonObject = FastJsonHelper.toJSONObject(jsonString);
                    if(jsonObject == null)
                    {
                        return;
                    }

                    String issue = jsonObject.getString(MyLotteryBetRecordCache.KEY_ISSUE);
                    if(StringUtils.isEmpty(issue))
                    {
                        return;
                    }
                    GameChildType type = GameChildType.getType(jsonObject.getString(MyLotteryBetRecordCache.KEY_LOTTERY_TYPE));
                    if(type == null)
                    {
                        return;
                    }

                    long endTs = jsonObject.getLong("endTs");
                    if(endTs <= 0)
                    {
                        return;
                    }

                    BetTask betTask = getBetTask(type);
                    betTask.setIssue(issue);
                    betTask.initRunning(endTs);
                    ScheduledFuture scheduledFuture = mThread.scheduleWithFixedDelay(betTask, 3, stepPeriodOfSeconds, TimeUnit.SECONDS);
                    betTask.setmRunningScheduledFuture(scheduledFuture);
                } catch (Exception e) {
                    LOG.error("handle error:", e);
                }
            }
        });
    }

    private class BetTask implements Runnable{

        private GameChildType gameType;
        private String mIssue;
        private long endTs;
        private int mRunningCount;

        private ScheduledFuture mRunningScheduledFuture;

        public BetTask(GameChildType gameType)
        {
            this.gameType = gameType;
        }

        public void initRunning(long endTs)
        {
            this.endTs = endTs;
            this.mRunningCount = RandomUtils.nextInt(50) + 1;
        }

        public void setIssue(String issue)
        {
            this.mIssue = issue;
        }

        private void addRecord(String issue)
        {
            mRunningCount --;
            if(mRunningCount <= 0)
            {
                stop();
                return;
            }

            if(System.currentTimeMillis() > endTs)
            {
                stop();
                return;
            }

            String username = "ep" + RandomStringUtils.generator0_Z(6) + "_gmail";
            long betAmount = BetAmountHelper.randomBetAmount();
            BigDecimal betTotalAmountValue = new BigDecimal(betAmount);
            BigDecimal feemoney = BetFeemoneyHelper.getSingleFeemoney(betTotalAmountValue);

            // 添加当前投注记录
            Date createtime = new Date();
            String orderno = NewLotteryBetTaskManager.nextOrderId(gameType);
            String betItemType = gameType.randomBetItem();

//            System.out.println("==========" + betItemType + " gameType = " + gameType.getKey());
            MyLotteryBetRecordCache.getInstance().addRecord(true, orderno, gameType, issue, username, null, new BigDecimal(betAmount), feemoney, betItemType, createtime);
        }

        @Override
        public void run() {
            addRecord(mIssue);
        }

        public void setmRunningScheduledFuture(ScheduledFuture mRunningScheduledFuture) {
            this.mRunningScheduledFuture = mRunningScheduledFuture;
        }

        private void stop()
        {
            this.mRunningScheduledFuture.cancel(false);
        }
    }

    public static void sendMessage(GameChildType gameType, String issue, long endTs)
    {
        if(!gameType.enableRobotBet())
        {
            return;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(MyLotteryBetRecordCache.KEY_ISSUE, issue);
        jsonObject.put(MyLotteryBetRecordCache.KEY_LOTTERY_TYPE, gameType.getKey());
        jsonObject.put(KEY_END_TS, endTs);
        mq.sendMessage(QUEUE_NAME, jsonObject.toJSONString());
    }

    public static void main(String[] args) throws IOException {
        MyLotteryRobotManager mgr = MyLotteryRobotManager.getInstance();
        mgr.init();

        String issue = "1";
        GameChildType lotteryRGType = LotteryRGType.PARITY;
        sendMessage(lotteryRGType, issue, System.currentTimeMillis() + 60_000);
        sendMessage(lotteryRGType, issue, System.currentTimeMillis() + 60_000);
        sendMessage(lotteryRGType, issue, System.currentTimeMillis() + 60_000);


        List rsList = MyLotteryBetRecordCache.getInstance().getAllRecordListFromCache(false, lotteryRGType, issue);

        FastJsonHelper.prettyJson(rsList);

        System.in.read();


//        System.out.println(TruntbBetRecordCache.getInstance().getAllRecordListFromCache(TurnTableType.T_1));
    }

}
