package com.inso.modules.common;

import com.google.common.collect.Maps;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.cache.OverviewCacheHelper;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.common.model.OverviewType;
import com.inso.modules.common.model.TodayGameReport;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.andar_bahar.model.ABPeriodInfo;
import com.inso.modules.game.andar_bahar.model.ABType;
import com.inso.modules.game.andar_bahar.service.ABPeriodService;
import com.inso.modules.game.fruit.model.FruitPeriodInfo;
import com.inso.modules.game.fruit.model.FruitType;
import com.inso.modules.game.fruit.service.FruitPeriodService;
import com.inso.modules.game.lottery_game_impl.btc_kline.model.BTCKlineType;
import com.inso.modules.game.lottery_game_impl.football.model.FootballType;
import com.inso.modules.game.lottery_game_impl.mines.model.MineType;
import com.inso.modules.game.lottery_game_impl.pg.model.PgGameType;
import com.inso.modules.game.lottery_game_impl.rg2.model.RedGreen2Type;
import com.inso.modules.game.lottery_game_impl.turntable.model.TurnTableType;
import com.inso.modules.game.model.NewLotteryOrderInfo;
import com.inso.modules.game.model.NewLotteryPeriodInfo;
import com.inso.modules.game.rg.model.LotteryPeriodInfo;
import com.inso.modules.game.rg.model.LotteryRGType;
import com.inso.modules.game.rg.service.LotteryPeriodService;
import com.inso.modules.game.rocket.model.RocketType;
import com.inso.modules.game.service.NewLotteryOrderService;
import com.inso.modules.game.service.NewLotteryPeriodService;
import com.inso.modules.passport.user.model.PassportStatsInfo;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.report.model.GameBusinessDay;
import com.inso.modules.report.model.MemberReport;
import com.inso.modules.report.model.PlatformReport;
import com.inso.modules.report.service.PlatformReportService;
import com.inso.modules.report.service.UserReportService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * 平台概况-总后台
 */
@Component
public class PlatformOverviewManager {

    private static Log LOG = LogFactory.getLog(PlatformOverviewManager.class);

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private UserReportService mUserReportService;

    @Autowired
    private PlatformReportService mPlatformReportService;

    @Autowired
    private LotteryPeriodService mLotteryPeriodService;

    @Autowired
    private ABPeriodService mABPeriodService;

    @Autowired
    private FruitPeriodService  mFruitPeriodService;

    @Autowired
    private NewLotteryPeriodService mNewLotteryPeriodService;

    @Autowired
    private NewLotteryOrderService mNewLotteryOrderService;

    private void test()
    {
        String timeStr = "2023-04-22 21:00:00";
        Date date = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, timeStr);

        DateTime nowTime = new DateTime(date);

        statsUserCount(nowTime);
    }

    public void doStats()
    {
//        test();
        DateTime nowTime = new DateTime();

        DateTime beginTime = nowTime.withTime(0, 0, 0, 0);
        DateTime endTime = nowTime.withTime(23, 59, 59, 0);

        statsUserCount(nowTime);
        statsUserAmount(nowTime);
//        statsRedGreenGame(nowTime);
//        statsABGame(nowTime);
//        statsFruitGame(nowTime);

        statsNewLotteryGame(nowTime, BTCKlineType.mArr, OverviewType.GAME_BTC_KLINE);
        statsNewLotteryGame(nowTime, TurnTableType.mArr, OverviewType.GAME_TURNTABLE);
        statsNewLotteryGame(nowTime, RocketType.mArr, OverviewType.GAME_ROCKET);
        statsNewLotteryGame(nowTime, RedGreen2Type.mArr, OverviewType.GAME_RED_GREEN);

        //
        statsNewLotteryGameByOrder(beginTime, endTime, FootballType.mArr, OverviewType.GAME_FOOTBALL);
        statsNewLotteryGameByOrder(beginTime, endTime, MineType.mArr, OverviewType.GAME_MINES);
        statsNewLotteryGameByOrder(beginTime, endTime, PgGameType.mArr, OverviewType.GAME_PG);
    }

    private void statsUserCount(DateTime nowTime)
    {
        // 今日注册人数 | 会员总数
        // 代理总数 | 员工总数
        int todayOfYear = nowTime.getDayOfYear();

        // 		sql.append("select count(*) total_count, sum(C.money_balance) total_balance, B.attr_direct_staffname staffname, B.attr_agentname agentname ");
        // 统计所有平台用户数据
        PassportStatsInfo passportStatsInfo = new PassportStatsInfo();
        passportStatsInfo.setRefreshTime(new Date());

        // 统计代理下级会员
        Map<String, PassportStatsInfo> agentStatsInfoMaps = Maps.newHashMap();

        mUserService.statsCountByUserType(new Callback<Map<String, Object>>() {
            @Override
            public void execute(Map<String, Object> model) {

                String staffname = StringUtils.asString(model.get("staffname"));
                if(StringUtils.isEmpty(staffname))
                {
                    return;
                }

                String agentname = StringUtils.asString(model.get("agentname"));
                if(StringUtils.isEmpty(agentname))
                {
                    return;
                }

                // reg-count
                long count = StringUtils.asLong(model.get("total_count"));
                passportStatsInfo.setTotalMemberRegCount(passportStatsInfo.getTotalMemberRegCount() + count);

                // balance
                float totalBalance = StringUtils.asFloat(model.get("total_balance"));
                passportStatsInfo.setTotalBalance(passportStatsInfo.getTotalBalance() + totalBalance);


                // 总后台-员工 + 1
                passportStatsInfo.setTotalStaffCount(passportStatsInfo.getTotalStaffCount() + 1);

                // 总后台-代理 + 1
                if(!agentStatsInfoMaps.containsKey(agentname))
                {
                    passportStatsInfo.setTotalAgentCount(passportStatsInfo.getTotalAgentCount() + 1);
                }

                // 代理后台
                PassportStatsInfo agentStatsInfo = getAgentStatsInfo(agentStatsInfoMaps, agentname);
                agentStatsInfo.setTotalMemberRegCount(agentStatsInfo.getTotalMemberRegCount() + count);
                agentStatsInfo.setTotalBalance(agentStatsInfo.getTotalBalance() + totalBalance);

                // 员工后台
                PassportStatsInfo staffStatsInfo = getAgentStatsInfo(agentStatsInfoMaps, staffname);
                staffStatsInfo.setTotalMemberRegCount(staffStatsInfo.getTotalMemberRegCount() + count);
                staffStatsInfo.setTotalBalance(staffStatsInfo.getTotalBalance() + totalBalance);

            }
        });

//        mUserService.queryAll(null, null, new Callback<UserInfo>() {
//            @Override
//            public void execute(UserInfo userInfo) {
//                passportStatsInfo.increByUserInfo(userInfo, todayOfYear);
//            }
//        });


        DateTime todayRechargeDateTime = nowTime;

        DateTime startTime = nowTime.withTime(0, 0, 0, 0);
        DateTime endTime = nowTime.withTime(23, 59, 59, 0);
        mUserAttrService.queryAllMember2(startTime.toDate(), endTime.toDate(), new Callback<UserAttr>() {
            public void execute(UserAttr userAttr) {

                // 今日充值
//                boolean isFirstRecharge = false;
//                if(userAttr.getFirstRechargeTime() != null && !StringUtils.isEmpty(userAttr.getFirstRechargeOrderno()))
//                {
//                    DateTime rechargeDateTime = new DateTime(userAttr.getFirstRechargeTime());
//                    isFirstRecharge = todayRechargeDateTime.getYear() == rechargeDateTime.getYear() && todayRechargeDateTime.getDayOfYear() == rechargeDateTime.getDayOfYear();
//                }
//
//                if(isFirstRecharge)
//                {
//                    // 今日首次充值人数
//                    passportStatsInfo.increRechargeCount(userAttr.getFirstRechargeAmount());
//                }

                DateTime regDateTime = new DateTime(userAttr.getRegtime());
                boolean isTodayReg = todayRechargeDateTime.getYear() == regDateTime.getYear() && todayRechargeDateTime.getDayOfYear() == regDateTime.getDayOfYear();

                // 分裂会员
                boolean isTodaySplitCount = isTodayReg && userAttr.getParentid() > 0;

                //今日平台分裂人数
                if(isTodaySplitCount){
                    passportStatsInfo.increTodaySplitCount();
                }
                if(isTodayReg)
                {
                    // 今日注册人数
                    passportStatsInfo.increTodayRegCount();
                }

                // agent
                if(userAttr.getAgentid() > 0)
                {
                    PassportStatsInfo statsInfo = getAgentStatsInfo(agentStatsInfoMaps, userAttr.getAgentname());
//                    statsInfo.increAgent(userAttr, isTodayReg);
//                    if(isFirstRecharge)
//                    {
//                        // 今日首次充值人数
//                        statsInfo.increRechargeCount(userAttr.getFirstRechargeAmount());
//                    }
                    if(isTodaySplitCount)
                    {
                        statsInfo.increTodaySplitCount();
                    }
                    if(isTodayReg)
                    {
                        statsInfo.setTodayMemberRegCount(statsInfo.getTodayMemberRegCount() + 1);
                    }
                }

                if(userAttr.getDirectStaffid() > 0)
                {
                    PassportStatsInfo statsInfo = getAgentStatsInfo(agentStatsInfoMaps, userAttr.getDirectStaffname());
//                    statsInfo.increAgent(userAttr, isTodayReg);
//                    if(isFirstRecharge)
//                    {
//                        // 今日首次充值人数
//                        statsInfo.increRechargeCount(userAttr.getFirstRechargeAmount());
//                    }
                    if(isTodaySplitCount)
                    {
                        statsInfo.increTodaySplitCount();
                    }
                    if(isTodayReg)
                    {
                        statsInfo.setTodayMemberRegCount(statsInfo.getTodayMemberRegCount() + 1);
                    }
                }

            }
        });

        // 金额
        mUserAttrService.queryAllMemberByUserReport(startTime, new Callback<UserAttr>() {
            public void execute(UserAttr userAttr) {

                // 今日充值
                boolean isFirstRecharge = false;
                if(userAttr.getFirstRechargeTime() != null && !StringUtils.isEmpty(userAttr.getFirstRechargeOrderno()))
                {
                    DateTime rechargeDateTime = new DateTime(userAttr.getFirstRechargeTime());
                    isFirstRecharge = todayRechargeDateTime.getYear() == rechargeDateTime.getYear() && todayRechargeDateTime.getDayOfYear() == rechargeDateTime.getDayOfYear();
                }

                if(isFirstRecharge)
                {
                    // 今日首次充值人数
                    passportStatsInfo.increRechargeCount(userAttr.getFirstRechargeAmount());
                }

                // agent
                if(userAttr.getAgentid() > 0)
                {
                    PassportStatsInfo statsInfo = getAgentStatsInfo(agentStatsInfoMaps, userAttr.getAgentname());
//                    statsInfo.increAgent(userAttr, isTodayReg);
                    if(isFirstRecharge)
                    {
                        // 今日首次充值人数
                        statsInfo.increRechargeCount(userAttr.getFirstRechargeAmount());
                    }
                }

                if(userAttr.getDirectStaffid() > 0)
                {
                    PassportStatsInfo statsInfo = getAgentStatsInfo(agentStatsInfoMaps, userAttr.getDirectStaffname());
//                    statsInfo.increAgent(userAttr, isTodayReg);
                    if(isFirstRecharge)
                    {
                        // 今日首次充值人数
                        statsInfo.increRechargeCount(userAttr.getFirstRechargeAmount());
                    }
                }

            }
        });

        // 保存平台相关统计信息
        savePlatformToCache(OverviewType.USER_COUNT, passportStatsInfo);

        // 代理统计信息
        Set<String> agentKeys = agentStatsInfoMaps.keySet();
        for(String username : agentKeys)
        {
            PassportStatsInfo statsInfo = agentStatsInfoMaps.get(username);
            saveAgentInfoToCache(OverviewType.AGENT_SUB_COUNT, username, statsInfo);
        }
        agentStatsInfoMaps.clear();
    }

    /**
     * 统计今日金额报表
     */
    private void statsUserAmount(DateTime nowTime)
    {
        // 今日提现总额 | 今日充值总额 |
        // 今日中奖总额 | 今日投注总额
        // 今日平台赠送 |
        String pdateStr = nowTime.toString(DateUtils.TYPE_YYYY_MM_DD);
        String startTime = DateUtils.getBeginTimeOfDay(pdateStr);
        String endTime = DateUtils.getEndTimeOfDay(pdateStr);

        MemberReport todayMemberReport = new MemberReport();
        todayMemberReport.init();
        mUserReportService.queryAllMemberReport(startTime, endTime, new Callback<MemberReport>() {
            @Override
            public void execute(MemberReport memberReport) {
                todayMemberReport.incre(memberReport);
            }
        });

        todayMemberReport.calcProfit();
        savePlatformToCache(OverviewType.USER_AMOUNT, todayMemberReport);

        // 平台汇总
        String platformCacheKey = OverviewCacheHelper.createPlatformHisoryCacheKey(nowTime.getDayOfYear());
        PlatformReport cacheReport = CacheManager.getInstance().getObject(platformCacheKey, PlatformReport.class);
        if(cacheReport == null)
        {
            cacheReport = new PlatformReport();
            cacheReport.init();
        }

        PlatformReport platformReport = cacheReport;
        // 小于0表示未计算或在凌晨时未统计
        if(MyEnvironment.isDev() || (platformReport.getRecharge().floatValue() <= 0 || nowTime.getHourOfDay() < 2))
        {
            mPlatformReportService.queryAll(new Callback<PlatformReport>() {
                @Override
                public void execute(PlatformReport report) {
                    platformReport.incre(report);
                }
            });
        }

        platformReport.incre(todayMemberReport);
        platformReport.calcProfit();
        savePlatformToCache(OverviewType.PLATFORM_STATS, platformReport);
    }

    /**
     * 统计红绿
     * @param nowTime
     */
    public void statsRedGreenGame(DateTime nowTime)
    {
        String pdateStr = nowTime.toString(DateUtils.TYPE_YYYY_MM_DD);
        String startTime = DateUtils.getBeginTimeOfDay(pdateStr);

        // 统计到现在
        String endTime = nowTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);

        Map<String, TodayGameReport> maps = Maps.newHashMap();

        // 所有类型汇总
        TodayGameReport totalStatus = new TodayGameReport();
        totalStatus.setTitle("所有类型汇总");
        maps.put("all", totalStatus);

        // 子类汇总
        LotteryRGType[] rgTypeValues = LotteryRGType.values();
        for(LotteryRGType rgType : rgTypeValues)
        {
            getTodayLotteryRgStatus(maps, rgType);
        }

        mLotteryPeriodService.queryAll(null, startTime, endTime, new Callback<LotteryPeriodInfo>() {
            @Override
            public void execute(LotteryPeriodInfo periodInfo) {
                try {
                    // 全部汇总
                    totalStatus.increAmount(periodInfo.getTotalBetAmount(), periodInfo.getTotalWinAmount(), periodInfo.getTotalFeemoney());
                    totalStatus.increCount(periodInfo.getTotalBetCount(), periodInfo.getTotalWinCount());

                    LotteryRGType rgType = LotteryRGType.getType(periodInfo.getType());
                    TodayGameReport status = getTodayLotteryRgStatus(maps, rgType);

                    status.increAmount(periodInfo.getTotalBetAmount(), periodInfo.getTotalWinAmount(), periodInfo.getTotalFeemoney());
                    status.increCount(periodInfo.getTotalBetCount(), periodInfo.getTotalWinCount());
                } catch (Exception e) {
                    LOG.error("handle error: ", e);
                }
            }
        });

        //
        savePlatformToCache(OverviewType.GAME_LOTTERY_RG, maps);
    }

    /**
     * 统计AB游戏
     * @param nowTime
     */
    public void statsABGame(DateTime nowTime)
    {
        String pdateStr = nowTime.toString(DateUtils.TYPE_YYYY_MM_DD);
        String startTime = DateUtils.getBeginTimeOfDay(pdateStr);

        // 统计到现在
        String endTime = nowTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);

        Map<String, TodayGameReport> maps = Maps.newHashMap();

        // 所有类型汇总
        TodayGameReport totalStatus = new TodayGameReport();
        totalStatus.setTitle("所有类型汇总");
        maps.put("all", totalStatus);

        // 子类汇总
        ABType[] abTypes = ABType.values();
        for(ABType type : abTypes)
        {
            getTodayABStatus(maps, type);
        }

        mABPeriodService.queryAll(null, startTime, endTime, new Callback<ABPeriodInfo>() {
            @Override
            public void execute(ABPeriodInfo periodInfo) {
                try {
                    // 全部汇总
                    totalStatus.increAmount(periodInfo.getTotalBetAmount(), periodInfo.getTotalWinAmount(), periodInfo.getTotalFeemoney());
                    totalStatus.increCount(periodInfo.getTotalBetCount(), periodInfo.getTotalWinCount());

                    ABType abType = ABType.getType(periodInfo.getType());
                    TodayGameReport status = getTodayABStatus(maps, abType);

                    status.increAmount(periodInfo.getTotalBetAmount(), periodInfo.getTotalWinAmount(), periodInfo.getTotalFeemoney());
                    status.increCount(periodInfo.getTotalBetCount(), periodInfo.getTotalWinCount());
                } catch (Exception e) {
                    LOG.error("handle error: ", e);
                }
            }
        });

        //
        savePlatformToCache(OverviewType.GAME_ANDAR_BAHAR, maps);
    }

    /**
     * 统计水果机游戏
     * @param nowTime
     */
    public void statsFruitGame(DateTime nowTime)
    {
        String pdateStr = nowTime.toString(DateUtils.TYPE_YYYY_MM_DD);
        String startTime = DateUtils.getBeginTimeOfDay(pdateStr);

        // 统计到现在
        String endTime = nowTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);

        Map<String, TodayGameReport> maps = Maps.newHashMap();

        // 所有类型汇总
        TodayGameReport totalStatus = new TodayGameReport();
        totalStatus.setTitle("所有类型汇总");
        maps.put("all", totalStatus);

        // 子类汇总
        FruitType[] fruitType = FruitType.values();
        for(FruitType type : fruitType)
        {
            getTodayABStatus(maps, type);
        }

        mFruitPeriodService.queryAll(null, startTime, endTime, new Callback<FruitPeriodInfo>() {
            @Override
            public void execute(FruitPeriodInfo periodInfo) {
                try {
                    // 全部汇总
                    totalStatus.increAmount(periodInfo.getTotalBetAmount(), periodInfo.getTotalWinAmount(), periodInfo.getTotalFeemoney());
                    totalStatus.increCount(periodInfo.getTotalBetCount(), periodInfo.getTotalWinCount());

                    FruitType  fruitType = FruitType.getType(periodInfo.getType());
                    TodayGameReport status = getTodayABStatus(maps, fruitType);

                    status.increAmount(periodInfo.getTotalBetAmount(), periodInfo.getTotalWinAmount(), periodInfo.getTotalFeemoney());
                    status.increCount(periodInfo.getTotalBetCount(), periodInfo.getTotalWinCount());
                } catch (Exception e) {
                    LOG.error("handle error: ", e);
                }
            }
        });

        //
        savePlatformToCache(OverviewType.GAME_FRUIT, maps);
    }

    /**
     * 统计新游戏
     * @param nowTime
     */
    public void statsNewLotteryGame(DateTime nowTime, GameChildType[] arr, OverviewType overviewType)
    {
        String pdateStr = nowTime.toString(DateUtils.TYPE_YYYY_MM_DD);
        String startTime = DateUtils.getBeginTimeOfDay(pdateStr);

        // 统计到现在
        String endTime = nowTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);

        Map<String, TodayGameReport> maps = Maps.newHashMap();

        // 所有类型汇总
        TodayGameReport totalStatus = new TodayGameReport();
        totalStatus.setTitle("所有类型汇总");
        maps.put("all", totalStatus);

        // 子类汇总
        for(GameChildType type : arr)
        {
            getTodayABStatus(maps, type);
        }

        mNewLotteryPeriodService.queryAll2(arr[0], startTime, endTime, null, new Callback<NewLotteryPeriodInfo>() {
            @Override
            public void execute(NewLotteryPeriodInfo periodInfo) {
                try {
                    // 全部汇总
                    totalStatus.increAmount(periodInfo.getTotalBetAmount(), periodInfo.getTotalWinAmount(), periodInfo.getTotalFeemoney());
                    totalStatus.increCount(periodInfo.getTotalBetCount(), periodInfo.getTotalWinCount());

                    GameChildType  gameType = GameChildType.getType(periodInfo.getType());
                    TodayGameReport status = getTodayABStatus(maps, gameType);

                    status.increAmount(periodInfo.getTotalBetAmount(), periodInfo.getTotalWinAmount(), periodInfo.getTotalFeemoney());
                    status.increCount(periodInfo.getTotalBetCount(), periodInfo.getTotalWinCount());
                } catch (Exception e) {
                    LOG.error("handle error: ", e);
                }
            }
        });

        //
        savePlatformToCache(overviewType, maps);
    }

    public void statsNewLotteryGameByOrder(DateTime beginTime, DateTime endTime, GameChildType[] arr, OverviewType overviewType)
    {

        Map<String, TodayGameReport> maps = Maps.newHashMap();

        // 所有类型汇总
        TodayGameReport totalStatus = new TodayGameReport();
        totalStatus.setTitle("所有类型汇总");
        maps.put("all", totalStatus);

        // 子类汇总
        for(GameChildType type : arr)
        {
            getTodayABStatus(maps, type);
        }

        GameChildType gameChildType = arr[0];

        mNewLotteryOrderService.statsAllMemberByTime(gameChildType, beginTime, endTime, new Callback<GameBusinessDay>() {
            @Override
            public void execute(GameBusinessDay o) {

                try {
                    // 全部汇总
                    totalStatus.increAmount(o.getTotalBetAmount(), o.getWinAmount(), o.getFeemoney());
                    long winCount = 0;
                    if(OrderTxStatus.REALIZED.getKey().equalsIgnoreCase(o.getStatus()))
                    {
                        winCount = o.getTotalRecordCount();
                    }

                    totalStatus.increCount(o.getTotalRecordCount(), winCount);

//                    GameChildType  gameType = GameChildType.getType(gameChildType);
                    TodayGameReport status = getTodayABStatus(maps, gameChildType);

                    status.increAmount(o.getTotalBetAmount(), o.getWinAmount(), o.getFeemoney());
                    status.increCount(o.getTotalRecordCount(), winCount);
                } catch (Exception e) {
                    LOG.error("handle error: ", e);
                }

            }
        });

        //
        savePlatformToCache(overviewType, maps);
    }


    private TodayGameReport getTodayLotteryRgStatus(Map<String, TodayGameReport> maps, LotteryRGType rgType)
    {
        TodayGameReport status = maps.get(rgType.getTitle());
        if(status == null)
        {
            status = new TodayGameReport();
            status.setTitle(rgType.getTitle() + " 汇总");
            maps.put(rgType.getTitle(), status);
        }
        return status;
    }

    private TodayGameReport getTodayABStatus(Map<String, TodayGameReport> maps, GameChildType type)
    {
        TodayGameReport status = maps.get(type.getTitle());
        if(status == null)
        {
            status = new TodayGameReport();
            status.setTitle(type.getTitle() + " 汇总");
            maps.put(type.getTitle(), status);
        }
        return status;
    }

    public <T> T getCache(OverviewType type, Class<T> clazz)
    {
        String cachekey = OverviewCacheHelper.createItemCacheKey(type);
        T model = CacheManager.getInstance().getObject(cachekey, clazz);
        return model;
    }

    public <T> T getAgentStatsInfoCache(OverviewType type, String username, Class<T> clazz)
    {
        String cachekey = OverviewCacheHelper.createAgentItemCacheKey(type, username);
        T model = CacheManager.getInstance().getObject(cachekey, clazz);
        return model;
    }

    /**
     * 平台相关统计信息
     * @param type
     * @param value
     */
    public void savePlatformToCache(OverviewType type, Object value)
    {
        String cachekey = OverviewCacheHelper.createItemCacheKey(type);
        CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(value), -1);
    }

    /**
     * 代理相关信息
     * @param type
     * @param value
     */
    public void saveAgentInfoToCache(OverviewType type, String username, Object value)
    {
        String cachekey = OverviewCacheHelper.createAgentItemCacheKey(type, username);
        CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(value), -1);
    }

    private static PassportStatsInfo getAgentStatsInfo(Map<String, PassportStatsInfo> maps, String username)
    {
        PassportStatsInfo statsInfo = maps.get(username);
        if(statsInfo == null)
        {
            statsInfo = new PassportStatsInfo();
            maps.put(username, statsInfo);
        }
        return statsInfo;
    }



}
