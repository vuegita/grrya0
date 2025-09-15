package com.inso.modules.common;

import com.beust.jcommander.internal.Maps;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.cache.LRUCache;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.modules.common.model.BusinessType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.andar_bahar.service.ABOrderService;
import com.inso.modules.game.fruit.service.FruitOrderService;
import com.inso.modules.game.lottery_game_impl.btc_kline.model.BTCKlineType;
import com.inso.modules.game.lottery_game_impl.football.model.FootballType;
import com.inso.modules.game.lottery_game_impl.mines.model.MineType;
import com.inso.modules.game.lottery_game_impl.pg.model.PgGameType;
import com.inso.modules.game.lottery_game_impl.rg2.model.RedGreen2Type;
import com.inso.modules.game.lottery_game_impl.turntable.model.TurnTableType;
import com.inso.modules.game.model.GameCategory;
import com.inso.modules.game.rg.service.LotteryOrderService;
import com.inso.modules.game.rocket.model.RocketType;
import com.inso.modules.game.service.NewLotteryOrderService;
import com.inso.modules.report.GameBusinessStatsService;
import com.inso.modules.report.cache.GameBusinessDayCacheUtils;
import com.inso.modules.report.model.GameBusinessDay;
import com.inso.modules.report.service.GameBusinessDayService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 代理后台-平台概况
 */
@Component
public class AgentOverviewManager {

    private static Log LOG = LogFactory.getLog(AgentOverviewManager.class);

    private GameBusinessDay mDefaultGameBusinessDay = new GameBusinessDay();

    @Autowired
    private GameBusinessDayService mGameBusinessDayService;

    @Autowired
    private LotteryOrderService mLotteryOrderService;

    @Autowired
    private ABOrderService mABOrderService;

    @Autowired
    private FruitOrderService mFruitOrderService;

    @Autowired
    private NewLotteryOrderService mNewLotteryOrderService;

    public AgentOverviewManager()
    {
        mDefaultGameBusinessDay.init();
    }

    private LRUCache<String, GameBusinessDay> mLRUCache = new LRUCache<>(100);

    private long mLastRefreshTime = -1;
    private int mDayOfYear;


    public void doStats()
    {
        DateTime nowTime = new DateTime();
//        int dayOfYear = nowTime.getDayOfYear();
//
//        String todayString = nowTime.toString(DateUtils.TYPE_YYYY_MM_DD);
//        String beginTimeString = DateUtils.getBeginTimeOfDay(todayString);
//        String endTimeString = DateUtils.getEndTimeOfDay(todayString);
//
//        Map<String, GameBusinessDay> maps = Maps.newHashMap();
////
//        // 员工
//        mGameBusinessDayService.queryAllStaff(beginTimeString, endTimeString, new Callback<GameBusinessDay>() {
//            @Override
//            public void execute(GameBusinessDay businessDay) {
//
//                BusinessType businessType = BusinessType.getType(businessDay.getBusinessName());
//                String cacheKey = GameBusinessDayCacheUtils.createFindGameBusinessDayKey(dayOfYear, businessDay.getAgentid(), businessType);
//                CacheManager.getInstance().setString(cacheKey, FastJsonHelper.jsonEncode(businessDay), CacheManager.EXPIRES_DAY);
//
//            }
//        });
//
//        // 代理
//        for(Map.Entry<String, GameBusinessDay> entry : maps.entrySet())
//        {
//            GameBusinessDay businessDay = entry.getValue();
//            BusinessType businessType = BusinessType.getType(businessDay.getBusinessName());
//
//            String cacheKey = GameBusinessDayCacheUtils.createFindGameBusinessDayKey(dayOfYear, businessDay.getAgentid(), businessType);
//            CacheManager.getInstance().setString(cacheKey, FastJsonHelper.jsonEncode(businessDay), CacheManager.EXPIRES_DAY);
//        }

//        statsRedGreen(mLotteryOrderService, nowTime, GameCategory.LOTTERY_RG, null);
//        statsRedGreen(mABOrderService, nowTime, GameCategory.ANDAR_BAHAR, null);
//        statsRedGreen(mFruitOrderService, nowTime, GameCategory.FRUIT, null);

        // new
        statsRedGreen(mNewLotteryOrderService, nowTime, GameCategory.BTC_KLINE, BTCKlineType.BTC_KLINE_1MIN);
        statsRedGreen(mNewLotteryOrderService, nowTime, GameCategory.TURNTABLE, TurnTableType.ROULETTE);
        statsRedGreen(mNewLotteryOrderService, nowTime, GameCategory.ROCKET, RocketType.CRASH);
        statsRedGreen(mNewLotteryOrderService, nowTime, GameCategory.RED_GREEN, RedGreen2Type.PARITY);

        //
        statsRedGreen(mNewLotteryOrderService, nowTime, GameCategory.FOOTABALL, FootballType.Football);
        statsRedGreen(mNewLotteryOrderService, nowTime, GameCategory.Mines, MineType.Mines);
        statsRedGreen(mNewLotteryOrderService, nowTime, GameCategory.PG, PgGameType.PG_Fortune_Tiger);
    }

    private void statsRedGreen(GameBusinessStatsService gameBusinessStatsService, DateTime fireTime, GameCategory category, GameChildType gameChildType)
    {
        try {
            int dayOfYear = fireTime.getDayOfYear();
            DateTime fromTime = fireTime.withTime(0, 0, 0, 0);
            DateTime toTime = fireTime.withTime(23, 59, 59, 0);

            Map<String, GameBusinessDay> staffMaps = Maps.newHashMap();
            gameBusinessStatsService.statsAllMemberByTime(gameChildType, fromTime, toTime, new Callback<GameBusinessDay>() {
                @Override
                public void execute(GameBusinessDay model) {

                    OrderTxStatus txStatus = OrderTxStatus.getType(model.getStatus());
                    if(!(txStatus == OrderTxStatus.FAILED || txStatus == OrderTxStatus.REALIZED))
                    {
                        return;
                    }

                    if(model.getWinAmount() != null && model.getWinAmount().compareTo(BigDecimal.ZERO) > 0)
                    {
                        model.setWinCount(model.getTotalRecordCount());
                    }

                    if(model.getTotalBetAmount() != null && model.getTotalBetAmount().compareTo(BigDecimal.ZERO) > 0)
                    {
                        model.setBetAmount(model.getTotalBetAmount());
                        model.setBetCount(model.getTotalRecordCount());
                    }
                    increBusinessAndSave(staffMaps, model, model.getStaffid(), model.getStaffname(), dayOfYear, category);
                }
            });


            Map<String, GameBusinessDay> agentMaps = Maps.newHashMap();
            for(Map.Entry<String, GameBusinessDay> entry : staffMaps.entrySet())
            {
                GameBusinessDay model = entry.getValue();
                increBusinessAndSave(agentMaps, model, model.getAgentid(), model.getAgentname(), dayOfYear, category);
            }


            // 代理
            for(Map.Entry<String, GameBusinessDay> entry : agentMaps.entrySet())
            {
                GameBusinessDay businessDay = entry.getValue();
                String cacheKey = GameBusinessDayCacheUtils.createFindGameBusinessDayKey(dayOfYear, businessDay.getAgentid(), category);
                CacheManager.getInstance().setString(cacheKey, FastJsonHelper.jsonEncode(businessDay), CacheManager.EXPIRES_DAY);
            }
        } catch (Exception e) {
            LOG.error("handle statsRedGreen error:", e);
        }
    }

    private void increBusinessAndSave(Map<String, GameBusinessDay> maps, GameBusinessDay model, long statsUserid, String statsKey, int dayOfYear, GameCategory category)
    {
        GameBusinessDay tmpStaffCache = maps.get(statsKey);
        if(tmpStaffCache == null)
        {
            tmpStaffCache = new GameBusinessDay();
            tmpStaffCache.setAgentid(model.getAgentid());
            tmpStaffCache.setAgentname(model.getAgentname());
            tmpStaffCache.setStaffid(model.getStaffid());
            tmpStaffCache.setStaffname(model.getStaffname());

            maps.put(statsKey, tmpStaffCache);
        }

        tmpStaffCache.incre(model);
        String staffCacheKey = GameBusinessDayCacheUtils.createFindGameBusinessDayKey(dayOfYear, statsUserid, category);
        CacheManager.getInstance().setString(staffCacheKey, FastJsonHelper.jsonEncode(tmpStaffCache), CacheManager.EXPIRES_DAY);
    }

    public void incre(GameBusinessDay businessDay)
    {
       // refresh();

//        increByUser(businessDay, businessDay.getStaffid(), businessDay.getStaffname(), false);
//        increByUser(businessDay, businessDay.getAgentid(), businessDay.getAgentname(), true);

    }

//    private void increByUser(GameBusinessDay businessDay, long userid, String username, boolean isAgent)
//    {
//        BusinessType businessType = BusinessType.getType(businessDay.getBusinessName());
//
//        String staffCacheKey = GameBusinessDayCacheUtils.createFindGameBusinessDayKey(mDayOfYear, userid, businessType);
//
//        String key = mDayOfYear + userid + businessType.getKey();
//        GameBusinessDay cacheValue = mLRUCache.get(key);
//        boolean addLRU = false;
//        if(cacheValue == null)
//        {
//            cacheValue = CacheManager.getInstance().getObject(staffCacheKey, GameBusinessDay.class);
//            addLRU = true;
//        }
//        if(cacheValue == null)
//        {
//            cacheValue = createBusinessDay(userid, username, businessType, isAgent);
//            addLRU = true;
//        }
//        if(addLRU)
//        {
//            mLRUCache.put(key, cacheValue);
//        }
//
//        cacheValue.incre(businessDay);
//        CacheManager.getInstance().setString(staffCacheKey, FastJsonHelper.jsonEncode(cacheValue), CacheManager.EXPIRES_DAY);
//
//        //LOG.info(FastJsonHelper.jsonEncode(cacheValue));
//    }


    public GameBusinessDay getAgentBusinessDay(int dayOfYear, long userid, GameCategory category)
    {
        String cacheKey = GameBusinessDayCacheUtils.createFindGameBusinessDayKey(dayOfYear, userid, category);
        GameBusinessDay businessDay = CacheManager.getInstance().getObject(cacheKey, GameBusinessDay.class);
        if(businessDay == null)
        {
            businessDay = mDefaultGameBusinessDay;
        }
        return businessDay;
    }

    private GameBusinessDay createBusinessDay(long userid, String useranme, BusinessType businessType, boolean isAgent)
    {
        GameBusinessDay model = new GameBusinessDay();
        model.init();
        if(isAgent)
        {
            model.setAgentid(userid);
            model.setAgentname(useranme);
        }
        else
        {
            model.setStaffid(userid);
            model.setStaffname(useranme);
        }
        model.setBusinessCode(businessType.getCode());
        model.setBusinessName(businessType.getKey());
        return model;
    }


    private void refresh()
    {
        long t = System.currentTimeMillis();
        if(mLastRefreshTime > 0 && t -mLastRefreshTime <= 60000)
        {
            return;
        }

        this.mDayOfYear = DateTime.now().getDayOfYear();
        this.mLastRefreshTime = t;
    }

}
