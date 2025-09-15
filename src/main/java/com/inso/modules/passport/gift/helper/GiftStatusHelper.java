package com.inso.modules.passport.gift.helper;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.mq.MQManager;
import com.inso.framework.mq.MQSupport;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.GameBetItemType;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.MyLotteryBetRecordCache;
import com.inso.modules.game.lottery_game_impl.btc_kline.model.BTCKlineType;
import com.inso.modules.game.lottery_game_impl.rg2.model.RedGreen2Type;
import com.inso.modules.game.lottery_game_impl.turntable.model.TurnTableType;
import com.inso.modules.game.rg.model.LotteryRGType;
import com.inso.modules.game.rocket.model.RocketType;
import com.inso.modules.passport.gift.logical.GiftManager;
import com.inso.modules.passport.gift.model.GiftPeriodType;
import com.inso.modules.passport.gift.model.GiftTargetType;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class GiftStatusHelper {

    private static Log LOG = LogFactory.getLog(GiftStatusHelper.class);

    private static MQSupport mq = MQManager.getInstance().getMQ(MQManager.MQType.REDIS);
    private static final String QUEUE_NAME = GiftStatusHelper.class.getName();

    private static final String ROOT_CACHE = GiftStatusHelper.class.getName();

    private static final String DAY_CACHE = ROOT_CACHE + "_day_";
    private static final String WEEK_CACHE = ROOT_CACHE + "_week_";

    public static int EXPIRES = CacheManager.EXPIRES_DAY * 2;
    public static int EXPIRES_WEEK = CacheManager.EXPIRES_WEEK + 3600;

    private long mLastRefreshTime = -1;

    private DateTime mDateTime = new DateTime();

    private GiftManager mGiftManager;

    private interface MyInternal {
        public GiftStatusHelper mgr = new GiftStatusHelper();
    }

    private GiftStatusHelper()
    {
        this.mGiftManager = SpringContextUtils.getBean(GiftManager.class);
    }

    public static GiftStatusHelper getInstance()
    {
        return MyInternal.mgr;
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

                    String username = jsonObject.getString(MyLotteryBetRecordCache.KEY_USERNAME);
                    if(StringUtils.isEmpty(username))
                    {
                        return;
                    }

                    String betItem = jsonObject.getString(MyLotteryBetRecordCache.KEY_BET_ITEM);
                    if(StringUtils.isEmpty(betItem))
                    {
                        return;
                    }

                    BigDecimal betAmount = jsonObject.getBigDecimal(MyLotteryBetRecordCache.KEY_BET_AMOUNT);
                    if(betAmount == null)
                    {
                        return;
                    }

                    GameChildType type = GameChildType.getType(jsonObject.getString(MyLotteryBetRecordCache.KEY_LOTTERY_TYPE));
                    if(type == null)
                    {
                        return;
                    }

                    save(type, username, betItem, betAmount);
                } catch (Exception e) {
                    LOG.error("handle error:", e);
                }
            }
        });
    }

    private void refresh()
    {
        long ts = System.currentTimeMillis();
        if(mLastRefreshTime != -1 && ts - mLastRefreshTime <= 5_000)
        {
            return;
        }

        this.mLastRefreshTime = ts;
        this.mDateTime = new DateTime(ts);
    }

    public static void sendMessage(GameChildType gameType, String username, String betItem, BigDecimal amount)
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(MyLotteryBetRecordCache.KEY_LOTTERY_TYPE, gameType.getKey());
        jsonObject.put(MyLotteryBetRecordCache.KEY_USERNAME, username);
        jsonObject.put(MyLotteryBetRecordCache.KEY_BET_ITEM, betItem);
        jsonObject.put(MyLotteryBetRecordCache.KEY_BET_AMOUNT, amount);

        mq.sendMessage(QUEUE_NAME, jsonObject.toJSONString());
    }

    public void save(GameChildType gameType, String username, String betItem, BigDecimal amount)
    {
        if(!gameType.autoBoot())
        {
            // 外部游戏关闭记录
            return;
        }
        GameBetItemType.MyCommonBetItemType betItemType = null;
        if(gameType == TurnTableType.ROULETTE)
        {
            betItemType = GameBetItemType.MyCommonBetItemType.NUMBER;
//            TurntableBetItemType tmpBetItemType = TurntableBetItemType.getType(betItem);
//            betItemType = tmpBetItemType.getFollowBetItem();
        }
        else if(gameType == RocketType.CRASH || gameType instanceof LotteryRGType || gameType instanceof RedGreen2Type)
        {
            betItemType = GameBetItemType.MyCommonBetItemType.NUMBER;
        }
        else
        {
            betItemType = GameBetItemType.MyCommonBetItemType.getType(betItem);
        }
        if(betItemType == null)
        {
            if(!gameType.enableBetNumber())
            {
                return;
            }

            betItemType = GameBetItemType.MyCommonBetItemType.NUMBER;
        }

//        GiftConfigInfo configInfo = mGiftManager.getByKey(betItemType.getGiftTargetType());
//        if(configInfo == null)
//        {
//            return;
//        }

        refresh();

        String periodCache = DAY_CACHE + mDateTime.getDayOfYear();

        // total-bet
        String cacheKey = periodCache + username + betItemType.getGiftTargetType().getKey();
        saveByKey(cacheKey, amount, EXPIRES);

        // 总金额
        GiftTargetType totalBetTargetType = GiftTargetType.BET_TURNOVER;
        String totalBetCacheKey = periodCache + username + totalBetTargetType.getKey();
        saveByKey(totalBetCacheKey, amount, EXPIRES);

//        if(betItemType != GameBetItemType.MyCommonBetItemType.NUMBER)
//        {
//            return;
//        }

        if(gameType == RocketType.CRASH)
        {
            return;
        }

        int rsBetBumber = StringUtils.asInt(betItem, -1);
//        if( rsBetBumber < 0)
//        {
//            return;
//        }

        boolean isSmallBig = GameBetItemType.MyCommonBetItemType.Small == betItemType || GameBetItemType.MyCommonBetItemType.Big == betItemType;
        boolean isOddEven = GameBetItemType.MyCommonBetItemType.Odd == betItemType || GameBetItemType.MyCommonBetItemType.Even == betItemType;

        BigDecimal halfStatusAmount = amount.divide(BigDecimalUtils.DEF_2, 2, RoundingMode.HALF_UP);
        BigDecimal statusAmount2_5 = amount.divide(BigDecimalUtils.DEF_5, 2, RoundingMode.HALF_UP).multiply(BigDecimalUtils.DEF_2);
        BigDecimal statusAmount3_5 = amount.subtract(statusAmount2_5);

        // 大小
        if( (rsBetBumber >= 0 && rsBetBumber <= 4) || isOddEven)
        {
            BigDecimal statusAmount = halfStatusAmount;
            // 0dd    1 3 5 7 9                    1 3
            if(GameBetItemType.MyCommonBetItemType.Odd == betItemType)
            {
                statusAmount = statusAmount2_5;
            }
            else if(GameBetItemType.MyCommonBetItemType.Even == betItemType)
            {
                //0 2 4 6 8
                statusAmount = statusAmount3_5;
            }
            String singleBetItem = GameBetItemType.MyCommonBetItemType.Small.getGiftTargetType().getKey();
            String dayCachekeyByNumber = periodCache + username + singleBetItem;
            saveByKey(dayCachekeyByNumber, statusAmount, EXPIRES);

        }

        if(rsBetBumber > 4 || isOddEven)
        {
            BigDecimal statusAmount = halfStatusAmount;
            // 1 3 5 7 9 = > 5 7 9
            if(GameBetItemType.MyCommonBetItemType.Odd == betItemType)
            {
                statusAmount = statusAmount3_5;
            }
            else if(GameBetItemType.MyCommonBetItemType.Even == betItemType)
            {
                // 0 2 4 6 8 => 6 8
                statusAmount = statusAmount2_5;
            }

            String singleBetItem = GameBetItemType.MyCommonBetItemType.Big.getGiftTargetType().getKey();
            String dayCachekeyByNumber = periodCache + username + singleBetItem;
            saveByKey(dayCachekeyByNumber, statusAmount, EXPIRES);

        }

        // 单双
        if( (rsBetBumber >= 0 && rsBetBumber % 2 != 0) || isSmallBig)
        {
            // 0 2 4 6 8
            BigDecimal statusAmount = halfStatusAmount;
            if(GameBetItemType.MyCommonBetItemType.Small == betItemType)
            {
                // 0 1 2 3 4 = > 1 3
                statusAmount = statusAmount2_5;
            }
            else if(GameBetItemType.MyCommonBetItemType.Big == betItemType)
            {
                // 5 6 7 8 9 => 5 7 9
                statusAmount = statusAmount3_5;
            }

            String singleBetItem = GameBetItemType.MyCommonBetItemType.Odd.getGiftTargetType().getKey();
            String dayCachekeyByNumber = periodCache + username + singleBetItem;
            saveByKey(dayCachekeyByNumber, statusAmount, EXPIRES);
        }

        if( (rsBetBumber >= 0 && rsBetBumber % 2 == 0) || isSmallBig)
        {
            // 0 2 4 6 8
            BigDecimal statusAmount = halfStatusAmount;
            if(GameBetItemType.MyCommonBetItemType.Small == betItemType)
            {
                // 0 1 2 3 4 = > 0 2 4
                statusAmount = statusAmount3_5;
            }
            else if(GameBetItemType.MyCommonBetItemType.Big == betItemType)
            {
                // 5 6 7 8 9 => 6 8
                statusAmount = statusAmount2_5;
            }

            String singleBetItem = GameBetItemType.MyCommonBetItemType.Even.getGiftTargetType().getKey();
            String dayCachekeyByNumber = periodCache + username + singleBetItem;
            saveByKey(dayCachekeyByNumber, statusAmount, EXPIRES);

        }
    }

    private void saveByKey(String cachekey, BigDecimal amount, int expires)
    {

        BigDecimal cacheValue = CacheManager.getInstance().getObject(cachekey, BigDecimal.class);
        if(cacheValue == null)
        {
            cacheValue = amount;
        }
        else
        {
            cacheValue = cacheValue.add(amount);
        }
        CacheManager.getInstance().setString(cachekey, cacheValue.toString(), expires);
    }


    public BigDecimal getAmount(GiftPeriodType periodType, DateTime dateTime, String username, String targetType)
    {
        String cachekey = null;
        if(periodType == GiftPeriodType.Day)
        {
            cachekey = DAY_CACHE + dateTime.getDayOfYear() + username + targetType;
        }
        else
        {
            cachekey = WEEK_CACHE + dateTime.getWeekOfWeekyear() + username + targetType;
        }

        BigDecimal amount = CacheManager.getInstance().getObject(cachekey, BigDecimal.class);
        if(amount == null)
        {
            amount = BigDecimal.ZERO;
        }
        return amount;
    }

    public static void main(String[] args) {

        DateTime dateTime = new DateTime();

        GameChildType gameChildType = BTCKlineType.BTC_KLINE_1MIN;
        String betItem = GameBetItemType.MyCommonBetItemType.Small.getKey();
        String username = "u1";
        GiftStatusHelper.getInstance().save(gameChildType, username, betItem, new BigDecimal(1));

        GiftTargetType targetType = GiftTargetType.BET_SMALL;
        BigDecimal value = GiftStatusHelper.getInstance().getAmount(GiftPeriodType.Week, dateTime, username, targetType.getKey());

        System.out.println(value);

    }


}
