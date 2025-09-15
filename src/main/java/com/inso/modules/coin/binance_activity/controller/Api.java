package com.inso.modules.coin.binance_activity.controller;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.binance_activity.model.BARecordInfo;
import com.inso.modules.coin.binance_activity.service.BARecordService;
import com.inso.modules.coin.approve.logical.ContractInfoManager;
import com.inso.modules.coin.core.model.CoinAccountInfo;
import com.inso.modules.coin.core.model.ContractInfo;
import com.inso.modules.coin.core.model.ProfitConfigInfo;
import com.inso.modules.coin.core.service.CoinAccountService;
import com.inso.modules.coin.approve.service.ContractService;
import com.inso.modules.coin.core.service.ProfitConfigService;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.passport.user.limit.MyLoginRequired;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.AuthService;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/coin/binanceActiveApi")
public class Api {

    @Autowired
    private AuthService mAuthService;

    @Autowired
    private UserService mUserService;

    @Autowired
    private BARecordService mRecordService;

    @Autowired
    private ContractService mContractService;

    @Autowired
    private CoinAccountService accountService;

    @Autowired
    private ProfitConfigService mBAProfitConfigService;

    @Autowired
    private UserAttrService mUserAttrService;

    @RequestMapping("/getConfigList")
    public String getConfigList()
    {
        String accessToken = WebRequest.getAccessToken();
        CryptoCurrency currencyType = CryptoCurrency.USDT;
        ProfitConfigInfo.ProfitType profitType = ProfitConfigInfo.ProfitType.BIANCE_ACTIVE;

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        if(profitType == null || currencyType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        List<ProfitConfigInfo> rsList = null;
        if(!StringUtils.isEmpty(accessToken))
        {
            String username = mAuthService.getAccountByAccessToken(accessToken);
            UserInfo userInfo = mUserService.findByUsername(false, username);
            UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());
            rsList = mBAProfitConfigService.queryAllList(false, userAttr.getAgentid(), profitType, currencyType);
        }

        if(CollectionUtils.isEmpty(rsList))
        {
            String username = UserInfo.DEFAULT_GAME_SYSTEM_STAFF;
            UserInfo userInfo = mUserService.findByUsername(false, username);
            UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());
            rsList = mBAProfitConfigService.queryAllList(false, userAttr.getAgentid(), profitType, currencyType);
        }

        if(CollectionUtils.isEmpty(rsList))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_NODATA);
            return apiJsonTemplate.toJSONString();
        }
        apiJsonTemplate.setData(rsList);
        return apiJsonTemplate.toJSONString();
    }


    @RequestMapping("/addRecord")
    @MyLoginRequired
    public String addRecord()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);;

        String address = WebRequest.getString("address");

        String contractIdString = WebRequest.getString("contractId");
        long contractId = ContractInfoManager.decryptId(contractIdString);

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(contractId <= 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        ContractInfo contractInfo = mContractService.findById(false, contractId);
        if(contractInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        UserInfo userInfo = mUserService.findByUsername(false, username);
        if(!userInfo.getType().equalsIgnoreCase(UserInfo.UserType.MEMBER.getKey()))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        CoinAccountInfo accountInfo = accountService.findByAddress(false, address);
        if(accountInfo.getUserid() != userInfo.getId())
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return apiJsonTemplate.toJSONString();
        }

        if(mRecordService.findByUseridAndContractid(false, userInfo.getId(), contractId) != null)
        {
            return apiJsonTemplate.toJSONString();
        }

        mRecordService.add(contractInfo, userInfo, address);
        return apiJsonTemplate.toJSONString();
    }

    @RequestMapping("/getUserRecordList")
    @MyLoginRequired
    public String getUserRecordList()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);;

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        UserInfo userInfo = mUserService.findByUsername(false, username);
        if(!userInfo.getType().equalsIgnoreCase(UserInfo.UserType.MEMBER.getKey()))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        List<BARecordInfo> rsList = mRecordService.queryByUser(false, userInfo.getId());
        apiJsonTemplate.setData(rsList);
        return apiJsonTemplate.toJSONString();
    }

}
