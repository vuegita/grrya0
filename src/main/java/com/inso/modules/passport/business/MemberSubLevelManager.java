package com.inso.modules.passport.business;

import com.alibaba.druid.util.LRUCache;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;
import com.inso.framework.utils.UUIDUtils;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.business.model.RechargeOrder;
import com.inso.modules.passport.business.model.UserLevelStatusInfo;
import com.inso.modules.passport.business.model.WithdrawOrder;
import com.inso.modules.passport.business.service.RechargeOrderService;
import com.inso.modules.passport.business.service.WithdrawOrderService;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.SystemRunningMode;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class MemberSubLevelManager {

    private static final String ROOT_CACHE = MemberSubLevelManager.class.getName();

    private static Log LOG = LogFactory.getLog(MemberSubLevelManager.class);

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private RechargeOrderService mRechargeOrderService;

    @Autowired
    private WithdrawOrderService mWithdrawOrderService;

    private static final UserLevelStatusInfo mEmptyDataInfo = new UserLevelStatusInfo();


    public void doTask(DateTime dateTime)
    {
        try {
            if(!(SystemRunningMode.isBCMode() || SystemRunningMode.isFundsMode() || MyEnvironment.isDev()))
            {
                return;
            }
            String pdate = dateTime.toString(DateUtils.TYPE_YYYY_MM_DD);
            String beginTime = DateUtils.getBeginTimeOfDay(pdate);
            String endTime = DateUtils.getEndTimeOfDay(pdate);
            int dayOfYear = dateTime.getDayOfYear();

            LRUCache<String, UserLevelStatusInfo> lruCache = new LRUCache<>(200);

            String uuid = UUIDUtils.getUUID();
            doStatsRecharge(uuid, lruCache, beginTime, endTime, dayOfYear);
            doStatsWithdraw(uuid, lruCache, beginTime, endTime, dayOfYear);
            lruCache.clear();
        } catch (Exception e) {
            LOG.error("handle error:", e);
        }
    }

    private void doStatsRecharge(String uuid, LRUCache<String, UserLevelStatusInfo> lruCache, String fromTime, String toTime, int dayOfYear)
    {
        mRechargeOrderService.queryAll(fromTime, toTime, new Callback<RechargeOrder>() {
            @Override
            public void execute(RechargeOrder o)
            {
                OrderTxStatus txStatus = OrderTxStatus.getType(o.getStatus());
                if(txStatus != OrderTxStatus.REALIZED)
                {
                    return;
                }

                UserAttr userAttr = mUserAttrService.find(false, o.getUserid());

                if(userAttr.getParentid() <= 0)
                {
                    return;
                }

                UserInfo.UserType userType = UserInfo.UserType.getType(userAttr.getUserType());
                if(userType != UserInfo.UserType.MEMBER)
                {
                    return;
                }

                increAmount(uuid, lruCache, true, true, o.getAmount(), userAttr.getParentname(), dayOfYear);
                if(userAttr.getGrantfatherid() > 0)
                {
                    increAmount(uuid, lruCache, true, false, o.getAmount(), userAttr.getGrantfathername(), dayOfYear);
                }
            }
        });

        mRechargeOrderService.queryAll(fromTime, toTime, new Callback<RechargeOrder>() {
            @Override
            public void execute(RechargeOrder o)
            {
                OrderTxStatus txStatus = OrderTxStatus.getType(o.getStatus());
                if(txStatus != OrderTxStatus.REALIZED)
                {
                    return;
                }

                UserAttr userAttr = mUserAttrService.find(false, o.getUserid());

                if(userAttr.getParentid() <= 0)
                {
                    return;
                }

                UserInfo.UserType userType = UserInfo.UserType.getType(userAttr.getUserType());
                if(userType != UserInfo.UserType.MEMBER)
                {
                    return;
                }

                increAmount(uuid, lruCache, true, true, null, userAttr.getParentname(), dayOfYear);
                if(userAttr.getGrantfatherid() > 0)
                {
                    increAmount(uuid, lruCache, true, false, null, userAttr.getGrantfathername(), dayOfYear);
                }
            }
        });
    }

    private void doStatsWithdraw(String uuid, LRUCache<String, UserLevelStatusInfo> lruCache, String fromTime, String toTime, int dayOfYear)
    {

        mWithdrawOrderService.queryAll(fromTime, toTime, new Callback<WithdrawOrder>() {
            @Override
            public void execute(WithdrawOrder o) {

                OrderTxStatus txStatus = OrderTxStatus.getType(o.getStatus());
                if(txStatus != OrderTxStatus.REALIZED)
                {
                    return;
                }

                UserAttr userAttr = mUserAttrService.find(false, o.getUserid());
                if(userAttr.getParentid() <= 0)
                {
                    return;
                }

                UserInfo.UserType userType = UserInfo.UserType.getType(userAttr.getUserType());
                if(userType != UserInfo.UserType.MEMBER)
                {
                    return;
                }

                increAmount(uuid, lruCache, false, true, o.getAmount(), userAttr.getParentname(), dayOfYear);
                if(userAttr.getGrantfatherid() > 0)
                {
                    increAmount(uuid, lruCache, false, false, o.getAmount(), userAttr.getGrantfathername(), dayOfYear);
                }
            }
        });

        mWithdrawOrderService.queryAll(fromTime, toTime, new Callback<WithdrawOrder>() {
            @Override
            public void execute(WithdrawOrder o) {

                OrderTxStatus txStatus = OrderTxStatus.getType(o.getStatus());
                if(txStatus != OrderTxStatus.REALIZED)
                {
                    return;
                }

                UserAttr userAttr = mUserAttrService.find(false, o.getUserid());
                if(userAttr.getParentid() <= 0)
                {
                    return;
                }

                UserInfo.UserType userType = UserInfo.UserType.getType(userAttr.getUserType());
                if(userType != UserInfo.UserType.MEMBER)
                {
                    return;
                }

                increAmount(uuid, lruCache, false, true, null, userAttr.getParentname(), dayOfYear);
                if(userAttr.getGrantfatherid() > 0)
                {
                    increAmount(uuid, lruCache, false, false, null, userAttr.getGrantfathername(), dayOfYear);
                }
            }
        });
    }

    public void increAmount(String uuid, LRUCache<String, UserLevelStatusInfo> lruCache, boolean isRecharge, boolean isLv1, BigDecimal amount, String username, int dayOfYear)
    {
        String cacheKey = ROOT_CACHE + dayOfYear + username;
        String cacheKey2 = cacheKey  + StringUtils.getNotEmpty(uuid);

        UserLevelStatusInfo jsonObject = lruCache.get(username);
        if(jsonObject == null)
        {
            jsonObject = CacheManager.getInstance().getObject(cacheKey2, UserLevelStatusInfo.class);
        }
        if(jsonObject == null)
        {
            jsonObject = new UserLevelStatusInfo();
            lruCache.put(username, jsonObject);
        }

        if(amount != null)
        {
            if(isRecharge)
            {
                jsonObject.increRecharge(isLv1, amount);
            }
            else
            {
                jsonObject.increWithdraw(isLv1, amount);
            }
            CacheManager.getInstance().setString(cacheKey2, FastJsonHelper.jsonEncode(jsonObject));
        }
        else
        {
            CacheManager.getInstance().setString(cacheKey, FastJsonHelper.jsonEncode(jsonObject), CacheManager.EXPIRES_DAY);
        }
//        LOG.info("username = " + username + ", data = " + FastJsonHelper.jsonEncode(jsonObject));
    }

    public UserLevelStatusInfo getTodayDataFromCache(String username)
    {
        DateTime dateTime = new DateTime();
        return getDataFromCache(dateTime, username);
    }

    public UserLevelStatusInfo getDataFromCache(DateTime dateTime, String username)
    {
        String cacheKey = ROOT_CACHE + dateTime.getDayOfYear() + username;
        UserLevelStatusInfo jsonObject = CacheManager.getInstance().getObject(cacheKey, UserLevelStatusInfo.class);
        if(jsonObject == null)
        {
            jsonObject = mEmptyDataInfo;
        }
        return jsonObject;
    }

    public boolean exist(DateTime dateTime, String username)
    {
        String cacheKey = ROOT_CACHE + dateTime.getDayOfYear() + username;
        return CacheManager.getInstance().exists(cacheKey);
    }

    private void deleteCache(int dayOfYear, UserAttr userAttr)
    {
        String cacheKey = ROOT_CACHE + dayOfYear + userAttr.getParentname();
        CacheManager.getInstance().delete(cacheKey);

        if(userAttr.getGrantfatherid() > 0)
        {
            cacheKey = ROOT_CACHE + dayOfYear + userAttr.getGrantfathername();
            CacheManager.getInstance().delete(cacheKey);
        }
    }

    private static void test(String username)
    {
        DateTime dateTime = new DateTime().minusDays(1);
        String cacheKey = ROOT_CACHE + dateTime.getDayOfYear() + username;

        UserLevelStatusInfo data = new UserLevelStatusInfo();
        data.setLv1RechargeAmount(new BigDecimal(1));
        data.setLv1WithdrawAmount(new BigDecimal(2));
        data.setLv2RechargeAmount(new BigDecimal(3));
        data.setLv2WithdrawAmount(new BigDecimal(4));

        CacheManager.getInstance().setString(cacheKey, FastJsonHelper.jsonEncode(data), CacheManager.EXPIRES_DAY);
    }

    public static void main(String[] args) {

        test("c_0xFA730bd82c7E8721aF28c8A0ed56Bf9041E");
    }

}
