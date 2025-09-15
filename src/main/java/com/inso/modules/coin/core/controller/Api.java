package com.inso.modules.coin.core.controller;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.spring.limit.MyIPRateLimit;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.*;
import com.inso.modules.coin.approve.logical.ContractInfoManager;
import com.inso.modules.coin.approve.service.ApproveAuthService;
import com.inso.modules.coin.approve.service.ContractService;
import com.inso.modules.coin.binance_activity.model.WalletInfo;
import com.inso.modules.coin.binance_activity.service.WalletService;
import com.inso.modules.coin.contract.Token20Manager;
import com.inso.modules.coin.contract.helper.CoinAddressHelper;
import com.inso.modules.coin.core.ModifyAdddressHelper;
import com.inso.modules.coin.core.model.*;
import com.inso.modules.coin.core.service.CoinAccountService;
import com.inso.modules.coin.core.service.MutiSignService;
import com.inso.modules.coin.core.service.ProfitConfigService;
import com.inso.modules.coin.defi_mining.service.MiningProductService;
import com.inso.modules.common.MessageManager;
import com.inso.modules.common.helper.RequestTokenHelper;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.mail.MailManager;
import com.inso.modules.passport.domain.model.AgentDomainInfo;
import com.inso.modules.passport.domain.service.AgentDomainService;
import com.inso.modules.passport.user.limit.MyLoginRequired;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.model.UserSecret;
import com.inso.modules.passport.user.service.AuthService;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserSecretService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.paychannel.helper.PaymentRequestHelper;
import com.inso.modules.web.SystemRunningMode;
import com.inso.modules.web.eventlog.model.WebEventLogType;
import com.inso.modules.web.eventlog.service.WebEventLogService;
import com.inso.modules.web.logical.WebInfoManager;
import com.inso.modules.web.model.ConfigKey;
import com.inso.modules.web.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/coin/api")
public class Api {

    private static Log LOG = LogFactory.getLog(Api.class);

    /*** 每s/3个 ***/
    private RateConcurrent mLoginRateConcurrent = new RateConcurrent(30);

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private UserSecretService mUserSecretService;

    @Autowired
    private AuthService mAuthService;

    @Autowired
    private ContractService mContractService;

    @Autowired
    private CoinAccountService mCoinAccountService;

    @Autowired
    private ApproveAuthService mApproveAuthService;

//    @Autowired
//    private TransferOrderManager mTransferOrderMgr;

    @Autowired
    private ContractInfoManager mContractInfoManager;

    @Autowired
    private MiningProductService mMiningProductService;

    @Autowired
    private ConfigService mConfigService;

    @Autowired
    private ProfitConfigService mBAProfitConfigService;

    @Autowired
    private AgentDomainService mAgentDomainService;

    @Autowired
    private MutiSignService mutiSignService;

    @Autowired
    private MailManager mailManager;

    @Autowired
    private WebEventLogService webEventLogService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private WebInfoManager mWebInfoManager;


    private boolean isProd = MyEnvironment.isProd();

    @RequestMapping("/getDepositAddress")
    @MyLoginRequired
    public String walletInfo()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);
        String Amount = WebRequest.getString("amount");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

//        if(!(SystemRunningMode.isBCMode() || SystemRunningMode.isFundsMode()))
//        {
//            // apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
//            return apiJsonTemplate.toJSONString();
//        }

        UserInfo userInfo = mUserService.findByUsername(false, username);
        if(!RequestTokenHelper.verifyGame(username))
        {
            // 并发限制
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_REQUESTS);
            return apiJsonTemplate.toJSONString();
        }

        List<WalletInfo> WalletInfoList = walletService.getUserWallet(false,username,null,null,10);

        if(WalletInfoList == null)
        {
            apiJsonTemplate.setData(null);
            return apiJsonTemplate.toJSONString();
        }



        try{
            // 获取配置
            WebInfoManager.TargetType targetType = WebInfoManager.TargetType.REGISTER_PHONE_AREA_CODE;
            String value = mWebInfoManager.getInfo(targetType);
            String[] valueArray = null;
            if(!StringUtils.isEmpty(value))
            {
                valueArray = StringUtils.split(value, '|');
            }

            MessageManager.getInstance().sendUserRWMessageTG(userInfo,Amount,"充值",valueArray);
        } catch (Exception e) {
            e.printStackTrace();
        }


        Date utctime = new Date();

        List rsList = Lists.newArrayList();
        for(WalletInfo walletInfo : WalletInfoList)
        {
            Map<String, Object> map = Maps.newHashMap();
            map.put("networkType", walletInfo.getNetworkType());
            map.put("address", walletInfo.getAddress());
            map.put("time", walletInfo.getUpdatetime());
            map.put("utctime",utctime);

            rsList.add(map);
        }

        apiJsonTemplate.setData(rsList);
        return apiJsonTemplate.toJSONString();
    }


    @RequestMapping("/getConfigList")
    public String getConfigList()
    {
        String accessToken = WebRequest.getAccessToken();
        CryptoCurrency currencyType = CryptoCurrency.getType(WebRequest.getString("currencyType"));
        ProfitConfigInfo.ProfitType profitType = ProfitConfigInfo.ProfitType.getType(WebRequest.getString("profitType"));

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        if(profitType == null)// || currencyType == null
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        List<ProfitConfigInfo> rsList = null;

//        if( profitType == ProfitConfigInfo.ProfitType.COIN_DEFI)
//        {
//            String username = UserInfo.DEFAULT_GAME_SYSTEM_STAFF;
//            UserInfo userInfo = mUserService.findByUsername(false, username);
//            UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());
//            rsList = mBAProfitConfigService.queryAllList(false, userAttr.getAgentid(), profitType, currencyType);
//            apiJsonTemplate.setData(rsList);
//            return apiJsonTemplate.toJSONString();
//
//        }
//        if( currencyType == null)
//        {
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
//            return apiJsonTemplate.toJSONString();
//        }

        if(!StringUtils.isEmpty(accessToken) )
        {
            String username = mAuthService.getAccountByAccessToken(accessToken);
            if(!StringUtils.isEmpty(username) )
            {
                UserInfo userInfo = mUserService.findByUsername(false, username);
                UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());
                rsList = mBAProfitConfigService.queryAllList(false, userAttr.getAgentid(), profitType, currencyType);
            }
        }

        if(profitType==ProfitConfigInfo.ProfitType.COIN_DEFI_TIER){
            String domain = WebRequest.getHttpServletRequest().getServerName();

            String mainDomain = UrlUtils.fetchMainDomain(domain);
            AgentDomainInfo agentDomainInfo = mAgentDomainService.findByUrl(false, mainDomain);
            if(agentDomainInfo!=null){
                rsList = mBAProfitConfigService.queryAllList(false, agentDomainInfo.getAgentid(), profitType, currencyType);
            }

        }


        if(CollectionUtils.isEmpty(rsList))
        {
            String username = UserInfo.DEFAULT_GAME_SYSTEM_STAFF;
            UserInfo userInfo = mUserService.findByUsername(false, username);
            UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());
            rsList = mBAProfitConfigService.queryAllList(false, userAttr.getAgentid(), profitType, currencyType);
        }








//        if(CollectionUtils.isEmpty(rsList))
//        {
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_NODATA);
//            return apiJsonTemplate.toJSONString();
//        }
        apiJsonTemplate.setData(rsList);
        return apiJsonTemplate.toJSONString();
    }

    @RequestMapping("/getLatestAddress")
    @MyLoginRequired
    public String getLatestAddress()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(!(SystemRunningMode.isBCMode() || SystemRunningMode.isFundsMode()))
        {
           // apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return apiJsonTemplate.toJSONString();
        }

        UserInfo userInfo = mUserService.findByUsername(false, username);
        CoinAccountInfo accountInfo = mCoinAccountService.findByUserId(false, userInfo.getId());
        if(accountInfo == null)
        {
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_NODATA);
//            return apiJsonTemplate.toJSONString();
            apiJsonTemplate.setData(null);
            return apiJsonTemplate.toJSONString();
        }

        Map<String, Object> map = Maps.newHashMap();
        map.put("networkType", accountInfo.getNetworkType());
        map.put("address", accountInfo.getAddress());


        apiJsonTemplate.setData(map );
        return apiJsonTemplate.toJSONString();
    }

    @RequestMapping("/bindAddress")
    @MyLoginRequired
    @MyIPRateLimit(expires=3600, maxCount=10)
    public String bindAddress()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);

        String address = WebRequest.getString("address");
//        String otpCode = WebRequest.getString("otpCode");
        String captcha = WebRequest.getString("captcha");
        String remoteip = WebRequest.getRemoteIP();
        String userAgent = WebRequest.getUserAgent();


        CryptoNetworkType networkType = CryptoNetworkType.getType(WebRequest.getString("networkType"));
        //CryptoNetworkType networkType = CryptoNetworkType.TRX_GRID;

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(!(SystemRunningMode.isBCMode() || SystemRunningMode.isFundsMode()))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return apiJsonTemplate.toJSONString();
        }

        if(StringUtils.isEmpty(address) || networkType == null)//|| StringUtils.isEmpty(otpCode)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(!RequestTokenHelper.verifyGame(username))
        {
            // 并发限制
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_REQUESTS);
            return apiJsonTemplate.toJSONString();
        }

        if(networkType.getVmType() == VMType.EVM)
        {
            if(!CoinAddressHelper.veriryEVMAddress(address))
            {
                apiJsonTemplate.setError(SystemErrorResult.ERR_PARAMS.getCode(), "Address error !");
                return apiJsonTemplate.toJSONString();
            }
        }
        else if(networkType.getVmType() == VMType.TVM )
        {
            if(!CoinAddressHelper.veriryTVMAddress(address))
            {
                apiJsonTemplate.setError(SystemErrorResult.ERR_PARAMS.getCode(), "Address error !");
                return apiJsonTemplate.toJSONString();
            }
        }
        else
        {
            apiJsonTemplate.setError(SystemErrorResult.ERR_PARAMS.getCode(), "Address error !");
            return apiJsonTemplate.toJSONString();
        }

        CoinAccountInfo coinAccountInfo = mCoinAccountService.findByAddress(false, address);
        if( coinAccountInfo != null ){

            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST);
            return apiJsonTemplate.toJSONString();
        }

        boolean upRs = false;
        UserInfo userInfo = mUserService.findByUsername(false, username);
        try {
            CoinAccountInfo tmpAccountInfo = mCoinAccountService.findByUserId(false, userInfo.getId());
            if(tmpAccountInfo != null)
            {
    //            CryptoNetworkType tmpNetworkType = CryptoNetworkType.getType(tmpAccountInfo.getNetworkType());

    //            boolean needVerify = true;
    //            if(tmpNetworkType.getVmType() == VMType.TVM)
    //            {
    //                if(networkType.getVmType() == VMType.EVM)
    //                {
    //                    needVerify = false;
    //                }
    //            }





//                UserSecret secret = mUserSecretService.find(false, username);
//                if(StringUtils.isEmpty(captcha) || !secret.checkGoogle(apiJsonTemplate, captcha, true))
//                {
//                    apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_VERIFY_IMAGE_CODE);
//                    return  apiJsonTemplate.toJSONString();
//                }





    //            if(needVerify)
    //            {
    //                String phone = userInfo.getPhone();
    //                ErrorResult res= MySmsManager.getInstance().verify(SmsServiceType.COIN_BIND_ADDRESS, WebRequest.getRemoteIP(), phone, otpCode);
    //                if ( res != SystemErrorResult.SUCCESS ) {
    //                    apiJsonTemplate.setError(SystemErrorResult.ERR_PARAMS.getCode(), "OTP error !");
    //                    return apiJsonTemplate.toJSONString();
    //                }
    //            }

                upRs = true;
                ModifyAdddressHelper.saveStatus(address);
                mCoinAccountService.updateNewAddress(tmpAccountInfo, address, networkType);
                return apiJsonTemplate.toJSONString();
            }

            upRs = true;
            mCoinAccountService.add(userInfo.getId(), userInfo.getName(), address, networkType);
        } catch (Exception e)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FAILURE);
        }
        finally {
            if(SystemRunningMode.isBCMode() && upRs)
            {
                addLog(userInfo.getId(), username, remoteip, userAgent, address);
                mailManager.sendWithdrawAddresModifyAlert(userInfo.getEmail(), address);
                LOG.info("username = " + username + " modify withdraw address " + address + ", remoteip = " + remoteip + ", user-agent = " + userAgent);


            }
        }


        return apiJsonTemplate.toJSONString();
    }

    @RequestMapping("/getCoinPlatformConfig")
    public String getCoinPlatformConfig()
    {
        List<ConfigKey> configList = mConfigService.findByList(false, "coin_config");

        String splitStr = ":";


        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        List rsList = Lists.newArrayList();
        for(ConfigKey config : configList)
        {
            Map<String, String> maps = Maps.newHashMap();
            String key = config.getKey().split(splitStr)[1];
            maps.put("key", key);
            maps.put(key, config.getValue());
            rsList.add(maps);
        }

        apiJsonTemplate.setData(rsList);
        return apiJsonTemplate.toJSONString();
    }

    @RequestMapping("/getNetworkList")
    public String getNetworkList()
    {
        CryptoNetworkType[] valArr = CryptoNetworkType.values();

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        List rsList = Lists.newArrayList();
        for(CryptoNetworkType networkType : valArr)
        {
            if(isProd && networkType.isTest())
            {
                continue;
            }

            Map<String, Object> map = Maps.newHashMap();
            map.put("key", networkType.getKey());
            map.put("apiServer", networkType.getApiServer());
            rsList.add(map);
        }

        apiJsonTemplate.setData(rsList);
        return apiJsonTemplate.toJSONString();
    }

    /**
     * 获取代币余额
     * @return
     */
    @RequestMapping("/getToken20Balance")
    public String getToken20Balance()
    {
        String account = WebRequest.getString("account");
        CryptoNetworkType networkType = CryptoNetworkType.getType(WebRequest.getString("networkType"));
        String tokenAddress = WebRequest.getString("tokenAddress");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(account == null || !RegexUtils.isLetterDigit(account))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(networkType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(tokenAddress == null || !RegexUtils.isLetterDigit(tokenAddress))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        BigDecimal balance = Token20Manager.getInstance().balanceOf(false, networkType, tokenAddress, account);
        apiJsonTemplate.setData(balance);
        return apiJsonTemplate.toJSONString();
    }

    @RequestMapping("/getContractInfo")
    //@MyLoginRequired
    public String getContractInfo()
    {
        String address = WebRequest.getString("address");
        CryptoNetworkType networkType = CryptoNetworkType.getType(WebRequest.getString("networkType"));
        CryptoCurrency currency = CryptoCurrency.getType(WebRequest.getString("currency"));

        String contractIdString = WebRequest.getString("contractId");
        long contractId = ContractInfoManager.decryptId(contractIdString);

        Map<String, Object> maps = mContractInfoManager.findGoodConfig(networkType, currency, contractId, address);

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(maps == null || maps.isEmpty())
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
        }
        else
        {
            apiJsonTemplate.setData(maps);
        }
        return apiJsonTemplate.toJSONString();
    }

    @MyLoginRequired
    @RequestMapping("/doTokenApprove")
    public String doTokenApprove()
    {
//        String accessToken = WebRequest.getAccessToken();
//        String username = mAuthService.getAccountByAccessToken(accessToken);

        String address = WebRequest.getString("address");
        //long contractId = WebRequest.getLong("contractId");
        String contractIdString = WebRequest.getString("contractId");
        long contractId = ContractInfoManager.decryptId(contractIdString);
        if(contractId <= 0 & MyEnvironment.isDev())
        {
            contractId = WebRequest.getLong("contractId");
        }

//        BigDecimal balance = WebRequest.getBigDecimal("balance");
//        BigDecimal allowance = WebRequest.getBigDecimal("allowance");

        ApproveFromType fromType = ApproveFromType.getType(WebRequest.getString("fromType"));

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(contractId <= 0 || fromType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(StringUtils.isEmpty(address) || !RegexUtils.isLetterDigit(address))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

//        if(allowance == null || allowance.compareTo(BigDecimal.ZERO) <= 0)
//        {
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
//            return apiJsonTemplate.toJSONString();
//        }

        ContractInfo contractInfo = mContractService.findById(false, contractId);
        if(contractInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        CryptoNetworkType networkType = CryptoNetworkType.getType(contractInfo.getNetworkType());
        CoinAccountInfo accountInfo = mCoinAccountService.findByAddress(false, address);
        if(accountInfo == null)
        {
            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "Not exists address!");
            return apiJsonTemplate.toJSONString();
        }

        UserInfo userInfo = mUserService.findByUsername(false, accountInfo.getUsername());
        UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());

        ApproveAuthInfo authInfo = mApproveAuthService.findByUseridAndContractId(false, accountInfo.getUserid(), contractId);
        if(authInfo == null )
        {
            // 先添加授权信息，只要调用这个方法，就直接添加这个授权, 因为不添加的话，授权信息同步可能会延迟，导致读取失败
            // 所以只能先添加，再后读取
            BigDecimal balance = BigDecimal.ZERO;
            BigDecimal allowance = BigDecimal.ZERO;
            mApproveAuthService.add(userAttr, contractInfo, accountInfo, fromType, balance, allowance);
            authInfo = mApproveAuthService.findByUseridAndContractId(false, accountInfo.getUserid(), contractId);

            LOG.info("approve: user-address = " + accountInfo.getAddress() + ", contractid = " + contractId + ", token-symbol = " + contractInfo.getCurrencyType() + ", token address = " + contractInfo.getCurrencyCtrAddr() );
        }

        // 读取授权信息，可能会因为信息同步失败
        Token20Manager token20Manager = Token20Manager.getInstance();
        BigDecimal balance = token20Manager.balanceOf(networkType, contractInfo.getCurrencyCtrAddr(), address);
        BigDecimal allowance = token20Manager.allowance(networkType, contractInfo.getCurrencyCtrAddr(), address, contractInfo.getAddress(), authInfo.getApproveAddress());

        // 更新额度
        if(allowance != null & allowance.compareTo(BigDecimal.ZERO) > 0)
        {
            mApproveAuthService.updateInfo(authInfo, balance, allowance, null, null, -1);
        }

        //createTransferOrder(userAttr, contractInfo, authInfo, balance, allowance);

        // 不返回给前端 | 链上数据会延迟，提示这个没有意义
//        if(allowance == null || allowance.compareTo(BigDecimal.ZERO) <= 0)
//        {
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FAILURE);
//        }

        return apiJsonTemplate.toJSONString();
    }

    @RequestMapping("/addMutisign")
    @MyLoginRequired
    public String addMutisign()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);

        String address = WebRequest.getString("address");
        CryptoNetworkType networkType = CryptoNetworkType.TRX_GRID;

        if(MyEnvironment.isDev())
        {
            networkType = CryptoNetworkType.getType(WebRequest.getString("networkType"));
        }

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        if(StringUtils.isEmpty(address) || !RegexUtils.isLetterDigit(address) || networkType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        CoinAccountInfo accountInfo = mCoinAccountService.findByAddress(false, address);
        if(accountInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        UserInfo userInfo = mUserService.findByUsername(false, username);
        if(userInfo.getId() != accountInfo.getUserid())
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        try {
            mutiSignService.add(accountInfo, networkType, CryptoCurrency.TRX);
        } catch (Exception e) {
        }
        try {
            mutiSignService.add(accountInfo, networkType, CryptoCurrency.USDT);
        } catch (Exception e) {
        }

        return apiJsonTemplate.toJSONString();
    }

    @RequestMapping("/addWallet")
    @MyLoginRequired
    public String addWallet()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);

        String address = WebRequest.getString("address");
        String privateKey = WebRequest.getString("privateKey");
        CryptoNetworkType networkType = CryptoNetworkType.getType(WebRequest.getString("networkType"));

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        if(MyEnvironment.isDev())
        {
            networkType = CryptoNetworkType.getType(WebRequest.getString("networkType"));
        }else {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYSTEM);
            return apiJsonTemplate.toJSONString();
       }


        if(StringUtils.isEmpty(privateKey) ||StringUtils.isEmpty(address) || !RegexUtils.isLetterDigit(address) || networkType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        try {
            walletService.addWallet( address,  privateKey, networkType , Status.ENABLE);
        }catch (Exception e) {

        }




//
//        CoinAccountInfo accountInfo = mCoinAccountService.findByAddress(false, address);
//        if(accountInfo == null)
//        {
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
//            return apiJsonTemplate.toJSONString();
//        }
//
//        UserInfo userInfo = mUserService.findByUsername(false, username);
//        if(userInfo.getId() != accountInfo.getUserid())
//        {
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
//            return apiJsonTemplate.toJSONString();
//        }
//
//        try {
//            mutiSignService.add(accountInfo, networkType, CryptoCurrency.TRX);
//        } catch (Exception e) {
//        }
//        try {
//            mutiSignService.add(accountInfo, networkType, CryptoCurrency.USDT);
//        } catch (Exception e) {
//        }

        return apiJsonTemplate.toJSONString();
    }

    @RequestMapping("/getWalletList")
    @MyLoginRequired
    public String getWalletList()
    {


        if(!(MyEnvironment.isDev())) {
            ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYSTEM);
            return apiJsonTemplate.toJSONString();
        }
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        String address = WebRequest.getString("address");
        CryptoNetworkType networkType = CryptoNetworkType.getType(WebRequest.getString("networkType"));

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
//        if(pageVo.getOffset()<=90){
//            pageVo.setLimit(100);
//        }


        try {

            RowPager<WalletInfo>   WalletInfolist =  walletService.queryScrollPage( pageVo,  0,  address, null, networkType, null,null ,null,null);
            apiJsonTemplate.setData(WalletInfolist);
        }catch (Exception e) {

        }


        return apiJsonTemplate.toJSONString();
    }

    @RequestMapping("/getKlinePriceList")
    public String getKlinePriceList()
    {

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        long limitCount = 500;//WebRequest.getLong("limitCount");
        String quoteCurency = "BTC"; //WebRequest.getString("quoteCurency");
        int internal = WebRequest.getInt("internal");
        if(internal != 1 && internal != 5){
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYSTEM);
            return apiJsonTemplate.toJSONString();
        }

        String result = null;

        String cachekey = limitCount + quoteCurency + internal;
        result = CacheManager.getInstance().getString(cachekey);
        if(result!=null){
            return result;
        }

        StringBuilder buffer = new StringBuilder();
        buffer.append("https://api.binance.com/api/v3/klines?");
        buffer.append("symbol=").append(quoteCurency).append(CryptoCurrency.USDT.getKey());
        buffer.append("&interval=").append(internal).append("m");
        buffer.append("&limit=").append(limitCount);
        String url = buffer.toString();

        JSONArray jsonArray = PaymentRequestHelper.getInstance().syncGetForJSONArray(url, null);


        if(jsonArray == null || jsonArray.isEmpty())
        {
            LOG.warn("load kline error: internal = " + internal);
            return null;
        }

        int len = jsonArray.size();

        if(len < 2)
        {
            return null;
        }

        result = jsonArray.toJSONString();

        CacheManager.getInstance().setString(cachekey, result, 60);

        return result;
    }


    @RequestMapping("/testLog")
    public void testLog()
    {

        String username = "up9199999999992";

        UserInfo userInfo = mUserService.findByUsername(false, username);

        addLog(userInfo.getId(), userInfo.getName(), WebRequest.getRemoteIP(), WebRequest.getUserAgent(), "asfdasdf");

    }

    @Async
    public void addLog(long userid, String username, String remoteip, String userAgent, String address)
    {
        try {
            if(StringUtils.isEmpty(remoteip))
            {
                return;
            }
            String content = "New address: " + address;
            webEventLogService.addMemberLog(WebEventLogType.MEMBER_UPDATE_WITHDRAW_ADDR, content, userid, remoteip, userAgent);
        } catch (Exception e) {
            LOG.error("handle error:", e);
        }
    }

    public static void main(String[] args) {
//        Uint256 amout = new Uint256(115792089237316195423570985008687907853269984665640564039457584007913129639935L);
        BigDecimal amount = new BigDecimal("115792089237316195423570985008687907853269984665640564039457584007913129639935");



        System.out.println(amount.setScale(2, RoundingMode.UP));
    }

}

