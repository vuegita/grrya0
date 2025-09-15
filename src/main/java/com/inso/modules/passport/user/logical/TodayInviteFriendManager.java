package com.inso.modules.passport.user.logical;

import com.alibaba.druid.util.LRUCache;
import com.google.common.collect.Lists;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.*;
import com.inso.modules.admin.config.PlatformConfig;
import com.inso.modules.common.config.PlarformConfig2;
import com.inso.modules.common.config.SystemConfig;
import com.inso.modules.common.model.*;
import com.inso.modules.passport.business.service.RechargeOrderService;
import com.inso.modules.passport.business.model.DayPresentOrder;
import com.inso.modules.passport.business.model.PresentBusinessType;
import com.inso.modules.passport.business.model.RechargeOrder;
import com.inso.modules.passport.business.service.DayPresentOrderService;
import com.inso.modules.passport.money.PayApiManager;
import com.inso.modules.passport.money.model.UserMoney;
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
import java.util.Date;
import java.util.List;

/**
 * 邀请好友管理器
 */
@Component
public class TodayInviteFriendManager {

    private static Log LOG = LogFactory.getLog(TodayInviteFriendManager.class);

    private static final String DEFAULT_CACHE_KEY = "inso_passport_invite_friend_status_";

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

    private PresentBusinessType mPresentBusinessType = PresentBusinessType.INVITE_WEEK;

    public boolean checkConfigValue(String configValue)
    {
        if(StringUtils.isEmpty(configValue))
        {
            return false;
        }
        String[] valueArray = StringUtils.split(configValue, '|');
        if(valueArray != null || valueArray.length > 0)
        {
            for(String item : valueArray)
            {
                int index = item.indexOf("=");
                if(index < 0)
                {
                    return false;
                }
                String key = item.substring(0, index).trim();
                if(StringUtils.isEmpty(key) || StringUtils.asInt(key) <= 0)
                {
                    return false;
                }
                String value = item.substring(index + 1, item.length());
                if(StringUtils.isEmpty(value) || StringUtils.asInt(value) <= 0)
                {
                    return false;
                }
            }
        }
        return true;
    }

    public void doTask(DateTime firDateTime, boolean isCalcAmountAndPresentation)
    {
        float limitMinRecharge =  mConfigService.getFloat(false,PlarformConfig2.ADMIN_APP_PLATFORM_USER_USER_INVITE_FRIEND_TASK_MIN_RECHARGE.getKey());
        if(limitMinRecharge <= 0)
        {
            return;
        }

        // 查询用户属性表

        // 最近3天的注册用户
        Date startDate = firDateTime.minusDays(InviteFriendStatus.DEF_LIMIT_LATEST_DAY).toDate();
        Date endDate = firDateTime.toDate();


        Date beginTime = DateUtils.getStartOfDay(startDate);
        Date endTime = DateUtils.getEndOfDay(endDate);

//        Map<String, TodayInviteFriendStatus> maps = Maps.newHashMap();

        // 表示当前线程处理数据缓存id-由于用户数据日后会大，需要借助缓存
        String extraKey = UUIDUtils.getUUID();


        int weekOfYear = firDateTime.getWeekOfWeekyear();

        mUserAttrService.queryAllMember2(beginTime, endTime, new Callback<UserAttr>() {
            public void execute(UserAttr model) {
                String parentName = model.getParentname();
                if(StringUtils.isEmpty(parentName))
                {
                    return;
                }

                InviteFriendStatus status = getTmpInviteFriendTaskStatus(firDateTime, parentName, extraKey);

//                if(model.getUsername().equalsIgnoreCase("c_0xFA730bd82c7E8721aF28c8A0ed56Bf9041E"))
//                {
//                    status.setRechargeCount(5);
//                    status.setInviteCount(100);
//                }

                //
                status.increInviteCount();
                if(!StringUtils.isEmpty(model.getFirstRechargeOrderno()) && model.getFirstRechargeTime() != null)
                {
                    if(model.getFirstRechargeAmount() == null || model.getFirstRechargeAmount().floatValue() < limitMinRecharge)
                    {
                        // 最少充值要200
                        return;
                    }

                    DateTime rechargeTime = new DateTime(model.getFirstRechargeTime());
                    if(weekOfYear == rechargeTime.getWeekOfWeekyear())
                    {
                        status.increRechargeCount();
                    }
                }

                saveTmpInviteFriendTaskStatus(firDateTime, parentName, status, extraKey);
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
            }
        });

        // 实时赠送
        doPresentation(firDateTime, beginTime, endTime, extraKey);
    }

    /**
     * 赠送邀请好友并完成充值的赠送金额
     */
    private void doPresentation(DateTime fireDatetime, Date beginTime, Date endTime, String extraKey)
    {
        BigDecimal limitTotalDeductCodeAmount = BigDecimalUtils.getNotNull(mConfigService.getBigDecimal(false, SystemConfig.VALID_INVITE_MEMBER_LIMIT_TOTAL_DEDUCT_CODE_AMOUNT.getKey()));

        // 获取配置
        String configValue = mConfigService.getValueByKey(true, PlatformConfig.ADMIN_APP_PLATFORM_USER_USER_INVITE_FRIEND_TASK);
        List<InviteKeyValue> configItemList = Lists.newArrayList();
        if(!StringUtils.isEmpty(configValue))
        {
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
                        continue;
                    }

                    InviteKeyValue keyValue = new InviteKeyValue();
                    keyValue.setKey(keyid);
                    keyValue.setAmount(new BigDecimal(value));

                    configItemList.add(keyValue);
                }
            }
        }
        else
        {
            return;
        }

        BusinessType businessType = BusinessType.FINISH_INVITE_FRIEND_TASK_PRESENTATION;
        RemarkVO remarkVO = RemarkVO.create("invite friend presentation");

        DateTime dateTime = new DateTime(beginTime);
        int weekOfWeekyear = dateTime.getWeekOfWeekyear();

        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
        PresentBusinessType presentBusinessType = PresentBusinessType.INVITE_WEEK;
        FundAccountType accountType = FundAccountType.Spot;

        mUserAttrService.queryAllMember2(beginTime, endTime, new Callback<UserAttr>() {
            public void execute(UserAttr model) {

                if(model.getFirstRechargeAmount() == null || model.getFirstRechargeAmount().compareTo(BigDecimal.ZERO) <= 0)
                {
                    return;
                }

                String parentName = model.getParentname();
                if(StringUtils.isEmpty(parentName) || model.getParentid() <= 0)
                {
                    return;
                }
                if(StringUtils.isEmpty(model.getUsername()) || model.getParentid() <= 0)
                {
                    return;
                }
                InviteFriendStatus status = getTmpInviteFriendTaskStatus(fireDatetime, parentName, extraKey);
                if(status == null)
                {
                    return;
                }

                long rechargeCount = status.getRechargeCount();
                if(rechargeCount <= 0)
                {
                    return;
                }

                // 验证最低下单扣款打码
                if(limitTotalDeductCodeAmount.compareTo(BigDecimal.ZERO) > 0)
                {
                    UserMoney userMoney = mUserMoneyService.findMoney(false, model.getUserid(), accountType, currencyType);
                    BigDecimal totalDeductCodeAmount = userMoney.getTotalDeductCodeAmount();
                    if(totalDeductCodeAmount.compareTo(limitTotalDeductCodeAmount) < 0)
                    {
                        return;
                    }
                }

                UserInfo parentInfo = mUserService.findByUsername(false, parentName);
                UserAttr parentAttr = mUserAttrService.find(false, parentInfo.getId());

                for(InviteKeyValue item : configItemList)
                {
                    // 充值人数 > 配置完成充值人数 则赠送金额
                    if(rechargeCount < item.getKey())
                    {
                        continue;
                    }
                    String taskid = item.getKey() + StringUtils.getEmpty();

                    String presentCacheKey = getPresentCacheKey(parentName, weekOfWeekyear, taskid);
                    if(CacheManager.getInstance().exists(presentCacheKey))
                    {
                        // 已赠送，则不在赠送
                        //LOG.info("invite present amount, it has present  " + parentName);
                        continue;
                    }

                    try {
                        BigDecimal presentationAmount = item.getAmount();

                        String msg = "Invite present, from " + model.getUsername();
                        remarkVO.setMesage(msg);

                        String orderno = null;
                        String outradeno = mPresentOrderService.generateOutTradeNo(parentAttr.getUserid(), presentBusinessType, taskid, fireDatetime);
                        DayPresentOrder orderInfo = mPresentOrderService.find(MyEnvironment.isDev(), outradeno);
                        if(orderInfo != null)
                        {
                            // 已成功
                            if(OrderTxStatus.REALIZED.getKey().equalsIgnoreCase(orderInfo.getStatus()))
                            {
                                CacheManager.getInstance().setString(presentCacheKey, "1", CacheManager.EXPIRES_DAY);
                                continue;
                            }

                            // 未完成
                            orderno = orderInfo.getNo();
                        }
                        else
                        {
                            orderno = mPresentOrderService.createOrder(outradeno, currencyType, parentAttr, presentBusinessType, presentationAmount, remarkVO);
                        }

                        FundAccountType accountType = FundAccountType.Spot;
                        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
                        ErrorResult errorResult = mPayApiManager.doPlatformPresentation(accountType, currencyType, businessType, orderno, parentInfo, presentationAmount, remarkVO);
                        if(errorResult == SystemErrorResult.SUCCESS)
                        {
                            mPresentOrderService.updateTxStatus(orderno, OrderTxStatus.REALIZED, parentInfo.getId(), presentBusinessType, null, null);

                            // 更新缓存
                            mPresentOrderService.find(true, outradeno);

                            // 更新日志
                            mUserAttrService.updateInviteFriendTotalAmount(parentInfo.getId(), presentationAmount);

                            CacheManager.getInstance().setString(presentCacheKey, "1", CacheManager.EXPIRES_DAY);
                        }

                    } catch (Exception e) {
                        LOG.error("do presentation error:", e);
                    }


                }

            }
        });

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
