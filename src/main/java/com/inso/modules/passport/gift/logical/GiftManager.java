package com.inso.modules.passport.gift.logical;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.CollectionUtils;
import com.inso.modules.common.model.*;
import com.inso.modules.passport.business.model.DayPresentOrder;
import com.inso.modules.passport.business.model.PresentBusinessType;
import com.inso.modules.passport.business.service.DayPresentOrderService;
import com.inso.modules.passport.gift.helper.GiftStatusHelper;
import com.inso.modules.passport.gift.model.GiftConfigInfo;
import com.inso.modules.passport.gift.model.GiftPeriodType;
import com.inso.modules.passport.gift.model.GiftTargetType;
import com.inso.modules.passport.gift.service.GiftConfigService;
import com.inso.modules.passport.money.PayApiManager;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Component
public class GiftManager {

    private static final String ROOT_CACHE = GiftManager.class.getName();
    private static final String EXIST_PRESENT_CACHE = ROOT_CACHE + "exist_present_cache";

    private static Log LOG = LogFactory.getLog(GiftManager.class);

    private List<GiftConfigInfo> mConfigItemList;


    @Autowired
    private GiftConfigService mGiftService;

    @Autowired
    private DayPresentOrderService mDayPresentOrderService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private PayApiManager mPayApiManager;

    private FundAccountType mAccountType = FundAccountType.Spot;
    private ICurrencyType mCurrencyType = ICurrencyType.getSupportCurrency();

    private long mLatestRefresh = -1;


    private void refresh()
    {
        long ts = System.currentTimeMillis();
        if(ts != -1 && ts - mLatestRefresh <= 60_000)
        {
            return;
        }

        this.mConfigItemList = mGiftService.queryAll(false);
    }

    public GiftConfigInfo getByKey(GiftTargetType targetType)
    {
        refresh();
        if(CollectionUtils.isEmpty(mConfigItemList))
        {
            return null;
        }

        for(GiftConfigInfo tmp : mConfigItemList)
        {
            if(targetType.getKey().equalsIgnoreCase(tmp.getTargetType()))
            {
                return tmp;
            }
        }
        return null;
    }


    public List getDataList(UserInfo userInfo, boolean getIfExist)
    {
        refresh();
        if(CollectionUtils.isEmpty(mConfigItemList))
        {
            return Collections.emptyList();
        }

        List rsList = Lists.newArrayList();
        DateTime dateTime = new DateTime();
        for(GiftConfigInfo model : mConfigItemList)
        {
            GiftPeriodType periodType = GiftPeriodType.getType(model.getPeriodType());
            BigDecimal statusAmount = GiftStatusHelper.getInstance().getAmount(periodType, dateTime, userInfo.getName(), model.getTargetType());

            String taskid = model.getTargetType() + model.getId();
            boolean hasReceive = hasReceive(dateTime, userInfo, taskid);

            if(getIfExist)
            {
                if(statusAmount == null || statusAmount.compareTo(BigDecimal.ZERO) <= 0)
                {
                    continue;
                }

                if(statusAmount.compareTo(model.getLimitAmount()) < 0)
                {
                    continue;
                }

                if(hasReceive)
                {
                    continue;
                }

            }
            model.setCurrentAmount(statusAmount);
            addItem(rsList, model, statusAmount, hasReceive);
        }

        return rsList;
    }

    private void addItem(List rsList, GiftConfigInfo model, BigDecimal statusAmount, boolean hasReceive)
    {
        JSONObject item = new JSONObject();
        item.put("id", model.getId());
        item.put("title", model.getTitle());
        item.put("desc", model.getDesc());
        item.put("type", model.getTargetType());
        item.put("periodType", model.getPeriodType());
        // 赠送金额
        item.put("amount", model.getPresentAmount());
        item.put("limitAmount", model.getLimitAmount());
        item.put("statusAmount", statusAmount);
        item.put("hasReceive", hasReceive);
        rsList.add(item);
    }

    private boolean hasReceive(DateTime dateTime, UserInfo userInfo, String taskid)
    {
        PresentBusinessType businessType = PresentBusinessType.GAME_BET_DAY;
        String outradeno = mDayPresentOrderService.generateOutTradeNo(userInfo.getId(), businessType, taskid, dateTime);
        String cachekey = EXIST_PRESENT_CACHE + outradeno;
        return CacheManager.getInstance().exists(cachekey);
    }

    public void receive(ApiJsonTemplate apiJsonTemplate, GiftConfigInfo model, UserInfo userInfo)
    {
        DateTime createtime = new DateTime();
        BusinessType systemBusinessType = BusinessType.GAME_BET_RETURN_WATER_2_SELF_PRESENTATION;
        PresentBusinessType businessType = PresentBusinessType.GAME_BET_DAY;

        int expires = GiftStatusHelper.EXPIRES;
        GiftPeriodType periodType = GiftPeriodType.getType(model.getPeriodType());
        if(periodType == GiftPeriodType.Week)
        {
            businessType = PresentBusinessType.GAME_BET_WEEK;
            expires = GiftStatusHelper.EXPIRES_WEEK;
        }

        String taskid = model.getTargetType() + model.getId();
        String outradeno = mDayPresentOrderService.generateOutTradeNo(userInfo.getId(), businessType, taskid, createtime);

        String cachekey = EXIST_PRESENT_CACHE + outradeno;
        if(CacheManager.getInstance().exists(cachekey))
        {
//            LOG.info("receiv cache success, outTradeno = " + outradeno + ", taskid = " + taskid + ", username = " + userInfo.getName() + ", ");
            apiJsonTemplate.setJsonResult(SystemErrorResult.SUCCESS);
            return;
        }

        UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());
        BigDecimal presentationAmount = model.getPresentAmount();
        RemarkVO remarkVO = RemarkVO.create("From gift by " + model.getTargetType() + ", period type = " + model.getPeriodType());

        DayPresentOrder existOrderInfo = mDayPresentOrderService.find(false, outradeno);

        String orderno = null;
        if(existOrderInfo != null)
        {
            OrderTxStatus txStatus = OrderTxStatus.getType(existOrderInfo.getStatus());
            if(txStatus == OrderTxStatus.REALIZED)
            {
//                LOG.info("receiv db success, outTradeno = " + outradeno + ", taskid = " + taskid + ", username = " + userInfo.getName() + ", ");
                CacheManager.getInstance().setString(cachekey, taskid, expires);
                apiJsonTemplate.setJsonResult(SystemErrorResult.SUCCESS);
                return;
            }
            orderno = existOrderInfo.getNo();
//            LOG.info("receiv old order success, outTradeno = " + outradeno + ", taskid = " + taskid + ", username = " + userInfo.getName() + ", ");
        }
        else
        {
            orderno = mDayPresentOrderService.createOrder(outradeno, mCurrencyType, userAttr, businessType, presentationAmount, remarkVO);
//            LOG.info("receiv new order success, outTradeno = " + outradeno + ", taskid = " + taskid + ", username = " + userInfo.getName() + ", ");
        }

        ErrorResult errorResult = mPayApiManager.doPlatformPresentation(mAccountType, mCurrencyType, systemBusinessType, orderno, userInfo, presentationAmount, remarkVO);
        if(errorResult == SystemErrorResult.SUCCESS)
        {
            mDayPresentOrderService.updateTxStatus(orderno, OrderTxStatus.REALIZED, userInfo.getId(), null, null, null);
            CacheManager.getInstance().setString(cachekey, taskid, expires);
//            LOG.info("receiv write db success, outTradeno = " + outradeno + ", taskid = " + taskid + ", username = " + userInfo.getName() + ", ");
        }
    }




}
