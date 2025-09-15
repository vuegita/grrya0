package com.inso.modules.common.model;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.inso.framework.conf.MyConfiguration;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.RandomStringUtils;
import com.inso.framework.utils.RandomUtils;
import org.joda.time.DateTime;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.modules.common.cache.OverviewCacheHelper;
import com.inso.modules.report.model.MemberReport;


/**
 * 今日会员盈亏统计
 */
public class TodayMemberProfitLossByUserType {
    private static int DEFAULT_CAPACITY = 20;

    private static int DEFAULT_INDEX_FIRST = 0;
    private static int DEFAULT_INDEX_LAST = DEFAULT_CAPACITY - 1;

    // 盈利
    private int mCurrentProfileCount = 0;
    private boolean isProfileOrder = false;
    private List<MemberReport> mProfileList = Lists.newArrayList();

    // 亏损
    private int mCurrentLossCount = 0;
    private boolean isLossOrder = false;
    private List<MemberReport> mLossList = Lists.newArrayList();

    private static boolean isDebug = false;

    private static final int minBetAmount;

    static {
        int tmpMinBetAmount = MyConfiguration.getInstance().getInt("game.basic.bet.amount.min", 1);
        if(tmpMinBetAmount < 1)
        {
            tmpMinBetAmount = 1;
        }
        minBetAmount = tmpMinBetAmount;
    }

    public void addReport(MemberReport report)
    {
        BigDecimal result = report.getBusinessRecharge().subtract(report.getBusinessDeduct());
        if(result == null)
        {
            return;
        }
        report.setTotalBusinessProfitLoss(result);

        if(result.compareTo(BigDecimal.ZERO) >= 0)
        {
            handleProfile(report, result);
        }
        else
        {
            handleLoss(report, result);
        }
    }

    private void handleProfile(MemberReport report, BigDecimal result)
    {
        // 如果等于容器最大个数，并且没有排序则排序
        if(!isProfileOrder && mCurrentProfileCount <= DEFAULT_CAPACITY)
        {
            sortProfileList(null);

            if(mCurrentProfileCount >= DEFAULT_CAPACITY)
            {
                isProfileOrder = true;
            }
        }

        // 已经满了，则用插入，并且删除最后一个，因为已经排序
        if(mCurrentProfileCount >= DEFAULT_CAPACITY)
        {
            // 比第一个还大
            MemberReport firstItem = mProfileList.get(DEFAULT_INDEX_FIRST);
            if(result.compareTo(firstItem.getTotalBusinessProfitLoss()) >= 0)
            {
                mProfileList.remove(DEFAULT_INDEX_LAST);
                mProfileList.add(0, report);
                return;
            }

            // 比最后一个元素都小
            MemberReport lastItem = mProfileList.get(DEFAULT_INDEX_LAST);
            if(result.compareTo(lastItem.getTotalBusinessProfitLoss()) <= 0)
            {
                return;
            }

            // 第一个和最后一个比了
            for(int i = 1; i < DEFAULT_CAPACITY - 1; i ++)
            {
                MemberReport item = mProfileList.get(i);
                if(result.compareTo(item.getTotalBusinessProfitLoss()) >= 0)
                {
                    mProfileList.remove(DEFAULT_INDEX_LAST);
                    mProfileList.add(i, report);
                    break;
                }
            }
        }
        else
        {
            mProfileList.add(report);
            mCurrentProfileCount++;
        }
    }

    private void handleLoss(MemberReport report, BigDecimal result)
    {
        // 如果等于容器最大个数，并且没有排序则排序
        if(!isLossOrder && mCurrentLossCount >= DEFAULT_CAPACITY)
        {
            sortLossList();
        }

        // 已经满了，则用插入，并且删除最后一个，因为已经排序
        if(mCurrentLossCount >= DEFAULT_CAPACITY)
        {
            // 比第一个还大
            MemberReport firstItem = mLossList.get(DEFAULT_INDEX_FIRST);
            if(result.compareTo(firstItem.getTotalBusinessProfitLoss()) < 0)
            {
                mLossList.add(0, report);
                mLossList.remove(DEFAULT_INDEX_LAST);
                return;
            }

            // 比最后一个元素都小
            MemberReport lastItem = mLossList.get(DEFAULT_INDEX_LAST);
            if(result.compareTo(lastItem.getTotalBusinessProfitLoss()) >= 0)
            {
                return;
            }

            // 第一个和最后一个比了
            for(int i = 1; i < DEFAULT_CAPACITY; i ++)
            {
                MemberReport item = mLossList.get(i);
                if(result.compareTo(item.getTotalBusinessProfitLoss()) < 0)
                {
                    mLossList.remove(DEFAULT_INDEX_LAST);
                    mLossList.add(i, report);
                    break;
                }
            }
        }
        else
        {
            mLossList.add(report);
            mCurrentLossCount++;
        }
    }



    private void sortProfileList(List<MemberReport> dataList)
    {
        if(dataList == null)
        {
            dataList = mProfileList;
        }
        Collections.sort(dataList, new Comparator<MemberReport>() {
            @Override
            public int compare(MemberReport report1, MemberReport report2) {
                if(report1.getTotalBusinessProfitLoss().compareTo(report2.getTotalBusinessProfitLoss()) >= 0)
                {
                    return -1;
                }
                return 1;
            }
        });
    }

    private void sortLossList()
    {
        Collections.sort(mLossList, new Comparator<MemberReport>() {
            @Override
            public int compare(MemberReport report1, MemberReport report2) {

                if(report1.getTotalBusinessProfitLoss().compareTo(report2.getTotalBusinessProfitLoss()) >= 0)
                {
                    return 1;
                }
                return -1;
            }
        });
    }

    private void addTestReport(String username, long businessRecharge, long businessDeduct)
    {
        MemberReport report = new MemberReport();
        report.setUsername(username);
        report.setBusinessRecharge(new BigDecimal(businessRecharge));
        report.setBusinessDeduct(new BigDecimal(businessDeduct));

        addReport(report);
    }

    /**
     * 获取盈亏数据
     * @param isProfit  true 为 盈利 |　false 为亏损
     * @return
     */
    public static List<MemberReport> getProfitLoss(boolean isProfit,long userid)
    {
        DateTime dateTime = DateTime.now();
        int dayOfYear = dateTime.getDayOfYear();
        String profileCacheKey = OverviewCacheHelper.createmProfileReportListByUserIdCacheKey(isProfit, dayOfYear,userid);
        List<MemberReport> list = CacheManager.getInstance().getList(profileCacheKey, MemberReport.class);
        if(list == null)
        {
            list = Collections.emptyList();
        }
        return list;
    }

    public void doFinish(DateTime dateTime, boolean isInitSystemProfit)
    {
        int tmpCacheSysId = -2;
        List<MemberReport> rsList = getProfitLoss(true, tmpCacheSysId);

        if(!isInitSystemProfit && !CollectionUtils.isEmpty(rsList))
        {
            return;
        }

        int tmpCacheSize = 20;
        boolean isInit = false;
        if(CollectionUtils.isEmpty(rsList))
        {
            rsList = Lists.newArrayList();
            for(int i = 0; i < tmpCacheSize; i ++)
            {
                MemberReport report = createOrUpdateRobotProfit(null);
                rsList.add(report);
            }
            isInit = true;
        }

        if(!isInit)
        {
            for(MemberReport report : rsList)
            {
                boolean updateData = RandomUtils.nextInt(10) <= 3;
                if(updateData)
                {
                    createOrUpdateRobotProfit(report);
                }
            }

            // 再加一个人
            MemberReport report = createOrUpdateRobotProfit(null);
            rsList.add(report);

            sortProfileList(rsList);
            rsList.remove(tmpCacheSize - 1);
        }

        doFinish(dateTime, tmpCacheSysId, rsList, null);

        //
        if(!CollectionUtils.isEmpty(mProfileList))
        {
            rsList.addAll(mProfileList);
        }

        sortProfileList(rsList);


        int size = rsList.size();
        for(int i = size - 1; i > 10; i --)
        {
            rsList.remove(i);
        }

        int cacheSysId = 0;
        doFinish(dateTime, cacheSysId, rsList, null);
    }

    private MemberReport createOrUpdateRobotProfit(MemberReport report)
    {
        boolean isInit = false;
        if(report == null)
        {
            report = new MemberReport();
            String username = "ep" + RandomStringUtils.generator0_Z(6) + "_gmail";
            report.setUsername(username);
            isInit = true;
        }
        int value = 0;
        if(isInit)
        {
            value = RandomUtils.nextInt(100_00) + 1;
        }
        else
        {
            value = RandomUtils.nextInt(1500_00);
        }
        float profit = value / 100.0f;

        profit = profit * minBetAmount;
        BigDecimal newValue = new BigDecimal(profit);
        BigDecimal rsValue = report.getTotalBusinessProfitLoss().add(newValue);
        report.setTotalBusinessProfitLoss(rsValue);
        return report;
    }

    public void doFinish(DateTime dateTime, long userid)
    {
        sortProfileList(null);
        sortLossList();

        doFinish(dateTime, userid, mProfileList, mLossList);
    }

    private void doFinish(DateTime dateTime, long userid, List<MemberReport> profitList, List<MemberReport> lossList)
    {
        int dayOfYear = dateTime.getDayOfYear();
        if(profitList != null)
        {
            String profileCacheKey = OverviewCacheHelper.createmProfileReportListByUserIdCacheKey(true, dayOfYear, userid);
            CacheManager.getInstance().setString(profileCacheKey, FastJsonHelper.jsonEncode(profitList), CacheManager.EXPIRES_DAY);
        }


        if(userid > 0 && lossList != null)
        {
            String lossCacheKey = OverviewCacheHelper.createmProfileReportListByUserIdCacheKey(false, dayOfYear, userid);
            CacheManager.getInstance().setString(lossCacheKey, FastJsonHelper.jsonEncode(lossList), CacheManager.EXPIRES_DAY);
        }

    }

    public static void main(String[] args) {

        isDebug = true;

        TodayMemberProfitLossByUserType profitLoss = new TodayMemberProfitLossByUserType();
//        profitLoss.addTestReport("a1",10,2); // 8
//        profitLoss.addTestReport("a2",20,5); // 5
//        profitLoss.addTestReport("a3",70,3); // 7
//        profitLoss.addTestReport("a4",30,1); // 9

//
//        profitLoss.addTestReport("a4",10,20);  // -10
//        profitLoss.addTestReport("a4",10,30);  // -20
//        profitLoss.addTestReport("a4",10,13);  // -3
//        profitLoss.addTestReport("a4",10,17);  // -7
//
        profitLoss.doFinish(new DateTime(), true);

        List<MemberReport> rsList = getProfitLoss(true, 0);
        for(MemberReport model : rsList)
        {
            System.out.println("username = " + model.getUsername() + ", profit = " + model.getTotalBusinessProfitLoss());
        }
//        FastJsonHelper.prettyJson(getProfitLoss(true, 0).get(2));
//        System.out.println();
    }

}
