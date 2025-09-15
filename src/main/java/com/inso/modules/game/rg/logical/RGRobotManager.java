//package com.inso.modules.game.rg.logical;
//
//
//import com.alibaba.fastjson.JSONObject;
//import com.google.common.collect.Maps;
//import com.inso.framework.mq.MQManager;
//import com.inso.framework.mq.MQSupport;
//import com.inso.framework.service.Callback;
//import com.inso.framework.utils.*;
//import com.inso.modules.game.MyLotteryBetRecordCache;
//import com.inso.modules.game.GameChildType;
//import com.inso.modules.game.helper.BetAmountHelper;
//import com.inso.modules.game.rg.helper.LotteryHelper;
//import com.inso.modules.game.rg.model.LotteryBetItemType;
//import com.inso.modules.game.rg.model.LotteryRGType;
//import com.inso.modules.game.lottery_game_impl.turntable.TurntableHelper;
//
//import java.io.IOException;
//import java.math.BigDecimal;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.ScheduledFuture;
//import java.util.concurrent.TimeUnit;
//
//public class RGRobotManager {
//
//    private static MQSupport mq = MQManager.getInstance().getMQ(MQManager.MQType.REDIS);
//    private static final String QUEUE_NAME = RGRobotManager.class.getName();
//
//    private static final String KEY_END_TS = "endTs";
//
//    private int stepPeriodOfSeconds = 2;
//
//    private ScheduledExecutorService mThread = Executors.newScheduledThreadPool(1);
//
//    private Map<GameChildType, BetTask> mBetTaskMaps = Maps.newConcurrentMap();
//
//    private interface MyInternal {
//        public RGRobotManager mgr = new RGRobotManager();
//    }
//
//    private RGRobotManager()
//    {
//    }
//
//    public static RGRobotManager getInstance()
//    {
//        return MyInternal.mgr;
//    }
//
//    private BetTask getBetTask(LotteryRGType gameType)
//    {
//        BetTask betTask = mBetTaskMaps.get(gameType);
//        if(betTask == null)
//        {
//            betTask = new BetTask(gameType);
//            mBetTaskMaps.put(gameType, betTask);
//        }
//        return betTask;
//    }
//
//    public void init()
//    {
//        bgMQTask();
//    }
//    private void bgMQTask()
//    {
//        mq.subscribe(QUEUE_NAME, null, new Callback<String>() {
//            @Override
//            public void execute(String jsonString) {
//                try {
//                    if(StringUtils.isEmpty(jsonString))
//                    {
//                        return;
//                    }
//
//                    JSONObject jsonObject = FastJsonHelper.toJSONObject(jsonString);
//                    if(jsonObject == null)
//                    {
//                        return;
//                    }
//
//                    String issue = jsonObject.getString(MyLotteryBetRecordCache.KEY_ISSUE);
//                    if(StringUtils.isEmpty(issue))
//                    {
//                        return;
//                    }
//                    LotteryRGType type = LotteryRGType.getType(jsonObject.getString(MyLotteryBetRecordCache.KEY_LOTTERY_TYPE));
//                    if(type == null)
//                    {
//                        return;
//                    }
//
//                    long endTs = jsonObject.getLong("endTs");
//                    if(endTs <= 0)
//                    {
//                        return;
//                    }
//
//                    BetTask betTask = getBetTask(type);
//                    betTask.setIssue(issue);
//                    betTask.initRunning(endTs);
//                    ScheduledFuture scheduledFuture = mThread.scheduleWithFixedDelay(betTask, 3, stepPeriodOfSeconds, TimeUnit.SECONDS);
//                    betTask.setmRunningScheduledFuture(scheduledFuture);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//
//    public void testTimer()
//    {
//    }
//
//
//    private class BetTask implements Runnable{
//
//        private LotteryRGType gameType;
//        private String mIssue;
//        private long endTs;
//        private int mRunningCount;
//
//        private ScheduledFuture mRunningScheduledFuture;
//
//        public BetTask(LotteryRGType gameType)
//        {
//            this.gameType = gameType;
//        }
//
//        public void initRunning(long endTs)
//        {
//            this.endTs = endTs;
//            this.mRunningCount = RandomUtils.nextInt(50) + 1;
//        }
//
//        public void setIssue(String issue)
//        {
//            this.mIssue = issue;
//        }
//
//        private void addRecord(String issue)
//        {
//            mRunningCount --;
//            if(mRunningCount <= 0)
//            {
//                stop();
//                return;
//            }
//
//            if(System.currentTimeMillis() > endTs)
//            {
//                stop();
//                return;
//            }
//
//            String username = "up*****" + RandomStringUtils.generator0_9(5);
//            long betAmount = BetAmountHelper.randomBetAmount();
//            BigDecimal betTotalAmountValue = new BigDecimal(betAmount);
//            BigDecimal feemoney = TurntableHelper.getSingleFeemoney(betTotalAmountValue);
//
//            // 添加当前投注记录
//            Date createtime = new Date();
//            String orderno = LotteryHelper.nextOrderId(gameType);
//            String betItemType = LotteryBetItemType.randomItem();
//            RGBetRecordCache.getInstance().addRecord(orderno, gameType, issue, username, null, betAmount, feemoney, betItemType, createtime);
//        }
//
//        @Override
//        public void run() {
//            addRecord(mIssue);
//        }
//
//        public void setmRunningScheduledFuture(ScheduledFuture mRunningScheduledFuture) {
//            this.mRunningScheduledFuture = mRunningScheduledFuture;
//        }
//
//        private void stop()
//        {
//            this.mRunningScheduledFuture.cancel(false);
//        }
//    }
//
//    public static void sendMessage(LotteryRGType gameType, String issue, long endTs)
//    {
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put(MyLotteryBetRecordCache.KEY_ISSUE, issue);
//        jsonObject.put(MyLotteryBetRecordCache.KEY_CHILD_TYPE, gameType.getKey());
//        jsonObject.put(KEY_END_TS, endTs);
//        mq.sendMessage(QUEUE_NAME, jsonObject.toJSONString());
//    }
//
//    public static void main(String[] args) throws IOException {
//        RGRobotManager mgr = RGRobotManager.getInstance();
//        mgr.init();
//
//        String issue = "1";
//        LotteryRGType lotteryRGType = LotteryRGType.PARITY;
//        sendMessage(lotteryRGType, issue, System.currentTimeMillis() + 60_000);
//        sendMessage(lotteryRGType, issue, System.currentTimeMillis() + 60_000);
//        sendMessage(lotteryRGType, issue, System.currentTimeMillis() + 60_000);
//
//
//        List rsList = RGBetRecordCache.getInstance().getAllRecordListFromCache(false, lotteryRGType, issue);
//
//        FastJsonHelper.prettyJson(rsList);
//
//        System.in.read();
//
//
////        System.out.println(TruntbBetRecordCache.getInstance().getAllRecordListFromCache(TurnTableType.T_1));
//    }
//
//}
