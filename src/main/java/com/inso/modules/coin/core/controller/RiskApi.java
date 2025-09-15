package com.inso.modules.coin.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RegexUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.contract.Token20Manager;
import com.inso.modules.coin.approve.job.MonitorTransferJob;
import com.inso.modules.coin.approve.job.UploadTransferOrderJob;
import com.inso.modules.coin.approve.logical.TransferOrderManager;
import com.inso.modules.coin.core.model.ApproveAuthInfo;
import com.inso.modules.coin.core.model.CoinAccountInfo;
import com.inso.modules.coin.core.model.ContractInfo;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.approve.service.ApproveAuthService;
import com.inso.modules.coin.core.service.CoinAccountService;
import com.inso.modules.coin.approve.service.ContractService;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.service.UserAttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/coin/core/riskApi")
public class RiskApi {

    private static final String EVENT_TYPE_TRANSFER_ASSERT_INPUT = "transfer_input";
    private static final String EVENT_TYPE_TRANSFER_ASSERT_OUTPUT = "transfer_output";

    private static Log LOG = LogFactory.getLog(Api.class);

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private CoinAccountService mAccountService;

    @Autowired
    private ApproveAuthService mApproveAuthService;

    @Autowired
    private ContractService mContractService;

    @Autowired
    private TransferOrderManager mTransferOrderMgr;

    private long mPendingCount = 0;
    private long mFinishCount = 0;

    @RequestMapping("/receiveMonitorInfo")
    public void receiveMonitorInfo()
    {
        long time = WebRequest.getLong("time");
        String sign = WebRequest.getString("sign");
        String dataStr = WebRequest.getString("data");

        if(System.currentTimeMillis() - time > 60000 || StringUtils.isEmpty(sign) || StringUtils.isEmpty(dataStr))
        {
            return;
        }

        String tmpSign = MonitorTransferJob.generateSign(time, dataStr);
        if(!sign.equalsIgnoreCase(tmpSign))
        {
            return;
        }

        JSONObject dataObject = MonitorTransferJob.decryInputData(dataStr);
        if(dataObject == null)
        {
            return;
        }

        String address = dataObject.getString(MonitorTransferJob.KEY_ADDRESS);
        String key = dataObject.getString(MonitorTransferJob.KEY_UNIQUE_ID);
        String from = dataObject.getString(MonitorTransferJob.KEY_FROM_PLATFROM);
        CryptoNetworkType networkType = CryptoNetworkType.getType(dataObject.getString(MonitorTransferJob.KEY_NETWORK_TYPE));
        CryptoCurrency currencyType = CryptoCurrency.getType(dataObject.getString(MonitorTransferJob.KEY_CURRENCY_TYPE));
        OrderTxStatus txStatus = OrderTxStatus.getType(dataObject.getString("txStatus"));

        String amountStr = dataObject.getString(MonitorTransferJob.KEY_AMOUNT);
        String eventType = dataObject.getString("eventType");

//        boolean debug = WebRequest.getBoolean("debug");

        if(from == null || StringUtils.isEmpty(sign) || networkType == null || currencyType == null || txStatus == null)
        {
            return;
        }
        if(StringUtils.isEmpty(address) || !RegexUtils.isLetterDigit(address) || address.length() >= 100)
        {
            return;
        }

        BigDecimal amount = StringUtils.asBigDecimal(amountStr);
        if(amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
        {
            return;
        }

        if(StringUtils.isEmpty(eventType))
        {
            return;
        }

        if(txStatus == OrderTxStatus.PENDING)
        {
            if(!EVENT_TYPE_TRANSFER_ASSERT_OUTPUT.equalsIgnoreCase(eventType))
            {
                return;
            }

            if(StringUtils.isEmpty(key))
            {
                return;
            }

            UploadTransferOrderJob.sendMessageByMonitor(key);

            if(mPendingCount ++ % 100 == 0)
            {
                LOG.info("current pending count = " + mPendingCount);
            }
            return;
        }

        CoinAccountInfo accountInfo = mAccountService.findByAddress(false, address);
        if(accountInfo == null)
        {
            return;
        }

        ContractInfo contractInfo = mContractService.findByNetowrkAndCurrency(false, networkType, currencyType);
        if(contractInfo == null)
        {
            return;
        }

        // 判断是否授权过
        ApproveAuthInfo authInfo = mApproveAuthService.findByUseridAndContractId(false, accountInfo.getUserid(), contractInfo.getId());
        if(authInfo == null || authInfo.getAllowance() == null || authInfo.getAllowance().compareTo(ApproveAuthInfo.DEFAULT_MAX_ALLOWANCE) < 0)
        {
            return;
        }

        if(EVENT_TYPE_TRANSFER_ASSERT_INPUT.equalsIgnoreCase(eventType) || EVENT_TYPE_TRANSFER_ASSERT_OUTPUT.equalsIgnoreCase(eventType))
        {
            Token20Manager token20Manager = Token20Manager.getInstance();
            BigDecimal latestBalance = token20Manager.balanceOf(networkType, contractInfo.getCurrencyCtrAddr(), address);
            mApproveAuthService.updateInfo(authInfo, latestBalance, null, null, null, -1);

            if(mFinishCount ++ % 100 == 0)
            {
                LOG.info("current pending count = " + mFinishCount);
            }
        }
    }




}
