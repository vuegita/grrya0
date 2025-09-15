package com.inso.modules.game.rocket.logical;


import com.google.common.collect.Lists;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RandomStringUtils;
import com.inso.framework.utils.RandomUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.MyLotteryBetRecordCache;
import com.inso.modules.game.helper.BetAmountHelper;
import com.inso.modules.game.rocket.helper.RocketHelper;
import com.inso.modules.game.rocket.model.RocketType;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RocketRobotManager {

    private static Log LOG = LogFactory.getLog(RocketRobotManager.class);

    private int maxBetCount;
    private AtomicInteger mCurrentBetCount = new AtomicInteger();
    private AtomicInteger mCashoutCount = new AtomicInteger();

    private int mRealBetCount = 0;

    private List<String> mList = Lists.newArrayList();

    private boolean isRunning = false;

    private boolean isBetFirst = true;
    private boolean isCashoutFirst = true;

    private boolean isDEV = MyEnvironment.isDev();


    private interface MyInternal {
        public RocketRobotManager mgr = new RocketRobotManager();
    }

    private RocketRobotManager()
    {
    }

    public static RocketRobotManager getInstance()
    {
        return MyInternal.mgr;
    }

    public void clear()
    {
        this.maxBetCount = RandomUtils.nextInt(40) + 1;
//        this.maxBetCount = 11;
        LOG.info("max robot bet count = " + this.maxBetCount);

        this.mList.clear();
        mCurrentBetCount.set(0);
        mCashoutCount.set(0);
        mRealBetCount = 0;
        if(isDEV)
        {
            isBetFirst = true;
        }

        this.isCashoutFirst = true;
    }

    public void doBet(String issue, RocketPeriodStatus periodStatus)
    {
        int currentCount = mCurrentBetCount.get();
        if(currentCount >= this.maxBetCount)
        {
            return;
        }

        String orderno = addRecord(issue);
        if(StringUtils.isEmpty(orderno))
        {
            return;
        }
//        LOG.info("current robot bet count = " + currentCount + ", orderno = " + orderno);

        if(currentCount >= 10)
        {
            mList.remove(0);
        }
        else
        {
            mRealBetCount ++;
        }

        currentCount = mCurrentBetCount.incrementAndGet();

        mList.add(orderno);
        periodStatus.updateRobot(true, currentCount);

//        if(currentCount >= 11)
//        {
//            mList.remove(10);
//        }

        if( isBetFirst)
        {
//            periodStatus.incre("a", null, new BigDecimal(100), new BigDecimal(3));
//            isBetFirst = false;
        }

        log(periodStatus);
    }

    public void cashout(String issue, float openResult, RocketPeriodStatus periodStatus)
    {
        try {
            if(isCashoutFirst)
            {
                isCashoutFirst = false;
                Collections.shuffle(mList);
            }
            int count = 20;
            if(openResult <= 1.2)
            {
                count = 15;
            }
            else if(openResult <= 1.3)
            {
                count = 18;
            }

            if(openResult <= 1.3 && RandomUtils.nextInt(count) >= 3)
            {
                return;
            }

            doCashout(issue, openResult, periodStatus);

            int totalRandomCashoutCount = RandomUtils.nextInt(3);
            if(totalRandomCashoutCount <= 0)
            {
                return;
            }

            for(int i = 0; i < totalRandomCashoutCount; i ++)
            {
                if(RandomUtils.nextBoolean())
                {
                    doCashout(issue, openResult, periodStatus);
                }
            }
        } finally {
            log(periodStatus);
        }

    }

    private void doCashout(String issue, float openResult, RocketPeriodStatus periodStatus)
    {
        if(isRunning)
        {
            return;
        }

        isRunning = true;

        try {
            int rsCount = mCurrentBetCount.get();
            if(rsCount <= 0)
            {
                return;
            }

            int cashoutCount = mCashoutCount.get();
            if(cashoutCount >= rsCount)
            {
                return;
            }

            cashoutCount = mCashoutCount.incrementAndGet();
            if (openResult >= 1.02)
            {
                int deductValue = RandomUtils.nextInt(5) + 1;
                float rsResult = openResult - deductValue / 100f;
                if(rsResult >= 1)
                {
                    openResult = rsResult;
                }
            }

            int cashouIndex = rsCount - cashoutCount;
            if(cashouIndex < 0)
            {
                return;
            }

            mRealBetCount --;
            if(mRealBetCount >= 0)
            {
                String orderno = mList.get(mRealBetCount);
                MyLotteryBetRecordCache.getInstance().updateBetItem(issue,true, null, RocketType.CRASH, orderno, openResult + StringUtils.getEmpty());

                if(mRealBetCount <= 0)
                {
//                    RocketBetRecordCache.getInstance().saveLatestRecord(issue, RocketType.CRASH);
//                    FastJsonHelper.prettyJson(RocketBetRecordCache.getInstance().getAllRecordListFromCache(false, RocketType.CRASH, issue));
                }
            }

            //LOG.info("cashouIndex = " + cashouIndex);
            periodStatus.updateRobot(false, cashoutCount);

            return;
        } catch (Exception e)
        {
            LOG.error("handle error:", e);
        }
        finally {
            isRunning = false;
        }
    }

    private void log(RocketPeriodStatus periodStatus)
    {
//        StringBuilder buffer = new StringBuilder();
//        buffer.append("totalBetCount = ").append(periodStatus.getmTotalBetCount());
//        buffer.append(", totalCashoutCount = ").append(periodStatus.getmCashoutCount().get());
//
//        buffer.append(", robotBetCount = ").append(periodStatus.getmRobotBetCount());
//        buffer.append(", totalCashoutCount = ").append(periodStatus.getmRobotCashoutCount());
//
//        LOG.info(buffer.toString());
    }

    private String addRecord(String issue)
    {
        RocketType rocketType = RocketType.CRASH;
        String username = "ep" + RandomStringUtils.generator0_Z(6) + "_gmail";
        long betTotalAmountValue = BetAmountHelper.randomBetAmount();
        // 添加当前投注记录
        Date createtime = new Date();
        String orderno = RocketHelper.nextOrderId(issue, -1, true);
        boolean rs = MyLotteryBetRecordCache.getInstance().addRecord(true, orderno, rocketType, issue, username, null, new BigDecimal(betTotalAmountValue), null, null, createtime);
        if(rs)
        {
            return orderno;
        }
        return null;
    }

    private void test1(String issue)
    {
        RocketType rocketType = RocketType.CRASH;
        RocketPeriodStatus periodStatus = RocketPeriodStatus.loadCache(false, rocketType, issue);
        periodStatus.saveCache();
    }

    public static void main(String[] args) {

        String issue = "123";

        RocketRobotManager mgr = RocketRobotManager.getInstance();
        mgr.test1(issue);

        mgr.doBet(issue, null);

        mgr.cashout(issue, 0.12f, null);

        List rsList = MyLotteryBetRecordCache.getInstance().getAllRecordListFromCache(true, RocketType.CRASH, issue);
        System.out.println(FastJsonHelper.jsonEncode(rsList));
    }

}
