package com.inso.modules.passport.money;

import com.google.common.collect.Lists;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.config.PlarformConfig2;
import com.inso.modules.common.model.*;
import com.inso.modules.passport.business.model.PresentBusinessType;
import com.inso.modules.passport.business.service.DayPresentOrderService;
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
import java.util.List;

@Component
public class RechargeActiveManager {

    private static Log LOG = LogFactory.getLog(RechargeActiveManager.class);

    @Autowired
    private ConfigService mConfigService;

    @Autowired
    private DayPresentOrderService mDayPresentOrderService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private UserService mUserService;

    @Autowired
    private PayApiManager mPayApiManager;

    private List<InviteKeyValue> mConfigItemList;

    private long mLastTimeRefresh = -1;

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

    private void refresh()
    {
        long ts = System.currentTimeMillis();
        if(mLastTimeRefresh > 0 && ts - mLastTimeRefresh < 60_000)
        {
            // 小于1分钟重新加载
            return;
        }
        this.mLastTimeRefresh = ts;

        PlarformConfig2 config = PlarformConfig2.ADMIN_APP_PLATFORM_USER_RECHARGE_PRESENTATION_OF_ACTIVE_LEVEL;
        String configValue = mConfigService.getValueByKey(false, config.getKey());
        if(StringUtils.isEmpty(configValue))
        {
            this.mConfigItemList = Collections.emptyList();
            return;
        }

        // 获取配置
        List<InviteKeyValue> configItemList = Lists.newArrayList();
        String[] valueArray = StringUtils.split(configValue, '|');
        if(valueArray != null || valueArray.length > 0)
        {
            String eqFlag = "=";
            for(String item : valueArray)
            {
                int index = item.indexOf(eqFlag);
                String rechargeAmountValue = item.substring(0, index).trim();
                String presentAmountValue = item.substring(index + 1, item.length());

                BigDecimal rechargeAmount = StringUtils.asBigDecimal(rechargeAmountValue);
                BigDecimal presentAmount = StringUtils.asBigDecimal(presentAmountValue);

                if(rechargeAmount == null || rechargeAmount.compareTo(BigDecimal.ZERO) <= 0 || presentAmount == null || presentAmount.compareTo(BigDecimal.ZERO) <= 0)
                {
                    continue;
                }

                InviteKeyValue keyValue = new InviteKeyValue();
                keyValue.setRechargeAmount(rechargeAmount);
                keyValue.setPresentAmount(presentAmount);

                configItemList.add(keyValue);
            }
        }
        this.mConfigItemList = configItemList;
    }

    /**
     * 赠送邀请好友并完成充值的赠送金额
     */
    public void doTask(UserInfo userInfo, BigDecimal rechargeAmount, String rechargeOrderNo)
    {
        refresh();
        if(CollectionUtils.isEmpty(this.mConfigItemList))
        {
            return;
        }

        InviteKeyValue activeItem = null;
        int len = mConfigItemList.size();
        for(int i = 0; i < len; i ++)
        {
            InviteKeyValue item = mConfigItemList.get(i);
            // 2=1|10=2    2
            if(rechargeAmount.compareTo(item.getRechargeAmount()) == 0)
            {
                activeItem = item;
                break;
            }
            else if(rechargeAmount.compareTo(item.getRechargeAmount()) < 0)
            {
                if(i > 1)
                {
                    activeItem = mConfigItemList.get(i - 1);
                }
                break;
            }
        }

        if(activeItem == null)
        {
            return;
        }

        BusinessType businessType = BusinessType.RECHARGE_ACTION_PRESENTATION;
        RemarkVO remarkVO = RemarkVO.create("Recharge Active presentation: recharge amount = " + rechargeAmount);

        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
        PresentBusinessType presentBusinessType = PresentBusinessType.INVITE_WEEK;
        FundAccountType accountType = FundAccountType.Spot;
        String taskid = rechargeOrderNo;
        DateTime dateTime = DateTime.now();

        try {

            String outradeno = mDayPresentOrderService.generateOutTradeNo(userInfo.getId(), presentBusinessType, taskid, dateTime);
            UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());
            String orderno = mDayPresentOrderService.createOrder(outradeno, currencyType, userAttr, presentBusinessType, activeItem.getPresentAmount(), remarkVO);

            ErrorResult errorResult = mPayApiManager.doPlatformPresentation(accountType, currencyType, businessType, orderno, userInfo, activeItem.getPresentAmount(), remarkVO);
            if(errorResult == SystemErrorResult.SUCCESS)
            {
                mDayPresentOrderService.updateTxStatus(orderno, OrderTxStatus.REALIZED, userInfo.getId(), presentBusinessType, null, null);
            }

        } catch (Exception e) {
            LOG.error("do presentation error:", e);
        }
    }

    private class InviteKeyValue {
        /*** 邀请完成人数, 如 1=20, key为1， amount=20 ***/
        private BigDecimal rechargeAmount;
        private BigDecimal presentAmount;

        public BigDecimal getRechargeAmount() {
            return rechargeAmount;
        }

        public void setRechargeAmount(BigDecimal rechargeAmount) {
            this.rechargeAmount = rechargeAmount;
        }

        public BigDecimal getPresentAmount() {
            return presentAmount;
        }

        public void setPresentAmount(BigDecimal presentAmount) {
            this.presentAmount = presentAmount;
        }
    }

    private void test1()
    {
        String username = "up9199999999992";
        UserInfo userInfo = mUserService.findByUsername(false, username);
        doTask(userInfo, new BigDecimal(10), "1");

    }
    public static void testRun()
    {
        RechargeActiveManager mgr = SpringContextUtils.getBean(RechargeActiveManager.class);
        mgr.test1();
    }

    public static void main(String[] args) {
        String configValue = "1=20|2=40|5=130|10=300|20=700|30=1200|50=2300|100=5000";


        RechargeActiveManager mgr = new RechargeActiveManager();
        mgr.checkConfigValue(configValue);

//        String[] valueArray = StringUtils.split(configValue, '|');
//        if(valueArray != null || valueArray.length > 0)
//        {
//            for(String item : valueArray)

//            {
//                int index = item.indexOf("=");
//                String key = item.substring(0, index).trim();
//                String value = item.substring(index + 1, item.length());
//                System.out.println(key + " = " + value);
//            }
//        }
    }

}
