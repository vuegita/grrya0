package com.inso.modules.game;


import com.alibaba.fastjson.JSONObject;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.mq.MQManager;
import com.inso.framework.mq.MQSupport;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class GlobalBetRecordManager {

    private static MQSupport mq = MQManager.getInstance().getMQ(MQManager.MQType.REDIS);

    private static final String ROOT_CACHE = GlobalBetRecordManager.class.getName();
    private static final String QUEUE_NAME = GlobalBetRecordManager.class.getName();

    private ConcurrentLinkedQueue<JSONObject> mQueue;

    private long mLatestRefresTime = -1;

    private AtomicInteger count = new AtomicInteger();

    private interface MyInternal {
        public GlobalBetRecordManager mgr = new GlobalBetRecordManager();
    }

    private GlobalBetRecordManager()
    {
        List rsList = CacheManager.getInstance().getList(ROOT_CACHE, JSONObject.class);
        if(rsList != null)
        {
            this.mQueue = new ConcurrentLinkedQueue<>(rsList);
        }
        else
        {
            this.mQueue = new ConcurrentLinkedQueue<>();
        }

        count.set(mQueue.size());
    }

    public static GlobalBetRecordManager getInstance()
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
                if(StringUtils.isEmpty(jsonString))
                {
                    return;
                }

                JSONObject jsonObject = FastJsonHelper.toJSONObject(jsonString);

                if(jsonObject == null)
                {
                    return;
                }

                addRecord(jsonObject);
            }
        });
    }

    public void addRecord(JSONObject jsonObject)
    {
        int rsCount = count.get();
        if(rsCount >= 10)
        {
            while (rsCount >= 11)
            {
                mQueue.poll();
                rsCount = count.decrementAndGet();
            }
        }
        count.incrementAndGet();
        mQueue.add(jsonObject);
//        long ts = System.currentTimeMillis();
//        if(ts == -1 || ts - mLatestRefresTime > 60_000)
//        {
//            CacheManager.getInstance().setString(ROOT_CACHE, FastJsonHelper.jsonEncode(mQueue));
//        }
    }

    public Collection getDataList()
    {
        return mQueue;
    }

    public static void sendMessage(JSONObject jsonObject)
    {
        mq.sendMessage(QUEUE_NAME, jsonObject.toJSONString());
    }


    public void test()
    {
        for(int i = 0; i < 100; i ++)
        {
            addRecord(new JSONObject());
        }

    }

    public static void main(String[] args) {
        GlobalBetRecordManager mgr = GlobalBetRecordManager.getInstance();

        mgr.test();
        System.out.println(FastJsonHelper.jsonEncode(mgr.mQueue));
    }

}
