package com.inso.modules.passport.user.logical;

import com.alibaba.druid.util.LRUCache;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.*;
import com.inso.modules.common.config.PlarformConfig2;
import com.inso.modules.common.model.*;
import com.inso.modules.passport.business.model.DayPresentOrder;
import com.inso.modules.passport.business.model.PresentBusinessType;
import com.inso.modules.passport.business.service.DayPresentOrderService;
import com.inso.modules.passport.business.service.RechargeOrderService;
import com.inso.modules.passport.money.PayApiManager;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.user.model.InviteFriendStatus;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.service.ConfigService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 邀请好友管理器
 */
@Component
public class TodayInviteFriendManagerV2 {

    private static Log LOG = LogFactory.getLog(TodayInviteFriendManagerV2.class);

    private static final String DEFAULT_CACHE_KEY = TodayInviteFriendManagerV2.class.getName();

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private UserMoneyService mUserMoneyService;

    @Autowired
    private ConfigService mConfigService;

//    @Autowired
//    private BusinessOrderService mBusinessOrderService;

    @Autowired
    private PayApiManager mPayApiManager;

    @Autowired
    private DayPresentOrderService mPresentOrderService;

    @Autowired
    private RechargeOrderService mRechargeOrderService;



    private LRUCache<String, InviteFriendStatus> mLRUCache = new LRUCache<>(100);

    private PresentBusinessType mPresentBusinessType = PresentBusinessType.INVITE_DAY_NO_NEED_RECHARGE;

    private List<InviteKeyValue> mConfigItemList;

    private long mLastRefreshTime = - 1;

    private void refresh()
    {
        long ts = System.currentTimeMillis();
        if(mLastRefreshTime > 0 && ts - mLastRefreshTime <= 60_000)
        {
            return;
        }
        // 获取配置
        String configValue = mConfigService.getValueByKey(false, PlarformConfig2.ADMIN_APP_PLATFORM_USER_USER_INVITE_FRIEND_TASK_NO_NEED_RECHARGE.getKey());

        if(StringUtils.isEmpty(configValue))
        {
            this.mConfigItemList = Collections.emptyList();
            return;
        }

        this.mConfigItemList = Lists.newArrayList();
        String[] valueArray = StringUtils.split(configValue, '|');
        if(valueArray != null || valueArray.length > 0)
        {
            for(String item : valueArray)
            {
                int index = item.indexOf("=");
                String key = item.substring(0, index).trim();
                String value = item.substring(index + 1, item.length());

                int keyid = StringUtils.asInt(key);
                BigDecimal amount = StringUtils.asBigDecimal(value);
                if(keyid <= 0 || amount.compareTo(BigDecimal.ZERO) <= 0)
                {
                    this.mConfigItemList = Collections.emptyList();
                    return;
                }

                InviteKeyValue keyValue = new InviteKeyValue();
                keyValue.setKey(keyid);
                keyValue.setAmount(new BigDecimal(value));

                mConfigItemList.add(keyValue);
            }
        }
    }

    public void doTask(DateTime firDateTime)
    {
        LOG.info("Start Begin calc invite friend task v2 ....");
        Date startDate = firDateTime.toDate();
        Date endDate = firDateTime.toDate();

        Date beginTime = DateUtils.getStartOfDay(startDate);
        Date endTime = DateUtils.getEndOfDay(endDate);

        // 表示当前线程处理数据缓存id-由于用户数据日后会大，需要借助缓存
        String extraKey = UUIDUtils.getUUID();

        String repeatRegisterKey = "repeatRegister";

        String username = "epv8239886392";

        mUserAttrService.queryAllMember2(beginTime, endTime, new Callback<UserAttr>() {
            public void execute(UserAttr model) {
                String parentName = model.getParentname();

//                boolean isUser = username.equalsIgnoreCase(model.getParentname());
//                String reason = null;

                try {
                    if(StringUtils.isEmpty(parentName))
                    {
                        return;
                    }

                    UserInfo userInfo = mUserService.findByUsername(false, model.getUsername());
                    if(StringUtils.isEmpty(userInfo.getLastloginip()))
                    {
//                        reason = "Not Login" ;
                        return;
                    }

                    JSONObject remark = FastJsonHelper.toJSONObject(userInfo.getRemark());
                    if(remark == null || remark.isEmpty())
                    {
//                        reason = "Not Rermark" ;
                        return;
                    }

                    boolean repeatRegister = remark.getBooleanValue(repeatRegisterKey);
                    if(repeatRegister)
                    {
//                        reason = "Repeat" ;
                        return;
                    }

                    InviteFriendStatus status = getTmpInviteFriendTaskStatus(firDateTime, parentName, extraKey);
                    status.setInviteCount(status.getInviteCount() + 1);
                    saveTmpInviteFriendTaskStatus(firDateTime, parentName, status, extraKey);

//                    reason = "Invite count = " + status.getInviteCount();
                } catch (Exception e) {
                } finally {
//                    if(isUser)
//                    {
//                        LOG.info("reason = " + reason);
//                    }
                }
            }
        });

        // 更新缓存
        mUserAttrService.queryAllMember2(beginTime, endTime, new Callback<UserAttr>() {
            public void execute(UserAttr model) {

                String parentName = model.getParentname();
                if(StringUtils.isEmpty(parentName))
                {
                    return;
                }
                InviteFriendStatus status = getTmpInviteFriendTaskStatus(firDateTime, parentName, extraKey);
                if(status == null)
                {
                    return;
                }

                status.save(parentName, firDateTime, null, StringUtils.getEmpty(), mPresentBusinessType);
//                LOG.info("username = " + parentName + ", detail = " + FastJsonHelper.jsonEncode(status));
            }
        });

        LOG.info("End Begin calc invite friend task v2 ....");
    }

    /**
     * 赠送邀请好友并完成充值的赠送金额
     */
    public long receive(UserInfo userInfo)
    {
        refresh();
        if(CollectionUtils.isEmpty(mConfigItemList))
        {
            return 0;
        }
        BusinessType businessType = BusinessType.RECHARGE_ACTION_PRESENTATION;
        RemarkVO remarkVO = RemarkVO.create("invite friend presentation no need recharge");

        DateTime dateTime = DateTime.now();
        int weekOfWeekyear = dateTime.getDayOfYear();

        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
        PresentBusinessType presentBusinessType = PresentBusinessType.INVITE_DAY_NO_NEED_RECHARGE;
        FundAccountType accountType = FundAccountType.Spot;

        UserAttr model = mUserAttrService.find(false, userInfo.getId());


        String username = userInfo.getName();


        InviteFriendStatus status = getTmpInviteFriendTaskStatus(dateTime, userInfo.getName(), null);
        if(status == null)
        {
            return 0;
        }

        long inviteCount = status.getInviteCount();
        if(inviteCount <= 0)
        {
            return 0;
        }

        String presentLevelCacheKey = getPresentLevelCountCacheKey(username);

        long receivCount = 0;
        List<InviteKeyValue> rsList = mConfigItemList;
        for(InviteKeyValue item : rsList)
        {
            // 充值人数 > 配置完成充值人数 则赠送金额
            if(inviteCount < item.getKey())
            {
                continue;
            }
            String taskid = item.getKey() + StringUtils.getEmpty();

            String presentCacheKey = getPresentCacheKey(username, weekOfWeekyear, taskid);
            if(CacheManager.getInstance().exists(presentCacheKey))
            {
                receivCount = inviteCount;
                // 已赠送，则不在赠送
                //LOG.info("invite present amount, it has present  " + parentName);
                continue;
            }

            try {
                BigDecimal presentationAmount = item.getAmount();

                String msg = "Invite present no need recharge, from " + model.getUsername();
                remarkVO.setMesage(msg);

                String orderno = null;
                String outradeno = mPresentOrderService.generateOutTradeNo(model.getUserid(), presentBusinessType, taskid, dateTime);
                DayPresentOrder orderInfo = mPresentOrderService.find(MyEnvironment.isDev(), outradeno);
                if(orderInfo != null)
                {
                    // 已成功
                    if(OrderTxStatus.REALIZED.getKey().equalsIgnoreCase(orderInfo.getStatus()))
                    {
                        CacheManager.getInstance().setString(presentCacheKey, "1", CacheManager.EXPIRES_DAY);
                        receivCount = inviteCount;
                        continue;
                    }

                    // 未完成
                    orderno = orderInfo.getNo();
                }
                else
                {
                    orderno = mPresentOrderService.createOrder(outradeno, currencyType, model, presentBusinessType, presentationAmount, remarkVO);
                }

                ErrorResult errorResult = mPayApiManager.doPlatformPresentation(accountType, currencyType, businessType, orderno, userInfo, presentationAmount, remarkVO);
                if(errorResult == SystemErrorResult.SUCCESS)
                {
                    mPresentOrderService.updateTxStatus(orderno, OrderTxStatus.REALIZED, userInfo.getId(), presentBusinessType, null, null);

                    // 更新缓存
                    mPresentOrderService.find(true, outradeno);
                    CacheManager.getInstance().setString(presentCacheKey, "1", CacheManager.EXPIRES_DAY);
                    CacheManager.getInstance().setString(presentLevelCacheKey, inviteCount + StringUtils.getEmpty());

                    receivCount = inviteCount;
                }

            } catch (Exception e) {
                LOG.error("do presentation error:", e);
            }

        }

        return receivCount;
    }

    /**
     * 今日已赠送的用户，要缓存起来
     * @param parentName
     * @return
     */
    private static String getPresentCacheKey(String parentName, int todayOfYear, String businessKey)
    {
        return DEFAULT_CACHE_KEY + "_has_present_" + todayOfYear + parentName + businessKey;
    }

    private static String getPresentLevelCountCacheKey(String username)
    {
        return DEFAULT_CACHE_KEY + "_getPresentLevelCount_" + username;
    }

    public long getPresentLevelCount(String username)
    {
        String cachekey = getPresentLevelCountCacheKey(username);
        return CacheManager.getInstance().getLong(cachekey);
    }

//    /**
//     * 获取用户邀请状态
//     * @param username
//     * @return
//     */
//    public TodayInviteFriendStatus getTodayInviteFriendTaskStatus(String username)
//    {
//        DateTime dateTime = new DateTime();
//        String cachekey = TodayInviteFriendStatus.getCacheKey(username, dateTime, StringUtils.getEmpty());
//        TodayInviteFriendStatus value = CacheManager.getInstance().getObject(cachekey, TodayInviteFriendStatus.class);
//        if(value == null)
//        {
//            return mDefaultStatus;
//        }
//        return value;
//    }

    public InviteFriendStatus getTmpInviteFriendTaskStatus(DateTime rechargeTime, String username, String extraKey)
    {
        String cachekey = InviteFriendStatus.getCacheKey(username, rechargeTime, null, extraKey, mPresentBusinessType);
        InviteFriendStatus status = mLRUCache.get(cachekey);
        if(status == null)
        {
            status = InviteFriendStatus.loadFromCache(cachekey);
        }
        mLRUCache.put(cachekey, status);
        return status;
    }

    public void saveTmpInviteFriendTaskStatus(DateTime rechargeTime, String username, InviteFriendStatus status, String extraKey)
    {
        String cachekey = InviteFriendStatus.getCacheKey(username, rechargeTime, null, extraKey, mPresentBusinessType);
        CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(status));
    }

    public void test1()
    {
        DateTime dateTime = DateTime.now();
        String username = "up9199999999992";
        UserInfo userInfo = mUserService.findByUsername(false, username);

        InviteFriendStatus status = getTmpInviteFriendTaskStatus(dateTime, username, null);
        System.out.println(FastJsonHelper.jsonEncode(status));
//        status.setInviteCount(10);
//        status.saveTodayCache(username, mPresentBusinessType);

        receive(userInfo);
    }

    public void test2()
    {
        String registerIP = "127.0.0.1";
        String deviceToken = "2";

        DateTime toTime = DateTime.now();
        DateTime fromTime = toTime.minusDays(3000);

        long value1 = mUserService.countRegisterIp(true, fromTime, toTime, registerIP);
        System.out.println("value1 = " + value1);

        long value2 = mUserService.countDeviceToken(true, fromTime, toTime, deviceToken);
        System.out.println("value1 = " + value2);
    }

    public void test3()
    {
        String timeStr = "2023-03-16";
        Date pdate = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD, timeStr);
        DateTime dateTime = new DateTime(pdate);
        doTask(dateTime);
    }

    public static void testRun()
    {
        TodayInviteFriendManagerV2 mgr = SpringContextUtils.getBean(TodayInviteFriendManagerV2.class);
        mgr.test1();
    }

    private class InviteKeyValue {
        /*** 邀请完成人数, 如 1=20, key为1， amount=20 ***/
        private int key;
        private BigDecimal amount;

        public int getKey() {
            return key;
        }

        public void setKey(int key) {
            this.key = key;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }

    public static void main(String[] args) {
        String configValue = "1=20|2=40|5=130|10=300|20=700|30=1200|50=2300|100=5000";

        String[] valueArray = StringUtils.split(configValue, '|');
        if(valueArray != null || valueArray.length > 0)
        {
            for(String item : valueArray)
            {
                int index = item.indexOf("=");
                String key = item.substring(0, index).trim();
                String value = item.substring(index + 1, item.length());
                System.out.println(key + " = " + value);
            }
        }
    }

}
