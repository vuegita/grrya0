package com.inso.modules.report.logical;

import com.google.common.collect.Maps;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.framework.utils.UUIDUtils;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.business.service.RechargeOrderService;
import com.inso.modules.passport.business.model.RechargeOrder;
import com.inso.modules.passport.business.model.WithdrawOrder;
import com.inso.modules.passport.business.service.WithdrawOrderService;
import com.inso.modules.passport.gift.helper.GiftStatusHelper;
import com.inso.modules.passport.gift.model.GiftPeriodType;
import com.inso.modules.passport.gift.model.GiftTargetType;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.report.model.UserStatusDay;
import com.inso.modules.report.service.UserStatusDayService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.Set;

@Component
public class MemberStatusStats {

    private static String ROOT_CACHE_KEY = UserStatusDay.class.getName();

    private static Log LOG = LogFactory.getLog(MemberStatusStats.class);

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private RechargeOrderService mRechargeOrderService;

    @Autowired
    private WithdrawOrderService mWithdrawOrderService;

    @Autowired
    private UserStatusDayService mUserStatusDayService;

    public void doTask(DateTime dateTime)
    {
        try {
            doStats(dateTime);
        } catch (Exception e) {
            LOG.error("handle error:", e);
        }
    }

    private void doStats(DateTime dateTime)
    {
        String timeString = dateTime.toString(DateUtils.TYPE_YYYY_MM_DD);
        Date pdate = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD, timeString);

        String startTimeString = timeString + " 00:00:00";
        String endTimeString = timeString + " 23:59:59";

        Date startDate = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, startTimeString);
        Date endDate = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, endTimeString);

        // 平台汇总
        UserStatusDay platformStatusDay = new UserStatusDay();

        // 所有员工
        Map<String, UserStatusDay> maps = Maps.newHashMap();
        int dayOfYear = dateTime.getDayOfYear();

        // 统计注册人数|分裂人数
        mUserAttrService.queryAllMember2(startDate, endDate, new Callback<UserAttr>() {
            @Override
            public void execute(UserAttr userAttr) {
                try {
                    // 平台
                    if(userAttr.getParentid() > 0)
                    {
                        platformStatusDay.increRegister(1, 1);
                    }
                    else
                    {
                        platformStatusDay.increRegister(1, 0);
                    }

                    if(userAttr.getDirectStaffid() <= 0)
                    {
                       return;
                    }

                    UserStatusDay statusDay = getUserStatusDay(maps, userAttr);
                    statusDay.increRegister(1, 1);
                } catch (Exception e) {
                    LOG.error("stats user register error:", e);
                }
            }
        });

        GiftPeriodType periodType = GiftPeriodType.Day;
        GiftTargetType targetType = GiftTargetType.BET_TURNOVER;
        mUserAttrService.queryAllMember2(null, null, new Callback<UserAttr>() {
            @Override
            public void execute(UserAttr userAttr) {
                try {

                    boolean isTodayRecharge = false;
                    if(!StringUtils.isEmpty(userAttr.getFirstRechargeOrderno()))
                    {
                        DateTime firstRechargeTime = new DateTime(userAttr.getFirstRechargeTime());
                        isTodayRecharge = firstRechargeTime.getDayOfYear() == dayOfYear;
                    }

                    if(isTodayRecharge)
                    {
                        platformStatusDay.increFirstRecharge(userAttr.getFirstRechargeAmount());
                    }

                    boolean isActive = false;
                    BigDecimal totalBetAmount = GiftStatusHelper.getInstance().getAmount(periodType, dateTime, userAttr.getUsername(), targetType.getKey());
                    if(totalBetAmount != null && totalBetAmount.compareTo(BigDecimal.ZERO) > 0)
                    {
                        isActive = true;
                        platformStatusDay.increActive(1);
                    }


                    if(userAttr.getDirectStaffid() <= 0)
                    {
                        return;
                    }

                    UserStatusDay statusDay = getUserStatusDay(maps, userAttr);
                    if(isActive)
                    {
                        statusDay.increActive(1);
                    }

                    if(isTodayRecharge)
                    {
                        statusDay.increFirstRecharge(userAttr.getFirstRechargeAmount());
                    }
                } catch (Exception e) {
                    LOG.error("stats user active error:", e);
                }
            }
        });

        String uuid = UUIDUtils.getUUID();

        // 充值统计
        mRechargeOrderService.queryAll(startTimeString, endTimeString, new Callback<RechargeOrder>() {
            @Override
            public void execute(RechargeOrder orderInfo) {

                try {
                    OrderTxStatus txStatus = OrderTxStatus.getType(orderInfo.getStatus());
                    if(!(OrderTxStatus.CAPTURED == txStatus || OrderTxStatus.REALIZED == txStatus))
                    {
                        return;
                    }

                    UserAttr userAttr = mUserAttrService.find(false, orderInfo.getUserid());

                    String cachekey = createCacheKey(uuid, orderInfo.getUsername(), true);

                    long userRechargeCount = 0;
                    if(!CacheManager.getInstance().exists(cachekey))
                    {
                        userRechargeCount = 1;
                        CacheManager.getInstance().setString(cachekey, "1");
                    }

                    // 平台
                    platformStatusDay.increRecharge(1, userRechargeCount, orderInfo.getAmount());
                    if(userAttr.getDirectStaffid() <= 0)
                    {
                        return;
                    }

                    UserStatusDay statusDay = getUserStatusDay(maps, userAttr);
                    statusDay.increRecharge(1, userRechargeCount, orderInfo.getAmount());
                } catch (Exception e) {
                    LOG.error("stats user recharge error:");
                }
            }
        });

        // 提现统计
        mWithdrawOrderService.queryAll(startTimeString, endTimeString, new Callback<WithdrawOrder>() {
            @Override
            public void execute(WithdrawOrder orderInfo) {
                try {
                    OrderTxStatus txStatus = OrderTxStatus.getType(orderInfo.getStatus());
                    if(!(OrderTxStatus.CAPTURED == txStatus || OrderTxStatus.REALIZED == txStatus))
                    {
                        return;
                    }

                    UserAttr userAttr = mUserAttrService.find(false, orderInfo.getUserid());

                    String cachekey = createCacheKey(uuid, orderInfo.getUsername(), false);

                    long userRechargeCount = 0;
                    if(!CacheManager.getInstance().exists(cachekey))
                    {
                        userRechargeCount = 1;
                        CacheManager.getInstance().setString(cachekey, "1");
                    }

                    // 平台
                    platformStatusDay.increWithdraw(1, userRechargeCount, orderInfo.getAmount(), orderInfo.getFeemoney());

                    if(userAttr.getDirectStaffid() <= 0)
                    {
                        return;
                    }

                    UserStatusDay statusDay = getUserStatusDay(maps, userAttr);
                    statusDay.increWithdraw(1, userRechargeCount, orderInfo.getAmount(), orderInfo.getFeemoney());
                } catch (Exception e) {
                    LOG.error("stats user withdraw error:");
                }
            }
        });

        // 平台
        mUserStatusDayService.delete(pdate, 0, 0);
        mUserStatusDayService.addReport(pdate, 0, null, 0, null, platformStatusDay);

        // 所有员工
        Set<String> keys = maps.keySet();
        for(String key : keys)
        {
            UserStatusDay statusDay = maps.get(key);
            mUserStatusDayService.delete(pdate, statusDay.getAgentid(), statusDay.getStaffid());
            mUserStatusDayService.addReport(pdate, statusDay.getAgentid(), statusDay.getAgentname(), statusDay.getStaffid(), statusDay.getStaffname(),  statusDay);
        }

    }

    private UserStatusDay getUserStatusDay(Map<String, UserStatusDay> maps, UserAttr userAttr)
    {
        UserStatusDay statusDay = maps.get(userAttr.getDirectStaffname());
        if(statusDay == null)
        {
            statusDay = new UserStatusDay();
            statusDay.setAgentid(userAttr.getAgentid());
            statusDay.setAgentname(userAttr.getAgentname());
            statusDay.setStaffid(userAttr.getDirectStaffid());
            statusDay.setStaffname(userAttr.getDirectStaffname());

            maps.put(userAttr.getDirectStaffname(), statusDay);
        }
        return statusDay;
    }

    private String createCacheKey(String uuid, String username, boolean isRecharge)
    {
        String cachekey = ROOT_CACHE_KEY + uuid + username + isRecharge;
        return cachekey;
    }


}
