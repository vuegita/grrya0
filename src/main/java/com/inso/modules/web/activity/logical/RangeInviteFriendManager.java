package com.inso.modules.web.activity.logical;

import com.alibaba.druid.util.LRUCache;
import com.google.common.collect.Lists;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.*;
import com.inso.modules.common.model.*;
import com.inso.modules.passport.business.model.PresentBusinessType;
import com.inso.modules.passport.business.service.RechargeOrderService;
import com.inso.modules.passport.business.model.RechargeOrder;
import com.inso.modules.passport.user.model.InviteFriendStatus;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.report.model.MemberReport;
import com.inso.modules.report.service.UserReportService;
import com.inso.modules.web.activity.model.ActivityBusinessType;
import com.inso.modules.web.activity.model.ActivityInfo;
import com.inso.modules.web.activity.service.ActivityService;
import com.inso.modules.web.service.ConfigService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

/**
 * 邀请好友管理器
 */
@Component
public class RangeInviteFriendManager {

    private static Log LOG = LogFactory.getLog(RangeInviteFriendManager.class);

    private static final String DEFAULT_CACHE_KEY = RangeInviteFriendManager.class.getName();

    private static final String EXIST_TASK_CACHE_KEY = DEFAULT_CACHE_KEY + "createExistTaskCacheKey";

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private ConfigService mConfigService;

    @Autowired
    private RechargeOrderService mRechargeOrderService;

    @Autowired
    private UserReportService mUserReportService;

    @Autowired
    private ActivityManager mActivityManager;

    @Autowired
    private ActivityService mActivityService;

    private LRUCache<String, InviteFriendStatus> mLRUCache = new LRUCache<>(100);

    private PresentBusinessType mPresentBusinessType = PresentBusinessType.INVITE_WEEK;



    public void doLatestActivity()
    {
        ActivityInfo activityInfo = mActivityService.findLatestActive(false, ActivityBusinessType.INVITE_ACTIVITY);
        if(activityInfo == null)
        {
            return;
        }

        OrderTxStatus txStatus = OrderTxStatus.getType(activityInfo.getStatus());
        if(txStatus != OrderTxStatus.WAITING)
        {
            return;
        }

        long validBeginTimeTs = activityInfo.getBegintime().getTime();
        long validEndTimeTs = activityInfo.getEndtime().getTime();

        long ts = System.currentTimeMillis();
        // 增加一个计算周期
        if(validBeginTimeTs >= ts || validEndTimeTs + 650000 < ts)
        {
            return;
        }

        doTask(activityInfo, false);
    }

    public void doSettle(ActivityInfo activityInfo)
    {
        if(activityInfo == null)
        {
            activityInfo = mActivityService.findLatestActive(false, ActivityBusinessType.INVITE_ACTIVITY);
        }

        if(activityInfo == null)
        {
            return;
        }

        OrderTxStatus txStatus = OrderTxStatus.getType(activityInfo.getStatus());

        if(!(txStatus == OrderTxStatus.WAITING || txStatus == OrderTxStatus.REALIZED))
        {
            return;
        }

        long validEndTimeTs = activityInfo.getEndtime().getTime();
        long ts = System.currentTimeMillis();
        // 增加一个计算周期
        if(ts <= validEndTimeTs + 1800_000)
        {
            // 结算周期内不处理
            return;
        }

        doTask(activityInfo, true);
    }

    private void doTask(ActivityInfo activityInfo, boolean isSettle)
    {
        float limitMinRecharge =  activityInfo.getLimitMinInvesAmount().floatValue();
        if(limitMinRecharge <= 0)
        {
            return;
        }
        mLRUCache.clear();

        // 查询用户属性表
        DateTime fromTime = new DateTime(activityInfo.getBegintime());
        DateTime toTime = new DateTime(activityInfo.getEndtime());

        // 最近3天的注册用户
        Date startDate = activityInfo.getBegintime();
        Date endDate = activityInfo.getEndtime();

        // 表示当前线程处理数据缓存id-由于用户数据日后会大，需要借助缓存
        String extraKey = UUIDUtils.getUUID();


        long validBeginTimeTs = startDate.getTime();
        long validEndTimeTs = endDate.getTime();

        FundAccountType accountType = FundAccountType.Spot;
        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();

        mUserAttrService.queryAllMember2(startDate, endDate, new Callback<UserAttr>() {
            public void execute(UserAttr model) {

                String parentName = model.getParentname();
                if(StringUtils.isEmpty(parentName))
                {
                    return;
                }

                InviteFriendStatus status = getTmpInviteFriendTaskStatus(parentName, fromTime, toTime, extraKey);

                try {
                    //
                    status.increInviteCount();
                    if(StringUtils.isEmpty(model.getFirstRechargeOrderno()) || model.getFirstRechargeTime() == null)
                    {
                        return;
                    }

                    RechargeOrder rechargeOrder = mRechargeOrderService.findByNo(model.getFirstRechargeOrderno());
                    if(rechargeOrder != null && rechargeOrder.getAmount().floatValue() >= limitMinRecharge)
                    {
                        // 最少充值要200
                        long rechargeTs = rechargeOrder.getCreatetime().getTime();
                        if(rechargeTs >= validBeginTimeTs && rechargeTs <= validEndTimeTs)
                        {
                            status.increRechargeCount();
                        }
                    }

                    MemberReport memberReport = mUserReportService.queryHistoryReportByUser(false, fromTime, toTime, model.getUserid(), accountType, currencyType);
                    if(memberReport != null)
                    {
                        status.setHistoryTotalAmount(memberReport.getRecharge());
                    }
                } catch (Exception e) {
                } finally {
                    saveTmpInviteFriendTaskStatus(parentName, status, fromTime, toTime, extraKey);
                }
            }
        });

        // 更新缓存
        mUserAttrService.queryAllMember2(startDate, endDate, new Callback<UserAttr>() {
            public void execute(UserAttr model) {

                String parentName = model.getParentname();
                if(StringUtils.isEmpty(parentName))
                {
                    return;
                }
                InviteFriendStatus status = getTmpInviteFriendTaskStatus(parentName, fromTime, toTime, extraKey);
                if(status == null)
                {
                    return;
                }

                status.save(parentName, fromTime, toTime, StringUtils.getEmpty(), mPresentBusinessType);
            }
        });

        long ts = System.currentTimeMillis();
        isSettle = isSettle && (ts >= validEndTimeTs + 1800_000);
        doPresentation(isSettle, activityInfo, fromTime, toTime, extraKey);
    }

    /**
     * 赠送邀请好友并完成充值的赠送金额
     */
    private void doPresentation(boolean isSettle, ActivityInfo activityInfo, DateTime fromTime, DateTime toTime, String extraKey)
    {
        // 获取配置
        String configValue = activityInfo.getExtraPresentTier();
        List<InviteKeyValue> configItemList = Lists.newArrayList();
        if(isSettle && !StringUtils.isEmpty(configValue))
        {
            String[] valueArray = StringUtils.split(configValue, '|');
            if(valueArray != null || valueArray.length > 0)
            {
                for(String item : valueArray)
                {
                    int index = item.indexOf("=");
                    String amountStr = item.substring(0, index).trim();
                    String rateStr = item.substring(index + 1, item.length());

                    BigDecimal amount = StringUtils.asBigDecimal(amountStr);
                    BigDecimal rate = StringUtils.asBigDecimal(rateStr);

                    if(rate == null || amount.compareTo(BigDecimal.ZERO) <= 0)
                    {
                        continue;
                    }

                    InviteKeyValue keyValue = new InviteKeyValue();
                    keyValue.setRate(rate);
                    keyValue.setAmount(amount);

                    configItemList.add(keyValue);
                }
            }


        }

        // clear and re stats
        activityInfo.setFinishInvesAmount(null);
        activityInfo.setFinishPresentAmount(null);
        activityInfo.setFinishInviteCount(0);
        activityInfo.setFinishInvesCount(0);

        Date startDate = fromTime.toDate();
        Date endDate = toTime.toDate();


        String existTaskCacheValue = "1";
        mUserAttrService.queryAllMember2(startDate, endDate, new Callback<UserAttr>() {
            public void execute(UserAttr model) {

                String parentName = model.getParentname();
                if(StringUtils.isEmpty(parentName) || model.getParentid() <= 0)
                {
                    return;
                }
                String existTaskCacheKey = createExistTaskCacheKey(parentName, fromTime, toTime, extraKey);
                if(CacheManager.getInstance().exists(existTaskCacheKey))
                {
                    return;
                }

                InviteFriendStatus status = getTmpInviteFriendTaskStatus(parentName, fromTime, toTime, null);
                if(status == null)
                {
                    return;
                }

                try {
                    long rechargeCount = status.getRechargeCount();
                    if(rechargeCount <= activityInfo.getLimitMinInviteCount())
                    {
                        return;
                    }

                    if(!isSettle)
                    {
                        return;
                    }

                    UserInfo parentInfo = mUserService.findByUsername(false, parentName);

                    BigDecimal presentAmount = activityInfo.getBasicPresentAmount();
                    BigDecimal extraPresentAmount = BigDecimal.ZERO;
                    BigDecimal extraPresentRate = BigDecimal.ZERO;

                    if(!configItemList.isEmpty() && status.getHistoryTotalAmount().compareTo(activityInfo.getBasicPresentAmount()) > 0)
                    {
                        BigDecimal tmpAmount = status.getHistoryTotalAmount().subtract(activityInfo.getBasicPresentAmount());
                        InviteKeyValue config = loadConfig(configItemList, tmpAmount);
                        if(config != null && config.getRate().compareTo(BigDecimal.ZERO) > 0 && config.getRate().compareTo(BigDecimalUtils.DEF_1) < 0)
                        {
                            extraPresentRate = config.getRate();
                            extraPresentAmount = tmpAmount.multiply(extraPresentRate);
                            presentAmount = presentAmount.add(extraPresentAmount).setScale(2, RoundingMode.DOWN);
                        }
                    }

                    mActivityManager.doPresent(activityInfo, status, parentInfo, presentAmount, extraPresentAmount, extraPresentRate);
                    BigDecimal totalPresentAmount = BigDecimalUtils.getNotNull(activityInfo.getFinishPresentAmount()).add(presentAmount);
                    activityInfo.setFinishPresentAmount(totalPresentAmount);

                } finally {
                    BigDecimal totalInvesAmount = BigDecimalUtils.getNotNull(activityInfo.getFinishInvesAmount()).add(status.getHistoryTotalAmount());
                    activityInfo.setFinishInvesAmount(totalInvesAmount);

                    activityInfo.setFinishInviteCount(status.getInviteCount() + activityInfo.getFinishInviteCount());
                    activityInfo.setFinishInvesCount(status.getRechargeCount() + activityInfo.getFinishInvesCount());

                    CacheManager.getInstance().setString(existTaskCacheKey, existTaskCacheValue);
                }
            }
        });

        // 完成统计状态
        long totalInviteCount = activityInfo.getFinishInviteCount();
        long totalInvesCount = activityInfo.getFinishInvesCount();
        BigDecimal totalPresentAmount = activityInfo.getFinishPresentAmount();
        BigDecimal totalInvesAmount = activityInfo.getFinishInvesAmount();

        OrderTxStatus txStatus = null;
        if(isSettle)
        {
            txStatus = OrderTxStatus.REALIZED;
        }
        else
        {
            totalPresentAmount = null;
        }

        mActivityService.updateInfo(activityInfo, null, totalInviteCount, totalInvesCount, totalInvesAmount, totalPresentAmount, txStatus, null);
    }

    public InviteFriendStatus getTmpInviteFriendTaskStatus(String username, DateTime fromTime, DateTime toTime, String extraKey)
    {
        String cachekey = InviteFriendStatus.getCacheKey(username, fromTime, toTime, extraKey, mPresentBusinessType);
        InviteFriendStatus status = mLRUCache.get(cachekey);
        if(status == null)
        {
            status = InviteFriendStatus.loadFromCache(cachekey);
        }
        mLRUCache.put(cachekey, status);
        return status;
    }

    public void saveTmpInviteFriendTaskStatus(String username, InviteFriendStatus status, DateTime fromTime, DateTime toTime, String extraKey)
    {
        String cachekey = InviteFriendStatus.getCacheKey(username, fromTime, toTime, extraKey, mPresentBusinessType);
        CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(status));
    }

    public static String createExistTaskCacheKey(String parentName, DateTime fromTime, DateTime toTime, String extraId)
    {
        int dayOfYear = fromTime.getDayOfYear();
        return EXIST_TASK_CACHE_KEY + dayOfYear + toTime.getDayOfYear() + parentName + extraId;
    }

    private InviteKeyValue loadConfig(List<InviteKeyValue> configList, BigDecimal amount)
    {
        if(configList == null || configList.isEmpty())
        {
            return null;
        }

        int len = configList.size();
        for(int i = len - 1; i >= 0; i --)
        {
            InviteKeyValue model = configList.get(i);
            if(amount.compareTo(model.getAmount()) >= 0)
            {
                return model;
            }
        }
        return null;
    }

    private class InviteKeyValue {
        /*** 邀请完成人数, 如 1=20, key为1， amount=20 ***/
        private BigDecimal amount;
        private BigDecimal rate;


        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public BigDecimal getRate() {
            return rate;
        }

        public void setRate(BigDecimal rate) {
            this.rate = rate;
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
