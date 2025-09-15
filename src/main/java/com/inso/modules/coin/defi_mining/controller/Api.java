package com.inso.modules.coin.defi_mining.controller;

import com.google.common.collect.Maps;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.RegexUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.contract.Token20Manager;
import com.inso.modules.coin.approve.logical.ApproveSyncManager;
import com.inso.modules.coin.approve.logical.TransferOrderManager;
import com.inso.modules.coin.core.model.*;
import com.inso.modules.coin.approve.service.ApproveAuthService;
import com.inso.modules.coin.core.service.CoinAccountService;
import com.inso.modules.coin.approve.service.ContractService;
import com.inso.modules.coin.defi_mining.logical.MiningProductManager;
import com.inso.modules.coin.defi_mining.model.MiningOrderInfo;
import com.inso.modules.coin.defi_mining.model.MiningProductInfo;
import com.inso.modules.coin.defi_mining.model.MiningRecordInfo;
import com.inso.modules.coin.defi_mining.service.MiningOrderService;
import com.inso.modules.coin.defi_mining.service.MiningProductService;
import com.inso.modules.coin.defi_mining.service.MiningRecordService;
import com.inso.modules.common.helper.RequestTokenHelper;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.money.PayApiManager;
import com.inso.modules.passport.money.model.UserMoney;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.user.limit.MyLoginRequired;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.AuthService;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.SystemRunningMode;
import com.inso.modules.web.logical.SystemStatusManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/coin/defiMiningApi")
public class Api {

    private static Log LOG = LogFactory.getLog(Api.class);

    @Autowired
    private AuthService mAuthService;

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private CoinAccountService mAccountService;

    @Autowired
    private MiningProductManager miningProductManager;

    @Autowired
    private MiningProductService miningProductService;

    @Autowired
    private MiningRecordService miningRecordService;

    @Autowired
    private ApproveAuthService mApproveAuthService;

    @Autowired
    private ContractService mContractService;

    @Autowired
    private PayApiManager mPayApiMgr;

    @Autowired
    private MiningOrderService miningOrderService;

    @Autowired
    private UserMoneyService mUserMoneyService;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private TransferOrderManager mTransferOrderMgr;

    @Autowired
    private ApproveSyncManager mApproveSyncMgr;

    @RequestMapping("/getMiningProductInfoList")
    public String getMiningProductInfoList()
    {
        String address = WebRequest.getString("address");
        CoinAccountInfo accountInfo=null;
        if( !StringUtils.isEmpty(address))
        {
            //CryptoNetworkType networkType = CryptoNetworkType.getType(WebRequest.getString("networkType"));
            accountInfo = mAccountService.findByAddress(false, address);
        }

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        List rsList= miningProductManager.queryAllProductList(accountInfo);

        if(rsList.isEmpty())
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
        }
        else
        {
            apiJsonTemplate.setData(rsList);
        }
        return apiJsonTemplate.toJSONString();
    }

    @RequestMapping("/getMiningRecordInfoList")
    @MyLoginRequired
    public String getMiningRecordInfoList()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);
        UserInfo userInfo = mUserService.findByUsername(false, username);

        List<MiningRecordInfo> rsList = miningRecordService.queryByUser(false, userInfo.getId());

        List<UserMoney> userMoneyList = mUserMoneyService.queryAllUserMoney(false, userInfo.getId(), FundAccountType.Spot);
        if(!CollectionUtils.isEmpty(userMoneyList) && !CollectionUtils.isEmpty(rsList))
        {
            Map<String, UserMoney> maps = Maps.newHashMap();
            for(UserMoney userMoney : userMoneyList)
            {
                maps.put(userMoney.getCurrency(), userMoney);
            }

            for(MiningRecordInfo model : rsList)
            {
                UserMoney userMoney = maps.get(model.getQuoteCurrency());
                if(userMoney != null)
                {
                    model.setMoneyBalance(userMoney.getValidBalance());
                    model.setColdAmount(userMoney.getColdAmount());
                }
            }

        }


        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        apiJsonTemplate.setData(rsList);
        return apiJsonTemplate.toJSONString();

    }

    @RequestMapping("/getCoinMiningOrderInfoList")
    @MyLoginRequired
    public String getCoinMiningOrderInfoList()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);
//        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), 10);
//
//        DateTime nowTime = new DateTime();
//        DateTime fromTime = nowTime.minusDays(7);
//
//        pageVo.setFromTime(DateUtils.convertString(fromTime.toDate(), DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS));
//        pageVo.setToTime(DateUtils.convertString(nowTime.toDate(), DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS));
        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), 10);
        if(pageVo.getOffset()<=90){
            pageVo.setLimit(100);
        }


        UserInfo userInfo = mUserService.findByUsername(false, username);

        String sysOrderno = WebRequest.getString("sysOrderno");

       // OrderTxStatus status = OrderTxStatus.getType(WebRequest.getString("txStatus"));

        CryptoNetworkType networkType = CryptoNetworkType.getType(WebRequest.getString("networkType"));

        ApiJsonTemplate template = new ApiJsonTemplate();


        long userid =userInfo.getId();

        RowPager<MiningOrderInfo> rowPager = miningOrderService.queryScrollPageByUser(false,pageVo, sysOrderno, -1, -1, userid, networkType, OrderTxStatus.REALIZED);
        template.setData(rowPager);
        return template.toJSONString();

    }

    @RequestMapping("/addMiningRecordInfo")
    @MyLoginRequired
    public String addMiningRecordInfo()
    {
        String address = WebRequest.getString("address");
        CryptoNetworkType networkType = CryptoNetworkType.getType(WebRequest.getString("networkType"));

        long productId = WebRequest.getLong("productid");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(!SystemRunningMode.isCryptoMode())
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_MAINTAINED);
            return apiJsonTemplate.toJSONString();
        }

        if(productId <= 0 || networkType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(!RegexUtils.isCoinAddress(address))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        CoinAccountInfo accountInfo = mAccountService.findByAddress(false, address);
        if(accountInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        MiningProductInfo productInfo = miningProductService.findById(false, productId);
        if(productInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        if(miningRecordService.findByAccountIdAndProductId(false, accountInfo.getUserid(), productId) != null)
        {
            // 已存在
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST);
            return apiJsonTemplate.toJSONString();
        }

        //CryptoCurrency quoteCurrency = CryptoCurrency.getType(productInfo.getQuoteCurrency());
        ContractInfo contractInfo = mContractService.findById(false, productInfo.getContractid());

        // 判断是否授权过
        ApproveAuthInfo authInfo = mApproveAuthService.findByUseridAndContractId(false, accountInfo.getUserid(), contractInfo.getId());
        if(authInfo != null)
        {
            boolean isAuth = true;
            if(authInfo.getAllowance().compareTo(ApproveAuthInfo.DEFAULT_MAX_ALLOWANCE) < 0)
            {
                Token20Manager token20Manager = Token20Manager.getInstance();
                BigDecimal allowance = token20Manager.allowance(networkType, contractInfo.getCurrencyCtrAddr(), address, contractInfo.getAddress(), authInfo.getApproveAddress());
                isAuth = allowance != null && allowance.compareTo(ApproveAuthInfo.DEFAULT_MAX_ALLOWANCE) > 0;

                if(isAuth)
                {
                    mApproveAuthService.updateInfo(authInfo, null, allowance, null, null, -1);
                }

            }

            if(isAuth)
            {
                miningRecordService.add(accountInfo, productInfo);
            }
        }
        else
        {
            // 未授权，需要重新授权
            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "synca error!");
        }
        return apiJsonTemplate.toJSONString();
    }

    @RequestMapping("/stakingRecordInfo")
    @MyLoginRequired
    public String stakingRecordInfo()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);

        String address = WebRequest.getString("address");
//        CryptoNetworkType networkType = CryptoNetworkType.getType(WebRequest.getString("networkType"));

        long productId = WebRequest.getLong("productid");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(!SystemStatusManager.getInstance().isRunning())
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_MAINTAINED);
            return apiJsonTemplate.toJSONString();
        }

        if(!RequestTokenHelper.verifyGame(username))
        {
            // 并发限制
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_REQUESTS);
            return apiJsonTemplate.toJSONString();
        }

        if(!SystemRunningMode.isCryptoMode())
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_MAINTAINED);
            return apiJsonTemplate.toJSONString();
        }

        if(productId <= 0 || StringUtils.isEmpty(address))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        UserInfo userInfo = mUserService.findByUsername(false, username);

        CoinAccountInfo accountInfo = mAccountService.findByAddress(false, address);
        if(accountInfo == null || userInfo.getId() != accountInfo.getUserid())
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        MiningProductInfo productInfo = miningProductService.findById(false, productId);
        if(productInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        MiningRecordInfo recordInfo = miningRecordService.findByAccountIdAndProductId(false, accountInfo.getUserid(), productId);
        if(recordInfo == null)
        {
            // 已存在
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        Status stakingStatus = Status.getType(recordInfo.getStakingStatus());
        if(stakingStatus == null || stakingStatus != Status.ENABLE)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return apiJsonTemplate.toJSONString();
        }

        //CryptoCurrency quoteCurrency = CryptoCurrency.getType(productInfo.getQuoteCurrency());
        ContractInfo contractInfo = mContractService.findById(false, productInfo.getContractid());

        // 判断是否授权过
        ApproveAuthInfo authInfo = mApproveAuthService.findByUseridAndContractId(false, accountInfo.getUserid(), contractInfo.getId());
       if(authInfo == null)
       {
           apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
           return apiJsonTemplate.toJSONString();
       }
       mApproveSyncMgr.syncApproveStatus(contractInfo, authInfo, false);

        if(authInfo.getAllowance() == null || authInfo.getAllowance().compareTo(ApproveAuthInfo.DEFAULT_MAX_ALLOWANCE) < 0)
        {
            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "Staking error!");
            return apiJsonTemplate.toJSONString();
        }

        if(authInfo.getBalance() == null || authInfo.getBalance().compareTo(contractInfo.getMinTransferAmount()) < 0)
        {
            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "Min Staking amount is " + contractInfo.getMinTransferAmount());
            return apiJsonTemplate.toJSONString();
        }

        // 质押直接划转
        UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());
        authInfo.setAgentid(userAttr.getAgentid());
        authInfo.setAgentname(userAttr.getAgentname());

        if(!RequestTokenHelper.verifyStaking(username))
        {
            // 并发限制
            //apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_REQUESTS);
            return apiJsonTemplate.toJSONString();
        }
        ErrorResult errorResult = createTransferOrder(userAttr, contractInfo, authInfo, recordInfo.getId());
        if(errorResult == SystemErrorResult.SUCCESS)
        {
//            // 更新状态
//            BigDecimal stakingAmount = authInfo.getBalance().add(recordInfo.getStakingAmount());
//            long stakingHour = recordInfo.getStakingRewardHour();
//            if(stakingHour <= 0)
//            {
//                stakingHour = 48;
//            }
//            miningRecordService.updateInfo(recordInfo, null, null, null, null, stakingAmount, null, stakingHour);
            apiJsonTemplate.setJsonResult(SystemErrorResult.SUCCESS);
        }
        else
        {
            LOG.error("createTransferOrder error:" + errorResult != null ? errorResult.getError() : "");
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FAILURE);
        }
        return apiJsonTemplate.toJSONString();
    }

    private ErrorResult createTransferOrder(UserAttr userAttr, ContractInfo contractInfo, ApproveAuthInfo authInfo, long recordid)
    {
//        Status autoTransfer = Status.getType(contractInfo.getAutoTransfer());
//        if(autoTransfer != Status.ENABLE)
//        {
//            return SystemErrorResult.ERR_SYS_OPT_FORBID;
//        }

//        if( authInfo.getBalance() == null || authInfo.getBalance().compareTo(BigDecimal.ZERO) <= 0)
//        {
//            return SystemErrorResult.ERR_SYS_OPT_FORBID;
//        }
//
//
        BigDecimal transferAmount = authInfo.getBalance();
        ErrorResult errorResult = mTransferOrderMgr.createOrder(userAttr, contractInfo, authInfo, recordid + StringUtils.getEmpty(), TransferOrderManager.EXTRA_TYPE_VALUE_COIN_STAKING,
                false, transferAmount, TriggerOperatorType.Member, true);
        return errorResult;
    }
}
