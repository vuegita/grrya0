package com.inso.modules.web.settle.job;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.core.model.TransferOrderInfo;
import com.inso.modules.coin.approve.service.TransferOrderService;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.business.model.UserWithdrawVO;
import com.inso.modules.passport.business.model.WithdrawOrder;
import com.inso.modules.passport.business.service.WithdrawOrderService;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.paychannel.model.PayProductType;
import com.inso.modules.web.SystemRunningMode;
import com.inso.modules.web.settle.model.SettleBusinessType;
import com.inso.modules.web.settle.model.SettleOrderInfo;
import com.inso.modules.web.settle.model.SettleRecordInfo;
import com.inso.modules.web.settle.service.SettleOrderService;
import com.inso.modules.web.settle.service.SettleRecordService;
import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

public class SettleOrderJob implements Job {

    private static Log LOG = LogFactory.getLog(SettleOrderJob.class);

    private UserService mUserService;
    private UserAttrService userAttrService;
    private WithdrawOrderService mWithdrawOrderService;
    private SettleOrderService mSettleOrderService;
    private TransferOrderService mTranseferOrderService;

    private SettleRecordService mRecordService;

    public SettleOrderJob()
    {
        this.mUserService = SpringContextUtils.getBean(UserService.class);
        this.userAttrService = SpringContextUtils.getBean(UserAttrService.class);
        this.mWithdrawOrderService = SpringContextUtils.getBean(WithdrawOrderService.class);
        this.mSettleOrderService = SpringContextUtils.getBean(SettleOrderService.class);
        this.mTranseferOrderService = SpringContextUtils.getBean(TransferOrderService.class);

        this.mRecordService = SpringContextUtils.getBean(SettleRecordService.class);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        try {
            if(!SystemRunningMode.isCryptoMode())
            {
                return;
            }

            DateTime dataTimme = new DateTime();
            syncWithrawOrder(dataTimme, false);
            handleSettleOrder(dataTimme, false);

            statsSettleRecord(dataTimme.minusDays(1));
        } catch (Exception e) {
            LOG.error("hanlde error:", e);
        }

    }


    public void syncWithrawOrder(DateTime dateTime, boolean isDEV)
    {
        DateTime fromTime = dateTime.minusDays(3);
        if(isDEV)
        {
            fromTime = dateTime.minusDays(90);
        }
        DateTime toTime = dateTime;

        String startTimeStr = fromTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String endTimeStr = toTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);

        mWithdrawOrderService.queryAll(startTimeStr, endTimeStr, new Callback<WithdrawOrder>() {
            @Override
            public void execute(WithdrawOrder o) {

                OrderTxStatus txStatus = OrderTxStatus.getType(o.getStatus());
                if(txStatus != OrderTxStatus.REALIZED)
                {
                    return;
                }

                PayProductType productType = PayProductType.getType(o.getPayProductType());
                if(productType != PayProductType.COIN)
                {
                    return;
                }

                JSONObject jsonObject = o.getRemarkVO();

                CryptoNetworkType networkType = CryptoNetworkType.getType(jsonObject.getString(UserWithdrawVO.KEY_IFSC));
                ICurrencyType currencyType = ICurrencyType.getType(o.getCurrency());
                if(currencyType == null || networkType == null)
                {
                    return;
                }

                try {
                    UserInfo userInfo = mUserService.findByUsername(false, o.getUsername());
                    UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());
                    if(userType == UserInfo.UserType.TEST)
                    {
                        // 测试号不结算
                        return;
                    }
                    UserAttr userAttr = userAttrService.find(false, o.getUserid());
                    JSONObject remark = o.getRemarkVO();
                    String account = remark.getString("account");

                    if(StringUtils.isEmpty(account))
                    {
                        return;
                    }

                    mSettleOrderService.addOrder(networkType, currencyType, SettleBusinessType.COIN_WITHDRAW_DAY, o.getNo(),  userAttr, OrderTxStatus.WAITING, o.getAmount(), o.getFeemoney(), o.getCreatetime(), account);
                }
                catch (org.springframework.dao.DuplicateKeyException e)
                {

                }
                catch (Exception e) {
                    LOG.error("handle error:", e);
                }

            }
        });
    }

    public void handleSettleOrder(DateTime dateTime, boolean isDEV)
    {
        DateTime fromTime = dateTime.minusDays(2);
        if(isDEV)
        {
            fromTime = dateTime.minusMonths(5);
        }
        DateTime toTime = dateTime;
//        BigDecimal rate = new BigDecimal(0.15);


        String keyFollowOrderno = "followOrderno";
        String keyfollowAmount = "followAmount";
        String keyfollowType = "followType";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(keyfollowType, "coinTransfer");

        mTranseferOrderService.queryAll(fromTime, toTime, OrderTxStatus.REALIZED, new Callback<TransferOrderInfo>() {
            @Override
            public void execute(TransferOrderInfo orderInfo) {
                try {
                    String fromAddress = orderInfo.getFromAddress().trim();
                    Date updatetime = orderInfo.getCreatetime();

                    jsonObject.put(keyFollowOrderno, orderInfo.getNo());
                    jsonObject.put(keyfollowAmount, orderInfo.getTotalAmount());

                    CryptoNetworkType networkType = CryptoNetworkType.getType(orderInfo.getCtrNetworkType());
                    CryptoCurrency currency = CryptoCurrency.getType(orderInfo.getCurrencyType());

//                    BigDecimal validAmount = orderInfo.getTotalAmount().multiply(rate);
                    // 更新订单
                    mSettleOrderService.updateTxStatusToRealized(fromAddress, networkType, currency, updatetime, null, jsonObject, orderInfo.getNo(), orderInfo.getTotalAmount());

                    // 更新划转订单，统计提现总额
                    BigDecimal totalWithdrowAmount = mSettleOrderService.findWithdrowAmountBytransferNo(orderInfo.getNo());

                    JSONObject remark = FastJsonHelper.toJSONObject(orderInfo.getRemark());
                    if(remark == null)
                    {
                        remark = new JSONObject();
                    }
                    remark.put("totalWithdrawAmount", totalWithdrowAmount);

                    mTranseferOrderService.updateRemarkWithdrawInfo( orderInfo.getNo(),OrderTxStatus.getType(orderInfo.getStatus()), orderInfo.getOutTradeNo(),  remark);
                    //LOG.info("fromAddress = " + fromAddress + ", currency = " + currency.getKey() + ", updatetime = " + updatetime + ", totalAmount = " + orderInfo.getTotalAmount());

                } catch (Exception e) {
                    LOG.error("handle error:", e);
                }
            }
        }, true);
    }

    public void statsSettleRecord(DateTime dateTime)
    {
        String pdateStr = dateTime.toString(DateUtils.TYPE_YYYYMMDD);
        Date pdate = DateUtils.convertDate(DateUtils.TYPE_YYYYMMDD, pdateStr);

        Date startTime = DateUtils.convertDate(DateUtils.TYPE_YYYYMMDDHHMMSS, DateUtils.getBeginTimeOfDayTwo(pdateStr));
        Date endTime = DateUtils.convertDate(DateUtils.TYPE_YYYYMMDDHHMMSS, DateUtils.getEndTimeOfDayTwo(pdateStr));

        Map<String, SettleRecordInfo> agentMaps = Maps.newHashMap();
        Map<String, SettleRecordInfo> staffMaps = Maps.newHashMap();

        mSettleOrderService.queryAll(new DateTime(startTime), new DateTime(endTime), new Callback<SettleOrderInfo>() {
            @Override
            public void execute(SettleOrderInfo o) {

                OrderTxStatus txStatus = OrderTxStatus.getType(o.getStatus());
                if(txStatus != OrderTxStatus.REALIZED)
                {
                    return;
                }

                increRecordInfo(agentMaps, o, true);
                increRecordInfo(staffMaps, o, false);
            }
        });

        addRecordInfoByMaps(agentMaps, pdate);
        addRecordInfoByMaps(staffMaps, pdate);
    }

    private void increRecordInfo(Map<String, SettleRecordInfo> maps, SettleOrderInfo model, boolean isAgent)
    {
        try {
            String key = model.getAgentid() + StringUtils.getEmpty() + model.getStaffid() + model.getCurrency();
            SettleRecordInfo tmp = maps.get(key);

            if(tmp == null)
            {
                tmp = new SettleRecordInfo();
                tmp.setCurrency(model.getCurrency());
                tmp.setAmount(BigDecimal.ZERO);
                tmp.setFeemoney(BigDecimal.ZERO);
                maps.put(key, tmp);

                tmp.setAgentid(model.getAgentid());
                tmp.setAgentname(model.getAgentname());

                if(!isAgent)
                {
                    tmp.setStaffid(model.getStaffid());
                    tmp.setStaffname(model.getStaffname());
                }
            }

            tmp.setAmount(tmp.getAmount().add(model.getAmount()));
            tmp.setFeemoney(tmp.getFeemoney().add(model.getFeemoney()));
        } catch (Exception e) {
            LOG.error("handle increRecordInfo error:", e);
        }
    }

    private void addRecordInfoByMaps(Map<String, SettleRecordInfo> maps, Date pdate)
    {
        if(maps.isEmpty())
        {
            return;
        }
        for(Map.Entry<String, SettleRecordInfo> entry : maps.entrySet())
        {
            try {
                SettleRecordInfo value = entry.getValue();
                ICurrencyType currencyType = ICurrencyType.getType(value.getCurrency());
                mRecordService.delete(pdate, SettleBusinessType.COIN_WITHDRAW_DAY, value.getAgentid(), value.getStaffid(), currencyType);
                mRecordService.addOrder(pdate, SettleBusinessType.COIN_WITHDRAW_DAY, entry.getValue(), null);
            } catch (Exception e) {
                LOG.error("addRecordInfoByMaps error:", e);
            }
        }
    }

    public void testSyncWithdraw()
    {
        DateTime dataTimme = new DateTime();
        syncWithrawOrder(dataTimme, true);
        handleSettleOrder(dataTimme, true);

        for(int i = 0; i < 90; i ++)
        {
            dataTimme = dataTimme.minusDays(1);
            statsSettleRecord(dataTimme);
        }
    }



}

