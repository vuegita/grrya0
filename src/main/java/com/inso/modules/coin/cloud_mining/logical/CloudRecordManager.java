package com.inso.modules.coin.cloud_mining.logical;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.NumberEncryptUtils;
import com.inso.modules.coin.cloud_mining.model.CloudOrderInfo;
import com.inso.modules.coin.cloud_mining.model.CloudProductType;
import com.inso.modules.coin.cloud_mining.model.CloudRecordInfo;
import com.inso.modules.coin.cloud_mining.service.CloudOrderService;
import com.inso.modules.coin.cloud_mining.service.CloudRecordService;
import com.inso.modules.common.model.BusinessType;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.UserErrorResult;
import com.inso.modules.passport.business.model.ReturnWaterType;
import com.inso.modules.passport.money.PayApiManager;
import com.inso.modules.passport.money.model.UserMoney;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.team.logical.TeamBuyGroupManager;
import com.inso.modules.web.team.model.TeamBusinessType;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

@Component
public class CloudRecordManager {

    private static Log LOG = LogFactory.getLog(CloudRecordManager.class);

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private CloudRecordService mRecordService;

    @Autowired
    private CloudOrderService mOrderService;

    @Autowired
    private PayApiManager mPayApiManager;

    @Autowired
    private UserMoneyService mUserMoneyService;

    public ErrorResult updateRecord(UserInfo userInfo, CloudProductType productType, long days, CryptoCurrency currencyType, BigDecimal invesAmount)
    {
        ErrorResult errorResult = null;
        try {

            UserMoney userMoney = mUserMoneyService.findMoney(false, userInfo.getId(), FundAccountType.Spot, currencyType);
            if(!userMoney.verify(invesAmount))
            {
                return UserErrorResult.ERR_PAY_NOT_ENOUGH_BALANCE;
            }

            UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());
            CloudOrderInfo.OrderType orderType = CloudOrderInfo.OrderType.DEPOSIT;

            String orderno = mOrderService.addOrder(userAttr, productType, currencyType, orderType, invesAmount, null, null);

            errorResult = mPayApiManager.doBusinessDeduct(FundAccountType.Spot, currencyType, BusinessType.COIN_CLOUD_MINING_REWARD_ORDER,
                    orderno, userInfo, invesAmount, null, null);

            if(errorResult == SystemErrorResult.SUCCESS)
            {
                addRecord(userInfo, productType, currencyType, days, invesAmount, orderno);
            }
            else
            {
                mOrderService.updateInfo(orderno, OrderTxStatus.FAILED, null, null);
            }
        } catch (Exception e) {
            LOG.error("handle error:", e);
            errorResult = SystemErrorResult.ERR_SYS_OPT_FAILURE;
        }
        return errorResult;
    }


    public ErrorResult withdrawToBalance(UserInfo userInfo, CloudRecordInfo recordInfo)
    {
        try {
            UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());
            CloudProductType productType = CloudProductType.getType(recordInfo.getProductType());
            CryptoCurrency currency = CryptoCurrency.getType(recordInfo.getCurrencyType());
            CloudOrderInfo.OrderType orderType = CloudOrderInfo.OrderType.WITHDRAW_2_BALANCE;

            Date nowDate = new Date();

            BigDecimal amount = recordInfo.getInvesTotalAmount();

            if(productType == CloudProductType.COIN_CLOUD_SOLID && nowDate.getTime() >= recordInfo.getEndtime().getTime() )
            {
                // 定期理财需要把 收益给到会员
                amount = amount.add(recordInfo.getRewardBalance());
            }

            String orderno = mOrderService.addOrder(userAttr, productType, currency, orderType, amount, null, null);

            // 1. 先把数据清除
            mRecordService.withdrawReward2(recordInfo, orderno);

            // 2.
            ErrorResult errorResult = mPayApiManager.doBusinessRecharge(FundAccountType.Spot, currency,
                    BusinessType.COIN_CLOUD_MINING_REWARD_ORDER, orderno, userInfo, amount, null);
            if(errorResult == SystemErrorResult.SUCCESS)
            {
                // 3. 修改订单状态 并清除它的所有收益
                mOrderService.updateInfo(orderno, OrderTxStatus.REALIZED, null, null);
            }
            return errorResult;
        } catch (Exception e) {
            LOG.error("handle withdrawToBalance error:", e);
            return SystemErrorResult.ERR_SYS_OPT_FAILURE;
        }
    }

    private boolean addRecord(UserInfo userInfo, CloudProductType productType, CryptoCurrency currencyType, long days, BigDecimal invesAmount, String orderno)
    {
        synchronized (userInfo.getName())
        {
            if(productType == CloudProductType.COIN_CLOUD_SOLID)
            {
                mRecordService.add(userInfo, productType, currencyType, days, invesAmount, orderno);
                mOrderService.updateInfo(orderno, OrderTxStatus.REALIZED, null, null);
            }
            else if(productType == CloudProductType.COIN_CLOUD_ACTIVE)
            {
                CloudRecordInfo recordInfo = mRecordService.findByAccountIdAndProductId(true, userInfo.getId(), productType, currencyType, days);
                if(recordInfo == null)
                {
                    mRecordService.add(userInfo, productType, currencyType, days, invesAmount, orderno);
                    mOrderService.updateInfo(orderno, OrderTxStatus.REALIZED, null, null);
                }
                else
                {
                    // 到期时间
                    DateTime dateTime = new DateTime();
                    dateTime = dateTime.plusDays((int)days);
                    BigDecimal newInvesAmount = recordInfo.getInvesTotalAmount().add(invesAmount);
                    mRecordService.updateInvesAmount(recordInfo, newInvesAmount, orderno, dateTime.toDate());
                }
            }
        }
        return true;
    }

}
