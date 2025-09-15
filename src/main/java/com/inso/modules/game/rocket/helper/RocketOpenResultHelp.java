package com.inso.modules.game.rocket.helper;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.*;
import org.apache.commons.compress.utils.Lists;

import java.util.Date;
import java.util.List;

public class RocketOpenResultHelp {

    private int capacity = 5;
    private List<JSONObject> mBackupList = Lists.newArrayList();

    private static final String KEY_VALUE = "value";
    private static final String KEY_TIME = "time";

    private static final String ROOT_CACHE = RocketOpenResultHelp.class.getName();

    private static final String ISSUE_OPEN_RESULT = ROOT_CACHE + "_issue_open_result_";

    private static final String ISSUE_STATUS_DATA = ROOT_CACHE + "_issue_status_data_";


    private interface MyInternal {
        public RocketOpenResultHelp mgr = new RocketOpenResultHelp();
    }

    private RocketOpenResultHelp()
    {
    }

    public void init()
    {
        if(!mBackupList.isEmpty())
        {
            return;
        }

        for(int i = 0; i < capacity; i ++)
        {
            JSONObject item = new JSONObject();
            mBackupList.add(item);
        }
    }

    public String createAndGetAccessKey()
    {
        String cachekey = ROOT_CACHE + "_access_key";
        String uuid = UUIDUtils.getUUID();
        CacheManager.getInstance().setString(cachekey, uuid, 60);
        return uuid;
    }

    public boolean existAccessKey(String key)
    {
        if(StringUtils.isEmpty(key))
        {
            return false;
        }
        String cachekey = ROOT_CACHE + "_access_key";
        String value = CacheManager.getInstance().getString(cachekey);
        return key.equalsIgnoreCase(value);
    }

    public static RocketOpenResultHelp getInstance()
    {
        return MyInternal.mgr;
    }


//    public void create(String openResult, String issue, DateTime dateTime)
//    {
//        mRunningList.clear();
//
//        try {
//            float openResultValue = StringUtils.asFloat(openResult);
//
//            int index = 0;
//            DateTime fireTime = dateTime;
//
//            if(openResultValue > 7)
//            {
//                addRandomItem(index ++, 1.5f, 1.99f, fireTime);
//                addRandomItem(index ++, 2.0f, 2.99f, fireTime);
//                addRandomItem(index ++, 3.0f, 5.49f, fireTime);
//                addRandomItem(index ++, 5.5f, 7f, fireTime);
//                addSolidItem(index, openResultValue, fireTime);
//                return;
//            }
//
//            if(openResultValue > 6)
//            {
//                addRandomItem(index ++, 1.5f, 1.99f, fireTime);
//                addRandomItem(index ++, 2.0f, 2.99f, fireTime);
//                addRandomItem(index ++, 3.0f, 3.99f, fireTime);
//                addRandomItem(index ++, 4.0f, 5.5f, fireTime);
//                addSolidItem(index, openResultValue, fireTime);
//                return;
//            }
//
//            if(openResultValue > 5)
//            {
//                addRandomItem(index ++, 1.5f, 1.99f, fireTime);
//                addRandomItem(index ++, 2.0f, 2.99f, fireTime);
//                addRandomItem(index ++, 3.0f, 3.99f, fireTime);
//                addRandomItem(index ++, 4.0f, 4.99f, fireTime);
//                addSolidItem(index, openResultValue, fireTime);
//                return;
//            }
//
//            if(openResultValue > 4)
//            {
//                addRandomItem(index ++, 1.4f, 1.99f, fireTime);
//                addRandomItem(index ++, 2.0f, 2.99f, fireTime);
//                addRandomItem(index ++, 3.0f, 3.99f, fireTime);
//                addSolidItem(index, openResultValue, fireTime);
//                return;
//            }
//
//            if(openResultValue > 3)
//            {
//                addRandomItem(index ++, 1.3f, 1.99f, fireTime);
//                addRandomItem(index ++, 2.0f, 2.99f, fireTime);
//                addSolidItem(index, openResultValue, fireTime);
//                return;
//            }
//
//            if(openResultValue > 2)
//            {
//                addRandomItem(index ++, 1.0f, 1.99f, fireTime);
//                addSolidItem(index, openResultValue, fireTime);
//                return;
//            }
//
//            if(openResultValue > 1.5)
//            {
//                addRandomItem(index ++, 1.0f, 1.49f, fireTime);
//                addSolidItem(index, openResultValue, fireTime);
//                return;
//            }
//
//            addSolidItem(index, openResultValue, fireTime);
//        } catch (Exception e) {
//        } finally {
//            saveCache(issue);
//        }
//
//    }

//    private void addRandomItem(int index, float start, float end, DateTime ts)
//    {
//        int startIndex = (int)(start * 100);
//        int endIndex = (int)(end * 100);
//
//        int randomValue = RandomUtils.nextInt(endIndex - startIndex) + startIndex;
//        float rsValue = randomValue / 100f;
//
//        addSolidItem(index, rsValue, ts);
//    }
//
//    private void addSolidItem(int index, float value, DateTime ts)
//    {
//        DateTime fireTime = ts;
//        if(index > 0)
//        {
//            fireTime = ts.plusSeconds(index);
//        }
//        JSONObject item = mBackupList.get(index);
//        item.put(KEY_VALUE, value);
//        item.put(KEY_TIME, fireTime.getMillis());
//        mRunningList.add(item);
//    }

    /**
     * 保存过程状态数据
     */
    public static void saveIssueProcessStatusData(String openResult, String prcessResult)
    {

    }

    public static void saveOpenResult(String issue, String openResult)
    {
        String cachekey = ISSUE_OPEN_RESULT + issue;
        CacheManager.getInstance().setString(cachekey, openResult, CacheManager.EXPIRES_DAY * 2);
    }

    public static String getOpenResult(String issue)
    {
        String cachekey = ISSUE_OPEN_RESULT + issue;
        String openResult = CacheManager.getInstance().getString(cachekey);
        return openResult;
    }

    public static float getRandomOpenResult(float start, float end)
    {
        return getRandomOpenResult(start, end, false);
    }
    public static float getRandomOpenResult(float start, float end, boolean first)
    {
        int startIndex = (int)(start * 100);
        int endIndex = (int)(end * 100);
        int randomValue = RandomUtils.nextInt(endIndex - startIndex) + startIndex + 5;
        if(first && randomValue >= endIndex)
        {
            randomValue = endIndex - 2;
            if(RandomUtils.nextBoolean())
            {
                randomValue --;
            }
        }
        float rsValue = randomValue / 100f;
        return rsValue;
    }

    public static float getMaxFloat(float start)
    {
        if(start < 1.1)
        {
            return 1.12f;
        }

        if(start < 1.2)
        {
            return 1.25f;
        }

        if(start < 1.25)
        {
            return 1.29f;
        }

        if(start < 1.3)
        {
            return 1.35f;
        }

        if(start < 1.35)
        {
            return 1.39f;
        }

        if(start < 1.4)
        {
            return 1.45f;
        }
        if(start < 1.45)
        {
            return 1.49f;
        }

        if(start < 1.5)
        {
            return 1.55f;
        }
        if(start < 1.55)
        {
            return 1.59f;
        }

        if(start < 1.6)
        {
            return 1.69f;
        }
        if(start < 1.7)
        {
            return 1.79f;
        }
        if(start < 1.8)
        {
            return 1.89f;
        }
        if(start < 1.9)
        {
            return 1.99f;
        }
        if(start < 2.0)
        {
            return 2.09f;
        }
        if(start < 2.1)
        {
            return 2.19f;
        }
        if(start < 2.3)
        {
            return 2.39f;
        }
        if(start < 2.5)
        {
            return 2.59f;
        }
        if(start < 2.8)
        {
            return 2.89f;
        }

        if(start < 3.0)
        {
            return 3.09f;
        }

        if(start < 3.5)
        {
            return 3.59f;
        }

        if(start < 4.0)
        {
            return 4.09f;
        }

        if(start < 6.0)
        {
            return 6.09f;
        }

        if(start < 8.0)
        {
            return 8.09f;
        }

        if(start < 10.0)
        {
            return 10.09f;
        }

        if(start < 15.0)
        {
            return 15.09f;
        }

        if(start < 20.0)
        {
            return 20.09f;
        }

        if(start < 30.0)
        {
            return 30.09f;
        }

        if(start < 50.0)
        {
            return 50.09f;
        }

        if(start < 80.0)
        {
            return 80.09f;
        }
        return 100;
    }



    public static void main(String[] args) {
        RocketOpenResultHelp mgr = RocketOpenResultHelp.getInstance();
        mgr.init();

        String issue = "1";

        String dataStr = "2023-02-16 12:12:12";
        Date pdate = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, dataStr);


        float maxOpenResult = getMaxFloat(0.95f);

        for(int i = 0 ; i < 100; i ++)
        {
            float nextResult = getRandomOpenResult(0.95f, maxOpenResult, true);
            System.out.println(nextResult);
            ThreadUtils.sleep(300);
        }




    }

}
