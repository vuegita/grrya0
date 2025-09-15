package com.inso.modules.game;

import com.alibaba.druid.util.LRUCache;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.*;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.game.lottery_game_impl.rg2.model.RedGreen2Type;
import com.inso.modules.game.rg.model.LotteryRGType;
import com.inso.modules.game.rocket.model.RocketType;
import com.inso.modules.passport.MyConstants;
import com.inso.modules.passport.user.model.UserInfo;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 当期投注记录
 */
public class MyLotteryBetRecordCache {

    private Log LOG = LogFactory.getLog(this.getClass());

    // 和
//    private static final int DEFAULT_EXPIRES = RGPeriodStatus.EXPIRES / 2;

    private String DEFAULT_ALL_BET_CACHE_KEY = MyConstants.DEFAULT_GAME_MODULE_NAME + this.getClass().getName() + "-all-user-";
    private String DEFAULT_USER_BET_CACHE_KEY = MyConstants.DEFAULT_GAME_MODULE_NAME + this.getClass().getName() + "-user-";
//    private static String DEFAULT_ROBOT_BET_CACHE_KEY = MyConstants.DEFAULT_GAME_MODULE_NAME + BetRecordCache.class.getName() + "-robot-";

    public static final int PERIOD_EXPIRES = CacheManager.EXPIRES_DAY;

    private static String DEFAULT_STR = "*****";

    public static final String KEY_ORDER_NO = "orderno";
    public static final String KEY_ISSUE = "issue";
    public static final String KEY_OPEN_RESULT = "openResult"; // 开奖结果

//    public static final String KEY_CHILD_TYPE = "childType";
//    public static final String KEY_CHILD_NAME = "childName";

    public static final String KEY_LOTTERY_TYPE = "lotteryType";
    public static final String KEY_LOTTERY_NAME = "lotteryName";

    public static final String KEY_USERNAME = "username";
    public static final String KEY_FEEMONEY = "feemoney";
    public static final String KEY_BET_AMOUNT = "betAmount";
    public static final String KEY_WIN_AMOUNT = "winAmount";

    public static final String KEY_BET_ITEM = "betItem";
    public static final String KEY_IS_WIN = "isWin";
    public static final String KEY_CREATETIME = "createtime";

    public static final String KEY_AVATAR_INDEX = "avatarIndex";

    public static final String KEY_REF_EXTERNAL = "referenceExt";
    public static final String KEY_REF_INTERNAL = "referenceInt";


    private LRUCache<String, JSONObject> mAllRecordItemLRUCache =  new LRUCache<String, JSONObject>(50);
    private LRUCache<String, List> mAllRecordLRUCache = new LRUCache<String, List>(5);
    private LRUCache<String, List> mUserRecordLRUCache = new LRUCache<String, List>(350);

    private static long refreshTime = -1;


    private static final String mIgnoreGmail = "_gmail";

    private interface MyInternal {
        MyLotteryBetRecordCache mgr = new MyLotteryBetRecordCache();
    }
    private MyLotteryBetRecordCache()
    {
    }

    public static MyLotteryBetRecordCache getInstance()
    {
        return MyInternal.mgr;
    }


    /**
     * 加密用户名
     * @param username
     * @return
     */
    public static String encryUsername(String username)
    {
        if(StringUtils.isEmpty(username))
        {
            return StringUtils.getEmpty();
        }
        try {
            if(username.startsWith(UserInfo.DEFAULT_GAME_TEST_ACCOUNT))
            {
                username = "ep" + username;
            }
            int ignoreStrIdx = username.indexOf("_");
            if(ignoreStrIdx > 0)
            {
                username = username.substring(0, ignoreStrIdx);
            }
            int len = username.length();
            StringBuilder buffer = new StringBuilder();
            if(len >= 3)
            {
                buffer.append(username.substring(0, 3));
            }
            else
            {
                buffer.append(username);
            }

            buffer.append(DEFAULT_STR);
            int subLen = 3;
            if(subLen >= len)
            {
                subLen = subLen - 3;
            }
            buffer.append(username.substring(len - subLen, len));
            return buffer.toString();
        } catch (Exception e) {
            return StringUtils.getEmpty();
        }
    }

    public int getUserExpires()
    {
        return CacheManager.EXPIRES_DAY * 2;
    }


    public boolean updateBetItem(String issue, boolean robot, String username, GameChildType childType, String orderno, String newBetItem)
    {
//        JSONObject item = mAllRecordItemLRUCache.get(orderno);
//        if(item == null)
//        {
//            return;
//        }
//        item.put(KEY_BET_ITEM, newBetItem);

        boolean update = false;

        // all
        String cacheKey = createAllRecordCacheKey(childType, issue);
        List<JSONObject> rsList = getAllRecordListFromCache(false, childType, issue);
        if(!CollectionUtils.isEmpty(rsList))
        {
            for(JSONObject tmp : rsList)
            {
                String tmpOrder = tmp.getString(KEY_ORDER_NO);
                if(orderno.equalsIgnoreCase(tmpOrder))
                {
                    tmp.put(KEY_BET_ITEM, newBetItem);
                    update = true;
                    break;
                }
            }

            if(update)
            {
                CacheManager.getInstance().setString(cacheKey, FastJsonHelper.jsonEncode(rsList), PERIOD_EXPIRES);
                //LOG.info("cashout success for order " + orderno + ", betItem = " + newBetItem + ", issue = " + issue);
            }
            else
            {
                //LOG.info("not found order no ...");
            }
        }

        if(robot || StringUtils.isEmpty(username))
        {
            return update;
        }

        // user
        List<JSONObject> userRecordList = getUserRecordListFromCache(false, childType, username);
        if(CollectionUtils.isEmpty(userRecordList))
        {
            return update;
        }

        boolean upUser = false;
        for(JSONObject tmp : userRecordList)
        {
            String tmpOrder = tmp.getString(KEY_ORDER_NO);
            if(orderno.equalsIgnoreCase(tmpOrder))
            {
                tmp.put(KEY_BET_ITEM, newBetItem);
                upUser = true;
                break;
            }
        }

        if(upUser)
        {
            String userCacheKey = DEFAULT_USER_BET_CACHE_KEY + childType.getKey() + username;
            CacheManager.getInstance().setString(userCacheKey, FastJsonHelper.jsonEncode(userRecordList), PERIOD_EXPIRES);
        }

        return update;
    }

    public void saveLatestRecord(String issue, GameChildType childType)
    {
        List<JSONObject> rsList = getAllRecordListFromCache(false, childType, issue);
        if(CollectionUtils.isEmpty(rsList))
        {
           return;
        }

        String cacheKey = createAllRecordCacheKey(childType, issue);
        CacheManager.getInstance().setString(cacheKey, FastJsonHelper.jsonEncode(rsList), PERIOD_EXPIRES);
    }

    /**
     * 根据类型获取最新投注记录
     * @param childType
     * @return
     */
    public List<JSONObject> getAllRecordListFromCache(boolean forceCache, GameChildType childType, String issue)
    {
        String cacheKey = createAllRecordCacheKey(childType, issue);
        List<JSONObject> list = mAllRecordLRUCache.get(cacheKey);
        if(forceCache || list == null)
        {
            list = CacheManager.getInstance().getList(cacheKey, JSONObject.class);
            if(list == null)
            {
                if(forceCache)
                {
                    list = Collections.emptyList();
                }
                else
                {
                    list = Lists.newArrayList();
                }
            }
            mAllRecordLRUCache.put(cacheKey, list);
        }
        return list;
    }

    /**
     * 获取用户投注记录
     * @param childType
     * @param username
     * @return
     */
    public List<JSONObject> getUserRecordListFromCache(boolean forceCache, GameChildType childType, String username)
    {
        String cacheKey = createUserRecordCacheKey(childType, username);

        List<JSONObject> list = mUserRecordLRUCache.get(cacheKey);
        if(forceCache || list == null)
        {
            list = CacheManager.getInstance().getList(cacheKey, JSONObject.class);
            if(list == null)
            {
                list = Lists.newArrayList();
            }
            mUserRecordLRUCache.put(cacheKey, list);
        }
        return list;
    }

    public void updateUserRecord(boolean forceCache, String orderno, GameChildType childType, String username, String openResult, OrderTxStatus txStatus)
    {
        String cacheKey = createUserRecordCacheKey(childType, username);

        List<JSONObject> list = mUserRecordLRUCache.get(cacheKey);
        if(forceCache || list == null)
        {
            list = CacheManager.getInstance().getList(cacheKey, JSONObject.class);
            if(list == null)
            {
                list = Lists.newArrayList();
            }
            mUserRecordLRUCache.put(cacheKey, list);
        }

        if(CollectionUtils.isEmpty(list))
        {
            return;
        }

        for(JSONObject tmp : list)
        {
            String tmpOrderNo = tmp.getString(KEY_ORDER_NO);
            if(!orderno.equalsIgnoreCase(tmpOrderNo))
            {
                continue;
            }

            tmp.put(KEY_OPEN_RESULT, openResult);
            tmp.put(KEY_IS_WIN, OrderTxStatus.REALIZED == txStatus);
            CacheManager.getInstance().setString(cacheKey, FastJsonHelper.jsonEncode(list), PERIOD_EXPIRES);
            break;
        }

    }

    public boolean addRecord(boolean robot, String orderno, GameChildType childType, String issue, String username, BigDecimal winAmountIfRealized, BigDecimal betAmount, BigDecimal feemoney, String betItem, Date createtime)
    {
        try {
            JSONObject model = new JSONObject();
            if(!StringUtils.isEmpty(orderno))
            {
                model.put(KEY_ORDER_NO, orderno);
            }
            model.put(KEY_LOTTERY_TYPE, childType.getKey());
            if(childType instanceof LotteryRGType)
            {
                LotteryRGType lotteryRGType = (LotteryRGType) childType;
                model.put(KEY_LOTTERY_NAME, lotteryRGType.getRootTitle());
            }
            else if(childType instanceof RedGreen2Type)
            {
                RedGreen2Type lotteryRGType = (RedGreen2Type) childType;
                model.put(KEY_LOTTERY_NAME, lotteryRGType.getRootTitle());
            }
            else
            {
                model.put(KEY_LOTTERY_NAME, childType.getTitle());
            }

            model.put(KEY_ISSUE, issue);
            model.put(KEY_OPEN_RESULT, StringUtils.getEmpty());
            model.put(KEY_USERNAME, encryUsername(username));

            // 头像索引
            model.put(KEY_AVATAR_INDEX, RandomUtils.nextInt(8));

            if(feemoney != null)
            {
                model.put(KEY_FEEMONEY, feemoney);
            }

            if(winAmountIfRealized != null)
            {
                model.put(KEY_WIN_AMOUNT, winAmountIfRealized);
            }

            model.put(KEY_BET_AMOUNT, betAmount);
            model.put(KEY_BET_ITEM, StringUtils.getNotEmpty(betItem));

            if(createtime != null)
            {
                model.put(KEY_CREATETIME, DateUtils.convertString(createtime, DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS));
            }

            List list = null;
            synchronized (childType.getKey())
            {
                list = getAllRecordListFromCache(false, childType, issue);
                if(list.size() >= 10)
                {
                    list.remove(9);
                }
                list.add(0, model);
            }

            if(list != null && !list.isEmpty() && childType == RocketType.CRASH)
            {
                long ts = System.currentTimeMillis();
                if(refreshTime == -1 || ts - refreshTime > 3_000)
                {
                    String cacheKey = createAllRecordCacheKey(childType, issue);
                    CacheManager.getInstance().setString(cacheKey, FastJsonHelper.jsonEncode(list), PERIOD_EXPIRES);
                    refreshTime = ts;
                }
            }

            if(!StringUtils.isEmpty(orderno))
            {
                mAllRecordItemLRUCache.put(orderno, model);
            }

            // 单个用户缓存
            if(!robot)
            {
                addUserRecord(childType, username, model);
            }

            if(childType == RocketType.CRASH)
            {
                GlobalBetRecordManager.sendMessage(model);
            }
            else
            {
                GlobalBetRecordManager.getInstance().addRecord(model);
            }
            return true;
        } catch (Exception e) {
            LOG.error("handle add record error:", e);
        }
        return false;
    }

    private void addUserRecord(GameChildType childType, String username, JSONObject model)
    {
        List list = getUserRecordListFromCache(false, childType, username);
        if(list == null)
        {
            list = Lists.newArrayList();
        }
        if(list.size() >= 20)
        {
            list.remove(19);
        }
        list.add(0, model);

        String cacheKey = DEFAULT_USER_BET_CACHE_KEY + childType.getKey() + username;
        CacheManager.getInstance().setString(cacheKey, FastJsonHelper.jsonEncode(list), getUserExpires());
    }

    private String createUserRecordCacheKey(GameChildType childType, String username)
    {
        String cacheKey = DEFAULT_USER_BET_CACHE_KEY + childType.getKey() + username;
        return cacheKey;
    }

    private String createAllRecordCacheKey(GameChildType childType, String issue)
    {
        String cacheKey = DEFAULT_ALL_BET_CACHE_KEY + childType.getKey() + issue;
        return cacheKey;
    }

    public static void main(String[] args) {
        String username = "C_gmail";
        username = encryUsername(username);
        System.out.println(username);
    }

}
