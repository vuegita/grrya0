package com.inso.modules.game.lottery_game_impl;

import com.alibaba.druid.util.LRUCache;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.MyLotteryBetRecordCache;
import com.inso.modules.game.model.NewLotteryPeriodInfo;
import com.inso.modules.game.rocket.model.RocketType;
import com.inso.modules.game.service.NewLotteryOrderServiceImpl;
import com.inso.modules.game.service.NewLotteryPeriodService;

public class GameResultManager {

    private static final String ROOT_CACHE = GameResultManager.class.getName() + "_V4";

    private LRUCache<String, JSONObject> mLRUCache = new LRUCache<>(100);

    private NewLotteryPeriodService mNewLotteryPeriodService;

    private interface MyInternal {
        public GameResultManager mgr = new GameResultManager();
    }


    private GameResultManager()
    {
        this.mNewLotteryPeriodService = SpringContextUtils.getBean(NewLotteryPeriodService.class);
    }

    public static GameResultManager getInstance()
    {
        return MyInternal.mgr;
    }

    public JSONObject saveResult(String issue, String value, GameChildType type, String referenceExternal, String referenceInternal)
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(MyLotteryBetRecordCache.KEY_OPEN_RESULT, value);
        if(!StringUtils.isEmpty(referenceExternal))
        {
            jsonObject.put(MyLotteryBetRecordCache.KEY_REF_EXTERNAL, referenceExternal);
        }

        if(!StringUtils.isEmpty(referenceInternal))
        {
            jsonObject.put(MyLotteryBetRecordCache.KEY_REF_INTERNAL, referenceInternal);
        }
        String cachekey = ROOT_CACHE + type.getKey() + issue;
        if(CacheManager.getInstance().exists(cachekey))
        {
            return null;
        }
        CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(jsonObject), CacheManager.EXPIRES_DAY);
        return jsonObject;
    }

    public JSONObject getJsonResult(GameChildType type, String issue)
    {
        String key = type.getKey() + issue;
        JSONObject jsonObject = mLRUCache.get(key);
        if(jsonObject == null)
        {
            String cachekey = ROOT_CACHE + key;
            jsonObject = CacheManager.getInstance().getObject(cachekey, JSONObject.class);
            if(jsonObject == null)
            {
                jsonObject = loadFromDB(type, issue);
            }
            if(jsonObject != null && !jsonObject.isEmpty())
            {
                mLRUCache.put(key, jsonObject);
            }
        }
        return jsonObject;
    }

    public String getStringResult(GameChildType type, String issue)
    {
        JSONObject jsonObject = getJsonResult(type, issue);
        if(jsonObject != null)
        {
            return jsonObject.getString(MyLotteryBetRecordCache.KEY_OPEN_RESULT);
        }
        return null;
    }

    private JSONObject loadFromDB(GameChildType type, String issue)
    {
        if(mNewLotteryPeriodService == null)
        {
            return null;
        }

        if(type == RocketType.CRASH)
        {
            return null;
        }

        NewLotteryPeriodInfo periodInfo = mNewLotteryPeriodService.findByIssue(false, type, issue);
        if(periodInfo == null)
        {
            return null;
        }

        if(periodInfo.getEndtime().getTime() >= System.currentTimeMillis())
        {
            return null;
        }

        if(StringUtils.isEmpty(periodInfo.getOpenResult()))
        {
            return null;
        }

        String openResult = periodInfo.getOpenResult();
        String referenceExternal = periodInfo.getReferenceExternal();

        JSONObject jsonObject = saveResult(issue, openResult, type, referenceExternal, null);
        return jsonObject;
    }



}
