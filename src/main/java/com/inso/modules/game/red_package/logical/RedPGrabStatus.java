package com.inso.modules.game.red_package.logical;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.druid.util.LRUCache;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RedPackageUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.GameErrorResult;
import com.inso.modules.game.red_package.model.RedPCreatorType;
import com.inso.modules.game.red_package.model.RedPType;
import com.inso.modules.passport.MyConstants;

/**
 * 抢红包的状态
 */
public class RedPGrabStatus {

    private static Log LOG = LogFactory.getLog(RedPGrabStatus.class);

    public static final int EXPIRES = CacheManager.EXPIRES_DAY + CacheManager.EXPIRES_HOUR;

    /*** 当前整个红包体系状态 ***/
    private static final String CACHE_KEY_STATUS = MyConstants.DEFAULT_GAME_MODULE_NAME + "_red_package_period_grab_status_";

    /*** 用户状态抢到状态 ***/
    private static final String CACHE_KEY_USER = MyConstants.DEFAULT_GAME_MODULE_NAME + "_red_package_period_grab_user_";

    private static LRUCache<String, RedPGrabStatus> mLruCache = new LRUCache<>(100);

    private RedPType type;
    /*** 期号-红包id ***/
    private long issue;
    /*** 开盘时间 ***/
    private Date startTime;
    /*** 结束时间 ***/
    private Date endTime;
    /*** 是否是 ***/
    private boolean isInit = false;
    /*** 红包创建者 ***/
    private RedPCreatorType creatorType;
    /*** 创建人用户id- 如果是系统则为-1 ***/
    private long creatorUserid = -1;
    /*** 红包总金额 ***/
    private BigDecimal totalRPAmount;
    private BigDecimal remainAmount;
    private BigDecimal maxAmount;
    /*** 红包总数 ***/
    private long totalRPCount;
    /*** 当前已领红包数 ***/
    private long currentRPCount;
    /*** 外部赠送条件 ***/
    private BigDecimal externalLimitMinAmount;

    /*** 指定用户名 ***/
    private String specifyUserName;

//    public JSONObject getmGrabRedPUserMap() {
//        return mGrabRedPUserMap;
//    }
//
//    public void setmGrabRedPUserMap(JSONObject mGrabRedPUserMap) {
//        this.mGrabRedPUserMap = mGrabRedPUserMap;
//    }

    /*** 已抢到红包的人 index->username ***/
//    private JSONObject mGrabRedPUserMap;

    public static RedPGrabStatus loadCache(boolean purge, long issue)
    {
        String uniqueKey =  issue + StringUtils.getEmpty();
        String cachekey = CACHE_KEY_STATUS + uniqueKey;

        RedPGrabStatus status = mLruCache.get(uniqueKey);
        if(purge || status == null)
        {
            status = CacheManager.getInstance().getObject(cachekey, RedPGrabStatus.class);
        }

        if(status == null)
        {
            status = new RedPGrabStatus();
            status.setIssue(issue);
        }

        status.init();

        mLruCache.put(uniqueKey, status);
        return status;
    }

    public static RedPGrabStatus tryLoadCache(boolean purge, long id)
    {
        String uniqueKey =  id + StringUtils.getEmpty();
        String cachekey = CACHE_KEY_STATUS + uniqueKey;

        RedPGrabStatus status = mLruCache.get(uniqueKey);
        if(purge || status == null)
        {
            status = CacheManager.getInstance().getObject(cachekey, RedPGrabStatus.class);
        }
        if(status != null)
        {
            status.init();
        }
        return status;
    }

    public void saveCache()
    {
        this.isInit = true;
        String cachekey = CACHE_KEY_STATUS + issue;
        CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(this), EXPIRES);
    }

    /**
     * 抢红包- 红包金额减少
     * 由外部进行线程并发控制
     */
    public BigDecimal decreRPAmount(String username)
    {
        BigDecimal grapAmount = null;
        // 红包类型为现金红包，要实时计算金额
        if(type == RedPType.SIMPLE)
        {
            grapAmount = RedPackageUtils.getRandomAmount(remainAmount, this.totalRPCount - this.currentRPCount, maxAmount);
//            this.mGrabRedPUserMap.put(username + "_amount", grapAmount.toString());

            this.remainAmount = this.remainAmount.subtract(grapAmount);
        }
        // 固定红包
        else if(type == RedPType.SOLID || type == RedPType.SOLIDCODE)
        {
            grapAmount = totalRPAmount.divide(new BigDecimal(this.totalRPCount), 2, RoundingMode.DOWN);
//            this.mGrabRedPUserMap.put(username + "_amount", grapAmount.toString());
        }
        // 指定用户红包
        else if(type == RedPType.SPECIGY)
        {
            grapAmount = totalRPAmount.divide(new BigDecimal(this.totalRPCount), 2, RoundingMode.DOWN);
//            this.mGrabRedPUserMap.put(username + "_amount", grapAmount.toString());
        }
        // 充值红包
        else if(type == RedPType.RECHARGE)
        {
            grapAmount = totalRPAmount.divide(new BigDecimal(this.totalRPCount), 2, RoundingMode.DOWN);
//            this.mGrabRedPUserMap.put(username + "_amount", grapAmount.toString());
        }
        // 再写入缓存
        this.currentRPCount += 1;
//        this.mGrabRedPUserMap.put(generateGrabIndexKey(username), this.currentRPCount + StringUtils.getEmpty());

        // 保存用户抢完状态
        saveUserGrabStausInfo(username, grapAmount, this.currentRPCount);

        return grapAmount;
    }
    @JSONField(serialize = false, deserialize = false)
    public BigDecimal getWinAmount(String username){

        String cachekey = createUserStatusCacheKey(username);
        JSONObject jsonObject = CacheManager.getInstance().getObject(cachekey, JSONObject.class);

        if(jsonObject == null || jsonObject.isEmpty())
        {
            return BigDecimal.ZERO;
        }

        BigDecimal amount = jsonObject.getBigDecimal("amount");
        if(amount == null)
        {
            amount=BigDecimal.ZERO;
        }
        return amount;

//        String key=username + "_amount";
//        BigDecimal amount = this.mGrabRedPUserMap.getBigDecimal(key);
//        if(amount == null)
//        {
//            amount=BigDecimal.ZERO;
//        }
//       return amount;
    }

    private void saveUserGrabStausInfo(String username, BigDecimal amount, long count)
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("amount", amount);
        jsonObject.put("count", count);

        String cachekey = createUserStatusCacheKey(username);

        // 2倍缓存时间, 因为当前状态最长一天
        CacheManager.getInstance().setString(cachekey, jsonObject.toJSONString(), EXPIRES * 2);
    }

    private String createUserStatusCacheKey(String username)
    {
        String cachekey = CACHE_KEY_USER + username + issue;
        return cachekey;
    }


    /**
     * 抢到红包的人员
     * @return
     */
//    @JSONField(serialize = false, deserialize = false)
//    public List<Map<String, Object>> getWinList()
//    {
//        return null;
//        String amountEndKey = "_amount";
//        String indexEndKey = "_index";
//
//        String usernameKey = "username";
//        String amountKey = "amount";
//        String indexKey = "index";
//
//        List<Map<String, Object>> list = Lists.newArrayList();
//
//        int indexEndKeyLen = indexEndKey.length();
//        Set<String> keys = mGrabRedPUserMap.keySet();
//
//        boolean isAddAmount = false;
//        if(type == RedPType.SIMPLE || isFinish())
//        {
//            isAddAmount = true;
//        }
//        for(String key : keys)
//        {
//            if(key.endsWith(indexEndKey))
//            {
//                String value = mGrabRedPUserMap.getString(key);
//                int index = StringUtils.asInt(value);
//                int len = key.length() - indexEndKeyLen;
//                String username = key.substring(0, len);
//                Map<String, Object> model = Maps.newHashMap();
//
//                model.put(usernameKey, username);
//                model.put(indexKey, index);
//
//                if(isAddAmount)
//                {
//                    Object amount = mGrabRedPUserMap.get(username + amountEndKey);
//                    model.put(amountKey, amount);
//                }
//
//                list.add(model);
//            }
//        }
//
//        return list;
//    }

    @JSONField(serialize = false, deserialize = false)
    public boolean isFinish()
    {
        if(type == RedPType.SIMPLE)
        {
            // 对于普通红包所有的红包都抢完，表示结束
            if(this.currentRPCount >= this.totalRPCount)
            {
                return true;
            }
        }

        // 红包已过期
        if(System.currentTimeMillis() > endTime.getTime())
        {
            return true;
        }

        return false;
    }

    private void init()
    {
//        if(mGrabRedPUserMap == null)
//        {
//            mGrabRedPUserMap = new JSONObject();
//        }
    }

    private ErrorResult verifyTime()
    {
        long time = System.currentTimeMillis();
        // 红包的最后10s不能再抢
        if( !(time >= startTime.getTime() && time < endTime.getTime() - type.getDisableMillis()))
        {
            // 封盘
            return GameErrorResult.ERR_CURRENT_ISSUE_FINISH;
        }
        return SystemErrorResult.SUCCESS;
    }

    public ErrorResult verify(String username)
    {
        ErrorResult result =verifyTime();
        if(result != SystemErrorResult.SUCCESS)
        {
            return result;
        }

        String userStatusCachekey = createUserStatusCacheKey(username);
//        String key = generateGrabIndexKey(username);
        // 缓存存在表示已经抢过了
        if(CacheManager.getInstance().exists(userStatusCachekey))
        {
            // 已经抢到,不能再抢了
            return SystemErrorResult.ERR_EXIST;
        }

        // 所有红包已被抢空
        if(this.currentRPCount >= this.totalRPCount)
        {
            return SystemErrorResult.ERR_NODATA;
        }

        // 余额不足
        if(remainAmount.compareTo(BigDecimal.ZERO) <= 0)
        {
            return SystemErrorResult.ERR_NODATA;
        }

        if(type == RedPType.SPECIGY && !(username.equalsIgnoreCase(specifyUserName)))
        {
            return SystemErrorResult.ERR_CUSTOM;
        }

        return SystemErrorResult.SUCCESS;
    }

    private static String generateGrabIndexKey(String username)
    {
        return username + "_index";
    }


    /**
     * 开奖结束获取开奖结果从这里获取
     * @return
     */
//    public ABBetItemType getOpenResult()
//    {
//
//    }



    public RedPType getType() {
        return type;
    }

    public void setType(RedPType type) {
        this.type = type;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }


    public boolean isInit() {
        return isInit;
    }

    public void setInit(boolean init) {
        isInit = init;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public BigDecimal getTotalRPAmount() {
        return totalRPAmount;
    }

    public void setTotalRPAmount(BigDecimal totalRPAmount) {
        this.totalRPAmount = totalRPAmount;
        this.remainAmount = totalRPAmount;
    }



    public long getTotalRPCount() {
        return totalRPCount;
    }

    public void setTotalRPCount(long totalRPCount) {
        this.totalRPCount = totalRPCount;
    }


    public long getCurrentRPCount() {
        return currentRPCount;
    }

    public void setCurrentRPCount(long currentRPCount) {
        this.currentRPCount = currentRPCount;
    }

    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }


    public long getIssue() {
        return issue;
    }

    public void setIssue(long issue) {
        this.issue = issue;
    }

    public RedPCreatorType getCreatorType() {
        return creatorType;
    }

    public void setCreatorType(RedPCreatorType creatorType) {
        this.creatorType = creatorType;
    }

    public long getCreatorUserid() {
        return creatorUserid;
    }

    public void setCreatorUserid(long creatorUserid) {
        this.creatorUserid = creatorUserid;
    }

    public BigDecimal getRemainAmount() {
        return remainAmount;
    }

    public void setRemainAmount(BigDecimal remainAmount) {
        this.remainAmount = remainAmount;
    }

    public BigDecimal getExternalLimitMinAmount() {
        return externalLimitMinAmount;
    }

    public void setExternalLimitMinAmount(BigDecimal externalLimitMinAmount) {
        this.externalLimitMinAmount = externalLimitMinAmount;
    }

    public String getSpecifyUserName(){
        return specifyUserName;
    }
    public void setSpecifyUserName(String specifyUserName) {
        this.specifyUserName = specifyUserName;
    }
}
