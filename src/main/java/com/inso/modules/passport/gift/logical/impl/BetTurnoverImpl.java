//package com.inso.modules.passport.gift.logical.impl;
//
//import com.inso.framework.bean.ErrorResult;
//import com.inso.framework.bean.SystemErrorResult;
//import com.inso.framework.cache.CacheManager;
//import com.inso.framework.log.Log;
//import com.inso.framework.log.LogFactory;
//import com.inso.framework.service.Callback;
//import com.inso.framework.spring.SpringContextUtils;
//import com.inso.framework.utils.DateUtils;
//import com.inso.framework.utils.StringUtils;
//import com.inso.framework.utils.UUIDUtils;
//import com.inso.modules.admin.config.PlatformConfig;
//import com.inso.modules.common.model.*;
//import com.inso.modules.passport.gift.logical.GiftManager;
//import com.inso.modules.passport.gift.model.GiftStatusInfo;
//import com.inso.modules.passport.gift.model.GiftTargetType;
//import com.inso.modules.passport.business.model.PresentBusinessType;
//import com.inso.modules.passport.business.service.DayPresentOrderService;
//import com.inso.modules.passport.money.PayApiManager;
//import com.inso.modules.passport.returnwater.ReturnWaterSelfManager;
//import com.inso.modules.passport.user.model.UserAttr;
//import com.inso.modules.passport.user.model.UserInfo;
//import com.inso.modules.passport.user.service.UserAttrService;
//import com.inso.modules.passport.user.service.UserService;
//import com.inso.modules.report.model.MemberReport;
//import com.inso.modules.report.service.UserReportService;
//import com.inso.modules.web.service.ConfigService;
//import org.joda.time.DateTime;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//import java.util.Date;
//
//@Component
//public class BetTurnoverImpl extends GiftProcessor {
//
//    private static Log LOG = LogFactory.getLog(ReturnWaterSelfManager.class);
//
//    private static final String DEFAULT_CACHE_KEY = BetTurnoverImpl.class.getName();
//
//    @Autowired
//    private UserService mUserService;
//
//    @Autowired
//    private UserAttrService mUserAttrService;
//
//    @Autowired
//    private ConfigService mConfigService;
//
//    @Autowired
//    private UserReportService mUserReportService;
//
//    @Autowired
//    private PayApiManager mPayApiManager;
//
//    @Autowired
//    private DayPresentOrderService mPresentOrderService;
//
//    private String mTaskId = "1";
//    private FundAccountType mAccountType = FundAccountType.Spot;
//    private ICurrencyType mCurrencyType = ICurrencyType.getSupportCurrency();
//
//    @Override
//    public void doTask(DateTime dateTime) {
//        doBgTask(dateTime);
//    }
//
//    @Override
//    public GiftTargetType getType() {
//        return GiftTargetType.BET_TURNOVER;
//    }
//
//    @Override
//    public boolean receive(UserInfo userInfo, GiftStatusInfo giftInfo) {
//        try {
//            PresentBusinessType presentBusinessType = PresentBusinessType.DAY_RETURN_TO_SELF;
//            BusinessType businessType = BusinessType.GAME_BET_RETURN_WATER_2_SELF_PRESENTATION;
//            RemarkVO remarkVO = RemarkVO.create("game_bet_return_water_2_self  presentation");
//
//            DateTime dateTime = new DateTime(giftInfo.getExpiresTime());
//            BigDecimal presentationAmount = giftInfo.getAmount();
//
//            UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());
//
//
//            String outradeno = mPresentOrderService.generateOutTradeNo(userInfo.getId(), presentBusinessType, mTaskId, dateTime);
//
//            String orderno = mPresentOrderService.createOrder(outradeno, mCurrencyType, userAttr, presentBusinessType, presentationAmount, remarkVO);
//
//            ErrorResult errorResult = mPayApiManager.doPlatformPresentation(mAccountType, mCurrencyType, businessType, orderno, userInfo, presentationAmount, remarkVO);
//            if(errorResult == SystemErrorResult.SUCCESS)
//            {
//                mPresentOrderService.updateTxStatus(orderno, OrderTxStatus.REALIZED, -1, null, null, null);
//                // 更新缓存
////                mPresentOrderService.find(false, outradeno);
//            }
//
//            return true;
//        } catch (Exception e) {
//            LOG.error("do game_return_water_self_presentation error:", e);
//        }
//
//        return false;
//    }
//
//    private void doBgTask(DateTime dateTime)
//    {
//        //获取反水比例小于等于0是不执行
//        ConfigService configService = SpringContextUtils.getBean(ConfigService.class);
//        BigDecimal value = configService.getBigDecimal(false, PlatformConfig.ADMIN_PLATFORM_CONFIG_GAME_BET_RETURN_WATER_2_SELF);
//        if(value.compareTo(BigDecimal.ZERO)<1){
//            return;
//        }
//
//        // 查询昨日会员每日统计
//        String pdateStr = dateTime.toString(DateUtils.TYPE_YYYY_MM_DD);
//        String beginTime = DateUtils.getBeginTimeOfDay(pdateStr);
//        String endTime = DateUtils.getEndTimeOfDay(pdateStr);
//
//        Date expiresDate = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, endTime);
//        int todayOfYear = dateTime.getDayOfYear();
//
//        Status status = Status.ENABLE;
//
//        GiftStatusInfo giftInfo = new GiftStatusInfo();
//
//        mUserReportService.queryAllMemberReport(beginTime, endTime, new Callback<MemberReport>() {
//            @Override
//            public void execute(MemberReport memberReport) {
//                BigDecimal businessDeduct=memberReport.getBusinessDeduct();
//                if(businessDeduct.compareTo(BigDecimal.ZERO) <= 0){
//                    return;
//                }
//
//                BigDecimal presentationAmount = businessDeduct.multiply(value);
//                String presentCacheKey = getPresentCacheKey(memberReport.getUsername(), todayOfYear, StringUtils.getEmpty());
//                if(CacheManager.getInstance().exists(presentCacheKey))
//                {
//                    // 已赠送，则不在赠送
//                    LOG.info("game_bet_return_water_2_self presentation  " + presentCacheKey);
//                    return;
//                }
//
//                String id = UUIDUtils.getUUID();
//
//                giftInfo.setId(id);
//                giftInfo.setAmount(presentationAmount);
//                giftInfo.setExpiresTime(expiresDate);
//                giftInfo.setTitle(StringUtils.getEmpty());
//                giftInfo.setDesc(StringUtils.getEmpty());
//                giftInfo.setStatus(status.getKey());
//                giftInfo.setUsername(memberReport.getUsername());
//
//                GiftManager.getInstance().addItem(memberReport.getUserid(), giftInfo, dateTime);
//
//                CacheManager.getInstance().setString(presentCacheKey, "1", CacheManager.EXPIRES_DAY);
//            }
//        });
//
//    }
//
//    /**
//     * 今日已赠送的用户，要缓存起来
//     * @param parentName
//     * @return
//     */
//    private static String getPresentCacheKey(String parentName, int todayOfYear, String businessKey)
//    {
//        return DEFAULT_CACHE_KEY + "_has_present_" + todayOfYear + parentName + businessKey;
//    }
//}
