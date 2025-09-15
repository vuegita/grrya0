package com.inso.modules.game.rocket.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.cache.LRUCache;
import com.inso.framework.utils.*;

public class RocketGameStatusInfo {

    private static final String ROOT_CACHE = RocketGameStatusInfo.class.getName();

    private String currentResult;
    private String openResult;

    private int totalBetCount;
    private int cashoutCount;

    private static String mSalt = "kljsdfsadf@fdsfj.com";

    private static long mLatestRefreshTime = -1;
    private static LRUCache<String, RocketGameStatusInfo> mLRUCache = new LRUCache<>(2);

    public static RocketGameStatusInfo loadFromCache(String issue)
    {
        RocketGameStatusInfo entity = mLRUCache.get(issue);
//        long ts = System.currentTimeMillis();
//        if(entity == null || mLatestRefreshTime == -1 || ts - mLatestRefreshTime > 1200)
//        {
//            String cachekey = ROOT_CACHE + issue;
//            entity = CacheManager.getInstance().getObject(cachekey, RocketGameStatusInfo.class);
//            if(entity != null)
//            {
//                mLRUCache.put(issue, entity);
//            }
//            mLRUCache.put(issue, entity);
//            mLatestRefreshTime = ts;
//        }
        return entity;
    }

    public void clear()
    {
        this.currentResult = StringUtils.getEmpty();
        this.openResult = StringUtils.getEmpty();
        this.totalBetCount = 0;
        this.cashoutCount = 0;
    }

    public void save(String issue)
    {
        mLRUCache.put(issue, this);
//        String cachekey = ROOT_CACHE + issue;
//        CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(this), CacheManager.EXPIRES_DAY * 2);
    }

    @JSONField(serialize = false, deserialize = false)
    public String getEncryptCurrentResult(String issue)
    {
        if(StringUtils.isEmpty(currentResult))
        {
            return StringUtils.getEmpty();
        }
        //System.out.println("固定salt = kljsdfsadf@fdsfj.com");
        String salt = MD5.encode(mSalt + issue).substring(0, 16);
        //System.out.println("salt = " + salt);
        String value = AESUtils.encrypt(currentResult, salt);
        return value;
    }

    public static String getStaticEncryptResult(String issue, String currentResult)
    {
        if(StringUtils.isEmpty(currentResult))
        {
            return StringUtils.getEmpty();
        }
        //System.out.println("固定salt = kljsdfsadf@fdsfj.com");
        String salt = MD5.encode(mSalt + issue).substring(0, 16);
        //System.out.println("salt = " + salt);
        String value = AESUtils.encrypt(currentResult, salt);
        return value;
    }

    @JSONField(serialize = false, deserialize = false)
    public String getEncryptOpenResult(String issue)
    {
        if(StringUtils.isEmpty(openResult))
        {
            return StringUtils.getEmpty();
        }
        //System.out.println("固定salt = kljsdfsadf@fdsfj.com");
        String salt = MD5.encode(mSalt + issue).substring(0, 16);
        //System.out.println("salt = " + salt);
        String value = AESUtils.encrypt(openResult, salt);
        return value;
    }

    @JSONField(serialize = false, deserialize = false)
    public String decryptResult(String issue, String encryValue)
    {
        if(StringUtils.isEmpty(encryValue))
        {
            return StringUtils.getEmpty();
        }
        String salt = MD5.encode(mSalt + issue).substring(0, 16);
        String value = AESUtils.decrypt(encryValue, salt);
        return value;
    }

    public String getCurrentResult() {
        return currentResult;
    }

    public void setCurrentResult(String currentResult) {
        this.currentResult = currentResult;
    }

    public String getOpenResult() {
        return openResult;
    }

    public void setOpenResult(String openResult) {
        this.openResult = openResult;
    }

    public int getTotalBetCount() {
        return totalBetCount;
    }

    public void setTotalBetCount(int totalBetCount) {
        this.totalBetCount = totalBetCount;
    }

    public int getCashoutCount() {
        return cashoutCount;
    }

    public void setCashoutCount(int cashoutCount) {
        this.cashoutCount = cashoutCount;
    }


    public static void main(String[] args) {
        String issue = "123";
        RocketGameStatusInfo statusInfo = new RocketGameStatusInfo();
        statusInfo.setCurrentResult("1.2");


        String encryptResult = statusInfo.getEncryptCurrentResult(issue);

//        System.out.println("issue = " + 123);
//        System.out.println(encryptResult);

        issue = "11202303020620";
        String decry = "wXUGa9ocnRrK+iJ+ZKWhAg==";
        System.out.println("issue = " + issue);
        System.out.println(statusInfo.decryptResult(issue, decry));

    }



}
