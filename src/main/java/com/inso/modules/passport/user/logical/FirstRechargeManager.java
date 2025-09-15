package com.inso.modules.passport.user.logical;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.conf.MyConfiguration;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.config.PlarformConfig2;
import com.inso.modules.common.model.*;
import com.inso.modules.passport.business.PlatformPayManager;
import com.inso.modules.passport.business.model.DayPresentOrder;
import com.inso.modules.passport.business.model.PresentBusinessType;
import com.inso.modules.passport.business.model.RechargeOrder;
import com.inso.modules.passport.business.service.DayPresentOrderService;
import com.inso.modules.passport.money.PayApiManager;
import com.inso.modules.passport.returnwater.ReturnRecordManager;
import com.inso.modules.passport.returnwater.service.ReturnFirstRechargeUpOrderService;
import com.inso.modules.passport.returnwater.service.ReturnFirstRechargeUpOrderServiceImpl;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.SystemRunningMode;
import com.inso.modules.web.service.ConfigService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

@Component
public class FirstRechargeManager {

    private static Log LOG = LogFactory.getLog(FirstRechargeManager.class);

    private String KEY_AMOUNT = "amount";
    private String KEY_PRESENT_AMOUNT = "presentAmount";
    private String KEY_RATE = "rate";
    private String KEY_TIPS = "remark";

    @Autowired
    private DayPresentOrderService mDayPresentOrderService;

    @Autowired
    private PayApiManager mPayApiManager;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private UserService mUserSerevice;

    @Autowired
    private ConfigService mConfigService;

    @Autowired
    private PlatformPayManager mPlatformPayManager;

    @Autowired
    private PayApiManager payApiManager;

    @Autowired
    private ReturnFirstRechargeUpOrderService mReturnFirstRechargeUpOrderService;

    private boolean enablPresent;

    private List<JSONObject> mPresentDeFList = Lists.newArrayList();
    private List<JSONObject> mNoPresentDeFList = Lists.newArrayList();
    private int mDefListSize;

    private long mUpRechargeAmounArrLastRefreshTime = -1;

    public FirstRechargeManager()
    {
        addDefault();
        this.mDefListSize = mPresentDeFList.size();

        this.enablPresent = MyConfiguration.getInstance().getBoolean("user.first_recharge.uni_amount_present");
    }

    private void initConf()
    {
        long ts = System.currentTimeMillis();
        if(this.mUpRechargeAmounArrLastRefreshTime > 0 && ts - this.mUpRechargeAmounArrLastRefreshTime <= 60_000)
        {
            return;
        }
        try {
            String value = mConfigService.getValueByKey(false, PlarformConfig2.ADMIN_APP_PLATFORM_USER_RECHARGE_AMOUNT_BTN_LIST.getKey());
            if(StringUtils.isEmpty(value))
            {
                return;
            }

            char split = '|';
            String[] arr = StringUtils.split(value, split);
            if(arr == null || arr.length <= 0)
            {
                return;
            }

            int index = 0;
            for (String line : arr)
            {
                if(index >= 6)
                {
                    // 最大6个
                    break;
                }

                int amount = StringUtils.asInt(line);
                if(amount <= 0)
                {
                    return;
                }
                replaceItem(amount, index);
                index ++;
            }
            this.mUpRechargeAmounArrLastRefreshTime = ts;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.mUpRechargeAmounArrLastRefreshTime = ts;
        }

    }

    private void addDefault()
    {
        addItem(10, 0.1f, "Bonus ");
        addItem(100, 0.15f, "Bonus ");
        addItem(500, 0.2f, "Bonus ");
        addItem(2000, 0.3f, "Bonus ");
        addItem(5000, 0.5f, "Bonus ");
        addItem(10000, 0.8f, "Bonus ");
    }

    /**
     * 首次充值给上线
     * @param userInfo
     * @param userAttr
     * @param rechargeOrder
     * @return
     */
    public boolean addPresentToLv1_2(UserInfo userInfo, UserAttr userAttr, RechargeOrder rechargeOrder) {

        try {
            if(userAttr.getParentid() <= 0)
            {
                return false;
            }
            if(!UserInfo.UserType.MEMBER.getKey().equals(userInfo.getType()))
            {
                return false;
            }

            BigDecimal rate = mConfigService.getBigDecimal(false, PlarformConfig2.USER_FIRST_RECHARGE_PRESENT_TO_LV1_RATE.getKey());
            // 小于等于0不赠送
            if (rate == null || rate.compareTo(BigDecimal.ZERO) <= 0) {
                return false;
            }
            Date createtime = new Date();

            BigDecimal lv1Amount = rechargeOrder.getAmount().multiply(rate);

            BigDecimal limitLv1Max = mConfigService.getBigDecimal(false, PlarformConfig2.USER_FIRST_RECHARGE_PRESENT_TO_LV1_MAX.getKey());
            if(limitLv1Max != null && lv1Amount.compareTo(limitLv1Max) > 0)
            {
                // 限制最大赠送
                lv1Amount = limitLv1Max;
            }

            UserInfo lv1UserInfo = mUserSerevice.findByUsername(false, userAttr.getParentname());
            addInternalPresent(lv1UserInfo, lv1Amount, userInfo, 1, rechargeOrder, createtime);

            // 二级赠送
            if(userAttr.getGrantfatherid() <= 0)
            {
                return false;
            }
            BigDecimal rate2 = mConfigService.getBigDecimal(false, PlarformConfig2.USER_FIRST_RECHARGE_PRESENT_TO_LV2_RATE.getKey());
            // 小于等于0不赠送
            if (rate2 == null || rate2.compareTo(BigDecimal.ZERO) <= 0) {
                return false;
            }

            BigDecimal lv2Amount = rechargeOrder.getAmount().multiply(rate2);
            BigDecimal limitLv2Max = mConfigService.getBigDecimal(false, PlarformConfig2.USER_FIRST_RECHARGE_PRESENT_TO_LV2_MAX.getKey());
            if(limitLv2Max != null && lv2Amount.compareTo(limitLv2Max) > 0)
            {
                // 限制最大赠送
                lv2Amount = limitLv2Max;
            }

            UserInfo lv2UserInfo = mUserSerevice.findByUsername(false, userAttr.getGrantfathername());
            addInternalPresent(lv2UserInfo, lv2Amount, userInfo, 2, rechargeOrder, createtime);
        } catch (Exception e) {
            LOG.error("addPresentToLv1_2 error:", e);
        }

        return true;
    }

    private void addInternalPresent(UserInfo userInfo, BigDecimal amount, UserInfo fromUserInfo, int level, RechargeOrder rechargeOrder, Date createtime)
    {
        String remarkMsg = "首充赠送给上级: from " + fromUserInfo.getName() + ", fromLevel = " + level + ", fromRecharge = " + rechargeOrder.getNo();
        RemarkVO remarkVO = RemarkVO.create(remarkMsg);
        String outTradeNo = rechargeOrder.getNo();
        FundAccountType accountType = FundAccountType.Spot;
        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
//        BusinessType businessType = BusinessType.RECHARGE_PRESENTATION_PARENTUSER_BY_PERCENT;
//        mPlatformPayManager.addPresentation(accountType, currencyType, userInfo, amount, null, remarkMsg, businessType);

        UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());
        String orderno = mReturnFirstRechargeUpOrderService.createOrder(level, outTradeNo, userInfo, userAttr, accountType, currencyType, amount, createtime, remarkVO);
        // 走返佣通道
        BusinessType businessType = ReturnFirstRechargeUpOrderServiceImpl.mReturnWaterBusinessType;
        ErrorResult result = payApiManager.doReturnWater(accountType, currencyType, businessType, orderno, userInfo, amount, remarkVO);
        if(result == SystemErrorResult.SUCCESS)
        {
            mReturnFirstRechargeUpOrderService.updateTxStatus(level, orderno, OrderTxStatus.REALIZED, null, null);

            ReturnRecordManager.sendMessage(ReturnRecordManager.MQ_EVENT_TYPE_RETURN_FIRST_RECHARGE_LV_AMOUNT, amount, userInfo.getName(), level, null);
        }
    }


    private void addItem(int amount, float rate, String remark)
    {
        JSONObject item = new JSONObject();

        BigDecimal showRate = new BigDecimal(rate).multiply(BigDecimalUtils.DEF_100).setScale(0, RoundingMode.DOWN);

        BigDecimal amountValue = new BigDecimal(amount);

        item.put(KEY_AMOUNT, amountValue);
        item.put(KEY_PRESENT_AMOUNT, amountValue.multiply(new BigDecimal(rate)).setScale(0, RoundingMode.DOWN));
        item.put(KEY_RATE, rate);
        item.put(KEY_TIPS, remark + showRate + "%");

        mPresentDeFList.add(item);

        JSONObject item2 = new JSONObject();
        item2.put(KEY_AMOUNT, amountValue);
        mNoPresentDeFList.add(item2);
    }

    private void replaceItem(int amount, int index)
    {
        JSONObject item = mPresentDeFList.get(index);

        BigDecimal amountValue = new BigDecimal(amount);
        BigDecimal rate = item.getBigDecimal(KEY_RATE);

        item.put(KEY_AMOUNT, amountValue);
        item.put(KEY_PRESENT_AMOUNT, amountValue.multiply(rate.setScale(0, RoundingMode.DOWN)));

        //
        JSONObject item2 = mNoPresentDeFList.get(index);
        item2.put(KEY_AMOUNT, amountValue);
    }


    public List getByUser(UserInfo userInfo)
    {
        initConf();
        if(!enablPresent)
        {
            return mNoPresentDeFList;
        }
        List<DayPresentOrder> rsList = mDayPresentOrderService.queryByUser(false, userInfo.getId(), PresentBusinessType.FIRST_RECHARGE_PRESENT_AMOUNT);
        if(CollectionUtils.isEmpty(rsList))
        {
            return mPresentDeFList;
        }
        else if(rsList.size() == mDefListSize)
        {
            return mNoPresentDeFList;
        }

        List list = Lists.newArrayList();
        for(JSONObject tmp : mPresentDeFList)
        {
            BigDecimal rechargeAmount = tmp.getBigDecimal(KEY_AMOUNT);
            BigDecimal presentAmount = tmp.getBigDecimal(KEY_PRESENT_AMOUNT);

            boolean exist = false;
            for(DayPresentOrder orderInfo : rsList)
            {
                if(orderInfo.getAmount().compareTo(presentAmount) == 0)
                {
                    exist = true;
                    break;
                }
            }

            JSONObject item = null;
            if(!exist)
            {
                item = tmp;
            }
            else
            {
                item = new JSONObject();
                item.put(KEY_AMOUNT, rechargeAmount);
            }

            list.add(item);
        }
        return list;
    }

    public void addPresent(UserInfo userInfo, BigDecimal amount)
    {
        if(!enablPresent)
        {
            return;
        }
        if(SystemRunningMode.getSystemConfig() != SystemRunningMode.BC)
        {
            return;
        }
        JSONObject configItem = null;
        for(JSONObject tmp : mPresentDeFList)
        {
            BigDecimal tmpAmount = tmp.getBigDecimal(KEY_AMOUNT);
            if(tmpAmount.compareTo(amount) == 0)
            {
                configItem = tmp;
                break;
            }
        }

        if(configItem == null)
        {
            return;
        }

        String taskid = amount.intValue() + StringUtils.getEmpty();
        PresentBusinessType businessType = PresentBusinessType.FIRST_RECHARGE_PRESENT_AMOUNT;
        String outradeno = mDayPresentOrderService.generateOutTradeNo(userInfo.getId(), businessType, taskid, null);
        DayPresentOrder orderInfo = mDayPresentOrderService.find(MyEnvironment.isDev(), outradeno);
        if(orderInfo != null)
        {
            return;
        }

        try {
            RemarkVO remarkVO = RemarkVO.create(configItem.getString(KEY_TIPS));
            ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
            UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());

            BigDecimal rate = configItem.getBigDecimal(KEY_RATE);
            BigDecimal presentAmount = amount.multiply(rate);
            String orderno = mDayPresentOrderService.createOrder(outradeno, currencyType, userAttr, businessType, presentAmount, remarkVO);

            FundAccountType accountType = FundAccountType.Spot;
            BusinessType payBusinessType = BusinessType.PLATFORM_PRESENTATION;
            ErrorResult errorResult = mPayApiManager.doPlatformPresentation(accountType, currencyType, payBusinessType, orderno, userInfo, presentAmount, remarkVO);
            if(errorResult == SystemErrorResult.SUCCESS)
            {
                mDayPresentOrderService.updateTxStatus(orderno, OrderTxStatus.REALIZED, userInfo.getId(), businessType, null, null);
            }

            long rsTime = System.currentTimeMillis() - userInfo.getCreatetime().getTime();
            if(rsTime > 172800)
            {
                // 超过2天就不算
                return;
            }

//            if(userAttr.getParentid() > 0)
//            {
//                ReturnRecordManager.sendMessage(ReturnRecordManager.MQ_EVENT_TYPE_REG_VALID, null, userAttr.getParentname(), 1, null);
//            }
//            if(userAttr.getGrantfatherid() > 0)
//            {
//                ReturnRecordManager.sendMessage(ReturnRecordManager.MQ_EVENT_TYPE_REG_VALID, null, userAttr.getGrantfathername(), 2, null);
//            }

        } catch (Exception e) {
            LOG.error("handle error:", e);
        }


    }


    private void test()
    {
        String username = "c_0xFA730bd82c7E8721aF28c8A0ed56Bf9041E94d22";
        UserInfo userInfo = mUserSerevice.findByUsername(false, username);
//        addPresent(userInfo, new BigDecimal(10));


        UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());

        RechargeOrder rechargeOrder = new RechargeOrder();
        rechargeOrder.setAmount(new BigDecimal(10000));
        rechargeOrder.setNo("" + System.currentTimeMillis());

        addPresentToLv1_2(userInfo, userAttr, rechargeOrder);

//        Object value = getByUser(userInfo);
//        FastJsonHelper.prettyJson(value);
    }

    public static void testRun()
    {
        FirstRechargeManager mgr = SpringContextUtils.getBean(FirstRechargeManager.class);
        mgr.getByUser(null);
    }

}
