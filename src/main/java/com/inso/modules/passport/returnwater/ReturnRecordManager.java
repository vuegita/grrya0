package com.inso.modules.passport.returnwater;

import com.alibaba.druid.util.LRUCache;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.mq.MQManager;
import com.inso.framework.mq.MQSupport;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.*;
import com.inso.modules.common.config.PlarformConfig2;
import com.inso.modules.common.config.SystemConfig;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.business.model.WithdrawOrder;
import com.inso.modules.passport.business.service.RechargeOrderService;
import com.inso.modules.passport.business.model.RechargeOrder;
import com.inso.modules.passport.business.model.ReturnWaterOrder;
import com.inso.modules.passport.business.service.WithdrawOrderService;
import com.inso.modules.passport.gift.helper.GiftStatusHelper;
import com.inso.modules.passport.gift.model.GiftPeriodType;
import com.inso.modules.passport.gift.model.GiftTargetType;
import com.inso.modules.passport.money.model.UserMoney;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.returnwater.service.ReturnFirstRechargeUpOrderService;
import com.inso.modules.passport.returnwater.service.ReturnWaterOrderService;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.report.model.UserStatusDay;
import com.inso.modules.report.model.UserStatusV2Day;
import com.inso.modules.report.service.UserStatusV2DayService;
import com.inso.modules.web.service.ConfigService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class ReturnRecordManager {

    private static Log LOG = LogFactory.getLog(ReturnRecordManager.class);

    private static String ROOT_CACHE = ReturnRecordManager.class.getName();

    private static final int EXPIRES = CacheManager.EXPIRES_WEEK + 10;

    private static final int  DEF_TMP_EXPIRES = 7200;

    private static final String QUEUE_NAME = ReturnRecordManager.class.getName();
    private static MQSupport mq = MQManager.getInstance().getMQ(MQManager.MQType.REDIS);

    private static String MQ_EVENT_TYPE_NAME = "eventType";
    public static String MQ_EVENT_TYPE_RETURN_AMOUNT = "returnAmount";
    public static String MQ_EVENT_TYPE_RETURN_FIRST_RECHARGE_LV_AMOUNT = "returnFirstRechargeToLVAmount";
    public static String MQ_EVENT_TYPE_REG= "register";
    public static String MQ_EVENT_TYPE_REG_VALID= "reg_valid";

    private static final String mCacheValue = "1";

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private ConfigService mConfigService;

    @Autowired
    private ReturnWaterOrderService mReturnWaterOrderService;

    @Autowired
    private ReturnFirstRechargeUpOrderService mReturnFirstRechargeUpOrderService;

    @Autowired
    private UserStatusV2DayService mUserStatusV2DayService;

    @Autowired
    private UserMoneyService mUserMoneyService;

    @Autowired
    private RechargeOrderService mRechargeOrderService;

    @Autowired
    private WithdrawOrderService mWithdrawOrderService;

    private static boolean debug = true;

    private boolean isRunningTask = false;

    private boolean isRunningMQ = false;

    public ReturnRecordManager()
    {
        if(!MyEnvironment.isDev())
        {
            debug = false;
        }
    }

    public static void runBootMQ() {
        ReturnRecordManager mgr = SpringContextUtils.getBean(ReturnRecordManager.class);
        mgr.bootMQ();
    }
    private void bootMQ()
    {
        if(isRunningMQ)
        {
            return;
        }
        isRunningMQ = true;
        mq.subscribe(QUEUE_NAME, null, new Callback<String>() {
            @Override
            public void execute(String jsonStr) {
                JSONObject jsonObject = FastJsonHelper.toJSONObject(jsonStr);
                if(jsonObject == null)
                {
                    return;
                }

                BigDecimal amount = jsonObject.getBigDecimal("amount");
                String username = jsonObject.getString("username");
                String eventName = jsonObject.getString(MQ_EVENT_TYPE_NAME);
                int fromLevel = jsonObject.getIntValue("fromLevel");

                DateTime dateTime = DateTime.now();

                String cachekey = createCacheKey(username, dateTime.getDayOfYear(), null);
                UserStatusV2Day model = getModel(cachekey, null, username, -1);

                if(MQ_EVENT_TYPE_RETURN_AMOUNT.equalsIgnoreCase(eventName))
                {
                    BigDecimal originAmount = jsonObject.getBigDecimal("originAmount");
                    originAmount = BigDecimalUtils.getNotNull(originAmount);

                    if(fromLevel == 1)
                    {
                        BigDecimal newAmount = model.getReturnLv1Amount().add(amount);
                        model.setReturnLv1Amount(newAmount);

                        BigDecimal newAmount2 = model.getTradeLv1Volumn().add(originAmount);
                        model.setTradeLv1Volumn(newAmount2);
                    }
                    else if(fromLevel == 2)
                    {
                        BigDecimal newAmount = model.getReturnLv2Amount().add(amount);
                        model.setReturnLv2Amount(newAmount);

                        BigDecimal newAmount2 = model.getTradeLv2Volumn().add(originAmount);
                        model.setTradeLv2Volumn(newAmount2);
                    }
                }
                else if(MQ_EVENT_TYPE_RETURN_FIRST_RECHARGE_LV_AMOUNT.equalsIgnoreCase(eventName))
                {
                    if(fromLevel == 1)
                    {
                        BigDecimal newAmount = model.getReturnFirstRechargeLv1Amount().add(amount);
                        model.setReturnFirstRechargeLv1Amount(newAmount);
                    }
                    else if(fromLevel == 2)
                    {
                        BigDecimal newAmount = model.getReturnFirstRechargeLv2Amount().add(amount);
                        model.setReturnFirstRechargeLv2Amount(newAmount);
                    }
                }
                else if(MQ_EVENT_TYPE_REG.equalsIgnoreCase(eventName))
                {
                    if(fromLevel == 1)
                    {
                        model.increTotalLvCount(true);
                    }
                    else if(fromLevel == 2)
                    {
                        model.increTotalLvCount(false);
                    }
                }
                else if(MQ_EVENT_TYPE_REG_VALID.equalsIgnoreCase(eventName))
                {
                    if(fromLevel == 1)
                    {
                        model.increValidCount(true);
                    }
                    else if(fromLevel == 2)
                    {
                        model.increValidCount(false);
                    }
                }
                else
                {
                    return;
                }

                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(model), DEF_TMP_EXPIRES);
            }
        });
    }

    public static void sendMessage(String eventName, BigDecimal amount, String username, int fromLevel, BigDecimal originAmount)
    {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(MQ_EVENT_TYPE_NAME, eventName);
            jsonObject.put("username", username);
            if(amount != null)
            {
                jsonObject.put("amount", amount);
            }

            if(originAmount != null)
            {
                jsonObject.put("originAmount", originAmount);
            }
            jsonObject.put("fromLevel", fromLevel);
            mq.sendMessage(QUEUE_NAME, jsonObject.toJSONString());
        } catch (Exception e) {
            LOG.error("handle error:", e);
        }
    }

    public void doTask(DateTime fireTime, boolean writeToDb, boolean forceDoTask)
    {
        if(!forceDoTask && isRunningTask)
        {
            return;
        }

        isRunningTask = true;
        try {
            // 统计代理下级会员
            LRUCache<String, UserStatusV2Day> tmpCacheMaps = new LRUCache<>(200);
            LRUCache<String, UserStatusDay> tmpCacheMaps2 = new LRUCache<>(200);

            int dayOfYear = fireTime.getDayOfYear();
            String pdateStr = fireTime.toString(DateUtils.TYPE_YYYY_MM_DD);

            String beginTimeStr = DateUtils.getBeginTimeOfDay(pdateStr);
            Date beginDate = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, beginTimeStr);

            String endTimeStr = DateUtils.getEndTimeOfDay(pdateStr);
            Date endDate = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, endTimeStr);

            LOG.info("Fire date time: dayOfYear - " + dayOfYear + ", pdateStr = " + fireTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS));

            String uuid = UUIDUtils.getUUID();

            //
            statsInviteCount(uuid, tmpCacheMaps, tmpCacheMaps2, fireTime.minusDays(7), endDate, dayOfYear, pdateStr);
            statsReturnAmount(uuid, tmpCacheMaps, beginTimeStr, endTimeStr, dayOfYear, pdateStr);
            statsReturnFirstRechargeToUpAmount(uuid, tmpCacheMaps, beginTimeStr, endTimeStr, dayOfYear, pdateStr);

            statsRechargeInfo(uuid, tmpCacheMaps2, beginTimeStr, endTimeStr, dayOfYear, pdateStr);
            statsWithdrawInfo(uuid, tmpCacheMaps2, beginTimeStr, endTimeStr, dayOfYear, pdateStr);

            statsGameInfo(uuid, tmpCacheMaps, tmpCacheMaps2, beginDate, endDate, fireTime, dayOfYear, pdateStr);
            saveInfoToDB(uuid, tmpCacheMaps, tmpCacheMaps2, beginDate, endDate, fireTime, dayOfYear, pdateStr, writeToDb);

            tmpCacheMaps.clear();

            LOG.info("Finish .........");
        } catch (Exception e) {
            LOG.error("handle error:", e);
        } finally {
            isRunningTask = false;
        }
    }


//    private static String tmpUsername = "epamandaagroinfluencer_gmail";
    private void statsInviteCount(String uuid, LRUCache<String, UserStatusV2Day> tmpCacheMaps, LRUCache<String, UserStatusDay> tmpCacheMaps2, DateTime beginDate, Date endDate, int dayOfYear, String pdateStr)
    {
        BigDecimal limitTotalDeductCodeAmount = BigDecimalUtils.getNotNull(mConfigService.getBigDecimal(false, SystemConfig.VALID_INVITE_MEMBER_LIMIT_TOTAL_DEDUCT_CODE_AMOUNT.getKey()));
        float limitMinRecharge =  mConfigService.getFloat(false, PlarformConfig2.ADMIN_APP_PLATFORM_USER_USER_INVITE_FRIEND_TASK_MIN_RECHARGE.getKey());

        Date beginDateValue = null;
//        if(debug)
//        {
//            endDate = null;
//        }
//        else
//        {
//            beginDateValue = beginDate.toDate();
//        }
        beginDateValue = beginDate.toDate();

        Date pdate = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD, pdateStr);

        FundAccountType accountType = FundAccountType.Spot;
        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();

        mUserAttrService.queryAllMember2(beginDateValue, endDate, new Callback<UserAttr>() {
            public void execute(UserAttr userAttr) {

                DateTime firstRechargeDateTime = null;
                if(userAttr.getFirstRechargeTime() != null)
                {
                    firstRechargeDateTime = new DateTime(userAttr.getFirstRechargeTime());
                }

                boolean validRecharge = false;
                if(firstRechargeDateTime != null && dayOfYear == firstRechargeDateTime.getDayOfYear() && !StringUtils.isEmpty(userAttr.getFirstRechargeOrderno()))
                {
                    // 验证最低充值
                    boolean validMinRecharge = false;
                    RechargeOrder rechargeOrder = mRechargeOrderService.findByNo(userAttr.getFirstRechargeOrderno());
                    if(rechargeOrder != null && rechargeOrder.getAmount().floatValue() >= limitMinRecharge)
                    {
                        validMinRecharge = true;
                    }

                    // 验证最低扣款打码
                    if(validMinRecharge)
                    {
                        boolean validLimitTotalDeductCodeAmount = false;
                        UserMoney userMoney = mUserMoneyService.findMoney(false, userAttr.getUserid(), accountType, currencyType);
                        if(userMoney.getTotalDeductCodeAmount().compareTo(limitTotalDeductCodeAmount) >= 0)
                        {
                            validLimitTotalDeductCodeAmount = true;
                        }
                        validRecharge = validMinRecharge && validLimitTotalDeductCodeAmount;
                    }
                }

                DateTime registerTime = new DateTime(userAttr.getRegtime());
                int memberRegDayOfYear = registerTime.getDayOfYear();
                boolean isTodayRegister = memberRegDayOfYear == dayOfYear;

                if(userAttr.getDirectStaffid() > 0)
                {
                    String staffname = userAttr.getDirectStaffname();
                    long staffid = userAttr.getDirectStaffid();
                    String cachekey = createUserStatusV1CacheKey(staffname, dayOfYear, uuid);
                    UserStatusDay model = getUserStatusDayModel(cachekey, tmpCacheMaps2, staffname, staffid);
                    model.increRegister(1, userAttr.getParentid() > 0 ? 1 : 0);
                    CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(model));
                }

//                LOG.info("invite count: user=" + userAttr.getUsername() + ", parent = " + userAttr.getParentname() + ", validRecharge = " + validRecharge + ", isTodayRegister = " + isTodayRegister + ", registerTime = " + registerTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS) + ", regDayOfYear = " + memberRegDayOfYear);
                if(!(validRecharge || isTodayRegister))
                {
                    return;
                }

                if(userAttr.getParentid() > 0)
                {
                    String cachekey = createCacheKey(userAttr.getParentname(), dayOfYear, uuid);
                    UserStatusV2Day model = getModel(cachekey, tmpCacheMaps, userAttr.getParentname(), userAttr.getParentid());
//                    LOG.info("invite count: user=" + userAttr.getUsername() + ", parent=" + userAttr.getParentname() + ", lv1Count = " + model.getTotalLv1Count() + ", lv2Count = " + model.getTotalLv1Count());
                    if(isTodayRegister)
                    {
                        model.increTotalLvCount(true);
                    }
                    if(validRecharge)
                    {
                        model.increValidCount(true);
                    }

                    model.setPdate(pdate);
                    CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(model), EXPIRES);
                }

                if(userAttr.getGrantfatherid() > 0)
                {
                    String cachekey = createCacheKey(userAttr.getGrantfathername(), dayOfYear, uuid);
                    UserStatusV2Day model = getModel(cachekey, tmpCacheMaps, userAttr.getGrantfathername(), userAttr.getGrantfatherid());
                    if(isTodayRegister)
                    {
                        model.increTotalLvCount(false);
                    }
                    if(validRecharge)
                    {
                        model.increValidCount(false);
                    }
                    model.setPdate(pdate);
                    CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(model), DEF_TMP_EXPIRES);
                }

            }
        });
    }

    private void statsReturnAmount(String uuid, LRUCache<String, UserStatusV2Day> tmpCacheMaps, String beginDate, String endDate, int dayOfYear, String pdateStr)
    {
        if(debug)
        {
            beginDate = null;
            endDate = null;
        }

        Date pdate = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD, pdateStr);

        mReturnWaterOrderService.statsAmountByTime(true, beginDate, endDate, new Callback<ReturnWaterOrder>() {
            @Override
            public void execute(ReturnWaterOrder o) {

                try {
                    if(o.getFromLevel() <= 0 ||o.getFromLevel() > 2)
                    {
                        return;
                    }

                    String cachekey = createCacheKey(o.getUsername(), dayOfYear, uuid);
                    UserStatusV2Day model = getModel(cachekey, tmpCacheMaps, o.getUsername(), o.getUserid());
                    model.setPdate(pdate);

                    if(o.getFromLevel() == 1 )
                    {
                        model.increAmount(true, o.getAmount());
                    }
                    else if(o.getFromLevel() == 2 )
                    {
                        model.increAmount(false, o.getAmount());
                    }

                    CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(model), DEF_TMP_EXPIRES);
                } catch (Exception e) {

                } finally {
                    o.clearByDB();
                }
            }
        });
    }

    private void statsReturnFirstRechargeToUpAmount(String uuid, LRUCache<String, UserStatusV2Day> tmpCacheMaps, String beginDate, String endDate, int dayOfYear, String pdateStr)
    {
//        if(debug)
//        {
//            beginDate = null;
//            endDate = null;
//        }

        Date pdate = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD, pdateStr);

        mReturnFirstRechargeUpOrderService.statsAmountByTime(true, beginDate, endDate, new Callback<ReturnWaterOrder>() {
            @Override
            public void execute(ReturnWaterOrder o) {

                try {
                    if(o.getFromLevel() <= 0 ||o.getFromLevel() > 2)
                    {
                        return;
                    }

                    String cachekey = createCacheKey(o.getUsername(), dayOfYear, uuid);
                    UserStatusV2Day model = getModel(cachekey, tmpCacheMaps, o.getUsername(), o.getUserid());
                    model.setPdate(pdate);


                    if(o.getFromLevel() == 1)
                    {
                        BigDecimal newAmount = model.getReturnFirstRechargeLv1Amount().add(o.getAmount());
                        model.setReturnFirstRechargeLv1Amount(newAmount);
                    }
                    else if(o.getFromLevel() == 2)
                    {
                        BigDecimal newAmount = model.getReturnFirstRechargeLv2Amount().add(o.getAmount());
                        model.setReturnFirstRechargeLv2Amount(newAmount);
                    }

                    CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(model), DEF_TMP_EXPIRES);
                } catch (Exception e) {

                } finally {
                    o.clearByDB();
                }
            }
        });
    }

    private void statsRechargeInfo(String uuid, LRUCache<String, UserStatusDay> tmpCacheMaps, String beginDate, String endDate, int dayOfYear, String pdateStr)
    {
        String target = "recharge";
        Date pdate = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD, pdateStr);
        mRechargeOrderService.queryAll(beginDate, endDate, new Callback<RechargeOrder>() {
            @Override
            public void execute(RechargeOrder o) {

                try {
                    OrderTxStatus txStatus = OrderTxStatus.getType(o.getStatus());
                    if(txStatus != OrderTxStatus.REALIZED)
                    {
                        return;
                    }

                    UserAttr userAttr = mUserAttrService.find(false, o.getUserid());
                    if(userAttr == null || userAttr.getDirectStaffid() <= 0)
                    {
                        return;
                    }

                    String staffname = userAttr.getDirectStaffname();
                    long staffid = userAttr.getDirectStaffid();
                    String cachekey = createUserStatusV1CacheKey(staffname, dayOfYear, uuid);
                    UserStatusDay model = getUserStatusDayModel(cachekey, tmpCacheMaps, staffname, staffid);
                    model.setPdate(pdate);

                    BigDecimal newAmount = model.getTotalRechargeAmount().add(o.getAmount());
                    model.setTotalRechargeAmount(newAmount);

                    String existCacheKey = createItemExistCacheKey(o.getUsername(), dayOfYear, uuid, target);
                    boolean existCount = CacheManager.getInstance().exists(existCacheKey);
                    long userRechargeCount = 0;
                    if(!existCount)
                    {
                        userRechargeCount = 1;
                        CacheManager.getInstance().setString(existCacheKey, mCacheValue);
                    }
                    model.increRecharge(1,userRechargeCount, o.getAmount());

                    CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(model), DEF_TMP_EXPIRES);
                } catch (Exception e) {
                    LOG.error("handle recharge status error:", e);
                }
            }
        });
    }

    private void statsWithdrawInfo(String uuid, LRUCache<String, UserStatusDay> tmpCacheMaps, String beginDate, String endDate, int dayOfYear, String pdateStr)
    {

        String target = "withdraw";


        Date pdate = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD, pdateStr);
        mWithdrawOrderService.queryAll(beginDate, endDate, new Callback<WithdrawOrder>() {
            @Override
            public void execute(WithdrawOrder o) {

                try {
                    OrderTxStatus txStatus = OrderTxStatus.getType(o.getStatus());
                    if(txStatus != OrderTxStatus.REALIZED)
                    {
                        return;
                    }

                    UserAttr userAttr = mUserAttrService.find(false, o.getUserid());
                    if(userAttr == null || userAttr.getDirectStaffid() <= 0)
                    {
                        return;
                    }

                    String staffname = userAttr.getDirectStaffname();
                    long staffid = userAttr.getDirectStaffid();
                    String cachekey = createUserStatusV1CacheKey(staffname, dayOfYear, uuid);
                    UserStatusDay model = getUserStatusDayModel(cachekey, tmpCacheMaps, staffname, staffid);
                    model.setPdate(pdate);

                    //
                    String existCacheKey = createItemExistCacheKey(o.getUsername(), dayOfYear, uuid, target);
                    boolean existCount = CacheManager.getInstance().exists(existCacheKey);
                    long userWithdrawCount = 0;
                    if(!existCount)
                    {
                        userWithdrawCount = 1;
                        CacheManager.getInstance().setString(existCacheKey, mCacheValue);
                    }
                    model.increWithdraw(1, userWithdrawCount, o.getAmount(), o.getFeemoney());

                    CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(model), DEF_TMP_EXPIRES);
                } catch (Exception e) {
                    LOG.error("handle recharge status error:", e);
                }
            }
        });
    }

    public void statsGameInfo(String uuid, LRUCache<String, UserStatusV2Day> tmpCacheMaps, LRUCache<String, UserStatusDay> tmpCacheMaps2, Date beginDate, Date endDate, DateTime dateTime, int dayOfYear, String pdateStr)
    {
        if(debug)
        {
            beginDate = null;
            endDate = null;
        }

        Date pdate = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD, pdateStr);

        GiftStatusHelper giftStatusHelper = GiftStatusHelper.getInstance();
        GiftPeriodType periodType = GiftPeriodType.Day;

        String target = "member_active_count";
        mUserAttrService.queryAllMemberByUserReport(dateTime, new Callback<UserAttr>() {
            public void execute(UserAttr userAttr) {

                try {
                    String fromUsername = userAttr.getUsername();
                    BigDecimal totalBetAmount = giftStatusHelper.getAmount(periodType, dateTime, fromUsername, GiftTargetType.BET_TURNOVER.getKey());
                    if(totalBetAmount == null || totalBetAmount.compareTo(BigDecimal.ZERO) <= 0)
                    {
                        return;
                    }

                    String myUsername = userAttr.getUsername();
                    String cachekey = createCacheKey(myUsername, dayOfYear, uuid);
                    UserStatusV2Day model = getModel(cachekey, tmpCacheMaps, myUsername, userAttr.getUserid());
                    model.setPdate(pdate);
                    model.loadTradeData(dateTime, myUsername);

                    String existCacheKey = createItemExistCacheKey(userAttr.getUsername(), dayOfYear, uuid, target);
                    boolean existCount = CacheManager.getInstance().exists(existCacheKey);
                    boolean addActiveCount = false;
                    if(!existCount)
                    {
                        // active
                        addActiveCount = true;

                        // save status
                        CacheManager.getInstance().setString(existCacheKey, mCacheValue);
                    }


                    CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(model), EXPIRES);
                    if(userAttr.getParentid() > 0)
                    {
                        updateGameInfo(uuid, totalBetAmount, userAttr.getParentname(), userAttr.getParentid(), tmpCacheMaps, dayOfYear, pdate, true, addActiveCount);
                    }

                    if(userAttr.getGrantfatherid() > 0)
                    {
                        updateGameInfo(uuid, totalBetAmount, userAttr.getGrantfathername(), userAttr.getGrantfatherid(), tmpCacheMaps, dayOfYear, pdate, false, false);
                    }

                    if(addActiveCount && userAttr.getDirectStaffid() > 0)
                    {
                        String staffname = userAttr.getDirectStaffname();
                        long staffid = userAttr.getDirectStaffid();
                        String cachekey2 = createUserStatusV1CacheKey(staffname, dayOfYear, uuid);
                        UserStatusDay model2 = getUserStatusDayModel(cachekey, tmpCacheMaps2, staffname, staffid);
                        model2.increActive(1);
                        CacheManager.getInstance().setString(cachekey2, FastJsonHelper.jsonEncode(model2));
                    }


                } catch (Exception e) {
                    LOG.error("handle error:", e);
                }
            }
        });
    }

    public void saveInfoToDB(String uuid, LRUCache<String, UserStatusV2Day> tmpCacheMaps, LRUCache<String, UserStatusDay> tmpCacheMaps2, Date beginDate, Date endDate, DateTime dateTime, int dayOfYear, String pdateStr, boolean writeDB)
    {

        // 查询所有用户
        beginDate = null;
        endDate = null;

        Date pdate = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD, pdateStr);

//        mUserAttrService.queryAllMemberByUserReport(dateTime, new Callback<UserAttr>() {
        mUserAttrService.queryAllMember2(null, null, new Callback<UserAttr>() {
            public void execute(UserAttr userAttr) {
                doSaveGameInfo(pdate, userAttr, uuid, tmpCacheMaps, tmpCacheMaps2, null, null, dateTime, dayOfYear, pdateStr, writeDB);
            }
        });
    }

    private void doSaveGameInfo(Date pdate, UserAttr userAttr, String uuid, LRUCache<String, UserStatusV2Day> tmpCacheMaps, LRUCache<String, UserStatusDay> tmpCacheMaps2, Date beginDate, Date endDate, DateTime dateTime, int dayOfYear, String pdateStr, boolean writeDB)
    {

        try {
            if(userAttr.getDirectStaffid() > 0)
            {
                //
                String cachekey = createUserStatusV1CacheKey(userAttr.getDirectStaffname(), dayOfYear, uuid);
                UserStatusDay model = getUserStatusDayModel(cachekey, tmpCacheMaps2, userAttr.getDirectStaffname(), userAttr.getDirectStaffid());

                if(!model.isWriteToCache())
                {
                    model.setPdate(pdate);
                    model.setWriteToCache(true);

                    String newCachekey = createUserStatusV1CacheKey(userAttr.getDirectStaffname(), dayOfYear, null);
                    CacheManager.getInstance().setString(newCachekey, FastJsonHelper.jsonEncode(model), EXPIRES);
                }
            }

            String username = userAttr.getUsername();
            String cachekey = createCacheKey(username, dayOfYear, uuid);
            UserStatusV2Day model = getModel(cachekey, tmpCacheMaps, username, userAttr.getUserid());
            model.loadTradeData(dateTime, username);
            model.setPdate(pdate);

//                    System.out.println("userid = " + userAttr.getUserid() + ", username = " + username);

//            LOG.info("uuid = " + uuid + ", " + pdateStr + ": username = " + username + ", lv1 = " + model.getTotalLv1Count() + ", lv2 = " + model.getTotalLv2Count() + ", isEmpty = " + model.verifyEmpty());
//            LOG.info("uuid = " + uuid + ", " + pdateStr + ": username = " + username + ", lv1 = " + model.getTotalLv1Count() + ", lv2 = " + model.getTotalLv2Count() + ", isEmpty = " + model.verifyEmpty() + ", " + FastJsonHelper.jsonEncode(model));
            if(model.verifyEmpty())
            {
                return;
            }


            if(!model.verifyEmpty() && writeDB)
            {
                model.setAgentid(userAttr.getAgentid());
                model.setAgentname(userAttr.getAgentname());
                model.setStaffid(userAttr.getDirectStaffid());
                model.setStaffname(userAttr.getDirectStaffname());
                try {
                    mUserStatusV2DayService.addLog(pdate, model);
                } catch (Exception e) {
                    mUserStatusV2DayService.delete(pdate, model);
                    mUserStatusV2DayService.addLog(pdate, model);
//                    LOG.info("delete and addLog for " + username + ", pdate = " + pdateStr);
                }
            }

            String newCachKey = createCacheKey(username, dayOfYear, null);
            if(!writeDB)
            {
                UserStatusV2Day cacheModel = getModel(cachekey, null, username, userAttr.getUserid());
                model.updateIfHashLatest(cacheModel);
            }

            CacheManager.getInstance().setString(newCachKey, FastJsonHelper.jsonEncode(model), EXPIRES);
        } catch (Exception e) {
            LOG.error("handle error:", e);
//                    e.printStackTrace();
        }
    }

    private void updateGameInfo(String uuid, BigDecimal totalBetAmount, String targetUsername, long targetUserid, LRUCache<String, UserStatusV2Day> tmpCacheMaps,
                                int dayOfYear, Date pdate, boolean isLv1, boolean incryActiveCount)
    {
        try {
            String cachekey = createCacheKey(targetUsername, dayOfYear, uuid);
            UserStatusV2Day model = getModel(cachekey, tmpCacheMaps, targetUsername, targetUserid);
            if(isLv1)
            {
                model.setTradeLv1Volumn(totalBetAmount);

//                if(incryActiveCount)
//                {
//                    model.setTotalLv1ActiveCount(model.getTotalLv1ActiveCount() + 1);
//                }
//
//                if(!model.isExistMemberbalance())
//                {
//                    // member balance
//                    BigDecimal totalMemberBalance = mUserMoneyService.queryAllMoneyByParentUserid(false, targetUserid);
//                    totalMemberBalance = BigDecimalUtils.getNotNull(totalMemberBalance);
//                    model.setTotalLv1MemberBalance(totalMemberBalance);
//                    model.setExistMemberbalance(true);
//                }
            }
            else
            {
                model.setTradeLv2Volumn(totalBetAmount);
            }
            model.setPdate(pdate);
            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(model), DEF_TMP_EXPIRES);
        } catch (Exception e) {
        }
    }

    private UserStatusV2Day getModel(String cachekey, Map<String, UserStatusV2Day> maps, String username, long userid)
    {
        UserStatusV2Day statsInfo = maps != null ? maps.get(username) : null;

        if(statsInfo == null)
        {
            statsInfo = CacheManager.getInstance().getObject(cachekey, UserStatusV2Day.class);
        }

        if(statsInfo == null)
        {
            statsInfo = new UserStatusV2Day();
            if(maps != null)
            {
                maps.put(username, statsInfo);
            }
        }
        if(userid > 0)
        {
            statsInfo.setUserid(userid);
        }
        statsInfo.setUsername(username);
        return statsInfo;
    }

    private UserStatusDay getUserStatusDayModel(String cachekey, Map<String, UserStatusDay> maps, String username, long staffid)
    {
        UserStatusDay statsInfo = maps != null ? maps.get(username) : null;

        if(statsInfo == null)
        {
            statsInfo = CacheManager.getInstance().getObject(cachekey, UserStatusDay.class);
        }

        if(statsInfo == null)
        {
            statsInfo = new UserStatusDay();
            if(maps != null)
            {
                maps.put(username, statsInfo);
            }
        }
        if(staffid > 0)
        {
            statsInfo.setStaffid(staffid);
        }
        statsInfo.setStaffname(username);
        return statsInfo;
    }

    private String createCacheKey(String userame, int dayOfYear, String tmpId)
    {
        String cachekey = ROOT_CACHE + userame + dayOfYear;
        if(!StringUtils.isEmpty(tmpId))
        {
            cachekey = cachekey + tmpId;
        }
        return cachekey;
    }

    private String createUserStatusV1CacheKey(String userame, int dayOfYear, String tmpId)
    {
        String cachekey = ROOT_CACHE + "_user_status_v1_" + userame + dayOfYear;
        if(!StringUtils.isEmpty(tmpId))
        {
            cachekey = cachekey + tmpId;
        }
        return cachekey;
    }

    private String createItemExistCacheKey(String userame, int dayOfYear, String tmpId, String target)
    {
        String cachekey = ROOT_CACHE + "_exist_item_" + target + userame + dayOfYear;
        if(!StringUtils.isEmpty(tmpId))
        {
            cachekey = cachekey + tmpId;
        }
        return cachekey;
    }

    public List<UserStatusV2Day> getDataList(String username)
    {
        DateTime dateTime = DateTime.now();
        List rsList = Collections.emptyList();

        UserStatusV2Day firstDataInfo = getDataInfo(username, dateTime.getDayOfYear());
        if(firstDataInfo == null)
        {
            return rsList;
        }

        rsList = Lists.newArrayList();
        rsList.add(firstDataInfo);

        for(int i = 1; i < 7; i ++)
        {
            DateTime lastTime = dateTime.minusDays(i);
            UserStatusV2Day dataInfo = getDataInfo(username, lastTime.getDayOfYear());
            if(dataInfo == null)
            {
                return rsList;
            }
            rsList.add(dataInfo);
        }
        return rsList;
    }

    public UserStatusV2Day getDataInfo(String username, int dayOfyear)
    {
        String cachekey = createCacheKey(username, dayOfyear, null);
        return CacheManager.getInstance().getObject(cachekey, UserStatusV2Day.class);
    }

    public UserStatusDay getAgentDataInfo(String username, int dayOfyear)
    {
        String cachekey = createUserStatusV1CacheKey(username, dayOfyear, null);
        return CacheManager.getInstance().getObject(cachekey, UserStatusDay.class);
    }


    public static void testRun()
    {
        ReturnRecordManager mgr = SpringContextUtils.getBean(ReturnRecordManager.class);

        String pdateStr = "2023-11-06 00:00:01";
//        String pdateStr = "2023-03-16 00:00:00";

        Date pdate = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, pdateStr);

        DateTime dateTime = new DateTime(pdate);
        mgr.doTask(dateTime, true, true);

//        DateTime dateTime = DateTime.now();
//        mgr.doTask(dateTime.minusDays(1), true);
//        mgr.doTask(dateTime.minusDays(2), true);
//        mgr.doTask(dateTime.minusDays(3), true);
//        mgr.doTask(dateTime.minusDays(4), true);
//        mgr.doTask(dateTime.minusDays(5), true);

//        String username = "c_29c2e84c_0xFA730bd82c7E8721aF28c8A0ed56Bf9041E94dFb";
//        FastJsonHelper.prettyJson(mgr.getDataList(username));
    }

    public static void main(String[] args) {
        String time = "2022-01-01 00:12:12";

        Date date = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, time);

        DateTime dateTime = new DateTime(date);

        System.out.println(dateTime.getMinuteOfDay());
    }

}
